package com.coding.challenges.adventofcode.year2022.Day1;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import com.coding.challenges.adventofcode.utils.Day;

public class Day1 extends Day<Integer> {

    public static void main(String[] args) throws IOException {
        Day1 day = new Day1();

        day.printPart1("sample-input", 24000);
        day.printPart1("input", 66186);

        day.printPart2("sample-input", 45000);
        day.printPart2("input", 196804);
    }

    @Override
    public Integer part1(List<String> lines) {
        List<Integer> allElfCalories = getElfCalories(lines);

        return allElfCalories.stream()
            .max(Comparator.naturalOrder()).get();
    }

    @Override
    public Integer part2(List<String> lines) {
        List<Integer> allElfCalories = getElfCalories(lines);

        return allElfCalories.stream()
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .mapToInt(Integer::intValue).sum();
    }

    private static List<Integer> getElfCalories(List<String> lines) {
        int currentElfCalories = 0;
        List<Integer> allElfCalories = new ArrayList<>();

        for (String line : lines) {
            if (line.equals("")) {
                allElfCalories.add(currentElfCalories);
                currentElfCalories = 0;
            } else {
                currentElfCalories += Integer.parseInt(line);
            }
        }
        allElfCalories.add(currentElfCalories);
        return allElfCalories;
    }
}
