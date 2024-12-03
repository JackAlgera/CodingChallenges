package com.coding.challenges.adventofcode.year2023.Day15;

import com.coding.challenges.adventofcode.utils.Day;

import java.io.IOException;
import java.util.*;

public class Day15 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day15 day = new Day15();

        day.printPart1("sample-input", 1320L);
        day.printPart1("input", 516469L);

        day.printPart2("sample-input", 145L);
        day.printPart2("input", 221627L);
    }

    @Override
    public Long part1(List<String> lines) {
        return Arrays.stream(lines.get(0).split(","))
            .mapToLong(this::generateHash)
            .sum();
    }

    @Override
    public Long part2(List<String> lines) {
        Map<Long, List<Lens>> map = new HashMap<>();
        for (String step : lines.get(0).split(",")) {
            String label = step.split("[=-]")[0];
            long index = generateHash(label);
            if (!map.containsKey(index)) {
                map.put(index, new ArrayList<>());
            }

            if (step.contains("=")) {
                putLens(map.get(index), new Lens(label, Long.parseLong(step.split("=")[1])));
            } else {
                removeLens(map.get(index), label);
            }
        }

        long total = 0;
        for (Map.Entry<Long, List<Lens>> box : map.entrySet()) {
            for (int i = 0; i < box.getValue().size(); i++) {
                total += (box.getKey() + 1) * (i + 1) * box.getValue().get(i).val;
            }
        }
        return total;
    }

    public void putLens(List<Lens> lenses, Lens lensToAdd) {
        for (int i = 0; i < lenses.size(); i++) {
            if (lenses.get(i).label.equals(lensToAdd.label)) {
                lenses.set(i, lensToAdd);
                return;
            }
        }
        lenses.add(lensToAdd);
    }

    public void removeLens(List<Lens> lenses, String label) {
        for (Lens lens : lenses) {
            if (lens.label.equals(label)) {
                lenses.remove(lens);
                return;
            }
        }
    }

    public long generateHash(String label) {
        long current = 0;
        for (char c : label.toCharArray()) {
            current += c;
            current *= 17;
            current = current % 256;
        }
        return current;
    }

    public record Lens(String label, long val) {}
}
