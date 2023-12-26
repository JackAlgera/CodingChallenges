package adventofcode.Year2023.Day25;

import adventofcode.utils.Day;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class Day25 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day25 day = new Day25();

        day.printPart1("sample-input", 54L);
        day.printPart1("input", 606062L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        Map<String, Node> nodes = parseInput(lines);
        Set<String> leftGroup = new HashSet<>(nodes.keySet());
        Set<String> rightGroup = new HashSet<>();

        while (true) {
            int totalWrongNeighbors = 0;
            int max = 0;
            String maxId = leftGroup.iterator().next();
            for (String nodeId : leftGroup) {
                int count = (int) nodes.get(nodeId).children().stream()
                    .filter(rightGroup::contains)
                    .count();
                if (count > max) {
                    max = count;
                    maxId = nodeId;
                }
                totalWrongNeighbors += count;
            }
            if (totalWrongNeighbors == 3L) {
                return (long) leftGroup.size() * rightGroup.size();
            }

            leftGroup.remove(maxId);
            rightGroup.add(maxId);
        }
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        return 0L;
    }

    public Map<String, Node> parseInput(List<String> lines) {
        Map<String, Node> nodes = new HashMap<>();
        for (String line : lines) {
            String id = line.split(": ")[0];
            Set<String> children = Arrays.stream(line.split(": ")[1].split(" "))
                .collect(Collectors.toSet());
            if (nodes.containsKey(id)) {
                nodes.get(id).children().addAll(children);
            } else {
                nodes.put(id, new Node(id, children));
            }

            for (String child : children) {
                if (nodes.containsKey(child)) {
                    nodes.get(child).children().add(id);
                } else {
                    nodes.put(child, new Node(child, new HashSet<>(Set.of(id))));
                }
            }
        }
        return nodes;
    }

    public record Node(String id, Set<String> children) {}
}
