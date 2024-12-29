package com.coding.challenges.adventofcode.year2024.Day04;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction8;
import java.io.IOException;
import java.util.List;

public class Day04 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day04 day = new Day04();

    day.printPart1("sample-input", 18L);
    day.printPart1("input", 2562L);

    day.printPart2("sample-input", 9L);
    day.printPart2("input", 1902L);
  }

  @Override
  public Long part1(List<String> lines) {
    int maxI = lines.size();
    int maxJ = lines.get(0).length();
    String word = "XMAS";
    long total = 0L;

    for (int i = 0; i < maxI; i++) {
      for (int j = 0; j < maxJ; j++) {
        Pos pos = new Pos(i, j);
        if (lines.get(i).charAt(j) != 'X') {
          continue;
        }
        for (Direction8 d : Direction8.values()) {
          Pos newPos = pos;
          for (int k = 1; k < 4; k++) {
            newPos = newPos.move(d);
            if (!newPos.isValid(maxI, maxJ)) {
              break;
            }
            if (lines.get(newPos.i()).charAt(newPos.j()) != word.charAt(k)) {
              break;
            }
            if (k == 3) {
              total++;
            }
          }
        }
      }
    }
    return total;
  }

  @Override
  public Long part2(List<String> lines) {
    int maxI = lines.size();
    int maxJ = lines.get(0).length();
    long total = 0L;

    for (int i = 0; i < maxI; i++) {
      for (int j = 0; j < maxJ; j++) {
        Pos pos = new Pos(i, j);
        if (lines.get(i).charAt(j) != 'A') {
          continue;
        }
        Pos pos1a = pos.move(Direction8.NE);
        Pos pos1b = pos.move(Direction8.SW);
        Pos pos2a = pos.move(Direction8.NW);
        Pos pos2b = pos.move(Direction8.SE);
        if (!pos1a.isValid(maxI, maxJ)
            || !pos1b.isValid(maxI, maxJ)
            || !pos2a.isValid(maxI, maxJ)
            || !pos2b.isValid(maxI, maxJ)) {
          continue;
        }

        if (((contains(pos1a, lines, 'M') && contains(pos1b, lines, 'S'))
                || (contains(pos1a, lines, 'S') && contains(pos1b, lines, 'M')))
            && ((contains(pos2a, lines, 'M') && contains(pos2b, lines, 'S'))
                || (contains(pos2a, lines, 'S') && contains(pos2b, lines, 'M')))) {
          total++;
        }
      }
    }
    return total;
  }

  private boolean contains(Pos pos, List<String> lines, char c) {
    return lines.get(pos.i()).charAt(pos.j()) == c;
  }
}
