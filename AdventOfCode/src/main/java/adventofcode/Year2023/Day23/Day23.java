package adventofcode.Year2023.Day23;

import adventofcode.utils.Day;
import adventofcode.utils.Pos;
import adventofcode.utils.Utilities;
import adventofcode.utils.enums.Direction;

import java.io.IOException;
import java.util.*;

public class Day23 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day23 day = new Day23();

        day.printPart1("sample-input", 94L);
        day.printPart1("input", 2430L);

        day.printPart2("sample-input", 154L);
        day.printPart2("input", 6534L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return dfs(parseGrid(lines, true));
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return dfs(parseGrid(lines, false));
    }

    public long dfs(Node[][] grid) {
        State start = new State(findBorderNode(grid, 0), 0);
        Pos endPos = findBorderNode(grid, grid[0].length - 1);
        return dfs(start, endPos, grid, new HashSet<>());
    }

    public long dfs(State current, Pos end, Node[][] grid, Set<Pos> visited) {
        if (current.pos().equals(end)) {
            return current.steps();
        }

        long best = -1;
        Node currentNode = grid[current.pos().i()][current.pos().j()];

        for (Pos neighbor : currentNode.neighbors()) {
            if (!Utilities.isValidIndex(neighbor, grid.length, grid[0].length) ||
                visited.contains(neighbor) ||
                grid[neighbor.i()][neighbor.j()].val().equals("#")) {
                continue;
            }
            visited.add(neighbor);
            long res = dfs(new State(neighbor, current.steps() + 1), end, grid, visited);
            if (res != -1) {
                best = Math.max(best, res);
            }
            visited.remove(neighbor);
        }

        return best;
    }

    public Pos findBorderNode(Node[][] grid, int i) {
        for (int j = 0; j < grid[0].length; j++) {
            if (grid[i][j].val.equals(".")) {
                return new Pos(i, j);
            }
        }
        return null;
    }

    public Node[][] parseGrid(List<String> lines, boolean part1) {
        Node[][] grid = new Node[lines.size()][lines.get(0).length()];
        for (int i = 0; i < grid.length; i++) {
            for (int j = 0; j < lines.get(i).length(); j++) {
                String val = "" + lines.get(i).charAt(j);
                Pos pos = new Pos(i, j);
                Set<Pos> neighbors = new HashSet<>();
                if (part1) {
                    switch (val) {
                        case ">" -> neighbors.add(pos.move(Direction.E));
                        case "<" -> neighbors.add(pos.move(Direction.W));
                        case "^" -> neighbors.add(pos.move(Direction.N));
                        case "v" -> neighbors.add(pos.move(Direction.S));
                        case "." -> {
                            neighbors.add(pos.move(Direction.N));
                            neighbors.add(pos.move(Direction.S));
                            neighbors.add(pos.move(Direction.E));
                            neighbors.add(pos.move(Direction.W));
                        }
                    }
                } else {
                    neighbors.add(pos.move(Direction.N));
                    neighbors.add(pos.move(Direction.S));
                    neighbors.add(pos.move(Direction.E));
                    neighbors.add(pos.move(Direction.W));
                }
                grid[i][j] = new Node(pos, val, neighbors);
            }
        }
        return grid;
    }

    public record State(Pos pos, int steps) {}
    public record Node(Pos pos, String val, Set<Pos> neighbors) {}
}
