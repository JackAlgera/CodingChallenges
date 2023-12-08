package adventofcode.Year2020.Day5;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.List;

public class Day5 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day5 day = new Day5();

        day.printPart1("sample-input", 820L);
        day.printPart1("input", 871L);

        day.printPart2("sample-input", 120L);
        day.printPart2("input", 640L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return lines.stream()
            .map(this::getSeatId)
            .max(Long::compareTo)
            .get();
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        List<Long> ids = lines.stream()
            .map(this::getSeatId)
            .sorted()
            .toList();

        long current = ids.get(0);
        for (int i = 1; i < lines.size(); i++) {
            if (!ids.get(i).equals(current + 1)) {
                return current + 1;
            }
            current++;
        }

        return 0L;
    }

    public long getSeatId(String seat) {
        int row = getMid(127, seat.substring(0, 7), 'F');
        int column = getMid(7, seat.substring(7, 10), 'L');
        return (long) row * 8 + column;
    }

    public int getMid(int r, String chars, char check) {
        int left = 0;
        int right = r;
        for (int i = 0; i < chars.length(); i++) {
            if (chars.charAt(i) == check) {
                right = (left + right) / 2;
            } else {
                left = (left + right) / 2;
            }
        }
        return right;
    }
}
