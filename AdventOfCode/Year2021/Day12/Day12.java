package Year2021.Day12;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

public class Day12 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day12/input.txt";

    public static void main(String[] args) throws IOException {
        new Day12Code().run();
    }

    private static class Day12Code {
        public void run() throws IOException {
            System.out.println("-------- Day 12 --------");
            // b-end
            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

            Map<String, Node> caves = new HashMap<>();
            br.lines().forEach(connection -> {
                String[] connecntionNames = connection.split("-");

                String leftCave = connecntionNames[0];
                String rightCave = connecntionNames[1];

                if (!caves.containsKey(leftCave)) {
                    caves.put(leftCave, new Node(leftCave));
                }
                if (!caves.containsKey(rightCave)) {
                    caves.put(rightCave, new Node(rightCave));
                }
                caves.get(leftCave).addConnection(caves.get(rightCave));
                caves.get(rightCave).addConnection(caves.get(leftCave));
            });

            Stack<PathStep> stepsToCheck = new Stack<>();
            stepsToCheck.push(new PathStep(caves.get("start"), false, new HashSet<>()));

            int totalUniquePaths = 0;

            while(!stepsToCheck.isEmpty()) {
                PathStep currentStep = stepsToCheck.pop();

                if (currentStep.cave.caveName.equals("end")) {
                    totalUniquePaths++;
                    continue;
                }

                for (Node connection : currentStep.cave.connections) {
                    if (connection.caveName.equals("start")) {
                        continue;
                    }

                    boolean visitedSmallCaveTwice = currentStep.visitedSmallCaveTwice;

                    if (!connection.isBigCave) {
                        if (visitedSmallCaveTwice && currentStep.visitedCaves.contains(connection.caveName)) {
                            continue;
                        }

                        if (currentStep.visitedCaves.contains(connection.caveName)) {
                            visitedSmallCaveTwice = true;
                        }
                    }

                    Set<String> newVisitedCaves = new HashSet<>(currentStep.visitedCaves);
                    newVisitedCaves.add(connection.caveName);

                    stepsToCheck.add(new PathStep(connection, visitedSmallCaveTwice, newVisitedCaves));
                }
            }

            System.out.println(totalUniquePaths);
        }
    }

    private static class PathStep {
        private Node cave;
        private Set<String> visitedCaves;
        private boolean visitedSmallCaveTwice;

        public PathStep(Node cave, boolean visitedSmallCaveTwice, Set<String> visitedCaves) {
            this.cave = cave;
            this.visitedCaves = new HashSet<>(visitedCaves);
            this.visitedSmallCaveTwice = visitedSmallCaveTwice;
        }
    }

    private static class Node {
        private String caveName;
        private boolean isBigCave;
        private ArrayList<Node> connections;

        public Node(String caveName) {
            this.caveName = caveName;
            this.isBigCave = caveName.matches("^[A-Z]+$");
            this.connections = new ArrayList<>();
        }

        public String getCaveName() {
            return caveName;
        }

        public boolean isBigCave() {
            return isBigCave;
        }

        public void addConnection(Node cave) {
            this.connections.add(cave);
        }

        @Override
        public String toString() {
            return caveName + " with connections=" + connections.stream().map(Node::getCaveName).collect(Collectors.toList());
        }
    }
}
