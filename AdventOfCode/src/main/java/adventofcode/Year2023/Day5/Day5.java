package adventofcode.Year2023.Day5;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day5 day = new Day5();

        day.printPart1("sample-input", 35L);
        day.printPart1("input", 806029445L);

        day.printPart2("sample-input", 46L);
        day.printPart2("input", 59370572L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        Set<Long> seeds = Arrays.stream(lines.get(0).split(": ")[1]
          .split(" "))
          .map(Long::parseLong)
          .collect(Collectors.toSet());

        List<List<MapRange>> mapSteps = parseInput(lines.subList(3, lines.size()));

        long min = Long.MAX_VALUE;
        for (long seed : seeds) {
            long val = seed;
            for (List<MapRange> step : mapSteps) {
                for (MapRange mapRange : step) {
                    if (val >= mapRange.sourceStart() && val <= mapRange.sourceEnd()) {
                        val = mapRange.destinationStart() + (val - mapRange.sourceStart());
                        break;
                    }
                }
            }
            min = Math.min(min, val);
        }

        return min;
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        String[] nums = lines.get(0).split(": ")[1].split(" ");
        Set<SeedRange> seedsSet = new HashSet<>();
        for (int i = 0; i < nums.length / 2; i++) {
            long start = Long.parseLong(nums[i * 2]);
            long end = start + Long.parseLong(nums[i * 2 + 1]) - 1;
            seedsSet.add(new SeedRange(start, end));
        }
        List<List<MapRange>> steps = parseInput(lines.subList(3, lines.size()));

        for (List<MapRange> step : steps) {
            seedsSet = playStep(seedsSet, step);
        }

        return seedsSet.stream().mapToLong(SeedRange::start).min().getAsLong();
    }

    public Set<SeedRange> playStep(Set<SeedRange> seedRanges, List<MapRange> mapRanges) {
        Set<SeedRange> checkedSeeds = new HashSet<>();
        Set<SeedRange> temp = new HashSet<>();
        for (MapRange mapRange : mapRanges) {
            temp = new HashSet<>();
            for (SeedRange seedRange : seedRanges) {
                if (seedRange.start() > mapRange.sourceEnd() || seedRange.end() < mapRange.sourceStart()) {
                    temp.add(new SeedRange(seedRange.start(), seedRange.end()));
                    continue;
                }

                checkedSeeds.add(new SeedRange(
                  mapRange.destinationStart() + (Math.max(mapRange.sourceStart(), seedRange.start()) - mapRange.sourceStart()),
                  mapRange.destinationEnd() + (Math.min(mapRange.sourceEnd(), seedRange.end()) - mapRange.sourceEnd())));

                if (seedRange.start() < mapRange.sourceStart()) {
                    temp.add(new SeedRange(seedRange.start(), mapRange.sourceStart() - 1));
                }
                if (seedRange.end() > mapRange.sourceEnd()) {
                    temp.add(new SeedRange(mapRange.sourceEnd() + 1, seedRange.end()));
                }
            }
            seedRanges = temp;
        }

        return Stream.concat(checkedSeeds.stream(), temp.stream())
          .collect(Collectors.toSet());
    }

    public List<List<MapRange>> parseInput(List<String> lines) {
        List<List<MapRange>> maps = new ArrayList<>();
        List<MapRange> currentMapRanges = new ArrayList<>();

        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);

            if (line.isBlank() && !currentMapRanges.isEmpty()) {
                maps.add(currentMapRanges);
                currentMapRanges = new ArrayList<>();
                i++;
                continue;
            }

            currentMapRanges.add(new MapRange(
              Long.parseLong(line.split(" ")[0]),
              Long.parseLong(line.split(" ")[1]),
              Long.parseLong(line.split(" ")[2])));
        }

        return maps;
    }

    public record MapRange(Long destinationStart, Long sourceStart, Long range) {
        public Long destinationEnd() {
            return destinationStart + range - 1;
        }
        public Long sourceEnd() {
            return sourceStart + range - 1;
        }
    }
    public record SeedRange(Long start, Long end) {}
}
