package com.coding.challenges.adventofcode.year2022.Day21;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day21 {

  private static final String INPUT_NAME = "Year2022/Day21/input.txt";

  public static void main(String[] args) throws Exception {
    Day21 day = new Day21();
    day.part1();
    day.part2();
  }

  private void part1() throws Exception {
    List<String> lines = Files.readAllLines(Path.of(INPUT_NAME));

    Map<String, Monkey> monkeys =
        lines.stream().collect(Collectors.toMap(m -> m.split(":")[0], this::extractMonkey));

    System.out.println("\n-------- Day 21 - part 1 --------");
    System.out.println("Root monkey value: " + getValue(monkeys.get("root"), monkeys));
    System.out.println("Expected value: " + 145167969204648L);
  }

  private void part2() throws Exception {
    List<String> lines = Files.readAllLines(Path.of(INPUT_NAME));

    Map<String, Monkey> monkeys =
        lines.stream().collect(Collectors.toMap(m -> m.split(":")[0], this::extractMonkey));

    boolean foundSolution = false;
    long leftBoundary = 0;
    long rightBoundary = Long.MAX_VALUE / 2;
    long val = rightBoundary;

    while (!foundSolution) {
      monkeys.put("humn", new Monkey("humn", val, null, null, null));
      long leftVal = getValue(monkeys.get(monkeys.get("root").leftId()), monkeys);
      long rightVal = getValue(monkeys.get(monkeys.get("root").rightId()), monkeys);

      if (leftVal == rightVal) {
        System.out.println("\n-------- Day 21 - part 2 --------");
        System.out.println("Found val for humn: " + val);
        System.out.println("Expected val for humn: 3330805295850");
        foundSolution = true;
      }

      if (leftVal < 0) {
        rightBoundary = val;
        val /= 2;
        continue;
      }

      if (leftVal < rightVal) {
        leftBoundary = val;
        val += (rightBoundary - val) / 2;
      } else {
        rightBoundary = val;
        val -= (val - leftBoundary) / 2;
      }
    }
  }

  public Long getValue(Monkey monkey, Map<String, Monkey> monkeys) throws Exception {
    if (monkey.val() != null) {
      return monkey.val();
    }

    Monkey leftMonkey = monkeys.get(monkey.leftId());
    Monkey rightMonkey = monkeys.get(monkey.rightId());

    switch (monkey.action()) {
      case "+":
        return getValue(leftMonkey, monkeys) + getValue(rightMonkey, monkeys);
      case "-":
        return getValue(leftMonkey, monkeys) - getValue(rightMonkey, monkeys);
      case "*":
        return getValue(leftMonkey, monkeys) * getValue(rightMonkey, monkeys);
      case "/":
        return getValue(leftMonkey, monkeys) / getValue(rightMonkey, monkeys);
    }

    throw new Exception();
  }

  public Monkey extractMonkey(String line) {
    String id = line.split(":")[0];
    if (line.matches(".*\\d.*")) {
      return new Monkey(id, Long.parseLong(line.split(":")[1].strip()), null, null, null);
    }

    return new Monkey(
        id, null, line.substring(6, 10), line.substring(13, 17), line.substring(11, 12));
  }

  public record Monkey(String id, Long val, String leftId, String rightId, String action) {}
}
