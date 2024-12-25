package com.coding.challenges.adventofcode.year2024.Day16;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.InputLinesUtilities;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Day16 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day16 day = new Day16();

    day.printPart1("sample-input-1", 7036L);
    day.printPart1("sample-input-2", 11048L);
    day.printPart1("input", 88416L);

    day.printPart2("sample-input-1", 45L);
    day.printPart2("sample-input-2", 64L);
    day.printPart2("input", 442L);
  }

  @Override
  public Long part1(List<String> lines) {
    char[][] grid = InputLinesUtilities.extractGrid(lines);
    Pos start = new Pos(grid[0].length - 2, 1);
    Pos end = new Pos(1, grid[0].length - 2);
    long[][][] cache = bfs(grid, new State(start, Direction.E, 0L), end);

    return Arrays.stream(cache[end.i()][end.j()]).filter(v -> v != 0L).min().orElse(0L);
  }

  @Override
  public Long part2(List<String> lines) {
    char[][] grid = InputLinesUtilities.extractGrid(lines);
    Pos start = new Pos(grid[0].length - 2, 1);
    Pos end = new Pos(1, grid[0].length - 2);
    long[][][] cache = bfs(grid, new State(start, Direction.E, 0L), end);

    long best = Arrays.stream(cache[end.i()][end.j()]).filter(v -> v != 0L).min().orElse(0L);
    return backtrack(start, end, grid, cache, best);
  }

  public long backtrack(Pos start, Pos end, char[][] grid, long[][][] cache, long best) {
    Set<Pos> bestPositions = new HashSet<>();
    Queue<State> queue = new LinkedList<>();
    for (Direction d : Direction.values()) {
      if (cache[end.i()][end.j()][d.ordinal()] == best) {
        queue.add(new State(end, d, best));
      }
    }
    bestPositions.add(end);
    bestPositions.add(start);

    while (!queue.isEmpty()) {
      State state = queue.poll();
      bestPositions.add(state.pos);

      if (state.pos().equals(start)) {
        continue;
      }

      for (Direction movementDirection : Direction.values()) {
        Pos newPos = state.pos().move(movementDirection);

        if (!newPos.isValid(cache.length, cache[0].length) || grid[newPos.i()][newPos.j()] == '#') {
          continue;
        }

        Arrays.stream(cache[newPos.i()][newPos.j()])
            .filter(v -> v == state.score() - 1 || v == state.score() - 1001)
            .mapToObj(v -> new State(newPos, movementDirection, v))
            .forEach(queue::add);
      }
    }
    return bestPositions.size();
  }

  public long[][][] bfs(char[][] grid, State start, Pos end) {
    long[][][] cache = new long[grid.length][grid[0].length][4];

    Queue<State> queue = new LinkedList<>();
    queue.add(start);

    while (!queue.isEmpty()) {
      State state = queue.poll();
      Direction direction = state.d;
      Pos pos = state.pos;

      if (pos.equals(end)) {
        continue;
      }

      for (Direction d : Direction.values()) {
        Pos newPos = pos.move(d);
        if (d.rotateRight(2) == direction) {
          continue;
        }

        if (!newPos.isValid(grid) || grid[newPos.i()][newPos.j()] == '#') {
          continue;
        }

        long newScore = state.score() + 1 + (d != direction ? 1000 : 0);
        if (cache[newPos.i()][newPos.j()][d.ordinal()] != 0L
            && newScore > cache[newPos.i()][newPos.j()][d.ordinal()]) {
          continue;
        }
        cache[newPos.i()][newPos.j()][d.ordinal()] = newScore;
        queue.add(new State(newPos, d, newScore));
      }
    }

    return cache;
  }

  public record State(Pos pos, Direction d, long score) {}
}
