package com.coding.challenges.adventofcode.utils;

import com.coding.challenges.adventofcode.utils.enums.Direction;

public record Pos(int i, int j) {
    public Pos move(Direction direction) {
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
}
