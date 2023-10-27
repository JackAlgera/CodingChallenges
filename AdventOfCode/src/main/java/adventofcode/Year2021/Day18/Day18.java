package adventofcode.Year2021.Day18;

import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class Day18 {

    private static final String INPUT_NAME = "Year2021/Day18/input.txt";

    public static void main(String[] args) throws IOException {
        new Day18Code().run();
    }

    private static class Day18Code {
        public void run() throws IOException {
            System.out.println("-------- Day 18 --------");
            // b-end
            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
            ArrayList<String> lines = new ArrayList<>();

            ArrayList<Node> nodesToAdd = new ArrayList<>();

            br.lines().forEach(line -> {
                nodesToAdd.add(getNodeFromString(line));
                lines.add(line);
            });

            // Part one
//            Map<Integer, Node> nodes = new HashMap<>();
//            nodes.put(0, nodesToAdd.get(0));
//            for (int i = 1; i < nodesToAdd.size(); i++) {
//                nodes.put(i, new Node(null, nodes.get(i - 1), nodesToAdd.get(i), null));
//
//                nodes.get(i).left.parent = nodes.get(i);
//                nodes.get(i).right.parent = nodes.get(i);
//
//                nodes.get(i).left.isRightChild = false;
//                nodes.get(i).right.isRightChild = true;
//                nodes.get(i).addDepth(0);
//
//                reduceNode(nodes.get(i));
//            }
//            System.out.println("Final : " + nodes.get(nodesToAdd.size() - 1).printNode());
//            System.out.println("Score : " + determineMagnitude(nodes.get(nodesToAdd.size() - 1)));

            // Part two
            int biggestScore = -1;

            for (int i = 0; i < nodesToAdd.size(); i++) {
                Node firstNode = getNodeFromString(lines.get(i));
                for (int j = i + 1; j < nodesToAdd.size(); j++) {
                    Node secondNode = getNodeFromString(lines.get(j));

                    Node finalNode = new Node(null, firstNode, secondNode, null);
                    finalNode.left.parent = finalNode;
                    finalNode.right.parent = finalNode;

                    finalNode.left.isRightChild = false;
                    finalNode.right.isRightChild = true;
                    finalNode.addDepth(0);
                    reduceNode(finalNode);

                    int score = determineMagnitude(finalNode);
                    if (score > biggestScore) {
                        biggestScore = score;
                    }
                }
            }
            for (int i = 0; i < nodesToAdd.size(); i++) {
                Node firstNode = getNodeFromString(lines.get(i));
                for (int j = i + 1; j < nodesToAdd.size(); j++) {
                    Node secondNode = getNodeFromString(lines.get(j));

                    Node finalNode = new Node(null, secondNode, firstNode, null);
                    finalNode.left.parent = finalNode;
                    finalNode.right.parent = finalNode;

                    finalNode.left.isRightChild = true;
                    finalNode.right.isRightChild = false;
                    finalNode.addDepth(0);
                    reduceNode(finalNode);

                    int score = determineMagnitude(finalNode);
                    if (score > biggestScore) {
                        biggestScore = score;
                    }
                }
            }

            System.out.println("Biggest val : " + biggestScore);
        }

        public Node getNodeFromString(String line) {
            Stack<Node> values = new Stack<>();

            for (int i = 0; i < line.length(); i++) {
                String digit = "" + line.charAt(i);

                if (digit.matches("^[0-9]$")) {
                    String nextDigit = "" + line.charAt(i + 1);
                    if (nextDigit.matches("^[0-9]$")) {
                        values.push(new Node(null, null, null, Integer.parseInt(digit + nextDigit)));
                        i++;
                    } else {
                        values.push(new Node(null, null, null, Integer.parseInt(digit)));
                    }
                    continue;
                }

                if (digit.equals("]")) {
                    Node right = values.pop();
                    Node left = values.pop();

                    Node newNode = new Node(null, left, right, null);

                    left.parent = newNode;
                    left.isRightChild = false;

                    right.parent = newNode;
                    right.isRightChild = true;

                    values.push(newNode);
                }
            }

            Node root = values.pop();

            root.addDepth(0);
            reduceNode(root);
            return root;
        }

        public boolean splitNode(Node root) {
            Node node = findFirstNodeToSplit(root);

            if (node != null) {
                node.left = new Node(node, null, null, node.val / 2);
                node.right = new Node(node, null, null, (node.val + 1) / 2);

                node.left.depth = node.depth + 1;
                node.right.depth = node.depth + 1;
                node.right.isRightChild = true;

                node.val = null;
                return true;
            }
            // return true if found number >= 10
            return false;
        }

        public Node findFirstNodeToExplode(Node root) {
            Stack<Node> stack = new Stack<>();
            stack.push(root);

            while (!stack.isEmpty()) {
                Node currentNode = stack.pop();

                if (currentNode.depth >= 4 && currentNode.val == null) {
                    return currentNode;
                }

                if (currentNode.right != null) {
                    stack.push(currentNode.right);
                }
                if (currentNode.left != null) {
                    stack.push(currentNode.left);
                }
            }

            return null;
        }

        public Node findFirstNodeToSplit(Node root) {
            Stack<Node> stack = new Stack<>();
            stack.push(root);

            while (!stack.isEmpty()) {
                Node currentNode = stack.pop();

                if (currentNode.val != null && currentNode.val >= 10) {
                    return currentNode;
                }

                if (currentNode.right != null) {
                    stack.push(currentNode.right);
                }
                if (currentNode.left != null) {
                    stack.push(currentNode.left);
                }
            }

            return null;
        }

        public void reduceNode(Node root) {
            boolean shouldKeepReducing = true;

            while (shouldKeepReducing) {
                Node nodeToExplode = findFirstNodeToExplode(root);
                if (nodeToExplode != null) {
                    nodeToExplode.explodeNode();
                    continue;
                }

                if (splitNode(root)) {
                    continue;
                }

                shouldKeepReducing = false;
            }
        }

        public int determineMagnitude(Node node) {
            if (node.val != null) {
                return node.val;
            }

            return 3 * determineMagnitude(node.left) + 2 * determineMagnitude(node.right);
        }
    }

    private static class Node {
        private Node parent;
        private boolean isRightChild;
        private int depth;
        private Node left, right;
        private Integer val;

        public Node(Node parent, Node left, Node right, Integer val) {
            this.left = left;
            this.right = right;
            this.val = val;
            this.parent = parent;
            this.isRightChild = false;
            this.depth = -1;
        }

        public void explodeNode() {
            int leftVal = left.val;
            int rightVal = right.val;

            // Add left value
            // Go up
            Node currentParent = parent;
            boolean isCurrentRightChild = isRightChild;
            while (currentParent != null && !isCurrentRightChild) {
                isCurrentRightChild = currentParent.isRightChild;
                currentParent = currentParent.parent;
            }

            // Go down
            if (currentParent != null) {
                currentParent = currentParent.left;
                while (currentParent.val == null) {
                    currentParent = currentParent.right;
                }

                currentParent.val += leftVal;
            }

            // Add right value
            // Go up
            currentParent = parent;
            isCurrentRightChild = isRightChild;
            while (currentParent != null && isCurrentRightChild) {
                isCurrentRightChild = currentParent.isRightChild;
                currentParent = currentParent.parent;
            }

            // Go down
            if (currentParent != null) {
                currentParent = currentParent.right;
                while (currentParent.val == null) {
                    currentParent = currentParent.left;
                }

                currentParent.val += rightVal;
            }

            if (parent != null) {
                if (isRightChild) {
                    parent.right = new Node(parent, null, null, 0);
                    parent.right.depth = parent.depth + 1;
                    parent.right.isRightChild = true;
                } else {
                    parent.left = new Node(parent, null, null, 0);
                    parent.left.depth = parent.depth + 1;
                }
            }
        }

        public String printNode() {
            if (val != null) {
                return "" + val;
            }

            return "[" + left.printNode() + "," + right.printNode() + "]";
        }

        public void addDepth(int depth) {
            this.depth = depth;

            if (val != null) {
                return;
            }

            if (left != null) {
                left.addDepth(depth + 1);
            }

            if (right != null) {
                right.addDepth(depth + 1);
            }
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        public boolean isRightChild() {
            return isRightChild;
        }

        public void setRightChild(boolean rightChild) {
            isRightChild = rightChild;
        }

        public Node getLeft() {
            return left;
        }

        public void setLeft(Node left) {
            this.left = left;
        }

        public Node getRight() {
            return right;
        }

        public void setRight(Node right) {
            this.right = right;
        }

        public Integer getVal() {
            return val;
        }

        public void setVal(Integer val) {
            this.val = val;
        }
    }
}
