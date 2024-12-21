package com.coding.challenges.adventofcode.year2024.Day21;

import com.coding.challenges.adventofcode.utils.Day;
import com.coding.challenges.adventofcode.utils.Pos;
import com.coding.challenges.adventofcode.utils.enums.Direction;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class Day21 extends Day<Long> {

  private static final Map<Character, Pos> POSITIONS = getDigitPositions();

  private static final Pos DIRECTIONAL_KEYPAD_MAX = new Pos(2, 3);
  private static final Pos NUMERIC_KEYPAD_MAX = new Pos(4, 3);

  private static final Pos DIRECTIONAL_KEYPAD_INVALID = new Pos(0, 0);
  private static final Pos NUMERIC_KEYPAD_INVALID = new Pos(3, 0);

  public static void main(String[] args) throws IOException {
    Day21 day = new Day21();

    day.printPart1("sample-input", 126384L);
    day.printPart1("input", 278748L);

    day.printPart2("sample-input", 154115708116294L);
    day.printPart2("input", 337744744231414L);
  }

  @Override
  public Long part1(List<String> lines) {
    return lines.stream().mapToLong(code -> countPresses(code, 2)).sum();
  }

  @Override
  public Long part2(List<String> lines) {
    return lines.stream().mapToLong(code -> countPresses(code, 25)).sum();
  }

  private long countPresses(String code, int robotCount) {
    Pos start = new Pos(3, 2);
    long score = 0;
    Map<DPState, Long> dp = new HashMap<>();

    for (char digit : code.toCharArray()) {
      Pos end = POSITIONS.get(digit);
      score += shortestPath(start, end, robotCount, false, dp);
      start = end;
    }

    return score * Long.parseLong(code.substring(0, 3));
  }

  private long shortestPath(
      Pos start, Pos end, int robotCount, boolean isDirectionalKeypad, Map<DPState, Long> dp) {
    Map<Pos, Long> visited = new HashMap<>();
    Queue<State> queue = new LinkedList<>();
    queue.add(new State(start, ""));
    long best = Long.MAX_VALUE;

    while (!queue.isEmpty()) {
      State current = queue.poll();
      if (visited.containsKey(current.pos)
          && current.presses.length() >= (visited.get(current.pos) + 2)) {
        continue;
      }
      visited.put(current.pos, (long) current.presses.length());

      if (current.pos.equals(end)) {
        best =
            Math.min(
                best,
                shortestRobot(
                    current.presses + "a", isDirectionalKeypad ? robotCount - 1 : robotCount, dp));
        continue;
      }

      for (Direction d : Direction.values()) {
        Pos nextPos = current.pos.move(d);
        if (!nextPos.isValid(isDirectionalKeypad ? DIRECTIONAL_KEYPAD_MAX : NUMERIC_KEYPAD_MAX)
            || (nextPos.equals(
                isDirectionalKeypad ? DIRECTIONAL_KEYPAD_INVALID : NUMERIC_KEYPAD_INVALID))) {
          continue;
        }
        char c =
            switch (d) {
              case N -> '^';
              case E -> '>';
              case S -> 'v';
              case W -> '<';
            };
        queue.add(new State(nextPos, current.presses + c));
      }
    }

    return best;
  }

  private long shortestRobot(String presses, int robotCount, Map<DPState, Long> dp) {
    if (robotCount == 0) {
      return presses.length();
    }
    DPState state = new DPState(presses, robotCount);
    if (dp.containsKey(state)) {
      return dp.get(state);
    }

    long total = 0;
    Pos start = new Pos(0, 2);
    for (char press : presses.toCharArray()) {
      Pos end = POSITIONS.get(press);
      total += shortestPath(start, end, robotCount, true, dp);
      start = end;
    }

    dp.put(state, total);
    return total;
  }

  private record State(Pos pos, String presses) {}

  private record DPState(String presses, int robotCount) {}

  private static Map<Character, Pos> getDigitPositions() {
    Map<Character, Pos> positions = new HashMap<>();
    positions.put('7', new Pos(0, 0));
    positions.put('8', new Pos(0, 1));
    positions.put('9', new Pos(0, 2));
    positions.put('4', new Pos(1, 0));
    positions.put('5', new Pos(1, 1));
    positions.put('6', new Pos(1, 2));
    positions.put('1', new Pos(2, 0));
    positions.put('2', new Pos(2, 1));
    positions.put('3', new Pos(2, 2));
    positions.put('0', new Pos(3, 1));
    positions.put('A', new Pos(3, 2));
    positions.put('^', new Pos(0, 1));
    positions.put('a', new Pos(0, 2));
    positions.put('<', new Pos(1, 0));
    positions.put('v', new Pos(1, 1));
    positions.put('>', new Pos(1, 2));
    return positions;
  }
}
