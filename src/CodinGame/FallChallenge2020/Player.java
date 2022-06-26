package CodinGame.FallChallenge2020;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

class Player {
    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        Brain brain = new Brain(in);
        // game loop
        while (true) {
            brain.updateLoop(in);

            // Update all params we need
            brain.setupParams();

            // Set states of all heroes
            brain.determineDefendingHeroStates();
            brain.determineAttackingHeroStates();

            // Perform actions
            brain.performActions();
        }
    }

    static class Brain {
        static final int TYPE_MONSTER       = 0;
        static final int TYPE_MY_HERO       = 1;
        static final int TYPE_OP_HERO       = 2;
        static final int MONSTER_SPEED      = 400;
        static final int HERO_SPEED         = 800;
        static final int HERO_ATTACK_RANGE  = 800;
        static final int RADIUS             = 5000;
        static final int WIND_SPELL_HERO_DISTANCE = 1280;
        static final int MAX_SPELL_RANGE    = 2200;
        static final Pair<Integer> BOTTOM_RIGHT     = new Pair<>(17630, 9000);

        static Pair<Float> LINE_POS_ONE_TWO_DIVIDER = new Pair<>(2.0f * BOTTOM_RIGHT.y / ((float) BOTTOM_RIGHT.x), 0f);
        static Pair<Float> LINE_PLAYER_OPP_DIVIDER  = new Pair<>(-3.0f * BOTTOM_RIGHT.y / ((float) BOTTOM_RIGHT.x), 2.0f * BOTTOM_RIGHT.y);
        static Pair<Float> DIRECTION_AWAY_FROM_BASE = new Pair<>(1f, 1f);

        static final int MONSTER_PUSH_BACK_FROM_BASE_RANGE = 600;
        static boolean IS_PLAYER_BOT_RIGHT;
        static Pair<Integer> PATROL_POSITION_OPP_SIDE           = new Pair<>((int) (BOTTOM_RIGHT.x * 0.6f), (int) (BOTTOM_RIGHT.y * 0.5f));
        static Pair<Integer> CONTROL_MONSTER_TO_POSITION_ONE    = new Pair<>(4900, 100);

        static final int CONTROL_MONSTER_MIN_HEALTH = 9;
        static final int TURN_START_SENDING_MONSTERS_FROM_PLAYER_BASE = 50;

        static Pair<Integer> SEND_MONSTERS_POS_ONE      = new Pair<>(450, 4650);
        static Pair<Integer> SEND_MONSTERS_POS_TWO      = new Pair<>(4650, 450);
        static Pair<Integer> WAITING_POS_ONE            = new Pair<>((int) (RADIUS * 0.6f), (int) (RADIUS * 1.35f));
        static Pair<Integer> WAITING_POS_TWO            = new Pair<>((int) (RADIUS * 1.35f), (int) (RADIUS * 0.6f));
        static Pair<Integer> ATTACK_POSITION            = new Pair<>(4500, 1200);

        static final AttackStrategy attackStrat = new AttackStrategy();

        List<Monster> monsters, monstersInsideBase, monstersHeadingTowardsBase, monstersOnPlayerSide;

        Map<Integer, Monster> allMonstersOppSide;
        List<Monster> monstersOnOpponentSide, monstersHeadingTowardsOpponentBase, monstersInsideOpponentBase;

        List<Hero> playerDefendingHeroes, oppHeroesInsideBase;

        int heroesPerPlayer;
        int currentTurn;
        Base player, opponent;
        Map<Integer, BaseAction> actionsToPerform;

        public Brain(Scanner in) {
            this.currentTurn = 0;
            this.actionsToPerform = new HashMap<>();
            this.monsters = new ArrayList<>();
            this.player = new Base(in);
            this.heroesPerPlayer = in.nextInt();

            IS_PLAYER_BOT_RIGHT = this.player.pos.x > 1000;
            this.opponent = new Base(
                    IS_PLAYER_BOT_RIGHT ? 0 : BOTTOM_RIGHT.x,
                    IS_PLAYER_BOT_RIGHT ? 0 : BOTTOM_RIGHT.y
            );

            if (IS_PLAYER_BOT_RIGHT) {
                WAITING_POS_ONE = getOppositePos(WAITING_POS_ONE);
                WAITING_POS_TWO = getOppositePos(WAITING_POS_TWO);
                PATROL_POSITION_OPP_SIDE = new Pair<>(BOTTOM_RIGHT.x - PATROL_POSITION_OPP_SIDE.x, PATROL_POSITION_OPP_SIDE.y);

                LINE_POS_ONE_TWO_DIVIDER.y = (float) -BOTTOM_RIGHT.y;
                DIRECTION_AWAY_FROM_BASE = new Pair<>(-1f, -1f);
            } else {
                ATTACK_POSITION = getOppositePos(ATTACK_POSITION);
                SEND_MONSTERS_POS_ONE   = getOppositePos(SEND_MONSTERS_POS_ONE);
                SEND_MONSTERS_POS_TWO   = getOppositePos(SEND_MONSTERS_POS_TWO);
                CONTROL_MONSTER_TO_POSITION_ONE   = getOppositePos(CONTROL_MONSTER_TO_POSITION_ONE);
            }
        }

        public void updateLoop(Scanner in) {
            this.currentTurn++;
            this.actionsToPerform = new HashMap<>(this.heroesPerPlayer);
            this.player.health = in.nextInt();
            this.player.mana = in.nextInt();
            this.opponent.health = in.nextInt();
            this.opponent.mana = in.nextInt();
            int entityCount = in.nextInt();

            this.player.heroes = new ArrayList<>(entityCount);
            this.opponent.heroes = new ArrayList<>(entityCount);
            this.monsters = new ArrayList<>(entityCount);

            for (int i = 0; i < entityCount; i++) {
                int id = in.nextInt();              // Unique identifier
                int type = in.nextInt();            // 0=monster, 1=your hero, 2=opponent hero
                int x = in.nextInt();               // Position of this entity
                int y = in.nextInt();
                int shieldLife = in.nextInt();      // Ignore for this league; Count down until shield spell fades
                int isControlled = in.nextInt();    // Ignore for this league; Equals 1 when this entity is under a control spell
                int health = in.nextInt();          // Remaining health of this monster
                int vx = in.nextInt();              // Trajectory of this monster
                int vy = in.nextInt();
                int nearBase = in.nextInt();        // 0=monster with no target yet, 1=monster targeting a base
                int threatFor = in.nextInt();       // Given this monster's trajectory, is it a threat to 1=your base, 2=your opponent's base, 0=neither

                switch (type) {
                    case TYPE_MONSTER:
                        monsters.add(new Monster(id, type, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor));
                        break;
                    case TYPE_MY_HERO:
                        player.heroes.add(new Hero(id, type, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor, (id == 0 || id == 3)));
                        break;
                    case TYPE_OP_HERO:
                        opponent.heroes.add(new Hero(id, type, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor, false));
                        break;
                }
            }
        }

        public void setupParams() {
            monstersInsideBase                  = new ArrayList<>(monsters.size());
            monstersHeadingTowardsBase          = new ArrayList<>(monsters.size());
            monstersOnPlayerSide                = new ArrayList<>(monsters.size());

            allMonstersOppSide                  = new HashMap<>(monsters.size());
            monstersInsideOpponentBase          = new ArrayList<>(monsters.size());
            monstersHeadingTowardsOpponentBase  = new ArrayList<>(monsters.size());
            monstersOnOpponentSide              = new ArrayList<>(monsters.size());

            for (Monster monster : monsters) {
                // Inside a base
                if (monster.nearBase == 1 && monster.threatFor == 1) {
                    monstersInsideBase.add(monster);
                    continue;
                }
                if (monster.nearBase == 1 && monster.threatFor == 2) {
                    monstersInsideOpponentBase.add(monster);
                    allMonstersOppSide.put(monster.id, monster);
                    continue;
                }

                // Headings towards a base
                if (monster.nearBase == 0 && monster.threatFor == 2) {
                    monstersHeadingTowardsOpponentBase.add(monster);
                    allMonstersOppSide.put(monster.id, monster);
                    continue;
                }

                float yValue = LINE_PLAYER_OPP_DIVIDER.x * monster.pos.x + LINE_PLAYER_OPP_DIVIDER.y;
                if ((IS_PLAYER_BOT_RIGHT && monster.pos.y > yValue) || (!IS_PLAYER_BOT_RIGHT && monster.pos.y < yValue)) {
                    if (monster.nearBase == 0 && monster.threatFor == 1) {
                        monstersHeadingTowardsBase.add(monster);
                    } else {
                        monstersOnPlayerSide.add(monster);
                    }
                } else {
                    monstersOnOpponentSide.add(monster);
                    allMonstersOppSide.put(monster.id, monster);
                }
            }

            playerDefendingHeroes = new ArrayList<>(heroesPerPlayer);
            player.heroes.stream().limit(2).forEach(hero -> playerDefendingHeroes.add(hero));

            oppHeroesInsideBase = opponent.heroes.stream().filter(oppHero -> getDistance(oppHero.pos, opponent.pos) < RADIUS).collect(Collectors.toList());
        }

        public void determineAttackingHeroStates() {
            Hero attacker = player.heroes.get(2);

            if (currentTurn > TURN_START_SENDING_MONSTERS_FROM_PLAYER_BASE) {
                attacker.setState(EHeroState.DESTROY_OPPONENT);
            } else if (allMonstersOppSide.isEmpty()) {
                attacker.setState(EHeroState.PATROL_CENTER_OPPONENT);
            } else {
                attacker.setState(EHeroState.ATTACK_RANDOM_OPPONENT_MONSTERS);
            }
            determineSingleHeroAction(attacker);
        }

        public void determineDefendingHeroStates() {
            if (!monstersInsideBase.isEmpty()) {
                Monster closestMonsterToBase = getClosestMonster(player.pos, monstersInsideBase, true, true, false, true);
                float distanceClosestMonsterToBase = getDistance(closestMonsterToBase.pos, player.pos);
                Hero closestHero = getClosestHeroToMonster(closestMonsterToBase, player.heroes);

                if (closestMonsterToBase.shieldLife == 0) {
                    float distanceMonsterToHero = getDistance(closestHero.pos, closestMonsterToBase.pos);

                    if (currentTurn > TURN_START_SENDING_MONSTERS_FROM_PLAYER_BASE && player.mana > 80 && closestMonsterToBase.health > 9 && distanceMonsterToHero < WIND_SPELL_HERO_DISTANCE) {
                        player.castSpell(closestHero, new WindSpellAction(closestHero, DIRECTION_AWAY_FROM_BASE, "WIND " + closestMonsterToBase.id), actionsToPerform);
                    } else if (player.canCastUrgentSpell() && distanceClosestMonsterToBase - MONSTER_SPEED <= MONSTER_PUSH_BACK_FROM_BASE_RANGE) {
                        if (distanceMonsterToHero < WIND_SPELL_HERO_DISTANCE) {
                            player.castSpell(closestHero, new WindSpellAction(closestHero, DIRECTION_AWAY_FROM_BASE, "WIND " + closestMonsterToBase.id), actionsToPerform);
                        } else if (distanceMonsterToHero < MAX_SPELL_RANGE && closestMonsterToBase.isControlled != 1) {
                            player.castSpell(closestHero, new ControlSpellAction(closestMonsterToBase, getRandomSendMonstersPos()), actionsToPerform);
                        }
                    }

                    actionsToPerform.putIfAbsent(closestHero.id, new MoveAction(getPositionToAttack(closestMonsterToBase, monsters), closestMonsterToBase.id));
                    closestMonsterToBase.isAttacked = true;
                } else if ((distanceClosestMonsterToBase / MONSTER_SPEED) * 2 < closestMonsterToBase.health) {
                    playerDefendingHeroes.forEach(hero -> {
                        actionsToPerform.putIfAbsent(hero.id, new MoveAction(getPositionToAttack(closestMonsterToBase, monsters), closestMonsterToBase.id));
                        hero.setState(EHeroState.STATE_ALREADY_SET);
                    });
                } else {
                    actionsToPerform.putIfAbsent(closestHero.id, new MoveAction(getPositionToAttack(closestMonsterToBase, monsters), closestMonsterToBase.id));
                    closestMonsterToBase.isAttacked = true;
                }
                closestHero.state = EHeroState.STATE_ALREADY_SET;
            }

            playerDefendingHeroes.forEach(hero -> hero.setState(EHeroState.DEFEND_BASE));
            playerDefendingHeroes.forEach(this::determineSingleHeroAction);
        }

        public void determineSingleHeroAction(Hero hero) {
            switch (hero.state) {
                case DEFEND_BASE:
                    if (handleMonstersIfNeeded(hero, monstersInsideBase, player.pos, true)) {
                        break;
                    }
                    if (handleMonstersIfNeeded(hero, monstersHeadingTowardsBase, hero.pos, false)) {
                        break;
                    }
                    if (handleMonstersIfNeeded(hero, monstersOnPlayerSide, hero.pos, false)) {
                        break;
                    }
                case PATROL_PLAYER:
                    if (hero.isPosOne) {
                        actionsToPerform.putIfAbsent(hero.id, new MoveAction(getRandomPosAround(WAITING_POS_ONE)));
                    } else {
                        actionsToPerform.putIfAbsent(hero.id, new MoveAction(getRandomPosAround(WAITING_POS_TWO)));
                    }
                    break;
                case DESTROY_OPPONENT:
                    attackStrat.handleHero(this, hero);
                    break;
                case ATTACK_RANDOM_OPPONENT_MONSTERS:
                    Monster closestMonster = getClosestMonster(hero.pos, new ArrayList<>(allMonstersOppSide.values()), true, true, hero.isPosOne, true);
                    actionsToPerform.putIfAbsent(hero.id, new MoveAction(getPositionToAttack(closestMonster, monsters), closestMonster.id));
                    break;
                case PATROL_CENTER_OPPONENT:
                    actionsToPerform.putIfAbsent(hero.id, new MoveAction(getRandomPosAround(PATROL_POSITION_OPP_SIDE)));
                    break;
                case STATE_ALREADY_SET:
                    break;
            }
        }

        public Pair<Integer> getOppositePos(Pair<Integer> pos) {
            return new Pair<>(BOTTOM_RIGHT.x - pos.y, BOTTOM_RIGHT.y - pos.x);
        }

        public Pair<Integer> getRandomPosAround(Pair<Integer> pos) {
            return new Pair<>(
                    pos.x + (int) (Math.random() * HERO_SPEED * 1.5f),
                    pos.y + (int) (Math.random() * HERO_SPEED * 1.5f)
            );
        }

        public Hero getClosestHeroToMonster(Monster monster, List<Hero> heroes) {
            Hero closestHero = null;
            float closestDistance = Integer.MAX_VALUE;
            for (Hero hero : heroes) {
                float distance = getDistance(hero.pos, monster.pos);
                if (distance < closestDistance) {
                    closestDistance = distance;
                    closestHero = hero;
                }
            }

            return closestHero;
        }

        private boolean handleMonstersIfNeeded(Hero hero, List<Monster> monsters, Pair<Integer> closestPosTo, boolean ignoreIsPosOneCheck) {
            if (!monsters.isEmpty()) {
                if (player.canCastSpell() && currentTurn > TURN_START_SENDING_MONSTERS_FROM_PLAYER_BASE && player.mana > 80) {
                    Monster furthestMonster = getFurthestMonsterInRangeEnoughHealth(hero.pos, monsters);
                    if (furthestMonster != null) {
                        player.castSpell(hero, new ControlSpellAction(furthestMonster, getRandomSendMonstersPos()), actionsToPerform);
                        return true;
                    }
                }

                Monster closestMonster = getClosestMonster(closestPosTo, monsters, false, ignoreIsPosOneCheck, hero.isPosOne, true);

                if (closestMonster != null) {
                    actionsToPerform.putIfAbsent(hero.id, new MoveAction(getPositionToAttack(closestMonster, monsters), closestMonster.id));
                    closestMonster.isAttacked = true;
                    hero.setState(EHeroState.STATE_ALREADY_SET);
                    return true;
                }
            }

            return false;
        }

        public boolean containsMonster(int monsterId, Collection<Monster> monsters) {
            for (Monster monster : monsters) {
                if (monster.id == monsterId) {
                    return true;
                }
            }

            return false;
        }

        public void performActions() {
            for (Entity hero : player.heroes) {
                actionsToPerform.get(hero.id).performAction();
            }
        }

        public Monster getFurthestMonsterInRangeEnoughHealth(Pair<Integer> pos, List<Monster> monsters) {
            Monster furthestMonster = null;
            float furthestMonsterDistance = 0f;

            for (Monster monster : monsters) {
                if (monster.isControlled == 1 || monster.health < CONTROL_MONSTER_MIN_HEALTH) {
                    continue;
                }

                float distance = getDistance(pos, monster.pos);
                if (distance < MAX_SPELL_RANGE && distance > furthestMonsterDistance) {
                    furthestMonster = monster;
                    furthestMonsterDistance = distance;
                }
            }

            return furthestMonster;
        }

        public Monster getClosestMonster(Pair<Integer> pos, List<Monster> monsters, boolean ignoreTargetedMonsters, boolean ignoreIsPosOneCheck, boolean isPosOne, boolean ignoreInRangeCheck) {
            Monster closestMonster = null;
            float closestMonsterDistance = Float.MAX_VALUE;

            for (Monster monster : monsters) {
                if (!ignoreTargetedMonsters && monster.isAttacked) {
                    continue;
                }

                if (!ignoreIsPosOneCheck) {
                    float yValue = monster.pos.x * LINE_POS_ONE_TWO_DIVIDER.x + LINE_POS_ONE_TWO_DIVIDER.y;

                    if ((isPosOne && monster.pos.y < yValue) || (!isPosOne && monster.pos.y > yValue)){
                        continue;
                    }
                }

                float distance = getDistance(pos, monster.pos);

                if (!ignoreInRangeCheck && distance > MAX_SPELL_RANGE) {
                    continue;
                }

                if (distance < closestMonsterDistance) {
                    closestMonster = monster;
                    closestMonsterDistance = distance;
                }
            }

            return closestMonster;
        }

        public Pair<Integer> getRandomSendMonstersPos() {
            return Math.round(Math.random()) == 1L ? SEND_MONSTERS_POS_ONE : SEND_MONSTERS_POS_TWO;
        }

        public Pair<Integer> getPositionToAttack(Monster target, Collection<Monster> monsters) {
            return getCentroid(getMonstersInRange(target, monsters));
        }

        public List<Monster> getMonstersInRange(Monster target, Collection<Monster> monsters) {
            List<Monster> monstersInRange = new ArrayList<>(monsters.size());
            monstersInRange.add(target);

            for (Monster monster : monsters) {
                if (target.id == monster.id) {
                    continue;
                }

                if (getDistance(target.pos, monster.pos) < 2 * HERO_ATTACK_RANGE) {
                    monstersInRange.add(monster);
                }
            }

            return monstersInRange;
        }

        public Pair<Integer> getCentroid(List<Monster> monsters) {
            Pair<Integer> centroid = new Pair<>(0, 0);
            for (Monster monster : monsters) {
                centroid.x += monster.pos.x;
                centroid.y += monster.pos.y;
            }
            centroid.x /= monsters.size();
            centroid.y /= monsters.size();

            return centroid;
        }

        public Pair<Float> getDirection(Pair<Integer> p1, Pair<Integer> p2) {
            return new Pair<>((float) (p2.x - p1.x), (float) (p2.y - p1.y));
        }

        public float getDistance(Pair<Integer> p1, Pair<Integer> p2) {
            return (float) Math.sqrt(Math.pow((p2.x - p1.x), 2) + Math.pow((p2.y - p1.y), 2));
        }
    }

    // ===================================================================================
    // ====================================== Classes ====================================
    // ===================================================================================

    enum EAttackState {
        NOT_READY, GET_SHIELD,
        SCOUTING, CONTROL_MONSTER_TO_POSITION, PREPARE_PUSH_POSITION,
        CONTROL_OPPONENT_AWAY, PUSH_MONSTER
    }

    static class AttackStrategy {
        EAttackState currentState = EAttackState.NOT_READY;

        Monster targetMonster = null;
        int nbrOfPushes = 0;
        int n = 3;
        int nbrOfControls = 0;

        public void handleHero(Brain brain, Hero hero) {
            switch (currentState) {
                case NOT_READY:
                    float distanceHeroCenter = brain.getDistance(hero.pos, Brain.ATTACK_POSITION);
                    if (distanceHeroCenter < Brain.RADIUS * 0.7) {
                        Monster closestMonster = brain.getClosestMonster(hero.pos, brain.monstersInsideOpponentBase, true, true, true, false);
                        if (closestMonster != null) {
                            reset();
                            n = 2;
                            currentState = EAttackState.PREPARE_PUSH_POSITION;
                            targetMonster = closestMonster;
                            handleHero(brain, hero);
                            break;
                        }
                    }

                    if (distanceHeroCenter < 100) {
                        currentState = EAttackState.SCOUTING;
                        handleHero(brain, hero);
                        break;
                    }

                    brain.actionsToPerform.putIfAbsent(hero.id, new MoveAction(Brain.ATTACK_POSITION));
                    break;
                case GET_SHIELD:
                    currentState = EAttackState.SCOUTING;
                    brain.player.castSpell(hero, new ShieldSpellAction(hero.id), brain.actionsToPerform);
                    break;
                case SCOUTING:
                    boolean shouldGetShield = brain.oppHeroesInsideBase.stream().filter(oppHero -> brain.getDistance(hero.pos, oppHero.pos) < Brain.WIND_SPELL_HERO_DISTANCE).count() != 0;
                    if (hero.shieldLife == 0 && shouldGetShield) {
                        currentState = EAttackState.GET_SHIELD;
                        handleHero(brain, hero);
                        break;
                    }

                    Monster closestMonster = brain.getClosestMonster(hero.pos, brain.monstersHeadingTowardsOpponentBase, true, true, true, false);

                    if (closestMonster != null) {
                        reset();
                        currentState = EAttackState.PREPARE_PUSH_POSITION;
                        targetMonster = closestMonster;
                        handleHero(brain, hero);
                        break;
                    }

                    closestMonster = brain.getClosestMonster(hero.pos, brain.monstersOnOpponentSide, true, true, true, false);
                    if (closestMonster != null) {
                        reset();
                        currentState = EAttackState.CONTROL_MONSTER_TO_POSITION;
                        targetMonster = closestMonster;
                        handleHero(brain, hero);
                        break;
                    }

                    brain.actionsToPerform.putIfAbsent(hero.id, new MoveAction(Brain.ATTACK_POSITION));
                    break;
                case CONTROL_MONSTER_TO_POSITION:
                    if (targetStillGood(brain, hero)) {
                        currentState = EAttackState.PREPARE_PUSH_POSITION;
                        brain.player.castSpell(hero, new ControlSpellAction(targetMonster, Brain.CONTROL_MONSTER_TO_POSITION_ONE), brain.actionsToPerform);
                    }
                    break;
                case PREPARE_PUSH_POSITION:
                    if (targetStillGood(brain, hero)) {
                        if (n <= 1) {
                            currentState = EAttackState.PUSH_MONSTER;
                        }
                        Pair<Integer> preparePushPosition = getPPositionInNTurns(brain, targetMonster, n);
                        brain.actionsToPerform.putIfAbsent(hero.id, new MoveAction(preparePushPosition));
                        n--;
                        break;
                    }
                    break;
                case PUSH_MONSTER:
                    if (targetStillGood(brain, hero)) {
                        nbrOfPushes--;
                        if (nbrOfPushes <= 0) {
                            currentState = EAttackState.CONTROL_OPPONENT_AWAY;
                        }

                        Pair<Float> direct = brain.getDirection(hero.pos, brain.opponent.pos);
                        float directNorm = brain.getDistance(hero.pos, brain.opponent.pos);
                        Pair<Float> directVect = new Pair<>(direct.x / directNorm, direct.y / directNorm);
                        brain.player.castSpell(hero, new WindSpellAction(hero, new Pair<>(directVect.x * 1200f, directVect.y * 1200f), "Wind " + nbrOfPushes), brain.actionsToPerform);
                        break;
                    }
                    break;
                case CONTROL_OPPONENT_AWAY:
                    if (brain.oppHeroesInsideBase.isEmpty()) {
                        reset();
                        handleHero(brain, hero);
                        break;
                    }

                    nbrOfControls--;
                    if (nbrOfControls <= 0) {
                        currentState = EAttackState.NOT_READY;
                    }

                    Hero bestOppHero = null;
                    float bestDist = Float.MAX_VALUE;
                    for (Hero oppHero : brain.oppHeroesInsideBase) {
                        if (oppHero.shieldLife != 0) {
                            continue;
                        }
                        float oppHeroDist = brain.getDistance(oppHero.pos, brain.opponent.pos);
                        if (oppHeroDist < bestDist) {
                            bestOppHero = oppHero;
                            bestDist = oppHeroDist;
                        }
                    }

                    if (bestOppHero == null) {
                        reset();
                        handleHero(brain, hero);
                        break;
                    }
                    brain.player.castSpell(hero, new ControlSpellAction(bestOppHero, brain.player.pos), brain.actionsToPerform);
                    break;
            }
        }

        public Pair<Integer> getPPositionInNTurns(Brain brain, Monster monster, int n) {
            Pair<Integer> monsterPosN = new Pair<>(monster.pos.x + n * monster.vx, monster.pos.y + n * monster.vy);
            Pair<Float> direct = brain.getDirection(monsterPosN, brain.opponent.pos);
            float directNorm = brain.getDistance(monsterPosN, brain.opponent.pos);
            Pair<Float> directVect = new Pair<>(direct.x / directNorm, direct.y / directNorm);

            return new Pair<>((int) (monsterPosN.x + directVect.x * 1200f), (int) (monsterPosN.y + directVect.y * 1200f));
        }

        public void reset() {
            currentState = EAttackState.NOT_READY;
            targetMonster = null;
            nbrOfPushes = 2;
            n = 3;
            nbrOfControls = 2;
        }

        public boolean targetStillGood(Brain brain, Hero hero) {
            if (!brain.containsMonster(targetMonster.id, brain.allMonstersOppSide.values())) {
                reset();
                handleHero(brain, hero);
                return false;
            }

            targetMonster = brain.allMonstersOppSide.get(targetMonster.id);
            return true;
        }
    }

    static class Base {
        private static final int MIN_MANA = 20;

        Pair<Integer> pos;
        int health;
        private int mana;
        List<Hero> heroes;

        public Base(int x, int y) {
            this.heroes = new ArrayList<>();
            this.pos = new Pair<>(x, y);
        }

        public Base(Scanner in) {
            this.heroes = new ArrayList<>();
            this.pos = new Pair<>(in.nextInt(), in.nextInt());
        }

        public boolean canCastUrgentSpell() {
            return mana > 10;
        }

        public boolean canCastSpell() {
            return mana > MIN_MANA;
        }

        public void castSpell(Hero hero, BaseAction spell, Map<Integer, BaseAction> actionsToPerform) {
            actionsToPerform.putIfAbsent(hero.id, spell);
            hero.state = EHeroState.STATE_ALREADY_SET;
            mana -= 10;
        }
    }

    static class Pair<T> {
        T x, y;

        public Pair(T x, T y) {
            this.x = x;
            this.y = y;
        }
    }

    static class Hero extends Entity {
        EHeroState state;
        boolean isPosOne;

        Hero(int id, int type, int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor, boolean isPosOne) {
            super(id, type, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor);
            this.state = null;
            this.isPosOne = isPosOne;
        }

        public void setState(EHeroState newState) {
            if (this.state != null) {
                return;
            }

            this.state = newState;
        }
    }

    static class Monster extends Entity {
        boolean isAttacked;
        int nbrOfHeroesAttacking;

        Monster(int id, int type, int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor) {
            super(id, type, x, y, shieldLife, isControlled, health, vx, vy, nearBase, threatFor);
            nbrOfHeroesAttacking = 0;
            isAttacked = false;
        }
    }

    static class Entity {
        int id, type, shieldLife, isControlled, health, vx, vy, nearBase, threatFor;
        Pair<Integer> pos;

        Entity(int id, int type, int x, int y, int shieldLife, int isControlled, int health, int vx, int vy, int nearBase, int threatFor) {
            this.id = id;
            this.type = type;
            this.pos = new Pair<>(x, y);
            this.shieldLife = shieldLife;
            this.isControlled = isControlled;
            this.health = health;
            this.vx = vx;
            this.vy = vy;
            this.nearBase = nearBase;
            this.threatFor = threatFor;
        }
    }

    enum EHeroState {
        ATTACK_RANDOM_OPPONENT_MONSTERS, STATE_ALREADY_SET,
        DEFEND_BASE, PATROL_PLAYER, PATROL_CENTER_OPPONENT, DESTROY_OPPONENT
    }

    // ===================================================================================
    // ===================================== Actions =====================================
    // ===================================================================================

    static class ControlSpellAction extends BaseAction {
        int entityId;
        Pair<Integer> pos;

        public ControlSpellAction(Entity entity, Pair<Integer> pos) {
            super("MC " + entity.id);
            this.entityId = entity.id;
            this.pos = pos;
            entity.isControlled = 1;
        }

        @Override
        void performAction() {
            System.out.printf("SPELL CONTROL %d %d %d %s%n", entityId, pos.x, pos.y, message);
        }
    }

    static class ShieldSpellAction extends BaseAction {
        int entityId;

        public ShieldSpellAction(int entityId) {
            super("Shield " + entityId);
            this.entityId = entityId;
        }

        public ShieldSpellAction(int entityId, String message) {
            super(message);
            this.entityId = entityId;
        }

        @Override
        void performAction() {
            System.out.printf("SPELL SHIELD %d %s%n", entityId, message);
        }
    }

    static class WindSpellAction extends BaseAction {
        Pair<Integer> pos;
        Pair<Float> direction;

        public WindSpellAction(Entity entity, Pair<Float> direction) {
            super("");
            this.pos = entity.pos;
            this.direction = direction;
        }

        public WindSpellAction(Entity entity, Pair<Float> direction, String message) {
            super(message);
            this.pos = entity.pos;
            this.direction = direction;
        }

        @Override
        void performAction() {
            System.out.printf("SPELL WIND %d %d %s%n", (int) (pos.x + direction.x), (int) (pos.y + direction.y), message);
        }
    }

    static class MoveAction extends BaseAction {
        Pair<Integer> pos;

        public MoveAction(Pair<Integer> pos) {
            super("");
            this.pos = pos;
        }

        public MoveAction(Pair<Integer> pos, int targetId) {
            super("" + targetId);
            this.pos = pos;
        }

        @Override
        void performAction() {
            System.out.printf("MOVE %d %d %s%n", pos.x , pos.y, message);
        }
    }

    static abstract class BaseAction {
        String message;

        public BaseAction(String message) {
            this.message = message;
        }

        abstract void performAction();
    }
}
