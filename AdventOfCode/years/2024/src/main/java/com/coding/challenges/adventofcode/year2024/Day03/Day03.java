package com.coding.challenges.adventofcode.year2024.Day03;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day03 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day03 day = new Day03();

    day.printPart1("sample-input-1", 161L);
    day.printPart1("input", 168539636L);

    day.printPart2("sample-input-2", 48L);
    day.printPart2("input", 97529391L);
  }

  @Override
  public Long part1(List<String> lines) {
    Pattern pattern = Pattern.compile("mul\\(\\d+,\\d+\\)");
    return lines.stream()
        .map(pattern::matcher)
        .flatMap(
            m -> {
              List<String> matches = new ArrayList<>();
              while (m.find()) {
                matches.add(m.group());
              }
              return matches.stream();
            })
        .mapToLong(this::mul)
        .sum();
  }

  @Override
  public Long part2(List<String> lines) {
    Pattern pattern = Pattern.compile("(mul\\(\\d+,\\d+\\)|don't\\(\\)|do\\(\\))");
    List<String> instructions =
        lines.stream()
            .map(pattern::matcher)
            .flatMap(
                m -> {
                  List<String> matches = new ArrayList<>();
                  while (m.find()) {
                    matches.add(m.group());
                  }
                  return matches.stream();
                })
            .toList();
    long total = 0L;
    boolean activated = true;
    for (String line : instructions) {
      if (line.equals("do()")) {
        activated = true;
        continue;
      }
      if (line.equals("don't()")) {
        activated = false;
        continue;
      }
      if (activated) {
        total += mul(line);
      }
    }

    return total;
  }

  private long mul(String s) {
    Matcher m = Pattern.compile("\\d+").matcher(s);
    m.find();
    long a = Long.parseLong(m.group());
    m.find();
    long b = Long.parseLong(m.group());
    return a * b;
  }
}
