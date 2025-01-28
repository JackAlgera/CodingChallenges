package com.coding.challenges.adventofcode.year2019.Day01;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;

public class Day01 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day01 day = new Day01();

    day.printPart1("sample-input", 34241L);
    day.printPart1("input", 3311492L);

    day.printPart2("sample-input", 51316L);
    day.printPart2("input", 4964376L);
  }

  @Override
  public Long part1(List<String> lines) {
    return lines.stream().map(Long::parseLong).mapToLong(m -> m / 3 - 2).sum();
  }

  @Override
  public Long part2(List<String> lines) {
    return lines.stream().map(Long::parseLong).mapToLong(this::calculateFuel).sum();
  }

  public Long calculateFuel(Long mass) {
    long fuel = mass / 3 - 2;
    return fuel <= 0 ? 0 : fuel + calculateFuel(fuel);
  }
}
