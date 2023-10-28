import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        Globals globals = new Globals(in);
        Brain brain = new Brain(globals, in);

        while (true) {
            brain.updateValues();
//            brain.playTurn();
//            brain.playTurn2();
            brain.playTurn3();
//            brain.playTurn4();
        }
    }
}

enum GameState {
    GET_EGGS_FIRST_LAYER, GET_EGGS_SECOND_LAYER,
    GET_CRYSTALS_ENEMY_SIDE, GET_CRYSTALS_OWN_SIDE
}

class Brain {
    GameState state;
    Globals globals;
    Map<Integer, Cell> cellMap;
    Scanner in;
    List<Action> actions;
    int myAnts;
//    int radius;

    public Brain(Globals globals, Scanner in) {
        this.state = GameState.GET_EGGS_FIRST_LAYER;
        this.globals = globals;
        this.cellMap = new HashMap<>();
        this.in = in;
//        this.radius = 1;
    }

    public void updateValues() {
        actions = new ArrayList<>();
        globals.eggsAndCrystals = new HashMap<>();
        globals.eggs = new HashMap<>();
        globals.crystals = new HashMap<>();
        myAnts = 0;
        for (int i = 0; i < globals.nbrCells; i++) {
            Cell updatedCell = globals.getUpdatedCell(i, in);
            myAnts += updatedCell.myAnts;
            cellMap.put(i, updatedCell);
            switch (updatedCell.type) {
                case EGG:
                    globals.eggs.put(updatedCell.index, updatedCell);
                    if (updatedCell.resources > 0) {
                        globals.eggsAndCrystals.put(updatedCell.index, updatedCell);
                    }
                    break;
                case CRYSTAL:
                    globals.crystals.put(updatedCell.index, updatedCell);
                    if (updatedCell.resources > 0) {
                        globals.eggsAndCrystals.put(updatedCell.index, updatedCell);
                    }
                    break;
            }
        }
    }

//    public void playTurn4() {
//        AtomicBoolean foundSomething = new AtomicBoolean(false);
//        List<Integer> cellsToUse = new ArrayList<>();
//        actions = new ArrayList<>();
//        Arrays.stream(globals.myBases).forEach(
//          baseId -> {
//              System.err.println("Base " + baseId + " radius " + radius);
//              List<Integer> cellsToAdd = globals.eggsAndCrystals.values().stream()
//                .map(cell -> cell.index)
//                .filter(id -> globals.distanceToCellMap.get(baseId).get(id) <= radius)
//                .filter(id -> !cellsToUse.contains(id))
//                .collect(Collectors.toList());
//
//              if (!cellsToAdd.isEmpty()) {
//                  foundSomething.set(true);
//                  cellsToUse.addAll(cellsToAdd);
//                  cellsToAdd.forEach(id -> actions.add(new Line(baseId, id, 1)));
//              }
//          });
//
//        if (!foundSomething.get()) {
//            this.radius++;
//            playTurn4();
//            return;
//        }
//
//        System.out.println(actions.stream().map(Action::execute).collect(Collectors.joining(";")));
//    }

    public void playTurn3() {
        boolean lookingForPath = true;
        actions = new ArrayList<>();
        int distance = 0;
        int currentCellIndex = globals.myBases[0];
        List<Integer> visitedCells = new ArrayList<>();
        visitedCells.add(currentCellIndex);

        while (lookingForPath) {
            int finalCurrentCellIndex = currentCellIndex;
            int nextCellIndex = globals.eggsAndCrystals.values().stream()
              .filter(cell -> !visitedCells.contains(cell.index))
              .min(Comparator.comparingInt(cell -> globals.distanceToCellMap.get(finalCurrentCellIndex).get(cell.index)))
              .orElse(new Cell(-1, CellType.NO_RESOURCES, -1, -1, -1))
              .index;

            if (nextCellIndex < 0) {
                lookingForPath = false;
                continue;
            }

            distance += globals.distanceToCellMap.get(currentCellIndex).get(nextCellIndex);
            actions.add(new Line(currentCellIndex, nextCellIndex, 1));
            visitedCells.add(nextCellIndex);
            currentCellIndex = nextCellIndex;

            if (((float) myAnts / (float) distance) < 2.5) {
                lookingForPath = false;
            }
        }

        System.out.println(actions.stream().map(Action::execute).collect(Collectors.joining(";")));
    }

//    static class NewPathState {
//        int index;
//        int heuristic;
//        int minResource;
//        int[] path;
//
//        public NewPathState(int index, int heuristic, int[] path, int minResource) {
//            this.index = index;
//            this.heuristic = heuristic;
//            this.path = path;
//            this.minResource = minResource;
//        }
//    }
//
//    public int getHeuristic(int index, int minResource, int[] path) {
//        return (myAnts / path.length) > minResource ? 0 : 1;
//    }
//
//    public void playTurn2() {
//        Queue<NewPathState> queue = new PriorityQueue<>(Comparator.comparingInt(o -> o.heuristic));
//        boolean[] visited = new boolean[globals.nbrCells];
//        queue.add(new NewPathState(globals.myBases[0], 0, new int[]{globals.myBases[0]}, 0));
//        visited[globals.myBases[0]] = true;
//
//        int bestHeuristic = 0;
//        int[] bestPath = new int[]{globals.myBases[0]};
//
//        while (!queue.isEmpty()) {
//            NewPathState current = queue.poll();
//
//            System.err.println("Current: " + current.index + " " + current.heuristic + " " + Arrays.toString(current.path));
//            if (current.heuristic > bestHeuristic) {
//                System.err.println("Best so far: " + current.index + " " + current.heuristic + " " + Arrays.toString(current.path));
//                bestHeuristic = current.heuristic;
//                bestPath = current.path;
//            }
//
//            if (current.path.length > (myAnts * 0.75)) {
//                continue;
//            }
//
//            for (int neighborIndex : globals.neighborMap.get(current.index).values()) {
//                if (neighborIndex == -1) continue;
//
//                if (!visited[neighborIndex]) {
//                    int[] newPath = Arrays.copyOf(current.path, current.path.length + 1);
//                    newPath[newPath.length - 1] = neighborIndex;
//
//                    int minResource = current.minResource;
//                    int cellResources = cellMap.get(neighborIndex).resources;
//                    if (current.minResource == 0) {
//                        if (cellResources > 0) {
//                            minResource = cellResources;
//                        }
//                    } else {
//                        minResource = Math.min(current.minResource, cellResources);
//                    }
//
//                    queue.add(new NewPathState(neighborIndex, getHeuristic(neighborIndex, minResource, current.path), newPath, minResource));
//                    visited[neighborIndex] = true;
//                }
//            }
//        }
//
//        Arrays.stream(bestPath)
//          .mapToObj(i -> cellMap.get(i))
//          .map(cell -> new Beacon(cell.index, 1))
//          .forEach(actions::add);
//
//        System.out.println(actions.stream().map(Action::execute).collect(Collectors.joining(";")));
//    }

//    public void swapState(GameState newState, String message) {
//        System.err.println(message);
//        state = newState;
//        playTurn();
//    }
//
//    public void playTurn() {
//        List<Action> newActions;
//        switch (state) {
//            case GET_EGGS_FIRST_LAYER:
//                newActions = globals.eggs.values().stream()
//                  .filter(egg -> egg.resources > 0)
//                  .filter(egg -> globals.distanceToCellMap.get(globals.myBases[0]).get(egg.index) == 1)
//                  .map(egg -> new Line(globals.myBases[0], egg.index, 1))
//                  .collect(Collectors.toList());
//
//                if (!newActions.isEmpty()) {
//                    actions.addAll(newActions);
//                } else {
//                    swapState(GameState.GET_EGGS_SECOND_LAYER, "No eggs in first layer, swapping to second layer");
//                    return;
//                }
//                break;
//            case GET_EGGS_SECOND_LAYER:
//                newActions = globals.eggs.values().stream()
//                  .filter(egg -> egg.resources > 0)
//                  .filter(egg -> globals.distanceToCellMap.get(globals.myBases[0]).get(egg.index) == 2)
//                  .map(egg -> Arrays.asList(new Line(globals.myBases[0], egg.index, 1), new Beacon(egg.index, 2)))
//                  .flatMap(Collection::stream)
//                  .collect(Collectors.toList());
//
//                if (!newActions.isEmpty()) {
//                    actions.addAll(newActions);
//                } else {
////                    swapState(GameState.GET_CRYSTALS_ENEMY_SIDE, "No eggs in second layer, getting crystals on enemy side");
//                    swapState(GameState.GET_CRYSTALS_OWN_SIDE, "No eggs in second layer, getting closest crystals");
//                    return;
//                }
//                break;
//            case GET_CRYSTALS_ENEMY_SIDE:
//                break;
//            case GET_CRYSTALS_OWN_SIDE:
//                actions.addAll(globals.crystals.values().stream()
//                  .filter(cell -> cell.resources > 0)
//                  .sorted(Comparator.comparingInt(cell -> globals.distanceToCellMap.get(globals.myBases[0]).get(cell.index)))
//                  .limit(2)
////                  .map(cell -> Arrays.asList(new Line(globals.myBases[0], cell.index, 1), new Beacon(cell.index, 2)))
////                  .flatMap(Collection::stream)
//                  .map(egg -> new Line(globals.myBases[0], egg.index, 1))
//                  .collect(Collectors.toList()));
//                break;
//        }
//
//        System.out.println(actions.stream().map(Action::execute).collect(Collectors.joining(";")));
//    }
}

class Beacon extends Action {
    int index;

    public Beacon(int index, int strength) {
        this.index = index;
        this.strength = strength;
    }

    @Override
    public String execute() {
        return String.format("BEACON %d %d", index, strength);
    }
}

class Line extends Action {
    int index1, index2;

    public Line(int index1, int index2, int strength) {
        this.index1 = index1;
        this.index2 = index2;
        this.strength = strength;
    }

    @Override
    public String execute() {
        return String.format("LINE %d %d %d", index1, index2, strength);
    }
}

abstract class Action {
    int strength;

    abstract String execute();
}
class Cell {
    CellType type;
    int index, resources, myAnts, oppAnts;

    public Cell(int index, CellType type, int resources, int myAnts, int oppAnts) {
        this.index = index; this.type = type; this.resources = resources; this.myAnts = myAnts; this.oppAnts = oppAnts;
    }

    @Override
    public String toString() {
        return String.format("Cell{index=%d, type=%s, resources=%d, myAnts=%d, oppAnts=%d}", index, type, resources, myAnts, oppAnts);
    }
}
class Globals {
    int[] myBases, oppBases;
    int nbrCells, totalEggs, totalCrystals;
    Map<Integer, Map<Direction, Integer>> neighborMap;
    Map<Integer, Cell> initCellsMap, eggs, crystals, eggsAndCrystals;
    Map<Integer, Map<Integer, Integer>> distanceToCellMap;

    public Globals(Scanner in) {
        this.neighborMap = new HashMap<>();
        this.initCellsMap = new HashMap<>();
        this.distanceToCellMap = new HashMap<>();
        this.eggs = new HashMap<>();
        this.crystals = new HashMap<>();
        this.eggsAndCrystals = new HashMap<>();
        this.totalEggs = 0;
        this.totalCrystals = 0;

        this.nbrCells = in.nextInt(); // amount of hexagonal cells in this map
        for (int i = 0; i < nbrCells; i++) {
            CellType type = CellType.fromInt(in.nextInt()); // 0 for empty, 1 for eggs, 2 for crystal
            int initialResources = in.nextInt(); // the initial amount of eggs/crystals on this cell
            Cell cell = new Cell(i, type, initialResources, 0, 0);
            initCellsMap.put(i, cell);
            Map<Direction, Integer> neighbors = new HashMap<Direction, Integer>() {{
                put(Direction.RIGHT, in.nextInt());
                put(Direction.TOP_RIGHT, in.nextInt());
                put(Direction.TOP_LEFT, in.nextInt());
                put(Direction.LEFT, in.nextInt());
                put(Direction.BOTTOM_LEFT, in.nextInt());
                put(Direction.BOTTOM_RIGHT, in.nextInt());
            }};
            neighborMap.put(i, neighbors);

            switch (type) {
                case EGG:
                    totalEggs += initialResources;
                    break;
                case CRYSTAL:
                    totalCrystals += initialResources;
                    break;
            }

            Map<Integer, Integer> tempMap = new HashMap<>();
            for (int j = 0; j < nbrCells; j++) {
                tempMap.put(j, -1);
            }
            distanceToCellMap.put(i, tempMap);
        }
        int numberOfBases = in.nextInt();
        this.myBases = IntStream.range(0, numberOfBases)
          .map(i -> in.nextInt())
          .toArray();
        this.oppBases = IntStream.range(0, numberOfBases)
          .map(i -> in.nextInt())
          .toArray();

        for (int i = 0; i < nbrCells; i++) {
            setCellDistances(i);
        }
    }

    public Cell getUpdatedCell(int index, Scanner in) {
        return new Cell(index, initCellsMap.get(index).type, in.nextInt(), in.nextInt(), in.nextInt());
    }

    private void setCellDistances(int startIndex) {
        boolean[] visited = new boolean[nbrCells];
        visited[startIndex] = true;
        Queue<PathState> queue = new ArrayDeque<>();
        queue.add(new PathState(startIndex, 0));

        while (!queue.isEmpty()) {
            PathState current = queue.poll();
            visited[current.index] = true;
            int newDistance = current.distance + 1;

            for (int neighbor : neighborMap.get(current.index).values()) {
                if (neighbor == -1) continue;

                if (distanceToCellMap.get(startIndex).get(neighbor) == -1 || newDistance < distanceToCellMap.get(startIndex).get(neighbor)) {
                    distanceToCellMap.get(startIndex).put(neighbor, newDistance);
                }

                if (!visited[neighbor]) {
                    queue.add(new PathState(neighbor, newDistance));
                    visited[neighbor] = true;
                }
            }
        }
    }

    public void printDistanceMap() {
        for (int i = 0; i < nbrCells; i++) {
            System.err.println("To cell " + i + ": " + distanceToCellMap.get(myBases[0]).get(i));
        }
    }
}
enum Direction {
    RIGHT, TOP_RIGHT, TOP_LEFT, LEFT, BOTTOM_LEFT, BOTTOM_RIGHT
}
enum CellType {
    NO_RESOURCES, EGG, CRYSTAL;

    public static CellType fromInt(int i) {
        return CellType.values()[i];
    }
}

class PathState {
    int index, distance;

    public PathState(int index, int distance) {
        this.index = index;
        this.distance = distance;
    }
}
