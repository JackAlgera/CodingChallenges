package com.coding.challenges.adventofcode.year2023.Day19;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.With;

public class Day19 extends Day<Long> {

  private record Range(int min, int max) {}

  public static void main(String[] args) throws IOException {
    Day19 day = new Day19();

    day.printPart1("sample-input", 19_114L);
    day.printPart1("input", 432_427L);

    day.printPart2("sample-input", 167_409_079_868_000L);
    day.printPart2("input", 143_760_172_569_135L);
  }

  @Override
  public Long part1(List<String> lines) {
    Map<String, RuleSet> ruleSets = parseRuleSets(lines);
    List<PartRanges> partRangesList = new ArrayList<>();
    boolean addingParts = false;
    for (int i = 0; i < lines.size(); i++) {
      if (lines.get(i).isBlank()) {
        addingParts = true;
        continue;
      }
      if (addingParts) {
        int[] parts =
            Arrays.stream(lines.get(i).substring(1, lines.get(i).length() - 1).split(","))
                .map(s -> s.split("=")[1])
                .mapToInt(Integer::parseInt)
                .toArray();

        partRangesList.add(
            new PartRanges(
                new Range(parts[0], parts[0]),
                new Range(parts[1], parts[1]),
                new Range(parts[2], parts[2]),
                new Range(parts[3], parts[3])));
      }
    }

    return partRangesList.stream()
        .map(partRanges -> applyRules(partRanges, ruleSets.get("in"), ruleSets, true))
        .reduce(0L, Long::sum);
  }

  @Override
  public Long part2(List<String> lines) {
    Map<String, RuleSet> ruleSets = parseRuleSets(lines);
    return applyRules(
        new PartRanges(
            new Range(1, 4000), new Range(1, 4000), new Range(1, 4000), new Range(1, 4000)),
        ruleSets.get("in"),
        ruleSets,
        false);
  }

  public long applyRules(
      PartRanges partRanges, RuleSet current, Map<String, RuleSet> ruleSets, boolean part1) {
    if (!partRanges.isValid()) {
      return 0L;
    }
    if (current.name().equals("A")) {
      return part1 ? partRanges.solvePart1() : partRanges.solvePart2();
    }
    if (current.name().equals("R")) {
      return 0L;
    }

    long combinations = 0L;
    // Invalid ranges here, used in the final applyRules that get's updated inside the for loop
    Range x = partRanges.x;
    Range m = partRanges.m;
    Range a = partRanges.a;
    Range s = partRanges.s;

    for (Rule rule : current.rules()) {
      // Valid ranges here
      PartRanges newPartRanges = new PartRanges(x, m, a, s);
      if (rule.checkLessThan) {
        switch (rule.part) {
          case "x" -> {
            newPartRanges =
                newPartRanges.withX(new Range(x.min(), Math.min(x.max(), rule.threshold - 1)));
            x = new Range(Math.max(x.min(), rule.threshold), x.max());
          }
          case "m" -> {
            newPartRanges =
                newPartRanges.withM(new Range(m.min(), Math.min(m.max(), rule.threshold - 1)));
            m = new Range(Math.max(m.min(), rule.threshold), m.max());
          }
          case "a" -> {
            newPartRanges =
                newPartRanges.withA(new Range(a.min(), Math.min(a.max(), rule.threshold - 1)));
            a = new Range(Math.max(a.min(), rule.threshold), a.max());
          }
          case "s" -> {
            newPartRanges =
                newPartRanges.withS(new Range(s.min(), Math.min(s.max(), rule.threshold - 1)));
            s = new Range(Math.max(s.min(), rule.threshold), s.max());
          }
        }
      } else {
        switch (rule.part) {
          case "x" -> {
            newPartRanges =
                newPartRanges.withX(new Range(Math.max(x.min(), rule.threshold + 1), x.max()));
            x = new Range(x.min(), Math.min(x.max(), rule.threshold));
          }
          case "m" -> {
            newPartRanges =
                newPartRanges.withM(new Range(Math.max(m.min(), rule.threshold + 1), m.max()));
            m = new Range(m.min(), Math.min(m.max(), rule.threshold));
          }
          case "a" -> {
            newPartRanges =
                newPartRanges.withA(new Range(Math.max(a.min(), rule.threshold + 1), a.max()));
            a = new Range(a.min(), Math.min(a.max(), rule.threshold));
          }
          case "s" -> {
            newPartRanges =
                newPartRanges.withS(new Range(Math.max(s.min(), rule.threshold + 1), s.max()));
            s = new Range(s.min(), Math.min(s.max(), rule.threshold));
          }
        }
      }

      combinations += applyRules(newPartRanges, ruleSets.get(rule.sendTo), ruleSets, part1);
    }
    combinations +=
        applyRules(new PartRanges(x, m, a, s), ruleSets.get(current.sendTo()), ruleSets, part1);

    return combinations;
  }

  public Map<String, RuleSet> parseRuleSets(List<String> lines) {
    int i = 0;
    Map<String, RuleSet> ruleSets = new HashMap<>();
    ruleSets.put("A", new RuleSet("A", List.of(), null));
    ruleSets.put("R", new RuleSet("R", List.of(), null));
    while (i < lines.size()) {
      if (lines.get(i).isBlank()) {
        break;
      } else {
        RuleSet ruleSet = parseRuleSet(lines.get(i));
        ruleSets.put(ruleSet.name(), ruleSet);
      }
      i++;
    }
    return ruleSets;
  }

  public RuleSet parseRuleSet(String line) {
    String name = line.split("\\{")[0];
    List<Rule> rules = new ArrayList<>();
    String[] rulesStr = line.split("\\{")[1].replaceAll("[{}]", "").split(",");
    String sendTo = "";
    for (String ruleStr : rulesStr) {
      if (ruleStr.contains(":")) {
        int threshold = Integer.parseInt(ruleStr.split(":")[0].substring(2));
        rules.add(
            new Rule(
                ruleStr.substring(0, 1), threshold, ruleStr.contains("<"), ruleStr.split(":")[1]));
      } else {
        sendTo = ruleStr;
      }
    }
    return new RuleSet(name, rules, sendTo);
  }

  public record RuleSet(String name, List<Rule> rules, String sendTo) {}

  public record Rule(String part, int threshold, boolean checkLessThan, String sendTo) {}

  @With
  public record PartRanges(Range x, Range m, Range a, Range s) {
    public boolean isValid() {
      return x.min() <= x.max() && m.min() <= m.max() && a.min() <= a.max() && s.min() <= s.max();
    }

    public long solvePart2() {
      return (long) (x.max() - x.min() + 1)
          * (m.max() - m.min() + 1)
          * (a.max() - a.min() + 1)
          * (s.max() - s.min() + 1);
    }

    public long solvePart1() {
      return x.min() + m.min() + a.min() + s.min();
    }
  }
}
