package adventofcode.Year2020.Day9;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day9 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day9 day = new Day9();

        day.printPart1("sample-input", 127L);
        day.printPart1("input", 104054607L);

        day.printPart2("sample-input", 62L);
        day.printPart2("input", 13935797L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return getInvalidNumber(lines, lines.size() <= 20 ? 5 : 25);
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        long invalidNumber = getInvalidNumber(lines, lines.size() <= 20 ? 5 : 25);
        for (int i = 0; i < lines.size() - 2; i++) {
            for (int j = i + 2; j < lines.size(); j++) {
                long sum = lines.subList(i, j).stream()
                    .mapToLong(Long::parseLong)
                    .sum();

                if (sum > invalidNumber) {
                    break;
                }

                if (sum == invalidNumber) {
                    List<Long> parsedVals = lines.subList(i, j).stream()
                        .mapToLong(Long::parseLong)
                        .sorted()
                        .boxed()
                        .toList();
                    return parsedVals.get(0) + parsedVals.get(parsedVals.size() - 1);
                }
            }
        }
        return 0L;
    }

    public long getInvalidNumber(List<String> lines, int preambleSize) {
        for (int i = 0; i < lines.size() - preambleSize; i++) {
            Set<Long> preambleNumbers = lines.stream()
                .skip(i)
                .limit(preambleSize)
                .mapToLong(Long::parseLong)
                .boxed()
                .collect(Collectors.toSet());
            long target = Long.parseLong(lines.get(i + preambleSize));

            if (!isValid(preambleNumbers, target)) {
                return target;
            }
        }

        return 0L;
    }

    public boolean isValid(Set<Long> preambleNumbers, Long target) {
        for (Long number : preambleNumbers) {
            if (preambleNumbers.contains(target - number)) {
                return true;
            }
        }
        return false;
    }
}
