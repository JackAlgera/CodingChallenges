package com.coding.challenges.adventofcode.year2019.Day02;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day02 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day02 day = new Day02();

    day.printPart1("sample-input", 3500L);
    day.printPart1("input", 3716293L);

    day.printPart2("input", 6429L);
  }

  @Override
  public Long part1(List<String> lines) {
    List<Integer> program =
        new ArrayList<>(Stream.of(lines.get(0).split(",")).map(Integer::parseInt).toList());

    if (program.size() > 12) {
      program.set(1, 12);
      program.set(2, 2);
    }

    return runProgram(program);
  }

  @Override
  public Long part2(List<String> lines) {
    for (int i = 0; i < 100; i++) {
      for (int j = 0; j < 100; j++) {
        List<Integer> program =
            new ArrayList<>(Stream.of(lines.get(0).split(",")).map(Integer::parseInt).toList());

        program.set(1, i);
        program.set(2, j);

        long result = runProgram(program);
        if (result == 19690720L) {
          return 100L * i + j;
        }
      }
    }
    return 0L;
  }

  public long runProgram(List<Integer> program) {
    for (int i = 0; i < program.size(); i += 4) {
      int opcode = program.get(i);

      if (opcode == 99) {
        break;
      }

      int a = program.get(program.get(i + 1));
      int b = program.get(program.get(i + 2));
      int c = program.get(i + 3);

      switch (opcode) {
        case 1:
          program.set(c, a + b);
          break;
        case 2:
          program.set(c, a * b);
          break;
        default:
          throw new IllegalArgumentException("Invalid opcode: " + opcode);
      }
    }

    return program.get(0);
  }
}
