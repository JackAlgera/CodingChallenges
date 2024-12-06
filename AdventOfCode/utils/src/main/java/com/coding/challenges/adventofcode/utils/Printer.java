package com.coding.challenges.adventofcode.utils;

import java.util.List;
import lombok.experimental.UtilityClass;

@UtilityClass
public class Printer {
  public static void print(List<String> lines) {
    System.out.println();
    for (String line : lines) {
      System.out.println(line);
    }
    System.out.println();
  }
}
