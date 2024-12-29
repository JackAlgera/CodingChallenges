package com.coding.challenges.adventofcode.year2024.Day07;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day07 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day07 day = new Day07();

    day.printPart1("sample-input", 3749L);
    day.printPart1("input", 975671981569L);

    day.printPart2("sample-input", 11387L);
    day.printPart2("input", 223472064194845L);
  }

  @Override
  public Long part1(List<String> lines) {
    return lines.stream().mapToLong(line -> isValid(line, false)).sum();
  }

  @Override
  public Long part2(List<String> lines) {
    return lines.stream().mapToLong(line -> isValid(line, true)).sum();
  }

  private long isValid(String line, boolean isPart2) {
    long target = Long.parseLong(line.split(":")[0]);
    List<Integer> equation =
        Arrays.stream(line.split(":")[1].split(" "))
            .filter(e -> !e.isBlank())
            .map(Integer::parseInt)
            .toList();

    long closest = evaluate(equation, 0, target, 0L, isPart2);
    return closest == target ? target : 0L;
  }

  private long evaluate(List<Integer> equation, int i, long target, long current, boolean isPart2) {
    if (current > target) {
      return 0;
    }

    if (i == equation.size()) {
      return current;
    }

    long sum = evaluate(equation, i + 1, target, current + equation.get(i), isPart2);
    long multiply = evaluate(equation, i + 1, target, current * equation.get(i), isPart2);
    long concatenate =
        isPart2
            ? evaluate(
                equation,
                i + 1,
                target,
                Long.parseLong("%d%d".formatted(current, equation.get(i))),
                isPart2)
            : 0L;
    return Math.max(sum, Math.max(multiply, concatenate));
  }
}
