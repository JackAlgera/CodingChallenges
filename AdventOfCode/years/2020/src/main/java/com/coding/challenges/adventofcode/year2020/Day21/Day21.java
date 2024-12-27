package com.coding.challenges.adventofcode.year2020.Day21;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class Day21 extends Day<String> {

  public static void main(String[] args) throws IOException {
    Day21 day = new Day21();

    day.printPart1("sample-input", "5");
    day.printPart1("input", "2826");

    day.printPart2("sample-input", "mxmxvkd,sqjhc,fvjkl");
    day.printPart2("input", "");
  }

  @Override
  public String part1(List<String> lines) {
    List<List<String>> allIngredients = new ArrayList<>();
    Map<String, Set<String>> allergensMap = getAllergensMap(lines, allIngredients);
    Set<String> ingredientsWithAllergens =
        allergensMap.values().stream()
            .filter(list -> list.size() == 1)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    return ""
        + allIngredients.stream()
            .flatMap(List::stream)
            .filter(ingredient -> !ingredientsWithAllergens.contains(ingredient))
            .count();
  }

  @Override
  public String part2(List<String> lines) {
    List<List<String>> allIngredients = new ArrayList<>();
    Map<String, Set<String>> allergensMap = getAllergensMap(lines, allIngredients);
    return allergensMap.keySet().stream()
        .sorted()
        .filter(allergen -> allergensMap.get(allergen).size() == 1)
        .map(allergensMap::get)
        .flatMap(Set::stream)
        .collect(Collectors.joining(","));
  }

  public Map<String, Set<String>> getAllergensMap(
      List<String> lines, List<List<String>> allIngredients) {
    Map<String, Set<String>> allergensMap = new HashMap<>();
    for (String line : lines) {
      Set<String> ingredients = new HashSet<>(Set.of(line.split(" \\(contains ")[0].split(" ")));
      allIngredients.add(new ArrayList<>(ingredients));
      String[] allergens = line.split(" \\(contains ")[1].replace(")", "").split(", ");

      for (String allergen : allergens) {
        if (allergensMap.containsKey(allergen)) {
          Set<String> currentIngredients = allergensMap.get(allergen);
          currentIngredients.retainAll(ingredients);
        } else {
          allergensMap.put(allergen, new HashSet<>(ingredients));
        }
      }
    }

    Set<String> handled = new HashSet<>();
    while (true) {
      boolean foundOne = false;
      for (String allergen : allergensMap.keySet()) {
        if (!handled.contains(allergen) && allergensMap.get(allergen).size() == 1) {
          for (String otherAllergen : allergensMap.keySet()) {
            if (!otherAllergen.equals(allergen)) {
              allergensMap.get(otherAllergen).removeAll(allergensMap.get(allergen));
            }
          }
          foundOne = true;
          handled.add(allergen);
        }
      }

      if (!foundOne) {
        return allergensMap;
      }
    }
  }
}
