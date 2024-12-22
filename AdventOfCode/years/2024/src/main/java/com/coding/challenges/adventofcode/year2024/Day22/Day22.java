package com.coding.challenges.adventofcode.year2024.Day22;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Day22 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day22 day = new Day22();

    day.printPart1("sample-input-1", 37327623L);
    day.printPart1("sample-input-2", 37990510L);
    day.printPart1("input", 17005483322L);

    day.printPart2("sample-input-1", 24L);
    day.printPart2("sample-input-2", 23L);
    day.printPart2("input", 1910L);
  }

  @Override
  public Long part1(List<String> lines) {
    return lines.stream().map(Long::parseLong).mapToLong(secret -> nextSecret(secret, 2000)).sum();
  }

  @Override
  public Long part2(List<String> lines) {
    Map<Key, Long> finalMap = new HashMap<>();

    for (String secretStr : lines) {
      long secret = Long.parseLong(secretStr);
      Map<Key, Integer> priceForInputMap = generateResponseMap(secret);
      priceForInputMap.forEach(
          (key, value) -> finalMap.compute(key, (k, v) -> v == null ? value : v + value));
    }

    return finalMap.values().stream().mapToLong(Long::longValue).max().orElse(0);
  }

  private Map<Key, Integer> generateResponseMap(long secret) {
    Map<Key, Integer> priceForInput = new HashMap<>();

    for (int i = 0; i < 2000; i++) {
      long s1 = nextSecret(secret);
      long s2 = nextSecret(s1);
      long s3 = nextSecret(s2);
      long s4 = nextSecret(s3);
      int d1 = lastDigit(s1) - lastDigit(secret);
      int d2 = lastDigit(s2) - lastDigit(s1);
      int d3 = lastDigit(s3) - lastDigit(s2);
      int d4 = lastDigit(s4) - lastDigit(s3);
      Key key = new Key(d1, d2, d3, d4);
      int price = lastDigit(s4);
      if (!priceForInput.containsKey(key)) {
        priceForInput.put(key, price);
      }
      secret = s1;
    }

    return priceForInput;
  }

  private long nextSecret(long secret, int steps) {
    for (int i = 0; i < steps; i++) {
      secret = nextSecret(secret);
    }
    return secret;
  }

  private int lastDigit(long secret) {
    return (int) (secret % 10);
  }

  private long nextSecret(long secret) {
    secret = mix(secret << 6, secret) % 16777216;
    secret = mix(secret >> 5, secret) % 16777216;
    secret = mix(secret << 11, secret) % 16777216;
    return secret;
  }

  private long mix(long val, long secret) {
    return val ^ secret;
  }

  private record Key(int d1, int d2, int d3, int d4) {}
}
