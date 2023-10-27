package foobargoogleinterview;

public class Exercise2A {
    public static void main(String[] args) {
//        printShit(getSolarDoomsDaySolution(new int[]{4, 17, 50}));
        printShit(getSolarDoomsDaySolution(new int[]{4, 30, 50}));
        printShit(getSolarDoomsDaySolution(new int[]{10, 70, 2000, 3945}));
//        System.out.println(getSolarDoomsDaySolution(new int[]{10, 10, 10, 10}));
//        System.out.println(getSolarDoomsDaySolution(new int[]{4, 5}));
    }

    public static void printShit(int [] vals) {
        System.out.println(vals[0] + " : " + vals[1]);
    }

    /*
    Each peg's position (A, B, C) has a radius (rA, rB, rC)
    ---- A ------------ B ------- C
         rA             rB        rC

    After doing some magic maths, I realized we have a system of equations to solve.
    We can then deduce the value of the first gear (rA = `firstGearRadius`), which
    depends on the parity of the total number of pegs.
    - If even   : we add a factor 2/3
    - If odd    : we add a factor 2
    */
    public static int[] getSolarDoomsDaySolution(int[] pegs) {
        int numerator = 0;
        for (int i = 1; i < pegs.length; i++) {
            int distPegs = pegs[i] - pegs[i - 1];
            numerator += distPegs * Math.pow(-1, i - 1);
        }

        numerator *= 2;
        int denominator = (pegs.length % 2 == 0) ? 3 : 1;

        // Make sure it's in its simplest form
        if(numerator % denominator == 0) {
            numerator /= denominator;
            denominator = 1;
        }

        float firstGearRadius = (float) numerator / (float) denominator;
        // Each gear has a radius >= 1, and as rA = 2 * rC, we have rA >= 2, so we check the opposite
        if (firstGearRadius < 2) {
            return new int[] {-1, -1};
        }

        // Make sure all gear radius sizes follow the >= 1 rule
        float currentGearRadius = firstGearRadius;
        for (int i = 1; i < pegs.length; i++) {
            int distPegs = pegs[i] - pegs[i - 1];
            float nextGearRadius = distPegs - currentGearRadius;
            if (nextGearRadius < 1) {
                return new int[] {-1, -1};
            }
            currentGearRadius = nextGearRadius;
        }

        return new int[] {numerator, denominator};
    }
}
