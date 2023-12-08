package adventofcode.Year{{ year }}.Day{{ day }};

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.List;

public class Day{{ day }} extends Day<{{ type }}> {

    public static void main(String[] args) throws IOException {
        Day{{ day }} day = new Day{{ day }}();

        day.printPart1("sample-input", {{ default_value }});
        day.printPart1("input", {{ default_value }});

        day.printPart2("sample-input", {{ default_value }});
        day.printPart2("input", {{ default_value }});
    }

    @Override
    public {{ type }} part1(List<String> lines) throws IOException {
        return {{ default_value }};
    }

    @Override
    public {{ type }} part2(List<String> lines) throws IOException {
        return {{ default_value }};
    }
}
