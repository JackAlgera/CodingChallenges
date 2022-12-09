package Year2022.Day9;

import utils.Pair;
import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Day9 {

    private static final String INPUT_NAME = "AdventOfCode/Year2022/Day9/input.txt";

    public static void main(String[] args) throws IOException {
        Day9 day = new Day9();
        day.part(List.of(
                new Pair<>(0, 0),
                new Pair<>(0, 0)
        ), 6494);
        day.part(List.of(
                new Pair<>(0, 0),
                new Pair<>(0, 0),
                new Pair<>(0, 0),
                new Pair<>(0, 0),
                new Pair<>(0, 0),
                new Pair<>(0, 0),
                new Pair<>(0, 0),
                new Pair<>(0, 0),
                new Pair<>(0, 0),
                new Pair<>(0, 0)
        ), 2691);
    }

    private void part(List<Pair<Integer>> joints, int expectedValue) throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        Set<String> uniqueSteps = new HashSet<>();
        uniqueSteps.add(generateKey(0, 0));

        while (br.ready()) {
            String line = br.readLine();

            String direction = line.split(" ")[0];
            int steps = Integer.parseInt(line.split(" ")[1]);

            while (steps > 0) {
                Pair<Integer> currentJoint = joints.get(0);

                switch (direction) {
                    case "U":
                        currentJoint.setSecond(currentJoint.getSecond() + 1);
                        break;
                    case "R":
                        currentJoint.setFirst(currentJoint.getFirst() + 1);
                        break;
                    case "D":
                        currentJoint.setSecond(currentJoint.getSecond() - 1);
                        break;
                    case "L":
                        currentJoint.setFirst(currentJoint.getFirst() - 1);
                        break;
                }

                for (int i = 1; i < joints.size(); i++) {
                    Pair<Integer> nextJoint = joints.get(i);

                    Pair<Integer> newPos = move(currentJoint, nextJoint);
                    nextJoint.setFirst(newPos.getFirst());
                    nextJoint.setSecond(newPos.getSecond());

                    currentJoint = nextJoint;
                }

                Pair<Integer> head = joints.get(0);
                Pair<Integer> tail = joints.get(joints.size() - 1);

                uniqueSteps.add(generateKey(tail.getFirst(), tail.getSecond()));
//                System.out.println("Pos head: " + head.getFirst() + " " + head.getSecond() + ", pos tail: " + tail.getFirst() + " " + tail.getSecond());
                steps--;
            }
        }

        System.out.println("-------- Day 9 --------");
        System.out.println("Unique positions: " + uniqueSteps.size());
        System.out.println("Expected unique positions: " + expectedValue);
    }

    private String generateKey(int x, int y) {
        return String.format("%d:%d'", x, y);
    }

    private Pair<Integer> move(Pair<Integer> head, Pair<Integer> tail) {
        int deltaX = head.getFirst() - tail.getFirst();
        int deltaY = head.getSecond() - tail.getSecond();

        if ((deltaY == 0 && deltaX == 0) || (Math.abs(deltaX) + Math.abs(deltaY) < 2) || (Math.abs(deltaX) == 1 && Math.abs(deltaY) == 1)) {
            return new Pair<>(tail.getFirst(), tail.getSecond());
        }

        if (deltaX == 0) {
            if (deltaY > 1) {
                return new Pair<>(tail.getFirst(), tail.getSecond() + 1);
            } else {
                return new Pair<>(tail.getFirst(), tail.getSecond() - 1);
            }
        }

        if (deltaY == 0) {
            if (deltaX > 1) {
                return new Pair<>(tail.getFirst() + 1, tail.getSecond());
            } else {
                return new Pair<>(tail.getFirst() - 1, tail.getSecond());
            }
        }

        int newX = tail.getFirst();
        int newY = tail.getSecond();

        if (deltaX > 0) {
            newX++;
        }

        if (deltaX < 0) {
            newX--;
        }

        if (deltaY > 0) {
            newY++;
        }

        if (deltaY < 0) {
            newY--;
        }

        return new Pair<>(newX, newY);
    }
}

// 2951 too high
