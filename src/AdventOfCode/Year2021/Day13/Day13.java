package Year2021.Day13;

import Year2021.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

public class Day13 {

    private static final String INPUT_NAME = "src/Year2022.Day13/input.txt";

    public static void main(String[] args) throws IOException {
        new Day13Code().run();
    }

    private static class Day13Code {
        public void run() throws IOException {
            System.out.println("-------- Day 13 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
            ArrayList<Point> dots = new ArrayList<>();
            ArrayList<String> instructions = new ArrayList<>();
            boolean lookingForInstructions = false;

            while(br.ready()) {
                String line = br.readLine();
                if (line.isBlank()) {
                    lookingForInstructions = true;
                    continue;
                }

                if (!lookingForInstructions) {
                    String[] posStr = line.split(",");
                    int x = Integer.parseInt(posStr[0]);
                    int y = Integer.parseInt(posStr[1]);

                    dots.add(new Point(x, y));
                } else {
                    instructions.add(line);
                }
            }

            int xMax = dots.stream().map(Point::getX).max(Integer::compareTo).get() + 1;
            int yMax = dots.stream().map(Point::getY).max(Integer::compareTo).get() + 1;

            Grid grid = new Grid(xMax, yMax, dots);

            for (int i = 0; i < instructions.size(); i++) {
                String instructionStr = instructions.get(i);

                String[] instructionLineStr = instructionStr.split(" ")[2].split("=");
                String line = instructionLineStr[0];
                int linePos = Integer.parseInt(instructionLineStr[1]);

                grid.fold(line, linePos);
//                grid.printGrid();
                System.out.println("Visible dots for fold " + (i + 1) + " : " + grid.visibleDots());
            }

            grid.printFinalGrid();
        }
    }

    private static class Grid {
        private final static Integer[] HEIGHT_DELTA = {-1, -1, -1, 0, 0, 1, 1, 1};
        private final static Integer[] WIDTH_DELTA = {-1, 0, 1, -1, 1, -1, 0, 1};

        private final int boardX, boardY;
        private final Integer[][] board;

        public Grid(int boardX, int boardY, ArrayList<Point> dots) {
            this.boardX = boardX;
            this.boardY = boardY;
            this.board = new Integer[boardY][boardX];

            for (int x = 0; x < boardX; x++) {
                for (int y = 0; y < boardY; y++) {
                    this.board[y][x] = 0;
                }
            }

            for (Point dot : dots) {
                this.board[dot.getY()][dot.getX()] = 1;
            }
        }

        public int visibleDots() {
            int tot = 0;
            for (int x = 0; x < boardX; x++) {
                for (int y = 0; y < boardY; y++) {
                    tot += this.board[y][x];
                }
            }

            return tot;
        }

        public void fold(String line, int position) {
            if (line.equals("y")) {
                for (int x = 0; x < boardX; x++) {
                    for (int y = position + 1; y < boardY; y++) {
                        if (this.board[y][x] == 1) {
                            this.board[2 * position - y][x] = 1;
                        }

                        this.board[y][x] = 0;
                    }
                }
            } else {
                for (int x = position + 1; x < boardX; x++) {
                    for (int y = 0; y < boardY; y++) {
                        if (this.board[y][x] == 1) {
                            this.board[y][2 * position - x] = 1;
                        }

                        this.board[y][x] = 0;
                    }
                }
            }
        }

        public void printGrid() {
            System.out.println();
            for (int y = 0; y < boardY; y++) {
                for (int x = 0; x < boardX; x++) {
                    System.out.print(board[y][x] == 0 ? "." : "#");
                }
                System.out.println();
            }
        }

        public void printFinalGrid() {
            System.out.println();
            for (int y = 0; y < 10; y++) {
                for (int x = 0; x < 40; x++) {
                    System.out.print(board[y][x] == 0 ? " " : "#");
                }
                System.out.println();
            }
        }
    }

    private static class Point {
        private int x, y;

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

        @Override
        public String toString() {
            return "(" + x + ", " + y + ")";
        }
    }
}
