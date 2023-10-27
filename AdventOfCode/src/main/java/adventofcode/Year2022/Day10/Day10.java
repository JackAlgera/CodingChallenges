package adventofcode.Year2022.Day10;

import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;

public class Day10 {

    private static final String INPUT_NAME = "Year2022/Day10/input.txt";

    public static void main(String[] args) throws IOException {
        Day10 day = new Day10();
        day.part1();
        day.part2();
    }

    private void part1() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        int cycleNumber = 0;
        int x = 1;
        int signalStrengths = 0;

        while (br.ready()) {
            String line = br.readLine();

            String[] inputs = line.split(" ");
            String instruction = inputs[0];

            switch (instruction) {
                case "noop":
                    cycleNumber++;
                    signalStrengths = updateSignalStrength(signalStrengths, cycleNumber, x);
                    break;
                case "addx":
                    cycleNumber++;
                    signalStrengths = updateSignalStrength(signalStrengths, cycleNumber, x);
                    cycleNumber++;
                    signalStrengths = updateSignalStrength(signalStrengths, cycleNumber, x);
                    x += Integer.parseInt("" + inputs[1]);
                    break;
            }
        }

        System.out.println("-------- Day 10 - Part 1 --------");
        System.out.println("Signal strength: " + signalStrengths);
        System.out.println("Expected signal strength: 10760");
    }

    private void part2() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        int cycleNumber = 0;
        int x = 1;
        String image = "";

        while (br.ready()) {
            String line = br.readLine();

            String[] inputs = line.split(" ");
            String instruction = inputs[0];

            switch (instruction) {
                case "noop":
                    image += getNextPixel(cycleNumber, x);
                    cycleNumber++;
                    break;
                case "addx":
                    image += getNextPixel(cycleNumber, x);
                    cycleNumber++;
                    image += getNextPixel(cycleNumber, x);
                    cycleNumber++;
                    x += Integer.parseInt("" + inputs[1]);
                    break;
            }
        }

        System.out.println("-------- Day 10 - Part 2 --------");
        for (int i = 0; i < 6; i++) {
            System.out.println(image.substring(i * 40, (i + 1) * 40));
        }
        System.out.println("Expected letters: FPGPHFGH");
    }

    private String getNextPixel(int cycleNumber, int x) {
        if (x - 1 <= cycleNumber%40 && cycleNumber%40 <= x + 1) {
            return "#";
        }

        return ".";
    }

    private int updateSignalStrength(int signalStrengths, int cycleNumber, int x) {
        if (cycleNumber % 40 == 20 && cycleNumber < 221) {
            return signalStrengths + cycleNumber * x;
        }

        return signalStrengths;
    }
}
