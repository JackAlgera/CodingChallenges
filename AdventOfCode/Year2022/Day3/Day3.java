package Year2022.Day3;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class Day3 {

    private static final String INPUT_NAME = "AdventOfCode/Year2022/Day3/input.txt";

    public static void main(String[] args) throws IOException {
        Day3 day = new Day3();
        day.part1();
        day.part2();
    }

    private void part1() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        int totalPoints = 0;

        while (br.ready()) {
            String line = br.readLine();
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

        System.out.println("-------- Day 3 - Part 1 --------");
        System.out.println("Sum of priorities: " + totalPoints);
        System.out.println("Expected sum of priorities: 8252");
    }

    private void part2() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        String items = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        int total = 0;

        while (br.ready()) {
            String firstElf = br.readLine();
            String secondElf = br.readLine();
            String thirdElf = br.readLine();

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

        System.out.println("-------- Day 3 - Part 2 --------");
        System.out.println("Sum of priorities: " + total);
        System.out.println("Expected sum of priorities: 2828");
    }
}
