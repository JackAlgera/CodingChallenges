package adventofcode.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Utilities {

    public static BufferedReader getBufferedReader(String dayInput) throws FileNotFoundException {
        return new BufferedReader(new FileReader("AdventOfCode/src/main/java/adventofcode/" + dayInput));
    }

    public static String greenWord(String word) {
        return String.format("\u001B[32m%s\u001B[0m", word);
    }
    public static String redWord(String word) {
        return String.format("\u001B[31m%s\u001B[0m", word);
    }
    public static String yellowWord(String word) {
        return String.format("\u001B[33m%s\u001B[0m", word);
    }
}
