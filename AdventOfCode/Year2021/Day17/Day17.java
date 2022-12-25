package Year2021.Day17;

import utils.Utilities;
import utils.Vector;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Day17 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day17/input.txt";

    public static void main(String[] args) throws IOException {
        System.out.println("-------- Day 17 --------");

        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
        String input = br.readLine();
        System.out.println(input);

        int minX = getCoordinate("x=([-0-9]+)", input);
        int maxX = getCoordinate("x=[-0-9]*..([-0-9]+)", input);
        int minY = getCoordinate("y=([-0-9]+)", input);
        int maxY = getCoordinate("y=[-0-9]*..([-0-9]+)", input);

//        printGrid(minX, maxX, minY, maxY);
        System.out.println("x = " + minX + ".." + maxX + " : y = " + minY + ".." + maxY);

        List<Vector> initVelocities = new ArrayList<>();
        int maxVelocity = 400;

        for (int initVelocityX = 0; initVelocityX < maxVelocity; initVelocityX++) {
            for (int initVelocityY = -maxVelocity; initVelocityY < maxVelocity; initVelocityY++) {
                int posX = 0;
                int posY = 0;
                int velocityX = initVelocityX;
                int velocityY = initVelocityY;

                while (posY > minY && posX < maxX) {
                    posX += velocityX;
                    posY += velocityY;

                    if (positionIsInSquare(posX, posY, minX, maxX, minY, maxY)) {
                        initVelocities.add(new Vector(initVelocityX, initVelocityY));
                        break;
                    }

                    velocityX--;
                    velocityY--;
                    if (velocityX < 0) {
                        velocityX = 0;
                    }
                }
            }
        }

        int bestHeight = Integer.MIN_VALUE;
        Vector bestVelocity = initVelocities.get(0);
        for (Vector initVelocity : initVelocities) {
            int height = getMaxHeight(initVelocity.y());
            if (height > bestHeight) {
                bestHeight = height;
                bestVelocity = initVelocity;
            }
        }

        System.out.println("Best velocity (" + bestVelocity.x() + ", " + bestVelocity.y() + "), with max height " + bestHeight);
        System.out.println("Distinct velocities : " + initVelocities.size());
    }

    private static int getCoordinate(String regex, String input) {
        Matcher m = Pattern.compile(regex).matcher(input);
        m.find();
        return Integer.parseInt(m.group(1));
    }

    private static void printGrid(int minX, int maxX, int minY, int maxY) {
        for (int y = 10; y > minY; y--) {
            for (int x = 0; x < maxX; x++) {
                if (x == 0 && y == 0) {
                    System.out.print("S");
                } else if (positionIsInSquare(x, y, minX, maxX, minY, maxY)) {
                    System.out.print("T");
                } else {
                    System.out.print(".");
                }
            }
            System.out.println();
        }
    }

    private static boolean positionIsInSquare(int x, int y, int minX, int maxX, int minY, int maxY) {
        return x <= maxX && x >= minX && y <= maxY && y >= minY;
    }

    private static int getMaxHeight(int initVelocityY) {
        return initVelocityY * (1 + initVelocityY) / 2;
    }
}
