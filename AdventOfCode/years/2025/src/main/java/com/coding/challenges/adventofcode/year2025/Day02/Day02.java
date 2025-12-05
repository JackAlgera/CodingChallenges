package com.coding.challenges.adventofcode.year2025.Day02;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.regex.Pattern;

public class Day02 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day02 day = new Day02();

    day.printPart1("sample-input", 1227775554L);
    day.printPart1("input", 34826702005L);

    day.printPart2("sample-input", 4174379265L);
    day.printPart2("input", 43287141963L);
  }

  @Override
  public Long part1(List<String> lines) {
    return part(lines, this::isValidPart1);
  }

  @Override
  public Long part2(List<String> lines) {
    return part(lines, this::isValidPart2);
  }

  private long part(List<String> lines, Function<String, Boolean> isValid) {
    long count = 0L;
    for (String ids : lines.get(0).split(",")) {
      long left = Long.parseLong(ids.split("-")[0]);
      long right = Long.parseLong(ids.split("-")[1]);

      for (long i = left; i <= right; i++) {
        if (!isValid.apply(Long.toString(i))) {
          count += i;
        }
      }
    }

    return count;
  }

  public boolean isValidPart1(String id) {
    if (id.startsWith("0")) {
      return false;
    }

    if (id.length() % 2 != 0) {
      return true;
    }

    var mid = id.length() / 2;
    if (id.substring(0, mid).equals(id.substring(mid))) {
      return false;
    }

    return true;
  }

  public boolean isValidPart2(String id) {
    for (int i = 1; i < id.length(); i++) {
      if (i > id.length() / 2) {
        return true;
      }

      var regex = id.substring(0, i);
      var pattern = Pattern.compile(regex).matcher(id);
      int matches = 0;
      while (pattern.find()) {
        matches++;
      }

      if (matches * i == id.length()) {
        return false;
      }
    }
    return true;
  }
}
