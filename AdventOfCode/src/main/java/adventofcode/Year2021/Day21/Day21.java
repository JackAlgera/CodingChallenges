package adventofcode.Year2021.Day21;

import adventofcode.utils.Pair;
import adventofcode.utils.Utilities;

import java.io.BufferedReader;
import java.io.IOException;

public class Day21 {

    private static Long gamesChecked = 0L;
    private static final Long[] nbrPossibleDiceThrowsOccurrences = { 0L, 0L, 0L, 0L, 0L, 0L, 0L };
    private static final String INPUT_NAME = "Year2021/Day21/input.txt";

    public static void main(String[] args) throws IOException {
        System.out.println("-------- Day 21 --------");

        BufferedReader br = Utilities.getBufferedReader(INPUT_NAME);

        int player1Position = Integer.parseInt(br.readLine().split(": ")[1]);
        int player2Position = Integer.parseInt(br.readLine().split(": ")[1]);

        System.out.println(player1Position + " - " + player2Position);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                for (int k = 0; k < 3; k++) {
                    nbrPossibleDiceThrowsOccurrences[i + j + k]++;
                }
            }
        }

        Player p1 = new Player(0, player1Position);
        Player p2 = new Player(0, player2Position);

        Pair<Long> wins = checkGame(p1, p2, true, 1);

        System.out.println(wins);
    }

    private static Pair<Long> checkGame(Player p1, Player p2, boolean p1IsPlaying, long possibleDiceRollCount) {
        Day21.gamesChecked++;
        if (Day21.gamesChecked % 50000000 == 0) {
            System.out.println(Day21.gamesChecked);
        }
        if (p1.getScore() >= 21) {
            return new Pair<>(1L, 0L);
        }
        if (p2.getScore() >= 21) {
            return new Pair<>(0L, 1L);
        }

        long p1Wins = 0;
        long p2Wins = 0;

        for (int i = 3; i <= 9; i++) {
            if (p1IsPlaying) {
                Player newPlayerState = p1.simulateTurn(i);
                Pair<Long> result = checkGame(newPlayerState, p2, false, nbrPossibleDiceThrowsOccurrences[i - 3] * possibleDiceRollCount);
                p1Wins += result.getFirst() * nbrPossibleDiceThrowsOccurrences[i - 3];
                p2Wins += result.getSecond() * nbrPossibleDiceThrowsOccurrences[i - 3];
            } else {
                Player newPlayerState = p2.simulateTurn(i);
                Pair<Long> result = checkGame(p1, newPlayerState, true, nbrPossibleDiceThrowsOccurrences[i - 3] * possibleDiceRollCount);
                p1Wins += result.getFirst() * nbrPossibleDiceThrowsOccurrences[i - 3];
                p2Wins += result.getSecond() * nbrPossibleDiceThrowsOccurrences[i - 3];
            }
        }

        return new Pair<>(p1Wins, p2Wins);
    }

    private static class Player {
        private final int score;
        private final int position;

        public Player(int score, int position) {
            this.score = score;
            this.position = position;
        }

        public Player simulateTurn(int totDiceThrow) {
            int newPosition = 1 + (position - 1 + totDiceThrow) % 10;
            return new Player(score + newPosition, newPosition);
        }

        public int getScore() {
            return score;
        }

        @Override
        public String toString() {
            return "(score=" + score +
                   ", position=" + position + ")";
        }
    }
}
