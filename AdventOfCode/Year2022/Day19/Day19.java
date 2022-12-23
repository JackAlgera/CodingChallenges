package Year2022.Day19;

import utils.Day;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

public class Day19 extends Day {

    public static void main(String[] args) throws IOException {
        Day19 day = new Day19();

        List<String> sampleInput = extractSampleInputLines(day.getName());
        List<String> mainInput = extractMainInputLines(day.getName());

        printAllResults(1, day.getName(),
            day.part1(sampleInput), 33,
            day.part1(mainInput), 1675);

        printAllResults(2, day.getName(),
            day.part2(sampleInput), 3472,
            day.part2(mainInput), 6840);
    }

    @Override
    public long part1(List<String> lines) throws IOException {
        List<State> blueprints = lines.stream()
                .map(l -> parseState(l, 24))
                .toList();

        return IntStream.range(0, blueprints.size())
            .map(i -> (i + 1) * bfs(blueprints.get(i)))
            .sum();
    }

    @Override
    public long part2(List<String> lines) throws IOException {
        List<State> blueprints = lines.stream()
            .map(l -> parseState(l, 32))
            .toList();

        return IntStream.range(0, blueprints.size())
            .limit(3)
            .map(i -> bfs(blueprints.get(i)))
            .reduce(1, (a, b) -> a * b);
    }

    public int bfs(State initState) {
        int maxGeodeCount = 0;
//        int totalSteps = 0;
        Deque<State> queue = new ArrayDeque<>();
        queue.add(initState);

        while (!queue.isEmpty()) {
//            totalSteps++;
            State state = queue.poll();

            int bestPossibleGeodeCount =
                state.resources[Type.GEODE.getId()]
                    + state.robotsCount[Type.GEODE.getId()] * state.timeLeft()
                    + (state.timeLeft() - 1) * state.timeLeft() / 2;

            if (bestPossibleGeodeCount <= maxGeodeCount) {
                continue;
            }

            boolean builtRobot = false;
            if (state.timeLeft() > 1) {
                for (Type robotType : Type.values()) {
                    State nextState = state.buildRobot(robotType);
                    if (nextState == null) {
                        continue;
                    }
                    queue.add(nextState);
                    builtRobot = true;
                }
            }

            if (!builtRobot) {
                int branchMaxGeodeCount = state.resources()[Type.GEODE.getId()] + state.robotsCount()[Type.GEODE.getId()] * state.timeLeft();
                maxGeodeCount = Math.max(maxGeodeCount, branchMaxGeodeCount);
            }
        }
        return maxGeodeCount;
    }

    public State parseState(String line, int maxTime) {
        String[] sections = line.split(" ");
        int[][] robotCosts = new int[][] {
            { Integer.parseInt(sections[6]), 0, 0, 0 },
            { Integer.parseInt(sections[12]), 0, 0, 0 },
            { Integer.parseInt(sections[18]), Integer.parseInt(sections[21]), 0, 0 },
            { Integer.parseInt(sections[27]), 0, Integer.parseInt(sections[30]), 0 }
        };

        return new State(
            0,
            maxTime,
            robotCosts,
            new int[]{
                Math.max(
                    Math.max(robotCosts[0][0], robotCosts[1][0]),
                    Math.max(robotCosts[2][0], robotCosts[3][0])),
                robotCosts[2][1],
                robotCosts[3][2],
                Integer.MAX_VALUE},
            new int[]{1, 0, 0, 0},
            new int[]{0, 0, 0, 0}
        );
    }

    public enum Type {
        ORE(0), CLAY(1), OBSIDIAN(2), GEODE(3);

        private final int id;

        Type(int id) { this.id = id; }
        public int getId() { return id; }
    }

    public record State(int time, int maxTime, int[][] robotCosts, int[] maxRobotCosts, int[] robotsCount, int[] resources) {
        public int timeLeft() {
            return maxTime - time;
        }

        public State buildRobot(Type robotType) {
            if (!canBuildRobot(robotType)) {
                return null;
            }

            if (robotsCount[robotType.getId()] >= maxRobotCosts[robotType.getId()]) {
                return null;
            }

            int timeToStartBuilding = 0;
            int[] robotCost = robotCosts[robotType.getId()];

            for (int i = 0; i < robotCost.length; i++) {
                if (resources[i] >= robotCost[i]) {
                    continue;
                }

                timeToStartBuilding = Math.max(
                    timeToStartBuilding,
                    (robotCost[i] - resources[i] + robotsCount[i] - 1) / robotsCount[i]
                );
            }

            int timeDoneBuilding = timeToStartBuilding + 1;

            return new State(
                time + timeDoneBuilding,
                maxTime,
                robotCosts,
                maxRobotCosts,
                new int[]{
                    robotsCount[Type.ORE.getId()] + (robotType == Type.ORE ? 1 : 0),
                    robotsCount[Type.CLAY.getId()] + (robotType == Type.CLAY ? 1 : 0),
                    robotsCount[Type.OBSIDIAN.getId()] + (robotType == Type.OBSIDIAN ? 1 : 0),
                    robotsCount[Type.GEODE.getId()] + (robotType == Type.GEODE ? 1 : 0)
                },
                new int[]{
                    resources[Type.ORE.getId()] + timeDoneBuilding * robotsCount[Type.ORE.getId()] - robotCost[Type.ORE.getId()],
                    resources[Type.CLAY.getId()] + timeDoneBuilding * robotsCount[Type.CLAY.getId()] - robotCost[Type.CLAY.getId()],
                    resources[Type.OBSIDIAN.getId()] + timeDoneBuilding * robotsCount[Type.OBSIDIAN.getId()] - robotCost[Type.OBSIDIAN.getId()],
                    resources[Type.GEODE.getId()] + timeDoneBuilding * robotsCount[Type.GEODE.getId()] - robotCost[Type.GEODE.getId()]
                }
            );
        }

        private boolean canBuildRobot(Type robotType) {
            int[] robotCost = robotCosts()[robotType.getId()];

            for (int i = 0; i < robotCost.length; i++) {
                int resourceCost = robotCost[i];
                if (resourceCost == 0) {
                    continue;
                }
                // Check if we can build a robot in time
                if (resourceCost >= resources[i] + robotsCount[i] * timeLeft()) {
                    return false;
                }
            }

            return true;
        }
    }
}
