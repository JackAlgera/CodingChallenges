package com.coding.challenges.adventofcode.year2023.Day24;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Vector2dFloat;
import com.coding.challenges.adventofcode.utils.Vector3dLong;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.*;

public class Day24 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day24 day = new Day24();

    day.printPart1("input", 16589L);
    day.printPart2("input", 781390555762385L);
  }

  @Override
  public Long part1(List<String> lines) {
    List<Hailstone> hailstones = parseInput(lines);
    long intersections = 0;
    long min = lines.size() < 10 ? 7L : 200_000_000_000_000L;
    long max = lines.size() < 10 ? 27L : 400_000_000_000_000L;
    for (int i = 0; i < hailstones.size(); i++) {
      for (int j = i + 1; j < hailstones.size(); j++) {
        Vector2dFloat intersection = getIntersection(hailstones.get(i), hailstones.get(j));
        if (intersection == null) {
          continue;
        }
        if (intersection.x() >= min
            && intersection.x() <= max
            && intersection.y() >= min
            && intersection.y() <= max) {
          intersections++;
        }
      }
    }

    return intersections;
  }

  @Override
  public Long part2(List<String> lines) {
    List<Hailstone> hailstones = parseInput(lines);

    Vector3dLong p0 = hailstones.get(0).pos();
    Vector3dLong v0 = hailstones.get(0).vel();
    Vector3dLong p1 = hailstones.get(1).pos();
    Vector3dLong v1 = hailstones.get(1).vel();
    Vector3dLong p2 = hailstones.get(2).pos();
    Vector3dLong v2 = hailstones.get(2).vel();

    RealMatrix mat =
        MatrixUtils.createRealMatrix(
            new double[][] {
              {v0.y() - v1.y(), v1.x() - v0.x(), 0.0, p1.y() - p0.y(), p0.x() - p1.x(), 0.0},
              {v0.y() - v2.y(), v2.x() - v0.x(), 0.0, p2.y() - p0.y(), p0.x() - p2.x(), 0.0},
              {v1.z() - v0.z(), 0.0, v0.x() - v1.x(), p0.z() - p1.z(), 0.0, p1.x() - p0.x()},
              {v2.z() - v0.z(), 0.0, v0.x() - v2.x(), p0.z() - p2.z(), 0.0, p2.x() - p0.x()},
              {0.0, v0.z() - v1.z(), v1.y() - v0.y(), 0.0, p1.z() - p0.z(), p0.y() - p1.y()},
              {0.0, v0.z() - v2.z(), v2.y() - v0.y(), 0.0, p2.z() - p0.z(), p0.y() - p2.y()},
            });

    RealVector realH =
        MatrixUtils.createRealVector(
            new double[] {
              (p1.y() * v1.x() - p1.x() * v1.y()) - (p0.y() * v0.x() - p0.x() * v0.y()),
              (p2.y() * v2.x() - p2.x() * v2.y()) - (p0.y() * v0.x() - p0.x() * v0.y()),
              (p1.x() * v1.z() - p1.z() * v1.x()) - (p0.x() * v0.z() - p0.z() * v0.x()),
              (p2.x() * v2.z() - p2.z() * v2.x()) - (p0.x() * v0.z() - p0.z() * v0.x()),
              (p1.z() * v1.y() - p1.y() * v1.z()) - (p0.z() * v0.y() - p0.y() * v0.z()),
              (p2.z() * v2.y() - p2.y() * v2.z()) - (p0.z() * v0.y() - p0.y() * v0.z()),
            });

    RealVector solution = new LUDecomposition(mat).getSolver().solve(realH);
    return Math.round(solution.getEntry(0))
        + Math.round(solution.getEntry(1))
        + Math.round(solution.getEntry(2));
  }

  public Vector2dFloat getIntersection(Hailstone h1, Hailstone h2) {
    long det = h2.vel().x() * h1.vel().y() - h2.vel().y() * h1.vel().x();
    if (det == 0) {
      return null;
    }
    long u =
        ((h2.pos().y() - h1.pos().y()) * h2.vel().x()
                - (h2.pos().x() - h1.pos().x()) * h2.vel().y())
            / det;
    long v =
        ((h2.pos().y() - h1.pos().y()) * h1.vel().x()
                - (h2.pos().x() - h1.pos().x()) * h1.vel().y())
            / det;
    if (u < 0 || v < 0) {
      return null;
    }
    return new Vector2dFloat(h2.pos().x() + h2.vel().x() * v, h2.pos().y() + h2.vel().y() * v);
  }

  public List<Hailstone> parseInput(List<String> lines) {
    List<Hailstone> hailstones = new ArrayList<>();
    for (String line : lines) {
      String[] posStr = line.split(" @ ")[0].split(", ");
      String[] velStr = line.split(" @ ")[1].split(", ");

      hailstones.add(
          new Hailstone(
              new Vector3dLong(
                  Long.parseLong(posStr[0].strip()),
                  Long.parseLong(posStr[1].strip()),
                  Long.parseLong(posStr[2].strip())),
              new Vector3dLong(
                  Long.parseLong(velStr[0].strip()),
                  Long.parseLong(velStr[1].strip()),
                  Long.parseLong(velStr[2].strip()))));
    }
    return hailstones;
  }

  public record Hailstone(Vector3dLong pos, Vector3dLong vel) {}
}
