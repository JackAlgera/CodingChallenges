package Year2022.Day22;

import utils.Day;
import utils.Utilities;
import utils.enums.Direction;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class Day22 extends Day<Integer> {

    // min included, max excluded
    private static final List<Area> SAMPLE_AREAS = List.of(
        new Area(0, 0, 4, 8, 12),
        new Area(1, 4, 8, 0, 4),
        new Area(2, 4, 8, 4, 8),
        new Area(3, 4, 8, 8, 12),
        new Area(4, 8, 12, 8, 12),
        new Area(5, 8, 12, 12, 16)
    );

    // min included, max excluded
    private static final List<Area> INPUT_AREAS = List.of(
        new Area(0, 0, 50, 50, 100),
        new Area(1, 0, 50, 100, 150),
        new Area(2, 50, 100, 50, 100),
        new Area(3, 100, 150, 0, 50),
        new Area(4, 100, 150, 50, 100),
        new Area(5, 150, 200, 0, 50)
    );

    private static boolean isSample = true;

    public static void main(String[] args) throws Exception {
        Day22 day = new Day22();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        day.printAllResults(1, day.getName(),
            day.part1(sampleInput), 6032,
            day.part1(mainInput), 191010);

        day.printAllResults(2, day.getName(),
            day.part2(sampleInput), 5031,
//            1, 960);
            day.part2(mainInput), 960);
    }

    @Override
    public Integer part1(List<String> lines) throws IOException {
        TileType[][] grid = extractGrid(lines);
        List<String> actions = extractActions(lines);
        Me me = new Me(0, findFirstOpenTile(lines.get(0)), Direction.EAST);

        for (int k = 0; k < actions.size(); k++) {
            String action = actions.get(k);
            if (k % 2 == 0) { // Move forward
                int distance = Integer.parseInt(action);
                while (distance > 0) {
                    distance--;
                    me = isSample ? moveWrapAroundSample(me, grid) : moveWrapAroundInput(me, grid);
                    grid[me.i()][me.j()] = TileType.values()[me.direction().ordinal()];
                }
            } else { // Rotate
                me = me.rotate(action.charAt(0));
                grid[me.i()][me.j()] = TileType.values()[me.direction().ordinal()];
            }
//            printGrid(grid);
        }

        isSample = !isSample;
        return me.getPassword();
    }

    @Override
    public Integer part2(List<String> lines) throws IOException {
        TileType[][] grid = extractGrid(lines);
        List<String> actions = extractActions(lines);
        Me me = new Me(0, findFirstOpenTile(lines.get(0)), Direction.EAST);

        for (int k = 0; k < actions.size(); k++) {
            String action = actions.get(k);
            if (k % 2 == 0) { // Move forward
                int distance = Integer.parseInt(action);
                while (distance > 0) {
                    distance--;
                    me = isSample ? moveCubeSample(me, grid) : moveCubeInput(me, grid);
                    grid[me.i()][me.j()] = TileType.values()[me.direction().ordinal()];
                }
            } else { // Rotate
                me = me.rotate(action.charAt(0));
                grid[me.i()][me.j()] = TileType.values()[me.direction().ordinal()];
            }
//            printGrid(grid);
        }

        isSample = !isSample;
        return me.getPassword();
    }

    public Me moveWrapAroundInput(Me me, TileType[][] grid) {
        int newI = me.i();
        int newJ = me.j();

        int areaId = INPUT_AREAS.stream().filter(a -> a.isInArea(me.i(), me.j())).findFirst().get().id();
        Me potentialMe = me.move();
        if (INPUT_AREAS.get(areaId).isInArea(potentialMe.i(), potentialMe.j())) {
            if (grid[potentialMe.i()][potentialMe.j()] == TileType.WALL) {
                return me;
            } else {
                return potentialMe;
            }
        }

        switch (areaId) {
            case 0:
                switch (me.direction()) {
                    case NORTH -> newI = INPUT_AREAS.get(4).iMax() - 1;
                    case SOUTH -> newI = INPUT_AREAS.get(2).iMin();
                    case EAST -> newJ = INPUT_AREAS.get(1).jMin();
                    case WEST -> newJ = INPUT_AREAS.get(1).jMax() - 1;
                }
                break;
            case 1:
                switch (me.direction()) {
                    case NORTH -> newI = INPUT_AREAS.get(1).iMax() - 1;
                    case SOUTH -> newI = INPUT_AREAS.get(1).iMin();
                    case EAST -> newJ = INPUT_AREAS.get(0).jMin();
                    case WEST -> newJ = INPUT_AREAS.get(0).jMax() - 1;
                }
                break;
            case 2:
                switch (me.direction()) {
                    case NORTH -> newI = INPUT_AREAS.get(0).iMax() - 1;
                    case SOUTH -> newI = INPUT_AREAS.get(4).iMin();
                    case EAST -> newJ = INPUT_AREAS.get(2).jMin();
                    case WEST -> newJ = INPUT_AREAS.get(2).jMax() - 1;
                }
                break;
            case 3:
                switch (me.direction()) {
                    case NORTH -> newI = INPUT_AREAS.get(5).iMax() - 1;
                    case SOUTH -> newI = INPUT_AREAS.get(5).iMin();
                    case EAST -> newJ = INPUT_AREAS.get(4).jMin();
                    case WEST -> newJ = INPUT_AREAS.get(4).jMax() - 1;
                }
                break;
            case 4:
                switch (me.direction()) {
                    case NORTH -> newI = INPUT_AREAS.get(2).iMax() - 1;
                    case SOUTH -> newI = INPUT_AREAS.get(0).iMin();
                    case EAST -> newJ = INPUT_AREAS.get(3).jMin();
                    case WEST -> newJ = INPUT_AREAS.get(3).jMax() - 1;
                }
                break;
            case 5:
                switch (me.direction()) {
                    case NORTH -> newI = INPUT_AREAS.get(3).iMax() - 1;
                    case SOUTH -> newI = INPUT_AREAS.get(3).iMin();
                    case EAST -> newJ = INPUT_AREAS.get(5).jMin();
                    case WEST -> newJ = INPUT_AREAS.get(5).jMax() - 1;
                }
                break;
        }

        if (grid[newI][newJ] == TileType.WALL) {
            return me;
        } else {
            return new Me(
                newI,
                newJ,
                me.direction()
            );
        }
    }

    public Me moveWrapAroundSample(Me me, TileType[][] grid) {
        int newI = me.i();
        int newJ = me.j();

        int areaId = SAMPLE_AREAS.stream().filter(a -> a.isInArea(me.i(), me.j())).findFirst().get().id();
        Me potentialMe = me.move();
        if (SAMPLE_AREAS.get(areaId).isInArea(potentialMe.i(), potentialMe.j())) {
            if (grid[potentialMe.i()][potentialMe.j()] == TileType.WALL) {
                return me;
            } else {
                return potentialMe;
            }
        }

        switch (areaId) {
            case 0:
                switch (me.direction()) {
                    case NORTH -> newI = SAMPLE_AREAS.get(4).iMax() - 1;
                    case SOUTH -> newI = SAMPLE_AREAS.get(3).iMin();
                    case EAST -> newJ = SAMPLE_AREAS.get(0).jMin();
                    case WEST -> newJ = SAMPLE_AREAS.get(0).jMax() - 1;
                }
                break;
            case 1:
                switch (me.direction()) {
                    case NORTH -> newI = SAMPLE_AREAS.get(1).iMax() - 1;
                    case SOUTH -> newI = SAMPLE_AREAS.get(1).iMin();
                    case EAST -> newJ = SAMPLE_AREAS.get(2).jMin();
                    case WEST -> newJ = SAMPLE_AREAS.get(3).jMax() - 1;
                }
                break;
            case 2:
                switch (me.direction()) {
                    case NORTH -> newI = SAMPLE_AREAS.get(2).iMax() - 1;
                    case SOUTH -> newI = SAMPLE_AREAS.get(2).iMin();
                    case EAST -> newJ = SAMPLE_AREAS.get(3).jMin();
                    case WEST -> newJ = SAMPLE_AREAS.get(1).jMax() - 1;
                }
                break;
            case 3:
                switch (me.direction()) {
                    case NORTH -> newI = SAMPLE_AREAS.get(0).iMax() - 1;
                    case SOUTH -> newI = SAMPLE_AREAS.get(4).iMin();
                    case EAST -> newJ = SAMPLE_AREAS.get(1).jMin();
                    case WEST -> newJ = SAMPLE_AREAS.get(2).jMax() - 1;
                }
                break;
            case 4:
                switch (me.direction()) {
                    case NORTH -> newI = SAMPLE_AREAS.get(3).iMax() - 1;
                    case SOUTH -> newI = SAMPLE_AREAS.get(0).iMin();
                    case EAST -> newJ = SAMPLE_AREAS.get(5).jMin();
                    case WEST -> newJ = SAMPLE_AREAS.get(5).jMax() - 1;
                }
                break;
            case 5:
                switch (me.direction()) {
                    case NORTH -> newI = SAMPLE_AREAS.get(5).iMax() - 1;
                    case SOUTH -> newI = SAMPLE_AREAS.get(5).iMin();
                    case EAST -> newJ = SAMPLE_AREAS.get(4).jMin();
                    case WEST -> newJ = SAMPLE_AREAS.get(4).jMax() - 1;
                }
                break;
        }

        if (grid[newI][newJ] == TileType.WALL) {
            return me;
        } else {
            return new Me(
                newI,
                newJ,
                me.direction()
            );
        }
    }

    public Me moveCubeInput(Me me, TileType[][] grid) {
        int newI = me.i();
        int newJ = me.j();

        int areaId = INPUT_AREAS.stream().filter(a -> a.isInArea(me.i(), me.j())).findFirst().get().id();
        Me potentialMe = me.move();
        if (INPUT_AREAS.get(areaId).isInArea(potentialMe.i(), potentialMe.j())) {
            if (grid[potentialMe.i()][potentialMe.j()] == TileType.WALL) {
                return me;
            } else {
                return potentialMe;
            }
        }

        int deltaI = me.i() - INPUT_AREAS.get(areaId).iMin();
        int deltaJ = me.j() - INPUT_AREAS.get(areaId).jMin();
        Direction newDirection = me.direction();

        switch (areaId) {
            case 0:
                switch (me.direction()) {
                    case NORTH -> {
                        newI = INPUT_AREAS.get(5).iMin() + deltaJ;
                        newJ = INPUT_AREAS.get(5).jMin();
                        newDirection = Direction.EAST;
                    }
                    case SOUTH -> newI = INPUT_AREAS.get(2).iMin();
                    case EAST -> newJ = INPUT_AREAS.get(1).jMin();
                    case WEST -> {
                        newI = INPUT_AREAS.get(3).iMax() - 1 - deltaI;
                        newJ = INPUT_AREAS.get(3).jMin();
                        newDirection = Direction.EAST;
                    }
                }
                break;
            case 1:
                switch (me.direction()) {
                    case NORTH -> {
                        newI = INPUT_AREAS.get(5).iMax() - 1;
                        newJ = INPUT_AREAS.get(5).jMin() + deltaJ;
                        newDirection = Direction.NORTH;
                    }
                    case SOUTH -> {
                        newI = INPUT_AREAS.get(2).iMin() + deltaJ;
                        newJ = INPUT_AREAS.get(2).jMax() - 1;
                        newDirection = Direction.WEST;
                    }
                    case EAST -> {
                        newI = INPUT_AREAS.get(4).iMax() - 1 - deltaI;
                        newJ = INPUT_AREAS.get(4).jMax() - 1;
                        newDirection = Direction.WEST;
                    }
                    case WEST -> newJ = INPUT_AREAS.get(0).jMax() - 1;
                }
                break;
            case 2:
                switch (me.direction()) {
                    case NORTH -> newI = INPUT_AREAS.get(0).iMax() - 1;
                    case SOUTH -> newI = INPUT_AREAS.get(4).iMin();
                    case EAST -> {
                        newI = INPUT_AREAS.get(1).iMax() - 1;
                        newJ = INPUT_AREAS.get(1).jMin() + deltaI;
                        newDirection = Direction.NORTH;
                    }
                    case WEST -> {
                        newI = INPUT_AREAS.get(3).iMin();
                        newJ = INPUT_AREAS.get(3).jMin() + deltaI;
                        newDirection = Direction.SOUTH;
                    }
                }
                break;
            case 3:
                switch (me.direction()) {
                    case NORTH -> {
                        newI = INPUT_AREAS.get(2).iMin() + deltaJ;
                        newJ = INPUT_AREAS.get(2).jMin();
                        newDirection = Direction.EAST;
                    }
                    case SOUTH -> newI = INPUT_AREAS.get(5).iMin();
                    case EAST -> newJ = INPUT_AREAS.get(4).jMin();
                    case WEST -> {
                        newI = INPUT_AREAS.get(0).iMax() - 1 - deltaI;
                        newJ = INPUT_AREAS.get(0).jMin();
                        newDirection = Direction.EAST;
                    }
                }
                break;
            case 4:
                switch (me.direction()) {
                    case NORTH -> newI = INPUT_AREAS.get(2).iMax() - 1;
                    case SOUTH -> {
                        newI = INPUT_AREAS.get(5).iMin() + deltaJ;
                        newJ = INPUT_AREAS.get(5).jMax() - 1;
                        newDirection = Direction.WEST;
                    }
                    case EAST -> {
                        newI = INPUT_AREAS.get(1).iMax() - 1 - deltaI;
                        newJ = INPUT_AREAS.get(1).jMax() - 1;
                        newDirection = Direction.WEST;
                    }
                    case WEST -> newJ = INPUT_AREAS.get(3).jMax() - 1;
                }
                break;
            case 5:
                switch (me.direction()) {
                    case NORTH -> newI = INPUT_AREAS.get(3).iMax() - 1;
                    case SOUTH -> {
                        newI = INPUT_AREAS.get(1).iMin();
                        newJ = INPUT_AREAS.get(1).jMin() + deltaJ;
                        newDirection = Direction.SOUTH;
                    }
                    case EAST -> {
                        newI = INPUT_AREAS.get(4).iMax() - 1;
                        newJ = INPUT_AREAS.get(4).jMin() + deltaI;
                        newDirection = Direction.NORTH;
                    }
                    case WEST -> {
                        newI = INPUT_AREAS.get(0).iMin();
                        newJ = INPUT_AREAS.get(0).jMin() + deltaI;
                        newDirection = Direction.SOUTH;
                    }
                }
                break;
        }

        if (newI < 0 || newJ < 0) {
            System.out.println("HERE");
        }

        if (grid[newI][newJ] == TileType.WALL) {
            return me;
        } else {
            return new Me(
                newI,
                newJ,
                newDirection
            );
        }
    }

    public Me moveCubeSample(Me me, TileType[][] grid) {
        int newI = me.i();
        int newJ = me.j();

        int areaId = SAMPLE_AREAS.stream().filter(a -> a.isInArea(me.i(), me.j())).findFirst().get().id();
        Me potentialMe = me.move();
        if (SAMPLE_AREAS.get(areaId).isInArea(potentialMe.i(), potentialMe.j())) {
            if (grid[potentialMe.i()][potentialMe.j()] == TileType.WALL) {
                return me;
            } else {
                return potentialMe;
            }
        }

        int deltaI = me.i() - SAMPLE_AREAS.get(areaId).iMin();
        int deltaJ = me.j() - SAMPLE_AREAS.get(areaId).jMin();
        Direction newDirection = me.direction();

        switch (areaId) {
            case 0:
                switch (me.direction()) {
                    case NORTH -> {
                        newI = SAMPLE_AREAS.get(1).iMin();
                        newJ = SAMPLE_AREAS.get(1).iMax() - 1 - deltaJ;
                        newDirection = Direction.SOUTH;
                    }
                    case SOUTH -> newI = SAMPLE_AREAS.get(3).iMin();
                    case EAST -> {
                        newI = SAMPLE_AREAS.get(5).iMax() - 1 - deltaI;
                        newJ = SAMPLE_AREAS.get(5).jMax() - 1;
                        newDirection = Direction.WEST;
                    }
                    case WEST -> {
                        newI = SAMPLE_AREAS.get(2).iMin();
                        newJ = SAMPLE_AREAS.get(2).jMin() + deltaI;
                        newDirection = Direction.SOUTH;
                    }
                }
                break;
            case 1:
                switch (me.direction()) {
                    case NORTH -> {
                        newI = SAMPLE_AREAS.get(0).iMin();
                        newJ = SAMPLE_AREAS.get(0).iMax() - 1 - deltaJ;
                        newDirection = Direction.SOUTH;
                    }
                    case SOUTH -> {
                        newI = SAMPLE_AREAS.get(4).iMax() - 1;
                        newJ = SAMPLE_AREAS.get(4).iMax() - 1 - deltaJ;
                        newDirection = Direction.NORTH;
                    }
                    case EAST -> newJ = SAMPLE_AREAS.get(2).iMin();
                    case WEST -> {
                        newI = SAMPLE_AREAS.get(5).iMax() - 1;
                        newJ = SAMPLE_AREAS.get(5).jMax() - 1 - deltaI;
                        newDirection = Direction.NORTH;
                    }
                }
                break;
            case 2:
                switch (me.direction()) {
                    case NORTH -> {
                        newI = SAMPLE_AREAS.get(0).iMin() + deltaJ;
                        newJ = SAMPLE_AREAS.get(0).jMin();
                        newDirection = Direction.EAST;
                    }
                    case SOUTH -> {
                        newI = SAMPLE_AREAS.get(4).iMin() + deltaJ;
                        newJ = SAMPLE_AREAS.get(4).jMin();
                        newDirection = Direction.EAST;
                    }
                    case EAST -> newJ = SAMPLE_AREAS.get(3).jMin();
                    case WEST -> newJ = SAMPLE_AREAS.get(1).jMax() - 1;
                }
                break;
            case 3:
                switch (me.direction()) {
                    case NORTH -> newI = SAMPLE_AREAS.get(0).iMax() - 1;
                    case SOUTH -> newI = SAMPLE_AREAS.get(4).iMin();
                    case EAST -> {
                        newI = SAMPLE_AREAS.get(5).iMin();
                        newJ = SAMPLE_AREAS.get(5).jMax() - 1 - deltaI;
                        newDirection = Direction.SOUTH;
                    }
                    case WEST -> newJ = SAMPLE_AREAS.get(2).jMax() - 1;
                }
                break;
            case 4:
                switch (me.direction()) {
                    case NORTH -> newI = SAMPLE_AREAS.get(3).iMax() - 1;
                    case SOUTH -> {
                        newI = SAMPLE_AREAS.get(1).iMax() - 1;
                        newJ = SAMPLE_AREAS.get(1).jMax() - 1 - deltaJ;
                        newDirection = Direction.NORTH;
                    }
                    case EAST -> newJ = SAMPLE_AREAS.get(5).jMin();
                    case WEST -> {
                        newI = SAMPLE_AREAS.get(2).iMax() - 1;
                        newJ = SAMPLE_AREAS.get(2).jMin() + deltaI;
                        newDirection = Direction.NORTH;
                    }
                }
                break;
            case 5:
                switch (me.direction()) {
                    case NORTH -> {
                        newI = SAMPLE_AREAS.get(3).iMax() - 1 - deltaJ;
                        newJ = SAMPLE_AREAS.get(3).jMax() - 1;
                        newDirection = Direction.WEST;
                    }
                    case SOUTH -> {
                        newI = SAMPLE_AREAS.get(1).iMax() - 1 - deltaJ;
                        newJ = SAMPLE_AREAS.get(1).jMin();
                        newDirection = Direction.EAST;
                    }
                    case EAST -> {
                        newI = SAMPLE_AREAS.get(0).iMax() - 1 - deltaI;
                        newJ = SAMPLE_AREAS.get(0).iMax() - 1;
                        newDirection = Direction.WEST;
                    }
                    case WEST -> newJ = SAMPLE_AREAS.get(4).jMax() - 1;
                }
                break;
        }

        if (grid[newI][newJ] == TileType.WALL) {
            return me;
        } else {
            return new Me(
                newI,
                newJ,
                newDirection
            );
        }
    }

    public List<String> extractActions(List<String> lines) {
        List<String> actions = new ArrayList<>();
        Matcher matcher = Pattern
            .compile("(\\d+|[A-Z])")
            .matcher(lines.get(lines.size() - 1));
        while (matcher.find()) {
            actions.add(matcher.group(0));
        }
        return actions;
    }

    public TileType[][] extractGrid(List<String> lines) {
        int height = lines.size() - 2;
        int width = lines.stream().limit(height).mapToInt(String::length).max().getAsInt();
        TileType[][] grid = new TileType[height][width];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                char tile = j >= lines.get(i).length() ? ' ' : lines.get(i).charAt(j);
                grid[i][j] = switch (tile) {
                    case '.' -> TileType.PATH;
                    case '#' -> TileType.WALL;
                    case ' ' -> TileType.EMPTY;
                    default -> throw new IllegalStateException("Unexpected value: " + tile);
                };
            }
        }
        return grid;
    }

    public int findFirstOpenTile(String line) {
        for (int j = 0; j < line.length(); j++) {
            if (line.charAt(j) != ' ') {
                return j;
            }
        }
        return -1;
    }

    public record Me(int i, int j, Direction direction) {
        public Me rotate(char r) {
            return new Me(
                i,
                j,
                Direction.values()[Math.floorMod(direction.ordinal() + (r == 'R' ? 1 : -1), Direction.values().length)]
            );
        }

        public Me move() {
            int newI = i;
            int newJ = j;
            switch (direction) {
                case NORTH -> newI--;
                case SOUTH -> newI++;
                case WEST -> newJ--;
                case EAST -> newJ++;
            }
            return new Me(
                newI,
                newJ,
                direction
            );
        }

        public int getPassword() {
            int password = switch (direction) {
                case NORTH -> 3;
                case EAST -> 0;
                case SOUTH -> 1;
                case WEST -> 2;
            };

            return password + 1000 * (i + 1) + 4 * (j + 1);
        }
    }

    public void printGrid(TileType[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                System.out.print(
                    switch (grid[i][j]) {
                        case EMPTY  -> " ";
                        case PATH   -> ".";
                        case WALL   -> "#";
                        case NORTH  -> "^";
                        case EAST   -> ">";
                        case SOUTH  -> "v";
                        case WEST   -> "<";
                    }
                );
            }
            System.out.println();
        }
        System.out.println();
    }

    public enum TileType {
        NORTH, EAST, SOUTH, WEST, PATH, WALL, EMPTY
    }

    public record Area(int id, int iMin, int iMax, int jMin, int jMax) {
        public boolean isInArea(int i, int j) {
            return i >= iMin && i < iMax && j >= jMin && j < jMax;
        }
    }
}
