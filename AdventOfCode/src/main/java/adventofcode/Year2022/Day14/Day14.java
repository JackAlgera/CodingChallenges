package adventofcode.Year2022.Day14;

import adventofcode.utils.Pair;
import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day14 {

    private static final String INPUT_NAME = "Year2022/Day14/input.txt";

    public static void main(String[] args) throws IOException {
        Day14 day = new Day14();
        day.part1();
        day.part2();
    }

    private void part1() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

//        Starts at x : 300 -> 700
//                  y : -2  -> 200
        Grid grid = new Grid(200, 400, -2, 300);

        while (br.ready()) {
            grid.addLine(br.readLine());
        }

        boolean doneAddingSand = false;

        while (!doneAddingSand) {
            doneAddingSand = grid.addSand();
        }

        System.out.println("\n-------- Day 14 - Part 1 --------");
        System.out.println("Total sand: " + grid.getTotalSand());
        System.out.println("Expected total sand: 793");
    }

    private void part2() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

//        Starts at x : 300 -> 700
//                  y : -2  -> 200
        Grid grid = new Grid(200, 400, -2, 300);

        while (br.ready()) {
            grid.addLine(br.readLine());
        }
        grid.addFloor();

        boolean doneAddingSand = false;

        while (!doneAddingSand) {
            doneAddingSand = grid.addSand();
        }

//        grid.printGrid();
        System.out.println("\n-------- Day 14 - Part 2 --------");
        System.out.println("Total sand: " + grid.getTotalSand());
        System.out.println("Expected total sand: 24166");
    }

    public class Grid {
        List<List<String>> grid;
        private int lowestPoint = 0;
        private int height, width, heightOffset, widthOffset;

        public Grid(int height, int width, int heightOffset, int widthOffset) {
            this.height = height;
            this.width = width;
            this.widthOffset = widthOffset;
            this.heightOffset = heightOffset;

            this.grid = new ArrayList<>();
            for (int x = 0; x < this.width; x++) {
                List<String> line = new ArrayList<>();
                for (int y = 0; y < this.height; y++) {
                    line.add(".");
                }
                this.grid.add(line);
            }
        }

        public boolean addSand() {
            boolean doneFalling = false;
            Pair<Integer> sandPos = new Pair<>(500, 0);

            try {
                while (!doneFalling) {
                    int posX = sandPos.getFirst();
                    int posY = sandPos.getSecond();

                    if (!isInGrid(posX, posY) || !getValueAt(posX, posY).equals(".")) {
                        return true;
                    }

                    if (getValueAt(posX, posY + 1).equals(".")) {
                        sandPos.setSecond(posY + 1);
                        continue;
                    }

                    if (getValueAt(posX - 1, posY + 1).equals(".")) {
                        sandPos.setFirst(posX - 1);
                        sandPos.setSecond(posY + 1);
                        continue;
                    }

                    if (getValueAt(posX + 1, posY + 1).equals(".")) {
                        sandPos.setFirst(posX + 1);
                        sandPos.setSecond(posY + 1);
                        continue;
                    }

                    doneFalling = true;
                }
            } catch (IndexOutOfBoundsException e) {
                System.out.println();
            }

            setValue(sandPos.getFirst(), sandPos.getSecond(), "o");
            return false;
        }

        public void addFloor() {
            for (int x = widthOffset; x < widthOffset + width; x++) {
                setValue(x, lowestPoint + 2, "#");
            }
        }

        public void addLine(String input) {
            List<Pair<Integer>> points = extractPoints(input);

            if (points.size() == 0) {
                return;
            }

            for (int i = 1; i < points.size(); i++) {
                Pair<Integer> start = points.get(i - 1);
                Pair<Integer> end = points.get(i);

                if (start.getSecond() > lowestPoint) {
                    lowestPoint = start.getSecond();
                }
                if (end.getSecond() > lowestPoint) {
                    lowestPoint = end.getSecond();
                }

                if (start.getFirst().equals(end.getFirst())) {
                    // x's are equal, so vertical line
                    int delta = end.getSecond() - start.getSecond();
                    int direction = delta > 0 ? 1 : -1;
                    for (int k = 0; k <= Math.abs(delta); k++) {
                        setValue(start.getFirst(), start.getSecond() + k * direction, "#");
                    }
                } else {
                    // y's are equal, so horizontal line
                    int delta = end.getFirst() - start.getFirst();
                    int direction = delta > 0 ? 1 : -1;
                    for (int k = 0; k <= Math.abs(delta); k++) {
                        setValue(start.getFirst() + k * direction, start.getSecond(), "#");
                    }
                }
            }
        }

        public boolean isInGrid(int x, int y) {
            return x >= widthOffset && x <= widthOffset + width && y >= heightOffset && y < heightOffset + height - 1;
        }

        public void setValue(int x, int y, String val) {
            grid.get(x - widthOffset).set(y - heightOffset, val);
        }

        public List<Pair<Integer>> extractPoints(String input) {
            return Arrays.stream(input.split(" -> "))
                    .map(str -> new Pair<>(
                        Integer.parseInt(str.split(",")[0]),
                        Integer.parseInt(str.split(",")[1])
                    )).collect(Collectors.toList());
        }

        public String getValueAt(int x, int y) {
            return grid.get(x - widthOffset).get(y - heightOffset);
        }

        public void printGrid() {
            for (int y = 0; y < grid.get(0).size(); y++) {
                for (int x = 0; x < grid.size(); x++) {
                    System.out.print(getValueAt(x + widthOffset, y + heightOffset));
                }
                System.out.println();
            }
        }

        public int getTotalSand() {
            int totalSand = 0;

            for (int x = 0; x < grid.size(); x++) {
                for (int y = 0; y < grid.get(0).size(); y++) {
                    if (getValueAt(x + widthOffset, y + heightOffset).equals("o")) {
                        totalSand++;
                    }
                }
            }

            return totalSand;
        }
    }
}
