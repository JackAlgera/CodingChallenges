package com.coding.challenges.adventofcode.year2025.Day12;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day12 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day12 day = new Day12();

    day.printPart1("sample-input", 2L);
    day.printPart1("input", 440L);

    day.printPart2("sample-input", 0L);
    day.printPart2("input", 0L);
  }

  @Override
  public Long part1(List<String> lines) {
    var presents = parsePresents(lines);
    var regions = parseRegions(lines);

    long validRegionsCount = 0;

    for (Region region : regions) {
      var regionPresents = new ArrayList<Present>();

      for (int id = 0; id < region.presents().length; id++) {
        int count = region.presents()[id];
        for (int k = 0; k < count; k++) {
          regionPresents.add(presents.get(id));
        }
      }

      // Fail fast if total area > region area
      int totalPresentArea = regionPresents.stream().mapToInt(p -> countHashes(p.grid())).sum();
      if (totalPresentArea > region.width() * region.height()) {
        continue;
      }

      // Sort presents by size descending for better packing performance
      regionPresents.sort(
          (p1, p2) -> Integer.compare(countHashes(p2.grid()), countHashes(p1.grid())));

      boolean[][] board = new boolean[region.height()][region.width()];
      if (canPack(board, regionPresents, 0)) {
        validRegionsCount++;
      }
    }
    return validRegionsCount;
  }

  @Override
  public Long part2(List<String> lines) {
    return 0L;
  }

  private boolean canPack(boolean[][] board, List<Present> presentsToPack, int index) {
    if (index == presentsToPack.size()) {
      return true;
    }

    // Generate all valid orientations (Rotations + Flips)
    var variations = generateVariations(presentsToPack.get(index).grid());
    for (var shape : variations) {
      for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[0].length; j++) {
          if (fits(board, shape, i, j)) {
            place(board, shape, i, j, true);

            if (canPack(board, presentsToPack, index + 1)) {
              return true;
            }

            place(board, shape, i, j, false);
          }
        }
      }
    }

    return false;
  }

  // --- Geometry & Transformations ---

  private Set<List<Pos>> generateVariations(Boolean[][] originalGrid) {
    var uniqueShapes = new HashSet<List<Pos>>();
    var currentGrid = originalGrid;

    // Add 4 rotations
    for (int i = 0; i < 4; i++) {
      uniqueShapes.add(gridToNormalizedPoints(currentGrid));
      currentGrid = rotate(currentGrid);
    }

    // Flip and add 4 rotations
    currentGrid = flip(originalGrid);
    for (int i = 0; i < 4; i++) {
      uniqueShapes.add(gridToNormalizedPoints(currentGrid));
      currentGrid = rotate(currentGrid);
    }

    return uniqueShapes;
  }

  private List<Pos> gridToNormalizedPoints(Boolean[][] grid) {
    var points = new ArrayList<Pos>();
    for (int i = 0; i < grid.length; i++) {
      for (int j = 0; j < grid[0].length; j++) {
        if (grid[i][j]) points.add(new Pos(i, j));
      }
    }
    if (points.isEmpty()) return points;

    // Normalize to (0,0) so rotations don't drift out of bounds
    int iMin = points.stream().mapToInt(Pos::i).min().orElse(0);
    int jMin = points.stream().mapToInt(Pos::j).min().orElse(0);

    return points.stream().map(p -> new Pos(p.i() - iMin, p.j() - jMin)).toList();
  }

  private Boolean[][] rotate(Boolean[][] grid) {
    int iMax = grid.length;
    int jMax = grid[0].length;
    var newGrid = new Boolean[jMax][iMax];
    for (int i = 0; i < iMax; i++) {
      for (int j = 0; j < jMax; j++) {
        newGrid[j][iMax - 1 - i] = grid[i][j];
      }
    }
    return newGrid;
  }

  private Boolean[][] flip(Boolean[][] grid) {
    int iMax = grid.length;
    int jMax = grid[0].length;
    var newGrid = new Boolean[iMax][jMax];
    for (int i = 0; i < iMax; i++) {
      for (int j = 0; j < jMax; j++) {
        newGrid[i][jMax - 1 - j] = grid[i][j];
      }
    }
    return newGrid;
  }

  private boolean fits(boolean[][] board, List<Pos> shape, int iOffset, int jOffset) {
    for (Pos p : shape) {
      var i = iOffset + p.i();
      var j = jOffset + p.j();
      if (i >= board.length || j >= board[0].length || board[i][j]) {
        return false;
      }
    }
    return true;
  }

  private void place(boolean[][] board, List<Pos> shape, int i, int j, boolean value) {
    for (Pos p : shape) {
      board[i + p.i()][j + p.j()] = value;
    }
  }

  private int countHashes(Boolean[][] grid) {
    int count = 0;
    for (var row : grid) {
      for (var val : row) {
        if (val) count++;
      }
    }
    return count;
  }

  private List<Region> parseRegions(List<String> lines) {
    List<Region> regions = new ArrayList<>();

    for (String line : lines.subList(6 * 5, lines.size())) {
      int width = Integer.parseInt(line.split(":")[0].split("x")[0]);
      int height = Integer.parseInt(line.split(":")[0].split("x")[1]);
      var presentIds =
          Arrays.stream(line.split(":")[1].trim().split(" ")).mapToInt(Integer::parseInt).toArray();
      regions.add(new Region(width, height, presentIds));
    }
    return regions;
  }

  private List<Present> parsePresents(List<String> lines) {
    List<Present> presents = new ArrayList<>();
    for (int i = 0; i < 6; i++) {
      var grid = new Boolean[3][3];
      for (int k = 0; k < 3; k++) {
        var row = lines.get(i * 5 + k + 1).chars().mapToObj(c -> c == '#').toArray(Boolean[]::new);
        grid[k] = row;
      }
      presents.add(new Present(i, grid));
    }
    return presents;
  }

  record Region(int width, int height, int[] presents) {
    @Override
    public String toString() {
      return "%dx%d: %s".formatted(width, height, Arrays.toString(presents));
    }
  }

  record Present(int id, Boolean[][] grid) {}
}
