package com.coding.challenges.adventofcode.year2022.Day20;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

public class Day20 {

    private static final String INPUT_NAME = "Year2022/Day20/input.txt";

    public static void main(String[] args) throws IOException {
        Day20 day = new Day20();
        day.part(1, 1L, 17490L);
        day.part(10, 811589153L, 1632917375836L);
    }

    private void part(int roundsMixin, long encryptionKey, long expectedValue) throws IOException {
        List<String> lines = Files.readAllLines(Path.of(INPUT_NAME));

        List<Number> numbers = new ArrayList<>(IntStream.range(0, lines.size())
                .mapToObj(i -> new Number(Long.parseLong(lines.get(i)) * encryptionKey, i))
                .toList());

        List<Number> initialOrder = new ArrayList<>(numbers);

        for (int i = 0; i < roundsMixin; i++) {
            for (Number numberToMove : initialOrder) {
                int currentIndex = numbers.indexOf(numberToMove);
                long delta = numbers.remove(currentIndex).val;

                int newIndex = (int) (currentIndex + delta >= 0 ?
                        ((currentIndex + delta) % numbers.size()) :
                        (numbers.size() - (Math.abs(delta + currentIndex) % numbers.size())));

                numbers.add(newIndex, numberToMove);
            }
        }

        int indexZero = numbers.indexOf(numbers.stream().filter(n -> n.val == 0).findFirst().get());
        long v1 = numbers.get((indexZero + 1000) % numbers.size()).val;
        long v2 = numbers.get((indexZero + 2000) % numbers.size()).val;
        long v3 = numbers.get((indexZero + 3000) % numbers.size()).val;

        System.out.println("\n-------- Day 20 --------");
        System.out.println("1000th: " + v1 + ", 2000th: " + v2 + ", 3000th: " + v3);
        System.out.println("Final value: " + (v1 + v2 + v3));
        System.out.println("Expected value: " + expectedValue);
    }

    public record Number(long val, int index) {}
}
