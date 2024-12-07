package com.coding.challenges.adventofcode.year2020.Day4;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Day4 extends Day<Long> {

  private static final String[] REQUIRED_FIELDS =
      new String[] {"byr", "iyr", "eyr", "hgt", "hcl", "ecl", "pid"};

  public static void main(String[] args) throws IOException {
    Day4 day = new Day4();

    day.printPart1("sample-input-part1", 2L);
    day.printPart1("input", 264L);

    day.printPart2("sample-valid-passports", 4L);
    day.printPart2("sample-invalid-passports", 0L);
    day.printPart2("input", 224L);
  }

  @Override
  public Long part1(List<String> lines) {
    return getValidPassports(lines, false);
  }

  @Override
  public Long part2(List<String> lines) {
    return getValidPassports(lines, true);
  }

  private long getValidPassports(List<String> lines, boolean useStrictRules) {
    int i = 0;
    long validPassports = 0;
    while (i < lines.size()) {
      int k = 0;
      while ((i + k) < lines.size() && !lines.get(i + k).isBlank()) {
        k++;
      }
      if (isValidPassport(lines.subList(i, i + k), useStrictRules)) {
        validPassports++;
      }
      i += k + 1;
    }

    return validPassports;
  }

  public boolean isValidPassport(List<String> inputs, boolean useStrictRules) {
    String joinedInputs = String.join(" ", inputs);
    Map<String, String> fields =
        Arrays.stream(joinedInputs.split(" "))
            .map(s -> s.split(":"))
            .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    for (String field : REQUIRED_FIELDS) {
      if (!joinedInputs.contains(field)) {
        return false;
      }

      if (useStrictRules) {
        switch (field) {
          case "byr":
            int byr = Integer.parseInt(fields.get(field));
            if (byr < 1920 || byr > 2002) {
              return false;
            }
            break;
          case "iyr":
            int iyr = Integer.parseInt(fields.get(field));
            if (iyr < 2010 || iyr > 2020) {
              return false;
            }
            break;
          case "eyr":
            int eyr = Integer.parseInt(fields.get(field));
            if (eyr < 2020 || eyr > 2030) {
              return false;
            }
            break;
          case "hgt":
            String hgt = fields.get(field);
            if (hgt.matches("[0-9]+cm")) {
              int value = Integer.parseInt(hgt.substring(0, hgt.length() - 2));
              if (value < 150 || value > 193) {
                return false;
              }
            } else if (hgt.matches("[0-9]+in")) {
              int value = Integer.parseInt(hgt.substring(0, hgt.length() - 2));
              if (value < 59 || value > 76) {
                return false;
              }
            } else {
              return false;
            }
            break;
          case "hcl":
            String hcl = fields.get(field);
            if (!hcl.matches("#[0-9a-f]{6}")) {
              return false;
            }
            break;
          case "ecl":
            String ecl = fields.get(field);
            if (!ecl.matches("amb|blu|brn|gry|grn|hzl|oth")) {
              return false;
            }
            break;
          case "pid":
            String pid = fields.get(field);
            if (!pid.matches("[0-9]{9}")) {
              return false;
            }
            break;
        }
      }
    }

    return true;
  }
}
