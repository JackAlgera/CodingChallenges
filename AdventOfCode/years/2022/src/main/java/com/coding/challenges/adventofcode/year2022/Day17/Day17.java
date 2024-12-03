package com.coding.challenges.adventofcode.year2022.Day17;

import com.coding.challenges.adventofcode.utils.Day;

import java.io.IOException;
import java.util.*;

public class Day17 extends Day<Long> {

    public static int[][][] ROCKS = {
        {
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 },
            { 1, 1, 1, 1 }
        },
        {
            { 0, 0, 0, 0 },
            { 0, 1, 0, 0 },
            { 1, 1, 1, 0 },
            { 0, 1, 0, 0 }
        },
        {
            { 0, 0, 0, 0 },
            { 0, 0, 1, 0 },
            { 0, 0, 1, 0 },
            { 1, 1, 1, 0 }
        },
        {
            { 1, 0, 0, 0 },
            { 1, 0, 0, 0 },
            { 1, 0, 0, 0 },
            { 1, 0, 0, 0 }
        },
        {
            { 0, 0, 0, 0 },
            { 0, 0, 0, 0 },
            { 1, 1, 0, 0 },
            { 1, 1, 0, 0 }
        }
    };

    public static void main(String[] args) throws IOException {
        Day17 day = new Day17();

        day.printPart1("sample-input", 3068L);
        day.printPart1("input", 3151L);

        day.printPart2("sample-input", 1_514_285_714_288L);
        day.printPart2("input", 1_560_919_540_245L);
    }

    @Override
    public Long part1(List<String> lines) {
        List<Direction> windDirections = extractDirections(lines);
        List<int[]> layers = initLayers();

        return getHighestRockLayer(2022, windDirections, layers);
    }

    @Override
    public Long part2(List<String> lines) {
        List<Direction> windDirections = extractDirections(lines);
        List<int[]> layers = initLayers();

        return getHighestRockLayer(1000_000_000_000L, windDirections, layers);
    }

    private long getHighestRockLayer(long nbrRocks, List<Direction> windDirections, List<int[]> layers) {
        long currentTowerHeight = 0;
        long rockCount = 0;
        long windIndex = 0;

        Map<String, Cycle> cycles = new HashMap<>();

        while (rockCount < nbrRocks) {
            int rockIndex = (int) (rockCount % 5);
            boolean rockFallen = false;
            int j = 2 + 1;
            int i = 0;
            int[][] rock = ROCKS[rockIndex];
            rockCount++;

            String cycleKey = hashCycleKey(rockIndex, (int) windIndex % windDirections.size(), layers);

            if (rockCount > 5_000L && cycles.containsKey(cycleKey)) {
                Cycle cycle = cycles
                    .values().stream()
                    .max(Comparator.comparingLong(Cycle::nbrRocksGain))
                    .get();

                long jumps = (nbrRocks - rockCount) / cycle.nbrRocksGain();
                currentTowerHeight += jumps * cycle.towerHeightGain();
                rockCount += jumps * cycle.nbrRocksGain();
            }

            buildCycleCache(currentTowerHeight, rockCount, cycles, cycleKey);

            while (!rockFallen) {
                Direction direction = windDirections.get((int) windIndex % windDirections.size());
                int newJ = j + (direction == Direction.LEFT ? -1 : 1);
                if (!willCollide(rock, i, newJ, layers)) {
                    j = newJ;
                }
                if (!willCollide(rock, i + 1, j, layers)) {
                    i += 1;
                } else {
                    currentTowerHeight += addRockLayers(rock, i, j, layers);
                    rockFallen = true;
                }
                windIndex++;
            }
        }
        return currentTowerHeight;
    }

    private void buildCycleCache(long currentTowerHeight, long rockCount, Map<String, Cycle> cycles, String cycleKey) {
        if (cycles.containsKey(cycleKey)) {
            Cycle cycle = cycles.get(cycleKey);
            if (cycle.towerHeightGain() < 0 && cycle.nbrRocksGain() < 0) {
                cycles.put(cycleKey, new Cycle(
                    currentTowerHeight,
                    currentTowerHeight - cycle.currentTowerHeight(),
                    rockCount,
                    rockCount - cycle.currentNbrRocks()
                ));
            }
        } else {
            cycles.put(cycleKey, new Cycle(currentTowerHeight, -1, rockCount, -1));
        }
    }

    public String hashCycleKey(int rockIndex, int windIndex, List<int[]> layers) {
        return rockIndex + ":" + windIndex + ":" + hashLayers(layers);
    }

    public String hashLayers(List<int[]> layers) {
        StringBuilder b = new StringBuilder();
        for (int[] layer : layers) {
            for (int tile : layer) {
                b.append(tile);
            }
        }
        return b.toString();
    }

    public void printLayers(List<int[]> layers) {
        for (int[] layer : layers) {
            for (int j : layer) {
                System.out.print(
                    switch (j) {
                        case 0 -> ".";
                        case 1 -> "@";
                        case 2 -> "#";
                        case 3 -> "|";
                        case 4 -> "+";
                        case 5 -> "-";
                        default -> throw new IllegalStateException("Unexpected value: " + j);
                    }
                );
            }
            System.out.println();
        }
        System.out.println();
    }

    public int addRockLayers(int[][] rock, int i, int j, List<int[]> layers) {
        for (int k = 0; k < 4; k++) {
            for (int dj = 0; dj < 4; dj++) {
                if (rock[4 - k - 1][dj] > 0 && j + dj < 8) {
                    layers.get(i - k)[j + dj] = 2;
                }
            }
        }
        return balanceLayers(layers);
    }

    public int balanceLayers(List<int[]> layers) {
        int addedLayers = 0;
        boolean isBalanced = false;
        while (!isBalanced) {
            boolean shouldRemoveLayer = true;
            for (int j = 0; j < layers.get(0).length; j++) {
                if (layers.get(0)[j] == 2) {
                    shouldRemoveLayer = false;
                    break;
                }
            }
            if (shouldRemoveLayer) {
                addedLayers--;
                layers.remove(0);
            } else {
                isBalanced = true;
            }
        }
        for (int i = 0; i < 4; i++) {
            addedLayers++;
            layers.add(0, new int[]{ 3, 0, 0, 0, 0, 0, 0, 0, 3 });
        }
        if (layers.size() > 60) {
            for (int i = 0; i < layers.size() - 60; i++) {
                layers.remove(layers.size() - 1);
            }
        }
        return addedLayers;
    }

    public boolean willCollide(int[][] rock, int i, int j, List<int[]> layers) {
        for (int k = 0; k < Math.min(i, 4); k++) {
            int[] line = layers.get(i - k);
            for (int dj = 0; dj < rock.length; dj++) {
                int rockJ = j + dj;
                if (rock[4 - k - 1][dj] > 0 && line[rockJ] > 0) {
                    return true;
                }
            }
        }

        return false;
    }

    public List<Direction> extractDirections(List<String> lines) {
        List<Direction> windDirections = new ArrayList<>();
        for (int i = 0; i < lines.get(0).length(); i++) {
            switch (lines.get(0).charAt(i)) {
                case '<' -> windDirections.add(Direction.LEFT);
                case '>' -> windDirections.add(Direction.RIGHT);
            }
        }
        return windDirections;
    }

    public List<int[]> initLayers() {
        List<int[]> layers = new ArrayList<>();
        layers.add(new int[]{ 3, 0, 0, 0, 0, 0, 0, 0, 3 });
        layers.add(new int[]{ 3, 0, 0, 0, 0, 0, 0, 0, 3 });
        layers.add(new int[]{ 3, 0, 0, 0, 0, 0, 0, 0, 3 });
        layers.add(new int[]{ 3, 0, 0, 0, 0, 0, 0, 0, 3 });
        layers.add(new int[]{ 4, 5, 5, 5, 5, 5, 5, 5, 4 });
        return layers;
    }

    public record Cycle(long currentTowerHeight, long towerHeightGain, long currentNbrRocks, long nbrRocksGain) {}

    public enum Direction {
        LEFT, RIGHT
    }
}
