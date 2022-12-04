package Year2021.Day8;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Day8 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day8/input.txt";

    public static void main(String[] args) throws IOException {
        new Day8Code().run();
    }

    private static class Day8Code {
        public void run() throws IOException {
            System.out.println("-------- Day 8 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

            int totDigits = 0;

            while (br.ready()) {
                List<Boolean> foundDigit = Arrays.asList(false, false, false, false, false, false, false, false, false, false);
                ArrayList<HexadecimalEncoding> digitToEncoding = new ArrayList<>();
                for (int i = 0; i < 10; i++) {
                    digitToEncoding.add(new HexadecimalEncoding(""));
                }

                String[] values = br.readLine().split(" \\| ");

                ArrayList<HexadecimalEncoding> uniqueSignalPatterns = Arrays.stream(values[0].split(" ")).map(HexadecimalEncoding::new).collect(Collectors.toCollection(ArrayList::new));
                ArrayList<HexadecimalEncoding> outputDigits = Arrays.stream(values[1].split(" ")).map(HexadecimalEncoding::new).collect(Collectors.toCollection(ArrayList::new));

                for (int i = 0; i < uniqueSignalPatterns.size(); i++) {
                    HexadecimalEncoding pattern = uniqueSignalPatterns.get(i);

                    if (pattern.getNbrOfActiveSegments() == 2) {
                        digitToEncoding.set(1, pattern);
                        continue;
                    }
                    if (pattern.getNbrOfActiveSegments() == 3) {
                        digitToEncoding.set(7, pattern);
                        continue;
                    }
                    if (pattern.getNbrOfActiveSegments() == 4) {
                        digitToEncoding.set(4, pattern);
                        continue;
                    }
                    if (pattern.getNbrOfActiveSegments() == 7) {
                        digitToEncoding.set(8, pattern);
                    }
                }

                HexadecimalEncoding eg = new HexadecimalEncoding("abcdefg");
                HexadecimalEncoding cf = new HexadecimalEncoding("");
                for (int j = 0; j < digitToEncoding.get(1).getSegments().size(); j++) {
                    cf.getSegments().set(j, digitToEncoding.get(1).getSegments().get(j) - digitToEncoding.get(7).getSegments().get(j));
                }
                for (int i = 0; i < cf.getSegments().size(); i++) {
                    if (cf.getSegments().get(i) != 0) {
                        cf.setSegments(digitToEncoding.get(7).getSegments());
                        cf.getSegments().set(i, 0);
                        eg.getSegments().set(i, 0);
                        break;
                    }
                }

                HexadecimalEncoding bd = new HexadecimalEncoding("");
                bd.setSegments(digitToEncoding.get(4).getSegments());
                for (int j = 0; j < cf.getSegments().size(); j++) {
                    bd.getSegments().set(j, bd.getSegments().get(j) - cf.getSegments().get(j));
                }
                for (int j = 0; j < cf.getSegments().size(); j++) {
                    if (cf.getSegments().get(j) == 1 || bd.getSegments().get(j) == 1) {
                        eg.getSegments().set(j, 0);
                    }
                }

                // 0, 6, 9, 2, 3, 5
                for (int j = 0; j < uniqueSignalPatterns.size(); j++) {
                    HexadecimalEncoding d = uniqueSignalPatterns.get(j);

                    if (d.getNbrOfActiveSegments() == 6) {
                        // 9
                        if (!foundDigit.get(9)) {
                            boolean isValue = true;
                            for (int i = 0; i < d.getSegments().size(); i++) {
                                if ((cf.getSegments().get(i) == 1 && d.getSegments().get(i) != 1) ||
                                    (bd.getSegments().get(i) == 1 && d.getSegments().get(i) != 1)) {
                                    isValue = false;
                                    break;
                                }
                            }
                            if (isValue) {
                                digitToEncoding.set(9, d);
                                foundDigit.set(9, true);
                            }
                        }

                        // 6
                        if (!foundDigit.get(6)) {
                            boolean isValue = true;
                            for (int i = 0; i < d.getSegments().size(); i++) {
                                if ((eg.getSegments().get(i) == 1 && d.getSegments().get(i) != 1) ||
                                    (bd.getSegments().get(i) == 1 && d.getSegments().get(i) != 1)) {
                                    isValue = false;
                                    break;
                                }
                            }
                            if (isValue) {
                                digitToEncoding.set(6, d);
                                foundDigit.set(6, true);
                            }
                        }

                        // 0
                        if (!foundDigit.get(0)) {
                            boolean isValue = true;
                            for (int i = 0; i < d.getSegments().size(); i++) {
                                if ((cf.getSegments().get(i) == 1 && d.getSegments().get(i) != 1) ||
                                    (eg.getSegments().get(i) == 1 && d.getSegments().get(i) != 1)) {
                                    isValue = false;
                                    break;
                                }
                            }
                            if (isValue) {
                                digitToEncoding.set(0, d);
                                foundDigit.set(0, true);
                            }
                        }
                    }

                    if (d.getNbrOfActiveSegments() == 5) {
                        if (d.isEquals(digitToEncoding.get(9))) {
                            continue;
                        }
                        // 2
                        if (!foundDigit.get(2)) {
                            boolean isValue = true;
                            for (int i = 0; i < d.getSegments().size(); i++) {
                                if (eg.getSegments().get(i) == 1 && d.getSegments().get(i) != 1) {
                                    isValue = false;
                                    break;
                                }
                            }
                            if (isValue) {
                                digitToEncoding.set(2, d);
                                foundDigit.set(2, true);
                            }
                        }

                        // 3
                        if (!foundDigit.get(3)) {
                            boolean isValue = true;
                            for (int i = 0; i < d.getSegments().size(); i++) {
                                if (cf.getSegments().get(i) == 1 && d.getSegments().get(i) != 1) {
                                    isValue = false;
                                    break;
                                }
                            }
                            if (isValue) {
                                digitToEncoding.set(3, d);
                                foundDigit.set(3, true);
                            }
                        }

                        // 5
                        if (!foundDigit.get(5)) {
                            boolean isValue = true;
                            for (int i = 0; i < d.getSegments().size(); i++) {
                                if (bd.getSegments().get(i) == 1 && d.getSegments().get(i) != 1) {
                                    isValue = false;
                                    break;
                                }
                            }
                            if (isValue) {
                                digitToEncoding.set(5, d);
                                foundDigit.set(5, true);
                            }
                        }
                    }
                }

                String finalValue = "";
                for (HexadecimalEncoding digit : outputDigits) {
                    for (int i = 0; i < digitToEncoding.size(); i++) {
                        if (digit.isEquals(digitToEncoding.get(i))) {
                            finalValue += "" + i;
                        }
                    }
                }

//                System.out.println(Integer.parseInt(finalValue));
//                totDigits += outputDigits.stream().filter(d -> d.getNbrOfActiveSegments() == 2 || d.getNbrOfActiveSegments() == 3 || d.getNbrOfActiveSegments() == 4 || d.getNbrOfActiveSegments() == 7).count();

                digitToEncoding.forEach(HexadecimalEncoding::printEncoding);
                totDigits += Integer.parseInt(finalValue);
            }

            System.out.println("Final value : " + totDigits);

        }
    }

    private static class HexadecimalEncoding {
        List<Integer> segments;

        public HexadecimalEncoding(String input) {
            this.segments = Arrays.asList(0, 0, 0, 0, 0, 0, 0);

            for (int i = 0; i < input.length(); i++) {
                this.segments.set(input.charAt(i) - 97, 1);
            }
        }

        public boolean isEquals(HexadecimalEncoding e) {
            for (int i = 0; i < e.getSegments().size(); i++) {
                if (e.getSegments().get(i) != this.getSegments().get(i)) {
                    return false;
                }
            }

            return true;
        }

        public int getNbrOfActiveSegments() {
            return segments.stream().mapToInt(i -> i).sum();
        }

        public List<Integer> getSegments() {
            return segments;
        }

        public void setSegments(List<Integer> segments) {
            this.segments = new ArrayList<>(segments);
        }

        public void printEncoding() {
            segments.forEach(System.out::print);
            System.out.println();
        }
    }
}
