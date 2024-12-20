package com.coding.challenges.adventofcode.utils.pathfinding;

import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import com.coding.challenges.adventofcode.utils.pathfinding.PathFindingAlgorithm.Node;
import java.util.Comparator;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import lombok.experimental.UtilityClass;

@UtilityClass
public class PathFindingAlgorithms {

  public PathFindingAlgorithm A_STAR =
      (grid, start, end, wallChar) -> {
        Queue<Node> open = new PriorityQueue<>(Comparator.comparingInt(Node::h));
        Set<Pos> closed = new HashSet<>();
        open.add(new Node(start, 0, start.manhattanDistance(end)));

        while (!open.isEmpty()) {
          Node current = open.poll();
          closed.add(current.pos());

          if (current.pos().equals(end)) {
            return current.steps();
          }

          for (Direction d : Direction.values()) {
            Pos newPos = current.pos().move(d);
            Node newNode =
                new Node(
                    newPos,
                    current.steps() + 1,
                    newPos.manhattanDistance(end) + current.steps() + 1);
            if (!newPos.isValid(grid)
                || grid[newPos.i()][newPos.j()] == wallChar
                || closed.contains(newNode.pos())) {
              continue;
            }

            Node existingNode =
                open.stream().filter(n -> n.pos().equals(newPos)).findFirst().orElse(null);
            if (existingNode != null && newNode.steps() < existingNode.steps()) {
              open.remove(existingNode);
              open.add(newNode);
            } else {
              open.add(newNode);
            }
          }
        }

        return -1L;
      };
}
