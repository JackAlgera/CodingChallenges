package Year2021.Day4;

import Year2021.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day4 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day4/input.txt";

    public static void main(String[] args) throws IOException {
        new PartOne().run();
        new PartTwo().run();
    }

    private static class PartOne {
        public void run() throws IOException {
            System.out.println("-------- Day 4 - Part 1 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

            ArrayList<Integer> inputs = Arrays.stream(br.readLine().split(","))
                                              .map(Integer::parseInt)
                                              .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Board> boards = new ArrayList<>();
            int nbrBoards = 0;

            while(br.ready() && br.readLine().isBlank()) {
                boards.add(new Board(br, nbrBoards));
                nbrBoards++;
            }

            boolean foundBoard = false;
            int i = 0;

            while (!foundBoard && i < inputs.size()) {
                Integer input = inputs.get(i);

                for (Board b : boards) {
                    if (b.updateAndCheckBingo(input)) {
                        b.setWinningNumber(input);
                        System.out.println("The winning board is : ");
                        b.printBoard();
                        System.out.println("Number : " + input + " --- board score : " + b.getBoardScore());
                        foundBoard = true;
                        break;
                    }
                }
                i++;
            }
        }
    }

    private static class PartTwo {
        public void run() throws IOException {
            System.out.println("-------- Day 4 - Part 2 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

            ArrayList<Integer> inputs = Arrays.stream(br.readLine().split(","))
                                              .map(Integer::parseInt)
                                              .collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Board> boards = new ArrayList<>();
            int nbrBoards = 0;

            while(br.ready() && br.readLine().isBlank()) {
                boards.add(new Board(br, nbrBoards));
                nbrBoards++;
            }

            int nbrBoardsWon = 0;

            for (Integer input : inputs) {
                for (Board b : boards) {
                    if (b.alreadyWon()) {
                        continue;
                    }

                    if (b.updateAndCheckBingo(input)) {
                        b.setBoardPosition(nbrBoardsWon);
                        b.setWinningNumber(input);
                        nbrBoardsWon++;
                    }
                }
            }

            for (Board b : boards) {
                if (b.getBoardPosition() == boards.size() - 1) {
                    System.out.println("The last winning board is : ");
                    b.printBoard();
                    System.out.println("Board score : " + b.getBoardScore());
                }
            }
        }
    }

    private static class Board {
        private static final Integer BOARD_SIZE = 5;

        private final Integer boardId;
        private Integer winningNumber;
        private Integer boardPosition;
        private final Integer[][] values;
        private final Boolean[][] valueMarked;

        public Board(BufferedReader br, Integer boardId) throws IOException {
            this.winningNumber = null;
            this.boardPosition = null;
            this.boardId = boardId;
            this.values = new Integer[BOARD_SIZE][BOARD_SIZE];
            this.valueMarked = new Boolean[BOARD_SIZE][BOARD_SIZE];

            for (int i = 0; i < BOARD_SIZE; i++) {
                List<Integer> lineValues = Arrays.stream(br.readLine().split("[ \t]+")).filter(c -> !c.isBlank()).map(Integer::parseInt).collect(Collectors.toList());
                for (int j = 0; j < BOARD_SIZE; j++) {
                    this.values[i][j] = lineValues.get(j);
                    this.valueMarked[i][j] = false;
                }
            }
        }

        public boolean updateAndCheckBingo(int value) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (values[i][j] == value) {
                        valueMarked[i][j] = true;
                        if (isBingo()) {
                            return true;
                        }
                    }
                }
            }

            return false;
        }

        public int getBoardScore() {
            int tot = 0;
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    if (!valueMarked[i][j]) {
                        tot += values[i][j];
                    }
                }
            }

            return tot * winningNumber;
        }

        public Integer getWinningNumber() {
            return winningNumber;
        }

        public void setWinningNumber(Integer winningNumber) {
            this.winningNumber = winningNumber;
        }

        public Integer getBoardPosition() {
            return boardPosition;
        }

        public void setBoardPosition(Integer boardPosition) {
            this.boardPosition = boardPosition;
        }

        public Integer getBoardId() {
            return boardId;
        }

        public boolean alreadyWon() {
            return winningNumber != null;
        }

        public boolean isBingo() {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (checkLine(i) || checkRow(i)) {
                    return true;
                }
            }

            return false;
        }

        private boolean checkLine(int i) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                if (!valueMarked[i][j]) {
                    return false;
                }
            }

            return true;
        }

        private boolean checkRow(int j) {
            for (int i = 0; i < BOARD_SIZE; i++) {
                if (!valueMarked[i][j]) {
                    return false;
                }
            }

            return true;
        }

        public void printBoard() {
            System.out.println();
            for (int i = 0; i < BOARD_SIZE; i++) {
                for (int j = 0; j < BOARD_SIZE; j++) {
                    System.out.print(values[i][j] + " ");
                }
                System.out.print("\t\t");
                for (int j = 0; j < BOARD_SIZE; j++) {
                    System.out.print((valueMarked[i][j] ? 1 : 0) + " ");
                }
                System.out.println();
            }
            System.out.println();
        }
    }
}
