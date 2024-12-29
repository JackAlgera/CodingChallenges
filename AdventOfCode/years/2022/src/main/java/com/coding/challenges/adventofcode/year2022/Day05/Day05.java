package com.coding.challenges.adventofcode.year2022.Day05;

import com.coding.challenges.adventofcode.utils.Utilities;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class Day05 {

  private static final String INPUT_NAME = "Year2022/Day5/input.txt";
  private static final int TOTAL_STACKS = 9;

  public static void main(String[] args) throws IOException {
    Day05 day = new Day05();
    day.part1();
    day.part2();
  }

  private void part1() throws IOException {
    BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

    List<Stack<String>> stacks =
        List.of(
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>());

    List<String> layers = new ArrayList<>();
    List<String> moves = new ArrayList<>();
    String currentLayer = br.readLine();

    while (br.ready()) {
      String nextLayer = br.readLine();

      if (nextLayer.equals("")) {
        break;
      }

      layers.add(currentLayer);
      currentLayer = nextLayer;
    }

    while (br.ready()) {
      moves.add(br.readLine());
    }

    for (int i = layers.size() - 1; i >= 0; i--) {
      String layer = layers.get(i);

      for (int j = 0; j < TOTAL_STACKS; j++) {
        int index = 1 + j * 4;

        if (layer.length() <= index) {
          break;
        }

        String crate = "" + layer.charAt(index);

        if (!crate.strip().equals("")) {
          stacks.get(j).push(crate);
        }
      }
    }

    for (String move : moves) {
      int nbrToMove = Integer.parseInt(move.split(" ")[1]);
      int fromCrate = Integer.parseInt(move.split(" ")[3]) - 1;
      int toCrate = Integer.parseInt(move.split(" ")[5]) - 1;

      for (int i = 0; i < nbrToMove; i++) {
        String crate = stacks.get(fromCrate).pop();
        stacks.get(toCrate).push(crate);
      }
    }

    System.out.println("-------- Day 5 - Part 1 --------");
    System.out.print("Top layer : ");
    stacks.forEach(
        stack -> {
          String lastCrate = " ";
          if (!stack.isEmpty()) {
            lastCrate = stack.pop();
          }
          System.out.print(lastCrate);
        });
    System.out.println("\nExpected top layer: RFFFWBPNS");
  }

  private void part2() throws IOException {
    BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

    List<Stack<String>> stacks =
        List.of(
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>(),
            new Stack<>());

    List<String> layers = new ArrayList<>();
    List<String> moves = new ArrayList<>();
    String currentLayer = br.readLine();

    while (br.ready()) {
      String nextLayer = br.readLine();

      if (nextLayer.equals("")) {
        break;
      }

      layers.add(currentLayer);
      currentLayer = nextLayer;
    }

    while (br.ready()) {
      moves.add(br.readLine());
    }

    for (int i = layers.size() - 1; i >= 0; i--) {
      String layer = layers.get(i);

      for (int j = 0; j < TOTAL_STACKS; j++) {
        int index = 1 + j * 4;

        if (layer.length() <= index) {
          break;
        }

        String crate = "" + layer.charAt(index);

        if (!crate.strip().equals("")) {
          stacks.get(j).push(crate);
        }
      }
    }

    for (String move : moves) {
      int nbrToMove = Integer.parseInt(move.split(" ")[1]);
      int fromCrate = Integer.parseInt(move.split(" ")[3]) - 1;
      int toCrate = Integer.parseInt(move.split(" ")[5]) - 1;

      String crateOrder = "";
      for (int i = 0; i < nbrToMove; i++) {
        crateOrder = stacks.get(fromCrate).pop() + crateOrder;
      }

      for (int i = 0; i < crateOrder.length(); i++) {
        stacks.get(toCrate).push("" + crateOrder.charAt(i));
      }
    }

    System.out.println("-------- Day 5 - Part 2 --------");
    System.out.print("Top layer : ");
    stacks.forEach(
        stack -> {
          String lastCrate = " ";
          if (!stack.isEmpty()) {
            lastCrate = stack.pop();
          }
          System.out.print(lastCrate);
        });
    System.out.println("\nExpected top layer: CQQBBJFCS");
  }
}
