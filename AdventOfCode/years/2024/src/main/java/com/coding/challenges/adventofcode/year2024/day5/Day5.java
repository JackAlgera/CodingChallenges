package com.coding.challenges.adventofcode.year2024.day5;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import com.coding.challenges.adventofcode.utils.Day;

public class Day5 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day5 day = new Day5();

        day.printPart1("sample-input", 143L);
        day.printPart1("input", 4637L);

        day.printPart2("sample-input", 123L);
        day.printPart2("input", 6370L);
    }

    @Override
    public Long part1(List<String> lines) {
        Map<Integer, Set<Integer>> map = extractMap(lines);
        List<List<Integer>> pages = extractPages(lines);

        return pages.stream()
                    .filter(page -> isValid(page, map))
                    .mapToLong(page -> page.get(page.size() / 2))
                    .sum();
    }

    @Override
    public Long part2(List<String> lines) {
        Map<Integer, Set<Integer>> map = extractMap(lines);
        List<List<Integer>> pages = extractPages(lines);

        return pages.stream()
                    .filter(page -> !isValid(page, map))
                    .peek(page -> {
                        for (int i = 0; i < page.size(); i++) {
                            for (int j = page.size() - 1; j > i; j--) {
                                if (!map.containsKey(page.get(j))) {
                                    continue;
                                }
                                if (map.get(page.get(j)).contains(page.get(i))) {
                                    int temp = page.get(i);
                                    page.set(i, page.get(j));
                                    page.set(j, temp);
                                }
                            }
                        }
                    })
                    .mapToLong(page -> page.get(page.size() / 2))
                    .sum();
    }

    private boolean isValid(List<Integer> page, Map<Integer, Set<Integer>> map) {
        for (int i = page.size() - 1; i >= 0; i--) {
            if (!map.containsKey(page.get(i))) {
                continue;
            }

            Set<Integer> current = map.get(page.get(i));
            for (int j = i - 1; j >= 0; j--) {
                if (current.contains(page.get(j))) {
                    return false;
                }
            }
        }
        return true;
    }

    private Map<Integer, Set<Integer>> extractMap(List<String> lines) {
        Map<Integer, Set<Integer>> map = new HashMap<>();
        for (String line : lines) {
            if (line.isBlank()) {
                return map;
            }
            Integer left = Integer.parseInt(line.split("\\|")[0]);
            Integer right = Integer.parseInt(line.split("\\|")[1]);

            map.compute(left, (k, v) -> {
                if (v == null) {
                    v = new HashSet<>();
                }
                v.add(right);
                return v;
            });
        }

        return map;
    }

    private List<List<Integer>> extractPages(List<String> lines) {
        List<List<Integer>> pages = new ArrayList<>();
        int i = 0;
        while (!lines.get(i).isBlank()) {
            i++;
        }
        i++;
        while (i < lines.size()) {
            pages.add(new ArrayList<>(Arrays.stream(lines.get(i).split(","))
                .map(Integer::parseInt)
                .toList()));
            i++;
        }
        return pages;
    }
}