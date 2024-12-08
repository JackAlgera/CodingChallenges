package com.coding.challenges.adventofcode.year2020.Day19;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day19 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day19 day = new Day19();

    day.printPart1("part1-sample-input-1", 2L);
    day.printPart1("part1-sample-input-2", 3L);
    day.printPart1("part1-input", 187L);

    day.printPart2("part2-sample-input", 12L);
    day.printPart2("part2-input", 0L);
    // 401 too high
    // 412 too high
    // 306 nope
    // 395 nope
    // 394 nope
    // 393 nope
    // 396 nope
  }

  @Override
  public Long part1(List<String> lines) {
    Map<Integer, String> rules = extractRules(lines);
    return lines.stream()
        .skip(rules.size() + 1)
        .filter(line -> line.matches(extractRegex(rules, 0)))
        .count();
  }

  @Override
  public Long part2(List<String> lines) {
    return 0L;
  }

  private Map<Integer, String> extractRules(List<String> lines) {
    int i = 0;
    Map<Integer, String> rules = new HashMap<>();

    while (!lines.get(i).isEmpty()) {
      rules.put(
          Integer.parseInt(lines.get(i).split(":")[0]),
          lines.get(i).split(":")[1].trim().replace("\"", ""));
      i++;
    }

    return rules;
  }

  private String extractRegex(Map<Integer, String> rules, int i) {
    if (rules.get(i).equals("a") || rules.get(i).equals("b")) {
      return rules.get(i);
    }

    String rule = rules.get(i);
    if (!rule.matches(".*\\d.*")) {
      return rule;
    }

    String regex = "(";
    for (String r : rule.split(" ")) {
      if (r.equals("|")) {
        regex += "|";
      } else {
        regex += extractRegex(rules, Integer.parseInt(r));
      }
    }
    regex += ")";

    rules.put(i, regex);
    return regex;
  }
}
