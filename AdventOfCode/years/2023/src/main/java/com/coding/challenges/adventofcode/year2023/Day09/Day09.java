package com.coding.challenges.adventofcode.year2023.Day09;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Day09 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day09 day = new Day09();

    day.printPart1("sample-input", 114L);
    day.printPart1("input", 1782868781L);

    day.printPart2("sample-input", 2L);
    day.printPart2("input", 1057L);
  }

  @Override
  public Long part1(List<String> lines) {
    return lines.stream().mapToLong(line -> extrapolateHistory(line, false)).sum();
  }

  @Override
  public Long part2(List<String> lines) {
    return lines.stream().mapToLong(line -> extrapolateHistory(line, true)).sum();
  }

  public long extrapolateHistory(String line, boolean reversed) {
    List<List<Long>> histories = generateHistories(line);

    long val = 0;
    for (int i = histories.size() - 2; i >= 0; i--) {
      val =
          reversed
              ? histories.get(i).get(0) - val
              : histories.get(i).get(histories.get(i).size() - 1) + val;
    }

    return val;
  }

  private List<List<Long>> generateHistories(String line) {
    boolean reachedEnd = false;
    List<Long> history = Arrays.stream(line.split(" ")).mapToLong(Long::parseLong).boxed().toList();
    List<List<Long>> histories = new ArrayList<>();
    histories.add(history);

    while (!reachedEnd) {
      history = getNextHistory(history);
      histories.add(history);
      reachedEnd = hasReachedEnd(history);
    }
    return histories;
  }

  public List<Long> getNextHistory(List<Long> numbers) {
    List<Long> nextNumbers = new ArrayList<>();
    for (int i = 1; i < numbers.size(); i++) {
      nextNumbers.add(numbers.get(i) - numbers.get(i - 1));
    }
    return nextNumbers;
  }

  public boolean hasReachedEnd(List<Long> numbers) {
    return numbers.stream().allMatch(n -> n == 0);
  }
}
