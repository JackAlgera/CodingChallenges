package com.coding.challenges.adventofcode.year2023.Day21;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.Utilities;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Day21 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day21 day = new Day21();

    day.printPart1("sample-input", 16L);
    day.printPart1("input", 3666L);

    day.printPart2("input", 609298746763952L);
  }

  @Override
  public Long part1(List<String> lines) {
    return countReachablePlots(lines, lines.size() < 15 ? 6 : 64);
  }

  @Override
  public Long part2(List<String> lines) {
    int n = lines.size();
    long a0 = countReachablePlots(lines, n / 2);
    long a1 = countReachablePlots(lines, n / 2 + n);
    long a2 = countReachablePlots(lines, n / 2 + 2 * n);
    long q = 26501365L / n;

    return a0 + q * (a1 - a0) + q * (q - 1) / 2 * (a2 - 2 * a1 + a0);
  }

  public long countReachablePlots(List<String> lines, int steps) {
    Set<Pos> reachable = new HashSet<>();
    String[][] grid = parseGrid(lines);
    bfs(grid, findAndRemoveStart(grid), 0, steps, reachable, new HashSet<>());
    return reachable.size();
  }

  public String[][] parseGrid(List<String> lines) {
    String[][] grid = new String[lines.size()][lines.get(0).length()];
    for (int i = 0; i < lines.size(); i++) {
      grid[i] = lines.get(i).split("");
    }
    return grid;
  }

  public Pos findAndRemoveStart(String[][] grid) {
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (Objects.equals(grid[i][j], "S")) {
          grid[i][j] = ".";
          return new Pos(i, j);
        }
      }
    }
    return null;
  }

  public void bfs(
      String[][] grid,
      Pos current,
      int totalSteps,
      int maxSteps,
      Set<Pos> reachableSpots,
      Set<State> dp) {
    if (dp.contains(new State(current.i(), current.j(), totalSteps))) {
      return;
    }
    dp.add(new State(current.i(), current.j(), totalSteps));
    if (totalSteps == maxSteps) {
      reachableSpots.add(new Pos(current.i(), current.j()));
      return;
    }

    for (int k = 0; k < Utilities.NEIGHBORS_4_DIRECTIONS.length; k++) {
      Pos next = current.move(Utilities.NEIGHBORS_4_DIRECTIONS[k]);

      int i = (next.i() < 0 ? grid.length + (next.i() % grid.length) : next.i()) % grid.length;
      int j =
          (next.j() < 0 ? grid[0].length + (next.j() % grid[0].length) : next.j()) % grid[0].length;

      if (!Objects.equals(grid[i][j], ".")) {
        continue;
      }

      bfs(grid, next, totalSteps + 1, maxSteps, reachableSpots, dp);
    }
  }

  public record State(int i, int j, int steps) {}
}
