package Year2021.Day7;

import Year2021.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

public class Day7 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day7/input.txt";

    public static void main(String[] args) throws IOException {
        new Day7Code().run();
    }

    private static class Day7Code {
        public void run() throws IOException {
            System.out.println("-------- Day 7 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

            ArrayList<Integer> crabPositions = Arrays.stream(br.readLine().split(",")).map(Integer::parseInt).collect(Collectors.toCollection(ArrayList::new));

            int maxHPos = crabPositions.stream().max(Integer::compareTo).get();

            int fuelCost = Integer.MAX_VALUE;

            for (int i = 0; i < maxHPos; i++) {
                int newFuelCost = 0;
                for (Integer pos : crabPositions) {
                    int n = Math.abs(pos - i);
                    newFuelCost += (long) n * (n + 1) / 2;
                }
                if (newFuelCost > fuelCost) {
                    System.out.printf("Pos %d will cost %d fuel\n", i, newFuelCost);
                    System.out.printf("\nBest position is %d and will cost %d fuel\n", i-1, fuelCost);
                    return;
                }

                fuelCost = newFuelCost;
                System.out.printf("Pos %d will cost %d fuel\n", i, fuelCost);
            }
        }
    }
}
