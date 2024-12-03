package com.coding.challenges.adventofcode.year2022.Day7;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Utilities;
import lombok.Getter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Day7 extends Day<Integer> {

    public static void main(String[] args) throws IOException {
        Day7 day = new Day7();

        day.printPart2("input", 3842121);
    }

    @Override
    public Integer part1(List<String> lines) {
        return 0;
    }

    @Override
    public Integer part2(List<String> lines) {
        Node root = new Node("/", 0, null);
        Node currentNode = root;

        for (String line : lines) {
            String[] sections = line.split(" ");

            if (sections[0].equals("$")) {
                if (sections[1].equals("cd")) {
                    if (sections[2].equals("..")) {
                        currentNode = currentNode.getParent();
                    } else {
                        currentNode = currentNode.getChildren().get(sections[2]);
                    }
                }
            } else {
                int size = 0;
                if (!sections[0].equals("dir")) {
                    size = Integer.parseInt(sections[0]);
                }
                currentNode.addChild(new Node(sections[1], size, currentNode));
            }
        }

        int currentSpaceUsed = 70000000 - root.getSize();
        int smallestDirectory = Integer.MAX_VALUE;

        Stack<Node> queue = new Stack<>();
        queue.push(root);

        while (!queue.isEmpty()) {
            Node node = queue.pop();

            if (!node.isDir()) {
                continue;
            }

            for (Node child : node.getChildren().values()) {
                queue.push(child);
            }

            int newSize = node.getSize();
            if (currentSpaceUsed + newSize > 30000000) {
                if (newSize < smallestDirectory) {
                    smallestDirectory = newSize;
                }
            }
        }

        return smallestDirectory;
    }

    private static class Node {
        @Getter
        private final String name;
        private final int size;
        @Getter
        private final Node parent;
        @Getter
        private final Map<String, Node> children;

        public Node(String name, int size, Node parent) {
            this.name = name;
            this.size = size;
            this.parent = parent;
            this.children = new HashMap<>();
        }

      public boolean isDir() {
            return this.size <= 0;
        }

        public int getSize() {
            if (!isDir()) {
                return size;
            }

            int totalSize = 0;
            for (Node child : children.values()) {
                totalSize += child.getSize();
            }
            return totalSize;
        }

      public void addChild(Node child) {
            this.children.put(child.getName(), child);
        }
    }
}
