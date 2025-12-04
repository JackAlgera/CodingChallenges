package com.coding.challenges.adventofcode.utils;

import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Utilities {

  public static BufferedReader getBufferedReader(String dayInput) throws FileNotFoundException {
    return new BufferedReader(
        new FileReader("AdventOfCode/src/main/java/adventofcode/" + dayInput));
  }

  /** Indexes for the 4 neighbors of a tile: NORTH, EAST, SOUTH, WEST */
  public static final int[] NEIGHBORS_4_I = new int[] {-1, 0, 1, 0};

  public static final int[] NEIGHBORS_4_J = new int[] {0, 1, 0, -1};
  public static final Direction[] NEIGHBORS_4_DIRECTIONS =
      new Direction[] {Direction.N, Direction.E, Direction.S, Direction.W};

  /** Indexes for the 26 neighbors of a tile in 3D */
  public static final int[] NEIGHBORS_26_X =
      new int[] {
        -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1, -1, 0, 1
      };

  public static final int[] NEIGHBORS_26_Y =
      new int[] {
        -1, -1, -1, 0, 0, 0, 1, 1, 1, -1, -1, -1, 0, 0, 1, 1, 1, -1, -1, -1, 0, 0, 0, 1, 1, 1
      };
  public static final int[] NEIGHBORS_26_Z =
      new int[] {
        -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1
      };

  public static boolean isValidIndex(int i, int j, int maxI, int maxJ) {
    return i >= 0 && i < maxI && j >= 0 && j < maxJ;
  }

  public static boolean isValidIndex(Pos pos, int maxI, int maxJ) {
    return pos.i() >= 0 && pos.i() < maxI && pos.j() >= 0 && pos.j() < maxJ;
  }

  public static <T> void deepCopy(T[][] grid, T[][] temp) {
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        grid[i][j] = temp[i][j];
      }
    }
  }

  public static char[][] deepCopy(char[][] grid) {
    char[][] copy = new char[grid.length][grid[0].length];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        copy[i][j] = grid[i][j];
      }
    }
    return copy;
  }

  /**
   * Indexes for the 8 neighbors of a tile: TOP_LEFT, TOP, TOP_RIGHT, LEFT, RIGHT, BOTTOM_LEFT,
   * BOTTOM, BOTTOM_RIGHT
   */
  public static final int[] NEIGHBORS_8_I = new int[] {-1, -1, -1, 0, 0, 1, 1, 1};

  public static final int[] NEIGHBORS_8_J = new int[] {-1, 0, 1, -1, 1, -1, 0, 1};

  public static String greenWord(String word) {
    return String.format("\u001B[32m%s\u001B[0m", word);
  }

  public static String redWord(String word) {
    return String.format("\u001B[31m%s\u001B[0m", word);
  }

  public static String yellowWord(String word) {
    return String.format("\u001B[33m%s\u001B[0m", word);
  }
}
