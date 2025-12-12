package com.coding.challenges.adventofcode.year2025.Day10;

import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.regex.Pattern;
import com.coding.challenges.adventofcode.utils.Day;

public class Day10 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day10 day = new Day10();

    day.printPart1("sample-input", 7L);
    day.printPart1("input", 514L);

    day.printPart2("sample-input", 33L);
    day.printPart2("input", 21824L);
  }

  @Override
  public Long part1(List<String> lines) {
    long count = 0L;

    for (String line : lines) {
      var input = Input.parse(line);
      count += input.findBestLightsCombination();
    }

    return count;
  }

  @Override
  public Long part2(List<String> lines) {
    long count = 0L;

    for (String line : lines) {
      var input = Input.parse(line);
      count += input.findBestJoltagesCombinationAStar();
    }

    return count;
  }

  record Input(String targetLights, List<Integer[]> buttons, List<Integer> targetJoltages) {
    private static final String regex = "^\\[([^\\]]+)\\]\\s+(.*?)\\s+\\{([^}]+)\\}$";

    public long findBestJoltagesCombinationAStar() {
      var equationCount = targetJoltages.size();
      var variablesCount = buttons.size();

      var matrix = new double[equationCount][variablesCount + 1];

      for (int i = 0; i < variablesCount; i++) {
        for (int j : buttons.get(i)) {
          if (j < equationCount) {
            matrix[j][i] = 1.0;
          }
        }
      }
      for (int i = 0; i < equationCount; i++) {
        matrix[i][variablesCount] = targetJoltages.get(i);
      }

      var pivotRow = 0;
      var pivotCols = new ArrayList<Integer>();

      for (int col = 0; col < variablesCount && pivotRow < equationCount; col++) {
        int sel = pivotRow;
        for (int i = pivotRow + 1; i < equationCount; i++) {
          if (Math.abs(matrix[i][col]) > Math.abs(matrix[sel][col])) {
            sel = i;
          }
        }

        if (Math.abs(matrix[sel][col]) < 1e-9) continue;

        var tmp = matrix[pivotRow];
        matrix[pivotRow] = matrix[sel]; matrix[sel] = tmp;
        var pivotVal = matrix[pivotRow][col];
        for (int j = col; j <= variablesCount; j++) matrix[pivotRow][j] /= pivotVal;

        for (int i = 0; i < equationCount; i++) {
          if (i != pivotRow) {
            double factor = matrix[i][col];
            for (int j = col; j <= variablesCount; j++) matrix[i][j] -= factor * matrix[pivotRow][j];
          }
        }

        pivotCols.add(col);
        pivotRow++;
      }

      var freeVars = new ArrayList<Integer>();
      for (int i = 0; i < variablesCount; i++) {
        if (!pivotCols.contains(i)) freeVars.add(i);
      }

      return solveFreeVars(matrix, freeVars, pivotCols, 0, new double[variablesCount]);
    }

    private long solveFreeVars(double[][] matrix, List<Integer> freeVariables, List<Integer> pivotColumns, int idx, double[] solution) {
      if (idx == freeVariables.size()) {
        long currentPresses = 0;

        for (int v : freeVariables) currentPresses += (long) solution[v];

        int pivotRow = 0;
        for (int pc : pivotColumns) {
          double val = matrix[pivotRow][matrix[0].length - 1]; // Constant

          for (int fv : freeVariables) {
            val -= matrix[pivotRow][fv] * solution[fv];
          }

          if (val < -1e-9 || Math.abs(val - Math.round(val)) > 1e-9) {
            return Long.MAX_VALUE;
          }

          solution[pc] = Math.round(val);
          currentPresses += (long)solution[pc];
          pivotRow++;
        }
        return currentPresses;
      }

      int varIdx = freeVariables.get(idx);
      long minTotal = Long.MAX_VALUE;

      int limit = 500;

      for (int val = 0; val <= limit; val++) {
        solution[varIdx] = val;
        long res = solveFreeVars(matrix, freeVariables, pivotColumns, idx + 1, solution);
        if (res != Long.MAX_VALUE) {
          minTotal = Math.min(minTotal, res);
        }
      }
      return minTotal;
    }

    public int findBestLightsCombination() {
      var queue = new ArrayDeque<LightsState>();
      var visited = new HashSet<>();

      queue.add(new LightsState(allLightsOff(), 0));
      visited.add(allLightsOff());

      while (!queue.isEmpty()) {
        var current = queue.poll();

        if (current.state().equals(targetLights)) {
          return current.steps;
        }

        for (Integer[] button : buttons) {
          var nextLights = applyButton(current.state, button);

          if (!visited.contains(nextLights)) {
            visited.add(nextLights);
            queue.add(new LightsState(nextLights, current.steps + 1));
          }
        }
      }

      return -1; // Target unreachable
    }

    private String applyButton(String current, Integer[] button) {
      StringBuilder sb = new StringBuilder(current);
      for (int i : button) {
        char c = sb.charAt(i);
        sb.setCharAt(i, c == '.' ? '#' : '.');
      }
      return sb.toString();
    }

    private String allLightsOff() {
      return ".".repeat(targetLights.length());
    }

    public static Input parse(String line) {
      var matcher = Pattern.compile(regex).matcher(line);
      matcher.find();
      var buttons =
          Arrays.stream(matcher.group(2).split(" "))
              .map(
                  b ->
                      Arrays.stream(b.substring(1, b.length() - 1).split(","))
                          .map(Integer::parseInt)
                          .toArray(Integer[]::new))
              .toList();
      var joltages =
          Arrays.stream(matcher.group(3).split(",")).mapToInt(Integer::parseInt).boxed().toList();

      return new Input(matcher.group(1), buttons, joltages);
    }

    record LightsState(String state, int steps) {}
  }
}
