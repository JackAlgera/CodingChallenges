package adventofcode.Year2024.Day2;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Day2 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day2 day = new Day2();

        day.printPart1("sample-input", 2L);
        day.printPart1("input", 306L);

        day.printPart2("sample-input", 4L);
        day.printPart2("input", 366L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return lines.stream()
                .map(line -> line.split(" "))
                .map(report -> Arrays.stream(report).mapToInt(Integer::parseInt).toArray())
                .filter(this::isSafe)
                .count();
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return lines.stream()
                    .map(line -> line.split(" "))
                    .map(report -> Arrays.stream(report).mapToInt(Integer::parseInt).toArray())
                    .filter(report -> {
                        for (int k = 0; k < report.length; k++) {
                            int j = 0;
                            int[] copy = new int[report.length - 1];
                            for (int i = 0; i < report.length; i++) {
                                if (i == k) {
                                    continue;
                                }
                                copy[j++] = report[i];
                            }
                            if (isSafe(copy)) {
                                return true;
                            }
                        }
                        return false;
                    })
                    .count();
    }

    private boolean isSafe(int[] report) {
        boolean isIncreasing = true;
        boolean isDecreasing = true;
        for (int i = 0; i < report.length - 1; i++) {
            int a = report[i];
            int b = report[i + 1];
            if (a == b || Math.abs(a - b) > 3) {
                return false;
            }
            if (a > b) {
                isIncreasing = false;
            } else {
                isDecreasing = false;
            }
        }
        return isIncreasing || isDecreasing;
    }
}