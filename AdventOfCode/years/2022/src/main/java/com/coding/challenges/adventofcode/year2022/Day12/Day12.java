package com.coding.challenges.adventofcode.year2022.Day12;

import com.coding.challenges.adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Day12 {

    private static final String INPUT_NAME = "Year2022/Day12/input.txt";

    public static void main(String[] args) throws IOException {
        Day12 day = new Day12();
        day.part1();
        day.part2();
    }

    private void part1() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        List<List<Node>> grid = new ArrayList<>();
        int i = 0;
        Node startNode = new Node();
        Node endNode = new Node();

        while (br.ready()) {
            String line = br.readLine();
            List<Node> nodeLine = new ArrayList<>();

            for (int j = 0; j < line.length(); j++) {
                Node newNode = new Node(i, j, 0);
                char height = line.charAt(j);

                switch ("" + height) {
                    case "S" -> {
                        startNode = newNode;
                        newNode.setHeight('a');
                    }
                    case "E" -> {
                        endNode = newNode;
                        newNode.setHeight('z');
                    }
                    default -> newNode.setHeight(height);
                }

                nodeLine.add(newNode);
            }

            i++;
            grid.add(nodeLine);
        }

        System.out.println("\n-------- Day 12 - Part 1 --------");
        System.out.println("Done in " + doAStar(grid, startNode, endNode) + " steps");
        System.out.println("Expected steps: 520");
    }

    private void part2() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        List<List<Node>> grid = new ArrayList<>();
        int i = 0;
        List<Node> startNodes = new ArrayList<>();
        Node endNode = new Node();

        while (br.ready()) {
            String line = br.readLine();
            List<Node> nodeLine = new ArrayList<>();

            for (int j = 0; j < line.length(); j++) {
                Node newNode = new Node(i, j, 0);
                char height = line.charAt(j);

                switch ("" + height) {
                    case "a" -> {
                        startNodes.add(newNode);
                        newNode.setHeight(height);
                    }
                    case "S" -> {
                        startNodes.add(newNode);
                        newNode.setHeight('a');
                    }
                    case "E" -> {
                        endNode = newNode;
                        newNode.setHeight('z');
                    }
                    default -> newNode.setHeight(height);
                }

                nodeLine.add(newNode);
            }

            i++;
            grid.add(nodeLine);
        }

        int lowestSteps = Integer.MAX_VALUE;
        for (Node startNode : startNodes) {
            int steps = doAStar(grid, startNode, endNode);
            if (steps > 0 && steps < lowestSteps) {
                lowestSteps = steps;
            }
        }

        System.out.println("\n-------- Day 12 - Part 2 --------");
        System.out.println("Shortest amount of steps are " + lowestSteps);
        System.out.println("Expected steps: 508");
    }

    public int doAStar(List<List<Node>> grid, Node start, Node end) {
        int[] neighborsI = { -1, 0, 1, 0};
        int[] neighborsJ = { 0, 1, 0 , -1 };

        List<List<Boolean>> visited = new ArrayList<>();
        for (int i = 0; i < grid.size(); i++) {
            List<Boolean> newLine = new ArrayList<>();

            for (int j = 0; j < grid.get(0).size(); j++) {
                newLine.add(false);
            }
            visited.add(newLine);
        }

        visited.get(start.getI()).set(start.getJ(), true);
        start.setHeuristic(0);
        start.setSteps(0);

        List<Node> queue = new ArrayList<>();
        queue.add(start);

        while(!queue.isEmpty()) {
            Node node = queue.stream().min(Comparator.comparing(Node::getHeuristic)).get();
            queue.remove(node);

            if (node.getI() == end.getI() && node.getJ() == end.getJ()) {
                return node.getSteps();
            }

            for (int k = 0; k < 4; k++) {
                int newI = node.getI() + neighborsI[k];
                int newJ = node.getJ() + neighborsJ[k];
                if (isValidPosition(newI, newJ, grid, visited)) {
                    Node possibleNode = grid.get(newI).get(newJ);
                    if (possibleNode.getHeight() <= node.getHeight() + 1) {
                        visited.get(possibleNode.getI()).set(possibleNode.getJ(), true);
                        possibleNode.setSteps(node.getSteps() + 1);
                        possibleNode.setHeuristic(generateHeuristicValue(possibleNode));
                        possibleNode.setParent(node);
                        queue.add(possibleNode);
                    }
                }
            }
        }

        return -1;
    }

    public void printVisited(List<List<Boolean>> visited, Node currentNode) {
        for (int i = 0; i < visited.size(); i++) {
            for (int j = 0; j < visited.get(0).size(); j++) {
                System.out.print(currentNode.getI() == i && currentNode.getJ() == j ? "O" : visited.get(i).get(j) ? "x" : ".");
            }
            System.out.println();
        }
        System.out.println();
    }

    public boolean isValidPosition(int i, int j, List<List<Node>> grid, List<List<Boolean>> visited) {
        return i >= 0 && i < grid.size() && j >= 0 && j < grid.get(0).size() && !visited.get(i).get(j);
    }

    public int generateHeuristicValue(Node node) {
        return node.getSteps() + node.getHeuristicHeight();
    }

    public class Node {
        int i;
        int j;
        int height;
        Integer heuristic = -1;
        int steps = -1;
        Node parent = null;

        public Node() {
        }

        public Node(int i, int j, int height) {
            this.i = i;
            this.j = j;
            this.height = height;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public int getSteps() {
            return steps;
        }

        public void setSteps(int steps) {
            this.steps = steps;
        }

        public void setHeuristic(Integer heuristic) {
            this.heuristic = heuristic;
        }

        public Integer getHeuristic() {
            return heuristic;
        }

        public void setHeuristic(int heuristic) {
            this.heuristic = heuristic;
        }

        public void setI(int i) {
            this.i = i;
        }

        public void setJ(int j) {
            this.j = j;
        }

        public void setHeight(int height) {
            this.height = height;
        }

        public int getHeight() {
            return height;
        }

        public int getHeuristicHeight() {
            return 'z' - (char) height;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        public char getHeightAsChar() {
            return (char) height;
        }
    }
}
