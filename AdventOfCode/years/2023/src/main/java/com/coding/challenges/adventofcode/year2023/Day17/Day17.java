package com.coding.challenges.adventofcode.year2023.Day17;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.Utilities;
import com.coding.challenges.adventofcode.utils.enums.Direction;

import java.io.IOException;
import java.util.*;

public class Day17 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day17 day = new Day17();

        day.printPart1("sample-input-1", 102L);
        day.printPart1("input", 755L);

        day.printPart2("sample-input-1", 94L);
        day.printPart2("sample-input-2", 71L);
        day.printPart2("input", 881L);
    }

    @Override
    public Long part1(List<String> lines) {
        return djikstra(parseInput(lines), 0, 3);
    }

    @Override
    public Long part2(List<String> lines) {
        return djikstra(parseInput(lines), 4, 10);
    }

    public long djikstra(Long[][] grid, int minSteps, int maxSteps) {
        int height = grid.length;
        int width = grid[0].length;
        Pos end = new Pos(height - 1, width - 1);

        Set<Key> visited = new HashSet<>();
        Queue<State> queue = new PriorityQueue<>(Comparator.comparingLong(s -> s.heatloss));
        queue.add(new State(0, new Key(new Pos(0, 0), Direction.E, 0)));
        queue.add(new State(0, new Key(new Pos(0, 0), Direction.S, 0)));

        while (!queue.isEmpty()) {
            State state = queue.poll();
            if (visited.contains(state.key) || !Utilities.isValidIndex(state.key.pos, height, width)) {
                continue;
            }
            visited.add(state.key);

            if (state.key.pos.equals(end) && state.key.continuousSteps >= minSteps) {
                return state.heatloss;
            }

            // Make sure to move forward min steps, and if possible continue moving forward
            Direction newDirection = state.key.direction;
            Pos newPos = state.key.pos.move(newDirection);
            Key moveForward = new Key(newPos, state.key.direction, state.key.continuousSteps + 1);
            if (state.key.continuousSteps < minSteps) {
                if (Utilities.isValidIndex(newPos, height, width)) {
                    queue.add(new State(state.heatloss + grid[newPos.i()][newPos.j()], moveForward));
                }
                continue;
            }

            if (state.key.continuousSteps < maxSteps && Utilities.isValidIndex(newPos, height, width)) {
                queue.add(new State(state.heatloss + grid[newPos.i()][newPos.j()], moveForward));
            }

            // Rotate left
            newDirection = state.key.direction.rotateLeft(1);
            newPos = state.key.pos.move(newDirection);
            if (Utilities.isValidIndex(newPos, height, width)) {
                queue.add(new State(
                    state.heatloss + grid[newPos.i()][newPos.j()],
                    new Key(newPos, newDirection, 1)));
            }

            // Rotate right
            newDirection = state.key.direction.rotateRight(1);
            newPos = state.key.pos.move(newDirection);
            if (Utilities.isValidIndex(newPos, height, width)) {
                queue.add(new State(
                    state.heatloss + grid[newPos.i()][newPos.j()],
                    new Key(newPos, newDirection, 1)));
            }
        }

        return -1;
    }

    public Long[][] parseInput(List<String> lines) {
        Long[][] grid = new Long[lines.size()][lines.get(0).length()];
        for (int i = 0; i < lines.size(); i++) {
            for (int j = 0; j < lines.get(0).length(); j++) {
                grid[i][j] = Long.parseLong("" + lines.get(i).charAt(j));
            }
        }
        return grid;
    }

    public record State(long heatloss, Key key) {
    }

    public record Key(Pos pos, Direction direction, int continuousSteps) {
    }
}
