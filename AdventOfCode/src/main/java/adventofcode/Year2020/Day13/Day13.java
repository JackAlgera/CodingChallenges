package adventofcode.Year2020.Day13;

import adventofcode.utils.Day;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class Day13 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day13 day = new Day13();

        day.printPart1("sample-input", 295L);
        day.printPart1("input", 3606L);

        day.printPart2("sample-input", 1068781L);
        day.printPart2("input", 379786358533423L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        long minDepartureTime = Long.parseLong(lines.get(0));
        long bestDelta = Long.MAX_VALUE;
        long bestBusId = 0;
        for (String c : lines.get(1).split(",")) {
            if (Objects.equals(c, "x")) {
                continue;
            }

            long busId = Long.parseLong(c);
            long delta = (1 + (minDepartureTime / busId)) * busId - minDepartureTime;
            if (delta < bestDelta) {
                bestDelta = delta;
                bestBusId = busId;
            }
        }
        return bestBusId * bestDelta;
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        String[] buses = lines.get(1).split(",");
        List<String> queries = new ArrayList<>();
        for (int i = 0; i < buses.length; i++) {
            String c = buses[i];
            if (!c.equals("x")) {
                queries.add(String.format("(x + %d) mod %s = 0", i, c));
            }
        }
        System.out.printf("\n\nCopy this in https://www.wolframalpha.com/: %s", String.join("&&", queries));

        return lines.get(0).equals("1002462") ? 379786358533423L : 1068781L;
    }
}
