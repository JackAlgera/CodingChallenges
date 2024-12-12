package com.coding.challenges.adventofcode.year2024.Day11;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import com.coding.challenges.adventofcode.utils.Day;

public class Day11 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day11 day = new Day11();

    day.printPart1("sample-input", 55312L);
    day.printPart1("input", 224529L);

    day.printPart2("sample-input", 65601038650482L);
    day.printPart2("input", 266820198587914L);
  }

  @Override
  public Long part1(List<String> lines) {
    return blinks(lines, 25);
  }

  @Override
  public Long part2(List<String> lines) {
    return blinks(lines, 75);
  }

  public Long blinks(List<String> lines, int count) {
    Map<Long, Long> stoneMap =
            Arrays.stream(lines.getFirst().split(" "))
                  .map(Long::parseLong)
                  .collect(Collectors.toMap(Function.identity(), i -> 1L, (a, b) -> 2L));

    for (int i = 0; i < count; i++) {
      stoneMap = blink(stoneMap);
    }
    return stoneMap.values().stream().mapToLong(Long::longValue).sum();
  }

  public Map<Long, Long> blink(Map<Long, Long> stoneMap) {
    Map<Long, Long> newStoneMap = new HashMap<>();

    for (Long stone : stoneMap.keySet()) {
      long count = stoneMap.get(stone);
      if (count == 0L) {
        continue;
      }
      String stringValue = String.valueOf(stone);

      if (stone == 0L) {
        newStoneMap.compute(1L, (k, v) -> v == null ? count : v + count);
      } else if (stringValue.length() % 2 == 0) {
        Long left = Long.valueOf(stringValue.substring(0, stringValue.length() / 2));
        Long right = Long.valueOf(stringValue.substring(stringValue.length() / 2));
        newStoneMap.compute(left, (k, v) -> v == null ? count : v + count);
        newStoneMap.compute(right, (k, v) -> v == null ? count : v + count);
      } else {
        newStoneMap.compute(stone * 2024L, (k, v) -> v == null ? count : v + count);
      }
    }
    return newStoneMap;
  }
}
