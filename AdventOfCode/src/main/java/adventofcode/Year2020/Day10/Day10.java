package adventofcode.Year2020.Day10;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Day10 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day10 day = new Day10();

        day.printPart1("sample-input-1", 35L);
        day.printPart1("sample-input-2", 220L);
        day.printPart1("input", 2310L);

        day.printPart2("sample-input-1", 8L);
        day.printPart2("sample-input-2", 19208L);
        day.printPart2("input", 64_793_042_714_624L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        long oneJoltDiff = 1;
        long threeJoltDiff = 1;

        List<Integer> sortedAdapters = lines.stream()
            .mapToInt(Integer::parseInt)
            .sorted()
            .boxed()
            .toList();
        for (int i = 0; i < lines.size() - 1; i++) {
            int delta = sortedAdapters.get(i + 1) - sortedAdapters.get(i);
            if (delta == 1) {
                oneJoltDiff++;
            }
            if (delta == 3) {
                threeJoltDiff++;
            }
        }

        return oneJoltDiff * threeJoltDiff;
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        List<Integer> adapters = new ArrayList<>(Stream.concat(Stream.of("0"), lines.stream())
            .mapToInt(Integer::parseInt)
            .sorted()
            .boxed()
            .toList());
        adapters.add(adapters.stream().max(Integer::compareTo).get() + 3);

        return countArrangements(adapters, 0, new long[adapters.size()]);
    }

    private long countArrangements(List<Integer> adapters, int index, long[] dp) {
        if (index == adapters.size() - 1) {
            return 1;
        }

        if (dp[index] != 0L) {
            return dp[index];
        }

        long count = 0;

        for (int i = index + 1; i < adapters.size(); i++) {
            if (adapters.get(i) - adapters.get(index) <= 3) {
                count += countArrangements(adapters, i, dp);
            } else {
                break;
            }
        }

        dp[index] = count;
        return count;
    }
}
