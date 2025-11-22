package com.coding.challenges.adventofcode.year2019.Day06;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

public class Day06 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day06 day = new Day06();

    day.printPart1("sample-input-1", 42L);
    day.printPart1("input", 223251L);

    day.printPart2("sample-input-2", 4L);
    day.printPart2("input", 430L);
  }

  @Override
  public Long part1(List<String> lines) {
    var root = computeNodes(lines).get("COM");
    return computeOrbits(root, 0);
  }

  @Override
  public Long part2(List<String> lines) {
    var nodes = computeNodes(lines);

    var youParents = getParents(nodes, "YOU");
    var sanParents = getParents(nodes, "SAN");

    for (int i = 0; i < sanParents.size(); i++) {
      int youIndex = youParents.indexOf(sanParents.get(i));
      if (youIndex != -1) {
        return (long) (i + youIndex);
      }
    }
    return -1L;
  }

  public List<String> getParents(Map<String, Node> nodes, String nodeVal) {
    var node = nodes.get(nodeVal).getParent();
    var parents = new ArrayList<String>();
    while (node != null) {
      parents.add(node.getVal());
      node = node.getParent();
    }
    return parents;
  }

  public Map<String, Node> computeNodes(List<String> lines) {
    Map<String, Node> nodes = new HashMap<>();

    for (String input : lines) {
      var left = input.split("\\)")[0];
      var right = input.split("\\)")[1];

      var leftNode = nodes.computeIfAbsent(left, Node::new);
      var rightNode = nodes.computeIfAbsent(right, Node::new);
      rightNode.setParent(leftNode);
      leftNode.addOrbit(rightNode);
    }

    return nodes;
  }

  @AllArgsConstructor
  @Data
  public class Node {
    private String val;
    private Node parent;
    private List<Node> orbits;

    public Node(String val) {
      this(val, null, new ArrayList<>());
    }

    public void addOrbit(Node newNode) {
      orbits.add(newNode);
    }
  }

  public long computeOrbits(Node node, int depth) {
    if (node.getOrbits().isEmpty()) {
      return depth;
    }

    return depth + node.getOrbits().stream().mapToLong(n -> computeOrbits(n, depth + 1)).sum();
  }
}
