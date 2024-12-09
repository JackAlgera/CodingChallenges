package com.coding.challenges.adventofcode.year2024.Day8;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day8 extends Day<Integer> {

  public static void main(String[] args) throws IOException {
    Day8 day = new Day8();

    day.printPart1("sample-input", 14);
    day.printPart1("input", 313);

    day.printPart2("sample-input", 34);
    day.printPart2("input", 1064);
  }

  @Override
  public Integer part1(List<String> lines) {
    Map<Character, List<Pos>> nodePositions = extractNodePositions(lines);
    return nodePositions.values().stream()
        .flatMap(
            positions ->
                countAntinodes(positions, lines.size(), lines.getFirst().length()).stream())
        .collect(Collectors.toSet())
        .size();
  }

  @Override
  public Integer part2(List<String> lines) {
    Map<Character, List<Pos>> nodePositions = extractNodePositions(lines);
    return nodePositions.values().stream()
        .flatMap(
            positions ->
                countAntinodes2(positions, lines.size(), lines.getFirst().length()).stream())
        .collect(Collectors.toSet())
        .size();
  }

  private Set<Pos> countAntinodes2(List<Pos> positions, int maxI, int maxJ) {
    Set<Pos> antinodes = new HashSet<>();
    for (int i = 0; i < positions.size(); i++) {
      for (int j = i + 1; j < positions.size(); j++) {
        Pos pos1 = positions.get(i);
        Pos pos2 = positions.get(j);
        Pos delta = new Pos(pos2.i() - pos1.i(), pos2.j() - pos1.j());

        int k = 0;
        while (true) {
          Pos newPos1 = new Pos(pos1.i() + k * delta.i(), pos1.j() + k * delta.j());
          Pos newPos2 = new Pos(pos1.i() - k * delta.i(), pos1.j() - k * delta.j());
          if (newPos1.isValid(maxI, maxJ)) {
            antinodes.add(newPos1);
          }
          if (newPos2.isValid(maxI, maxJ)) {
            antinodes.add(newPos2);
          }
          if (!newPos1.isValid(maxI, maxJ) && !newPos2.isValid(maxI, maxJ)) {
            break;
          }
          k++;
        }
      }
    }
    return antinodes;
  }

  private Set<Pos> countAntinodes(List<Pos> positions, int maxI, int maxJ) {
    Set<Pos> antinodes = new HashSet<>();
    for (int i = 0; i < positions.size(); i++) {
      for (int j = i + 1; j < positions.size(); j++) {
        Pos pos1 = positions.get(i);
        Pos pos2 = positions.get(j);
        Pos delta = new Pos(pos2.i() - pos1.i(), pos2.j() - pos1.j());

        Pos newPos1 = new Pos(pos1.i() - delta.i(), pos1.j() - delta.j());
        Pos newPos2 = new Pos(pos2.i() + delta.i(), pos2.j() + delta.j());
        if (newPos1.isValid(maxI, maxJ)) {
          antinodes.add(newPos1);
        }
        if (newPos2.isValid(maxI, maxJ)) {
          antinodes.add(newPos2);
        }
      }
    }
    return antinodes;
  }

  private Map<Character, List<Pos>> extractNodePositions(List<String> lines) {
    Map<Character, List<Pos>> nodePositions = new HashMap<>();
    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.getFirst().length(); j++) {
        char c = lines.get(i).charAt(j);
        if (c != '.') {
          if (nodePositions.containsKey(c)) {
            nodePositions.get(c).add(new Pos(i, j));
          } else {
            nodePositions.put(c, new ArrayList<>(List.of(new Pos(i, j))));
          }
        }
      }
    }
    return nodePositions;
  }
}
