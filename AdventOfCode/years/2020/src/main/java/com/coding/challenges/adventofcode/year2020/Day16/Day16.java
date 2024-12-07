package com.coding.challenges.adventofcode.year2020.Day16;

import com.coding.challenges.adventofcode.utils.Day;
import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class Day16 extends Day<Long> {

  public static void main(String[] args) throws IOException {
    Day16 day = new Day16();

    day.printPart1("sample-input", 71L);
    day.printPart1("input", 26026L);

    day.printPart2("input", 1305243193339L);
  }

  @Override
  public Long part1(List<String> lines) {
    List<Field> fields = new ArrayList<>();
    List<Ticket> tickets = new ArrayList<>();
    parseInput(lines, fields, tickets);

    long errorRate = 0L;
    for (Ticket ticket : tickets) {
      for (int value : ticket.values) {
        if (!ticket.isValidField(value, fields)) {
          errorRate += value;
        }
      }
    }

    return errorRate;
  }

  @Override
  public Long part2(List<String> lines) {
    List<Field> fields = new ArrayList<>();
    List<Ticket> tickets = new ArrayList<>();
    Ticket myTicket = parseInput(lines, fields, tickets);

    tickets = tickets.stream().filter(ticket -> ticket.isValid(fields)).toList();

    List<ArrayList<Integer>> fieldIndexesList =
        IntStream.range(0, fields.size()).mapToObj(i -> new ArrayList<Integer>()).toList();

    for (int k = 0; k < fields.size(); k++) {
      Field field = fields.get(k);
      for (int j = 0; j < tickets.get(0).values().size(); j++) {
        boolean isValid = true;
        for (int i = 0; i < tickets.size(); i++) {
          if (!field.contains(tickets.get(i).values().get(j))) {
            isValid = false;
            break;
          }
        }
        if (isValid) {
          fieldIndexesList.get(k).add(j);
        }
      }
    }

    int[] finalIndexes = new int[fields.size()];
    boolean everythingSorted = false;
    while (!everythingSorted) {
      everythingSorted = true;

      for (int i = 0; i < fieldIndexesList.size(); i++) {
        if (fieldIndexesList.get(i).size() > 1) {
          everythingSorted = false;
        }

        if (fieldIndexesList.get(i).size() == 1) {
          int index = fieldIndexesList.get(i).get(0);
          finalIndexes[i] = index;
          for (ArrayList<Integer> integers : fieldIndexesList) {
            integers.remove(Integer.valueOf(index));
          }
        }
      }
    }

    return Arrays.stream(finalIndexes)
        .limit(6)
        .mapToLong(index -> myTicket.values().get(index))
        .reduce(1, (a, b) -> a * b);
  }

  public Ticket parseInput(List<String> lines, List<Field> fields, List<Ticket> tickets) {
    int i = 0;
    while (!lines.get(i).isEmpty()) {
      String line = lines.get(i);
      fields.add(
          new Field(
              line.split(":")[0],
              Arrays.stream(line.split(": ")[1].split(" or "))
                  .map(
                      range ->
                          new Range(
                              Integer.parseInt(range.split("-")[0]),
                              Integer.parseInt(range.split("-")[1])))
                  .toList()));
      i++;
    }
    i += 2;
    Ticket myTicket =
        new Ticket(Arrays.stream(lines.get(i).split(",")).map(Integer::parseInt).toList());
    i += 3;
    while (i < lines.size()) {
      String line = lines.get(i);
      tickets.add(new Ticket(Arrays.stream(line.split(",")).map(Integer::parseInt).toList()));
      i++;
    }
    return myTicket;
  }

  public record Field(String name, List<Range> ranges) {
    public boolean contains(int value) {
      for (Range range : ranges) {
        if (range.contains(value)) {
          return true;
        }
      }
      return false;
    }
  }

  public record Range(int min, int max) {
    public boolean contains(int value) {
      return value >= min && value <= max;
    }
  }

  public record Ticket(List<Integer> values) {
    public boolean isValid(List<Field> fields) {
      for (int value : values) {
        if (!isValidField(value, fields)) {
          return false;
        }
      }
      return true;
    }

    public boolean isValidField(int value, List<Field> fields) {
      boolean isValid = false;
      for (Field field : fields) {
        for (Range range : field.ranges) {
          if (range.contains(value)) {
            isValid = true;
            break;
          }
        }
        if (isValid) {
          break;
        }
      }
      return isValid;
    }
  }
}
