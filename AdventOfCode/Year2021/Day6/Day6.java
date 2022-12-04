package Year2021.Day6;

import utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class Day6 {

    private static final String INPUT_NAME = "AdventOfCode/Year2021/Day6/input.txt";

    public static void main(String[] args) throws IOException {
        new Day6Code().run();
    }

    private static class Day6Code {
        public void run() throws IOException {
            System.out.println("-------- Day 6 --------");

            BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

            ArrayList<Long> fish = Arrays.stream(br.readLine().split(",")).map(Long::parseLong).collect(Collectors.toCollection(ArrayList::new));
            ArrayList<Input> fishInput = new ArrayList<>();

            int i = 0;

            while (i < fish.size()) {
                Long currentUnit = fish.get(i);
                Long nbrOccurrences = 1L;
                int j = i;

                while (j + 1 < fish.size() && fish.get(j + 1) == currentUnit) {
                    nbrOccurrences++;
                    j++;
                }

                fishInput.add(new Input(currentUnit, nbrOccurrences));

                i = j + 1;
            }

            for (int day = 1; day <= 256; day++) {
                AtomicLong nbrNewFish = new AtomicLong();
                fishInput.forEach(f -> {
                    if (f.getVal() == 0) {
                        f.setVal(6L);
                        nbrNewFish.set(nbrNewFish.get() + f.nbrOccurrences);
                    } else {
                        f.setVal(f.getVal() - 1);
                    }
                });

                if (nbrNewFish.get() > 0) {
                    fishInput.add(new Input(8L, nbrNewFish.get()));
                }
                System.out.printf("Fish count after %d days : %d%n", day, getTotalFish(fishInput));
            }


            System.out.printf("Final fish count : %d%n", getTotalFish(fishInput));
        }

        private Long getTotalFish(ArrayList<Input> fishInput) {
            return fishInput.stream().map(Input::getNbrOccurrences).mapToLong(Long::longValue).sum();
        }
    }

    private static class Input {
        private Long val, nbrOccurrences;

        public Input(Long val, Long nbrOccurrences) {
            this.val = val;
            this.nbrOccurrences = nbrOccurrences;
        }

        public Long getVal() {
            return val;
        }

        public void setVal(Long val) {
            this.val = val;
        }

        public Long getNbrOccurrences() {
            return nbrOccurrences;
        }

        public void setNbrOccurrences(Long nbrOccurrences) {
            this.nbrOccurrences = nbrOccurrences;
        }

        @Override
        public String toString() {
            return String.format("(%d, %d)", val, nbrOccurrences);
        }
    }
}
