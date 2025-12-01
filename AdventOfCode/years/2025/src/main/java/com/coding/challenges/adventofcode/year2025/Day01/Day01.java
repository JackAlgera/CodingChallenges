package com.coding.challenges.adventofcode.year2025.Day01;

import java.io.IOException;
import java.util.List;
import com.coding.challenges.adventofcode.utils.Day;

public class Day01 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day01 day = new Day01();

    day.printPart1("sample-input", 3L);
    day.printPart1("input", 1034L);

    day.printPart2("sample-input", 6L);
    day.printPart2("input", 6166L);
    // Too high
  }

  @Override
  public Long part1(List<String> lines) {
    int val = 50;
    long count = 0;

    for (String command : lines) {
      int direction = command.charAt(0) == 'L' ? -1 : 1;
      int rotations = Integer.parseInt(command.substring(1));
      val = Math.floorMod(val + direction * rotations, 100);
      if (val == 0) {
        count++;
      }
    }

    return count;
  }

  @Override
  public Long part2(List<String> lines) {
    long val = 50;
    long count = 0;

    for (String command : lines) {
      long direction = command.charAt(0) == 'L' ? -1 : 1;
      long rotations = Integer.parseInt(command.substring(1));

      long prevVal = val;
      val += direction * rotations;

      if (direction == 1) {
        count += Math.floorDiv(val, 100) - Math.floorDiv(prevVal, 100);
      } else {
        count += Math.floorDiv(prevVal - 1, 100) - Math.floorDiv(val - 1, 100);
      }
    }

    return count;
  }
}