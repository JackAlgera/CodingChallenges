package com.coding.challenges.adventofcode.year2020.Day7;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day7 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day7 day = new Day7();

    day.printPart1("sample-input-part1", 4L);
    day.printPart1("input", 233L);

    day.printPart2("sample-input-part1", 32L);
    day.printPart2("sample-input-part2", 126L);
    day.printPart2("input", 421550L);
  }

  @Override
  public Long part1(List<String> lines) {
    Map<String, Node> nodes = parseInput(lines);
    return nodes.values().stream().filter(node -> containsShinyGold(node, nodes)).count() - 1;
  }

  @Override
  public Long part2(List<String> lines) {
    Map<String, Node> nodes = parseInput(lines);
    return countBags(nodes.get("shiny gold"), nodes) - 1;
  }

  public long countBags(Node node, Map<String, Node> nodes) {
    return node.children.entrySet().stream()
        .map(child -> child.getValue() * countBags(nodes.get(child.getKey()), nodes))
        .reduce(1L, Long::sum);
  }

  public boolean containsShinyGold(Node node, Map<String, Node> nodes) {
    if (Objects.equals(node.type, "shiny gold")) {
      return true;
    }

    if (node.children().isEmpty()) {
      return false;
    }

    return node.children.keySet().stream()
        .map(nodes::get)
        .anyMatch(child -> containsShinyGold(child, nodes));
  }

  public Map<String, Node> parseInput(List<String> lines) {
    return lines.stream()
        .map(this::extractNode)
        .map(node -> Map.entry(node.type, node))
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
  }

  public Node extractNode(String line) {
    String type = line.substring(0, line.indexOf(" bags contain "));
    Map<String, Integer> children = new HashMap<>();
    for (String child : line.split(" bags contain ")[1].split(", ")) {
      if (child.contains("no other bags")) {
        continue;
      }
      String childType = child.replaceAll(" bags?\\.?", "").substring(2);
      int childWeight = Integer.parseInt(child.replaceAll(" bags?\\.?", "").substring(0, 1));
      children.put(childType, childWeight);
    }

    return new Node(type, children);
  }

  public record Node(String type, Map<String, Integer> children) {}
}
