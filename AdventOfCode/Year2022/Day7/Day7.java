package Year2022.Day7;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Day7 {

    private static final String INPUT_NAME = "AdventOfCode/Year2022/Day7/input.txt";

    public static void main(String[] args) throws IOException {
        Day7 day = new Day7();
        day.part2();
    }

    private void part2() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
        br.readLine();

        Node root = new Node("/", 0, null);
        Node currentNode = root;

        while (br.ready()) {
            String line = br.readLine();
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

        System.out.println(smallestDirectory);
        System.out.println("-------- Day 7 - part 2 --------");
        System.out.println("Smallest directory size: " + smallestDirectory);
        System.out.println("Expected smallest directory size: 3842121");
    }

    private class Node {
        private final String name;
        private final int size;
        private Node parent;
        private final Map<String, Node> children;

        public Node(String name, int size, Node parent) {
            this.name = name;
            this.size = size;
            this.parent = parent;
            this.children = new HashMap<>();
        }

        public String getName() {
            return name;
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

        public Map<String, Node> getChildren() {
            return children;
        }

        public Node getParent() {
            return parent;
        }

        public void addChild(Node child) {
            this.children.put(child.getName(), child);
        }
    }
}
