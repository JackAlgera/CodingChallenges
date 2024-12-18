package com.coding.challenges.adventofcode.year2024.Day18;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Day18 extends Day<String> {

  public static void main(String[] args) throws IOException {
    Day18 day = new Day18();

    day.printPart1("sample-input", "22");
    day.printPart1("input", "326");

    day.printPart2("sample-input", "6,1");
    day.printPart2("input", "18,62");
  }

  @Override
  public String part1(List<String> lines) {
    int maxI = lines.size() < 100 ? 7 : 71;
    int maxJ = lines.size() < 100 ? 7 : 71;
    int nbrBytes = lines.size() < 100 ? 12 : 1024;

    return aStar(
        createGrid(lines, nbrBytes, maxI, maxJ), new Pos(0, 0), new Pos(maxI - 1, maxJ - 1));
  }

  @Override
  public String part2(List<String> lines) {
    int maxI = lines.size() < 100 ? 7 : 71;
    int maxJ = lines.size() < 100 ? 7 : 71;
    int nbrBytes = lines.size() < 100 ? 12 : 1024;

    for (int i = nbrBytes; i < lines.size(); i++) {
      String result =
          aStar(createGrid(lines, i, maxI, maxJ), new Pos(0, 0), new Pos(maxI - 1, maxJ - 1));
      if (result.equals("oops")) {
        return lines.get(i - 1);
      }
    }
    return "0";
  }

  public char[][] createGrid(List<String> lines, int nbrBytes, int maxI, int maxJ) {
    List<Pos> positions =
        lines.stream()
            .map(l -> new Pos(Integer.parseInt(l.split(",")[0]), Integer.parseInt(l.split(",")[1])))
            .toList();
    char[][] grid = new char[maxI][maxJ];
    for (int i = 0; i < maxI; i++) {
      for (int j = 0; j < maxJ; j++) {
        grid[i][j] = '.';
      }
    }

    for (int i = 0; i < nbrBytes; i++) {
      Pos pos = positions.get(i);
      grid[pos.j()][pos.i()] = '#';
    }
    return grid;
  }

  public String aStar(char[][] grid, Pos start, Pos end) {
    Queue<Node> open = new PriorityQueue<>(Comparator.comparingInt(n -> n.h));
    Set<Pos> closed = new HashSet<>();
    open.add(new Node(start, 0, start.manhattanDistance(end)));

    while (!open.isEmpty()) {
      Node current = open.poll();
      closed.add(current.pos);

      if (current.pos.equals(end)) {
        return "" + current.steps();
      }

      for (Direction d : Direction.values()) {
        Pos newPos = current.pos.move(d);
        Node newNode =
            new Node(
                newPos, current.steps() + 1, newPos.manhattanDistance(end) + current.steps() + 1);
        if (!newPos.isValid(grid)
            || grid[newPos.i()][newPos.j()] == '#'
            || closed.contains(newNode.pos)) {
          continue;
        }

        Node existingNode =
            open.stream().filter(n -> n.pos.equals(newPos)).findFirst().orElse(null);
        if (existingNode != null && newNode.steps() < existingNode.steps()) {
          open.remove(existingNode);
          open.add(newNode);
        } else {
          open.add(newNode);
        }
      }
    }

    return "oops";
  }

  public record Node(Pos pos, int steps, int h) {
    @Override
    public boolean equals(Object o) {
      return pos.equals(((Node) o).pos);
    }
  }
}
