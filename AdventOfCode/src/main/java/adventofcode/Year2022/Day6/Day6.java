package adventofcode.Year2022.Day6;

import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;

public class Day6 {

    private static final String INPUT_NAME = "Year2022/Day6/input.txt";

    public static void main(String[] args) throws IOException {
        Day6 day = new Day6();
        day.part(4, 1287);
        day.part(14, 3716);
    }

    private void part(int packetSize, int expectedValue) throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
        String line = br.readLine();

        for (int i = packetSize - 1; i < line.length(); i++) {
            String subLine = line.substring(i - packetSize + 1, i + 1);

            if (checkChars(subLine)) {
                System.out.println("-------- Day 6 --------");
                System.out.println("Chars processed : " + (i + 1));
                System.out.println("Expected chars processed: " + expectedValue);
                return;
            }
        }
    }

    private boolean checkChars(String sequence) {
        for (int i = 0; i < sequence.length(); i++) {
            char left = sequence.charAt(i);
            for (int j = i + 1; j < sequence.length(); j++) {
                char right = sequence.charAt(j);
                if (left == right) {
                    return false;
                }
            }
        }

        return true;
    }
}
