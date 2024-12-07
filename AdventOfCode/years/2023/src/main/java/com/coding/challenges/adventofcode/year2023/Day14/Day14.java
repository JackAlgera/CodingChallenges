package com.coding.challenges.adventofcode.year2023.Day14;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pair;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day14 extends Day<Long> {

  private static final Long TOTAL_CYCLES = 1_000_000_000L;

  public static void main(String[] args) throws IOException {
    Day14 day = new Day14();

    day.printPart1("sample-input", 136L);
    day.printPart1("input", 106378L);

    day.printPart2("sample-input", 64L);
    day.printPart2("input", 90795L);
  }

  @Override
  public Long part1(List<String> lines) {
    Platform platform = parseInput(lines);
    platform.rollRocksNorth();
    return platform.countLoad();
  }

  @Override
  public Long part2(List<String> lines) {
    Platform platform = parseInput(lines);
    Map<String, Pair<Long>> cache = new HashMap<>();

    long i = 0;
    while (i < TOTAL_CYCLES) {
      platform.rollOneCycle();
      i++;
      if (!cache.containsKey(platform.getGridKey())) {
        cache.put(platform.getGridKey(), new Pair<>(i, -1L));
      } else {
        Pair<Long> cycle = cache.get(platform.getGridKey());
        if (cycle.getSecond() == -1) {
          cycle.setSecond(i - cycle.getFirst());
        } else {
          long totalCycles = cycle.getSecond();
          i += ((TOTAL_CYCLES - i) / totalCycles) * totalCycles;
        }
      }
    }

    return platform.countLoad();
  }

  public Platform parseInput(List<String> lines) {
    int height = lines.size();
    int width = lines.get(0).length();
    Character[][] grid = new Character[height][width];
    for (int i = 0; i < height; i++) {
      for (int j = 0; j < width; j++) {
        char c = lines.get(i).charAt(j);
        grid[i][j] = c;
      }
    }
    return new Platform(grid);
  }

  public record Platform(Character[][] grid) {
    public long countLoad() {
      long height = grid.length;
      long total = 0;
      for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
          if (grid[i][j] == 'O') {
            total += height - i;
          }
        }
      }
      return total;
    }

    public void rollOneCycle() {
      rollRocksNorth();
      rollRocksWest();
      rollRocksSouth();
      rollRocksEast();
    }

    public String getGridKey() {
      StringBuilder key = new StringBuilder();
      for (Character[] characters : grid) {
        for (Character character : characters) {
          key.append(character);
        }
      }

      return key.toString();
    }

    private void rollRocksNorth() {
      for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
          if (grid[i][j] == 'O') {
            int newI = i;
            while (newI > 0 && grid[newI - 1][j] == '.') {
              newI--;
            }
            if (newI != i) {
              grid[newI][j] = 'O';
              grid[i][j] = '.';
            }
          }
        }
      }
    }

    private void rollRocksSouth() {
      for (int i = grid.length - 1; i >= 0; i--) {
        for (int j = 0; j < grid[0].length; j++) {
          if (grid[i][j] == 'O') {
            int newI = i;
            while (newI < grid.length - 1 && grid[newI + 1][j] == '.') {
              newI++;
            }
            if (newI != i) {
              grid[newI][j] = 'O';
              grid[i][j] = '.';
            }
          }
        }
      }
    }

    private void rollRocksWest() {
      for (int i = 0; i < grid.length; i++) {
        for (int j = 0; j < grid[0].length; j++) {
          if (grid[i][j] == 'O') {
            int newJ = j;
            while (newJ > 0 && grid[i][newJ - 1] == '.') {
              newJ--;
            }
            if (newJ != j) {
              grid[i][newJ] = 'O';
              grid[i][j] = '.';
            }
          }
        }
      }
    }

    private void rollRocksEast() {
      for (int i = 0; i < grid.length; i++) {
        for (int j = grid[0].length - 1; j >= 0; j--) {
          if (grid[i][j] == 'O') {
            int newJ = j;
            while (newJ < grid[0].length - 1 && grid[i][newJ + 1] == '.') {
              newJ++;
            }
            if (newJ != j) {
              grid[i][newJ] = 'O';
              grid[i][j] = '.';
            }
          }
        }
      }
    }
  }
}
