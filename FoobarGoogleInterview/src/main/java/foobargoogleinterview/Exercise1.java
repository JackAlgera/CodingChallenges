package foobargoogleinterview;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Exercise1 {
    public static void main(String[] args) {
        System.out.println(getSolarDoomsDaySolution(0) + " done");
        System.out.println(getSolarDoomsDaySolution(100) + " done");
        System.out.println(getSolarDoomsDaySolution(12) + " done");
        System.out.println(getSolarDoomsDaySolution(12345) + " done");
        System.out.println(getSolarDoomsDaySolution(1000000) + " done");
        System.out.println(getSolarDoomsDaySolution(123456) + " done");
        System.out.println(getSolarDoomsDaySolution(-1) + " done");
    }

    public static String getSolarDoomsDaySolution(int area) {
        int remainingArea = area;
        List<Integer> squares = new ArrayList<>();

        // We keep removing square areas until there is nothing left, and store each value in a list
        while (remainingArea > 0) {
            // We get the closest square number to the remaining area, and take its square value
            int closestSquareNbr = (int) Math.sqrt(remainingArea);
            int closestArea = (int) Math.pow(closestSquareNbr, 2);

            // If it's bigger than the remaining area, we know it's the previous square number, otherwise it's the current one
            if (closestArea > remainingArea) {
                closestArea = (int) Math.pow(closestSquareNbr - 1, 2);
            }

            remainingArea -= closestArea ;
            squares.add(closestArea);
        }
        return squares.stream().map(Object::toString).collect(Collectors.joining(",")); //squares.stream().mapToInt(Integer::intValue).toArray();
    }
}
