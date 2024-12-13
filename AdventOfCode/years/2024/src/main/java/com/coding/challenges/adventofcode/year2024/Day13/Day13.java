package com.coding.challenges.adventofcode.year2024.Day13;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import com.coding.challenges.adventofcode.utils.Day;

public class Day13 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day13 day = new Day13();

    day.printPart1("sample-input", 480L);
    day.printPart1("input", 32026L);

    day.printPart2("sample-input", 875318608908L);
    day.printPart2("input", 89013607072065L);
  }

  @Override
  public Long part1(List<String> lines) {
    return parseInput(lines, false).stream()
            .mapToLong(this::solveEquation)
            .sum();
  }

  @Override
  public Long part2(List<String> lines) {
    return parseInput(lines, true).stream()
                                   .mapToLong(this::solveEquation)
                                   .sum();
  }

  public long solveEquation(Input input) {
    double b = (input.buttonA.x * input.target.y - input.buttonA.y * input.target.x) / (double) (input.buttonA.x * input.buttonB.y - input.buttonA.y * input.buttonB.x);
    double a = (input.target.x - b * input.buttonB.x) / (double) input.buttonA.x;

    if (b != (long) b || a != (long) a || a < 0 || b < 0) {
      return 0L;
    }

    return (long) (a * 3L + b);
  }

  public List<Input> parseInput(List<String> lines, boolean isPart2) {
    List<Input> inputs = new ArrayList<>();
    for (int i = 0; i < lines.size(); i += 4) {
      Vector buttonA =
          Pattern.compile("Button A: X\\+(\\d+), Y\\+(\\d+)")
              .matcher(lines.get(i))
              .results()
              .map(
                  m -> {
                    long x = Long.parseLong(m.group(1));
                    long y = Long.parseLong(m.group(2));
                    return new Vector(x, y);
                  })
              .findFirst()
              .get();
      Vector buttonB =
          Pattern.compile("Button B: X\\+(\\d+), Y\\+(\\d+)")
              .matcher(lines.get(i + 1))
              .results()
              .map(
                  m -> {
                    long x = Long.parseLong(m.group(1));
                    long y = Long.parseLong(m.group(2));
                    return new Vector(x, y);
                  })
              .findFirst()
              .get();
      Vector target =
          Pattern.compile("Prize: X=(\\d+), Y=(\\d+)")
              .matcher(lines.get(i + 2))
              .results()
              .map(
                  m -> {
                    long x = Long.parseLong(m.group(1)) + (isPart2 ? 10_000_000_000_000L : 0);
                    long y = Long.parseLong(m.group(2)) + (isPart2 ? 10_000_000_000_000L : 0);;
                    return new Vector(x, y);
                  })
              .findFirst()
              .get();
      inputs.add(new Input(buttonA, buttonB, target));
    }

    return inputs;
  }

  public record Input(Vector buttonA, Vector buttonB, Vector target) {}

  public record Vector(long x, long y) {}
}
