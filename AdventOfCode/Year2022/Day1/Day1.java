package Year2022.Day1;

import utils.Day;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day1 extends Day {

    public static void main(String[] args) throws IOException {
        Day1 day = new Day1();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        printAllResults(1, day.getName(),
            day.part1(sampleInput), 24000,
            day.part1(mainInput), 66186);

        printAllResults(2, day.getName(),
            day.part2(sampleInput), 45000,
            day.part2(mainInput), 196804);
    }

    @Override
    public long part1(List<String> lines) throws IOException {
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

        return allElfCalories.stream().max(Comparator.naturalOrder()).get();
    }

    @Override
    public long part2(List<String> lines) throws IOException {
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

        return allElfCalories.stream()
            .sorted(Comparator.reverseOrder())
            .limit(3)
            .mapToInt(Integer::intValue).sum();
    }
}
