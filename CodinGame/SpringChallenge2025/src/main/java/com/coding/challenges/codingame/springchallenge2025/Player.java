package com.coding.challenges.codingame.springchallenge2025;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
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

        // Write an action using System.out.println()
        // To debug: System.err.println("Debug messages...");

        Map<Board, Long> dp = new HashMap<>();

        System.out.println(board.getBoardHash(dp));
        System.out.println("here");
    }

    private record Board(String state, int turnsLeft) {
        private static final long MODULO_2_30 = 1L << 30;

        private static final int[][] neighbor2s = {{0, 1}, {0, 2}, {0, 3}, {1, 2}, {1, 3}, {2, 3}};
        private static final int[][] neighbor3s = {{0, 1, 2}, {0, 1, 3}, {0, 2, 3}, {1, 2, 3}};
        private static final int[][] neighbor4s = {{0, 1, 2, 3}};

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
                        for (Board nexState : nextStates(i, j)) {
                            finalBoardHash += nexState.getBoardHash(dp) % MODULO_2_30;
                        }
                    }
                }
            }

            dp.put(this, finalBoardHash);
            return finalBoardHash;
        }

        private List<Board> nextStates(int i, int j) {
            List<Board> nextStates = new ArrayList<>();
            Neighbor[] neighbors = new Neighbor[4];
            for (int k = 0; k < neighborsI.length; k++) {
                int ni = i + neighborsI[k];
                int nj = j + neighborsJ[k];
                neighbors[k] = null;
                if (ni >= 0 && ni < SIZE && nj >= 0 && nj < SIZE && !isEmptySquare(ni, nj)) {
                    neighbors[k] = new Neighbor(ni, nj, state.charAt(ni * SIZE + nj) - '0');
                }
            }

            // Only has one or fewer neighbors, add dice with 1 and don't capture neighbors
            var neighborsCount = Arrays.stream(neighbors).filter(Objects::nonNull).count();
            if (neighborsCount <= 1) {
                nextStates.add(this.withOneDiceAdded(i, j));
                return nextStates;
            }

            var atLeastOneCombination = false;
            // Has more than one neighbor, check if we can capture
            for (int k = 0; k < neighbor2s.length; k++) {
                Neighbor neighbor1 = neighbors[neighbor2s[k][0]];
                Neighbor neighbor2 = neighbors[neighbor2s[k][1]];
                if (Objects.nonNull(neighbor1) && Objects.nonNull(neighbor2) && neighbor1.val + neighbor2.val <= 6) {
                    // Capture 2
                    var nextState = "" + this.state;
                    nextState = removeDice(neighbor1, nextState);
                    nextState = removeDice(neighbor2, nextState);
                    nextState = addDice(neighbor1.val + neighbor2.val, i, j, nextState);
                    nextStates.add(new Board(nextState, turnsLeft - 1));
                    atLeastOneCombination = true;
                }
            }

            for (int k = 0; k < neighbor3s.length; k++) {
                Neighbor neighbor1 = neighbors[neighbor3s[k][0]];
                Neighbor neighbor2 = neighbors[neighbor3s[k][1]];
                Neighbor neighbor3 = neighbors[neighbor3s[k][2]];
                if (Objects.nonNull(neighbor1) && Objects.nonNull(neighbor2) && Objects.nonNull(neighbor3) && neighbor1.val + neighbor2.val + neighbor3.val <= 6) {
                    // Capture 3
                    var nextState = "" + this.state;
                    nextState = removeDice(neighbor1, nextState);
                    nextState = removeDice(neighbor2, nextState);
                    nextState = removeDice(neighbor3, nextState);
                    nextState = addDice(neighbor1.val + neighbor2.val + neighbor3.val, i, j, nextState);
                    nextStates.add(new Board(nextState, turnsLeft - 1));
                    atLeastOneCombination = true;
                }
            }

            for (int k = 0; k < neighbor4s.length; k++) {
                Neighbor neighbor1 = neighbors[neighbor4s[k][0]];
                Neighbor neighbor2 = neighbors[neighbor4s[k][1]];
                Neighbor neighbor3 = neighbors[neighbor4s[k][2]];
                Neighbor neighbor4 = neighbors[neighbor4s[k][3]];
                if (Objects.nonNull(neighbor1) && Objects.nonNull(neighbor2) && Objects.nonNull(neighbor3) && Objects.nonNull(neighbor4) && neighbor1.val + neighbor2.val + neighbor3.val + neighbor4.val <= 6) {
                    // Capture 4
                    var nextState = "" + this.state;
                    nextState = removeDice(neighbor1, nextState);
                    nextState = removeDice(neighbor2, nextState);
                    nextState = removeDice(neighbor3, nextState);
                    nextState = removeDice(neighbor4, nextState);
                    nextState = addDice(neighbor1.val + neighbor2.val + neighbor3.val + neighbor4.val, i, j, nextState);
                    nextStates.add(new Board(nextState, turnsLeft - 1));
                    atLeastOneCombination = true;
                }
            }

            // If no combination was found, add dice with 1
            if (!atLeastOneCombination) {
                nextStates.add(this.withOneDiceAdded(i, j));
            }

            return nextStates;
        }

        private Board withOneDiceAdded(int i, int j) {
            return new Board(state.substring(0, i * SIZE + j) + "1" + state.substring(i * SIZE + j + 1), turnsLeft - 1);
        }

        private String addDice(int val, int i, int j, String state) {
            return state.substring(0, i * SIZE + j) + val + state.substring(i * SIZE + j + 1);
        }

        private String removeDice(Neighbor neighbor, String state) {
            return state.substring(0, neighbor.i * SIZE + neighbor.j) + "0" + state.substring(neighbor.i * SIZE + neighbor.j + 1);
        }

        private boolean isBoardFull() {
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    if (isEmptySquare(i, j)) {
                        return false;
                    }
                }
            }
            return true;
        }

        private boolean isEmptySquare(int i, int j) {
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

    private record Neighbor(int i, int j, int val) {
    }
}