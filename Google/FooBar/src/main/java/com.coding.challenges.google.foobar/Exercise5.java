package com.coding.challenges.google.foobar;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Exercise5 {
    public static void main(String[] args) {
        System.out.println(getSolarDoomsDaySolution(new boolean[][]{
                new boolean[] {true, false, true},
                new boolean[] {false, true, false},
                new boolean[] {true, false, true}
        }));
        System.out.println(getSolarDoomsDaySolution(new boolean[][]{
                new boolean[] {true, false, true, false, false, true, true, true},
                new boolean[] {true, false, true, false, false, false, true, false},
                new boolean[] {true, true, true, false, false, false, true, false},
                new boolean[] {true, false, true, false, false, false, true, false},
                new boolean[] {true, false, true, false, false, true, true, true}
        }));
        System.out.println(getSolarDoomsDaySolution(new boolean[][]{
                new boolean[] {true, true, false, true, false, true, false, true, true, false},
                new boolean[] {true, true, false, false, false, false, true, true, true, false},
                new boolean[] {true, true, false, false, false, false, false, false, false, true},
                new boolean[] {false, true, false, false, false, false, true, true, false, false}
        }));
    }

    public static final Map<Pair, Integer> SQUARE_PAIR_TO_VAL = new HashMap<Pair, Integer>() {{
        put(new Pair(0, 0), 0); put(new Pair(0, 1), 1); put(new Pair(0, 2), 1); put(new Pair(0, 3), 0);
        put(new Pair(1, 0), 1); put(new Pair(1, 1), 0); put(new Pair(1, 2), 0); put(new Pair(1, 3), 0);
        put(new Pair(2, 0), 1); put(new Pair(2, 1), 0); put(new Pair(2, 2), 0); put(new Pair(2, 3), 0);
        put(new Pair(3, 0), 0); put(new Pair(3, 1), 0); put(new Pair(3, 2), 0);put(new Pair(3, 3), 0);
    }};

    public static final Map<Integer, List<List<Integer>>> POSSIBLE_INITIAL_SOLUTIONS = new HashMap<Integer, List<List<Integer>>>(2) {{
        put(0, Arrays.asList(
                Arrays.asList(2, 1),
                Arrays.asList(1, 2),
                Arrays.asList(3, 0),
                Arrays.asList(0, 3),
                Arrays.asList(2, 2),
                Arrays.asList(1, 1),
                Arrays.asList(3, 2),
                Arrays.asList(2, 3),
                Arrays.asList(1, 3),
                Arrays.asList(3, 1),
                Arrays.asList(3, 3),
                Arrays.asList(0, 0)));
        put(1, Arrays.asList(
                Arrays.asList(2, 0),
                Arrays.asList(1, 0),
                Arrays.asList(0, 1),
                Arrays.asList(0, 2)));
    }};

    public static int getSolarDoomsDaySolution(boolean[][] g) {
        int[][] grid = swapArray(g);
        List<Pair> possibleColumnSolutions = determineColumnSolutions(grid[0]);

        Map<Integer, Integer> preSolutionCount = new HashMap<>();
        for (Pair pair : possibleColumnSolutions) {
            preSolutionCount.put(pair.a, 1);
        }

        for (int[] col : grid) {
            possibleColumnSolutions = determineColumnSolutions(col);
            Map<Integer, Integer> solutionCount = new HashMap<>();
            for (Pair pair : possibleColumnSolutions) {
                if (!preSolutionCount.containsKey(pair.a)) {
                    preSolutionCount.put(pair.a, 0);
                }
                if (!solutionCount.containsKey(pair.b)) {
                    solutionCount.put(pair.b, 0);
                }
                solutionCount.put(pair.b, solutionCount.get(pair.b) + preSolutionCount.get(pair.a));
            }
            preSolutionCount = solutionCount;
        }

        return preSolutionCount.values().stream().mapToInt(Integer::intValue).sum();
    }

    /**
     * We get a list of double columns (converted to a pair of binary numbers) that can generate the current column
     *
     * @param col
     * @return
     */
    public static List<Pair> determineColumnSolutions(int[] col) {
        List<Integer> possibleRows = Arrays.asList(0, 1, 2, 3);
        List<List<Integer>> currentPossibleColumns = POSSIBLE_INITIAL_SOLUTIONS.get(col[0]);

        for (int i = 1; i < col.length; i++) {
            List<List<Integer>> temp = new ArrayList<>();
            for (List<Integer> tes : currentPossibleColumns) {
                for (Integer comb : possibleRows) {
                    if (SQUARE_PAIR_TO_VAL.get(new Pair(tes.get(i), comb)) == col[i]) {
                        temp.add(copyWithVal(tes, comb));
                    }
                }
            }
            currentPossibleColumns = temp;
        }
        List<Pair> listPossibleColumns = new ArrayList<>();
        for (List<Integer> column : currentPossibleColumns) {
            listPossibleColumns.add(getBinaryNumber(column));
        }
        return listPossibleColumns;
    }

    /**
     * Converts a double column into a pair of binary numbers
     *
     * @param col
     * @return
     */
    public static Pair getBinaryNumber(List<Integer> col) {
        int a = 0, b = 0;

        for (int i = 0; i < col.size(); i++) {
            switch (col.get(i)) {
                case 1:
                    b += (int) Math.pow(2, i);
                    break;
                case 2:
                    a += (int) Math.pow(2, i);
                    break;
                case 3:
                    a += (int) Math.pow(2, i);
                    b += (int) Math.pow(2, i);
                    break;
            }
        }

        return new Pair(a, b);
    }

    public static List<Integer> copyWithVal(List<Integer> list, Integer val) {
            List<Integer> copy = new ArrayList<>(list);
            copy.add(val);
            return copy;
    }

    /**
     * To increase performance, I'm inverting rows and columns (int[height][width] -> int[width][height]).
     * As the 2D array (int[][]) is just an array of arrays, doing this means we store columns inside each unique
     * array instead of rows
     *
     * @param g
     * @return
     */
    public static int[][] swapArray(boolean[][] g) {
        int height = g.length;
        int width = g[0].length;

        int[][] grid = new int[width][height];
        for (int row = 0; row < height; row++) {
            for (int col = 0; col < width; col++) {
                grid[col][row] = g[row][col] ? 1 : 0;
            }
        }

        return grid;
    }

    public static class Pair {
        int a, b;

        public Pair(int a, int b) {
            this.a = a;
            this.b = b;
        }

        @Override
        public String toString() {
            return "(" + a + "," + b + ")";
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Pair pair = (Pair) o;
            return a == pair.a && b == pair.b;
        }

        @Override
        public int hashCode() {
            return Objects.hash(a, b);
        }
    }
}
