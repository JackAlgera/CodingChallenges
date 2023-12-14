package adventofcode.Year2020.Day8;

import adventofcode.utils.Day;
import lombok.With;

import java.io.IOException;
import java.util.List;

public class Day8 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day8 day = new Day8();

        day.printPart1("sample-input", 5L);
        day.printPart1("input", 1782L);

        day.printPart2("sample-input", 8L);
        day.printPart2("input", 797L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return getAcc(parseInput(lines), -1);
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        List<Instruction> instructions = parseInput(lines);
        for (int i = 0; i < instructions.size(); i++) {
            if (instructions.get(i).type().equals("acc")) {
                continue;
            }

            long acc = getAcc(instructions, i);
            if (acc > 0) {
                return acc;
            }
        }
        return 0L;
    }

    public long getAcc(List<Instruction> instructions, int indexToChange) {
        boolean[] visited = new boolean[instructions.size()];
        long acc = 0;
        int index = 0;

        while (index != instructions.size() && !visited[index]) {
            Instruction instruction = instructions.get(index);
            if (indexToChange == index) {
                instruction = instruction.switchType();
            }
            visited[index] = true;

            switch (instruction.type()) {
                case "acc" -> {
                    acc += instruction.val();
                    index++;
                }
                case "jmp" -> index += instruction.val();
                case "nop" -> index++;
            }
        }

        if (indexToChange < 0 || index == instructions.size()) {
            return acc;
        } else {
            return -1;
        }
    }

    public List<Instruction> parseInput(List<String> lines) {
        return lines.stream()
            .map(line -> new Instruction(
                line.split(" ")[0],
                Integer.parseInt(line.split(" ")[1])))
            .toList();
    }

    public record Instruction(String type, int val) {
        public Instruction switchType() {
            return switch (type) {
                case "jmp" -> new Instruction("nop", val);
                case "nop" -> new Instruction("jmp", val);
                default -> this;
            };
        }
    }
}
