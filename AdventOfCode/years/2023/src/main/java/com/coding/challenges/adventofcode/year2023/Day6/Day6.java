package com.coding.challenges.adventofcode.year2023.Day6;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

public class Day6 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day6 day = new Day6();

    day.printPart1("sample-input", 288L);
    day.printPart1("input", 3317888L);

    day.printPart2("sample-input", 71503L);
    day.printPart2("input", 24655068L);
  }

  @Override
  public Long part1(List<String> lines) {
    List<Integer> times = parseInputPart1(lines, 0);
    List<Integer> distances = parseInputPart1(lines, 1);

    return IntStream.range(0, times.size())
        .mapToLong(i -> countWins(times.get(i), distances.get(i)))
        .reduce(1L, (a, b) -> a * b);
  }

  @Override
  public Long part2(List<String> lines) {
    return countWins(parseInputPart2(lines, 0), parseInputPart2(lines, 1));
  }

  private static long countWins(long time, long distance) {
    return LongStream.range(0, time).map(t -> t * (time - t)).filter(d -> d > distance).count();
  }

  private static List<Integer> parseInputPart1(List<String> lines, int index) {
    return Arrays.stream(lines.get(index).split(":")[1].trim().split("[ ]+"))
        .mapToInt(Integer::parseInt)
        .boxed()
        .toList();
  }

  private static long parseInputPart2(List<String> lines, int index) {
    return Long.parseLong(lines.get(index).split(":")[1].replace(" ", ""));
  }
}
