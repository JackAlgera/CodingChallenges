package com.coding.challenges.adventofcode.year2024.Day19;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day19 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day19 day = new Day19();

    day.printPart1("sample-input", 6L);
    day.printPart1("input", 374L);

    day.printPart2("sample-input", 16L);
    day.printPart2("input", 1100663950563322L);
  }

  @Override
  public Long part1(List<String> lines) {
    Set<String> towels = Arrays.stream(lines.get(0).split(", ")).collect(Collectors.toSet());

    return lines.stream()
        .skip(2)
        .map(design -> isPossible(towels, design))
        .mapToLong(i -> i ? 1 : 0)
        .sum();
  }

  @Override
  public Long part2(List<String> lines) {
    Set<String> towels = Arrays.stream(lines.get(0).split(", ")).collect(Collectors.toSet());
    Map<String, Long> dp = new HashMap<>();

    return lines.stream().skip(2).mapToLong(design -> addPossible(towels, design, dp)).sum();
  }

  public long addPossible(Set<String> towels, String design, Map<String, Long> dp) {
    if (design.isBlank()) {
      return 1L;
    }

    if (dp.containsKey(design)) {
      return dp.get(design);
    }

    long total = 0;
    for (String towel : towels) {
      if (design.startsWith(towel)) {
        total += addPossible(towels, design.substring(towel.length()), dp);
      }
    }

    dp.put(design, total);
    return total;
  }

  public boolean isPossible(Set<String> towels, String design) {
    if (design.isBlank()) {
      return true;
    }

    boolean isPossible = false;
    for (String towel : towels) {
      if (design.startsWith(towel)) {
        isPossible = isPossible || isPossible(towels, design.substring(towel.length()));
      }
    }

    return isPossible;
  }
}
