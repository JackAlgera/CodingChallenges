package adventofcode.Year2023.Day18;

import adventofcode.utils.Day;
import adventofcode.utils.enums.Direction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day18 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day18 day = new Day18();

        day.printPart1("sample-input", 62L);
        day.printPart1("input", 50603L);

        day.printPart2("sample-input", 952408144115L);
        day.printPart2("input", 96556251590677L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        List<Instruction> instructions = parseInputPart1(lines);
        return countLava(instructions);
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        List<Instruction> instructions = parseInputPart2(lines);
        return countLava(instructions);
    }

    public long countLava(List<Instruction> instructions) {
        long x = 0;
        long y = 0;
        long area = 0;
        long perimeter = 0;

        for (Instruction instruction : instructions) {
            perimeter += instruction.length;
            long dX = 0L;
            long dY = 0L;
            switch (instruction.direction) {
                case N -> dY += instruction.length;
                case E -> dX += instruction.length;
                case S -> dY -= instruction.length;
                case W -> dX -= instruction.length;
            }
            x += dX;
            y += dY;
            area += x * dY;
        }

        return Math.abs(area) + 1 + perimeter / 2;
    }

    public List<Instruction> parseInputPart1(List<String> lines) {
        return lines.stream()
            .map(line -> new Instruction(
                Direction.fromOrientation(line.substring(0, 1)),
                Long.parseLong(line.split(" ")[1])))
            .toList();
    }

    public List<Instruction> parseInputPart2(List<String> lines) {
        List<Instruction> instructions = new ArrayList<>();
        for (String line : lines) {
            String digits = line.split("#")[1].substring(0, 6);
            instructions.add(new Instruction(
                Direction.fromDigit(Integer.parseInt(digits.substring(5), 16)),
                Long.parseLong(digits.substring(0, 5), 16)));
        }
        return instructions;
    }

    public record Instruction(Direction direction, long length) {
    }
}
