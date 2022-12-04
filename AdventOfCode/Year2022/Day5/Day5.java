package Year2022.Day5;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;

public class Day5 {

    private static final String INPUT_NAME = "AdventOfCode/Year2022/Day4/input.txt";

    public static void main(String[] args) throws IOException {
        Day5 day = new Day5();
        day.part1();
        day.part2();
    }

    private void part1() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        int totalOverlaps = 0;

        while (br.ready()) {
            String[] elfSections = br.readLine().split(",");
            int aStart = Integer.parseInt(elfSections[0].split("-")[0]);
            int aEnd = Integer.parseInt(elfSections[0].split("-")[1]);
            int bStart = Integer.parseInt(elfSections[1].split("-")[0]);
            int bEnd = Integer.parseInt(elfSections[1].split("-")[1]);

            if ((aStart <= bStart && bEnd <= aEnd) || (bStart <= aStart && aEnd <= bEnd)) {
                totalOverlaps++;
            }
        }

        System.out.println("-------- Day 4 - Part 1 --------");
        System.out.println("Total overlaps: " + totalOverlaps);
        System.out.println("Expected total overlaps: 518");
    }

    private void part2() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        int totalOverlaps = 0;

        while (br.ready()) {
            String[] elfSections = br.readLine().split(",");
            int aStart = Integer.parseInt(elfSections[0].split("-")[0]);
            int aEnd = Integer.parseInt(elfSections[0].split("-")[1]);
            int bStart = Integer.parseInt(elfSections[1].split("-")[0]);
            int bEnd = Integer.parseInt(elfSections[1].split("-")[1]);

            if (aEnd < bStart || bEnd < aStart ) {
                continue;
            }

            totalOverlaps++;
        }

        System.out.println("-------- Day 4 - Part 2 --------");
        System.out.println("Total overlaps: " + totalOverlaps);
        System.out.println("Expected total overlaps: 909");
    }
}
