package com.coding.challenges.adventofcode.utils;

import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Printer {
  public static void print(List<String> lines) {
    System.out.println();
    for (String line : lines) {
      System.out.println(line);
    }
    System.out.println();
  }

  public static void print(char[][] grid) {
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        System.out.print(grid[i][j]);
      }
      System.out.println();
    }
    System.out.println();
  }
}
