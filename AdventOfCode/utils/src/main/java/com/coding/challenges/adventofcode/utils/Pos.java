package com.coding.challenges.adventofcode.utils;

import java.util.List;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import com.coding.challenges.adventofcode.utils.enums.Direction8;

public record Pos(int i, int j) {
    public Pos move(Direction direction) {
        return switch (direction) {
            case N -> new Pos(i - 1, j);
            case E -> new Pos(i, j + 1);
            case S -> new Pos(i + 1, j);
            case W -> new Pos(i, j - 1);
        };
    }

    public Pos move(Direction8 direction) {
        return switch (direction) {
            case N -> new Pos(i - 1, j);
            case E -> new Pos(i, j + 1);
            case S -> new Pos(i + 1, j);
            case W -> new Pos(i, j - 1);
            case NW -> new Pos(i - 1, j - 1);
            case NE -> new Pos(i - 1, j + 1);
            case SW -> new Pos(i + 1, j - 1);
            case SE -> new Pos(i + 1, j + 1);
        };
    }

    public boolean isValid(int maxI, int maxJ) {
        return i >= 0 && i < maxI && j >= 0 && j < maxJ;
    }

    public boolean isValid(List<String> lines) {
        return i >= 0 && i < lines.size() && j >= 0 && j < lines.get(0).length();
    }
}
