package foobargoogleinterview;

import java.math.BigInteger;
import java.util.LinkedList;
import java.util.Queue;

public class Exercise3A {
    public static void main(String[] args) {
        System.out.println(getSolarDoomsDaySolution("1", "1"));
        System.out.println(getSolarDoomsDaySolution("2", "1"));
        System.out.println(getSolarDoomsDaySolution("2", "0"));
        System.out.println(getSolarDoomsDaySolution("2", "-1"));
        System.out.println(getSolarDoomsDaySolution("0", "0"));
        System.out.println(getSolarDoomsDaySolution("4", "7"));
        System.out.println(getSolarDoomsDaySolution("5", "5"));
        System.out.println(getSolarDoomsDaySolution("7", "5"));
        System.out.println(getSolarDoomsDaySolution("1", "100"));
        System.out.println(getSolarDoomsDaySolution("1", "10000"));
        System.out.println(getSolarDoomsDaySolution("1", "10000000"));
        System.out.println(getSolarDoomsDaySolution("100000000000000000000000000000000000000000000000000", "100000000000000000000000000000000000000000000000000"));
        System.out.println(getSolarDoomsDaySolution("2", "100000000000000000000000000000000000000000000000000"));
        System.out.println(getSolarDoomsDaySolution("100000000000000000000000000000000000000000000000000", "100000000000000000000000000000000000000000000000000"));
        System.out.println(getSolarDoomsDaySolution("99999999999999999999999999999999999999999999999999", "100000000000000000000000000000000000000000000000000"));
        System.out.println(getSolarDoomsDaySolution("100000000000000000000000000000000000000000000000000", "99999999999999999999999999999999999999999999999999"));
        System.out.println(getSolarDoomsDaySolution("2", "100000000000000001"));
    }

    /*
    After trying for a while to do a binary tree search to find the solution, I realized
    it could be done way faster by starting at the end result (x,y) and working our way
    down. We can also further increase the efficiency by reducing the number of duplicate actions we do (i.e if
    x == 2 and y == 10, we will remove 2 multiple times from y.
    Couple of edge cases:
    - if x == y, then there's no solution
    - if x == 1 (respectively y) then there will be y-1 (respectively x-1) more generations til the solution
     */
    public static String getSolarDoomsDaySolution(String x, String y) {
        BigInteger xVal = new BigInteger(x);
        BigInteger yVal = new BigInteger(y);
        BigInteger generations = BigInteger.ZERO;

        while (xVal.signum() == 1 && yVal.signum() == 1) {
            if (xVal.longValue() == 1 && yVal.longValue() == 1) {
                return generations.toString();
            }

            if (xVal.longValue() == 1) {
                return generations.add(yVal).subtract(BigInteger.ONE).toString();
            }

            if (yVal.longValue() == 1) {
                return generations.add(xVal).subtract(BigInteger.ONE).toString();
            }

            BigInteger newGenerations;

            switch (xVal.compareTo(yVal)) {
                case -1:
                    newGenerations = yVal.divide(xVal);
                    yVal = yVal.subtract(xVal.multiply(newGenerations));
                    break;
                case 0:
                    return "impossible";
                default:
                    newGenerations = xVal.divide(yVal);
                    xVal = xVal.subtract(yVal.multiply(newGenerations));
                    break;
            }

            generations = generations.add(newGenerations);
        }

        return "impossible";
    }
}
