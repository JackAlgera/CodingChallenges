package com.coding.challenges.adventofcode.year2024.Day17;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day17 extends Day<String> {

  public static void main(String[] args) throws IOException {
    Day17 day = new Day17();

    day.printPart1("sample-input-1", "4,6,3,5,6,3,5,2,1,0");
    day.printPart1("sample-input-2", "5,7,3,0");
    day.printPart1("input", "4,1,5,3,1,5,3,5,7");

    day.printPart2("sample-input-1", "29328");
    day.printPart2("sample-input-2", "117440");
    day.printPart2("input", "164542125272765");
  }

  @Override
  public String part1(List<String> lines) {
    List<Instruction> instructions = extractInstructions(lines);

    long registerA = Long.parseLong(lines.get(0).split(" A: ")[1]);
    List<Integer> output = new ArrayList<>();

    while (registerA != 0) {
      List<Long> loop = runOneLoop(instructions, registerA);
      output.add(loop.get(1).intValue());
      registerA = loop.get(0);
    }

    return output.stream().map(String::valueOf).collect(Collectors.joining(","));
  }

  @Override
  public String part2(List<String> lines) {
    List<Long> programs =
        Arrays.stream(lines.get(4).substring(9).split(","))
            .mapToLong(Long::parseLong)
            .boxed()
            .toList();

    List<Long> solutions = new ArrayList<>();
    List<Instruction> instructions = extractInstructions(lines);
    for (int i = 0; i < 8; i++) {
      solve(solutions, instructions, programs, i, 0);
    }

    return "" + solutions.get(0);
  }

  public void solve(
      List<Long> solutions,
      List<Instruction> instructions,
      List<Long> programs,
      long registerA,
      int col) {
    if (!Objects.equals(
        runOneLoop(instructions, registerA).get(1), programs.get(programs.size() - col - 1))) {
      return;
    }

    if (col == programs.size() - 1) {
      solutions.add(registerA);
    } else {
      for (int i = 0; i < 8; i++) {
        solve(solutions, instructions, programs, registerA * 8 + i, col + 1);
      }
    }
  }

  public List<Long> runOneLoop(List<Instruction> instructions, long registerA) {
    long registerB = 0;
    long registerC = 0;
    int i = 0;
    long out = -1;
    while (out < 0) {
      Instruction instruction = instructions.get(i);
      long comboOperand = instruction.comboOperand(registerA, registerB, registerC);
      i += 1;
      switch (instruction.opcode) {
        case 0 -> registerA = registerA >> comboOperand;
        case 1 -> registerB = registerB ^ instruction.operand;
        case 2 -> registerB = comboOperand % 8;
          //        case 3 -> {
          //          if (registerA != 0) i = 0;
          //        }
        case 4 -> registerB = registerB ^ registerC;
        case 5 -> out = (int) (comboOperand % 8);
        case 6 -> registerB = registerA >> comboOperand;
        case 7 -> registerC = registerA >> comboOperand;
      }
    }

    return List.of(registerA, out);
  }

  public List<Instruction> extractInstructions(List<String> lines) {
    List<Integer> programs =
        Arrays.stream(lines.get(4).substring(9).split(","))
            .mapToInt(Integer::parseInt)
            .boxed()
            .toList();
    return IntStream.range(0, programs.size() / 2)
        .map(i -> i * 2)
        .mapToObj(i -> new Instruction(programs.get(i), programs.get(i + 1)))
        .toList();
  }

  public record Instruction(int opcode, int operand) {
    public long comboOperand(long registerA, long registerB, long registerC) {
      return switch (operand) {
        case 0, 1, 2, 3 -> operand;
        case 4 -> registerA;
        case 5 -> registerB;
        case 6 -> registerC;
        default -> throw new IllegalStateException("Unexpected value: " + operand);
      };
    }
  }
}
