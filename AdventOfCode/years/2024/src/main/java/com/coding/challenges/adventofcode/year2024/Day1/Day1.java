package com.coding.challenges.adventofcode.year2024.Day1;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day1 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day1 day = new Day1();

    day.printPart1("sample-input", 11L);
    day.printPart1("input", 2176849L);

    day.printPart2("sample-input", 31L);
    day.printPart2("input", 23384288L);
  }

  @Override
  public Long part1(List<String> lines) {
    List<Integer> left = new ArrayList<>();
    List<Integer> right = new ArrayList<>();
    for (String line : lines) {
      left.add(Integer.parseInt(line.split(" {3}")[0]));
      right.add(Integer.parseInt(line.split(" {3}")[1]));
    }
    Collections.sort(left);
    Collections.sort(right);
    long total = 0L;
    for (int i = 0; i < left.size(); i++) {
      total += Math.abs(left.get(i) - right.get(i));
    }
    return total;
  }

  @Override
  public Long part2(List<String> lines) {
    List<Integer> left = new ArrayList<>();
    Map<Integer, Long> right = new HashMap<>();
    for (String line : lines) {
      left.add(Integer.parseInt(line.split(" {3}")[0]));
      right.compute(Integer.parseInt(line.split(" {3}")[1]), (k, v) -> v == null ? 1 : v + 1);
    }
    return left.stream().mapToLong(n -> right.getOrDefault(n, 0L) * n).sum();
  }
}
