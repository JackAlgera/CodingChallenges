package FoobarGoogleInterview;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Exercise4B {
    public static void main(String[] args) {
        System.out.println(getSolarDoomsDaySolution(new int[]{3, 2}, new int[]{1, 1}, new int[]{2, 1}, 4));
        System.out.println(getSolarDoomsDaySolution(new int[]{300, 275}, new int[]{150, 150}, new int[]{185, 100}, 500));
        System.out.println(getSolarDoomsDaySolution(new int[]{2, 5}, new int[]{1, 2}, new int[]{1, 3}, 11)); // 33
        System.out.println(getSolarDoomsDaySolution(new int[]{2, 5}, new int[]{1, 3}, new int[]{1, 4}, 11)); // 31
    }

    /*
    The idea here is instead of doing complicated calculations to figure out angles and max distances in a single box, we
    create mirrors images of the original box, and calculate the mirrored positions for the hero and trainer. Then we
    just need to check the distance between the initial position of the hero and the mirror images, and check if that
    distance is < the max travel distance.
     */
    public static int getSolarDoomsDaySolution(int[] dimensions, int[] your_position, int[] trainer_position, int distance) {
        Vector initHeroPos = new Vector(your_position[0], your_position[1]);
        Vector initTrainerPos = new Vector(trainer_position[0], trainer_position[1]);
        Vector boxDimensions = new Vector(dimensions[0], dimensions[1]);

        int maxIterationsX = distance / boxDimensions.x + 1;
        int maxIterationsY = distance / boxDimensions.y + 1;

        // To store good bearings which won't cause the hero to take damage when used to fire in
        Map<Vector, Double> goodBearings = new HashMap<>();

        for (int i = -maxIterationsX; i <= maxIterationsX; i++) {
            for (int j = -maxIterationsY; j <= maxIterationsY; j++) {
                // Get mirrored position of guard
                Vector guardPos = getPosition(boxDimensions, initTrainerPos, i, j);
                double shotDistance = getDistance(initHeroPos, guardPos);

                if (shotDistance <= distance) {
                    Vector guardBearing = getBearing(guardPos.x - initHeroPos.x, guardPos.y - initHeroPos.y);
                    // Check if the bearing already exists, if it's the cause, we update the distance with the smallest one of the 2
                    if (!goodBearings.containsKey(guardBearing) || goodBearings.get(guardBearing) > shotDistance) {
                        goodBearings.put(guardBearing, shotDistance);
                    }
                }
            }
        }

        // Same thing here, but instead we remove the bearings that intersect with the hero being shot
        for (int i = -maxIterationsX; i <= maxIterationsX; i++) {
            for (int j = -maxIterationsY; j <= maxIterationsY; j++) {
                Vector heroPos = getPosition(boxDimensions, initHeroPos, i, j);
                double shotDistance = getDistance(initHeroPos, heroPos);

                if (shotDistance > 0 && shotDistance <= distance) {
                    Vector heroBearing = getBearing(heroPos.x - initHeroPos.x, heroPos.y - initHeroPos.y);

                    if (goodBearings.containsKey(heroBearing) && goodBearings.get(heroBearing) > shotDistance) {
                        goodBearings.remove(heroBearing);
                    }
                }
            }
        }

        return goodBearings.size();
    }

    public static Vector getPosition(Vector boxDimensions, Vector pos, int boxI, int boxJ) {
        int newX = boxI * boxDimensions.x + getProjection(boxDimensions.x, pos.x, boxI);
        int newY = boxJ * boxDimensions.y + getProjection(boxDimensions.y, pos.y, boxJ);

        return new Vector(newX, newY);
    }

    public static int getProjection(int boxDimension, int coordinate, int box) {
        if (box % 2 == 0) {
            return coordinate;
        } else {
            return boxDimension - coordinate;
        }
    }

    public static double getDistance(Vector a, Vector b) {
        return Math.sqrt(Math.pow(b.x - a.x, 2) + Math.pow(b.y - a.y, 2));
    }

    public static class Vector {
        int x, y;

        public Vector(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Vector vector = (Vector) o;
            return x == vector.x && y == vector.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }

    public static Vector getBearing(int x, int y) {
        int c = gcd(x, y);
        return new Vector(x / c, y / c);
    }

    public static int gcd(int a, int b) {
        if (b == 0) {
            return Math.abs(a);
        }
        return gcd(b, a % b);
    }
}
