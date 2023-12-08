package adventofcode.Year2023.Day8;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

public class Day8 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day8 day = new Day8();

        day.printPart1("sample-input-part1", 6L);
        day.printPart1("input", 22357L);

        day.printPart2("sample-input-part2", 6L);
        day.printPart2("input", 10371555451871L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        String instructions = lines.get(0);
        Map<String, Node> nodes = parseInput(lines.subList(2, lines.size()));

        return getSteps("AAA", instructions, nodes, 0, false).initSteps();
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        String instructions = lines.get(0);
        Map<String, Node> nodes = parseInput(lines.subList(2, lines.size()));

        return nodes.values().stream()
          .map(Node::id)
          .filter(id -> id.charAt(2) == 'A')
          .map(id -> getCycle(id, instructions, nodes))
          .mapToLong(Cycle::cycle)
          .reduce(this::lcm)
          .getAsLong();
    }

    public long gcd(long a, long b)
    {
        if (a == 0) {
            return b;
        }
        return gcd(b % a, a);
    }

    public long lcm(long a, long b)
    {
        return (a / gcd(a, b)) * b;
    }

    public Cycle getCycle(String id, String instructions, Map<String, Node> nodes) {
        Cycle initSteps = getSteps(id, instructions, nodes, 0, false);

        return new Cycle(
          id,
          initSteps.end,
          initSteps.initSteps,
          getSteps(initSteps.end, instructions, nodes, (int) initSteps.initSteps, true).initSteps);
    }

    public Cycle getSteps(String id, String instructions, Map<String, Node> nodes, int offset, boolean skipFirst) {
        Node current = nodes.get(id);
        long steps = 0;
        int i = offset % instructions.length();
        while (skipFirst || !current.isEndNode()) {
            skipFirst = false;
            if (instructions.charAt(i) == 'L') {
                current = nodes.get(nodes.get(current.id).left());
            } else {
                current = nodes.get(nodes.get(current.id).right());
            }

            i++;
            steps++;
            if (i >= instructions.length()) {
                i = 0;
            }
        }

        return new Cycle(id, current.id, steps, 0);
    }

    public Map<String, Node> parseInput(List<String> lines) {
        Map<String, Node> nodes = new HashMap<>();
        for (String line : lines) {
            String id = line.split(" = ")[0];
            String left = line.substring(7, 10);
            String string = line.substring(12, 15);
            nodes.put(id, new Node(id, left, string, id.charAt(2) == 'Z'));
        }
        return nodes;
    }

    public record Node(String id, String left, String right, boolean isEndNode) {}
    public record Cycle(String start, String end, long initSteps, long cycle) {}
}
