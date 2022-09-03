package Year2021.Day22;

import Year2021.utils.Pair;
import Year2021.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day22 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day22/input.txt";
    private static final int GRID_SIZE = 50;

    public static void main(String[] args) throws IOException {
        System.out.println("-------- Day 22 --------");

        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        ArrayList<Step> steps = new ArrayList<>();

        while (br.ready()) {
            steps.add(new Step(br.readLine()));
        }

        Map<Pair<Integer>, TreeSet<Integer>> rowsMap = new HashMap<>();

        for (Step step : steps) {
            System.out.println("Checking step " + step);
            for (int x = step.xMin; x <= step.xMax; x++) {
                for (int y = step.yMin; y <= step.yMax; y++) {
                    Pair<Integer> rowIndex = new Pair<>(x, y);

                    if (!rowsMap.containsKey(rowIndex)) {
                        rowsMap.put(rowIndex, new TreeSet<>());
                    }

                    for (int z = step.zMin; z <= step.zMax; z++) {
                        if (step.turnOn) {
                            rowsMap.get(rowIndex).add(z);
                        } else {
                            rowsMap.get(rowIndex).remove(z);
                        }
                    }
                }
            }
        }

        System.out.println("Total rows : " + rowsMap.values().size());

        int totCubesOn = 0;
        for (TreeSet row : rowsMap.values()) {
            totCubesOn += row.size();
        }

        System.out.println("Total cubes turned on : " + totCubesOn);
    }

    private static class Grid {
        private int size;
        private boolean[][][] area;

        public Grid(int size) {
            this.size = size;
            this.area = new boolean[size][size][size];
        }

        public void runStep(Step step) {
//            if (step.xMin > 50 || step.xMax < -50 || step.yMin > 50 || step.yMax < -50 || step.zMin > 50 || step.zMax < -50) {
//                System.out.println("-------- Step outside bounds : " + step);
//                return;
//            }

            for (int x = step.xMin; x <= step.xMax; x++) {
                for (int y = step.yMin; y <= step.yMax; y++) {
                    for (int z = step.zMin; z <= step.zMax; z++) {
                        area[x + size][y + size][z + size] = step.turnOn;
                    }
                }
            }
            System.out.println(nbrOfCubesOn() + " : " + step);
        }

        public void changeValue(int x, int y, int z, boolean turnOn) {
            area[x][y][z] = turnOn;
        }

        public int nbrOfCubesOn() {
            int tot = 0;
            for (int x = -size; x <= size; x++) {
                for (int y = -size; y <= size; y++) {
                    for (int z = -size; z <= size; z++) {
                        if (area[x + size][y + size][z + size]) {
                            tot++;
                        }
                    }
                }
            }
            return tot;
        }
    }

    private static class Step {
        private boolean turnOn;
        private int xMin, xMax;
        private int yMin, zMax;
        private int zMin, yMax;

        public Step(String input) {
            this.turnOn = Objects.equals(input.split(" ")[0], "on");
            this.xMin = getValue("x=([-0-9]+)", input);
            this.xMax = getValue("x=[-0-9]*..([-0-9]+)", input);
            this.yMin = getValue("y=([-0-9]+)", input);
            this.yMax = getValue("y=[-0-9]*..([-0-9]+)", input);
            this.zMin = getValue("z=([-0-9]+)", input);
            this.zMax = getValue("z=[-0-9]*..([-0-9]+)", input);
        }

        @Override
        public String toString() {
            return (turnOn ? "on " : "off ") + "x=" + xMin + ".." + xMax + ",y=" + yMin + ".." + yMax + ",z=" + zMin + ".." + zMax;
        }
    }

    private static int getValue(String regex, String input) {
        Matcher m = Pattern.compile(regex).matcher(input);
        m.find();
        return Integer.parseInt(m.group(1));
    }
}
