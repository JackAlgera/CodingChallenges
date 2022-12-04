package Year2021.Day25;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Day25 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day25/input.txt";

    public static void main(String[] args) throws IOException {
        new Day25Code().run();
    }

    private static class Day25Code {
        public void run() throws IOException {
            System.out.println("-------- Day 25 --------");
            // b-end
            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
            ArrayList<String> lines = br.lines().collect(Collectors.toCollection(ArrayList::new));

            int boardSegmentHeight = lines.size();
            int boardSegmentWidth = lines.get(0).length();

            Grid grid = new Grid(boardSegmentHeight, boardSegmentWidth, lines);
            boolean somethingMoved = true;
            int i = 0;

            grid.printGrid();
//            for (int j = 0; j < 58; j++) {
//                grid.moveCucumbers();
//            }
//            grid.printGrid();
            while(somethingMoved) {
                somethingMoved = grid.moveCucumbers();
                i++;
            }

            System.out.println(i);
        }
    }

    private static class Grid {
        private final static Integer[] HEIGHT_DELTA = {-1, -1, -1, 0, 0, 1, 1, 1};
        private final static Integer[] WIDTH_DELTA = {-1, 0, 1, -1, 1, -1, 0, 1};

        private final int boardHeight, boardWidth;
        private String[][] board;

        public Grid(int boardHeight, int boardWidth, ArrayList<String> lines) {
            this.boardHeight = boardHeight;
            this.boardWidth = boardWidth;
            this.board = new String[boardHeight][boardWidth];
            for (int i = 0; i < boardHeight; i++) {
                String line = lines.get(i);
                for (int j = 0; j < boardWidth; j++) {
                    this.board[i][j] = "" + line.charAt(j);
                }
            }
        }

        public void printGrid() {
            System.out.println();
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    System.out.print(board[i][j]);
                }
                System.out.println();
            }
        }

        public boolean moveCucumbers() {
            String[][] newBoard = new String[boardHeight][boardWidth];
            copyBoard(newBoard, board);
            boolean somethingMoved = false;
            // Move east first
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    if (!board[i][j].equals(">")) {
                        continue;
                    }

                    int newJ = (j + 1) % boardWidth;
                    if (board[i][newJ].equals(".")) {
                        newBoard[i][j] = ".";
                        newBoard[i][newJ] = ">";
                        somethingMoved = true;
                    }
                }
            }
            copyBoard(board, newBoard);
            // Move south
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    if (!board[i][j].equals("v")) {
                        continue;
                    }

                    int newI = (i + 1) % boardHeight;
                    if (board[newI][j].equals(".")) {
                        newBoard[i][j] = ".";
                        newBoard[newI][j] = "v";
                        somethingMoved = true;
                    }
                }
            }
            copyBoard(board, newBoard);
            return somethingMoved;
        }

        public void copyBoard(String[][] newBoard, String[][] oldBoard) {
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    newBoard[i][j] = oldBoard[i][j];
                }
            }
        }

        public boolean isValidPoint(int i, int j) {
            return i >= 0 && i < boardHeight && j >=0 && j < boardWidth;
        }
    }
}
