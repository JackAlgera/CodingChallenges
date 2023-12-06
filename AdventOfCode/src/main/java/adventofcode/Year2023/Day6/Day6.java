package adventofcode.Year2023.Day6;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

public class Day6 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day6 day = new Day6();

        day.printPart1("sample-input", 288L);
        day.printPart1("input", 1L);

        day.printPart2("sample-input", 71503L);
        day.printPart2("input", 24655068L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        List<Integer> times = parseInputPart1(lines, 0);
        List<Integer> distances = parseInputPart1(lines, 1);

        long total = 1L;
        for (int i = 0; i < times.size(); i++) {
            total *= countWins(times.get(i), distances.get(i));
        }

        return total;
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return countWins(parseInputPart2(lines, 0), parseInputPart2(lines, 1));
    }

    private static long countWins(long time, long distance) {
        long count = 0;
        for (int t = 0; t < time; t++) {
            long totalDistance = t * (time - t);
            if (totalDistance > distance) {
                count++;
            }
        }
        return count;
    }

    private static List<Integer> parseInputPart1(List<String> lines, int index) {
        return Arrays.stream(lines.get(index).split(":")[1].trim().split("[ ]+"))
          .mapToInt(Integer::parseInt)
          .boxed()
          .toList();
    }

    private static long parseInputPart2(List<String> lines, int index) {
        return Long.parseLong(lines.get(index)
          .split(":")[1]
          .trim()
          .replace(" ", ""));
    }
}
