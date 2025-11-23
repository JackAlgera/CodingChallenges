package com.coding.challenges.google.foobar;

public class Exercise3C {
    public static void main(String[] args) {
        System.out.println(getSolarDoomsDaySolution(new int[]{1, 1, 1}));
        System.out.println(getSolarDoomsDaySolution(new int[]{1, 2, 3, 4, 5, 6}));
    }

    /*
    This problem we can break down into two steps ; first determine all the "Lucky doubles" in the list, then
    the "Lucky triples" which are in essence just two "Lucky double" pairs.
     */
    public static int getSolarDoomsDaySolution(int[] l) {
        if (l.length < 3) {
            return 0;
        }

        int nbrLuckyTriples = 0;
        int[] nbrLuckyDoubles = new int[l.length];

        // Determine all "Lucky doubles"
        for (int i = 0; i < l.length; i++) {
            for (int j = 0; j < i; j++) {
                if (l[i] % l[j] == 0) {
                    nbrLuckyDoubles[i] += 1;
                }
            }
        }

        // Start at j = 1, so we at least have two digits at start to form one pair
        for (int i = 2; i < l.length; i++) {
            for (int j = 1; j < i; j++) {
                if (l[i] % l[j] == 0) {
                    // If i % j, means we have "Lucky triples" with all other "Lucky doubles" of l[j]
                    nbrLuckyTriples += nbrLuckyDoubles[j];
                }
            }
        }

        return nbrLuckyTriples;
    }

}
