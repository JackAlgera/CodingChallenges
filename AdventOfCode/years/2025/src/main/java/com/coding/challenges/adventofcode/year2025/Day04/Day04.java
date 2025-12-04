package com.coding.challenges.adventofcode.year2025.Day04;

import java.io.IOException;
import java.util.List;
import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.InputLinesUtilities;
import com.coding.challenges.adventofcode.utils.Utilities;

public class Day04 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day04 day = new Day04();

    day.printPart1("sample-input", 13L);
    day.printPart1("input", 1508L);

    day.printPart2("sample-input", 43L);
    day.printPart2("input", 8538L);
  }

  @Override
  public Long part1(List<String> lines) {
    var grid = InputLinesUtilities.extractGrid(lines);
    int maxI = grid.length;
    int maxJ = grid[0].length;
    long accessableRolls = 0;
    for (int i = 0; i < maxI; i++) {
      for (int j = 0; j < maxJ; j++) {
        if (canBeRemoved(grid, i, j, maxI, maxJ)) {
          accessableRolls++;
        }
      }
    }
    return accessableRolls;
  }

  @Override
  public Long part2(List<String> lines) {
    var grid = InputLinesUtilities.extractGrid(lines);
    int maxI = grid.length;
    int maxJ = grid[0].length;
    long removedRolls = 0;
    boolean removedNoRolls = false;
    while (!removedNoRolls) {
      removedNoRolls = true;
      var newGrid = Utilities.deepCopy(grid);
      for (int i = 0; i < maxI; i++) {
        for (int j = 0; j < maxJ; j++) {
          if (canBeRemoved(grid, i, j, maxI, maxJ)) {
            removedRolls++;
            removedNoRolls = false;
            newGrid[i][j] = '.';
          }
        }
      }
      grid = newGrid;
    }
    return removedRolls;
  }

  private static boolean canBeRemoved(char[][] grid, int i, int j, int maxI, int maxJ) {
    char c = grid[i][j];
    if (c != '@') {
      return false;
    }

    int neighbors = 0;
    for (int k = 0; k < Utilities.NEIGHBORS_8_I.length; k++) {
      int ni = i + Utilities.NEIGHBORS_8_I[k];
      int nj = j + Utilities.NEIGHBORS_8_J[k];
      if (Utilities.isValidIndex(ni, nj, maxI, maxJ) && grid[ni][nj] == '@') {
        neighbors++;
      }
    }

    return neighbors < 4;
  }
}