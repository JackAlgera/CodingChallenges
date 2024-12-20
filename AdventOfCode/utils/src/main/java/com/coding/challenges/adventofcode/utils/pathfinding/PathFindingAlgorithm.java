package com.coding.challenges.adventofcode.utils.pathfinding;

import com.coding.challenges.adventofcode.utils.Pos;

public interface PathFindingAlgorithm {

  long solve(char[][] grid, Pos start, Pos end, char wallChar);

  record Node(Pos pos, int steps, int h) {
    @Override
    public boolean equals(Object o) {
      return pos.equals(((Node) o).pos);
    }
  }
}
