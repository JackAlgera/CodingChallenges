package com.coding.challenges.adventofcode.utils;

import com.coding.challenges.adventofcode.utils.enums.Direction;
import com.coding.challenges.adventofcode.utils.enums.Direction8;
import java.util.List;

public record Pos(int i, int j) {
  public Pos move(Direction direction) {
    return switch (direction) {
      case N -> new Pos(i - 1, j);
      case E -> new Pos(i, j + 1);
      case S -> new Pos(i + 1, j);
      case W -> new Pos(i, j - 1);
    };
  }

  public Pos move(Direction8 direction) {
    return switch (direction) {
      case N -> new Pos(i - 1, j);
      case E -> new Pos(i, j + 1);
      case S -> new Pos(i + 1, j);
      case W -> new Pos(i, j - 1);
      case NW -> new Pos(i - 1, j - 1);
      case NE -> new Pos(i - 1, j + 1);
      case SW -> new Pos(i + 1, j - 1);
      case SE -> new Pos(i + 1, j + 1);
    };
  }

  public boolean isValid(int maxI, int maxJ) {
    return i >= 0 && i < maxI && j >= 0 && j < maxJ;
  }

  public boolean isValid(List<String> lines) {
    return i >= 0 && i < lines.size() && j >= 0 && j < lines.getFirst().length();
  }

  public boolean isValid(int[][] map) {
    return i >= 0 && i < map.length && j >= 0 && j < map[0].length;
  }

  public boolean isValid(long[][] map) {
    return i >= 0 && i < map.length && j >= 0 && j < map[0].length;
  }

  public boolean isValid(char[][] map) {
    return i >= 0 && i < map.length && j >= 0 && j < map[0].length;
  }

  public int manhattanDistance(Pos other) {
    return Math.abs(i - other.i) + Math.abs(j - other.j);
  }
}
