package com.coding.challenges.adventofcode.year2025.Day06;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day06 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day06 day = new Day06();

    day.printPart1("sample-input", 4277556L);
    day.printPart1("input", 5873191732773L);

    day.printPart2("sample-input", 3263827L);
    day.printPart2("input", 11386445308378L);
  }

  @Override
  public Long part1(List<String> lines) {
    var operators = Arrays.stream(lines.get(lines.size() - 1).split("\\s+")).toList();
    var grid = extractGrid(lines);

    long finalTotal = 0L;
    for (int j = 0; j < grid[0].length; j++) {
      long total = grid[0][j];
      if (operators.get(j).equals("+")) {
        for (int i = 1; i < grid.length; i++) {
          total += grid[i][j];
        }
      } else {
        for (int i = 1; i < grid.length; i++) {
          total *= grid[i][j];
        }
      }
      finalTotal += total;
    }
    return finalTotal;
  }

  @Override
  public Long part2(List<String> lines) {
    long finalTotal = 0L;
    var operators = lines.get(lines.size() - 1);
    int width = lines.get(0).length();

    int j = 0;
    while (j < width) {
      if (wholeColumnIsEmpty(lines, j)) {
        j++;
        continue;
      }

      char operator = operators.charAt(j);
      long total = extractNumber(lines, j);
      j++;
      while (j < width && !wholeColumnIsEmpty(lines, j)) {
        long nextNumber = extractNumber(lines, j);
        total = operator == '+' ? total + nextNumber : total * nextNumber;
        j++;
      }
      j++;
      finalTotal += total;
    }

    return finalTotal;
  }

  private boolean wholeColumnIsEmpty(List<String> lines, int j) {
    for (String line : lines) {
      if (line.charAt(j) != ' ') return false;
    }
    return true;
  }

  private long extractNumber(List<String> lines, int j) {
    StringBuilder numberStr = new StringBuilder();
    for (int i = 0; i < lines.size() - 1; i++) {
      var c = lines.get(i).charAt(j);
      if (c != ' ') numberStr.append(c);
    }
    return Long.parseLong(numberStr.toString());
  }

  private long[][] extractGrid(List<String> lines) {
    var grid = new ArrayList<long[]>();

    for (int i = 0; i < lines.size() - 1; i++) {
      grid.add(
          Arrays.stream(lines.get(i).strip().split("\\s+")).mapToLong(Long::parseLong).toArray());
    }
    return grid.toArray(long[][]::new);
  }
}
