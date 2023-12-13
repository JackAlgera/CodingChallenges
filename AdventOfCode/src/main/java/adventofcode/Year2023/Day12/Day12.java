package adventofcode.Year2023.Day12;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Day12 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day12 day = new Day12();

        day.printPart1("sample-input", 21L);
        day.printPart1("input", 7716L);

        day.printPart2("sample-input", 525_152L);
        day.printPart2("input", 18_716_325_559_999L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return unfoldRecords(1, lines);
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return unfoldRecords(5, lines);
    }

    public long unfoldRecords(int nbrCopies, List<String> lines) {
        long totalArrangements = 0;
        for (String line : lines) {
            List<Integer> groups = IntStream.range(0, nbrCopies)
                .mapToObj(i -> Arrays.stream(line.split(" ")[1]
                    .split(","))
                .mapToInt(Integer::parseInt)
                .boxed())
                .flatMap(s -> s.toList().stream())
                .toList();
            String springs = IntStream.range(0, nbrCopies)
                .mapToObj(i -> line.split(" ")[0])
                .collect(Collectors.joining("?"));
            totalArrangements += countArrangements(new State(0, 0, 0), springs, groups, new HashMap<>());
        }

        return totalArrangements;
    }

    public long countArrangements(State state, String springs, List<Integer> groups, Map<State, Long> dp) {
        if (dp.containsKey(state)) {
            return dp.get(state);
        }

        if (state.springIndex == springs.length()) {
            if (state.groupsIndex == groups.size() && state.groupSize == 0) {
                return 1;
            }
            if (state.groupsIndex == groups.size() - 1 && state.groupSize == groups.get(state.groupsIndex)) {
                return 1;
            }
            return 0;
        }

        char c = springs.charAt(state.springIndex);

        long tot = 0;
        if (c == '#') {
            tot += countArrangements(state.next(state.groupsIndex, state.groupSize + 1), springs, groups, dp);
        }

        if (c == '.') {
            if (state.groupSize == 0) {
                tot += countArrangements(state.next(state.groupsIndex, 0), springs, groups, dp);
            }
            if (state.groupsIndex < groups.size() && groups.get(state.groupsIndex) == state.groupSize) {
                tot += countArrangements(state.next(state.groupsIndex + 1, 0), springs, groups, dp);
            }
        }

        if (c == '?') {
            if (state.groupSize == 0) {
                tot += countArrangements(state.next(state.groupsIndex, 0), springs, groups, dp);
            }
            if (state.groupsIndex < groups.size() && groups.get(state.groupsIndex) == state.groupSize) {
                tot += countArrangements(state.next(state.groupsIndex + 1, 0), springs, groups, dp);
            }
            tot += countArrangements(state.next(state.groupsIndex, state.groupSize + 1), springs, groups, dp);
        }

        dp.put(state, tot);

        return tot;
    }

    public record State(int springIndex, int groupsIndex, int groupSize) {
        public State next(int groupsIndex, int groupSize) {
            return new State(springIndex + 1, groupsIndex, groupSize);
        }
    }
}
