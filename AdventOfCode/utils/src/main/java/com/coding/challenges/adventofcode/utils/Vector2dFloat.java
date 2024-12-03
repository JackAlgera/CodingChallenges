package com.coding.challenges.adventofcode.utils;

public record Vector2dFloat(float x, float y) {
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
}
