package adventofcode.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Utilities {

//    public static String hexToBinary(char h) {
//        return switch (h) {
//            case '0' -> "0000";
//            case '1' -> "0001";
//            case '2' -> "0010";
//            case '3' -> "0011";
//            case '4' -> "0100";
//            case '5' -> "0101";
//            case '6' -> "0110";
//            case '7' -> "0111";
//            case '8' -> "1000";
//            case '9' -> "1001";
//            case 'A' -> "1010";
//            case 'B' -> "1011";
//            case 'C' -> "1100";
//            case 'D' -> "1101";
//            case 'E' -> "1110";
//            case 'F' -> "1111";
//            default -> throw new IllegalStateException("Incorrect hexadecimal value ?");
//        };
//    }

    public static BufferedReader getBufferedReader(String dayInput) throws FileNotFoundException {
        return new BufferedReader(new FileReader("AdventOfCode/src/main/java/adventofcode/" + dayInput));
    }

    /**
     * Tile indices for the 4 neighbors following neighbors of a tile:
     * TOP, RIGHT, BOTTOM, LEFT
     * NEIGHBORS_4[0] -> Rows (i)
     * NEIGHBORS_4[1] -> Columns (j)
     */
    public static int[][] NEIGHBORS_4 = new int[][]{
        { -1, 0, 1, 0 },
        { 0, 1, 0, -1 }
    };

    /**
     * Tile indices for the 8 neighbors following neighbors of a tile:
     * TOP_LEFT, TOP, TOP_RIGHT, LEFT, RIGHT, BOTTOM_LEFT, BOTTOM, BOTTOM_RIGHT
     * NEIGHBORS_8[0] -> Rows (i)
     * NEIGHBORS_8[1] -> Columns (j)
     */
    public static int[][] NEIGHBORS_8 = new int[][]{
        { -1, -1, -1, 0, 0, 1, 1, 1 },
        { -1, 0, 1, -1, 1, -1, 0, 1 }
    };
}
