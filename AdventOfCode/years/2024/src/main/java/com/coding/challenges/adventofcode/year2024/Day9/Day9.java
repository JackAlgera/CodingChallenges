package com.coding.challenges.adventofcode.year2024.Day9;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;

public class Day9 extends Day<Long> {

  private static final int EMPTY_ID = -1;

  public static void main(String[] args) throws IOException {
    Day9 day = new Day9();

    day.printPart1("sample-input", 1928L);
    day.printPart1("input", 6334655979668L);

    day.printPart2("sample-input", 2858L);
    day.printPart2("input", 6349492251099L);
  }

  @Override
  public Long part1(List<String> lines) {
    return day(lines, true);
  }

  @Override
  public Long part2(List<String> lines) {
    return day(lines, false);
  }

  private long day(List<String> lines, boolean isPart1) {
    List<Block> blocks = extractBlocks(lines);
    Map<Integer, Node> nodeMap = buildNodes(blocks);
    Node root = nodeMap.get(blocks.getFirst().id());

    for (int i = blocks.size() - 1; i >= 0; i--) {
      Block block = blocks.get(i);
      if (block.id() == EMPTY_ID) {
        continue;
      }
      Node end = nodeMap.get(block.id());
      if (end.getId() == EMPTY_ID) {
        continue;
      }

      Node current = root;
      while (current.getId() != end.getId()) {
        if (current.getId() != EMPTY_ID) {
          current = current.getRight();
          continue;
        }

        if (current.getSize() == end.getSize()) {
          current.setId(end.getId());
          current.setSize(end.getSize());
          end.setId(EMPTY_ID);
          end.setSize(current.size);
        } else if (current.getSize() > end.getSize()) {
          current.setId(end.getId());
          Node empty =
              new Node(EMPTY_ID, current.getSize() - end.getSize(), current, current.getRight());
          current.setSize(end.getSize());
          current.setRight(empty);
          end.setId(EMPTY_ID);
          end.setSize(end.getSize());
        } else if (isPart1) {
          current.setId(end.getId());
          end.setSize(end.getSize() - current.getSize());
        }

        current = current.getRight();
      }
    }

    return checksum(root);
  }

  private Map<Integer, Node> buildNodes(List<Block> blocks) {
    Node current = new Node(blocks.getFirst().id(), blocks.getFirst().size(), null, null);
    Map<Integer, Node> nodes = new HashMap<>(Map.of(current.getId(), current));
    for (int i = 1; i < blocks.size(); i++) {
      Node next = new Node(blocks.get(i).id(), blocks.get(i).size(), current, null);
      current.setRight(next);
      current = next;
      if (current.getId() != EMPTY_ID) {
        nodes.put(current.getId(), current);
      }
    }
    return nodes;
  }

  private List<Block> extractBlocks(List<String> lines) {
    String line = lines.getFirst();
    List<Block> blocks = new ArrayList<>();
    int k = 0;
    int i = 0;
    while (i < line.length()) {
      int d = Integer.parseInt("" + line.charAt(i));

      if (i % 2 == 0) {
        blocks.add(new Block(k, d));
        k++;
      } else {
        if (d != 0) {
          blocks.add(new Block(EMPTY_ID, d));
        }
      }
      i++;
    }
    return blocks;
  }

  private long checksum(Node root) {
    long total = 0;
    int i = 0;
    Node current = root;
    while (current != null) {
      for (int j = 0; j < current.getSize(); j++) {
        total += (long) (current.getId() == EMPTY_ID ? 0 : current.getId()) * i;
        i++;
      }
      current = current.getRight();
    }

    return total;
  }

  private record Block(int id, int size) {}

  @Data
  @AllArgsConstructor
  private static class Node {
    private int id, size;
    private Node left, right;
  }
}
