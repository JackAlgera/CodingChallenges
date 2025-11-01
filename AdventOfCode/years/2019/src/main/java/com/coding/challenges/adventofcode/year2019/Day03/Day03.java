package com.coding.challenges.adventofcode.year2019.Day03;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

public class Day03 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day03 day = new Day03();

    day.printPart1("sample-input-1", 159L);
    day.printPart1("sample-input-2", 135L);
    day.printPart1("input", 303L);

    day.printPart2("sample-input-1", 610L);
    day.printPart2("sample-input-2", 410L);
    day.printPart2("input", 11222L);
  }

  @Override
  public Long part1(List<String> lines) {
    List<Path> l1 = parsePaths(lines.get(0));
    List<Path> l2 = parsePaths(lines.get(1));

    Set<Pos> wire1Positions = tracePath(l1, (pos, steps) -> true).keySet();
    return traceAndFindMinIntersection(
        l2, wire1Positions, (pos, steps) -> (long) Math.abs(pos.i()) + Math.abs(pos.j()));
  }

  @Override
  public Long part2(List<String> lines) {
    List<Path> l1 = parsePaths(lines.get(0));
    List<Path> l2 = parsePaths(lines.get(1));

    Map<Pos, Integer> wire1Steps = tracePath(l1, (pos, steps) -> steps);
    return traceAndFindMinIntersection(
        l2, wire1Steps.keySet(), (pos, steps) -> (long) steps + wire1Steps.get(pos));
  }

  private <T> Map<Pos, T> tracePath(List<Path> paths, BiFunction<Pos, Integer, T> valueFunction) {
    Map<Pos, T> visitedPositions = new HashMap<>();
    Pos pos = new Pos(0, 0);
    int steps = 0;

    for (Path path : paths) {
      int distance = path.distance;
      while (distance > 0) {
        pos = pos.move(path.direction);
        steps++;
        visitedPositions.put(pos, valueFunction.apply(pos, steps));
        distance--;
      }
    }

    return visitedPositions;
  }

  private Long traceAndFindMinIntersection(
      List<Path> paths,
      Set<Pos> intersectionPositions,
      BiFunction<Pos, Integer, Long> distanceFunction) {
    long minDistance = Long.MAX_VALUE;
    Pos pos = new Pos(0, 0);
    int steps = 0;

    for (Path path : paths) {
      int distance = path.distance;
      while (distance > 0) {
        pos = pos.move(path.direction);
        steps++;
        if (intersectionPositions.contains(pos)) {
          minDistance = Math.min(minDistance, distanceFunction.apply(pos, steps));
        }
        distance--;
      }
    }

    return minDistance;
  }

  List<Path> parsePaths(String path) {
    return Arrays.stream(path.split(",")).map(Path::new).toList();
  }

  record Path(Direction direction, int distance) {
    public Path(String string) {
      this(
          Direction.fromOrientation(string.substring(0, 1)), Integer.parseInt(string.substring(1)));
    }
  }
}
