package com.coding.challenges.adventofcode.year2023.Day10;

import com.coding.challenges.adventofcode.utils.Day;
import lombok.With;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public class Day10 extends Day<Integer> {

    public final List<String> NORTH_PIPES = List.of("|", "L", "J");
    public final List<String> EAST_PIPES = List.of("-", "L", "F");
    public final List<String> SOUTH_PIPES = List.of("|", "7", "F");
    public final List<String> WEST_PIPES = List.of("-", "J", "7");

    public final int[] neighborsI = new int[]{-1, 0, 1, 0};
    public final int[] neighborsJ = new int[]{0, 1, 0, -1};
    public final List<List<String>> OUTWARD_PIPES = List.of(NORTH_PIPES, EAST_PIPES, SOUTH_PIPES, WEST_PIPES);
    public final List<List<String>> INWARD_PIPES = List.of(SOUTH_PIPES, WEST_PIPES, NORTH_PIPES, EAST_PIPES);

    public static void main(String[] args) throws IOException {
        Day10 day = new Day10();

        day.printPart1("sample-input-1", 4);
        day.printPart1("sample-input-2", 8);
        day.printPart1("input", 6979);

        day.printPart2("sample-input-1", 1);
        day.printPart2("sample-input-2", 1);
        day.printPart2("sample-input-3", 4);
        day.printPart2("sample-input-4", 4);
        day.printPart2("sample-input-5", 8);
        day.printPart2("sample-input-5", 8);
        day.printPart2("sample-input-6", 10);
        day.printPart2("input", 443);
    }

    @Override
    public Integer part1(List<String> lines) {
        Node[][] grid = parseGrid(lines);
        return bfs(extractStart(grid), grid, lines, new boolean[lines.size()][lines.get(0).length()]);
    }

    @Override
    public Integer part2(List<String> lines) {
        boolean[][] visited = new boolean[lines.size()][lines.get(0).length()];
        Node[][] grid = parseGrid(lines);
        Node start = extractStart(grid);
        bfs(start, grid, lines, visited);

        List<String> cleanedLines = new ArrayList<>(lines.size());
        for (int i = 0; i < lines.size(); i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < visited[0].length; j++) {
                if (!visited[i][j]) {
                    sb.append(".");
                } else {
                    sb.append(grid[i][j].type);
                }
            }
            cleanedLines.add(sb.toString()
                .replace('S', convertS(start.i, start.j, grid))
                .replaceAll("F-*7|L-*J", "")
                .replaceAll("F-*J|L-*7", "|"));
        }
        int count = 0;
        for (String line : cleanedLines) {
            int parity = 0;
            for (char c : line.toCharArray()){
                if (c == '|') {
                    parity++;
                    continue;
                }
                if (c == '.' && parity % 2 == 1) {
                    count++;
                }
            }
        }
        return count;
    }

    public Node extractStart(Node[][] grid) {
        for (Node[] nodes : grid) {
            for (Node node : nodes) {
                if (node.type.equals("S")) {
                    return node;
                }
            }
        }
        return null;
    }

    public Node[][] parseGrid(List<String> lines) {
        int rowCount = lines.size();
        int colCount = lines.get(0).length();
        Node[][] grid = new Node[rowCount][colCount];
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                grid[i][j] = new Node(lines.get(i).charAt(j) + "", i, j, null);
            }
        }
        return grid;
    }

    public int bfs(Node start, Node[][] grid, List<String> lines, boolean[][] visited) {
        Queue<Node> queue = new ArrayDeque<>();
        queue.add(start);
        Node current = null;

        while (!queue.isEmpty()) {
            current = queue.poll();
            visited[current.i][current.j] = true;

            for (int k = 0; k < neighborsJ.length; k++) {
                int nI = current.i + neighborsI[k];
                int nJ = current.j + neighborsJ[k];
                if (isValid(nI, nJ, lines) &&
                    !visited[nI][nJ] &&
                    (current.type.equals("S") || OUTWARD_PIPES.get(k).contains(current.type)) &&
                    INWARD_PIPES.get(k).contains(grid[nI][nJ].type)) {
                    grid[nI][nJ] = grid[nI][nJ].withParent(current);
                    queue.add(grid[nI][nJ]);
                }
            }
        }

        return backtrackPath(current);
    }

    public boolean isValid(int i, int j, List<String> lines) {
        return i >= 0 && i < lines.size() && j >= 0 && j < lines.get(0).length();
    }

    public int backtrackPath(Node node) {
        int steps = 0;
        while (node.parent != null) {
            steps++;
            node = node.parent;
        }
        return steps;
    }

    public char convertS(int i, int j, Node[][] grid) {
        char c;
        if (i == 0) {
            if (j == 0) {
                c = 'F';
            } else if (j == grid[0].length - 1) {
                c = '7';
            } else {
                c = '|';
            }
        } else if (i == grid.length - 1) {
            if (j == 0) {
                c = 'L';
            } else if (j == grid[0].length - 1) {
                c = 'J';
            } else {
                c = '-';
            }
        } else {
            if (SOUTH_PIPES.contains(grid[i - 1][j].type) && WEST_PIPES.contains(grid[i][j + 1].type)) {
                c = 'L';
            } else if (WEST_PIPES.contains(grid[i][j + 1].type) && NORTH_PIPES.contains(grid[i + 1][j].type)) {
                c = 'F';
            } else if (NORTH_PIPES.contains(grid[i + 1][j].type) && EAST_PIPES.contains(grid[i][j - 1].type)) {
                c = '7';
            } else if(EAST_PIPES.contains(grid[i][j - 1].type) && SOUTH_PIPES.contains(grid[i - 1][j].type)) {
                c = 'J';
            } else {
                c = '|';
            }
        }
        return c;
    }

    @With
    public record Node(String type, int i, int j, Node parent) {}
}
