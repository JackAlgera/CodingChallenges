package foobargoogleinterview;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

public class Exercise4A {
    public static void main(String[] args) {
        System.out.println(getSolarDoomsDaySolution(
                new int[]{0},
                new int[]{3},
                new int[][]{{0, 7, 0, 0}, {0, 0, 6, 0}, {0, 0, 0, 8}, {9, 0, 0, 0}}
        ));
        System.out.println(getSolarDoomsDaySolution(
                new int[]{0, 1},
                new int[]{4, 5},
                new int[][]{{0, 0, 4, 6, 0, 0}, {0, 0, 5, 2, 0, 0}, {0, 0, 0, 0, 4, 4}, {0, 0, 0, 0, 6, 6}, {0, 0, 0, 0, 0, 0}, {0, 0, 0, 0, 0, 0}}
        ));
    }

    /*
    Looking at this problem, it's clearly a maximum flow problem. After doing some research, it seems like the Ford-Fulkerson
    algorithm is the way to go. To determine if theirs still a path from one entrance to any of the exits, I used the
    Breadth-first search (BFS) algorithm.
     */
    public static int getSolarDoomsDaySolution(int[] entrances, int[] exits, int[][] path) {
        return determineMaxFlowWithFordFulkerson(
                Arrays.stream(entrances).boxed().collect(Collectors.toList()),
                Arrays.stream(exits).boxed().collect(Collectors.toList()),
                path
        );
    }

    // Normal breadth-first search algo, that returns the exit for the first path found, otherwise -1.
    public static int bfs(List<Integer> entrances, List<Integer> exits, List<Integer> parents, int[][] path) {
        int nbrNodes = path.length;

        List<Boolean> visited = Arrays.asList(new Boolean[nbrNodes]);
        Collections.fill(visited, false);

        Queue<Integer> queue = new LinkedList<>();
        entrances.forEach(e -> {
            queue.add(e);
            visited.set(e, true);
            parents.set(e, -1);
        });

        while (!queue.isEmpty()) {
            int node = queue.poll();

            for (int i = 0; i < nbrNodes; i++) {
                if (!visited.get(i) && path[node][i] > 0) {
                    if (exits.contains(i)) {
                        parents.set(i, node);
                        return i;
                    }

                    queue.add(i);
                    parents.set(i, node);
                    visited.set(i, true);
                }
            }
        }

        return -1;
    }

    public static int determineMaxFlowWithFordFulkerson(List<Integer> entrances, List<Integer> exits, int path[][]) {
        int totalRooms = path.length;
        int[][] pathFlow = new int[totalRooms][totalRooms];

        // Fill path flow counter with default values from problem
        for (int i = 0; i < totalRooms; i++) {
            for (int j = 0; j < totalRooms; j++) {
                pathFlow[i][j] = path[i][j];
            }
        }

        List<Integer> parents = Arrays.asList(new Integer[totalRooms]);

        int maxFlow = 0;
        int lastExit = bfs(entrances, exits, parents, path);

        while (lastExit >= 0) {
            int pathMaxFlow = Integer.MAX_VALUE;

            // Find flow bottleneck for current path
            for (int i = lastExit; !entrances.contains(i); i = parents.get(i)) {
                pathMaxFlow = Math.min(pathMaxFlow, path[parents.get(i)][i]);
            }

            // Apply the bottleneck to each connection along the path
            for (int i = lastExit; !entrances.contains(i); i = parents.get(i)) {
                int parent = parents.get(i);
                path[parent][i] -= pathMaxFlow;
                path[i][parent] += pathMaxFlow;
            }

            // Update max flow
            maxFlow += pathMaxFlow;

            lastExit = bfs(entrances, exits, parents, path);
        }

        return maxFlow;
    }
}
