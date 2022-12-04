package Year2022.Day1;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day1 {

    private static final String INPUT_NAME = "AdventOfCode/Year2022/Day1/input.txt";

    public static void main(String[] args) throws IOException {
        Day1 day = new Day1();
        day.partTwo();
    }

    private void partTwo() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        int currentElfCalories = 0;
        List<Integer> allElfCalories = new ArrayList<>();

        while (br.ready()) {
            String line = br.readLine();

            if (line.equals("")) {
                allElfCalories.add(currentElfCalories);
                currentElfCalories = 0;
            } else {
                currentElfCalories += Integer.parseInt(line);
            }
        }

        int totalCalories = allElfCalories.stream()
                .sorted(Comparator.reverseOrder())
                .limit(3)
                .mapToInt(Integer::intValue).sum();

        System.out.println("-------- Day 1 --------");
        System.out.println("Total calories: " + totalCalories);
        System.out.println("Expected calories: 196804");
    }
}
