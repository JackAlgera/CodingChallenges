package Year2022.Day2;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Map;

public class Day2 {

    private static final String INPUT_NAME = "AdventOfCode/Year2022/Day2/input.txt";

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
        day.partTwo();
    }

    private void partTwo() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        int totalPoints = 0;

        while (br.ready()) {
            String line = br.readLine();
            totalPoints += points.get(line);
        }

        System.out.println("-------- Day 2 --------");
        System.out.println("Final score: " + totalPoints);
        System.out.println("Expected score: 10334");
    }
}
