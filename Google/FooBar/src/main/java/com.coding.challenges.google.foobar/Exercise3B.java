package com.coding.challenges.google.foobar;

import java.util.HashMap;
import java.util.Map;

public class Exercise3B {
    public static void main(String[] args) {
        System.out.println(getSolarDoomsDaySolution(3));
        System.out.println(getSolarDoomsDaySolution(5));
        System.out.println(getSolarDoomsDaySolution(200));
    }

    /*
    For this one, we can break it down into stages and at each stage we have two choices: build a new step with the
    current height, or don't build and check the next height. Of course, we can't build a step if the new height is
    greater than or equal to the amount of bricks we have left.
    We also seem to do a lot of repetitive calculations for a given height and amount of bricks left, so to
    increase efficiency we can cache calculations.
     */
    public static int getSolarDoomsDaySolution(int n) {
        return countSteps(1, n, new HashMap<>(new HashMap<>())) - 1;
    }

    public static int countSteps(int height, int bricksLeft, Map<Integer, Map<Integer, Integer>> cache) {
        if (cache.containsKey(height) && cache.get(height).containsKey(bricksLeft)) {
            return cache.get(height).get(bricksLeft);
        }

        if (bricksLeft == 0) {
            return 1;
        }

        if (bricksLeft < height) {
            return 0;
        }

        int cachedCountSteps = countSteps(height + 1, bricksLeft - height, cache) + countSteps(height + 1, bricksLeft, cache);

        if (!cache.containsKey(height)) {
            cache.put(height, new HashMap<>());
        }
        cache.get(height).put(bricksLeft, cachedCountSteps);

        return cachedCountSteps;
    }
}
