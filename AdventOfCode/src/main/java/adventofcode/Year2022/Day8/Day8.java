package adventofcode.Year2022.Day8;

import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Day8 {

    private static final String INPUT_NAME = "Year2022/Day8/input.txt";

    private static final int HEIGHEST_VALUE = 9;

    public static void main(String[] args) throws IOException {
        Day8 day = new Day8();
        day.part1();
        day.part2();
    }

    private void part1() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        List<String> lines = new ArrayList<>();
        while (br.ready()) {
            lines.add(br.readLine());
        }

        Set<String> visibleTrees = new HashSet<>();

        // Visible left and right
        for (int i = 0; i < lines.size(); i++) {
            addHighestLeft(lines, i, visibleTrees);
            addHighestRight(lines, i, visibleTrees);
        }
        // Visible top and bot
        for (int j = 0; j < lines.get(0).length(); j++) {
            addHighestTop(lines, j, visibleTrees);
            addHighestBot(lines, j, visibleTrees);
        }

        System.out.println("-------- Day 8 - part 1 --------");
        System.out.println("Total visible trees: " + visibleTrees.size());
        System.out.println("Expected total visible trees: 1700");
    }

    private void addHighestLeft(List<String> lines, int i, Set<String> visibleTrees) {
        // Find highest tree
        int biggestHeight = -1;
        int biggestIndex = -1;
        for (int j = 0; j < lines.get(i).length(); j++) {
            int height = treeHeight(lines, i, j);
            if (height > biggestHeight) {
                biggestHeight = height;
                biggestIndex = j;
            }

            if (height == HEIGHEST_VALUE) {
                break;
            }
        }

        visibleTrees.add("" + i + ":" + biggestIndex);

        for (int currentHeight = biggestHeight-1; currentHeight >= 0; currentHeight--) {
            for (int j = 0; j < biggestIndex; j++) {
                int height = treeHeight(lines, i, j);
                if (height == currentHeight) {
                    biggestIndex = j;
                    visibleTrees.add("" + i + ":" + j);
                    break;
                }
            }
        }
    }

    private void addHighestRight(List<String> lines, int i, Set<String> visibleTrees) {
        // Find highest tree
        int biggestHeight = -1;
        int biggestIndex = -1;
        for (int j = lines.get(i).length() - 1; j >= 0; j--) {
            int height = treeHeight(lines, i, j);
            if (height > biggestHeight) {
                biggestHeight = height;
                biggestIndex = j;
            }

            if (height == HEIGHEST_VALUE) {
                break;
            }
        }

        visibleTrees.add("" + i + ":" + biggestIndex);

        for (int currentHeight = biggestHeight-1; currentHeight >= 0; currentHeight--) {
            for (int j = lines.get(i).length() - 1; j > biggestIndex; j--) {
                int height = treeHeight(lines, i, j);
                if (height == currentHeight) {
                    biggestIndex = j;
                    visibleTrees.add("" + i + ":" + j);
                    break;
                }
            }
        }
    }

    private void addHighestTop(List<String> lines, int j, Set<String> visibleTrees) {
        // Find highest tree
        int biggestHeight = -1;
        int biggestIndex = -1;
        for (int i = 0; i < lines.size(); i++) {
            int height = treeHeight(lines, i, j);
            if (height > biggestHeight) {
                biggestHeight = height;
                biggestIndex = i;
            }

            if (height == HEIGHEST_VALUE) {
                break;
            }
        }

        visibleTrees.add("" + biggestIndex + ":" + j);

        for (int currentHeight = biggestHeight-1; currentHeight >= 0; currentHeight--) {
            for (int i = 0; i < biggestIndex; i++) {
                int height = treeHeight(lines, i, j);
                if (height == currentHeight) {
                    biggestIndex = i;
                    visibleTrees.add("" + i + ":" + j);
                    break;
                }
            }
        }
    }

    private void addHighestBot(List<String> lines, int j, Set<String> visibleTrees) {
        // Find highest tree
        int biggestHeight = -1;
        int biggestIndex = -1;
        for (int i = lines.size() - 1; i >= 0; i--) {
            int height = treeHeight(lines, i, j);
            if (height > biggestHeight) {
                biggestHeight = height;
                biggestIndex = i;
            }

            if (height == HEIGHEST_VALUE) {
                break;
            }
        }

        visibleTrees.add("" + biggestIndex + ":" + j);

        for (int currentHeight = biggestHeight-1; currentHeight >= 0; currentHeight--) {
            for (int i = lines.size() - 1; i > biggestIndex; i--) {
                int height = treeHeight(lines, i, j);
                if (height == currentHeight) {
                    biggestIndex = i;
                    visibleTrees.add("" + i + ":" + j);
                    break;
                }
            }
        }
    }

    private void part2() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        List<String> lines = new ArrayList<>();
        while (br.ready()) {
            lines.add(br.readLine());
        }

        int bestScore = -1;

        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(0).length(); j++) {
                int score = getScenicScore(lines, i, j);
                if (score > bestScore) {
                    bestScore = score;
                }
            }
        }

        System.out.println("-------- Day 8 - part 2 --------");
        System.out.println("Best scenic score: " + bestScore);
        System.out.println("Expected best scenic score: 470596");
    }

    private int getScenicScore(List<String> lines, int i, int j) {
        int height = treeHeight(lines, i, j);

        int topTrees = 0;
        int rightTrees = 0;
        int botTrees = 0;
        int leftTrees = 0;
        // Top
        for (int k = i - 1; k >= 0; k--) {
            topTrees++;
            if (treeHeight(lines, k, j) >= height) {
                break;
            }
        }
        // Bot
        for (int k = i + 1; k < lines.size(); k++) {
            botTrees++;
            if (treeHeight(lines, k, j) >= height) {
                break;
            }
        }
        // Right
        for (int k = j + 1; k < lines.get(0).length(); k++) {
            rightTrees++;
            if (treeHeight(lines, i, k) >= height) {
                break;
            }
        }
        // Left
        for (int k = j - 1; k >= 0; k--) {
            leftTrees++;
            if (treeHeight(lines, i, k) >= height) {
                break;
            }
        }

        return topTrees * rightTrees * botTrees * leftTrees;
    }

    private int treeHeight(List<String> lines, int i, int j) {
        return Integer.parseInt("" + lines.get(i).charAt(j));
    }
}
