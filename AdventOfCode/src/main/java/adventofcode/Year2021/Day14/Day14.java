package adventofcode.Year2021.Day14;

import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Day14 {

    private static final String INPUT_NAME = "Year2021/Day14/input.txt";

    public static void main(String[] args) throws IOException {
        new Day14Code().run();
    }

    private static class Day14Code {
        public void run() throws IOException {
            System.out.println("-------- Day 14 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

            Map<String, Long> pairCount = new HashMap<>();
            Map<String, String> formulas = new HashMap<>();

            String polymer = br.readLine();
            for (int i = 0; i < polymer.length() - 1; i++) {
                String pair = "" + polymer.charAt(i) + polymer.charAt(i + 1);
                pairCount.put(pair, pairCount.getOrDefault(pair, 0L) + 1L);
            }

            br.readLine();

            br.lines().forEach(line -> {
                String[] vals = line.split(" -> ");
                formulas.put(vals[0], vals[1]);
            });

            for (int i = 0; i < 40; i++) {
                HashMap<String, Long> newPairCount = new HashMap<>();
                pairCount.forEach((key, count) -> {
                    String formulaChar = formulas.get(key);
                    String leftPair = "" + key.charAt(0) + formulaChar;
                    String rightPair = "" + formulaChar + key.charAt(1);
                    newPairCount.put(leftPair, count + newPairCount.getOrDefault(leftPair, 0L));
                    newPairCount.put(rightPair, count + newPairCount.getOrDefault(rightPair, 0L));
                });

                pairCount = newPairCount;
            }

            Map<String, Long> elementsCount = new HashMap<>();
            Map<String, Long> pairStartWithElement = new HashMap<>();
            Map<String, Long> pairEndWithElement = new HashMap<>();

            pairCount.forEach((key, count) -> {
                String[] elements = key.split("");
                pairStartWithElement.put(elements[0], pairStartWithElement.getOrDefault(elements[0], 0L) + count);
                pairEndWithElement.put(elements[1], pairEndWithElement.getOrDefault(elements[1], 0L) + count);
            });

            Long min = Long.MAX_VALUE;
            Long max = Long.MIN_VALUE;

            pairStartWithElement.forEach(elementsCount::put);

            pairEndWithElement.forEach((key, count) -> {
                if (!elementsCount.containsKey(key)) {
                    elementsCount.put(key, count);
                    return;
                }

                if (elementsCount.get(key) < count) {
                    elementsCount.put(key, count);
                }
            });

            for (Long count : elementsCount.values().stream().toList()) {
                if (count > max) {
                    max = count;
                }
                if (count < min) {
                    min = count;
                }
            }

            System.out.println(" HERE");
            System.out.println(pairStartWithElement);
            System.out.println(pairEndWithElement);
            System.out.println(elementsCount);
            System.out.println(" HERE");

            System.out.println("max:" + max + " - min:" + min + " = " + (max-min));
        }
    }
}
