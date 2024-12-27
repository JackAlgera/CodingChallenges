package com.coding.challenges.adventofcode.year2020.Day22;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.stream.Stream;
import com.coding.challenges.adventofcode.utils.Day;

public class Day22 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day22 day = new Day22();

    day.printPart1("sample-input", 306L);
    day.printPart1("input", 30780L);

    day.printPart2("sample-input", 291L);
    day.printPart2("input", 36621L);
  }

  @Override
  public Long part1(List<String> lines) {
    return part(lines, false);
  }

  @Override
  public Long part2(List<String> lines) {
    return part(lines, true);
  }

  public long part(List<String> lines, boolean isPart2) {
    Queue<Integer> p1 = new LinkedList<>();
    Queue<Integer> p2 = new LinkedList<>();
    extractDecks(lines, p1, p2);

    boolean p1Wins = recursiveCombat(p1, p2, isPart2, new HashMap<>());
    Queue<Integer> winner = p1Wins ? p1 : p2;
    long total = 0;
    int i = winner.size();
    while (!winner.isEmpty()) {
      total += (long) i-- * winner.poll();
    }
    return total;
  }

  public void extractDecks(List<String> lines, Queue<Integer> p1, Queue<Integer> p2) {
    Queue<Integer> currentQueue = p1;
    for (String line : lines) {
      if (line.startsWith("Player")) {
        continue;
      }
      if (line.isEmpty()) {
        currentQueue = p2;
        continue;
      }
      currentQueue.add(Integer.parseInt(line));
    }
  }

  public boolean recursiveCombat(Queue<Integer> p1, Queue<Integer> p2, boolean isPart2, Map<String, Boolean> cache) {
    String key = p1.toString() + p2.toString();
    if (cache.containsKey(key)) {
      return cache.get(key);
    }

    Set<String> p1Decks = new HashSet<>();
    Set<String> p2Decks = new HashSet<>();
    while (!p1.isEmpty() && !p2.isEmpty()) {
      if (isPart2 && (p1Decks.contains(p1.toString()) || p2Decks.contains(p2.toString()))) {
        cache.put(key, true);
        return true;
      }
      p1Decks.add(p1.toString());
      p2Decks.add(p2.toString());

      int c1 = p1.poll();
      int c2 = p2.poll();

      boolean p1Wins;
      if (isPart2 && c1 <= p1.size() && c2 <= p2.size()) {
        Queue<Integer> p1Copy = new LinkedList<>(p1.stream().limit(c1).toList());
        Queue<Integer> p2Copy = new LinkedList<>(p2.stream().limit(c2).toList());
        p1Wins = recursiveCombat(p1Copy, p2Copy, true, cache);
      } else {
        p1Wins = c1 > c2;
      }

      if (p1Wins) {
        p1.add(c1);
        p1.add(c2);
      } else {
        p2.add(c2);
        p2.add(c1);
      }
    }

    boolean result = !p1.isEmpty();
    cache.put(key, result);
    return result;
  }
}