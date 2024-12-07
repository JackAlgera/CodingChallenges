package com.coding.challenges.adventofcode.utils;

public record Vector(int x, int y) {

  @Override
  public String toString() {
    return "(" + x + ", " + y + ")";
  }
}
