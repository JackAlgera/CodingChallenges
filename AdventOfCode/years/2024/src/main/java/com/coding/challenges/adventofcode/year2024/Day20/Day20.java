package com.coding.challenges.adventofcode.year2024.Day20;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.InputLinesUtilities;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Day20 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day20 day = new Day20();

    day.printPart1("sample-input", 44L);
    day.printPart1("input", 1338L);

    day.printPart2("sample-input", 285L);
    day.printPart2("input", 975376L);
  }

  @Override
  public Long part1(List<String> lines) {
    return solve(lines, 2, lines.size() < 20 ? 2 : 100);
  }

  @Override
  public Long part2(List<String> lines) {
    return solve(lines, 20, lines.size() < 20 ? 50 : 100);
  }

  private long solve(List<String> lines, int cheats, int totalSaved) {
    char[][] grid = InputLinesUtilities.extractGrid(lines);
    Pos start = null;
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == 'S') {
          start = new Pos(i, j);
        }
      }
    }

    Map<Pos, Integer> distances = floodPath(grid, start);
    List<Pos> positions = distances.keySet().stream().toList();
    long total = 0;

    for (int i = 0; i < positions.size(); i++) {
      for (int j = i + 1; j < positions.size(); j++) {
        Pos p1 = positions.get(i);
        Pos p2 = positions.get(j);
        int d = p1.manhattanDistance(p2);
        int stepsSaved = Math.abs(distances.get(p1) - distances.get(p2)) - d;
        if (d <= cheats && stepsSaved >= totalSaved) {
          total++;
        }
      }
    }

    return total;
  }

  private Map<Pos, Integer> floodPath(char[][] grid, Pos start) {
    Map<Pos, Integer> distances = new HashMap<>();
    Queue<Pos> queue = new LinkedList<>();
    queue.add(start);
    distances.put(start, 0);

    while (!queue.isEmpty()) {
      Pos current = queue.poll();

      for (Direction d : Direction.values()) {
        Pos newPos = current.move(d);
        if (!newPos.isValid(grid) || grid[newPos.i()][newPos.j()] == '#') {
          continue;
        }

        if (distances.containsKey(newPos)) {
          continue;
        }

        queue.add(newPos);
        distances.put(newPos, distances.get(current) + 1);
      }
    }

    return distances;
  }
}
