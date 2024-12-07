package com.coding.challenges.adventofcode.year2020.Day11;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Utilities;
import java.io.IOException;
import java.util.List;

public class Day11 extends Day<Integer> {

  public static void main(String[] args) throws IOException {
    Day11 day = new Day11();

    day.printPart1("sample-input", 37);
    day.printPart1("input", 2338);

    day.printPart2("sample-input", 26);
    day.printPart2("input", 2134);
  }

  @Override
  public Integer part1(List<String> lines) {
    Character[][] grid = parseInput(lines);
    while (playRound(grid, 4, true)) {}
    return countOccupiedSeats(grid);
  }

  @Override
  public Integer part2(List<String> lines) {
    Character[][] grid = parseInput(lines);
    while (playRound(grid, 5, false)) {}
    return countOccupiedSeats(grid);
  }

  private static Integer countOccupiedSeats(Character[][] grid) {
    Integer occupiedSeats = 0;
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == '#') {
          occupiedSeats++;
        }
      }
    }
    return occupiedSeats;
  }

  public boolean playRound(Character[][] grid, int maxNeighbors, boolean directNeighbors) {
    boolean someoneMoved = false;
    Character[][] temp = new Character[grid.length][grid[0].length];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == '.') {
          temp[i][j] = '.';
          continue;
        }

        int occupiedNeighbors =
            directNeighbors ? getDirectNeighbors(grid, i, j) : getLineNeighbors(grid, i, j);

        if (grid[i][j] == 'L' && occupiedNeighbors == 0) {
          temp[i][j] = '#';
          someoneMoved = true;
          continue;
        }
        if (grid[i][j] == '#' && occupiedNeighbors >= maxNeighbors) {
          temp[i][j] = 'L';
          someoneMoved = true;
          continue;
        }
        temp[i][j] = grid[i][j];
      }
    }
    Utilities.deepCopy(grid, temp);
    return someoneMoved;
  }

  private static int getDirectNeighbors(Character[][] grid, int i, int j) {
    int occupiedNeighbors = 0;
    for (int k = 0; k < Utilities.NEIGHBORS_8_I.length; k++) {
      int neighborI = i + Utilities.NEIGHBORS_8_I[k];
      int neighborJ = j + Utilities.NEIGHBORS_8_J[k];
      if (!Utilities.isValidIndex(neighborI, neighborJ, grid.length, grid[0].length)) {
        continue;
      }

      if (grid[neighborI][neighborJ] == '#') {
        occupiedNeighbors++;
      }
    }
    return occupiedNeighbors;
  }

  private static int getLineNeighbors(Character[][] grid, int i, int j) {
    int occupiedNeighbors = 0;
    for (int k = 0; k < Utilities.NEIGHBORS_8_I.length; k++) {
      int l = 1;
      int neighborI = i + Utilities.NEIGHBORS_8_I[k] * l;
      int neighborJ = j + Utilities.NEIGHBORS_8_J[k] * l;

      while (Utilities.isValidIndex(neighborI, neighborJ, grid.length, grid[0].length)) {
        if (grid[neighborI][neighborJ] == '#') {
          occupiedNeighbors++;
          break;
        }
        if (grid[neighborI][neighborJ] == 'L') {
          break;
        }
        l++;
        neighborI = i + Utilities.NEIGHBORS_8_I[k] * l;
        neighborJ = j + Utilities.NEIGHBORS_8_J[k] * l;
      }
    }
    return occupiedNeighbors;
  }

  public Character[][] parseInput(List<String> lines) {
    Character[][] grid = new Character[lines.size()][lines.get(0).length()];
    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.get(0).length(); j++) {
        grid[i][j] = lines.get(i).charAt(j);
      }
    }
    return grid;
  }
}
