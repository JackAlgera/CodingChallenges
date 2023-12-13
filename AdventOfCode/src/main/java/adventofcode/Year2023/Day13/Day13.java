package adventofcode.Year2023.Day13;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day13 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day13 day = new Day13();

        day.printPart1("sample-input", 405L);
        day.printPart1("input", 39939L);

        day.printPart2("sample-input", 400L);
        day.printPart2("input", 32069L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return parsePatterns(lines).stream()
            .map(this::getPatternNote)
            .mapToLong(Note::val)
            .sum();
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return parsePatterns(lines).stream()
            .mapToLong(this::fixSmudge)
            .sum();
    }

    public String[][] toMatrix(List<String> pattern) {
        String[][] matrix = new String[pattern.size()][pattern.get(0).length()];
        for (int i = 0; i < pattern.size(); i++) {
            for (int j = 0; j < pattern.get(0).length(); j++) {
                matrix[i][j] = pattern.get(i).substring(j, j + 1);
            }
        }
        return matrix;
    }

    public List<String[][]> parsePatterns(List<String> lines) {
        List<String[][]> patterns = new ArrayList<>();
        List<String> pattern = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (i == lines.size() - 1 || line.isBlank()) {
                patterns.add(toMatrix(pattern));
                pattern = new ArrayList<>();
                continue;
            }

            pattern.add(line);
        }
        return patterns;
    }

    public Note getPatternNote(String[][] pattern) {
        return getPatternNote(pattern, new Note(-1, -1, -1));
    }

    public Note getPatternNote(String[][] pattern, Note previousNote) {
        for (int i = 0; i < pattern.length - 1; i++) {
            boolean isPattern = true;
            for (int j = 0; j < pattern[0].length; j++) {
                int kt = i;
                int kb = i + 1;
                while (isPattern && kt >= 0 && kb < pattern.length) {
                    if (!pattern[kt][j].equals(pattern[kb][j])) {
                        isPattern = false;
                        break;
                    }
                    kt--;
                    kb++;
                }
            }

            if (isPattern && previousNote.i != i + 1) {
                return new Note(100L * (i + 1), i + 1, 0);
            }
        }
        for (int j = 0; j < pattern[0].length - 1; j++) {
            boolean isPattern = true;
            for (int i = 0; i < pattern.length; i++) {
                int kLeft = j;
                int kRight = j + 1;
                while (isPattern && kLeft >= 0 && kRight < pattern[0].length) {
                    if (!pattern[i][kLeft].equals(pattern[i][kRight])) {
                        isPattern = false;
                        break;
                    }
                    kLeft--;
                    kRight++;
                }
            }

            if (isPattern && previousNote.j != j + 1) {
                return new Note(j + 1, 0, j + 1);
            }
        }
        return null;
    }

    public long fixSmudge(String[][] pattern) {
        Note previousNote = getPatternNote(pattern);
        for (int i = 0; i < pattern.length; i++) {
            for (int j = 0; j < pattern[0].length; j++) {
                pattern[i][j] = pattern[i][j].equals("#") ? "." : "#";

                Note newNote = getPatternNote(pattern, previousNote);
                if (newNote != null && (previousNote.i != newNote.i || previousNote.j != newNote.j)) {
                    return newNote.val;
                }

                pattern[i][j] = pattern[i][j].equals("#") ? "." : "#";
            }
        }
        return Long.MIN_VALUE;
    }

    public record Note(long val, int i, int j) {}
}
