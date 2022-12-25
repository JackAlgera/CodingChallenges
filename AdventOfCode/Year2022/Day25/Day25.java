package Year2022.Day25;

import utils.Day;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

public class Day25 extends Day<String> {

    public static void main(String[] args) throws IOException {
        Day25 day = new Day25();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        day.printAllResults(1, day.getName(),
            day.part1(sampleInput), "2=-1=0",
            day.part1(mainInput), "2=0=02-0----2-=02-10");

//        printAllResults(2, day.getName(),
//            day.part2(sampleInput), 20,
//            day.part2(mainInput), 960);
    }

    @Override
    public String part1(List<String> lines) throws IOException {
        long decimal = lines.stream().mapToLong(this::snafuToDecimal).sum();

        return decimalToSnafu(BigDecimal.valueOf(decimal));
    }

    @Override
    public String part2(List<String> lines) throws IOException {
        return "";
    }

    public long snafuToDecimal(String line) {
        BigDecimal total = new BigDecimal(0);
        int power = 0;
        for (int i = line.length() - 1; i >= 0; i--) {
            int factor = switch (line.charAt(i)) {
                case '2' -> 2;
                case '1' -> 1;
                case '0' -> 0;
                case '-' -> -1;
                case '=' -> -2;
                default -> throw new IllegalStateException("Unexpected value: " + line.charAt(i));
            };
            total = total.add(BigDecimal.valueOf(Math.pow(5, power)).multiply(BigDecimal.valueOf(factor)));
            power++;
        }
        return total.longValue();
    }

    public String decimalToSnafu(BigDecimal decimal) {
        String val = "";
        while (decimal.longValue() > 0) {
            BigDecimal[] qr = decimal.divideAndRemainder(BigDecimal.valueOf(5));
            decimal = qr[0];
            long remainder = qr[1].longValue();
            if (remainder == 0 || remainder == 1 || remainder == 2) {
                val = "" + remainder + val;
                continue;
            }
            if (remainder == 3) {
                decimal = decimal.add(BigDecimal.ONE);
                val = "=" + val;
                continue;
            }
            if (remainder == 4) {
                decimal = decimal.add(BigDecimal.ONE);
                val = "-" + val;
            }
        }
        return val;
    }
}
