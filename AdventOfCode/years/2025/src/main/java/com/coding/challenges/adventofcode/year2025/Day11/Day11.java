package com.coding.challenges.adventofcode.year2025.Day11;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day11 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day11 day = new Day11();

    day.printPart1("sample-input-1", 5L);
    day.printPart1("input", 662L);

    day.printPart2("sample-input-2", 2L);
    day.printPart2("input", 429399933071120L);
  }

  @Override
  public Long part1(List<String> lines) {
    var nodes = parseInput(lines);
    return countPaths(
        nodes.get("you"), nodes.get("out"), Set.of(), new HashSet<>(), new HashMap<>());
  }

  @Override
  public Long part2(List<String> lines) {
    var nodes = parseInput(lines);
    return countPaths(
        nodes.get("svr"), nodes.get("out"), Set.of("dac", "fft"), new HashSet<>(), new HashMap<>());
  }

  private long countPaths(
      Node start,
      Node end,
      Set<String> requiredNodes,
      Set<String> foundNodes,
      Map<NodeState, Long> dp) {
    var key = new NodeState(start.id, Set.copyOf(foundNodes));

    if (dp.containsKey(key)) return dp.get(key);
    if (start == end) return foundNodes.containsAll(requiredNodes) ? 1L : 0L;

    long totalPaths = 0L;
    for (var child : start.children) {
      var newFoundNodes = new HashSet<>(foundNodes);
      if (requiredNodes.contains(child.id)) {
        newFoundNodes.add(child.id);
      }
      totalPaths += countPaths(child, end, requiredNodes, newFoundNodes, dp);
    }
    dp.put(key, totalPaths);
    return totalPaths;
  }

  private Map<String, Node> parseInput(List<String> lines) {
    var nodes = new HashMap<String, Node>();
    nodes.put("out", new Node("out"));

    for (String line : lines) {
      var node = nodes.computeIfAbsent(line.split(":")[0], Node::new);
      for (String childId : line.split(":")[1].strip().split(" ")) {
        var childNode = nodes.computeIfAbsent(childId, Node::new);
        node.children.add(childNode);
      }
    }

    return nodes;
  }

  record Node(String id, List<Node> children) {
    public Node(String id) {
      this(id, new ArrayList<>());
    }
  }

  record NodeState(String nodeId, Set<String> nodesFound) {}
}
