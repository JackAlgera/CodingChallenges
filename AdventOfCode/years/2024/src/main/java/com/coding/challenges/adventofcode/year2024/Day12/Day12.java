package com.coding.challenges.adventofcode.year2024.Day12;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import lombok.AllArgsConstructor;

public class Day12 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day12 day = new Day12();

    day.printPart1("sample-input-1", 1930L);
    day.printPart1("sample-input-2", 692L);
    day.printPart1("sample-input-3", 1184L);
    day.printPart1("input", 1374934L);

    day.printPart2("sample-input-1", 1206L);
    day.printPart2("sample-input-2", 236L);
    day.printPart2("sample-input-3", 368L);
    day.printPart2("input", 841078L);
  }

  @Override
  public Long part1(List<String> lines) {
    return part(lines).stream().mapToLong(r -> r.area * r.perimeter).sum();
  }

  @Override
  public Long part2(List<String> lines) {
    return part(lines).stream().mapToLong(r -> r.area * r.corners).sum();
  }

  public List<Region> part(List<String> lines) {
    boolean[][] visited = new boolean[lines.size()][lines.get(0).length()];
    List<Region> regions = new ArrayList<>();

    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.get(0).length(); j++) {
        if (!visited[i][j]) {
          regions.add(searchPlot(lines, visited, new Pos(i, j)));
        }
      }
    }

    return regions;
  }

  private Region searchPlot(List<String> lines, boolean[][] visited, Pos root) {
    char type = lines.get(root.i()).charAt(root.j());
    Region region = new Region(0, 0, 0);
    Queue<Pos> queue = new LinkedList<>();
    queue.add(root);

    while (!queue.isEmpty()) {
      Pos current = queue.poll();
      if (visited[current.i()][current.j()]) {
        continue;
      }
      visited[current.i()][current.j()] = true;
      region.area++;
      region.corners += getCorners(lines, current, type);
      region.perimeter += 4;

      for (Direction direction : Direction.values()) {
        Pos newPos = current.move(direction);
        if (!newPos.isValid(lines) || lines.get(newPos.i()).charAt(newPos.j()) != type) {
          continue;
        }

        if (!visited[newPos.i()][newPos.j()]) {
          queue.add(newPos);
          region.perimeter -= 2;
        }
      }
    }

    return region;
  }

  public long getCorners(List<String> lines, Pos pos, char type) {
    long totalCorners = 0;
    List<Corner> corners =
        Arrays.stream(Direction.values()).map(d -> getCorner(pos, d, lines)).toList();

    for (int k = 0; k < 4; k++) {
      Corner corner = corners.get(k);

      // Inside corners
      if (corner.a != type && corner.b != type) {
        totalCorners++;
      }

      // Outside corners
      if (corner.a == type && corner.b == type && corner.diag != type) {
        totalCorners++;
      }
    }

    return totalCorners;
  }

  @AllArgsConstructor
  public static class Region {
    long area, perimeter, corners;
  }

  private Corner getCorner(Pos pos, Direction d, List<String> lines) {
    Pos a = pos.move(d);
    Pos b = pos.move(d.rotateLeft(1));
    Pos diag = pos.move(d).move(d.rotateLeft(1));
    return new Corner(
        a.isValid(lines) ? lines.get(a.i()).charAt(a.j()) : '@',
        b.isValid(lines) ? lines.get(b.i()).charAt(b.j()) : '@',
        diag.isValid(lines) ? lines.get(diag.i()).charAt(diag.j()) : '@');
  }

  private record Corner(char a, char b, char diag) {}
}
