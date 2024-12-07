package com.coding.challenges.adventofcode.year2024.Day6;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day6 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day6 day = new Day6();

    day.printPart1("sample-input", 41L);
    day.printPart1("input", 4663L);

    day.printPart2("sample-input", 6L);
    day.printPart2("input", 1530L);
  }

  @Override
  public Long part1(List<String> lines) {
    Guard guard = extractGuard(lines);
    markPosition(lines, guard.pos(), 'X');

    while (guard != null) {
      guard = moveGuard(guard, lines, true);
    }

    return countUniquePositions(lines);
  }

  @Override
  public Long part2(List<String> lines) {
    long total = 0L;
    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.get(0).length(); j++) {
        char c = lines.get(i).charAt(j);
        if (c != '.') {
          continue;
        }

        markPosition(lines, new Pos(i, j), '#');
        total += createsLoop(lines) ? 1 : 0;
        markPosition(lines, new Pos(i, j), c);
      }
    }
    return total;
  }

  public boolean createsLoop(List<String> lines) {
    Guard guard = extractGuard(lines);
    Set<Guard> positions = new HashSet<>(Set.of(guard));
    guard = moveGuard(guard, lines, false);

    while (guard != null) {
      if (positions.contains(guard)) {
        return true;
      }

      positions.add(guard);
      guard = moveGuard(guard, lines, false);
    }

    return false;
  }

  public void markPosition(List<String> lines, Pos pos, char c) {
    String line = lines.get(pos.i());
    lines.set(
        pos.i(), "%s%s%s".formatted(line.substring(0, pos.j()), c, line.substring(pos.j() + 1)));
  }

  public Guard moveGuard(Guard guard, List<String> lines, boolean markPosition) {
    Pos pos = guard.pos().move(guard.direction());

    while (pos.isValid(lines)) {
      if (lines.get(pos.i()).charAt(pos.j()) == '#') {
        return new Guard(guard.direction().rotateRight(1), pos.move(guard.direction().opposite()));
      }

      if (markPosition) {
        markPosition(lines, pos, 'X');
      }
      pos = pos.move(guard.direction());
    }

    return null;
  }

  public long countUniquePositions(List<String> lines) {
    return lines.stream()
        .flatMap(line -> line.chars().asLongStream().boxed())
        .filter(c -> c == 'X')
        .count();
  }

  public Guard extractGuard(List<String> lines) {
    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.get(0).length(); j++) {
        Pos pos = new Pos(i, j);
        Direction d =
            switch (lines.get(i).charAt(j)) {
              case '^' -> Direction.N;
              case '>' -> Direction.E;
              case '<' -> Direction.S;
              case 'v' -> Direction.W;
              default -> null;
            };
        if (d != null) {
          return new Guard(d, pos);
        }
      }
    }

    throw new IllegalArgumentException("No guard found");
  }

  public record Guard(Direction direction, Pos pos) {}
}
