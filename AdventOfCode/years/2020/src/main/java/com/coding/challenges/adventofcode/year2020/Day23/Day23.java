package com.coding.challenges.adventofcode.year2020.Day23;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.coding.challenges.adventofcode.utils.Day;

public class Day23 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day23 day = new Day23();

    day.printPart1("sample-input", 67384529L);
    day.printPart1("input", 49725386L);
//
//    day.printPart2("sample-input", 0L);
//    day.printPart2("input", 0L);
  }

  @Override
  public Long part1(List<String> lines) {
    State current = new State(Integer.parseInt("" + lines.get(0).charAt(0)), lines.get(0));
    for (int i = 0; i < 100; i++) {
      current = current.move();
    }
    return Long.parseLong(current.label());
  }

  @Override
  public Long part2(List<String> lines) {
    State current = new State(Integer.parseInt("" + lines.get(0).charAt(0)), lines.get(0));
    Map<State, State> cache = new HashMap<>();
    for (int i = 0; i < 10_000_000; i++) {
      if (cache.containsKey(current)) {
        current = cache.get(current);
      } else {
        State next = current.move();
        cache.put(current, next);
        current = next;
      }
    }
    return Long.parseLong(current.label());
  }

  public record State(int currentCup, String order) {
    State move() {
      List<Integer> cups = order.chars().mapToObj(c -> c - '0').toList();
      int index = cups.indexOf(currentCup);

      String newOrder = order;
      String removedCups = "";
      for (int i = 0; i < 3; i++) {
        int removedCup = cups.get((index + 1 + i) % cups.size());
        newOrder = newOrder.replace("" + removedCup, "");
        removedCups += removedCup;
      }

      int destinationCup = currentCup;
      do {
        destinationCup = (destinationCup - 1);
        if (destinationCup == 0) {
          destinationCup = 9;
        }
      } while (!newOrder.contains("" + destinationCup));

      cups = newOrder.chars().mapToObj(c -> c - '0').toList();
      index = cups.indexOf(destinationCup);
      newOrder = newOrder.substring(0, index + 1) + removedCups + newOrder.substring(index + 1);

      cups = newOrder.chars().mapToObj(c -> c - '0').toList();
      index = cups.indexOf(currentCup);
      int newCurrentCup = cups.get((index + 1) % cups.size());
      return new State(newCurrentCup, newOrder);
    }

    String label() {
      int index = order.indexOf(('1')) + 1;
      return order.substring(index) + order.substring(0, index - 1);
    }
  }
}