package com.coding.challenges.adventofcode.utils;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Pair <T> {
    private T first, second;

    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }
}
