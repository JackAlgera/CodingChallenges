package com.coding.challenges.adventofcode.year2024.Day15;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.InputLinesUtilities;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

public class Day15 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day15 day = new Day15();

    day.printPart1("sample-input-1", 2028L);
    day.printPart1("sample-input-2", 10092L);
    day.printPart1("sample-input-3", 908L);
    day.printPart1("input", 1497888L);

    day.printPart2("sample-input-1", 1751L);
    day.printPart2("sample-input-2", 9021L);
    day.printPart2("sample-input-3", 618L);
    day.printPart2("input", 1522420L);
  }

  @Override
  public Long part1(List<String> lines) {
    return part(lines, false);
  }

  @Override
  public Long part2(List<String> lines) {
    return part(lines, true);
  }

  public long part(List<String> lines, boolean isPart2) {
    char[][] grid = extractGrid(lines, isPart2);
    Pos robot = InputLinesUtilities.findChar(grid, '@');

    int i = lines.indexOf("") + 1;
    while (i < lines.size()) {
      for (Direction d : getMoves(lines, i)) {
        if (canPush(d, robot, grid)) {
          push(d, robot, grid);
          robot = robot.move(d);
        }
      }
      i++;
    }

    return sumGpsCoordinates(grid);
  }

  public List<Direction> getMoves(List<String> lines, int i) {
    return lines
        .get(i)
        .chars()
        .mapToObj(d -> (char) d)
        .map(
            d ->
                switch (d) {
                  case '>' -> Direction.E;
                  case '<' -> Direction.W;
                  case '^' -> Direction.N;
                  case 'v' -> Direction.S;
                  default -> throw new IllegalStateException("Unexpected value: " + d);
                })
        .toList();
  }

  public boolean canPush(Direction d, Pos robot, char[][] grid) {
    Pos newPos = robot.move(d);
    if (!newPos.isValid(grid)) {
      return false;
    }
    char c = grid[newPos.i()][newPos.j()];

    if (c == '#') {
      return false;
    }

    if (c == '.') {
      return true;
    }

    if (c == 'O') {
      return canPush(d, newPos, grid);
    }

    if (d == Direction.E || d == Direction.W) {
      return canPush(d, newPos.move(d), grid);
    }

    if (c == ']') {
      return canPush(d, newPos, grid) && canPush(d, newPos.move(Direction.W), grid);
    } else {
      return canPush(d, newPos, grid) && canPush(d, newPos.move(Direction.E), grid);
    }
  }

  public void push(Direction d, Pos pos, char[][] grid) {
    Set<Pos> visited = new HashSet<>();
    Queue<Pos> queue = new LinkedList<>();
    queue.add(pos);
    Set<ToMove> toMove = new HashSet<>();

    while (!queue.isEmpty()) {
      Pos current = queue.poll();
      if (!current.isValid(grid) || visited.contains(current)) {
        continue;
      }
      visited.add(current);

      char c = grid[current.i()][current.j()];
      if (c == '.') {
        continue;
      }

      queue.add(current.move(d));
      toMove.add(new ToMove(current, c));

      if (c == '[') {
        queue.add(current.move(d).move(Direction.E));
        toMove.add(new ToMove(current.move(Direction.E), ']'));
      } else if (c == ']') {
        queue.add(current.move(d).move(Direction.W));
        toMove.add(new ToMove(current.move(Direction.W), '['));
      }
    }

    for (ToMove p : toMove) {
      grid[p.pos().i()][p.pos().j()] = '.';
    }
    for (ToMove p : toMove) {
      Pos newPos = p.pos().move(d);
      grid[newPos.i()][newPos.j()] = p.c();
    }
  }

  public char[][] extractGrid(List<String> lines, boolean isPart2) {
    char[][] grid = InputLinesUtilities.extractGrid(lines);

    if (!isPart2) {
      return grid;
    }

    char[][] expandedGrid = new char[grid.length][grid[0].length * 2];
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        char c = grid[i][j];
        switch (c) {
          case '#' -> {
            expandedGrid[i][j * 2] = '#';
            expandedGrid[i][j * 2 + 1] = '#';
          }
          case '.' -> {
            expandedGrid[i][j * 2] = '.';
            expandedGrid[i][j * 2 + 1] = '.';
          }
          case '@' -> {
            expandedGrid[i][j * 2] = '@';
            expandedGrid[i][j * 2 + 1] = '.';
          }
          case 'O' -> {
            expandedGrid[i][j * 2] = '[';
            expandedGrid[i][j * 2 + 1] = ']';
          }
        }
      }
    }

    return expandedGrid;
  }

  public long sumGpsCoordinates(char[][] grid) {
    Set<Character> validEdges = Set.of('O', '[');
    long total = 0;
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (validEdges.contains(grid[i][j])) {
          total += 100L * i + j;
        }
      }
    }
    return total;
  }

  public record ToMove(Pos pos, char c) {}
}
