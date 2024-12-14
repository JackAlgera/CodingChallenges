package com.coding.challenges.adventofcode.year2024.Day14;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class Day14 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day14 day = new Day14();

    day.printPart1("sample-input", 12L);
    day.printPart1("input", 230436441L);

    day.printPart2("input", 8270L);
  }

  @Override
  public Long part1(List<String> lines) {
    List<Robot> robots = extractRobots(lines);
    int xMax = lines.size() < 20 ? 11 : 101;
    int yMax = lines.size() < 20 ? 7 : 103;
    return safetyFactor(robots, xMax, yMax, 100);
  }

  @Override
  public Long part2(List<String> lines) {
    List<Robot> robots = extractRobots(lines);
    for (int i = 1; i < 10_000; i++) {
      robots = robots.stream().map(r -> r.move(1, 101, 103)).toList();
      Vector barycenter =
          new Vector(
              robots.stream().mapToLong(r -> r.pos.x).sum() / robots.size(),
              robots.stream().mapToLong(r -> r.pos.y).sum() / robots.size());
      double averageDistanceFromBarycenter =
          robots.stream().mapToDouble(r -> barycenter.distance(r.pos)).sum() / robots.size();
      if (averageDistanceFromBarycenter < 21) {
        printRobots(robots, 101, 103);
        return (long) i;
      }
    }
    return 0L;
  }

  public Long safetyFactor(List<Robot> robots, int xMax, int yMax, int seconds) {
    int topLeft = 0;
    int topRight = 0;
    int bottomLeft = 0;
    int bottomRight = 0;
    for (Robot robot : robots) {
      Robot movedRobot = robot.move(seconds, xMax, yMax);
      if (movedRobot.pos.x < xMax / 2 && movedRobot.pos.y < yMax / 2) {
        topLeft++;
      } else if (movedRobot.pos.x < xMax / 2 && movedRobot.pos.y > yMax / 2) {
        topRight++;
      } else if (movedRobot.pos.x > xMax / 2 && movedRobot.pos.y < yMax / 2) {
        bottomLeft++;
      } else if (movedRobot.pos.x > xMax / 2 && movedRobot.pos.y > yMax / 2) {
        bottomRight++;
      }
    }
    return (long) topLeft * topRight * bottomLeft * bottomRight;
  }

  public List<Robot> extractRobots(List<String> lines) {
    return lines.stream()
        .map(
            line ->
                Pattern.compile("p=([\\d-]+),([\\d-]+) v=([\\d-]+),([\\d-]+)")
                    .matcher(line)
                    .results()
                    .map(
                        m ->
                            new Robot(
                                new Vector(
                                    Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
                                new Vector(
                                    Integer.parseInt(m.group(3)), Integer.parseInt(m.group(4)))))
                    .findFirst()
                    .get())
        .toList();
  }

  public void printRobots(List<Robot> robots, int xMax, int yMax) {
    boolean[][] grid = new boolean[yMax][xMax];
    for (Robot robot : robots) {
      grid[(int) robot.pos.y][(int) robot.pos.x] = true;
    }
    System.out.println();
    for (int y = 0; y < yMax; y++) {
      for (int x = 0; x < xMax; x++) {
        System.out.print(grid[y][x] ? "#" : ".");
      }
      System.out.println();
    }
  }

  public record Robot(Vector pos, Vector velocity) {
    public Robot move(int seconds, int xMax, int yMax) {
      long x = (pos.x + velocity.x * seconds) % xMax;
      long y = (pos.y + velocity.y * seconds) % yMax;
      if (x < 0) {
        x = xMax - (Math.abs(x) % xMax);
      }
      if (y < 0) {
        y = yMax - (Math.abs(y) % yMax);
      }
      return new Robot(new Vector(x, y), velocity);
    }
  }

  public record Vector(long x, long y) {
    public double distance(Vector other) {
      return Math.sqrt(Math.pow(x - other.x, 2) + Math.pow(y - other.y, 2));
    }
  }
}
