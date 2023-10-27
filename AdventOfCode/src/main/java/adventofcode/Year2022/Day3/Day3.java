package adventofcode.Year2022.Day3;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

public class Day3 extends Day<Integer> {

    public static void main(String[] args) throws IOException {
        Day3 day = new Day3();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        day.printAllResults(1, day.getName(),
            day.part1(sampleInput), 157,
            day.part1(mainInput), 8252);

        day.printAllResults(2, day.getName(),
            day.part2(sampleInput), 70,
            day.part2(mainInput), 2828);
    }

    @Override
    public Integer part1(List<String> lines) throws IOException {
        int totalPoints = 0;

        for (String line : lines) {
            String firstCompartment = line.substring(0, line.length()/2);
            String secondCompartment = line.substring(line.length()/2);

            for (int i = 0; i < firstCompartment.length(); i++) {
                char val = firstCompartment.charAt(i);
                String valStr = "" + val;

                if (secondCompartment.contains(valStr)) {
                    if (Pattern.matches("[a-z]", valStr)) {
                        totalPoints += ((int) val) - 96;
                    } else if (Pattern.matches("[A-Z]", valStr)) {
                        totalPoints += ((int) val) - 64 + 26;
                    }
                    break;
                }
            }
        }

        return totalPoints;
    }

    @Override
    public Integer part2(List<String> lines) throws IOException {
        String items = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int total = 0;

        for (int k = 0; k < lines.size() / 3; k++) {
            String firstElf = lines.get(k * 3);
            String secondElf = lines.get(k * 3 + 1);
            String thirdElf = lines.get(k * 3 + 2);

            for (int i = 0; i < items.length(); i++) {
                char val = items.charAt(i);
                String valStr = "" + val;

                if (firstElf.contains(valStr) && secondElf.contains(valStr) && thirdElf.contains(valStr)) {
                    if (Pattern.matches("[a-z]", valStr)) {
                        total += ((int) val) - 96;
                    } else if (Pattern.matches("[A-Z]", valStr)) {
                        total += ((int) val) - 64 + 26;
                    }
                }
            }
        }
        return total;
    }
}
