package Year2022.Day4;

import utils.Day;
import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

public class Day4 extends Day<Integer> {

    public static void main(String[] args) throws IOException {
        Day4 day = new Day4();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        day.printAllResults(1, day.getName(),
            day.part1(sampleInput), 2,
            day.part1(mainInput), 518);

        day.printAllResults(2, day.getName(),
            day.part2(sampleInput), 4,
            day.part2(mainInput), 909);
    }

    @Override
    public Integer part1(List<String> lines) throws IOException {
        int totalOverlaps = 0;

        for (String line : lines) {
            String[] elfSections = line.split(",");
            int aStart = Integer.parseInt(elfSections[0].split("-")[0]);
            int aEnd = Integer.parseInt(elfSections[0].split("-")[1]);
            int bStart = Integer.parseInt(elfSections[1].split("-")[0]);
            int bEnd = Integer.parseInt(elfSections[1].split("-")[1]);

            if ((aStart <= bStart && bEnd <= aEnd) || (bStart <= aStart && aEnd <= bEnd)) {
                totalOverlaps++;
            }
        }

        return totalOverlaps;
    }

    @Override
    public Integer part2(List<String> lines) throws IOException {
        int totalOverlaps = 0;
        for (String line : lines) {
            String[] elfSections = line.split(",");
            int aStart = Integer.parseInt(elfSections[0].split("-")[0]);
            int aEnd = Integer.parseInt(elfSections[0].split("-")[1]);
            int bStart = Integer.parseInt(elfSections[1].split("-")[0]);
            int bEnd = Integer.parseInt(elfSections[1].split("-")[1]);

            if (aEnd < bStart || bEnd < aStart ) {
                continue;
            }

            totalOverlaps++;
        }

        return totalOverlaps;
    }
}
