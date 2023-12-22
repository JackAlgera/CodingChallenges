package adventofcode.Year2023.Day22;

import adventofcode.utils.Day;
import adventofcode.utils.Vector2d;
import adventofcode.utils.enums.Vector3d;

import java.io.IOException;
import java.util.*;

public class Day22 extends Day<Integer> {

    public static void main(String[] args) throws IOException {
        Day22 day = new Day22();

        day.printPart1("sample-input", 5);
        day.printPart1("input", 468);

        day.printPart2("sample-input", 7);
        day.printPart2("input", 75358);
    }

    @Override
    public Integer part1(List<String> lines) throws IOException {
        BricksState bricksState = dropBricks(bricks(lines));

        int disintegratableBricks = 0;
        for (Brick brick : bricksState.bricks) {
            List<Brick> tempBricks = new ArrayList<>(bricksState.bricks());
            tempBricks.remove(brick);
            if (dropBricks(tempBricks).totalDropped() == 0) {
                disintegratableBricks++;
            }
        }

        return disintegratableBricks;
    }

    @Override
    public Integer part2(List<String> lines) throws IOException {
        BricksState bricksState = dropBricks(bricks(lines));

        int totalDropped = 0;
        for (Brick brick : bricksState.bricks) {
            List<Brick> tempBricks = new ArrayList<>(bricksState.bricks());
            tempBricks.remove(brick);
            totalDropped += dropBricks(tempBricks).totalDropped();
        }

        return totalDropped;
    }

    public BricksState dropBricks(List<Brick> bricks) {
        Map<Vector2d, Integer> highestZMap = new HashMap<>();
        List<Brick> setBricks = new ArrayList<>();
        int totalDropped = 0;

        for (Brick brick : bricks) {
            int highestZ = highestZ(brick, highestZMap);
            for (int x = brick.c1.x(); x <= brick.c2.x(); x++) {
                for (int y = brick.c1.y(); y <= brick.c2.y(); y++) {
                    Vector2d pos = new Vector2d(x, y);
                    highestZMap.put(pos, highestZ);
                }
            }
            Brick setBrick = brick.dropTo(highestZ);
            if (!setBrick.equals(brick)) {
                totalDropped++;
            }
            setBricks.add(setBrick);
        }
        return new BricksState(setBricks, totalDropped);
    }

    public int highestZ(Brick brick, Map<Vector2d, Integer> highestZMap) {
        int highestZ = 0;
        for (int x = brick.c1.x(); x <= brick.c2.x(); x++) {
            for (int y = brick.c1.y(); y <= brick.c2.y(); y++) {
                Vector2d pos = new Vector2d(x, y);
                if (highestZMap.containsKey(pos)) {
                    highestZ = Math.max(highestZ, highestZMap.get(pos));
                }
            }
        }
        return highestZ + brick.c2.z() - brick.c1.z() + 1;
    }

    public List<Brick> bricks(List<String> lines) {
        return lines.stream()
            .map(line -> new Brick(
                new Vector3d(Integer.parseInt(line.split("~")[0].split(",")[0]),
                    Integer.parseInt(line.split("~")[0].split(",")[1]),
                    Integer.parseInt(line.split("~")[0].split(",")[2])),
                new Vector3d(Integer.parseInt(line.split("~")[1].split(",")[0]),
                    Integer.parseInt(line.split("~")[1].split(",")[1]),
                    Integer.parseInt(line.split("~")[1].split(",")[2]))))
            .sorted(Comparator.comparingInt(brick -> Math.min(brick.c1.z(), brick.c2.z())))
            .toList();
    }

    public record Brick(Vector3d c1, Vector3d c2) {
        public Brick dropTo(int z) {
            return new Brick(new Vector3d(c1.x(), c1.y(), z), new Vector3d(c2.x(), c2.y(), z + c2.z() - c1.z()));
        }
    }
    public record BricksState(List<Brick> bricks, int totalDropped) {}
}
