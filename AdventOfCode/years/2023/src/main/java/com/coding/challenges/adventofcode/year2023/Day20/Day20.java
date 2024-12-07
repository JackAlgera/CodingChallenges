package com.coding.challenges.adventofcode.year2023.Day20;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.*;
import lombok.With;

public class Day20 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day20 day = new Day20();

    day.printPart1("sample-input", 32000000L);
    day.printPart1("input", 929810733L);

    day.printPart2("input", 231657829136023L);
  }

  @Override
  public Long part1(List<String> lines) {
    Map<String, Conjunction> conjunctions = new HashMap<>();
    Map<String, FlipFlop> flipFlops = new HashMap<>();
    parseInput(lines, conjunctions, flipFlops);

    Pulses pulses = new Pulses(0, 0);
    for (int i = 0; i < 1000; i++) {
      Pulses newPulses = bfs(conjunctions, flipFlops, null);
      pulses = new Pulses(pulses.low() + newPulses.low(), pulses.high() + newPulses.high());
    }
    return pulses.low() * pulses.high();
  }

  @Override
  public Long part2(List<String> lines) {
    Map<String, Conjunction> conjunctions = new HashMap<>();
    Map<String, FlipFlop> flipFlops = new HashMap<>();
    parseInput(lines, conjunctions, flipFlops);

    return conjunctions.get("lg").parentPulses().keySet().stream()
        .mapToLong(k -> countPresses(lines, k))
        .reduce(1L, (a, b) -> a * b);
  }

  public long countPresses(List<String> lines, String target) {
    Map<String, Conjunction> conjunctions = new HashMap<>();
    Map<String, FlipFlop> flipFlops = new HashMap<>();
    parseInput(lines, conjunctions, flipFlops);

    long totalPresses = 0;
    while (true) {
      totalPresses++;
      Pulses newPulses = bfs(conjunctions, flipFlops, target);
      if (newPulses.high < 0) {
        return totalPresses;
      }
    }
  }

  public Pulses bfs(
      Map<String, Conjunction> conjunctions, Map<String, FlipFlop> flipFlops, String target) {
    Queue<State> queue = new LinkedList<>();
    queue.add(new State("broadcaster", PulseType.LOW));

    long lowPulses = 0L;
    long highPulses = 0L;
    boolean foundTarget = false;

    while (!queue.isEmpty()) {
      if (target != null && conjunctions.get("lg").parentPulses().get(target) == PulseType.HIGH) {
        foundTarget = true;
      }

      State state = queue.poll();
      if (state.pulse() == PulseType.LOW) {
        lowPulses++;
      } else {
        highPulses++;
      }

      if (conjunctions.containsKey(state.id())) {
        Conjunction conjunction = conjunctions.get(state.id());
        PulseType pulse = conjunction.pulse();

        for (String destination : conjunction.destinations()) {
          queue.add(new State(destination, pulse));
          if (conjunctions.containsKey(destination)) {
            conjunctions.get(destination).parentPulses().put(conjunction.id(), pulse);
          }
        }
      } else {
        if (state.pulse() == PulseType.HIGH) {
          continue;
        }

        FlipFlop flipFlop = flipFlops.get(state.id());
        if (!Objects.equals(state.id(), "broadcaster")) {
          flipFlop = flipFlop.flip();
        }
        flipFlops.put(state.id(), flipFlop);

        for (String destination : flipFlop.destinations()) {
          queue.add(new State(destination, flipFlop.pulse()));
          if (conjunctions.containsKey(destination)) {
            conjunctions.get(destination).parentPulses().put(flipFlop.id(), flipFlop.pulse());
          }
        }
      }
    }

    if (foundTarget) {
      return new Pulses(-1L, -1L);
    } else {
      return new Pulses(lowPulses, highPulses);
    }
  }

  public void parseInput(
      List<String> lines, Map<String, Conjunction> conjunctions, Map<String, FlipFlop> flipFlops) {
    for (String line : lines) {
      String[] split = line.split(" -> ");
      List<String> destinations = Arrays.stream(split[1].split(",")).map(String::strip).toList();
      String id = split[0];

      if (Objects.equals(id, "broadcaster")) {
        flipFlops.put(id, new FlipFlop(id, PulseType.LOW, destinations));
        continue;
      }

      char type = id.charAt(0);
      id = id.substring(1);
      if (type == '%') {
        flipFlops.put(id, new FlipFlop(id, PulseType.LOW, destinations));
      } else {
        conjunctions.put(id, new Conjunction(id, new HashMap<>(), destinations));
      }
    }
    for (FlipFlop flipFlop : flipFlops.values()) {
      flipFlop
          .destinations()
          .forEach(
              destination -> {
                if (conjunctions.containsKey(destination)) {
                  conjunctions.get(destination).parentPulses().put(flipFlop.id(), PulseType.LOW);
                }
              });
    }
    for (Conjunction conjunction : conjunctions.values()) {
      conjunction
          .destinations()
          .forEach(
              destination -> {
                if (conjunctions.containsKey(destination)) {
                  conjunctions.get(destination).parentPulses().put(conjunction.id(), PulseType.LOW);
                }
              });
    }
  }

  public record Conjunction(
      String id, Map<String, PulseType> parentPulses, List<String> destinations) {
    public PulseType pulse() {
      return parentPulses.values().stream().allMatch(pulse -> pulse == PulseType.HIGH)
          ? PulseType.LOW
          : PulseType.HIGH;
    }
  }

  @With
  public record FlipFlop(String id, PulseType pulse, List<String> destinations) {
    public FlipFlop flip() {
      return this.withPulse(pulse == PulseType.HIGH ? PulseType.LOW : PulseType.HIGH);
    }
  }

  public enum PulseType {
    HIGH,
    LOW
  }

  public record State(String id, PulseType pulse) {}

  public record Pulses(long low, long high) {}
}
