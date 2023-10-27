package adventofcode.Year2022.Day15;

import adventofcode.utils.Pair;
import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day15 {

    private static final String INPUT_NAME = "Year2022/Day15/input.txt";

    public static void main(String[] args) throws IOException {
        Day15 day = new Day15();
        day.part1();
        day.part2();
    }

    private void part1() throws IOException {
        int rowNumber = 2000000;
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        List<Pair<Pair<Integer>>> lines = extractInputPairs(br);
        List<Pair<Integer>> coveredArea = new ArrayList<>();

        for (Pair<Pair<Integer>> pair : lines) {
            Pair<Integer> sensor = pair.getFirst();
            Pair<Integer> beacon = pair.getSecond();
            if (isInRange(sensor, beacon, rowNumber)) {
                coveredArea.add(getCoveredArea(sensor, beacon, rowNumber));
            }
        }
        coveredArea.sort(Comparator.comparing(Pair::getFirst));

        List<Pair<Integer>> areaList = removeOverlaps(coveredArea);

        int positionsCannotContainBeacon = 0;
        for (Pair<Integer> area : areaList) {
            positionsCannotContainBeacon += area.getSecond() - area.getFirst();
        }

        System.out.println("\n-------- Day 15 - Part 1 --------");
        System.out.println("Total positions that cannot contain beacon: " + positionsCannotContainBeacon);
        System.out.println("Expected totap positions: 4861076");
    }

    private void part2() throws IOException {
        int max = 4000000;
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        List<Pair<Integer>> coveredArea;
        List<Pair<Pair<Integer>>> lines = extractInputPairs(br);

        for (int y = 0; y <= max; y++) {
            coveredArea = new ArrayList<>();
            for (Pair<Pair<Integer>> pair : lines) {
                if (isInRange(pair.getFirst(), pair.getSecond(), y)) {
                    coveredArea.add(getCoveredAreaLimited(pair.getFirst(), pair.getSecond(), y, max));
                }
            }
            coveredArea.sort(Comparator.comparing(Pair::getFirst));

            List<Pair<Integer>> areaList = removeOverlaps(coveredArea);
            int positionsCannotContainBeacon = 0;
            for (Pair<Integer> area : areaList) {
                positionsCannotContainBeacon += area.getSecond() - area.getFirst();
            }

            if (positionsCannotContainBeacon < max - 1) {
                for (int i = 1; i < areaList.size(); i++) {
                    Pair<Integer> n1 = areaList.get(i - 1);
                    Pair<Integer> n2 = areaList.get(i);

                    if (n1.getSecond() < n2.getFirst()) {
                        System.out.println("\n-------- Day 15 - Part 2 --------");
                        System.out.println("x: " + (n1.getSecond() + 1) + " y: " + y);
                        System.out.println("Tuning frequency: " + getTuningFrequency((n1.getSecond() + 1), y));
                        System.out.println("Expected Tuning frequency: 10649103160102");
                        return;
                    }
                }
                for (Pair<Integer> area : areaList) {
                    positionsCannotContainBeacon += area.getSecond() - area.getFirst();
                }
            }
        }
    }

    private static List<Pair<Integer>> removeOverlaps(List<Pair<Integer>> coveredArea) {
        Stack<Pair<Integer>> stack = new Stack<>();
        stack.push(coveredArea.get(0));

        for (int i = 1; i < coveredArea.size(); i++) {
            Pair<Integer> top = stack.peek();
            Pair<Integer> a1 = coveredArea.get(i);

            // Not overlapping
            if (top.getSecond() < a1.getFirst()) {
                stack.push(a1);
            } else if (top.getSecond() < a1.getSecond()) {
                top.setSecond(a1.getSecond());
                stack.pop();
                stack.push(top);
            }
        }
        return new ArrayList<>(stack);
    }

    private List<Pair<Pair<Integer>>> extractInputPairs(BufferedReader br) throws IOException {
        List<Pair<Pair<Integer>>> lines = new ArrayList<>();

        while (br.ready()) {
            String line = br.readLine();

            List<Pair<Integer>> positions = extractPositions(line);
            Pair<Integer> sensor = positions.get(0);
            Pair<Integer> beacon = positions.get(1);

            lines.add(new Pair<>(sensor, beacon));
        }
        return lines;
    }

    private List<Pair<Integer>> extractPositions(String input) {
        Matcher matcher = Pattern.compile("x=([-0-9]+), y=([-0-9]+)").matcher(input);
        matcher.find();
        Pair<Integer> sensor = new Pair<>(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        matcher.find();
        Pair<Integer> beacon = new Pair<>(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));

        return List.of(sensor, beacon);
    }

    private int distanceBetween(Pair<Integer> p1, Pair<Integer> p2) {
        return Math.abs(p1.getFirst() - p2.getFirst()) + Math.abs(p1.getSecond() - p2.getSecond());
    }

    private boolean isInRange(Pair<Integer> sensor, Pair<Integer> beacon, int line) {
        int distanceBetween = distanceBetween(sensor, beacon);
        return Math.abs(sensor.getSecond() - line) <= distanceBetween;
    }

    private Pair<Integer> getCoveredArea(Pair<Integer> sensor, Pair<Integer> beacon, int line) {
        int delta = distanceBetween(sensor, beacon) - Math.abs(sensor.getSecond() - line);

        return new Pair<>(
                sensor.getFirst() - delta,
                sensor.getFirst() + delta
        );
    }

    private Pair<Integer> getCoveredAreaLimited(Pair<Integer> sensor, Pair<Integer> beacon, int line, int max) {
        int delta = distanceBetween(sensor, beacon) - Math.abs(sensor.getSecond() - line);

        return new Pair<>(
                Math.max(sensor.getFirst() - delta, 0),
                Math.min(sensor.getFirst() + delta, max)
        );
    }

    private BigInteger getTuningFrequency(int x, int y) {
        return BigInteger.valueOf(x).multiply(BigInteger.valueOf(4000000)).add(BigInteger.valueOf(y));
    }
}
