package Year2021.Day11;

import Year2021.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class Day11 {

    private static final String INPUT_NAME = "src/Year2022.Day11/input.txt";

    public static void main(String[] args) throws IOException {
        new Day11Code().run();
    }

    private static class Day11Code {
        public void run() throws IOException {
            System.out.println("-------- Day 11 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
            ArrayList<String> lines = br.lines().collect(Collectors.toCollection(ArrayList::new));

            Grid grid = new Grid(lines.size(), lines.get(0).length(), lines);

            int totalFlashes = 0;
            int i = 0;

            while(!grid.isEverythingFlashing()) {
                grid.playStep();
                i++;
            }

            System.out.println("i " + i);
        }
    }

    private static class Grid {
        private final static Integer[] HEIGHT_DELTA = { -1, -1, -1, 0, 0, 1, 1, 1};
        private final static Integer[] WIDTH_DELTA = { -1, 0, 1, -1, 1, -1, 0, 1};

        private final int boardHeight, boardWidth;
        private final Octopus[][] board;

        public Grid(int boardHeight, int boardWidth, ArrayList<String> lines) {
            this.boardHeight = boardHeight;
            this.boardWidth = boardWidth;
            this.board = new Octopus[boardHeight][boardWidth];
            for (int i = 0; i < boardHeight; i++) {
                String line = lines.get(i);
                for (int j = 0; j < boardWidth; j++) {
                    this.board[i][j] = new Octopus(Integer.parseInt("" + line.charAt(j)));
                }
            }
        }

        public int playStep() {
            int totalFlashes = 0;
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    Octopus currentOctopus = board[i][j];
                    currentOctopus.energyLevel++;
                    currentOctopus.flashedThisRound = false;
                }
            }

            boolean shouldContinueStep = true;

            while (shouldContinueStep) {
                shouldContinueStep = false;
                for (int i = 0; i < boardHeight; i++) {
                    for (int j = 0; j < boardWidth; j++) {
                        Octopus currentOctopus = board[i][j];

                        if (currentOctopus.energyLevel > 9 && !currentOctopus.flashedThisRound) {
                            totalFlashes++;
                            shouldContinueStep = true;
                            currentOctopus.flashedThisRound = true;
                            currentOctopus.energyLevel = 0;
                            for (int k = 0; k < HEIGHT_DELTA.length; k++) {
                                int newI = i + HEIGHT_DELTA[k];
                                int newJ = j + WIDTH_DELTA[k];
                                if (isValidPoint(newI, newJ) && !board[newI][newJ].flashedThisRound) {
                                    board[newI][newJ].energyLevel++;
                                }
                            }
                        }
                    }
                }
            }

            return totalFlashes;
        }

        public boolean isEverythingFlashing() {
            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    if (board[i][j].getEnergyLevel() != 0) {
                        return false;
                    }
                }
            }

            return true;
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

        public boolean isValidPoint(int i, int j) {
            return i >= 0 && i < boardHeight && j >=0 && j < boardWidth;
        }
    }

    private static class Octopus {
        private int energyLevel;
        private boolean flashedThisRound;

        public Octopus(int energyLevel) {
            this.energyLevel = energyLevel;
            this.flashedThisRound = false;
        }

        public int getEnergyLevel() {
            return energyLevel;
        }

        public void setEnergyLevel(int energyLevel) {
            this.energyLevel = energyLevel;
        }

        public boolean isFlashedThisRound() {
            return flashedThisRound;
        }

        public void setFlashedThisRound(boolean flashedThisRound) {
            this.flashedThisRound = flashedThisRound;
        }

        @Override
        public String toString() {
            return "" + energyLevel;
        }
    }
}
