package com.coding.challenges.adventofcode.year2023.Day2;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class Day2 extends Day<Integer> {

  public static final int MAX_RED = 12;
  public static final int MAX_GREEN = 13;
  public static final int MAX_BLUE = 14;

  public static void main(String[] args) throws IOException {
    Day2 day = new Day2();

    day.printPart1("sample-input", 8);
    day.printPart1("input", 2176);

    day.printPart2("sample-input", 2286);
    day.printPart2("input", 63700);
  }

  @Override
  public Integer part1(List<String> lines) {
    return lines.stream().mapToInt(this::playGamePart1).sum();
  }

  @Override
  public Integer part2(List<String> lines) {
    return lines.stream().mapToInt(this::playGamePart2).sum();
  }

  public int playGamePart1(String game) {
    for (String turn : game.split(":")[1].trim().split(";")) {
      for (String cube : turn.split(", ")) {
        int count = Integer.parseInt(cube.trim().split(" ")[0]);
        String color = cube.trim().split(" ")[1];
        if ((Objects.equals(color, "red") && count > MAX_RED)
            || (Objects.equals(color, "green") && count > MAX_GREEN)
            || (Objects.equals(color, "blue") && count > MAX_BLUE)) {
          return 0;
        }
      }
    }
    return Integer.parseInt(game.split(":")[0].split(" ")[1]);
  }

  public int playGamePart2(String game) {
    int minRed = 0;
    int minGreen = 0;
    int minBlue = 0;
    for (String turn : game.split(":")[1].trim().split(";")) {
      for (String cube : turn.split(", ")) {
        int count = Integer.parseInt(cube.trim().split(" ")[0]);
        String color = cube.trim().split(" ")[1];
        if (Objects.equals(color, "red") && count > minRed) {
          minRed = count;
        }
        if (Objects.equals(color, "green") && count > minGreen) {
          minGreen = count;
        }
        if (Objects.equals(color, "blue") && count > minBlue) {
          minBlue = count;
        }
      }
    }
    return minRed * minBlue * minGreen;
  }
}
