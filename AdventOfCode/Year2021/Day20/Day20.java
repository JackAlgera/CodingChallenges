package Year2021.Day20;

import Year2021.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Day20 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day20/input.txt";

    public static void main(String[] args) throws IOException {
        System.out.println("-------- Day 20 --------");

        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        String imageEnhancementAlgo = br.readLine();
        br.readLine();
        List<String> lines = new ArrayList<>();

        while (br.ready()) {
            lines.add(br.readLine());
        }

        Grid grid = new Grid(lines.size(), lines.get(0).length(), lines);

        for (int i = 0; i < 50; i++) {
            grid.addExtraLayers(i);
            grid.enhanceImage(imageEnhancementAlgo, i);
        }
        System.out.println("\nTot lit pixels = " + grid.nbrOfLitPixels());
    }

    private static class Grid {
        private final static Integer[] HEIGHT_DELTA = { -1, -1, -1, 0, 0, 0, 1, 1, 1};
        private final static Integer[] WIDTH_DELTA = { -1, 0, 1, -1, 0, 1, -1, 0, 1};

        private int boardHeight, boardWidth;
        private int[][] pixels;

        public Grid(int boardHeight, int boardWidth, List<String> lines) {
            this.boardHeight = boardHeight;
            this.boardWidth = boardWidth;
            this.pixels = new int[boardHeight][boardWidth];
            for (int i = 0; i < boardHeight; i++) {
                String line = lines.get(i);
                for (int j = 0; j < boardWidth; j++) {
                    this.pixels[i][j] = ("" + line.charAt(j)).equals("#") ? 1 : 0;
                }
            }
        }

        // Even step -> all infinite pixels are .,
        // Odd step  -> all infinite pixels are #
        public void enhanceImage(String imageEnhancementAlgo, int step) {
            int[][] tempPixels = new int[boardHeight][boardWidth];
            for (int i = 0; i < pixels.length; i++) {
                for (int j = 0; j < pixels[0].length; j++) {
                    StringBuilder binaryNum = new StringBuilder();
                    for (int k = 0; k < HEIGHT_DELTA.length; k++) {
                        int newI = i + HEIGHT_DELTA[k];
                        int newJ = j + WIDTH_DELTA[k];

                        if (!isValidPoint(newI, newJ, boardHeight, boardWidth)) {
                            binaryNum.append(step % 2);
                        } else {
                            binaryNum.append(pixels[newI][newJ]);
                        }
                    }

                    tempPixels[i][j] = ("" + imageEnhancementAlgo.charAt(Integer.parseInt(binaryNum.toString(), 2))).equals("#") ? 1 : 0;
                }
            }

            pixels = tempPixels;
        }

        // Even step -> all infinite pixels are .,
        // Odd step  -> all infinite pixels are #
        public void addExtraLayers(int step) {
            int borderWidth = 1;
            this.boardHeight += borderWidth * 2;
            this.boardWidth += borderWidth * 2;
            int[][] tempPixels = new int[boardHeight][boardWidth];

            for (int i = 0; i < boardHeight; i++) {
                for (int j = 0; j < boardWidth; j++) {
                    if (isValidPoint(i - borderWidth, j - borderWidth, pixels.length, pixels[0].length)) {
                        tempPixels[i][j] = pixels[i - borderWidth][j - borderWidth];
                    } else {
                        if (step % 2 == 0) {
                            tempPixels[i][j] = 0;
                        } else {
                            tempPixels[i][j] = 1;
                        }
                    }
                }
            }

            this.pixels = tempPixels;
        }

        public long nbrOfLitPixels() {
            long tot = 0;
            for (int i = 0; i < pixels.length; i++) {
                for (int j = 0; j < pixels[0].length; j++) {
                    tot += pixels[i][j];
                }
            }
            return tot;
        }

        public void printGrid() {
            System.out.println();
            for (int i = 0; i < 10; i++) {
                for (int j = 0; j < 10; j++) {
                    System.out.print(pixels[i][j] == 1 ? "#" : ".");
                }
                System.out.println();
            }
        }

        public boolean isValidPoint(int i, int j, int boardHeight, int boardWidth) {
            return i >= 0 && i < boardHeight && j >=0 && j < boardWidth;
        }
    }
}
