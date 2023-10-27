package adventofcode.Year2021.Day15;

import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Day15 {

    private static final String INPUT_NAME = "Year2021/Day15/input.txt";

    public static void main(String[] args) throws IOException {
        new Day15Code().run();
    }

    private static class Day15Code {
        public void run() throws IOException {
            System.out.println("-------- Day 15 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
            ArrayList<String> lines = br.lines().collect(Collectors.toCollection(ArrayList::new));

            ArrayList<String> newLines = new ArrayList<>();
            int boardSegmentHeight = lines.size();
            int boardSegmentWidth = lines.get(0).length();

            int bigBoardSegmentHeight = 5 * boardSegmentHeight;
            int bigBoardSegmentWidth = 5 * boardSegmentWidth;

            for (int i = 0; i < bigBoardSegmentHeight; i++) {
                newLines.add("");
                String previousLine = lines.get(i % boardSegmentHeight);
                for (int j = 0; j < bigBoardSegmentWidth; j++) {
                    int val = Integer.parseInt("" + previousLine.charAt(j % boardSegmentWidth)) + (j / boardSegmentWidth) + (i / boardSegmentHeight);
                    newLines.set(i, newLines.get(i) + (val > 9 ? (val % 10) + 1: val));
                }
            }

            Graph graph = new Graph(newLines.size(), newLines.get(0).length(), newLines);
//            graph.printBoard();
//
            graph.printPath(graph.doAStarSearch(new Node(0, 0, 0, null), new Node(graph.getBoardHeight() - 1, graph.getBoardWidth() - 1, 0, null)));
            System.out.println("Total risk : " + graph.getPathRisk(new Node(0, 0, 0, null), graph.doAStarSearch(new Node(0, 0, 0, null), new Node(graph.getBoardHeight() - 1, graph.getBoardWidth() - 1, 0, null))));

        }
    }

    private static class Graph {
        private final static Integer[] HEIGHT_DELTA = { -1, 0, 1, 0};
        private final static Integer[] WIDTH_DELTA = { 0, 1, 0, -1};

        private final int boardHeight, boardWidth;
        private final Integer[][] board;

        public Graph(int boardHeight, int boardWidth, ArrayList<String> lines) {
            this.boardHeight = boardHeight;
            this.boardWidth = boardWidth;
            this.board = new Integer[boardHeight][boardWidth];
            for (int i = 0; i < boardHeight; i++) {
                String line = lines.get(i);
                for (int j = 0; j < boardWidth; j++) {
                    this.board[i][j] = Integer.parseInt("" + line.charAt(j));
                }
            }
        }

        public Node doAStarSearch(Node startNode, Node endNode) {
            boolean[][] visitedNodes = new boolean[boardHeight][boardHeight];
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    visitedNodes[i][j] = false;
                }
            }

            List<Node> openNodes = new ArrayList<>();
            openNodes.add(startNode);

            while(!openNodes.isEmpty()) {
                Node currentNode = popBestNode(openNodes, endNode);
                if (currentNode.getI() == endNode.getI() && currentNode.getJ() == endNode.getJ()) {
                    return currentNode;
                }

                for (int k = 0; k < 4; k++) {
                    int newI = currentNode.getI() + HEIGHT_DELTA[k];
                    int newJ = currentNode.getJ() + WIDTH_DELTA[k];

                    if (isValidPoint(newI, newJ) && !visitedNodes[newI][newJ]) {
                        openNodes.add(new Node(newI, newJ, getChildHeuristic(currentNode, newI, newJ), currentNode));
                        visitedNodes[newI][newJ] = true;
                    }
                }
            }

            return null;
        }

        public int getPathRisk(Node start, Node node) {
            int totalRisk = board[node.getI()][node.getJ()];
            while(node.getParent() != null) {
                node = node.parent;
                totalRisk += board[node.getI()][node.getJ()];
            }
            return totalRisk - board[start.getI()][start.getJ()];
        }

        public void printPath(Node node) {
            boolean[][] dontShowNodes = new boolean[boardHeight][boardHeight];
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    dontShowNodes[i][j] = false;
                }
            }

            dontShowNodes[node.getI()][node.getJ()] = true;
            while(node.getParent() != null) {
                node = node.parent;
                dontShowNodes[node.getI()][node.getJ()] = true;
            }

            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    System.out.print(dontShowNodes[i][j] ? "." : board[i][j]);
                }
                System.out.println();
            }


        }

        public int getChildHeuristic(Node parent, int i, int j) {
            return parent.heuristic + board[i][j];
        }

        public int getDistanceToEnd(Node node, Node endNode) {
            return (endNode.getI() - node.getI()) + (endNode.getJ() - node.getJ());
        }

        public Node popBestNode(List<Node> nodes, Node endNode) {
            int bestNodeIndex = 0;
            Node bestNode = nodes.get(bestNodeIndex);
            for (int i = 0; i < nodes.size(); i++) {
                Node node = nodes.get(i);
                if (node.heuristic + getDistanceToEnd(node, endNode) < bestNode.heuristic + getDistanceToEnd(bestNode, endNode)) {
                    bestNode = node;
                    bestNodeIndex = i;
                }
            }

            nodes.remove(bestNodeIndex);
            return bestNode;
        }

        public void printBoard() {
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    System.out.print(board[i][j]);
                }
                System.out.println();
            }
        }

        public boolean isValidPoint(int i, int j) {
            return i >= 0 && i < boardHeight && j >=0 && j < boardWidth;
        }

        public int getBoardHeight() {
            return boardHeight;
        }

        public int getBoardWidth() {
            return boardWidth;
        }
    }

    private static class Node {
        private int i, j;
        private int heuristic;
        private Node parent;

        public Node(int i, int j, int heuristic, Node parent) {
            this.heuristic = heuristic;
            this.parent = parent;
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        public int getHeuristic() {
            return heuristic;
        }

        public void setHeuristic(int heuristic) {
            this.heuristic = heuristic;
        }

        public Node getParent() {
            return parent;
        }

        public void setParent(Node parent) {
            this.parent = parent;
        }

        @Override
        public String toString() {
            return "(" + i + ", " + j + ")";
        }
    }
}
