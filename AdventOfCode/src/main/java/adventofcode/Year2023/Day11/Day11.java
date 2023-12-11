package adventofcode.Year2023.Day11;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day11 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day11 day = new Day11();

        day.printPart1("sample-input", 374L);
        day.printPart1("input", 10292708L);

        day.printPart2("sample-input", 82000210L);
        day.printPart2("input", 790194712336L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return countLengths(lines, 2);
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return countLengths(lines, 1_000_000L);
    }

    public long countLengths(List<String> lines, long expansionSize) {
        boolean[] isExpandedRows = getIsExpandedRows(lines);
        boolean[] isExpandedColumns = getIsExpandedColumns(lines);
        List<Galaxy> galaxies = extractGalaxies(lines);
        long totalLengths = 0;
        for (int m = 0; m < galaxies.size(); m++) {
            for (int n = m + 1; n < galaxies.size(); n++) {
                int iMin = Math.min(galaxies.get(m).i, galaxies.get(n).i);
                int jMin = Math.min(galaxies.get(m).j, galaxies.get(n).j);

                int height = Math.abs(galaxies.get(m).i - galaxies.get(n).i);
                int width = Math.abs(galaxies.get(m).j - galaxies.get(n).j);

                for (int k = iMin; k < iMin + height; k++) {
                    totalLengths += isExpandedRows[k] ? expansionSize : 1;
                }
                for (int k = jMin; k < jMin + width; k++) {
                    totalLengths += isExpandedColumns[k] ? expansionSize : 1;
                }
            }
        }
        return totalLengths;
    }

    public List<Galaxy> extractGalaxies(List<String> expanded) {
        List<Galaxy> galaxies = new ArrayList<>();
        for (int i = 0; i < expanded.size(); i++) {
            for (int j = 0; j < expanded.get(i).length(); j++) {
                if (expanded.get(i).charAt(j) == '#') {
                    galaxies.add(new Galaxy(i, j));
                }
            }
        }
        return galaxies;
    }

    public boolean[] getIsExpandedRows(List<String> lines) {
        boolean[] isExpanded = new boolean[lines.size()];
        for (int i = 0; i < lines.size(); i++) {
            boolean foundGalaxy = false;
            for (char c : lines.get(i).toCharArray()) {
                if (c == '#') {
                    foundGalaxy = true;
                    break;
                }
            }

            isExpanded[i] = !foundGalaxy;
        }
        return isExpanded;
    }

    public boolean[] getIsExpandedColumns(List<String> lines) {
        int width = lines.get(0).length();
        boolean[] isExpanded = new boolean[width];
        for (int j = 0; j < width; j++) {
            boolean foundGalaxy = false;
            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).charAt(j) == '#') {
                    foundGalaxy = true;
                    break;
                }
            }

            isExpanded[j] = !foundGalaxy;
        }
        return isExpanded;
    }

    public record Galaxy(int i, int j) {}
}
