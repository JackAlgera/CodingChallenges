package com.coding.challenges.adventofcode.year2022.Day04;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;

public class Day04 extends Day<Integer> {

  public static void main(String[] args) throws IOException {
    Day04 day = new Day04();

    day.printPart1("sample-input", 2);
    day.printPart1("input", 518);

    day.printPart2("sample-input", 4);
    day.printPart2("input", 909);
  }

  @Override
  public Integer part1(List<String> lines) {
    int totalOverlaps = 0;

    for (String line : lines) {
      String[] elfSections = line.split(",");
      int aStart = Integer.parseInt(elfSections[0].split("-")[0]);
      int aEnd = Integer.parseInt(elfSections[0].split("-")[1]);
      int bStart = Integer.parseInt(elfSections[1].split("-")[0]);
      int bEnd = Integer.parseInt(elfSections[1].split("-")[1]);

      if ((aStart <= bStart && bEnd <= aEnd) || (bStart <= aStart && aEnd <= bEnd)) {
        totalOverlaps++;
      }
    }

    return totalOverlaps;
  }

  @Override
  public Integer part2(List<String> lines) {
    int totalOverlaps = 0;
    for (String line : lines) {
      String[] elfSections = line.split(",");
      int aStart = Integer.parseInt(elfSections[0].split("-")[0]);
      int aEnd = Integer.parseInt(elfSections[0].split("-")[1]);
      int bStart = Integer.parseInt(elfSections[1].split("-")[0]);
      int bEnd = Integer.parseInt(elfSections[1].split("-")[1]);

      if (aEnd < bStart || bEnd < aStart) {
        continue;
      }

      totalOverlaps++;
    }

    return totalOverlaps;
  }
}
