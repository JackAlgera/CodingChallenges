package adventofcode.Year2020.Day6;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class Day6 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day6 day = new Day6();

        day.printPart1("sample-input", 11L);
        day.printPart1("input", 6161L);

        day.printPart2("sample-input", 6L);
        day.printPart2("input", 2971L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return countAnswers(lines, false);
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return countAnswers(lines, true);
    }

    private long countAnswers(List<String> lines, boolean countOnlyUnanimous) {
        int i = 0;
        long yeses = 0;
        while (i < lines.size()) {
            int k = 0;
            while ((i + k) < lines.size() && !lines.get(i + k).isBlank()) {
                k++;
            }
            yeses += countOnlyUnanimous ? countUnanimousAnswers(lines.subList(i, i + k)) : countAllAnswers(lines.subList(i, i + k));
            i += k + 1;
        }

        return yeses;
    }

    public int countAllAnswers(List<String> inputs) {
        return String.join("", inputs)
            .chars()
            .boxed()
            .collect(Collectors.toSet())
            .size();
    }

    public long countUnanimousAnswers(List<String> inputs) {
        int totalPeople = inputs.size();
        return String.join("", inputs)
            .chars()
            .boxed()
            .collect(Collectors.groupingBy(c -> c, Collectors.counting()))
            .values()
            .stream()
            .filter(count -> count == totalPeople)
            .count();
    }
}
