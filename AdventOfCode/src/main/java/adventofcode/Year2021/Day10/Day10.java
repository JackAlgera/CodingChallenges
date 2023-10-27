package adventofcode.Year2021.Day10;

import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.stream.Collectors;

public class Day10 {

    private static final String INPUT_NAME = "Year2021/Day10/input.txt";

    public static void main(String[] args) throws IOException {
        new Day10Code().run();
    }

    private static class Day10Code {
        public void run() throws IOException {
            System.out.println("-------- Day 10 --------");

            ArrayList<String> opens = new ArrayList<>(List.of("(", "[", "{", "<"));
            ArrayList<String> closes = new ArrayList<>(List.of(")", "]", "}", ">"));

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);
            ArrayList<String> lines = br.lines().collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Long> finalScores = new ArrayList<>();

            Map<String, Integer> nbrCorruptedChars = new HashMap<>();

            nbrCorruptedChars.put(")", 0);
            nbrCorruptedChars.put("]", 0);
            nbrCorruptedChars.put(">", 0);
            nbrCorruptedChars.put("}", 0);

            for (String line : lines) {
                Stack<String> openCharOrder = new Stack<>();

                boolean lineIsCorrupted = false;
                int i = 0;

                while (i < line.length() && !lineIsCorrupted) {
                    String val = "" + line.charAt(i);

                    if (opens.contains(val)) {
                        openCharOrder.push(val);
                    } else if (closes.contains(val)) {
                        String lastOpenValue = openCharOrder.pop();

                        switch (val) {
                            case ")":
                                if (!lastOpenValue.equals("(")) {
                                    lineIsCorrupted = true;
                                    nbrCorruptedChars.put(val, nbrCorruptedChars.get(val) + 1);
                                }
                                break;
                            case "]":
                                if (!lastOpenValue.equals("[")) {
                                    lineIsCorrupted = true;
                                    nbrCorruptedChars.put(val, nbrCorruptedChars.get(val) + 1);
                                }
                                break;
                            case "}":
                                if (!lastOpenValue.equals("{")) {
                                    lineIsCorrupted = true;
                                    nbrCorruptedChars.put(val, nbrCorruptedChars.get(val) + 1);
                                }
                                break;
                            case ">":
                                if (!lastOpenValue.equals("<")) {
                                    lineIsCorrupted = true;
                                    nbrCorruptedChars.put(val, nbrCorruptedChars.get(val) + 1);
                                }
                                break;
                        }
                    }

                    i++;
                }

                if (!lineIsCorrupted) {
                    long totScore = 0L;
                    while (!openCharOrder.isEmpty()) {
                        totScore = (totScore * 5) + getCharValue(openCharOrder.pop());
                    }
                    finalScores.add(totScore);
                }
            }

            finalScores.sort(Collections.reverseOrder());

            System.out.println(finalScores);
            System.out.println(finalScores.get(finalScores.size()/2));
//            System.out.println(nbrCorruptedChars);
        }

        public long getCharValue(String val) {
            switch (val) {
                case "(" -> {
                    return 1L;
                }
                case "[" -> {
                    return 2L;
                }
                case "{" -> {
                    return 3L;
                }
                case "<" -> {
                    return 4L;
                }
            }

            return 0;
        }
    }
}
