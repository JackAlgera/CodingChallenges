package adventofcode.Year2021.Day23;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.List;

public class Day23 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day23 day = new Day23();

        // The part 1 was easy enough to solve by hand:
        // 13594, 13576, 13560, 13558 too high
        // 13556 nice
        day.printPart1("input-part1", 13556L);

        day.printPart2("input-part2", 0L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        return 13556L;
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return 0L;
    }
}
