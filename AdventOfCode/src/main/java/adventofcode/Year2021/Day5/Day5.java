package adventofcode.Year2021.Day5;

import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Day5 {

    private static final String INPUT_NAME = "Year2021/Day5/input.txt";

    public static void main(String[] args) throws IOException {
        new PartOneAndTwo().run();
    }

    private static class PartOneAndTwo {
        public void run() throws IOException {
            System.out.println("-------- Day 5 - Part 1 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

            ArrayList<Line> lines = new ArrayList<>();
            Board board = new Board();

            while(br.ready()) {
                String[] pointsStr = br.readLine().split(" -> ");

                String[] startPosStr = pointsStr[0].split(",");
                String[] endPosStr = pointsStr[1].split(",");

                Line line = new Line(
                        Integer.parseInt(startPosStr[0]),
                        Integer.parseInt(startPosStr[1]),
                        Integer.parseInt(endPosStr[0]),
                        Integer.parseInt(endPosStr[1])
                );

                lines.add(line);
                board.addLine(line);
//                board.printBoard();
            }

            System.out.println("Total overlapping lines : " + board.determineTotOverlappingLines());
        }
    }

    private static class Board {
        private static final int BOARD_SIZE = 1000;

        private final Integer[][] nbrOverlaps;

        public Board() {
            this.nbrOverlaps = new Integer[BOARD_SIZE][BOARD_SIZE];
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int y = 0; y < BOARD_SIZE; y++) {
                    this.nbrOverlaps[x][y] = 0;
                }
            }
        }

        public void addLine(Line line) {
            if (line.start.getX() == line.end.getX()) {
                if (line.start.getY() > line.end.getY()) {
                    for (int i = line.end.getY(); i <= line.start.getY(); i++) {
                        addValue(i, line.start.getX());
                    }
                } else {
                    for (int i = line.start.getY(); i <= line.end.getY(); i++) {
                        addValue(i, line.start.getX());
                    }
                }
            } else if (line.start.getY() == line.end.getY()) {
                if (line.start.getX() > line.end.getX()) {
                    for (int j = line.end.getX(); j <= line.start.getX(); j++) {
                        addValue(line.start.getY(), j);
                    }
                } else {
                    for (int j = line.start.getX(); j <= line.end.getX(); j++) {
                        addValue(line.start.getY(), j);
                    }
                }
            } else {
                int deltaX = Math.abs(line.end.getX() - line.start.getX());
                int ySign = Math.abs(line.end.getY() - line.start.getY()) / (line.end.getY() - line.start.getY());

                if (line.end.getX() > line.start.getX()) {
                    for (int j = 0; j <= deltaX; j++) {
                        addValue(line.start.getY() + j * ySign, line.start.getX() + j);
                    }
                } else {
                    for (int j = 0; j <= deltaX; j++) {
                        addValue(line.end.getY() - j * ySign, line.end.getX() + j);
                    }
                }
            }
        }

        public void addValue(int x, int y) {
            nbrOverlaps[x][y]++;
        }

        public void printBoard() {
            System.out.println();
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int y = 0; y < BOARD_SIZE; y++) {
                    System.out.print((nbrOverlaps[x][y] > 0 ? nbrOverlaps[x][y] : "."));
                }
                System.out.println();
            }
            System.out.println();
        }

        public int determineTotOverlappingLines() {
            int tot = 0;
            for (int x = 0; x < BOARD_SIZE; x++) {
                for (int y = 0; y < BOARD_SIZE; y++) {
                    if (nbrOverlaps[x][y] > 1) {
                        tot++;
                    }
                }
            }

            return tot;
        }
    }

    private static class Line {
        private final Point start, end;

        public Line(int startX, int startY, int endX, int endY) {
            this.start = new Point(startX, startY);
            this.end = new Point(endX, endY);
        }

        public void printLine() {
            System.out.println(start.getX() + "," + start.getY() + " -> " + end.getX() + "," + end.getY());
        }
    }

    private static class Point {
        private final int x, y;

        public Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }
    }
}
