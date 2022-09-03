package CodinGame.UnleashTheGeek;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Scanner;

import static java.lang.Math.abs;
import static java.lang.Math.random;

class Coord {
    final int x;
    final int y;

    Coord(int x, int y) {
        this.x = x;
        this.y = y;
    }

    Coord(Scanner in) {
        this(in.nextInt(), in.nextInt());
    }

    Coord add(Coord other) {
        return new Coord(x + other.x, y + other.y);
    }

    // Manhattan distance (for 4 directions maps)
    // see: https://en.wikipedia.org/wiki/Taxicab_geometry
    int distance(Coord other) {
        return abs(x - other.x) + abs(y - other.y);
    }

    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + x;
        result = PRIME * result + y;
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Coord other = (Coord) obj;
        return (x == other.x) && (y == other.y);
    }

    public String toString() {
        return x + " " + y;
    }
}


class Cell {
    boolean known;
    int ore;
    boolean hole;
    boolean dugByEnnemy;

    Cell() {
        known = false;
        ore = 0;
        hole = false;
        dugByEnnemy = false;
    }

    Cell(boolean known, int ore, boolean hole) {
        this.known = known;
        this.ore = ore;
        this.hole = hole;
        this.dugByEnnemy = false;
    }

    Cell(Scanner in) {
        String oreStr = in.next();
        if (oreStr.charAt(0) == '?') {
            known = false;
            ore = 0;
        } else {
            known = true;
            ore = Integer.parseInt(oreStr);
        }
        String holeStr = in.next();
        hole = (holeStr.charAt(0) != '0');
    }

    public Cell copy() {
        Cell c = new Cell(this.known, this.ore, this.hole);
        c.dugByEnnemy = this.dugByEnnemy;
        return c;
    }
}

enum EAction {
    WAIT, DEAD, MOVE, DIG, REQUEST_RADAR, REQUEST_TRAP, KAMIKAZE, KAMIKAZE_BLOW_UP, SINGLE_KAMIKAZE, SINGLE_KAMIKAZE_PLANT, SINGLE_KAMIKAZE_BLOW_UP
}

class Action {
    final String command;
    final Coord pos;
    final EntityType item;
    String message;

    private Action(String command, Coord pos, EntityType item) {
        this.command = command;
        this.pos = pos;
        this.item = item;
    }

    static Action none() {
        return new Action("WAIT", null, null);
    }

    static Action move(Coord pos) {
        return new Action("MOVE", pos, null);
    }

    static Action dig(Coord pos) {
        return new Action("DIG", pos, null);
    }

    static Action request(EntityType item) {
        return new Action("REQUEST", null, item);
    }

    public String toString() {
        StringBuilder builder = new StringBuilder(command);
        if (pos != null) {
            builder.append(' ').append(pos);
        }
        if (item != null) {
            builder.append(' ').append(item);
        }
        if (message != null) {
            builder.append(' ').append(message);
        }
        return builder.toString();
    }
}


enum EntityType {
    NOTHING, ALLY_ROBOT, ENEMY_ROBOT, RADAR, TRAP, AMADEUSIUM;

    static EntityType valueOf(int id) {
        return values()[id + 1];
    }
}

class OreLocation {
    int totOre;
    Coord pos;

    public OreLocation(int totOre, Coord pos) {
        this.totOre = totOre;
        this.pos = pos;
    }
}

class Entity {
    private static final Coord DEAD_POS = new Coord(-1, -1);

    // Updated every turn
    int id;
    EntityType type;
    EAction prevAction;
    Coord pos;
    Coord prevPos;
    EntityType item;
    Entity target = null;

    // Computed for my robots
    Action action;

    public Entity() {
        this.id = 0;
        this.type = null;
        this.pos = null;
        this.prevAction = EAction.WAIT;
    }

    public Entity(Scanner in) {
        id = in.nextInt();
        type = EntityType.valueOf(in.nextInt());
        pos = new Coord(in);
        item = EntityType.valueOf(in.nextInt());
        prevAction = EAction.WAIT;
        action = Action.move(new Coord(0,0));
    }

    boolean isAlive() {
        return !DEAD_POS.equals(pos);
    }

    public void update(Entity entity) {
        this.id = entity.id;
        this.type = entity.type;
        this.prevPos = new Coord(pos.x, pos.y);
        this.pos = entity.pos;
        this.item = entity.item;
    }
}

enum GameState {
    USE_LINE_KAMIKAZE, USE_SINGLE_KAMIKAZE, END_GAME
}

class Team {
    int score;
    ArrayList<Entity> robots = new ArrayList<>();

    public Team(int size) {
        for (int i = 0; i < size; i++) {
            Entity newRobot = new Entity();
            robots.add(newRobot);
        }
    }

    void readScore(Scanner in) {
        score = in.nextInt();
    }
}

class Board {
    boolean firstUpdate = true;
    final int nbrRobots = 5;

    // Given at startup
    final int width;
    final int height;

    // Updated each turn
    final Team myTeam = new Team(nbrRobots);
    final Team opponentTeam = new Team(nbrRobots);
    private Cell[][] cells;
    private Cell[][] prevCells;

    private ArrayList<OreLocation> oreLocations;
    int totOreAvailable = 0;
    int maxOresBeforeGettingRarar = 15;
    int midX = 7;

    GameState state = GameState.USE_LINE_KAMIKAZE;
    int kamikazeId = 0;
    int startTrapPosY = 2;

    int myRadarCooldown;
    int myTrapCooldown;
    boolean gettingTrap = false;
    boolean gettingRadar = false;
    Map<Integer, Entity> entitiesById;
    Collection<Coord> myRadarPos;
    Collection<Coord> myTrapPos;

    Board(Scanner in) {
        width = in.nextInt();
        height = in.nextInt();
        cells = new Cell[height][width];
        prevCells = new Cell[height][width];

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new Cell();
                prevCells[y][x] = new Cell();
            }
        }
    }

    void update(Scanner in) {
        totOreAvailable = 0;
        oreLocations = new ArrayList<>();

        // Read new data
        myTeam.readScore(in);
        opponentTeam.readScore(in);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                prevCells[y][x] = cells[y][x].copy();
                cells[y][x] = new Cell(in);
                cells[y][x].dugByEnnemy = prevCells[y][x].dugByEnnemy;
            }
        }

        int entityCount = in.nextInt();
        myRadarCooldown = in.nextInt();
        myTrapCooldown = in.nextInt();

        entitiesById = new HashMap<>();
        myRadarPos = new ArrayList<>();
        myTrapPos = new ArrayList<>();
        for (int i = 0; i < entityCount; i++) {
            Entity entity = new Entity(in);
            entitiesById.put(entity.id, entity);
            switch (entity.type) {
                case ALLY_ROBOT:
                    if(firstUpdate) {
                        myTeam.robots.set(entity.id%nbrRobots, entity);
                    } else {
                        myTeam.robots.get(entity.id%nbrRobots).update(entity);
                    }
                    break;
                case ENEMY_ROBOT:
                    if(firstUpdate) {
                        opponentTeam.robots.set(entity.id%nbrRobots, entity);
                    } else {
                        opponentTeam.robots.get(entity.id%nbrRobots).update(entity);
                    }
                    break;
                case RADAR:
                    myRadarPos.add(entity.pos);
                    break;
                case TRAP:
                    myTrapPos.add(entity.pos);
                    break;
                default:
            }
        }

        if(firstUpdate) {
            firstUpdate = false;
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if(!cells[y][x].dugByEnnemy && cells[y][x].known && cells[y][x].ore > 0 && !myTrapPos.contains(new Coord(x,y))) {
                    totOreAvailable += cells[y][x].ore;
                    oreLocations.add(new OreLocation(cells[y][x].ore, new Coord(x,y)));
                }

                if(cells[y][x].hole != prevCells[y][x].hole) {
                    Coord newHolePos = new Coord(x,y);
                    boolean myTeamMadeHole = false;

                    for (Entity robot : myTeam.robots) {
                        if((robot.action.command.equals("MOVE") | robot.action.command.equals("DIG")) && robot.pos.equals(newHolePos)) {
                            // My robots made the new hole
                            myTeamMadeHole = true;
                            break;
                        }
                    }

                    if(!myTeamMadeHole) {
                        cells[y][x].dugByEnnemy = true;
                    }
                }
            }
        }
    }

    int getId(int id) {
        return id % nbrRobots;
    }

    boolean cellExist(Coord pos) {
        return (pos.x >= 0) && (pos.y >= 0) && (pos.x < width) && (pos.y < height);
    }

    boolean cellExist(int x, int y) {
        return (x >= 0) && (y >= 0) && (x < width) && (y < height);
    }

    boolean canDigHere(int x, int y) {
        return (x >= 1) && (y >= 0) && (x < width) && (y < height);
    }

    Cell getCell(Coord pos) {
        return cells[pos.y][pos.x];
    }

    public boolean shouldGetRadar() {
        System.err.println((totOreAvailable <= maxOresBeforeGettingRarar) + " - " + !gettingRadar + " - " + (myRadarCooldown == 0));
        return (totOreAvailable <= maxOresBeforeGettingRarar) && !gettingRadar && myRadarCooldown == 0;
    }
}


class Player {

    public static void main(String args[]) {
        new Player().run();
    }

    final Scanner in = new Scanner(System.in);

    final Coord[] radarPositions = {
            new Coord(6,8),
            new Coord(14,8), new Coord(10,13), new Coord(10,3),
            new Coord(22,8), new Coord(18,13), new Coord(18,3),
            new Coord(26,13), new Coord(26,3), new Coord(29,8),
            new Coord(2,13), new Coord(2,3)
    };

    final int[] xNeighborPos = { 1, 0, -1, 0};
    final int[] yNeighborPos = { 0, -1, 0, 1};

    void run() {
        // Parse initial conditions
        Board board = new Board(in);

        while (true) {
            // Parse current state of the game
            board.update(in);
            System.err.println(board.state + " - kamiId:" + board.kamikazeId);
            // Insert your strategy here
            for (Entity robot : board.myTeam.robots) {
                if(robot.isAlive()){
                    // Check if game finished
                    if(board.state == GameState.END_GAME) {
                        robot.prevAction = EAction.WAIT;
                    }

                    // Set Kamikaze robot if needed
                    else if(board.state == GameState.USE_LINE_KAMIKAZE && board.getId(robot.id) == board.kamikazeId &&
                            robot.prevAction != EAction.KAMIKAZE && robot.prevAction != EAction.KAMIKAZE_BLOW_UP) {
                        if(robot.prevAction == EAction.REQUEST_RADAR) {
                            board.gettingRadar = false;
                        }
                        robot.prevAction = EAction.KAMIKAZE;
                    }

                    // Set single Kamikaze robot if needed
                    else if(board.state == GameState.USE_SINGLE_KAMIKAZE && board.getId(robot.id) == board.kamikazeId &&
                            robot.prevAction != EAction.SINGLE_KAMIKAZE && robot.prevAction != EAction.SINGLE_KAMIKAZE_BLOW_UP &&
                            robot.prevAction != EAction.SINGLE_KAMIKAZE_PLANT) {
                        if(robot.prevAction == EAction.REQUEST_RADAR) {
                            board.gettingRadar = false;
                        }
                        robot.prevAction = EAction.SINGLE_KAMIKAZE;
                        // Assign target
                        if(robot.target == null) {
                            for (Entity opponentRobot : board.opponentTeam.robots) {
                                if(opponentRobot.isAlive()) {
                                    robot.target = opponentRobot;
                                    break;
                                }
                            }

                            // No robots are alive
                            if(robot.target == null) {
                                board.state = GameState.END_GAME;
                                robot.prevAction = EAction.WAIT;
                            }
                        }
                    }

                    // Handle special robots
                    switch(robot.prevAction) {
                        case KAMIKAZE: // Continue Kamikaze
                            handleLineKamikaze(robot, board);
                            continue;
                        case KAMIKAZE_BLOW_UP: // Blow up
                            explodeUnit(robot, board);
                            continue;
                        case SINGLE_KAMIKAZE: // Continue single kamikaze strat
                            handleSingleKamikaze(robot, board);
                            continue;
                        case SINGLE_KAMIKAZE_PLANT: // Plant trap before opponent gets to base
                            robot.action = Action.dig(new Coord(1, robot.target.pos.y));
                            if(robot.item == EntityType.NOTHING) {
                                handleSingleKamikaze(robot, board);
                            }
                            continue;
                        case SINGLE_KAMIKAZE_BLOW_UP: // Blow up
                            if(!explodeUnit(robot, board)) {
                                robot.prevAction = EAction.SINGLE_KAMIKAZE;
                                robot.action = Action.request(EntityType.TRAP);
                            }
                            continue;
                        case REQUEST_RADAR: // Continue fetching radar
                            if(robot.item == EntityType.RADAR) {
                                board.gettingRadar = false;
                                handleMiner(robot, board);
                            } else {
                                robot.action = Action.request(EntityType.RADAR);
                            }
                            continue;
                        case DEAD:
                            robot.action = Action.move(new Coord(0,0));
                            continue;
                        default:
                            break;
                    }
                                
                                /*
                                // Continue fetching trap
                                if(robot.prevAction == EAction.REQUEST_TRAP) {
                                    if(robot.item == EntityType.TRAP) {
                                        board.gettingTrap = false;
                                        handleMiner(robot, board);
                                    } else {
                                        robot.action = Action.request(EntityType.TRAP);
                                    }
                                    continue;
                                }
                                */

                    // Check if Radar should be fetched
                    if(board.shouldGetRadar()) {
                        robot.prevAction = EAction.REQUEST_RADAR;
                        handleRadarPlanter(robot, board);
                        continue;
                    }

                    handleMiner(robot, board);
                } else {
                    robot.action = Action.move(new Coord(0, 0));
                }
            }

            // Send your actions for this turn
            for (Entity robot : board.myTeam.robots) {
                System.out.println(robot.action);
            }
        }
    }

    public boolean opponentRobotsLeft(Board board) {
        for (Entity opponentRobot : board.opponentTeam.robots) {
            if(opponentRobot.isAlive()) {
                return true;
            }
        }
        return false;
    }

    public boolean makeThingGoBoom(Board board, Entity currentRobot, Coord enemyPos) {
        Coord trapPosition = new Coord(-1, -1);

        for (Coord trapPos : board.myTrapPos) {
            if(trapPos.x == 1 && trapPos.y == enemyPos.y){
                trapPosition = trapPos;
            }
        }

        if(trapPosition.x == -1) { // No trap found
            return false;
        }

        int myCasualties = 0;
        int enemyCasualties = 0;

        for (Entity enemyRobot : board.opponentTeam.robots) {
            if(enemyRobot.isAlive() && inExplosionRange(trapPosition, enemyRobot.pos)){
                enemyCasualties++;
            }
        }

        for (Entity myRobot : board.myTeam.robots) {
            if(myRobot.isAlive() && inExplosionRange(trapPosition, myRobot.pos)){
                myCasualties++;
            }
        }

        return ((enemyCasualties - myCasualties) >= 0);
    }

    public boolean inExplosionRange(Coord myPos, Coord opponentPos) {
        if(myPos.equals(opponentPos)) {
            return true;
        }
        for (int i = 0; i < xNeighborPos.length; i++) {
            int newX = myPos.x + xNeighborPos[i];
            int newY = myPos.y + yNeighborPos[i];
            if(opponentPos.equals(new Coord(newX, newY))) {
                return true;
            }
        }

        return false;
    }

    public boolean makeThingsGoBoom(Board board) {
        Coord lowestTrapPos = new Coord(-1, -1);

        for (Coord trapPos : board.myTrapPos) {
            if(trapPos.x == 1 && trapPos.y > lowestTrapPos.y){
                lowestTrapPos = trapPos;
            }
        }

        if(lowestTrapPos.x == -1) { // No traps yet
            return false;
        }

        Coord topTrapRadiusCell = new Coord(1, board.startTrapPosY - 1);
        Coord botTrapRadiusCell = new Coord(1, lowestTrapPos.y + 1);

        int myCasualties = 0;
        int enemyCasualties = 0;

        for (Entity enemyRobot : board.opponentTeam.robots) {
            if((enemyRobot.pos.x <= 2 && enemyRobot.pos.y <= lowestTrapPos.y && enemyRobot.pos.y >= board.startTrapPosY)
               || enemyRobot.pos.equals(topTrapRadiusCell)
               || enemyRobot.pos.equals(botTrapRadiusCell)){
                enemyCasualties++;
            }
        }

        for (Entity myRobot : board.myTeam.robots) {
            if((myRobot.pos.x <= 2 && myRobot.pos.y <= lowestTrapPos.y && myRobot.pos.y >= board.startTrapPosY)
               || myRobot.pos.equals(topTrapRadiusCell)
               || myRobot.pos.equals(botTrapRadiusCell)){
                myCasualties++;
            }
        }
        System.err.println("My:" + myCasualties + " - op:" + enemyCasualties + " - final:" + ((enemyCasualties - myCasualties) > 0));
        return ((enemyCasualties - myCasualties) > 0);
    }

    public void handleSingleKamikaze(Entity robot, Board board) {
        System.err.println("id:" + robot.id + " - " + robot.prevAction + " - " + robot.item + " - targetId:" + robot.target.id);
        if(robot.target != null && !robot.target.isAlive()) { // Target died by itself, getting new target
            for (Entity opponentRobot : board.opponentTeam.robots) {
                if(opponentRobot.isAlive()) {
                    robot.target = opponentRobot;
                    break;
                }
            }

            // No robots are alive
            if(robot.target == null || (robot.target != null && !robot.target.isAlive())) {
                board.state = GameState.END_GAME;
                robot.prevAction = EAction.WAIT;
            }
        }
        System.err.println("Go boom:" + makeThingGoBoom(board, robot, robot.target.pos));
        if(robot.target != null && robot.item != EntityType.TRAP && makeThingGoBoom(board, robot, robot.target.pos)) {
            if(explodeUnit(robot, board)) {
                board.state = GameState.USE_SINGLE_KAMIKAZE;
                robot.prevAction = EAction.SINGLE_KAMIKAZE_BLOW_UP;

                int nextKamikazeId = getNextKamikazeRobot(board, robot);
                if(nextKamikazeId < 0) {
                    board.state = GameState.END_GAME;
                } else {
                    board.kamikazeId = nextKamikazeId;
                }
                return;
            }
        }

        switch (robot.item) {
            case TRAP: // Plant trap if opponent coming back
                if((robot.target.prevPos.x - robot.target.pos.x) > 2 && robot.target.prevPos.y == robot.target.pos.y && robot.target.pos.x > 4) { // Robot coming back
                    robot.prevAction = EAction.SINGLE_KAMIKAZE_PLANT;
                    robot.action = Action.dig(new Coord(1, robot.target.pos.y));
                    return;
                }
                // Otherwise wait for opponent to start coming back
                robot.action = Action.move(new Coord(1, robot.target.pos.y));
                break;
            case NOTHING:
                if(robot.prevAction == EAction.SINGLE_KAMIKAZE_PLANT) {
                    if(!board.myTrapPos.contains(new Coord(1, robot.target.pos.y - 1)) &&
                       !board.myTrapPos.contains(new Coord(1, robot.target.pos.y)) &&
                       !board.myTrapPos.contains(new Coord(1, robot.target.pos.y + 1))) {
                        robot.prevAction = EAction.SINGLE_KAMIKAZE;
                    } else {
                        robot.action = Action.move(new Coord(1, robot.target.pos.y));
                    }
                } else {
                    if(robot.pos.x > 3) {
                        robot.action = Action.move(new Coord(0, robot.pos.y + randomMovement()));
                    } else {
                        robot.action = Action.request(EntityType.TRAP);
                    }
                }
                break;
            default: // Get Trap
                robot.action = Action.request(EntityType.TRAP);
                break;
        }
    }

    public void handleLineKamikaze(Entity robot, Board board) {
        if (makeThingsGoBoom(board)) {
            if(explodeUnit(robot, board)) {
                board.state = GameState.USE_SINGLE_KAMIKAZE;
                robot.prevAction = EAction.KAMIKAZE_BLOW_UP;

                int nextKamikazeId = getNextKamikazeRobot(board, robot);
                if(nextKamikazeId < 0) {
                    board.state = GameState.END_GAME;
                } else {
                    board.kamikazeId = nextKamikazeId;
                }
                return;
            }
        }

        switch (robot.item) {
            case TRAP:
                robot.action = null;
                for(int i = board.startTrapPosY; i < board.height; i++) {
                    Coord newTrapPos = new Coord(1, i);
                    if(!board.myTrapPos.contains(newTrapPos)) {
                        robot.action = Action.dig(newTrapPos);
                        break;
                    }
                }

                // Else wait for suicide
                if(robot.action == null) {
                    robot.action = Action.move(new Coord(0, 7));
                }
                break;
            default: // Get Trap
                robot.action = Action.request(EntityType.TRAP);
                break;
        }
    }

    public boolean explodeUnit(Entity robot, Board board) {
        Coord closestTrap = new Coord(-100,-100);
        int distToTrap = 1000000;
        for (Coord trapPos : board.myTrapPos) {
            if(trapPos.distance(robot.pos) <= distToTrap) {
                closestTrap = trapPos;
                distToTrap = trapPos.distance(robot.pos);
            }
        }
        if(closestTrap.x < 0) {
            return false;
        } else {
            robot.action = Action.dig(closestTrap);
            return true;
        }
    }

    public int getNextKamikazeRobot(Board board, Entity currentRobot) {
        for (Entity robot : board.myTeam.robots) {
            if(robot.isAlive() && robot.id != currentRobot.id) {
                return robot.id%board.nbrRobots;
            }
        }
        return -1;
    }

    public void handleRadarPlanter(Entity robot, Board board) {
        switch (robot.item) {
            case NOTHING: // Get radar
                if(robot.pos.x > 3) {
                    robot.action = Action.move(new Coord(0, robot.pos.y + randomMovement()));
                } else {
                    robot.action = Action.request(EntityType.RADAR);
                }
                robot.prevAction = EAction.REQUEST_RADAR;
                board.gettingRadar = true;
                break;
            case AMADEUSIUM: // Get radar
                if(robot.pos.x > 3) {
                    robot.action = Action.move(new Coord(0, robot.pos.y + randomMovement()));
                } else {
                    robot.action = Action.request(EntityType.RADAR);
                }
                robot.prevAction = EAction.REQUEST_RADAR;
                board.gettingRadar = true;
                break;
            case RADAR:
                handleMiner(robot, board);
                break;
            case TRAP:
                handleMiner(robot, board);
                break;
            default:
                robot.action = Action.move(new Coord(0, robot.pos.y));
        }
    }

    public void handleBomber(Entity robot, Board board) {
        switch (robot.item) {
            case NOTHING: // Get trap
                if(robot.pos.x <= 4) {
                    robot.action = Action.request(EntityType.TRAP);
                    robot.prevAction = EAction.REQUEST_TRAP;
                    board.gettingTrap = true;
                } else {
                    handleMiner(robot, board);
                }
                break;
            case AMADEUSIUM: // Get trap
                robot.action = Action.request(EntityType.TRAP);
                robot.prevAction = EAction.REQUEST_TRAP;
                board.gettingTrap = true;
                break;
            case RADAR:
                handleMiner(robot, board);
                break;
            case TRAP:
                handleMiner(robot, board);
                break;
            default:
                robot.action = Action.move(new Coord(0, robot.pos.y));
        }
    }

    public void handleMiner(Entity robot, Board board) {
        switch (robot.item) {
            case NOTHING: // Go mine closest vine
                if(board.totOreAvailable > 0) {
                    robot.action = Action.dig(BFSFindOre(robot, board, 1));
                    if(robot.action.pos.x == 0) {
                        robot.action = Action.dig(BFSFindOreIfNoneAvaiable(robot, board, new Coord(board.midX, robot.pos.y)));
                    }
                } else {
                    robot.action = Action.dig(BFSFindOreIfNoneAvaiable(robot, board, new Coord(board.midX, robot.pos.y)));
                }
                robot.prevAction = EAction.DIG;
                break;
            case RADAR: // Plant radar, otherwise go mine of no radar positions left
                for(Coord pos : radarPositions) {
                    if(!board.myRadarPos.contains(pos)) {
                        robot.action = Action.dig(pos);
                        robot.prevAction = EAction.DIG;
                        return;
                    }
                }
                robot.item = EntityType.NOTHING;
                handleMiner(robot, board);
                break;
            case AMADEUSIUM: // Deposit ore
                robot.action = Action.move(new Coord(0, robot.pos.y + randomMovement()));
                robot.prevAction = EAction.MOVE;
                break;
            case TRAP:
                Coord possibleTrapPos = BFSFindOre(robot, board, 2);
                if(possibleTrapPos.x == 0) {
                    robot.action = Action.dig(BFSFindOreIfNoneAvaiable(robot, board, new Coord(board.midX, robot.pos.y)));
                } else {
                    robot.action = Action.dig(possibleTrapPos);
                    board.getCell(possibleTrapPos).ore -= 1;
                }
                robot.prevAction = EAction.DIG;
                break;
            default:
                robot.action = Action.move(new Coord(0, robot.pos.y));
        }
    }

    public int randomMovement() {
        return ((int) (random() * 6f)) - 2;
    }

    public Coord BFSFindOre(Entity robot, Board board, int minOre) {
        boolean[][] visited = new boolean[board.width][board.height];

        LinkedList<Coord> coordQueue = new LinkedList<Coord>();

        visited[robot.pos.x][robot.pos.y] = true;
        coordQueue.add(robot.pos);

        while(!coordQueue.isEmpty()) {
            Coord currentPos = coordQueue.poll();

            if(board.getCell(currentPos).ore >= minOre && !board.myTrapPos.contains(currentPos) && !board.getCell(currentPos).dugByEnnemy) {
                board.getCell(currentPos).ore --;
                return currentPos;
            }

            // Add neighboars of currentPos to queue
            for (int i = 0; i < 4; i++) {
                int posX = currentPos.x + xNeighborPos[i];
                int posY = currentPos.y + yNeighborPos[i];

                if(board.cellExist(posX, posY) && !visited[posX][posY]) {
                    visited[posX][posY] = true;
                    coordQueue.add(new Coord(posX, posY));
                }
            }
        }

        // If no more ores are available, just sit at base
        return new Coord(0, robot.pos.y);
    }

    public Coord BFSFindOreIfNoneAvaiable(Entity robot, Board board, Coord startPos) {
        boolean[][] visited = new boolean[board.width][board.height];

        LinkedList<Coord> coordQueue = new LinkedList<Coord>();
            /*
            Coord startPos;
            
            if(robot.pos.x == 0) {
                startPos = new Coord(1, robot.pos.y);
            } else {
                startPos = robot.pos;
            }
*/

        visited[startPos.x][startPos.y] = true;
        coordQueue.add(startPos);

        while(!coordQueue.isEmpty()) {
            Coord currentPos = coordQueue.poll();

            if(!board.getCell(currentPos).hole) {
                return currentPos;
            }

            // Add neighboars of currentPos to queue
            for (int i = 0; i < 4; i++) {
                int posX = currentPos.x + xNeighborPos[i];
                int posY = currentPos.y + yNeighborPos[i];

                if(board.canDigHere(posX, posY) && !visited[posX][posY]) {
                    visited[posX][posY] = true;
                    coordQueue.add(new Coord(posX, posY));
                }
            }
        }

        // If no more ores are available, just sit at base
        return new Coord(0, robot.pos.y);
    }
}
