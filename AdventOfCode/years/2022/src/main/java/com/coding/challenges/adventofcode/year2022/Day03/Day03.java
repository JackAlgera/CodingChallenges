package com.coding.challenges.adventofcode.year2022.Day03;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class Day03 extends Day<Integer> {

  public static void main(String[] args) throws IOException {
    Day03 day = new Day03();

    day.printPart1("sample-input", 157);
    day.printPart1("input", 8252);

    day.printPart2("sample-input", 70);
    day.printPart2("input", 2828);
  }

  @Override
  public Integer part1(List<String> lines) {
    int totalPoints = 0;

    for (String line : lines) {
      String firstCompartment = line.substring(0, line.length() / 2);
      String secondCompartment = line.substring(line.length() / 2);

      for (int i = 0; i < firstCompartment.length(); i++) {
        char val = firstCompartment.charAt(i);
        String valStr = "" + val;

        if (secondCompartment.contains(valStr)) {
          if (Pattern.matches("[a-z]", valStr)) {
            totalPoints += ((int) val) - 96;
          } else if (Pattern.matches("[A-Z]", valStr)) {
            totalPoints += ((int) val) - 64 + 26;
          }
          break;
        }
      }
    }

    return totalPoints;
  }

  @Override
  public Integer part2(List<String> lines) {
    String items = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    int total = 0;

    for (int k = 0; k < lines.size() / 3; k++) {
      String firstElf = lines.get(k * 3);
      String secondElf = lines.get(k * 3 + 1);
      String thirdElf = lines.get(k * 3 + 2);

      for (int i = 0; i < items.length(); i++) {
        char val = items.charAt(i);
        String valStr = "" + val;

        if (firstElf.contains(valStr) && secondElf.contains(valStr) && thirdElf.contains(valStr)) {
          if (Pattern.matches("[a-z]", valStr)) {
            total += ((int) val) - 96;
          } else if (Pattern.matches("[A-Z]", valStr)) {
            total += ((int) val) - 64 + 26;
          }
        }
      }
    }
    return total;
  }
}
