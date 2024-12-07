package com.coding.challenges.adventofcode.year2022.Day2;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Day2 extends Day<Integer> {

  // Rock: A, X, 1
  // Paper: B, Y, 2
  // Scissors: C, Z, 3
  private static final Map<String, Integer> POINTS_PART_1 =
      Map.of(
          "A X", 4, // 3 + 1,
          "A Y", 8, // 6 + 2,
          "A Z", 3, // 0 + 3,
          "B X", 1, // 0 + 1,
          "B Y", 5, // 3 + 2,
          "B Z", 9, // 6 + 3,
          "C X", 7, // 6 + 1,
          "C Y", 2, // 0 + 2,
          "C Z", 6 // 3 + 3
          );

  private static final Map<String, Integer> POINTS_PART_2 =
      Map.of(
          "A X", 3, // 0 + 3,
          "A Y", 4, // 3 + 1,
          "A Z", 8, // 6 + 2,
          "B X", 1, // 0 + 1,
          "B Y", 5, // 3 + 2,
          "B Z", 9, // 6 + 3,
          "C X", 2, // 0 + 2,
          "C Y", 6, // 3 + 3,
          "C Z", 7 // 6 + 1
          );

  public static void main(String[] args) throws IOException {
    Day2 day = new Day2();

    day.printPart1("sample-input", 15);
    day.printPart1("input", 10404);

    day.printPart2("sample-input", 12);
    day.printPart2("input", 10334);
  }

  @Override
  public Integer part1(List<String> lines) {
    return lines.stream().mapToInt(POINTS_PART_1::get).sum();
  }

  @Override
  public Integer part2(List<String> lines) {
    return lines.stream().mapToInt(POINTS_PART_2::get).sum();
  }
}
