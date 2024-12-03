package com.coding.challenges.adventofcode.year2020.Day1;

import java.io.IOException;
import java.util.List;
import com.coding.challenges.adventofcode.utils.Day;

public class Day1 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day1 day = new Day1();

        day.printPart1("sample-input", 514579L);
        day.printPart1("input", 744475L);

        day.printPart2("sample-input", 241861950L);
        day.printPart2("input", 70276940L);
    }

    @Override
    public Long part1(List<String> lines) {
        List<Long> expenses = extractExpenses(lines);

        for (int i = 0; i < expenses.size(); i++) {
            for (int j = i + 1; j < expenses.size(); j++) {
                if (expenses.get(i) + expenses.get(j) == 2020) {
                    return expenses.get(i) * expenses.get(j);
                }
            }
        }

        return 0L;
    }

    @Override
    public Long part2(List<String> lines) {
        List<Long> expenses = extractExpenses(lines);

        for (int i = 0; i < expenses.size(); i++) {
            for (int j = i + 1; j < expenses.size(); j++) {
                for (int k = j + 1; k < expenses.size(); k++) {
                    if (expenses.get(i) + expenses.get(j) + expenses.get(k) == 2020) {
                        return expenses.get(i) * expenses.get(j) * expenses.get(k);
                    }
                }
            }
        }

        return 0L;
    }

    private static List<Long> extractExpenses(List<String> lines) {
        return lines.stream()
          .mapToLong(Long::parseLong)
          .boxed()
          .sorted(Long::compareTo)
          .toList();
    }
}
