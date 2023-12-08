package adventofcode.Year2020.Day2;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.List;

public class Day2 extends Day<Integer> {

    public static void main(String[] args) throws IOException {
        Day2 day = new Day2();

        day.printPart1("sample-input", 2);
        day.printPart1("input", 550);

        day.printPart2("sample-input", 1);
        day.printPart2("input", 634);
    }

    @Override
    public Integer part1(List<String> lines) throws IOException {
        int validPasswords = 0;
        for (String line : lines) {
            PasswordPolicy policy = parseLine(line);
            int count = 0;
            for (char c : line.split(" ")[2].toCharArray()) {
                if (c == policy.letter()) {
                    count++;
                }
                if (count > policy.right()) {
                    break;
                }
            }
            if (count >= policy.left() && count <= policy.right()) {
                validPasswords++;
            }
        }
        return validPasswords;
    }

    @Override
    public Integer part2(List<String> lines) throws IOException {
        int validPasswords = 0;
        for (String line : lines) {
            PasswordPolicy policy = parseLine(line);
            String password = line.split(" ")[2];
            int count = 0;
            if (password.charAt(policy.left() - 1) == policy.letter()) {
                count++;
            }
            if (password.charAt(policy.right() - 1) == policy.letter()) {
                count++;
            }
            if (count == 1) {
                validPasswords++;
            }
        }
        return validPasswords;
    }

    public PasswordPolicy parseLine(String line) {
        return new PasswordPolicy(
          Integer.parseInt(line.split("-")[0]),
          Integer.parseInt(line.split("-")[1].split(" ")[0]),
          line.split(" ")[1].charAt(0)
        );
    }

    public record PasswordPolicy(int left, int right, char letter) { }
}
