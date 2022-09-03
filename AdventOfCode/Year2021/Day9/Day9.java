package Year2021.Day9;

import Year2021.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;
import java.util.stream.Collectors;

public class Day9 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day9/input.txt";

    public static void main(String[] args) throws IOException {
        new Day9Code().run();
    }

    private static class Day9Code {
        public void run() throws IOException {
            System.out.println("-------- Day 9 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
            ArrayList<String> lines = br.lines().collect(Collectors.toCollection(ArrayList::new));

            Board board = new Board(lines.size(), lines.get(0).length(), lines);
            board.printBoard();

            ArrayList<Point> lowPoints = board.getLowPoints();
            ArrayList<Integer> basinValues = new ArrayList<>();

            long finalVal = 1L;
            for (Point p : lowPoints) {
                basinValues.add(board.getBasinSize(p));
            }

            basinValues.sort(Collections.reverseOrder());

            for (int i = 0; i < 3; i++) {
                finalVal *= basinValues.get(i);
            }

            System.out.println(basinValues);
            System.out.println(finalVal);
        }
    }

    private static class Board {
        private final static Integer[] HEIGHT_DELTA = { -1, 0, 1, 0};
        private final static Integer[] WIDTH_DELTA = { 0, 1, 0, -1};

        private final int boardHeight, boardWidth;
        private final Integer[][] board;

        public Board(int boardHeight, int boardWidth, ArrayList<String> lines) {
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

        public int getBasinSize(Point lowPoint) {
            boolean[][] checkedPoint = new boolean[boardHeight][boardWidth];
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    checkedPoint[i][j] = false;
                }
            }

            Queue<Point> pointsToCheck = new LinkedList<>();
            pointsToCheck.add(lowPoint);
            int basinSize = 0;

            while (!pointsToCheck.isEmpty()) {
                Point p = pointsToCheck.poll();
                int pointVal = board[p.getI()][p.getJ()];

                for (int k = 0; k < 4; k++) {
                    int newI = p.getI() + HEIGHT_DELTA[k];
                    int newJ = p.getJ() + WIDTH_DELTA[k];
                    if (isValidPoint(newI, newJ) && !checkedPoint[newI][newJ] && board[newI][newJ] > pointVal && board[newI][newJ] < 9) {
                        checkedPoint[newI][newJ] = true;
                        pointsToCheck.add(new Point(newI, newJ));
                    }
                }

                basinSize++;
            }

            return basinSize;
        }

        public ArrayList<Point> getLowPoints() {
            ArrayList<Point> validPoints = new ArrayList<>();

            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    int pointVal = board[i][j];
                    boolean isLowPoint = true;

                    for (int k = 0; k < 4; k++) {
                        int newI = i + HEIGHT_DELTA[k];
                        int newJ = j + WIDTH_DELTA[k];
                        if (isValidPoint(newI, newJ) && board[newI][newJ] <= pointVal) {
                            isLowPoint = false;
                            break;
                        }
                    }

                    if (isLowPoint) {
                        validPoints.add(new Point(i, j));
                    }
                }
            }

            return validPoints;
        }

        public int getSumOfRiskLevels(ArrayList<Point> lowPoints) {
            int tot = 0;

            for (Point p : lowPoints) {
                tot += board[p.getI()][p.getJ()] + 1;
            }

            return tot;
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
    }

    private static class Point {
        private int i,j;

        public Point(int i, int j) {
            this.i = i;
            this.j = j;
        }

        public int getI() {
            return i;
        }

        public int getJ() {
            return j;
        }

        @Override
        public String toString() {
            return "(" + i + ", " + j + ")";
        }
    }
}
