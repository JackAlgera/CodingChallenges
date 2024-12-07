package com.coding.challenges.adventofcode.year2022.Day24;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.*;

public class Day24 extends Day<Integer> {

  public static void main(String[] args) throws IOException {
    Day24 day = new Day24();

    day.printPart1("sample-input", 18);
    day.printPart1("input", 269);

    day.printPart2("sample-input", 54);
    day.printPart2("input", 825);
  }

  public Map<Character, Vector> DIRECTIONS =
      Map.of(
          '^', new Vector(-1, 0),
          'v', new Vector(1, 0),
          '>', new Vector(0, 1),
          '<', new Vector(0, -1));

  @Override
  public Integer part1(List<String> lines) {
    Vector dimensions = new Vector(lines.size(), lines.get(0).length());
    Vector start = new Vector(0, 1);
    Vector end = new Vector(dimensions.i() - 1, dimensions.j() - 2);

    return getShortestPath(List.of(start, end), dimensions, lines);
  }

  @Override
  public Integer part2(List<String> lines) {
    Vector dimensions = new Vector(lines.size(), lines.get(0).length());
    List<Vector> blizzardDirections = new ArrayList<>();
    List<Vector> blizzardPositions = new ArrayList<>();
    extractBlizzards(lines, dimensions, blizzardPositions, blizzardDirections);

    Vector start = new Vector(0, 1);
    Vector end = new Vector(dimensions.i() - 1, dimensions.j() - 2);

    return getShortestPath(List.of(start, end, end, start, start, end), dimensions, lines);
  }

  private int getShortestPath(List<Vector> positions, Vector dimensions, List<String> lines) {
    List<Vector> blizzardDirections = new ArrayList<>();
    List<Vector> blizzardPositions = new ArrayList<>();
    extractBlizzards(lines, dimensions, blizzardPositions, blizzardDirections);

    int time = 0;
    for (int k = 0; k < positions.size() / 2; k++) {
      Vector start = positions.get(k * 2);
      Vector end = positions.get((k + 1) * 2 - 1);

      Set<Vector> playerPositions = new HashSet<>();
      playerPositions.add(start);

      while (!playerPositions.contains(end)) {
        blizzardPositions =
            updateBlizzardPositions(blizzardPositions, blizzardDirections, dimensions);
        playerPositions =
            updatePlayerPositions(end, dimensions, playerPositions, blizzardPositions);
        time++;
      }
    }

    return time;
  }

  private Set<Vector> updatePlayerPositions(
      Vector end, Vector dimensions, Set<Vector> playerPositions, List<Vector> blizzardPositions) {
    Set<Vector> newPaths = new HashSet<>();

    for (Vector position : playerPositions) {
      if (!blizzardPositions.contains(position)) {
        newPaths.add(position);
      }

      for (Vector direction : DIRECTIONS.values()) {
        Vector newPos = position.move(direction);
        if (newPos.equals(end)) {
          newPaths.add(end);
          continue;
        }

        if (!isValidPosition(newPos, dimensions) || blizzardPositions.contains(newPos)) {
          continue;
        }

        newPaths.add(newPos);
      }
    }

    return newPaths;
  }

  private static List<Vector> updateBlizzardPositions(
      List<Vector> blizzardPositions, List<Vector> blizzardDirections, Vector dimensions) {
    List<Vector> updatedBlizzardPositions = new ArrayList<>();
    for (int k = 0; k < blizzardPositions.size(); k++) {
      Vector blizzardPosition = blizzardPositions.get(k);
      Vector newPosition = blizzardPosition.move(blizzardDirections.get(k));

      updatedBlizzardPositions.add(
          new Vector(
              Math.floorMod(newPosition.i() - 1, dimensions.i() - 2) + 1,
              Math.floorMod(newPosition.j() - 1, dimensions.j() - 2) + 1));
    }
    return updatedBlizzardPositions;
  }

  private void extractBlizzards(
      List<String> lines,
      Vector dimensions,
      List<Vector> blizzardPositions,
      List<Vector> blizzardDirections) {
    for (int i = 1; i < dimensions.i() - 1; i++) {
      String line = lines.get(i);
      for (int j = 1; j < dimensions.j() - 1; j++) {
        char tile = line.charAt(j);

        if (tile == '.') {
          continue;
        }

        blizzardPositions.add(new Vector(i, j));
        blizzardDirections.add(DIRECTIONS.get(tile));
      }
    }
  }

  public boolean isValidPosition(Vector position, Vector dimensions) {
    return position.i() >= 1
        && position.i() <= dimensions.i() - 2
        && position.j() >= 1
        && position.j() <= dimensions.j() - 2;
  }

  public void printBlizzards(List<Vector> blizzards, Vector dimensions) {
    int[][] grid = new int[dimensions.i()][dimensions.j()];
    for (Vector blizzard : blizzards) {
      grid[blizzard.i()][blizzard.j()] = 2;
    }
    for (int i = 0; i < dimensions.i(); i++) {
      grid[i][0] = 1;
      grid[i][dimensions.j() - 1] = 1;
    }
    for (int j = 0; j < dimensions.j(); j++) {
      grid[0][j] = 1;
      grid[dimensions.i() - 1][j] = 1;
    }
    for (int i = 0; i < dimensions.i(); i++) {
      for (int j = 0; j < dimensions.j(); j++) {
        System.out.print(
            switch (grid[i][j]) {
              case 0 -> ".";
              case 1 -> "#";
              case 2 -> "o";
              default -> throw new IllegalStateException("Unexpected value: " + grid[i][j]);
            });
      }
      System.out.println();
    }
    System.out.println();
  }

  public record Vector(int i, int j) {
    public Vector move(Vector direction) {
      return new Vector(i + direction.i(), j + direction.j());
    }
  }
}
