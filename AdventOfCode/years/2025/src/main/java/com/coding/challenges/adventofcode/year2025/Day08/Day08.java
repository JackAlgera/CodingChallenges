package com.coding.challenges.adventofcode.year2025.Day08;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Day08 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day08 day = new Day08();

    day.printPart1("sample-input", 40L);
    day.printPart1("input", 83520L);

    day.printPart2("sample-input", 25272L);
    day.printPart2("input", 1131823407L);
  }

  @Override
  public Long part1(List<String> lines) {
    var shortestConnectionsCount = lines.size() < 30 ? 10 : 1000;
    var data = Data.parse(lines);

    for (BoxDistance boxDistance : data.distances.subList(0, shortestConnectionsCount)) {
      int circuitIndex1 = findCircuitIndex(boxDistance.boxIndex1, data.parents);
      int circuitIndex2 = findCircuitIndex(boxDistance.boxIndex2, data.parents);

      if (circuitIndex1 != circuitIndex2) {
        data.parents[circuitIndex1] = circuitIndex2;
      }
    }

    Map<Integer, Set<Integer>> distinctCircuits = new HashMap<>();
    for (int i = 0; i < data.boxes.size(); i++) {
      int index = findCircuitIndex(i, data.parents);
      distinctCircuits.computeIfAbsent(index, k -> new HashSet<>()).add(i);
    }

    return distinctCircuits.values().stream()
        .sorted((c1, c2) -> Integer.compare(c2.size(), c1.size()))
        .limit(3)
        .mapToLong(Set::size)
        .reduce((a, b) -> a * b)
        .orElse(-1);
  }

  @Override
  public Long part2(List<String> lines) {
    var data = Data.parse(lines);

    for (BoxDistance boxDistance : data.distances) {
      int circuitIndex1 = findCircuitIndex(boxDistance.boxIndex1, data.parents);
      int circuitIndex2 = findCircuitIndex(boxDistance.boxIndex2, data.parents);

      if (circuitIndex1 != circuitIndex2) {
        data.parents[circuitIndex1] = circuitIndex2;
      }

      int root = findCircuitIndex(0, data.parents);
      boolean allConnected = true;
      for (int i = 1; i < data.parents.length; i++) {
        if (findCircuitIndex(i, data.parents) != root) {
          allConnected = false;
          break;
        }
      }
      if (allConnected) {
        return (long) data.boxes.get(boxDistance.boxIndex1).x
            * data.boxes.get(boxDistance.boxIndex2).x;
      }
    }

    return -1L;
  }

  private int findCircuitIndex(int index, int[] parents) {
    while (index != parents[index]) {
      parents[index] = parents[parents[index]];
      index = parents[index];
    }
    return index;
  }

  private static List<Box> extractBoxes(List<String> lines) {
    return lines.stream()
        .map(
            line -> {
              var parts = line.split(",");
              return new Box(
                  Integer.parseInt(parts[0]),
                  Integer.parseInt(parts[1]),
                  Integer.parseInt(parts[2]));
            })
        .toList();
  }

  record Box(int x, int y, int z) {
    public long distanceTo(Box other) {
      long dx = x - other.x;
      long dy = y - other.y;
      long dz = z - other.z;
      return dx * dx + dy * dy + dz * dz;
    }
  }

  record BoxDistance(int boxIndex1, int boxIndex2, long distance) {}

  record Data(List<Box> boxes, List<BoxDistance> distances, int[] parents) {
    public static Data parse(List<String> lines) {
      var boxes = extractBoxes(lines);
      var distances = new ArrayList<BoxDistance>();

      for (int i = 1; i < boxes.size(); i++) {
        for (int j = 0; j < i; j++) {
          distances.add(new BoxDistance(i, j, boxes.get(i).distanceTo(boxes.get(j))));
        }
      }

      distances.sort(Comparator.comparingLong(d -> d.distance));

      int[] parents = new int[boxes.size()];
      for (int i = 0; i < parents.length; i++) parents[i] = i;
      return new Data(boxes, distances, parents);
    }
  }
}
