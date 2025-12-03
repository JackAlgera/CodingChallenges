package com.coding.challenges.adventofcode.year2025.Day03;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import com.coding.challenges.adventofcode.utils.Day;

public class Day03 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day03 day = new Day03();

    day.printPart1("sample-input", 357L);
    day.printPart1("input", 17408L);

    day.printPart2("sample-input", 3121910778619L);
    day.printPart2("input", 172740584266849L);
  }

  @Override
  public Long part1(List<String> lines) {
    return part(lines, 2);
  }

  @Override
  public Long part2(List<String> lines) {
    return part(lines, 12);
  }

  public Long part(List<String> lines, int size) {
    long count = 0L;

    for (String line : lines) {
      var values = Arrays.stream(line.split("")).limit(size).toList().toArray(new String[size]);
      var best = Long.parseLong(String.join("", values));

      for (int i = size; i < line.length(); i++) {
        var newValues = Arrays.copyOf(values, size + 1);
        newValues[size] = String.valueOf(line.charAt(i));
        int ignoredIndex = -1;
        for (int k = size - 1; k >= 0; k--) {
          long aVal = toLongWithIgnoredIndex(newValues, k);
          if (aVal > best) {
            best = aVal;
            ignoredIndex = k;
          }
        }
        if (ignoredIndex != -1) {
          var temp = new String[size];
          int index = 0;
          for (int k = 0; k < newValues.length; k++) {
            if (k != ignoredIndex) {
              temp[index++] = newValues[k];
            }
          }
          values = temp;
        }
      }
      count += best;
    }

    return count;
  }

  public long toLongWithIgnoredIndex(String[] values, int ignoredIndex) {
    StringBuilder stringBuilder = new StringBuilder();
    for (int i = 0; i < values.length; i++) {
      if (i != ignoredIndex) {
        stringBuilder.append(values[i]);
      }
    }
    return Long.parseLong(stringBuilder.toString());
  }
}