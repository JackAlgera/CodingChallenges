package com.coding.challenges.adventofcode.year2022.Day06;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;

public class Day06 extends Day<Integer> {

  public static void main(String[] args) throws IOException {
    Day06 day = new Day06();

    day.printPart1("input", 1287);
    day.printPart2("input", 3716);
  }

  @Override
  public Integer part1(List<String> lines) {
    return part(lines, 4);
  }

  @Override
  public Integer part2(List<String> lines) {
    return part(lines, 14);
  }

  private int part(List<String> lines, int packetSize) {
    String line = lines.get(0);
    for (int i = packetSize - 1; i < line.length(); i++) {
      String subLine = line.substring(i - packetSize + 1, i + 1);

      if (checkChars(subLine)) {
        return i + 1;
      }
    }
    return -1;
  }

  private boolean checkChars(String sequence) {
    for (int i = 0; i < sequence.length(); i++) {
      char left = sequence.charAt(i);
      for (int j = i + 1; j < sequence.length(); j++) {
        char right = sequence.charAt(j);
        if (left == right) {
          return false;
        }
      }
    }

    return true;
  }
}
