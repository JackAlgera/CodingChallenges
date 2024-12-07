package com.coding.challenges.adventofcode.year2023.Day7;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class Day7 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day7 day = new Day7();

    day.printPart1("sample-input", 6440L);
    day.printPart1("input", 253603890L);

    day.printPart2("sample-input", 5905L);
    day.printPart2("input", 253630098L);
  }

  @Override
  public Long part1(List<String> lines) {
    return getTotalWinnings(lines, false);
  }

  @Override
  public Long part2(List<String> lines) {
    return getTotalWinnings(lines, true);
  }

  public Long getTotalWinnings(List<String> lines, boolean useJokers) {
    List<List<Hand>> handTypes = new ArrayList<>();
    for (int i = 0; i < CardType.values().length; i++) {
      handTypes.add(new ArrayList<>());
    }

    lines.stream()
        .map(line -> line.split(" "))
        .map(
            hand -> new Hand(hand[0], Long.parseLong(hand[1]), getHandStrength(hand[0], useJokers)))
        .forEach(hand -> handTypes.get(hand.cardType().ordinal()).add(hand));

    List<Hand> handsList =
        handTypes.stream()
            .flatMap(hands -> hands.stream().sorted((h1, h2) -> h1.compare(h2, useJokers)))
            .toList();

    return IntStream.range(0, handsList.size())
        .mapToLong(i -> (i + 1) * handsList.get(i).bid())
        .sum();
  }

  public CardType getHandStrength(String cards, boolean useJokers) {
    int jokerCount = useJokers ? (int) cards.chars().filter(c -> c == 'J').count() : 0;

    if (cards.equals("JJJJJ")) {
      return CardType.FIVE_OF_A_KIND;
    }

    Map<String, Integer> counts = new HashMap<>();
    cards
        .chars()
        .forEach(
            card -> {
              if (useJokers && card == 'J') {
                return;
              }
              counts.compute("" + card, (k, v) -> v == null ? 1 : v + 1);
            });

    int max = -10;
    String key = "";
    for (String k : counts.keySet()) {
      if (counts.get(k) > max) {
        max = counts.get(k);
        key = k;
      }
    }
    counts.put(key, counts.get(key) + jokerCount);

    if (counts.containsValue(5)) {
      return CardType.FIVE_OF_A_KIND;
    }
    if (counts.containsValue(4)) {
      return CardType.FOUR_OF_A_KIND;
    }
    if (counts.containsValue(3) && counts.containsValue(2)) {
      return CardType.FULL_HOUSE;
    }
    if (counts.containsValue(3)) {
      return CardType.THREE_OF_A_KIND;
    }
    if (counts.values().stream().filter(count -> count == 2).count() >= 2) {
      return CardType.TWO_PAIRS;
    }
    if (counts.containsValue(2)) {
      return CardType.ONE_PAIR;
    }
    return CardType.HIGH_CARD;
  }

  public record Hand(String cards, long bid, CardType cardType) {
    private int getCardStrength(int index, boolean useJokers) {
      return switch (cards.charAt(index)) {
        case 'A' -> 14;
        case 'K' -> 13;
        case 'Q' -> 12;
        case 'J' -> useJokers ? 0 : 11;
        case 'T' -> 10;
        default -> Integer.parseInt("" + cards.charAt(index));
      };
    }

    public int compare(Hand other, boolean useJokers) {
      for (int i = 0; i < cards.length(); i++) {
        if (getCardStrength(i, useJokers) > other.getCardStrength(i, useJokers)) {
          return 1;
        }
        if (getCardStrength(i, useJokers) < other.getCardStrength(i, useJokers)) {
          return -1;
        }
      }
      return 0;
    }
  }

  public enum CardType {
    HIGH_CARD,
    ONE_PAIR,
    TWO_PAIRS,
    THREE_OF_A_KIND,
    FULL_HOUSE,
    FOUR_OF_A_KIND,
    FIVE_OF_A_KIND
  }
}
