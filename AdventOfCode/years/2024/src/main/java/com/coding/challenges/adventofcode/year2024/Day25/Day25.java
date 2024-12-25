package com.coding.challenges.adventofcode.year2024.Day25;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day25 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day25 day = new Day25();

    day.printPart1("sample-input", 3L);
    day.printPart1("input", 3663L);
  }

  @Override
  public Long part1(List<String> lines) {
    List<Pins> keys = new ArrayList<>();
    List<Pins> locks = new ArrayList<>();
    int i = 0;
    while (i < lines.size()) {
      if (lines.get(i).equals("#####")) {
        // Key
        keys.add(extractPins(lines, i + 1, true));
      } else {
        // Lock
        locks.add(extractPins(lines, i + 1 + 4, false));
      }
      i += 8;
    }

    long total = 0L;
    for (Pins key : keys) {
      for (Pins lock : locks) {
        if (fit(key, lock)) {
          total++;
        }
      }
    }

    return total;
  }

  @Override
  public Long part2(List<String> lines) {
    return 0L;
  }

  public boolean fit(Pins key, Pins lock) {
    for (int j = 0; j < 5; j++) {
      if (key.pins().get(j) + lock.pins().get(j) > 5) {
        return false;
      }
    }
    return true;
  }

  public Pins extractPins(List<String> lines, int startI, boolean isKey) {
    List<Integer> pins = new ArrayList<>();
    for (int j = 0; j < 5; j++) {
      int height = 0;
      for (int i = 0; i < 5; i++) {
        if (lines.get(startI + (isKey ? i : -i)).charAt(j) == '#') {
          height++;
        } else {
          break;
        }
      }
      pins.add(height);
    }

    return new Pins(pins);
  }

  public record Pins(List<Integer> pins) {}
}
