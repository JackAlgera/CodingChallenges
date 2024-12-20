package com.coding.challenges.adventofcode.year2024.Day18;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.pathfinding.PathFindingAlgorithms;
import java.io.IOException;
import java.util.List;

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

    return ""
        + PathFindingAlgorithms.A_STAR.solve(
            createGrid(lines, nbrBytes, maxI, maxJ),
            new Pos(0, 0),
            new Pos(maxI - 1, maxJ - 1),
            '#');
  }

  @Override
  public String part2(List<String> lines) {
    int maxI = lines.size() < 100 ? 7 : 71;
    int maxJ = lines.size() < 100 ? 7 : 71;
    int nbrBytes = lines.size() < 100 ? 12 : 1024;

    for (int i = nbrBytes; i < lines.size(); i++) {
      long steps =
          PathFindingAlgorithms.A_STAR.solve(
              createGrid(lines, i, maxI, maxJ), new Pos(0, 0), new Pos(maxI - 1, maxJ - 1), '#');
      if (steps == -1L) {
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
}
