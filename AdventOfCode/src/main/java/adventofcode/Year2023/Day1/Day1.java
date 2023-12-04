package adventofcode.Year2023.Day1;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class Day1 extends Day<String> {

    public static void main(String[] args) throws IOException {
        Day1 day = new Day1();

        day.printPart1("sample-input-part1", "142");
        day.printPart1("input", "53080");

        day.printPart2("sample-input-part2", "281");
        day.printPart2("input", "53268");
    }

    private final Map<String, Integer> numberMap = Map.of(
      "zero", 0,
      "one", 1,
      "two", 2,
      "three", 3,
      "four", 4,
      "five", 5,
      "six", 6,
      "seven", 7,
      "eight", 8,
      "nine", 9
    );

    @Override
    public String part1(List<String> lines) throws IOException {
        return "" + lines.stream().mapToInt(line -> extractNumber(line, false)).sum();
    }

    @Override
    public String part2(List<String> lines) throws IOException {
        return "" + lines.stream().mapToInt(line -> extractNumber(line, true)).sum();
    }

    public int extractNumber(String line, boolean checkMap) {
        int left = 0;
        int right = line.length() - 1;

        int leftVal = -1;
        int rightVal = -1;

        while (leftVal < 0 || rightVal < 0) {
            if (Character.isDigit(line.charAt(left))) {
                leftVal = Integer.parseInt("" + line.charAt(left));
            }
            if (Character.isDigit(line.charAt(right))) {
                rightVal = Integer.parseInt("" + line.charAt(right));
            }

            if (checkMap) {
                for (Map.Entry<String, Integer> entry : numberMap.entrySet()) {
                    if (leftVal < 0 && line.substring(left).indexOf(entry.getKey()) == 0) {
                        leftVal = entry.getValue();
                    }
                    if (rightVal < 0 && line.substring(right).indexOf(entry.getKey()) == 0) {
                        rightVal = entry.getValue();
                    }
                }
            }

            if (leftVal < 0) {
                left++;
            }
            if (rightVal < 0) {
                right--;
            }
        }

        return Integer.parseInt("" + leftVal + rightVal);
    }
}
