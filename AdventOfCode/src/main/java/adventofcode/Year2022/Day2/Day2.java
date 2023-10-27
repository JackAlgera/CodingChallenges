package adventofcode.Year2022.Day2;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Day2 extends Day<Integer> {

    private static final Map<String, Integer> points = Map.of(
            "A X", 3, // 0 + 3,
            "A Y", 4, // 3 + 1,
            "A Z", 8, // 6 + 2,
            "B X", 1, // 0 + 1,
            "B Y", 5, // 3 + 2,
            "B Z", 9, // 6 + 3,
            "C X", 2, // 0 + 2,
            "C Y", 6, // 3 + 3,
            "C Z", 7 // 6 + 1
    );

    public static void main(String[] args) throws IOException {
        Day2 day = new Day2();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        day.printAllResults(1, day.getName(),
            day.part1(sampleInput), 15,
            day.part1(mainInput), 10404);

        day.printAllResults(2, day.getName(),
            day.part2(sampleInput), 12,
            day.part2(mainInput), 10334);
    }

    @Override
    public Integer part1(List<String> lines) throws IOException {
        return 0;
    }

    @Override
    public Integer part2(List<String> lines) throws IOException {
        return lines.stream().mapToInt(points::get).sum();
    }
}
