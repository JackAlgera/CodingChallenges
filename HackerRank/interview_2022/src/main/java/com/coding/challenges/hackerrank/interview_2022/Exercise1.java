package com.coding.challenges.hackerrank.interview_2022;

import java.util.List;
import java.util.stream.Collectors;

public class Exercise1 {

  public static void main(String[] args) {
    System.out.println(doubleSize(List.of(1L, 1L, 1L), 1));         // = 2
    System.out.println(doubleSize(List.of(2L, 5L, 4L, 6L, 8L), 2)); // = 16
  }

  public static long doubleSize(List<Long> arr, long b) {
    for (long v : arr.stream().sorted().collect(Collectors.toList())) {
      if (v == b) {
        b *= 2;
      }
    }

    return b;
  }
}
