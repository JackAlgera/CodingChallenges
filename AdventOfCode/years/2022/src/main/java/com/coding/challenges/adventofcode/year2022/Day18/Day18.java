package com.coding.challenges.adventofcode.year2022.Day18;

import com.coding.challenges.adventofcode.utils.Utilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Day18 {

  private static final String INPUT_NAME = "Year2022/Day18/input.txt";
  private static final int[] NEIGHBORS_X = {0, 1, 0, -1, 0, 0};
  private static final int[] NEIGHBORS_Y = {1, 0, -1, 0, 0, 0};
  private static final int[] NEIGHBORS_Z = {0, 0, 0, 0, 1, -1};

  public static void main(String[] args) throws IOException {
    Day18 day = new Day18();
    day.part1();
    day.part2();
  }

  private void part1() throws IOException {
    BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
    Map<Cube, Boolean> cubeDict = new HashMap<>();

    while (br.ready()) {
      List<Integer> pos = Arrays.stream(br.readLine().split(",")).map(Integer::valueOf).toList();
      Cube cube = new Cube(pos.get(0), pos.get(1), pos.get(2));
      cubeDict.put(cube, true);
    }

    int totalSidesNotConnected = 0;

    for (Cube cube : cubeDict.keySet()) {
      for (int k = 0; k < NEIGHBORS_X.length; k++) {
        int neighborX = cube.x() + NEIGHBORS_X[k];
        int neighborY = cube.y() + NEIGHBORS_Y[k];
        int neighborZ = cube.z() + NEIGHBORS_Z[k];

        if (!cubeDict.containsKey(new Cube(neighborX, neighborY, neighborZ))) {
          totalSidesNotConnected++;
        }
      }
    }

    System.out.println("\n-------- Day 18 - Part 1 --------");
    System.out.println("Total exposed sides: " + totalSidesNotConnected);
    System.out.println("Expected total exposed sides: 4636");
  }

  private void part2() throws IOException {
    BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
    Map<Cube, Integer> cubeDict = new HashMap<>();
    List<Cube> cubes = new ArrayList<>();

    while (br.ready()) {
      List<Integer> pos = Arrays.stream(br.readLine().split(",")).map(Integer::valueOf).toList();
      Cube cube = new Cube(pos.get(0), pos.get(1), pos.get(2));
      cubes.add(cube);
      cubeDict.put(cube, 1);
    }

    bfs(new Cube(-1, -1, -1), 2, cubeDict);

    int totalSidesNotConnected = 0;

    for (Cube cube : cubes) {
      for (int k = 0; k < NEIGHBORS_X.length; k++) {
        int neighborX = cube.x() + NEIGHBORS_X[k];
        int neighborY = cube.y() + NEIGHBORS_Y[k];
        int neighborZ = cube.z() + NEIGHBORS_Z[k];

        Cube neighborCube = new Cube(neighborX, neighborY, neighborZ);

        if (cubeDict.containsKey(neighborCube) && cubeDict.get(neighborCube) == 2) {
          totalSidesNotConnected++;
        }
      }
    }

    System.out.println("\n-------- Day 18 - Part 2 --------");
    System.out.println("Total exposed sides: " + totalSidesNotConnected);
    System.out.println("Expected total exposed sides: 2572");
  }

  public void bfs(Cube root, int val, Map<Cube, Integer> cubeDict) {
    Stack<Cube> stack = new Stack<>();
    stack.push(root);

    while (!stack.isEmpty()) {
      Cube cube = stack.pop();
      if (!isValidPosition(cube)) {
        continue;
      }

      if (cubeDict.containsKey(cube)) {
        continue;
      }

      cubeDict.put(cube, val);
      for (int k = 0; k < NEIGHBORS_X.length; k++) {
        int neighborX = cube.x() + NEIGHBORS_X[k];
        int neighborY = cube.y() + NEIGHBORS_Y[k];
        int neighborZ = cube.z() + NEIGHBORS_Z[k];
        stack.push(new Cube(neighborX, neighborY, neighborZ));
      }
    }
  }

  public boolean isValidPosition(Cube cube) {
    return cube.x() >= -5
        && cube.x() < 25
        && cube.y() >= -5
        && cube.y() < 25
        && cube.z() >= -5
        && cube.z() < 25;
  }

  public record Cube(int x, int y, int z) {}
}
