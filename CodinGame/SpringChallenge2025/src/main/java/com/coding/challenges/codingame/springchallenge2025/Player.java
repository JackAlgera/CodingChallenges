package com.coding.challenges.codingame.springchallenge2025;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;
import java.util.stream.Stream;

class Player {

  public static void main(String args[]) {
    Scanner in = new Scanner(System.in);
    int depth = in.nextInt();
    String initialState = "";
    for (int i = 0; i < 3; i++) {
      for (int j = 0; j < 3; j++) {
        initialState += "" + in.nextInt();
      }
    }

    Board board = new Board(initialState, depth);
    Map<Board, Long> dp = new HashMap<>();

    System.out.println(board.getBoardHash(dp));
  }

  public record Board(String state, int turnsLeft) {
    private static final long MODULO_2_30 = 1L << 30;

    public static final int[][] neighbor2s = {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}};
    public static final int[][] neighbor3s = {{0, 1, 2}, {0, 1, 3}, {0, 2, 3}, {1, 2, 3}};
    public static final int[][] neighbor4s = {{0, 1, 2, 3}};

    private static final int[] neighborsI = {-1, 0, 1, 0};
    private static final int[] neighborsJ = {0, 1, 0, -1};
    private static final int SIZE = 3;

    public long getBoardHash(Map<Board, Long> dp) {
      if (dp.containsKey(this)) {
        return dp.get(this);
      }

      if (isBoardFull() || turnsLeft == 0) {
        var boardHash = Long.parseLong(state);
        dp.put(this, boardHash);
        return boardHash;
      }

      long finalBoardHash = 0;
      for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
          if (isEmptySquare(i, j)) {
            for (Board nexState : checkNextStates(i, j)) {
              finalBoardHash = (finalBoardHash + nexState.getBoardHash(dp)) % MODULO_2_30;
            }
          }
        }
      }

      dp.put(this, finalBoardHash);
      return finalBoardHash;
    }

    private List<Board> checkNextStates(int i, int j) {
      List<Board> nextStates = new ArrayList<>();
      Map<Integer, Neighbor> neighborsMap = new HashMap<>();
      for (int k = 0; k < neighborsI.length; k++) {
        int ni = i + neighborsI[k];
        int nj = j + neighborsJ[k];
        if (ni >= 0 && ni < SIZE && nj >= 0 && nj < SIZE && !isEmptySquare(ni, nj)) {
          neighborsMap.put(k, new Neighbor(ni, nj, state.charAt(ni * SIZE + nj) - '0'));
        }
      }

      var atLeastOneCombination =
          Stream.of(
                        addNextStates(i, j, neighbor2s, neighborsMap, nextStates),
                        addNextStates(i, j, neighbor3s, neighborsMap, nextStates),
                        addNextStates(i, j, neighbor4s, neighborsMap, nextStates))
              .anyMatch(Boolean::booleanValue);

      // If no combination was found, add dice with 1
      if (!atLeastOneCombination) {
        nextStates.add(new Board(StateUtils.addDice(1, i, j, state), turnsLeft - 1));
      }

      return nextStates;
    }

    public boolean addNextStates(
        int i,
        int j,
        int[][] globalNeighborIds,
        Map<Integer, Neighbor> neighborsMap,
        List<Board> nextStates) {
      var atLeastOneCombination = false;
      for (int[] neighborIds : globalNeighborIds) {
        // Get all neighbors
        var neighbors =
            Arrays.stream(neighborIds)
                .mapToObj(neighborId -> neighborsMap.getOrDefault(neighborId, null))
                .toList();

        // Check if there is an available next state
        if (!isNextStateAvailable(neighbors)) {
          continue;
        }

        // Add the next state to the list of new states
        nextStates.add(getNextState(i, j, neighbors));
        atLeastOneCombination = true;
      }

      return atLeastOneCombination;
    }

    public boolean isNextStateAvailable(List<Neighbor> neighbors) {
      // If any neighbor is null, skip this combination
      if (neighbors.stream().anyMatch(Objects::isNull)) {
        return false;
      }

      int sum = getSum(neighbors);
      if (sum > 6) {
        return false;
      }

      return true;
    }

    public Board getNextState(int i, int j, List<Neighbor> neighbors) {
      var newState = "" + this.state;
      for (Neighbor neighbor : neighbors) {
        newState = StateUtils.removeDice(neighbor, newState);
      }
      int sum = getSum(neighbors);
      newState = StateUtils.addDice(sum, i, j, newState);
      return new Board(newState, turnsLeft - 1);
    }

    public int getSum(List<Neighbor> neighbors) {
      return neighbors.stream().mapToInt(Neighbor::val).sum();
    }

    public boolean isBoardFull() {
      for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
          if (isEmptySquare(i, j)) {
            return false;
          }
        }
      }
      return true;
    }

    public boolean isEmptySquare(int i, int j) {
      return state.charAt(i * SIZE + j) == '0';
    }

    public void print() {
      System.out.println("Turns left: " + turnsLeft);
      for (int i = 0; i < SIZE; i++) {
        for (int j = 0; j < SIZE; j++) {
          System.out.print(state.charAt(i * SIZE + j) + " ");
        }
        System.out.println();
      }
    }
  }

  public record Neighbor(int i, int j, int val) {}

  public static class StateUtils {
    public static String removeDice(Neighbor neighbor, String state) {
      return state.substring(0, neighbor.i * Board.SIZE + neighbor.j)
          + "0"
          + state.substring(neighbor.i * Board.SIZE + neighbor.j + 1);
    }

    public static String addDice(int val, int i, int j, String state) {
      return state.substring(0, i * Board.SIZE + j) + val + state.substring(i * Board.SIZE + j + 1);
    }
  }
}
