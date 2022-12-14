package Year2022.Day13;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class Day13 {

    private static final String INPUT_NAME = "AdventOfCode/Year2022/Day13/input.txt";

    public static void main(String[] args) throws IOException {
        Day13 day = new Day13();
        day.part1();
    }

    private void part1() throws IOException {
        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
        int pairsChecked = 0;
        int sum = 0;

        while (br.ready()) {
            pairsChecked++;
            Node firstPacket = new Node();
            generateNode(firstPacket, br.readLine().substring(1));
//            firstPacket.printTree();
//            System.out.println("\nNext\n");
            Node secondPacket = new Node();
            generateNode(secondPacket, br.readLine().substring(1));
            br.readLine();

            boolean inCorrectOrder = compare(firstPacket, secondPacket);
            if (inCorrectOrder) {
                sum += pairsChecked;
//                System.out.println("Is in correct order ? " + inCorrectOrder + " \n");

            }
        }
        System.out.println("Pairs checked: " + pairsChecked + " - sum correct order: " + sum);
    }

    public boolean compare(Node left, Node right) {
        if (left.isEmptyAndNull() && right.isEmptyAndNull()) {
            return true;
        }

        if (left.isEmptyAndNull() && right.hasSomething()) {
            return true;
        }

        if (left.hasSomething() && right.isEmptyAndNull()) {
            return false;
        }

        if (left.isValue() && right.isValue()) {
            return left.getValue() <= right.getValue();
        }

        if (left.isValue() && !right.isValue()) {
            if (right.getChildren().size() > 1) {
                return false;
            }

            return compare(left, right.getChildren().get(0));
        }

        if (!left.isValue() && right.isValue()) {
            return compare(left.getChildren().get(0), right);
        }

        if (!left.isValue() && !right.isValue()) {
            if (left.getChildren().size() > right.getChildren().size()) {
                return false;
            }

            boolean inCorrectOrder = true;
            for (int i = 0; i < left.getChildren().size(); i++) {
                inCorrectOrder = inCorrectOrder & compare(left.getChildren().get(i), right.getChildren().get(i));
            }

            return inCorrectOrder;
        }

        return true;
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

        Node child = new Node(Integer.parseInt(val), root);
        root.addChildren(child);
        generateNode(root, input.substring(1));
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

        public Node(Integer value, Node parent) {
            this.value = value;
            this.parent = parent;
            this.children = new ArrayList<>();
        }

        public boolean isEmptyAndNull() {
            return children.isEmpty() && value == null;
        }

        public boolean hasSomething() {
            return children.isEmpty() || value != null;
        }

        public boolean isValue() {
            return children.isEmpty();
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
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

// 561 -> too low
