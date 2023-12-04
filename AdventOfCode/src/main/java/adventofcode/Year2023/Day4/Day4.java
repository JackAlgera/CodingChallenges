package adventofcode.Year2023.Day4;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Day4 extends Day<Integer> {

    public static void main(String[] args) throws IOException {
        Day4 day = new Day4();

        day.printPart1("sample-input", 13);
        day.printPart1("input", 26346);

        day.printPart2("sample-input", 30);
        day.printPart2("input", 8467762);
    }

    @Override
    public Integer part1(List<String> lines) throws IOException {
        int total = 0;

        for (String card : lines) {
            Set<Integer> winningNumbers = extractWinningNumbers(lines, card);
            total += (int) Math.pow(2, countMatches(lines, card, winningNumbers) - 1);
        }

        return total;
    }

    @Override
    public Integer part2(List<String> lines) throws IOException {
        List<Integer> cardCount = new ArrayList<>();
        List<Integer> cardValues = new ArrayList<>();
        for (String card : lines) {
            Set<Integer> winningNumbers = extractWinningNumbers(lines, card);
            int tot = (int) countMatches(lines, card, winningNumbers);

            cardCount.add(1);
            cardValues.add(tot);
        }

        for (int i = 0; i < cardCount.size(); i++) {
            for (int k = 1; k <= cardValues.get(i); k++) {
                if (i + k >= cardCount.size()) {
                    break;
                }
                cardCount.set(i + k, cardCount.get(i + k) + cardCount.get(i));
            }
        }
        return cardCount.stream().mapToInt(Integer::valueOf).sum();
    }

    private long countMatches(List<String> lines, String card, Set<Integer> winningNumbers) {
        return Arrays.stream(card
            .substring(isSample(lines) ? 25 : 42)
            .trim()
            .split("[ ]+"))
          .map(Integer::parseInt)
          .filter(winningNumbers::contains)
          .count();
    }

    private Set<Integer> extractWinningNumbers(List<String> lines, String card) {
        return Arrays.stream(card
            .substring(isSample(lines) ? 8 : 10, isSample(lines) ? 22 : 39)
            .trim()
            .split("[ ]+"))
          .map(Integer::parseInt)
          .collect(Collectors.toSet());
    }

    public boolean isSample(List<String> lines) {
        return lines.get(0).length() == 48;
    }
}
