package com.coding.challenges.adventofcode.year2024.Day23;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import com.coding.challenges.adventofcode.utils.Day;

public class Day23 extends Day<String> {

  public static void main(String[] args) throws IOException {
    Day23 day = new Day23();

    day.printPart1("sample-input", "7");
    day.printPart1("input", "1156");

    day.printPart2("sample-input", "co,de,ka,ta");
    day.printPart2("input", "bx,cx,dr,dx,is,jg,km,kt,li,lt,nh,uf,um");
  }

  @Override
  public String part1(List<String> lines) {
    Map<String, Set<String>> pcs = getPcs(lines);
    Set<Tuple> tuples = new HashSet<>();

    for (String pc1 : pcs.keySet()) {
      List<String> connections = new ArrayList<>(pcs.get(pc1));
      for (int i = 0; i < connections.size(); i++) {
        for (int j = i + 1; j < connections.size(); j++) {
          String pc2 = connections.get(i);
          String pc3 = connections.get(j);
          if (!pcs.get(pc2).contains(pc3)) {
            continue;
          }
          List<String> ordered = Stream.of(pc1, pc2, pc3).sorted().toList();
          tuples.add(new Tuple(ordered.get(0), ordered.get(1), ordered.get(2)));
        }
      }
    }

    return "" + tuples.stream().filter(t -> t.pc1().startsWith("t") || t.pc2().startsWith("t") || t.pc3().startsWith("t")).count();
  }

  @Override
  public String part2(List<String> lines) {
    Map<String, Set<String>> pcs = getPcs(lines);
    Set<String> longestConnection = getLongestConnection(pcs);
    return longestConnection.stream().sorted().collect(Collectors.joining(","));
  }

  public Set<String> getLongestConnection(Map<String, Set<String>> pcs) {
    Set<String> biggestGroup = new HashSet<>();
    for (String pc1 : pcs.keySet()) {
      Set<String> connections = pcs.get(pc1);
      Set<String> group = new HashSet<>();
      group.add(pc1);

      for (String pc2 : connections) {
        boolean shouldAdd = true;
        for (String pc3 : group) {
          if (!pcs.get(pc2).contains(pc3)) {
            shouldAdd = false;
            break;
          }
        }
        if (shouldAdd) {
          group.add(pc2);
        }
      }

      if (group.size() > biggestGroup.size()) {
        biggestGroup = group;
      }
    }

    return biggestGroup;
  }

  private Map<String, Set<String>> getPcs(List<String> lines) {
    Map<String, Set<String>> pcs =new HashMap<>();
    for (String connection : lines) {
      String pc1 = connection.split("-")[0];
      String pc2 = connection.split("-")[1];
      pcs.compute(pc1, (k, v) -> {
        if (v == null) return new HashSet<>(Set.of(pc2));
        v.add(pc2);
        return v;
      });
      pcs.compute(pc2, (k, v) -> {
        if (v == null) return new HashSet<>(Set.of(pc1));
        v.add(pc1);
        return v;
      });
    }

    return pcs;
  }

  private record Tuple(String pc1, String pc2, String pc3) {}
}