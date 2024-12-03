package com.coding.challenges.adventofcode.utils.enums;

public enum Direction {
    N, E, S, W;

    public Direction rotateLeft(int turns) {
        int totalDirections = Direction.values().length;
        return Direction.values()[(totalDirections + this.ordinal() - turns) % totalDirections];
    }

    public Direction rotateRight(int turns) {
        int totalDirections = Direction.values().length;
        return Direction.values()[(totalDirections + this.ordinal() + turns) % totalDirections];
    }

    public boolean isOpposite(Direction direction) {
        return this.rotateLeft(2) == direction;
    }

    public static Direction fromOrientation(String s) {
        return switch (s) {
            case "U" -> N;
            case "R" -> E;
            case "D" -> S;
            case "L" -> W;
            default -> throw new IllegalArgumentException("Unknown orientation: " + s);
        };
    }

    public static Direction fromDigit(int d) {
        return switch (d) {
            case 0 -> E;
            case 1 -> S;
            case 2 -> W;
            case 3 -> N;
            default -> throw new IllegalArgumentException("Unknown digit direction: " + d);
        };
    }
}
