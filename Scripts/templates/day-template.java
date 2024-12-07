package com.coding.challenges.adventofcode.year{{ year }}.Day{{ day }};

import java.io.IOException;
import java.util.List;
import com.coding.challenges.adventofcode.utils.Day;

public class Day{{ day }} extends Day<{{ type }}> {

    public static void main(String[] args) throws IOException {
        Day{{ day }} day = new Day{{ day }}();

        day.printPart1("sample-input", {{ default_value }});
        day.printPart1("input", {{ default_value }});

        day.printPart2("sample-input", {{ default_value }});
        day.printPart2("input", {{ default_value }});
    }

    @Override
    public {{ type }} part1(List<String> lines) {
        return {{ default_value }};
    }

    @Override
    public {{ type }} part2(List<String> lines) {
        return {{ default_value }};
    }
}
