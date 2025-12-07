package com.coding.challenges.adventofcode.year2025.Day07;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.InputLinesUtilities;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.Utilities;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;

public class Day07 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day07 day = new Day07();

    day.printPart1("sample-input", 21L);
    day.printPart1("input", 1562L);

    day.printPart2("sample-input", 40L);
    day.printPart2("input", 24292631346665L);
  }

  @Override
  public Long part1(List<String> lines) {
    var grid = InputLinesUtilities.extractGrid(lines);
    return fillGrid(grid);
  }

  @Override
  public Long part2(List<String> lines) {
    var grid = InputLinesUtilities.extractGrid(lines);
    fillGrid(grid);

    var nodes = new HashMap<Pos, Node>();

    // Create nodes top-down for '^' and 'S'
    for (int i = 0; i < grid.length; i += 2) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j] == '^' || grid[i][j] == 'S') {
          var pos = new Pos(i, j);
          nodes.computeIfAbsent(pos, p -> new Node(p.i(), p.j()));
        }
      }
    }

    // Link root
    var rootPos = InputLinesUtilities.findChar(grid, 'S');
    var root = nodes.get(new Pos(rootPos.i() + 2, rootPos.j()));

    // Link children top-down
    for (int i = 2; i < grid.length; i += 2) {
      for (int j = 0; j < grid[0].length; j++) {
        var pos = new Pos(i, j);
        if (!nodes.containsKey(pos)) {
          continue;
        }

        var node = nodes.get(pos);
        // Find left child
        findAndLinkChild(node, i + 2, j - 1, grid.length, nodes);
        // Find right child
        findAndLinkChild(node, i + 2, j + 1, grid.length, nodes);
      }
    }

    return countPaths(root, new HashMap<>());
  }

  private void findAndLinkChild(
      Node parent, int childI, int childJ, int maxI, Map<Pos, Node> nodes) {
    while (childI < maxI) {
      if (nodes.containsKey(new Pos(childI, childJ))) {
        boolean isLeft = childJ < parent.j;
        if (isLeft) {
          parent.setLeft(nodes.get(new Pos(childI, childJ)));
        } else {
          parent.setRight(nodes.get(new Pos(childI, childJ)));
        }
        break;
      }
      childI += 2;
    }
  }

  private void splitBean(char[][] grid, int i, int j) {
    int maxI = grid.length;
    int maxJ = grid[0].length;
    // Left beam
    if (Utilities.isValidIndex(i, j - 1, maxI, maxJ) && grid[i][j - 1] == '.') {
      grid[i][j - 1] = '|';
    }
    // Right beam
    if (Utilities.isValidIndex(i, j + 1, maxI, maxJ) && grid[i][j + 1] == '.') {
      grid[i][j + 1] = '|';
    }
  }

  private long countPaths(Node node, Map<Node, Long> dp) {
    if (node == null) {
      return 1L;
    }

    if (dp.containsKey(node)) return dp.get(node);
    long pathCount = countPaths(node.left, dp) + countPaths(node.right, dp);
    dp.put(node, pathCount);

    return pathCount;
  }

  private long fillGrid(char[][] grid) {
    long splitCount = 0;

    for (int i = 1; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        char previousChar = grid[i - 1][j];
        if (previousChar != '|' && previousChar != 'S') {
          continue;
        }

        char currentChar = grid[i][j];
        if (currentChar == '.') {
          grid[i][j] = '|';
          continue;
        }

        if (currentChar == '^') {
          splitBean(grid, i, j);
          splitCount++;
        }
      }
    }

    return splitCount;
  }

  @AllArgsConstructor
  @Data
  @EqualsAndHashCode
  static class Node {
    int i, j;
    Node left, right;

    public Node(int i, int j) {
      this(i, j, null, null);
    }

    @Override
    public String toString() {
      return "{(%s,%s) -> [(%s), (%s)]}"
          .formatted(
              i,
              j,
              left == null ? "null" : left.i + "," + left.j,
              right == null ? "null" : right.i + "," + right.j);
    }
  }
}
