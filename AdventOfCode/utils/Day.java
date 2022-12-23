package utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public abstract class Day {

    public String getName() {
        return getClass().getSimpleName();
    }

    public static List<String> extractMainInputLines(String day) throws IOException {
        return Files.readAllLines(Path.of(getInputPath(day, "input")));
    }

    public static List<String> extractSampleInputLines(String day) throws IOException {
        return Files.readAllLines(Path.of(getInputPath(day, "sample-input")));
    }

    private static String getInputPath(String day, String fileName) {
        return String.format("AdventOfCode/Year2022/%s/%s.txt", day, fileName);
    }

    public static void printAllResults(int part, String dayName, long sampleResult1, long expectedSampleResult1, long sampleResult2, long expectedSampleResult2) {
        System.out.printf("\n-------- %s - Part %d --------%n", dayName, part);
        System.out.println("------------ Sample -----------");
        System.out.println("Result: " + sampleResult1);
        System.out.println("Expected result: " + expectedSampleResult1);

        System.out.println("------------- Main ------------");
        System.out.println("Result: " + sampleResult2);
        System.out.println("Expected result: " + expectedSampleResult2);
    }

    public abstract long part1(List<String> lines) throws IOException;

    public abstract long part2(List<String> lines) throws IOException;
}
