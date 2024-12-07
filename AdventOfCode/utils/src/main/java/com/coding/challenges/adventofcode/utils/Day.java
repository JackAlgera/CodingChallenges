package com.coding.challenges.adventofcode.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Day<T> {

  private final Pattern YEAR_PATTERN = Pattern.compile("year(\\d+)");

  public String getDayName() {
    return getClass().getSimpleName();
  }

  public List<String> extractInputLines(String fileName) throws IOException {
    String path =
        "AdventOfCode/years/%d/src/main/java/%s/%s.txt"
            .formatted(extractYear(), getClass().getPackageName().replaceAll("\\.", "/"), fileName);
    return Files.readAllLines(Paths.get(path));
  }

  private int extractYear() {
    Matcher m = YEAR_PATTERN.matcher(getClass().getPackageName());
    return m.find() ? Integer.parseInt(m.group(1)) : 2024;
  }

  public void printPart1(String fileName, T expectedResult) throws IOException {
    printResponse(1, fileName, part1(extractInputLines(fileName)), expectedResult);
  }

  public void printPart2(String fileName, T expectedResult) throws IOException {
    printResponse(2, fileName, part2(extractInputLines(fileName)), expectedResult);
    if (Objects.equals(fileName, "input")) System.out.println();
  }

  private void printResponse(int part, String fileName, T result, T expectedResult) {
    System.out.printf(
        String.format(
            "\n%s, Part %d: %s (%s)\t for %s",
            getDayName(),
            part,
            Objects.equals(result, expectedResult)
                ? Utilities.greenWord("" + result)
                : Utilities.redWord("" + result),
            expectedResult,
            Utilities.yellowWord(fileName)));
  }

  public abstract T part1(List<String> lines);

  public abstract T part2(List<String> lines);
}
