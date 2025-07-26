//package com.coding.challenges.codingame.springchallenge2025;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

class Player {

  static final int BOMB_DAMAGE = 30; // Damage dealt by a bomb
  static final int MAX_THROW_DISTANCE = 4; // Max distance for throwing bombs
  static final int KILL_BONUS = 1000;
  static final int PUSH_OVER_50_BONUS = 200;

  public static void main(String args[]) {
    Game game = new Game();

    while (true) {
      List<String> commands = new ArrayList<>();
      GameState gameState = game.nextState();

      Map<Integer, Position> plannedSteps = Voronoi.planBestVoronoiMoves(gameState);
      for (Agent myAgent : gameState.myAgents) {
        var bestStep = plannedSteps.getOrDefault(myAgent.agentId, new Position(myAgent.x, myAgent.y));
        var myAgentMoved = myAgent.move(bestStep.x, bestStep.y);
        var bestCombat = Stream.of(chooseBestShootTarget(myAgentMoved, gameState), chooseBestThrowTarget(myAgentMoved, plannedSteps, gameState))
                               .flatMap(Optional::stream)
                               .max(Comparator.comparingDouble(c -> c.score))
                               .orElse(new CombatChoice(new HunkerDown(), 0.0))
                .combat;

        if (bestStep.x == myAgent.x && bestStep.y == myAgent.y) {
          commands.add(Command.of(myAgent.agentId, bestCombat));
        } else {
          commands.add(Command.of(myAgent.agentId, new Move(bestStep.x, bestStep.y), bestCombat));
        }
      }

      commands.forEach(System.out::println);
    }
  }

  static int distance(Agent a, Agent b) {
    return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
  }

  static int distance(Agent a, int x, int y) {
    return Math.abs(a.x - x) + Math.abs(a.y - y);
  }

  static boolean isInBombAOE(Agent target, int bombX, int bombY) {
    return isInBombAOE(new Position(target.x, target.y), bombX, bombY);
  }

  static boolean isInBombAOE(Position target, int bombX, int bombY) {
    for (int dx = -1; dx <= 1; dx++) {
      for (int dy = -1; dy <= 1; dy++) {
        int x = bombX + dx;
        int y = bombY + dy;
        if (x == target.x && y == target.y) {
          return true; // Target is in the AOE of the bomb
        }
      }
    }

    return false;
  }

  record Voronoi() {
    // N, E, S, W
    private static final List<Position> MOVES = List.of(new Position(0, -1), new Position(1, 0), new Position(0, 1), new Position(-1, 0));

    private record StepScore(int agentId, Position position, int score) {}

    static Map<Integer, Position> planBestVoronoiMoves(GameState gameState) {
      // Get all planned steps for each agent based on Voronoi scoring
      Map<Integer, StepScore> plannedSteps = new HashMap<>();

      for (Agent agent : gameState.myAgents) {
        plannedSteps.put(agent.agentId, chooseBestVoronoiStep(agent, gameState));
      }

      // Remove duplicates steps by choosing the best score for each position
      Map<Position, StepScore> bestSteps = new HashMap<>();
      for (StepScore step : plannedSteps.values()) {
        bestSteps.merge(step.position, step, (existingStep, newStep) -> newStep.score > existingStep.score ? newStep : existingStep);
      }

      return bestSteps.entrySet().stream().collect(Collectors.toMap((entry) -> entry.getValue().agentId, Map.Entry::getKey));
    }

    static StepScore chooseBestVoronoiStep(Agent agent, GameState gameState) {
      Position bestPosition = new Position(agent.x, agent.y);
      int bestScore = voronoiScore(gameState.myAgents, gameState.enemyAgents, gameState.grid);

      for (Position move : MOVES) {
        int dx = agent.x + move.x;
        int dy = agent.y + move.y;
        if (!isFreeCell(gameState, dx, dy)) continue;

        List<Agent> myAgents = withAgentMoved(gameState.myAgents, agent, dx, dy);
        int score = voronoiScore(myAgents, gameState.enemyAgents, gameState.grid);
        if (score > bestScore) {
          bestScore = score;
          bestPosition = new Position(dx, dy);
        }
      }

      return new StepScore(agent.agentId, bestPosition, bestScore);
    }

    static List<Agent> withAgentMoved(List<Agent> myAgents, Agent agent, int dx, int dy) {
      List<Agent> copy = new ArrayList<>();

      for (Agent a : myAgents) {
        if (a.agentId == agent.agentId) {
          copy.add(agent.move(dx, dy));
        } else {
          copy.add(a);
        }
      }

      return copy;
    }

    static int voronoiScore(List<Agent> myAgents, List<Agent> enemyAgents, Grid grid) {
      int score = 0;
      for (int x = 0; x < grid.width; x++) {
        for (int y = 0; y < grid.height; y++) {
          int myMinDistance = Integer.MAX_VALUE;
          for (Agent a : myAgents) {
            myMinDistance = Math.min(myMinDistance, effectiveDistanceTo(a, x, y));
          }

          int enemyDistance = Integer.MAX_VALUE;
          for (Agent a : enemyAgents) {
            enemyDistance = Math.min(enemyDistance, effectiveDistanceTo(a, x, y));
          }

          if (myMinDistance < enemyDistance) score += 1;
          else if (myMinDistance > enemyDistance) score -= 1;
        }
      }

      return score;
    }

    static int effectiveDistanceTo(Agent agent, int x, int y) {
      int d = Math.abs(agent.x - x) + Math.abs(agent.y - y);
      return agent.wetness >= 50 ? (d * 2) : d;
    }

    static boolean isFreeCell(GameState gameState, int x, int y) {
      if (!gameState.grid.inBounds(x, y)) return false;
      if (!gameState.grid.passable(x, y)) return false;
      for (Agent a : gameState.myAgents) {
        if (a.x == x && a.y == y) return false;
      }
      for (Agent a : gameState.enemyAgents) {
        if (a.x == x && a.y == y) return false;
      }
      return true;
    }
  }

  static Optional<CombatChoice> chooseBestThrowTarget(Agent thrower, Map<Integer, Position> plannedSteps, GameState gameState) {
    if (!thrower.canThrow()) return Optional.empty();

    int bestX = -1;
    int bestY = -1;
    double bestScore = 0.0;

    for (int dx = -MAX_THROW_DISTANCE; dx <= MAX_THROW_DISTANCE; dx++) {
      int maxDy = MAX_THROW_DISTANCE - Math.abs(dx);
      for (int dy = -maxDy; dy <= maxDy; dy++) {
        int x = thrower.x + dx;
        int y = thrower.y + dy;
        if (!gameState.grid.inBounds(x, y)) continue;

        double score = 0.0;

        for (Agent myAgent : gameState.myAgents) {
          var plannedStep = plannedSteps.getOrDefault(myAgent.agentId, new Position(myAgent.x, myAgent.y));
          if (isInBombAOE(plannedStep, x, y)) {
            score = Double.NEGATIVE_INFINITY; // Don't throw bombs where our agents are
            break;
          }
        }
        if (score == Double.NEGATIVE_INFINITY) continue;

        for (Agent enemy : gameState.enemyAgents) {
          if (isInBombAOE(enemy, x, y)) {
            score += scoreDamage(enemy, BOMB_DAMAGE);
          }
        }

        if (score > bestScore) {
          bestScore = score;
          bestX = x;
          bestY = y;
        }
      }
    }

    if (bestScore > 0.0) return Optional.of(new CombatChoice(new Throw(bestX, bestY), bestScore));

    return Optional.empty();
  }

  static Optional<CombatChoice> chooseBestShootTarget(Agent shooter, GameState gameState) {
    if (!shooter.canShoot()) return Optional.empty();

    Agent bestEnemy = null;
    double bestScore = Double.NEGATIVE_INFINITY;

    for (Agent enemy : gameState.enemyAgents) {
      var damage = expectedShootDamage(shooter, enemy, gameState.grid);
      if (damage <= 0.0) continue;

      double score = scoreDamage(enemy, damage);
      if (score > bestScore) {
        bestScore = score;
        bestEnemy = enemy;
      }
    }

    double finalBestScore = bestScore;
    return Optional.ofNullable(bestEnemy).map(e -> new CombatChoice(new Shoot(e.agentId), finalBestScore));
  }

  static double scoreDamage(Agent enemy, double damage) {
    int newWetness = enemy.wetness + (int) Math.round(damage);

    double score = 0.0;
    if (newWetness >= 100) score += KILL_BONUS;
    if (enemy.wetness < 50 && newWetness >= 50) score += PUSH_OVER_50_BONUS;

    score += damage;
    return score;
  }

  static double expectedShootDamage(Agent shooter, Agent target, Grid grid) {
    double rangeFactor = shootRangeFactor(shooter, target);
    if (rangeFactor == 0.0) return 0.0;
    return shooter.soakingPower * rangeFactor * (1.0 - coverReductionAgainst(shooter, target, grid));
  }

  static double shootRangeFactor(Agent shooter, Agent target) {
    int d = distance(shooter, target);
    if (d <= shooter.optimalRange) return 1.0;
    if (d <= 2 * shooter.optimalRange) return 0.5;
    return 0.0;
  }

  static double coverReductionAgainst(Agent shooter, Agent target, Grid grid) {
    int[][] directions = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}}; // N, E, S, W
    double best = 0.0;
    for (int[] dir : directions) {
      int cx = target.x + dir[0];
      int cy = target.y + dir[1];

      if (!grid.inBounds(cx, cy) ||
          grid.tiles[cy][cx] == TileType.EMPTY ||
          distance(shooter, cx, cy) == 1) {
        continue;
      }

      // Shooter must be on the opposite side of the cover relative to target
      // n = (cx - tx, cy - ty)
      int nx = cx - target.x, ny = cy - target.y;
      int dot = (shooter.x - cx) * nx + (shooter.y - cy) * ny;
      if (dot <= 0) continue;

      double reduction = switch (grid.tiles[cy][cx]) {
        case EMPTY -> 0.0;
        case LOW_COVER -> 0.5;
        case HIGH_COVER -> 0.75;
      };
      if (reduction > best) {
        best = reduction;
      }
    }
    return best;
  }

  // ========================== Records ==============================================

  public static class Game {
    private final Scanner in = new Scanner(System.in);

    private final int myId;
    private final int enemyId;
    private final Map<Integer, Agent> agentBlueprints = new HashMap<>();
    private final Grid initialGrid;

    public Game() {
      this.myId = in.nextInt();
      this.enemyId = 1 - myId;
      var agentBlueprintCount = in.nextInt();
      for (int i = 0; i < agentBlueprintCount; i++) {
        var agent = new Agent(
            in.nextInt(), // agentId
            in.nextInt(), // player
            in.nextInt(), // shootCooldown
            in.nextInt(), // optimalRange
            in.nextInt(), // soakingPower
            in.nextInt() // splashBombs
        );
        agentBlueprints.put(agent.agentId, agent);
      }
      this.initialGrid = setupGrid(in);
    }

    public GameState nextState() {
      var agentsThisTurn = in.nextInt();
      var myAgents = new ArrayList<Agent>();
      var enemyAgents = new ArrayList<Agent>();
      for (int i = 0; i < agentsThisTurn; i++) {
        int agentId = in.nextInt();
        Agent agent = agentBlueprints.get(agentId).updateLive(in);

        if (agent.player == myId) {
          myAgents.add(agent);
        } else {
          enemyAgents.add(agent);
        }
      }
      in.nextInt(); // Number of agents controlled by this player
      return new GameState(myAgents, enemyAgents, myId, enemyId, initialGrid);
    }

    private Grid setupGrid(Scanner in) {
      var width = in.nextInt();
      var height = in.nextInt();
      var grid = new TileType[height][width];
      for (int k = 0; k < height * width; k++) {
        int j = in.nextInt(); // j coordinate, 0 is left edge
        int i = in.nextInt(); // i coordinate, 0 is top edge
        int tileType = in.nextInt();
        grid[i][j] = switch (tileType) {
          case 0 -> TileType.EMPTY; // Empty tile
          case 1 -> TileType.LOW_COVER; // Low cover tile
          case 2 -> TileType.HIGH_COVER; // High cover tile
          default -> throw new IllegalStateException("Unexpected value: " + tileType);
        };
      }
      return new Grid(width, height, grid);
    }
  }

  public record GameState(List<Agent> myAgents, List<Agent> enemyAgents, int myId, int enemyId, Grid grid) {}

  public record Grid(int width, int height, TileType[][] tiles) {
    public boolean inBounds(int x, int y) {
      return x >= 0 && x < width && y >= 0 && y < height;
    }
    public boolean passable(int x, int y) {
      return inBounds(x, y) && tiles[y][x] == TileType.EMPTY;
    }
  }

  public record Agent(int agentId, int player, int shootCooldown, int optimalRange, int soakingPower, int splashBombs, int wetness, int x, int y) {
    public Agent (int agentId, int player, int shootCooldown, int optimalRange, int soakingPower, int splashBombs) {
      this(agentId, player, shootCooldown, optimalRange, soakingPower, splashBombs, 0, 0, 0);
    }

    public Agent move(int x, int y) {
      return new Agent(this.agentId, this.player, this.shootCooldown, this.optimalRange, this.soakingPower, this.splashBombs, this.wetness, x, y);
    }

    public Agent updateLive(Scanner in) {
      var newX = in.nextInt();
      var newY = in.nextInt();
      var newCooldown = in.nextInt();
      var newSplashBombs = in.nextInt();
      var newWetness = in.nextInt();
      return new Agent(this.agentId, this.player, newCooldown, this.optimalRange, this.soakingPower, newSplashBombs, newWetness, newX, newY);
    }

    public boolean canShoot() {
      return shootCooldown == 0;
    }

    public boolean canThrow() {
      return splashBombs > 0;
    }
  }

  public enum TileType { EMPTY, LOW_COVER, HIGH_COVER }

  record CombatChoice(Combat combat, double score) {}

  record Command(int agentId, Move move, Combat combat, Message message) {
    static String of(int agentId, Move move) { return new Command(agentId, move, new HunkerDown(), null).encode(); }
    static String of(int agentId, Combat combat) { return new Command(agentId, null, combat, null).encode(); }
    static String of(int agentId, Move move, Combat combat) { return new Command(agentId, move, combat, null).encode(); }

    public String encode() {
      List<String> commands = new ArrayList<>();
      if (move != null) commands.add(move.encode());
      if (combat != null) commands.add(combat.encode());
      if (message != null) commands.add(message.encode());
      return agentId + ";" + String.join(";", commands);
    }
  }
  record Position(int x, int y) {}

  interface Combat { String encode(); }
  record Shoot(int targetId) implements Combat { public String encode() { return "SHOOT %d".formatted(targetId); } }
  record Throw(int x, int y) implements Combat { public String encode() { return "THROW %d %d".formatted(x, y); } }
  record HunkerDown() implements Combat { public String encode() { return "HUNKER_DOWN"; } }
  record Move(int x, int y) { public String encode() { return "MOVE %d %d".formatted(x, y); } }
  record Message(String message) { public String encode() { return "MESSAGE %s".formatted(message); } }
}