package com.coding.challenges.adventofcode.year2025.Day09;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;

public class Day09 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day09 day = new Day09();

    day.printPart1("sample-input", 50L);
    day.printPart1("input", 4774877510L);

    day.printPart2("sample-input", 24L);
    day.printPart2("input", 1560475800L);
  }

  @Override
  public Long part1(List<String> lines) {
    var tiles =
        lines.stream()
            .map(
                t ->
                    new Coordinate(
                        Integer.parseInt(t.split(",")[0]), Integer.parseInt(t.split(",")[1])))
            .toList();

    long maxArea = 0L;
    for (int i = 0; i < tiles.size(); i++) {
      for (int j = i + 1; j < tiles.size(); j++) {
        var tileA = tiles.get(i);
        var tileB = tiles.get(j);
        long area = computeArea(tileA, tileB);
        if (area > maxArea) {
          maxArea = area;
        }
      }
    }

    return maxArea;
  }

  @Override
  public Long part2(List<String> lines) {
    var tiles =
        lines.stream()
            .map(
                t ->
                    new Coordinate(
                        Integer.parseInt(t.split(",")[0]), Integer.parseInt(t.split(",")[1])))
            .toList();
    long maxArea = 0L;

    for (int i = 0; i < tiles.size(); i++) {
      for (int j = i + 1; j < tiles.size(); j++) {
        var tileA = tiles.get(i);
        var tileB = tiles.get(j);

        // Not a rectangle?
        if (tileA.x() == tileB.x() || tileA.y() == tileB.y()) continue;

        // Smaller than maxArea?
        long area = computeArea(tileA, tileB);
        if (area <= maxArea) continue;

        if (isRectangleInside(tileA, tileB, tiles)) {
          maxArea = area;
        }
      }
    }

    return maxArea;
  }

  private long computeArea(Coordinate tileA, Coordinate tileB) {
    return (long) (1 + Math.abs(tileA.x() - tileB.x())) * (1 + Math.abs(tileA.y() - tileB.y()));
  }

  private boolean isRectangleInside(Coordinate tileA, Coordinate tileB, List<Coordinate> tiles) {
    long minX = Math.min(tileA.x(), tileB.x());
    long maxX = Math.max(tileA.x(), tileB.x());
    long minY = Math.min(tileA.y(), tileB.y());
    long maxY = Math.max(tileA.y(), tileB.y());

    for (int i = 0; i < tiles.size(); i++) {
      var pA = tiles.get(i);
      var pB = tiles.get((i + 1) % tiles.size());

      if (pA.x() == pB.x()) {
        if (pA.x() <= minX || pA.x() >= maxX) continue;
        if (Math.min(pA.y(), pB.y()) >= maxY || Math.max(pA.y(), pB.y()) <= minY) continue;
      } else {
        if (pA.y() <= minY || pA.y() >= maxY) continue;
        if (Math.min(pA.x(), pB.x()) >= maxX || Math.max(pA.x(), pB.x()) <= minX) continue;
      }
      return false;
    }

    return true;
  }

  record Coordinate(int x, int y) {}
}
