package com.coding.challenges.adventofcode.year2024.Day24;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day24 extends Day<String> {

  public static void main(String[] args) throws IOException {
    Day24 day = new Day24();

    day.printPart1("sample-input-1", "4");
    day.printPart1("sample-input-2", "2024");
    day.printPart1("input", "45923082839246");

    day.printPart2("input", "jgb,rkf,rrs,rvc,vcg,z09,z20,z24");
  }

  @Override
  public String part1(List<String> lines) {
    Map<String, Boolean> gates = extractGates(lines);
    List<Rule> rules = extractRules(lines);
    return "" + applyRules(rules, gates);
  }

  @Override
  public String part2(List<String> lines) {
    List<Rule> rules = extractRules(lines);
    List<Rule> invalidRules = new ArrayList<>();
    for (Rule rule : rules) {
      // 1. All XOR gates that input x and y cannot every output z (excluding x00,y00)
      if (rule.isXOR() && rule.inputXAndY() && rule.outputZ()) {
        if (!rule.isFirstBitXY()) {
          invalidRules.add(rule);
        }
      }

      // 2. All other XOR gates must output z
      if (rule.isXOR() && !rule.inputXAndY() && !rule.outputZ()) {
        invalidRules.add(rule);
      }

      // 3. All gates that output z must be XOR (except for z45)
      if (rule.outputZ() && !rule.isXOR()) {
        if (!rule.g3.equals("z45")) {
          invalidRules.add(rule);
        }
      }
    }
    // All invalid rules are checked visually and swapped to make them valid (6 first run)
    //    printRules(rules);
    // Then with the one bit that was wrong, I was able to visually see where the error was and fix
    // it
    Set<String> wrongBits = wrongBits(rules, lines);

    return List.of("z24", "vcg", "z20", "jgb", "z09", "rkf", "rvc", "rrs").stream()
        .sorted()
        .collect(Collectors.joining(","));
  }

  /**
   * Print rules with colors that fits the .dot format to be used with Graphviz
   *
   * @param rules List of rules
   */
  public void printRules(List<Rule> rules) {
    System.out.println();
    for (Rule rule : rules) {
      String color = "[color=%s]";
      switch (rule.logic) {
        case "AND" -> color = color.formatted("blue");
        case "OR" -> color = color.formatted("red");
        case "XOR" -> color = color.formatted("green");
      }
      System.out.printf("%s -> %s %s ", rule.g1, rule.g3, color);
      System.out.printf("%s -> %s %s ", rule.g2, rule.g3, color);
    }
  }

  public Set<String> wrongBits(List<Rule> rules, List<String> lines) {
    Set<String> wrongBits = new HashSet<>();
    for (int k = 0; k < 45; k++) {
      Map<String, Boolean> gates = extractGates(lines);
      for (int i = 0; i < 45; i++) {
        gates.put("x%02d".formatted(i), false);
        gates.put("y%02d".formatted(i), false);
        if (i == k) {
          gates.put("x%02d".formatted(i), true);
          gates.put("y%02d".formatted(i), true);
        }
      }
      applyRules(rules, gates);

      long z = getNumericalValue("z", gates);
      if (z != 2L << k) {
        wrongBits.add("z%02d".formatted(k));
      }
    }

    return wrongBits;
  }

  public long applyRules(List<Rule> rules, Map<String, Boolean> gates) {
    List<Rule> copiedRules = new ArrayList<>(rules);
    while (!copiedRules.isEmpty()) {
      List<Rule> rulesToRemove = new ArrayList<>();
      for (Rule rule : copiedRules) {
        if (!gates.containsKey(rule.g1) || !gates.containsKey(rule.g2)) {
          continue;
        }

        switch (rule.logic) {
          case "AND" -> gates.put(rule.g3, gates.get(rule.g1) && gates.get(rule.g2));
          case "OR" -> gates.put(rule.g3, gates.get(rule.g1) || gates.get(rule.g2));
          case "XOR" -> gates.put(rule.g3, gates.get(rule.g1) ^ gates.get(rule.g2));
        }

        rulesToRemove.add(rule);
      }
      if (rulesToRemove.isEmpty()) {
        // Unable to apply all rules, invalid structure
        return -1;
      }
      copiedRules.removeAll(rulesToRemove);
    }

    return getNumericalValue("z", gates);
  }

  public long getNumericalValue(String prefix, Map<String, Boolean> gates) {
    long value = 0;
    for (int i = 0; i <= 45; i++) {
      String z = "%s%02d".formatted(prefix, i);
      if (gates.containsKey(z) && gates.get("%s%02d".formatted(prefix, i))) {
        value += (long) Math.pow(2, i);
      }
    }
    return value;
  }

  public Map<String, Boolean> extractGates(List<String> lines) {
    Map<String, Boolean> gates = new HashMap<>();
    int i = 0;
    while (i < lines.size()) {
      String gate = lines.get(i);

      if (gate.isBlank()) {
        i++;
        break;
      }

      gates.put(gate.split(": ")[0], gate.split(": ")[1].equals("1"));
      i++;
    }

    return gates;
  }

  public List<Rule> extractRules(List<String> lines) {
    List<Rule> rules = new ArrayList<>();
    int i = 0;
    while (i < lines.size()) {
      if (lines.get(i).isBlank()) {
        i++;
        break;
      }
      i++;
    }

    while (i < lines.size()) {
      rules.add(extractRule(lines.get(i)));
      i++;
    }

    return rules;
  }

  public Rule extractRule(String ruleStr) {
    return Pattern.compile("(.*) (AND|OR|XOR) (.*) -> (.*)")
        .matcher(ruleStr)
        .results()
        .map(r -> new Rule(r.group(1), r.group(3), r.group(2), r.group(4)))
        .findFirst()
        .get();
  }

  public record Rule(String g1, String g2, String logic, String g3) {
    @Override
    public String toString() {
      return "%s %s %s -> %s".formatted(g1, logic, g2, g3);
    }

    boolean isXOR() {
      return logic.equals("XOR");
    }

    boolean inputXAndY() {
      return (g1.startsWith("x") || g1.startsWith("y"))
          && (g2.startsWith("x") || g2.startsWith("y"));
    }

    boolean outputZ() {
      return g3.startsWith("z");
    }

    boolean isFirstBitXY() {
      return (g1.equals("x00") || g1.equals("y00")) && (g2.equals("x00") || g2.equals("y00"));
    }
  }
}
