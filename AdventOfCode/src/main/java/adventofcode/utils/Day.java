package adventofcode.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Day<T> {

    public static final String INPUT_PATH = "AdventOfCode/src/main/java/adventofcode/%s/%s/%s.txt";
    private final String YEAR_REGEX = "(Year\\d+)";
    private final Pattern YEAR_PATTERN = Pattern.compile(YEAR_REGEX);

    public String getDayName() {
        return getClass().getSimpleName();
    }

    public List<String> extractInputLines(String name) throws IOException {
        return Files.readAllLines(Path.of(getInputPath(name)));
    }

    private String getInputPath(String fileName) {
        Matcher m = YEAR_PATTERN.matcher(getClass().getPackageName());
        return String.format(INPUT_PATH, m.find() ? m.group(0) : "year2023", getDayName(), fileName);
    }

    public void printPart1(String fileName, T expectedResult) throws IOException {
        System.out.printf("\n-------- %s - Part 1 - %s --------%n", fileName, getDayName());
        System.out.println("- Actual Result\t   : " + part1(extractInputLines(fileName)));
        System.out.println("- Expected result\t : " + expectedResult);
    }

    public void printPart2(String fileName, T expectedResult) throws IOException {
        System.out.printf("\n-------- %s - Part 2 - %s --------%n", fileName, getDayName());
        System.out.println("- Actual Result\t   : " + part2(extractInputLines(fileName)));
        System.out.println("- Expected result\t : " + expectedResult);
    }

    public abstract T part1(List<String> lines) throws IOException;

    public abstract T part2(List<String> lines) throws IOException;
}
