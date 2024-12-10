package com.coding.challenges.adventofcode.year2024.Day10;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day10 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day10 day = new Day10();

    day.printPart1("sample-input", 36L);
    day.printPart1("input", 719L);

    day.printPart2("sample-input", 81L);
    day.printPart2("input", 1530L);
  }

  @Override
  public Long part1(List<String> lines) {
    int[][] map = extractMap(lines);
    long total = 0L;
    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.getFirst().length(); j++) {
        char c = lines.get(i).charAt(j);
        if (c != '0') {
          continue;
        }
        Set<Pos> ends = new HashSet<>();
        Pos pos = new Pos(i, j);
        trial(ends, pos, map, 0);
        total += ends.size();
      }
    }
    return total;
  }

  @Override
  public Long part2(List<String> lines) {
    int[][] map = extractMap(lines);
    long total = 0L;
    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.getFirst().length(); j++) {
        char c = lines.get(i).charAt(j);
        if (c != '0') {
          continue;
        }
        Pos pos = new Pos(i, j);
        total += trial(pos, map);
      }
    }
    return total;
  }

  private long trial(Pos current, int[][] map) {
    int c = map[current.i()][current.j()];
    if (c == 9) {
      return 1;
    }

    long total = 0L;

    for (Direction direction : Direction.values()) {
      Pos next = current.move(direction);
      if (next.isValid(map) && map[next.i()][next.j()] == c + 1) {
        total += trial(next, map);
      }
    }

    return total;
  }

  private void trial(Set<Pos> ends, Pos current, int[][] map, int score) {
    int c = map[current.i()][current.j()];
    if (c == 9) {
      ends.add(current);
      return;
    }

    for (Direction direction : Direction.values()) {
      Pos next = current.move(direction);
      if (next.isValid(map) && map[next.i()][next.j()] == c + 1) {
        trial(ends, next, map, score + 1);
      }
    }
  }

  private int[][] extractMap(List<String> lines) {
    int[][] map = new int[lines.size()][lines.getFirst().length()];
    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.getFirst().length(); j++) {
        char c = lines.get(i).charAt(j);
        if (c == '.') {
          map[i][j] = -1;
        } else {
          map[i][j] = Integer.parseInt(String.valueOf(c));
        }
      }
    }
    return map;
  }
}
