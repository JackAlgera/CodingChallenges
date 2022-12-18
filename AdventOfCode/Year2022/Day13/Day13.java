package Year2022.Day13;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.regex.Pattern;

public class Day13 {

    private static final String INPUT_NAME = "AdventOfCode/Year2022/Day13/input.txt";

    public static void main(String[] args) throws IOException {
        Day13 day = new Day13();
        day.part1();
        day.part2();
    }

    private void part1() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
        int pairsChecked = 0;
        int sum = 0;

        while (br.ready()) {
            pairsChecked++;
            Node firstPacket = new Node();
            generateNode(firstPacket, br.readLine().substring(1));
            Node secondPacket = new Node();
            generateNode(secondPacket, br.readLine().substring(1));
            br.readLine();

            boolean inCorrectOrder = compare(firstPacket, secondPacket);
            if (inCorrectOrder) {
                sum += pairsChecked;
            }
        }

        System.out.println("\n-------- Day 13 - Part 1 --------");
        System.out.println("Pairs checked: " + pairsChecked + " - sum correct order: " + sum);
        System.out.println("Expected sum correct order: 5555");
    }

    private void part2() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        List<Node> packets = new ArrayList<>();
        Node firstDivider = new Node(null, List.of(new Node(2)));
        Node secondDivider = new Node(null, List.of(new Node(6)));
        packets.add(firstDivider);
        packets.add(secondDivider);

        while (br.ready()) {
            String line = br.readLine();

            if (line.equals("")) {
                continue;
            }

            Node packet = new Node();
            generateNode(packet, line.substring(1));
            packets.add(packet);
        }

        packets.sort((p1, p2) -> {
            Boolean val = compare(p1, p2);

            if (val == null) {
                return 0;
            }

            return val ? -1 : 1;
        });

        System.out.println("\n-------- Day 13 - Part 2 --------");
        System.out.println("Multiply together: " + ((packets.indexOf(firstDivider) + 1) * (packets.indexOf(secondDivider) + 1)));
        System.out.println("Expected multiply together: 22852");
    }

    public Boolean compare(Node left, Node right) {
        if (left.isValue() && right.isValue()) {
            if (left.getValue() < right.getValue()) {
                return true;
            }

            if (left.getValue() > right.getValue()) {
                return false;
            }

            return null;
        }

        if (left.isList() && right.isList()) {
            int maxSize = Math.max(left.getChildren().size(), right.getChildren().size());
            boolean isSameSize = left.getChildren().size() == right.getChildren().size();

            Boolean response = null;

            for (int i = 0; i < maxSize; i++) {
                if (!isSameSize && i > left.getChildren().size() - 1) {
                    return true;
                }

                if (!isSameSize && i > right.getChildren().size() - 1) {
                    return false;
                }

                response = compare(left.getChildren().get(i), right.getChildren().get(i));

                if (response != null) {
                    return response;
                }
            }

            return response;
        }

        if (left.isList() && right.isValue()) {
            right.addChildren(new Node(right.getValue(), right));
            right.setValue(null);
            return compare(left, right);
        }

        if (left.isValue() && right.isList()) {
            left.addChildren(new Node(left.getValue(), left));
            left.setValue(null);
            return compare(left, right);
        }

        return null;
    }

    public void generateNode(Node root, String input) {
        if (input.isBlank()) {
            return;
        }

        String val = "" + input.charAt(0);

        if (val.equals("[")) {
            Node child = new Node(null, root);
            root.addChildren(child);
            generateNode(child, input.substring(1));
            return;
        }

        if (val.equals("]")) {
            generateNode(root.getParent(), input.substring(1));
            return;
        }

        if (val.equals(",")) {
            generateNode(root, input.substring(1));
            return;
        }

        Pattern pattern = Pattern.compile("\\d");
        int i;

        for (i = 1; i < input.length(); i++) {
            if (pattern.matcher("" + input.charAt(i)).find()) {
                val += "" + input.charAt(i);
            } else {
                break;
            }
        }
        Node child = new Node(Integer.parseInt(val), root);

        root.addChildren(child);
        generateNode(root, input.substring(i));
    }

    private class Node {

        private Node parent;
        private Integer value;
        private final List<Node> children;

        public Node() {
            this.children = new ArrayList<>();
        }

        public Node(Integer value) {
            this.value = value;
            this.children = new ArrayList<>();
        }

        public Node(Integer value, List<Node> children) {
            this.value = value;
            this.children = children;
        }

        public Node(Integer value, Node parent) {
            this.value = value;
            this.parent = parent;
            this.children = new ArrayList<>();
        }

        public boolean isValue() {
            return children.isEmpty() && value != null;
        }

        public boolean isList() {
            return value == null;
        }

        public Node getParent() {
            return parent;
        }

        public Integer getValue() {
            return value;
        }

        public void setValue(Integer value) {
            this.value = value;
        }

        public List<Node> getChildren() {
            return children;
        }

        public void addChildren(Node child) {
            this.children.add(child);
        }

        public void printTree() {
            if (children.isEmpty()) {
                System.out.print(value + " ");
                return;
            }

            System.out.print("[");
            for (Node child : children) {
                child.printTree();
                System.out.print(",");
            }
            System.out.print("]");
        }
    }
}
