package com.coding.challenges.adventofcode.year2023.Day16;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.InputLinesUtilities;
import com.coding.challenges.adventofcode.utils.Utilities;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;
import java.util.Queue;

public class Day16 extends Day<Integer> {

  public static void main(String[] args) throws IOException {
    Day16 day = new Day16();

    day.printPart1("sample-input", 46);
    day.printPart1("input", 7415);

    day.printPart2("sample-input", 51);
    day.printPart2("input", 7943);
  }

  @Override
  public Integer part1(List<String> lines) {
    return bfs(InputLinesUtilities.extractGrid(lines), 0, 0, Direction.E);
  }

  @Override
  public Integer part2(List<String> lines) {
    char[][] grid = InputLinesUtilities.extractGrid(lines);
    int height = lines.size();
    int width = lines.get(0).length();
    int maxEnergized = 0;
    for (int i = 0; i < height; i++) {
      maxEnergized = Math.max(maxEnergized, bfs(grid, i, 0, Direction.E));
      maxEnergized = Math.max(maxEnergized, bfs(grid, i, width - 1, Direction.W));
    }
    for (int j = 1; j < width - 1; j++) {
      maxEnergized = Math.max(maxEnergized, bfs(grid, 0, j, Direction.S));
      maxEnergized = Math.max(maxEnergized, bfs(grid, height - 1, j, Direction.N));
    }
    return maxEnergized;
  }

  public int bfs(char[][] grid, int startI, int startJ, Direction startDirection) {
    int maxI = grid.length;
    int maxJ = grid[0].length;
    boolean[][][] visisted = new boolean[maxI][maxJ][Direction.values().length];
    Queue<Beam> queue =
        new ArrayDeque<>(getNextBeams(new Beam(startI, startJ, startDirection), grid));

    while (!queue.isEmpty()) {
      Beam beam = queue.poll();
      if (visisted[beam.i()][beam.j()][beam.direction.ordinal()]) {
        continue;
      }
      visisted[beam.i()][beam.j()][beam.direction.ordinal()] = true;
      beam = beam.move();
      if (!Utilities.isValidIndex(beam.i(), beam.j(), maxI, maxJ)) {
        continue;
      }

      queue.addAll(getNextBeams(beam, grid));
    }

    return countEnergizedTiles(visisted);
  }

  public List<Beam> getNextBeams(Beam beam, char[][] grid) {
    char tile = grid[beam.i()][beam.j()];
    return switch (beam.direction()) {
      case N ->
          switch (tile) {
            case '/' -> List.of(new Beam(beam.i(), beam.j(), Direction.E));
            case '\\' -> List.of(new Beam(beam.i(), beam.j(), Direction.W));
            case '-' ->
                List.of(
                    new Beam(beam.i(), beam.j(), Direction.W),
                    new Beam(beam.i(), beam.j(), Direction.E));
            default -> List.of(beam);
          };
      case E ->
          switch (tile) {
            case '/' -> List.of(new Beam(beam.i(), beam.j(), Direction.N));
            case '\\' -> List.of(new Beam(beam.i(), beam.j(), Direction.S));
            case '|' ->
                List.of(
                    new Beam(beam.i(), beam.j(), Direction.N),
                    new Beam(beam.i(), beam.j(), Direction.S));
            default -> List.of(beam);
          };
      case S ->
          switch (tile) {
            case '/' -> List.of(new Beam(beam.i(), beam.j(), Direction.W));
            case '\\' -> List.of(new Beam(beam.i(), beam.j(), Direction.E));
            case '-' ->
                List.of(
                    new Beam(beam.i(), beam.j(), Direction.W),
                    new Beam(beam.i(), beam.j(), Direction.E));
            default -> List.of(beam);
          };
      case W ->
          switch (tile) {
            case '/' -> List.of(new Beam(beam.i(), beam.j(), Direction.S));
            case '\\' -> List.of(new Beam(beam.i(), beam.j(), Direction.N));
            case '|' ->
                List.of(
                    new Beam(beam.i(), beam.j(), Direction.N),
                    new Beam(beam.i(), beam.j(), Direction.S));
            default -> List.of(beam);
          };
    };
  }

  public int countEnergizedTiles(boolean[][][] visisted) {
    int count = 0;
    for (int i = 0; i < visisted.length; i++) {
      for (int j = 0; j < visisted[0].length; j++) {
        boolean energized = false;
        for (int k = 0; k < Direction.values().length; k++) {
          if (visisted[i][j][k]) {
            energized = true;
            break;
          }
        }
        if (energized) {
          count++;
        }
      }
    }
    return count;
  }

  public record Beam(int i, int j, Direction direction) {
    public Beam move() {
      return switch (direction) {
        case N -> new Beam(i - 1, j, direction);
        case E -> new Beam(i, j + 1, direction);
        case S -> new Beam(i + 1, j, direction);
        case W -> new Beam(i, j - 1, direction);
      };
    }
  }
}
