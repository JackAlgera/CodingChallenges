package com.coding.challenges.adventofcode.year2025.Day05;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day05 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day05 day = new Day05();

    day.printPart1("sample-input", 3L);
    day.printPart1("input", 520L);

    day.printPart2("sample-input", 14L);
    day.printPart2("input", 347338785050515L);
  }

  @Override
  public Long part1(List<String> lines) {
    var input = Input.fromInput(lines);
    var freshIngredients = 0L;
    for (long id : input.ids) {
      if (input.freshRanges().stream().anyMatch(range -> id >= range[0] && id <= range[1])) {
        freshIngredients++;
      }
    }
    return freshIngredients;
  }

  @Override
  public Long part2(List<String> lines) {
    var input = Input.fromInput(lines);
    var mergedRanges = new ArrayList<long[]>();

    for (long[] range : input.freshRanges()) {
      mergedRanges.add(range);
      mergedRanges = mergeOverlappingRanges(mergedRanges);
    }

    return mergedRanges.stream().mapToLong(range -> 1 + range[1] - range[0]).sum();
  }

  private ArrayList<long[]> mergeOverlappingRanges(ArrayList<long[]> ranges) {
    ranges.sort(Comparator.comparingLong(r -> r[0]));
    var mergedRanges = new ArrayList<long[]>();

    int i = 0;
    while (i < ranges.size()) {
      long start = ranges.get(i)[0];
      long end = ranges.get(i)[1];

      while (i + 1 < ranges.size() && ranges.get(i + 1)[0] <= end) {
        end = Math.max(end, ranges.get(i + 1)[1]);
        i++;
      }

      mergedRanges.add(new long[] {start, end});
      i++;
    }

    return mergedRanges;
  }

  record Input(List<long[]> freshRanges, List<Long> ids) {
    public static Input fromInput(List<String> lines) {
      int i = 0;
      var freshRanges = new ArrayList<long[]>();

      while (i < lines.size()) {
        String line = lines.get(i);
        freshRanges.add(
            new long[] {Long.parseLong(line.split("-")[0]), Long.parseLong(line.split("-")[1])});

        i++;
        if (lines.get(i).isBlank()) {
          i++;
          break;
        }
      }

      var ids = lines.stream().skip(i).map(Long::parseLong).toList();

      return new Input(freshRanges, ids);
    }
  }
}
