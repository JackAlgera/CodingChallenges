package adventofcode.utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
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
        printResponse(1, fileName, part1(extractInputLines(fileName)), expectedResult);
    }

    public void printPart2(String fileName, T expectedResult) throws IOException {
        printResponse(2, fileName, part2(extractInputLines(fileName)), expectedResult);
        if (Objects.equals(fileName, "input")) System.out.println();
    }

    private void printResponse(int part, String fileName, T result, T expectedResult) {
        System.out.printf(String.format("\n%s, Part %d: %s (%s)\t for %s",
          getDayName(),
          part,
          Objects.equals(result, expectedResult) ? Utilities.greenWord("" + result) : Utilities.redWord("" + result),
          expectedResult,
          Utilities.yellowWord(fileName)
        ));
    }

    public abstract T part1(List<String> lines) throws IOException;

    public abstract T part2(List<String> lines) throws IOException;
}
