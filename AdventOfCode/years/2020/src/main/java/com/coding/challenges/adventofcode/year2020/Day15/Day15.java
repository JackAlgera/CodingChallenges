package com.coding.challenges.adventofcode.year2020.Day15;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day15 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day15 day = new Day15();

    day.printPart1("sample-input", 436L);
    day.printPart1("input", 1238L);

    day.printPart2("sample-input", 175594L);
    day.printPart2("input", 3745954L);
  }

  @Override
  public Long part1(List<String> lines) {
    return extractNumberAfter(lines, 2020L);
  }

  @Override
  public Long part2(List<String> lines) {
    return extractNumberAfter(lines, 30000000L);
  }

  private static long extractNumberAfter(List<String> lines, long lastNumberIndex) {
    List<Long> numbers = Arrays.stream(lines.get(0).split(",")).map(Long::parseLong).toList();
    int turn = 0;
    Map<Long, SpokenNumber> spokenNumbers = new HashMap<>();
    while (turn < numbers.size()) {
      spokenNumbers.put(numbers.get(turn), new SpokenNumber(numbers.get(turn), turn + 1, 0));
      turn++;
    }

    SpokenNumber currentSpokenNumber = spokenNumbers.get(numbers.get(turn - 1));
    while (turn < lastNumberIndex) {
      final int nextTurn = turn + 1;
      long nextNumber = 0L;
      if (currentSpokenNumber.lastLastTurn() != 0) {
        nextNumber = currentSpokenNumber.lastTurn() - currentSpokenNumber.lastLastTurn();
      }
      spokenNumbers.compute(
          nextNumber,
          (k, v) ->
              v == null
                  ? new SpokenNumber(k, nextTurn, 0)
                  : new SpokenNumber(k, nextTurn, spokenNumbers.get(k).lastTurn()));
      currentSpokenNumber = spokenNumbers.get(nextNumber);
      turn++;
    }

    return currentSpokenNumber.number;
  }

  public record SpokenNumber(long number, long lastTurn, long lastLastTurn) {}
}
