package Year2022.Day16;

import utils.Day;
import utils.Pair;

import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class Day16 extends Day {

    public static void main(String[] args) throws IOException {
        Day16 day = new Day16();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        printAllResults(1, day.getName(),
            day.part1(sampleInput), 1651,
            day.part1(mainInput), 2114);

        printAllResults(2, day.getName(),
            day.part2(sampleInput), 1707,
            day.part2(mainInput), 2666);
    }

    @Override
    public long part1(List<String> lines) throws IOException {
        int maxTime = 30;
        Map<String, Node> nodeMap = lines.stream().map(this::extractValve).collect(Collectors.toMap(Node::id, n -> n));
        Map<String, Map<String, Integer>> shortestDistances = getShortestDistances(nodeMap);
        Map<String, Node> nodesWithFlowMap = nodeMap.values()
            .stream()
            .filter(n -> n.flowRate > 0)
            .collect(Collectors.toMap(Node::id, n -> n));

        return bfs(
            new State(
                0,
                maxTime,
                0,
                "AA",
                nodesWithFlowMap
                    .values().stream()
                    .map(n -> n.id)
                    .filter(n -> !n.equals("AA"))
                    .collect(Collectors.toList())
            ),
            maxTime,
            nodeMap,
            shortestDistances
        );
    }

    @Override
    public long part2(List<String> lines) throws IOException {
        int maxTime = 26;
        Map<String, Node> nodeMap = lines.stream().map(this::extractValve).collect(Collectors.toMap(Node::id, n -> n));
        Map<String, Map<String, Integer>> shortestDistances = getShortestDistances(nodeMap);
        Map<String, Node> nodesWithFlowMap = nodeMap.values()
            .stream()
            .filter(n -> n.flowRate > 0)
            .collect(Collectors.toMap(Node::id, n -> n));

        int bestCombinedPressure = 0;

        for (int i = 0; i < 250; i++) {
            List<String> nodesToCheck = new ArrayList<>(nodesWithFlowMap.values().stream()
                .map(n -> n.id)
                .filter(n -> !n.equals("AA"))
                .toList());
            Collections.shuffle(nodesToCheck);
            for (int k = 1; k < nodesWithFlowMap.size() - 1; k++) {
                int pressureElephant = bfs(
                    new State(
                        0,
                        maxTime,
                        0,
                        "AA",
                        nodesToCheck.stream().limit(k).toList()
                    ),
                    maxTime,
                    nodeMap,
                    shortestDistances
                );

                int pressureJack = bfs(
                    new State(
                        0,
                        maxTime,
                        0,
                        "AA",
                        nodesToCheck.stream().skip(k).toList()
                    ),
                    maxTime,
                    nodeMap,
                    shortestDistances
                );

                if (pressureJack + pressureElephant > bestCombinedPressure) {
                    bestCombinedPressure = pressureJack + pressureElephant;
                }
            }
        }

        return bestCombinedPressure;
    }

    public int bfs(State initState, int maxTime, Map<String, Node> nodeMap, Map<String, Map<String, Integer>> shortestDistances) {
        int mostReleasedPressure = 0;
        Queue<State> queue = new ArrayDeque<>();
        queue.add(initState);

        while (!queue.isEmpty()) {
            State state = queue.poll();

            if (state.nodesToVisit().isEmpty()) {
                if (state.totalPressure() > mostReleasedPressure) {
                    mostReleasedPressure = state.totalPressure();
                }
                continue;
            }

            for (String node : state.nodesToVisit) {
                int distanceToNode = shortestDistances.get(state.node).get(node);

                int timeToStartProducing = distanceToNode + 1;
                if (timeToStartProducing >= state.timeLeft()) {
                    if (state.totalPressure() > mostReleasedPressure) {
                        mostReleasedPressure = state.totalPressure();
                    }
                    continue;
                }

                int totalPossiblePressure = (state.timeLeft() - timeToStartProducing) * nodeMap.get(node).flowRate;
                queue.add(new State(
                    state.time + timeToStartProducing,
                    maxTime,
                    state.totalPressure + totalPossiblePressure,
                    node,
                    state.nodesToVisit.stream()
                        .filter(n -> !n.equals(node))
                        .collect(Collectors.toList())
                ));
            }
        }

        return mostReleasedPressure;
    }

    public record State(int time, int maxTime, int totalPressure, String node, List<String> nodesToVisit) {
        public int timeLeft() {
            return maxTime - time;
        }
    }

    public Map<String, Map<String, Integer>> getShortestDistances(Map<String, Node> nodeMap) {
        Map<String, Map<String, Integer>> shortestDistances = new HashMap<>();

        for (String startId : nodeMap.keySet()) {
            Map<String, Integer> map = new HashMap<>();
            for (String endId : nodeMap.keySet()) {
                map.put(endId, getShortestDistance(nodeMap, startId, endId));
            }
            shortestDistances.put(startId, map);
        }

        return shortestDistances;
    }

    private int getShortestDistance(Map<String, Node> nodeMap, String startId, String endId) {
        Map<String, Boolean> visited = new HashMap<>(nodeMap.size());
        visited.put(startId, true);

        // Heuristic value for Node id
        Queue<Key> queue = new ArrayDeque<>();
        queue.add(new Key(startId, 0));

        while(!queue.isEmpty()) {
            Key node = queue.stream().min(Comparator.comparing(Key::val)).get();
            queue.remove(node);

            if (node.key().equals(endId)) {
                return node.val;
            }

            for (String childId : nodeMap.get(node.key()).children()) {
                if (!visited.containsKey(childId) || !visited.get(childId)) {
                    visited.put(childId, true);
                    queue.add(new Key(childId, node.val() + 1));
                }
            }
        }

        return -1;
    }

    public record Key(String key, Integer val) {}

    public record Node(String id, int flowRate, List<String> children) { }

    private Node extractValve(String input) {
        Matcher matcher = Pattern
            .compile("Valve ([A-Z]+) has flow rate=(\\d+); tunnel(s\\b|\\b) lead(s\\b|\\b) to valve(s\\b|\\b) (.*)")
            .matcher(input);
        if (matcher.find()) {
            return new Node(
                matcher.group(1),
                Integer.parseInt(matcher.group(2)),
                Arrays.stream(matcher.group(6).split(","))
                    .map(String::strip)
                    .collect(Collectors.toList())
            );
        }

        return null;
    }
}
