package com.coding.challenges.adventofcode.year2020.Day3;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pair;
import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

public class Day3 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day3 day = new Day3();

    day.printPart1("sample-input", 7L);
    day.printPart1("input", 234L);

    day.printPart2("sample-input", 336L);
    day.printPart2("input", 5813773056L);
  }

  @Override
  public Long part1(List<String> lines) {
    return countTreesHit(getTreesGrid(lines), new Pair<>(1, 3));
  }

  @Override
  public Long part2(List<String> lines) {
    return Stream.of(
            new Pair<>(1, 1),
            new Pair<>(1, 3),
            new Pair<>(1, 5),
            new Pair<>(1, 7),
            new Pair<>(2, 1))
        .map(p -> countTreesHit(getTreesGrid(lines), p))
        .reduce(1L, (t1, t2) -> t1 * t2);
  }

  private boolean[][] getTreesGrid(List<String> lines) {
    int height = lines.size();
    int width = lines.get(0).length();
    boolean[][] trees = new boolean[height][width];
    for (int i = 0; i < height; i++) {
      String row = lines.get(i);
      for (int j = 0; j < width; j++) {
        trees[i][j] = row.charAt(j) == '#';
      }
    }
    return trees;
  }

  private long countTreesHit(boolean[][] trees, Pair<Integer> slope) {
    int height = trees.length;
    int width = trees[0].length;

    long treesHit = 0;
    int i = 0;
    int j = 0;
    while (i < height) {
      if (trees[i][j]) {
        treesHit++;
      }
      i += slope.getFirst();
      j = (j + slope.getSecond()) % width;
    }

    return treesHit;
  }
}
