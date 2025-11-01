package com.coding.challenges.adventofcode.year2019.Day04;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;

public class Day04 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day04 day = new Day04();

    day.printPart1("sample-input", 4L);
    day.printPart1("input", 1955L);

    day.printPart2("sample-input", 3L);
    day.printPart2("input", 1319L);
  }

  @Override
  public Long part1(List<String> lines) {
    int min = Integer.parseInt(lines.get(0).split("-")[0]);
    int max = Integer.parseInt(lines.get(0).split("-")[1]);

    long total = 0;
    for (int i = min; i <= max; i++) {
      String digits = String.valueOf(i);
      boolean isIncreasing = true;
      boolean hasDouble = false;

      for (int k = 1; k < digits.length(); k++) {
        if (digits.charAt(k) < digits.charAt(k - 1)) {
          isIncreasing = false;
          break;
        }

        if (digits.charAt(k) == digits.charAt(k - 1)) {
          hasDouble = true;
        }
      }

      if (isIncreasing && hasDouble) {
        total++;
      }
    }

    return total;
  }

  @Override
  public Long part2(List<String> lines) {
    int min = Integer.parseInt(lines.get(0).split("-")[0]);
    int max = Integer.parseInt(lines.get(0).split("-")[1]);

    long total = 0;
    for (int i = min; i <= max; i++) {
      String digits = String.valueOf(i);
      boolean isIncreasing = true;
      boolean hasDouble = false;
      int grounCount = 1;

      for (int k = 1; k < digits.length(); k++) {
        if (digits.charAt(k) < digits.charAt(k - 1)) {
          isIncreasing = false;
          break;
        }

        if (digits.charAt(k) == digits.charAt(k - 1)) {
          grounCount++;
        } else {
          if (grounCount == 2) {
            hasDouble = true;
          }
          grounCount = 1;
        }
      }

      if (grounCount == 2) {
        hasDouble = true;
      }

      if (isIncreasing && hasDouble) {
        total++;
      }
    }

    return total;
  }
}
