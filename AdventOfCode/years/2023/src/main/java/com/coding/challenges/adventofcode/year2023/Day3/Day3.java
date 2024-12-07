package com.coding.challenges.adventofcode.year2023.Day3;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.List;

public class Day3 extends Day<Integer> {

  private final int[] neighborsI = {-1, -1, -1, 0, 0, 1, 1, 1};
  private final int[] neighborsJ = {-1, 0, 1, -1, 1, -1, 0, 1};

  public static void main(String[] args) throws IOException {
    Day3 day = new Day3();

    day.printPart1("sample-input", 4361);
    day.printPart1("input", 520135);

    day.printPart2("sample-input", 467835);
    day.printPart2("input", 72514855);
  }

  @Override
  public Integer part1(List<String> lines) {
    int iMax = lines.size();
    int jMax = lines.get(0).length();

    int total = 0;

    for (int i = 0; i < lines.size(); i++) {
      for (int j = 0; j < lines.get(0).length(); j++) {
        if (Character.isDigit(lines.get(i).charAt(j))) {
          for (int k = 0; k < neighborsI.length; k++) {
            int newI = i + neighborsI[k];
            int newJ = j + neighborsJ[k];
            if (!isValidPoint(newI, newJ, iMax, jMax)) {
              continue;
            }

            char c = lines.get(newI).charAt(newJ);
            if (!Character.isDigit(c) && c != '.') {
              total += findDigit(lines.get(i), j);
              while (j < jMax && Character.isDigit(lines.get(i).charAt(j))) {
                j++;
              }
              break;
            }
          }
        }
      }
    }

    return total;
  }

  @Override
  public Integer part2(List<String> lines) {
    boolean[][] added = new boolean[lines.size()][lines.get(0).length()];
    int iMax = lines.size();
    int jMax = lines.get(0).length();

    int total = 0;

    for (int i = 0; i < added.length; i++) {
      for (int j = 0; j < added[0].length; j++) {
        if (lines.get(i).charAt(j) == '*') {
          int totalNeighbors = 0;
          int val = 1;

          for (int k = 0; k < neighborsI.length; k++) {
            int newI = i + neighborsI[k];
            int newJ = j + neighborsJ[k];
            if (isValidPoint(newI, newJ, iMax, jMax)
                && !added[newI][newJ]
                && Character.isDigit(lines.get(newI).charAt(newJ))) {
              totalNeighbors++;
              val *= findDigitWithChecked(lines.get(newI), newI, newJ, added);
            }
          }

          if (totalNeighbors == 2) {
            total += val;
          }
        }
      }
    }

    return total;
  }

  public boolean isValidPoint(int i, int j, int iMax, int jMax) {
    return i >= 0 && i < iMax && j >= 0 && j < jMax;
  }

  public int findDigit(String s, int j) {
    int left = j;
    int right = j;
    while (left >= 0 && Character.isDigit(s.charAt(left))) {
      left--;
    }
    while (right < s.length() && Character.isDigit(s.charAt(right))) {
      right++;
    }
    return Integer.parseInt(s.substring(left + 1, right));
  }

  public int findDigitWithChecked(String s, int i, int j, boolean[][] checked) {
    int left = j;
    int right = j;
    checked[i][j] = true;
    while (left >= 0 && Character.isDigit(s.charAt(left))) {
      checked[i][left] = true;
      left--;
    }
    while (right < s.length() && Character.isDigit(s.charAt(right))) {
      checked[i][right] = true;
      right++;
    }
    return Integer.parseInt(s.substring(left + 1, right));
  }
}
