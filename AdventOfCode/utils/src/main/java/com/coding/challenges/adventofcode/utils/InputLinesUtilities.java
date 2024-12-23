package com.coding.challenges.adventofcode.utils;

import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class InputLinesUtilities {

  public Pos findChar(List<String> lines, char target) {
    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.get(0).length(); j++) {
        if (lines.get(i).charAt(j) == target) {
          return new Pos(i, j);
        }
      }
    }

    throw new IllegalArgumentException("Target not found");
  }

  public Pos findChar(char[][] grid, char target) {
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == target) {
          return new Pos(i, j);
        }
      }
    }

    throw new IllegalArgumentException("Target not found");
  }

  public char[][] extractGrid(List<String> lines) {
    int maxI = lines.contains("") ? lines.indexOf("") : lines.size();
    char[][] grid = new char[maxI][lines.get(0).length()];

    for (int i = 0; i < maxI; i++) {
      for (int j = 0; j < lines.get(0).length(); j++) {
        grid[i][j] = lines.get(i).charAt(j);
      }
    }

    return grid;
  }
}
