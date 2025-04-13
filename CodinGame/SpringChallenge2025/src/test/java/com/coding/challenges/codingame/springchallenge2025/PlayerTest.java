package com.coding.challenges.codingame.springchallenge2025;

import com.coding.challenges.codingame.springchallenge2025.Player.Board;
import com.coding.challenges.codingame.springchallenge2025.Player.Neighbor;
import com.coding.challenges.codingame.springchallenge2025.Player.StateUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class PlayerTest implements WithAssertions {

  private static final Board EMPTY_BOARD = new Board("000000000", 3);

  @Nested
  class GetBoardHash {
    @Test
    void test() {
      var board = new Board("054030030", 40);
      Map<Board, Long> dp = new HashMap<>();

      var response = board.getBoardHash(dp);

      // End states: 559238314648167
      assertThat(response).isEqualTo(667094338L);
    }
  }

  @Nested
  class CheckNextStates {

  }

  @Nested
  class AddNextStates {

    static Stream<Arguments> addNextStates_test() {
      return Stream.of(
              Arguments.of(Board.neighbor2s, 3),
              Arguments.of(Board.neighbor3s, 1),
              Arguments.of(Board.neighbor4s, 0));
    }

    @ParameterizedTest
    @MethodSource
    void addNextStates_test(int[][] neighborIds, int expectedSize) {
      // Given
      var i = 1;
      var j = 1;
      Map<Integer, Neighbor> neighborsMap = Map.of(
              0, new Neighbor(0, 1, 1),
              1, new Neighbor(1, 2, 6),
              2, new Neighbor(2, 1, 1),
              3, new Neighbor(1, 0, 1));
      List<Board> nextStates = new ArrayList<>();
      var board1 = getBoard("""
                                    1 1 1
                                    1 0 6
                                    1 1 1
                                    """, 3);

      // When
      var result1 = board1.addNextStates(i, j, neighborIds, neighborsMap, nextStates);

      // Then
      assertThat(result1).isEqualTo(expectedSize > 0);
      assertThat(nextStates).hasSize(expectedSize);
    }

    static Stream<Arguments> addNextStates_allNeighborsNonNull() {
      return Stream.of(
          Arguments.of(Board.neighbor2s, 6),
          Arguments.of(Board.neighbor3s, 4),
          Arguments.of(Board.neighbor4s, 1));
    }

    @ParameterizedTest
    @MethodSource
    void addNextStates_allNeighborsNonNull(int[][] neighborIds, int expectedSize) {
      // Given
      var i = 1;
      var j = 1;
      Map<Integer, Neighbor> neighborsMap = Map.of(
          0, new Neighbor(0, 0, 1),
          1, new Neighbor(1, 1, 1),
          2, new Neighbor(2, 0, 1),
          3, new Neighbor(1, 1, 1));
      List<Board> nextStates = new ArrayList<>();
      var board1 = getBoard("""
                                    1 1 1
                                    1 0 1
                                    1 1 1
                                    """, 3);

      // When
      var result1 = board1.addNextStates(i, j, neighborIds, neighborsMap, nextStates);

      // Then
      assertThat(result1).isTrue();
      assertThat(nextStates).hasSize(expectedSize);
    }

    static Stream<Arguments> addNextStates_allNeighborsNull() {
      return Stream.of(
              Arguments.of((Object) Board.neighbor2s),
              Arguments.of((Object) Board.neighbor3s),
              Arguments.of((Object) Board.neighbor4s));
    }

    @ParameterizedTest
    @MethodSource
    void addNextStates_allNeighborsNull(int[][] neighborIds) {
      // Given
      var i = 1;
      var j = 1;
      Map<Integer, Neighbor> neighborsMap = Map.of();
      List<Board> nextStates = new ArrayList<>();
      var board1 = getBoard("""
                                    1 0 1
                                    0 0 0
                                    1 0 1
                                    """, 3);
      var board2 = getBoard("""
                                    1 1 1
                                    0 0 0
                                    1 0 1
                                    """, 3);

      // When
      var result1 = board1.addNextStates(i, j, neighborIds, neighborsMap, nextStates);
      var result2 = board2.addNextStates(i, j, neighborIds, neighborsMap, nextStates);

      // Then
      assertThat(result1).isFalse();
      assertThat(result2).isFalse();
      assertThat(nextStates).isEmpty();
    }
  }

  @Nested
  class GetNextState {

    @Test
    void getNextState() {
      // Given
      var turnsLeft = 3;
      var board =
          getBoard(
              """
                                   1 2 3
                                   3 0 4
                                   6 7 8
                                   """,
              turnsLeft);
      var neighbors = List.of(new Neighbor(0, 1, 2), new Neighbor(1, 2, 4));

      // When
      var nextState = board.getNextState(1, 1, neighbors);

      // Then
      var expectedState =
          getBoard(
              """
          1 0 3
          3 6 0
          6 7 8
          """, turnsLeft - 1);
      assertThat(nextState).isEqualTo(expectedState);
    }
  }

  @Nested
  class getSum {

    static Stream<Arguments> testGetSum_nominal() {
      return Stream.of(
          Arguments.of(List.of(new Neighbor(0, 0, 2), new Neighbor(0, 1, 5)), 7),
          Arguments.of(List.of(new Neighbor(0, 0, 1)), 1));
    }

    @ParameterizedTest
    @MethodSource
    void testGetSum_nominal(List<Neighbor> neighbors, int expected) {
      // Given / When / Then
      assertThat(EMPTY_BOARD.getSum(neighbors)).isEqualTo(expected);
    }
  }

  @Nested
  class IsNextStateAvailable {

    static Stream<Arguments> testIsNextStateAvailable_nominal() {
      return Stream.of(
              Arguments.of(List.of(), true),
              Arguments.of(List.of(new Neighbor(0, 0, 2)), true),
              Arguments.of(Stream.of(new Neighbor(0, 0, 2), null).toList(), false),
              Arguments.of(List.of(new Neighbor(0, 0, 2), new Neighbor(0, 0, 5)), false));
    }

    @ParameterizedTest
    @MethodSource
    void testIsNextStateAvailable_nominal(List<Neighbor> neighbors, boolean expected) {
      // Given / When / Then
      assertThat(EMPTY_BOARD.isNextStateAvailable(neighbors)).isEqualTo(expected);
    }
  }

  @Nested
  class IsBoardFull {

    static Stream<Arguments> testIsBoardFull_nominal() {
      return Stream.of(
          Arguments.of("123456789", true),
          Arguments.of("123456780", false),
          Arguments.of("123456700", false),
          Arguments.of("234056712", false),
          Arguments.of("123456744", true),
          Arguments.of("999999999", true),
          Arguments.of("111111111", true));
    }

    @ParameterizedTest
    @MethodSource
    void testIsBoardFull_nominal(String state, boolean expected) {
      // Given
      var board = new Board(state, 3);

      // When / Then
      assertThat(board.isBoardFull()).isEqualTo(expected);
    }
  }

  @Nested
  class IsEmptySquare {
    static Stream<Arguments> testIsEmptySquare_nominal() {
      return Stream.of(
          Arguments.of(0, 0, true),
          Arguments.of(0, 1, false),
          Arguments.of(0, 2, true),
          Arguments.of(1, 0, false),
          Arguments.of(1, 1, false),
          Arguments.of(1, 2, false),
          Arguments.of(2, 0, true),
          Arguments.of(2, 1, false),
          Arguments.of(2, 2, true));
    }

    @ParameterizedTest
    @MethodSource
    void testIsEmptySquare_nominal(int i, int j, boolean expected) {
      // Given
      var board =
          new Board(
              """
                    0 2 0
                    4 5 6
                    0 8 0
                    """
                  .replaceAll("\\s+", ""),
              3);

      // When / Then
      assertThat(board.isEmptySquare(i, j)).isEqualTo(expected);
    }
  }

  @Nested
  class StateUtilsTest {

    static Stream<Arguments> testAddDice() {
      return Stream.of(
          Arguments.of("000000000", 0, 0, 1, "100000000"),
          Arguments.of("000000000", 0, 1, 2, "020000000"),
          Arguments.of("000000000", 0, 2, 3, "003000000"),
          Arguments.of("000000000", 1, 0, 4, "000400000"),
          Arguments.of("000000000", 1, 1, 5, "000050000"),
          Arguments.of("000000000", 1, 2, 6, "000006000"),
          Arguments.of("000000000", 2, 0, 7, "000000700"),
          Arguments.of("000000000", 2, 1, 8, "000000080"),
          Arguments.of("000000000", 2, 2, 9, "000000009"),
          Arguments.of("000000001", 2, 2, 9, "000000009"));
    }

    @ParameterizedTest
    @MethodSource
    void testAddDice(String state, int i, int j, int val, String expected) {
      // Given / When / Then
      assertThat(StateUtils.addDice(val, i, j, state)).isEqualTo(expected);
    }

    static Stream<Arguments> testRemoveDice() {
      return Stream.of(
          Arguments.of("000000000", 2, 2, "000000000"),
          Arguments.of("000100001", 1, 0, "000000001"),
          Arguments.of("123456789", 0, 0, "023456789"),
          Arguments.of("123456789", 0, 1, "103456789"),
          Arguments.of("123456789", 0, 2, "120456789"),
          Arguments.of("123456789", 1, 0, "123056789"),
          Arguments.of("123456789", 1, 1, "123406789"),
          Arguments.of("123456789", 1, 2, "123450789"),
          Arguments.of("123456789", 2, 0, "123456089"),
          Arguments.of("123456789", 2, 1, "123456709"),
          Arguments.of("123456789", 2, 2, "123456780"));
    }

    @ParameterizedTest
    @MethodSource
    void testRemoveDice(String state, int i, int j, String expected) {
      // Given
      var neighbor = new Player.Neighbor(i, j, 3);

      // When / Then
      assertThat(StateUtils.removeDice(neighbor, state)).isEqualTo(expected);
    }
  }

  private Board getBoard(String state, int turnsLeft) {
    return new Board(state.replaceAll("\\s+", ""), turnsLeft);
  }
}
