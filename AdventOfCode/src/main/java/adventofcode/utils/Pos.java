package adventofcode.utils;

import adventofcode.utils.enums.Direction;

public record Pos(int i, int j) {
    public Pos move(Direction direction) {
        return switch (direction) {
            case N -> new Pos(i - 1, j);
            case E -> new Pos(i, j + 1);
            case S -> new Pos(i + 1, j);
            case W -> new Pos(i, j - 1);
        };
    }
}
