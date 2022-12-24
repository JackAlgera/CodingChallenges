package Year2022.Day23;

import utils.Day;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day23 extends Day {

    public static void main(String[] args) throws IOException {
        Day23 day = new Day23();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        printAllResults(1, day.getName(),
            day.part1(sampleInput), 110,
            day.part1(mainInput), 4034);

        printAllResults(2, day.getName(),
            day.part2(sampleInput), 20,
            day.part2(mainInput), 960);
    }

    @Override
    public long part1(List<String> lines) throws IOException {
        List<Elf> elves = extractElves(lines);

        List<Direction> directions = new ArrayList<>(List.of(
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
        ));

        for (int k = 0; k < 10; k++) {
            playRound(elves, directions);
            changeDirectionSequence(directions);
        }
        return getEmptyTiles(elves);
    }

    @Override
    public long part2(List<String> lines) throws IOException {
        List<Elf> elves = extractElves(lines);

        List<Direction> directions = new ArrayList<>(List.of(
            Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST
        ));

        int totalRounds = 1;
        while (playRound(elves, directions)) {
            changeDirectionSequence(directions);
            totalRounds++;
        }
        return totalRounds;
    }

    public int getEmptyTiles(List<Elf> elves) {
        int minI = Integer.MAX_VALUE, maxI = Integer.MIN_VALUE;
        int minJ = Integer.MAX_VALUE, maxJ = Integer.MIN_VALUE;

        for (Elf elf : elves) {
            if (elf.getI() < minI) {
                minI = elf.getI();
            }
            if (elf.getI() > maxI) {
                maxI = elf.getI();
            }
            if (elf.getJ() < minJ) {
                minJ = elf.getJ();
            }
            if (elf.getJ() > maxJ) {
                maxJ = elf.getJ();
            }
        }

        return (maxI - minI + 1) * (maxJ - minJ + 1) - elves.size();
    }

    public boolean[][] generateGrid(List<Elf> elves) {
        int maxI = 0, maxJ = 0;

        for (Elf elf : elves) {
            elf.setI(elf.getI() + 1);
            elf.setJ(elf.getJ() + 1);
            if (elf.getI() > maxI) {
                maxI = elf.getI();
            }
            if (elf.getJ() > maxJ) {
                maxJ = elf.getJ();
            }
        }
        boolean[][] grid = new boolean[maxI + 2][maxJ + 2];
        for (Elf elf : elves) {
            grid[elf.getI()][elf.getJ()] = true;
        }

        return grid;
    }

    public void printGrid(boolean[][] grid) {
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < grid[0].length; j++) {
                System.out.print(grid[i][j] ? "#" : ".");
            }
            System.out.println();
        }
        System.out.println("\n");
    }

    public List<Elf> extractElves(List<String> lines) {
        List<Elf> elves = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(0).length(); j++) {
                if (lines.get(i).charAt(j) == '#') {
                    elves.add(new Elf(i, j));
                }
            }
        }
        return elves;
    }

    public void changeDirectionSequence(List<Direction> directions) {
        Direction direction = directions.remove(0);
        directions.add(directions.size(), direction);
    }

    public boolean playRound(List<Elf> elves, List<Direction> directions) {
        List<Move> moves = new ArrayList<>();
        Map<Integer, Map<Integer, Integer>> moveCount = new HashMap<>();
        boolean[][] grid = generateGrid(elves);

        for (Elf elf : elves) {
            if (!elf.hasNeighbor(grid)) {
                continue;
            }

            for (Direction direction : directions) {
                if (elf.canMoveInDirection(direction, grid)) {
                    int newI = elf.getI();
                    int newJ = elf.getJ();
                    switch (direction) {
                        case NORTH  -> newI -= 1;
                        case SOUTH  -> newI += 1;
                        case EAST   -> newJ += 1;
                        case WEST   -> newJ -= 1;
                    }
                    moves.add(new Move(elf, newI, newJ));
                    if (!moveCount.containsKey(newI)) {
                        Map<Integer, Integer> something = new HashMap<>();
                        something.put(newJ, 1);
                        moveCount.put(newI, something);
                    } else {
                        if (!moveCount.get(newI).containsKey(newJ)) {
                            moveCount.get(newI).put(newJ, 1);
                        } else {
                            moveCount.get(newI).put(newJ, moveCount.get(newI).get(newJ) + 1);
                        }
                    }
                    break;
                }
            }
        }

        boolean elfMoved = false;
        for (Move move : moves) {
            if (moveCount.get(move.i()).get(move.j()) <= 1) {
                move.elf().move(move);
                elfMoved = true;
            }
        }
        return elfMoved;
    }

    public record Move(Elf elf, int i, int j) {}

    public class Elf {
        int i, j;

        public Elf(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        public void setI(int i) {
            this.i = i;
        }

        public void setJ(int j) {
            this.j = j;
        }

        public boolean hasNeighbor(boolean[][] grid) {
            for (int di = -1; di < 2; di++) {
                for (int dj = -1; dj < 2; dj++) {
                    if (di == 0 && dj == 0) {
                        continue;
                    }

                    if (grid[i + di][j + dj]) {
                        return true;
                    }
                }
            }

            return false;
        }

        public boolean canMoveInDirection(Direction direction, boolean[][] grid) {
            return switch (direction) {
                case NORTH  -> !grid[i - 1][j - 1] && !grid[i - 1][j] && !grid[i - 1][j + 1];
                case SOUTH  -> !grid[i + 1][j - 1] && !grid[i + 1][j] && !grid[i + 1][j + 1];
                case EAST   -> !grid[i - 1][j + 1] && !grid[i][j + 1] && !grid[i + 1][j + 1];
                case WEST   -> !grid[i - 1][j - 1] && !grid[i][j - 1] && !grid[i + 1][j - 1];
            };
        }

        public void move(Move move) {
            this.i = move.i();
            this.j = move.j();
        }
    }

    public enum Direction {
        NORTH, EAST, SOUTH, WEST
    }
}
