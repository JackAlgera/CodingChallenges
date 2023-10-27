package adventofcode.Year2022.Day11;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.*;

public class Day11 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day11 day = new Day11();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        day.printAllResults(1, day.getName(),
            day.part1(sampleInput), 10605L,
            day.part1(mainInput), 57838L);

        day.printAllResults(2, day.getName(),
            day.part2(sampleInput), 2713310158L,
            day.part2(mainInput), 15050382231L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return getMonkeyBusiness(1, 20, lines);
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return getMonkeyBusiness(2, 10_000, lines);
    }

    private Long getMonkeyBusiness(int part, int nbrRounds, List<String> lines) {
        List<Monkey> monkeys = extractMonkeys(lines);
        long lcm = monkeys.stream().mapToInt(Monkey::divisibleBy).reduce(1, (a, b) -> a * b);
        long[] inspected = new long[monkeys.size()];

        for (int k = 0; k < nbrRounds; k++) {
            for (Monkey monkey : monkeys) {
                while (!monkey.items().isEmpty()) {
                    long item = monkey.playNextItem();
                    inspected[monkey.id()] += 1;

                    item = part == 1 ? item / 3 : item % lcm;
                    int monkeyToAddIndex;

                    if (item % monkey.divisibleBy() == 0) {
                        monkeyToAddIndex = monkey.ifTrueMonkeyIndex();
                    } else {
                        monkeyToAddIndex = monkey.ifFalseMonkeyIndex();
                    }
                    monkeys.get(monkeyToAddIndex).items().add(item);
                }
            }
        }

        return Arrays.stream(inspected)
            .sorted()
            .skip(inspected.length - 2)
            .reduce(1L, (m1, m2) -> m1 * m2);
    }

    enum OperationType {
        MULTIPLICATION,
        ADDITION;
    }

    public List<Monkey> extractMonkeys(List<String> lines) {
        int totalMonkeys = lines.size() / 7;
        List<Monkey> monkeys = new ArrayList<>();
        for (int k = 0; k < totalMonkeys; k++) {
            List<String> monkeyLines = lines.subList(k * 7, (k + 1) * 7);
            monkeys.add(new Monkey(
                Integer.parseInt(monkeyLines.get(0).split(" ")[1].substring(0, 1)),
                new ArrayDeque<>(Arrays.stream(monkeyLines.get(1).split(":")[1].split(","))
                    .map(val -> Long.parseLong(val.strip()))
                    .toList()),
                monkeyLines.get(2).contains("*") ? OperationType.MULTIPLICATION : OperationType.ADDITION,
                monkeyLines.get(2).split(" ")[7].contains("old") ? -1 : Integer.parseInt(monkeyLines.get(2).split(" ")[7].strip()),
                Integer.parseInt(monkeyLines.get(3).split("by")[1].strip()),
                Integer.parseInt(monkeyLines.get(4).split("monkey")[1].strip()),
                Integer.parseInt(monkeyLines.get(5).split("monkey")[1].strip())));
        }
        return monkeys;
    }

    public record Monkey(int id, Queue<Long> items, OperationType operationType, int operationValue, int divisibleBy, int ifTrueMonkeyIndex, int ifFalseMonkeyIndex) {
        public long playNextItem() {
            Long item = items.poll();

            switch (operationType) {
                case ADDITION -> {
                    return (item + (operationValue < 0 ? item : operationValue));
                }
                case MULTIPLICATION -> {
                    return (item * (operationValue < 0 ? item : operationValue));
                }
                default -> {
                    return Long.MIN_VALUE;
                }
            }
        }
    }
}
