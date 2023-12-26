package adventofcode.Year2020.Day17;

import adventofcode.utils.Day;
import adventofcode.utils.Utilities;
import adventofcode.utils.Vector3d;
import adventofcode.utils.Vector4d;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class Day17 extends Day<Long> {

    public static void main(String[] args) throws IOException {
        Day17 day = new Day17();

        day.printPart1("sample-input", 112L);
        day.printPart1("input", 215L);

        day.printPart2("sample-input", 848L);
        day.printPart2("input", 1728L);
    }

    @Override
    public Long part1(List<String> lines) throws IOException {
        Set<Vector3d> activeCubes = parseInput3d(lines);

        for (int i = 0; i < 6; i++) {
            activeCubes = performCycle3d(activeCubes);
        }

        return (long) activeCubes.size();
    }

    @Override
    public Long part2(List<String> lines) throws IOException {
        Set<Vector4d> activeCubes = parseInput4d(lines);

        for (int i = 0; i < 6; i++) {
            activeCubes = performCycle4d(activeCubes);
        }

        return (long) activeCubes.size();
    }

    public Set<Vector3d> performCycle3d(Set<Vector3d> activeCubes) {
        Set<Vector3d> visited = new HashSet<>();
        Set<Vector3d> newActiveCubes = new HashSet<>();
        for (Vector3d activeCube : activeCubes) {
            // Check active cubes
            int activeNeighbors = getActiveNeighbors3d(activeCube, activeCubes);
            if (activeNeighbors == 2 || activeNeighbors == 3) {
                newActiveCubes.add(activeCube);
            }
            // Check inactive cubes
            for (Vector3d neighbor : getNeighbors3d(activeCube)) {
                if (!activeCubes.contains(neighbor) && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    if (getActiveNeighbors3d(neighbor, activeCubes) == 3) {
                        newActiveCubes.add(neighbor);
                    }
                }
            }
        }
        return newActiveCubes;
    }

    public Set<Vector4d> performCycle4d(Set<Vector4d> activeCubes) {
        Set<Vector4d> visited = new HashSet<>();
        Set<Vector4d> newActiveCubes = new HashSet<>();
        for (Vector4d activeCube : activeCubes) {
            // Check active cubes
            int activeNeighbors = getActiveNeighbors4d(activeCube, activeCubes);
            if (activeNeighbors == 2 || activeNeighbors == 3) {
                newActiveCubes.add(activeCube);
            }
            // Check inactive cubes
            for (Vector4d neighbor : getNeighbors4d(activeCube)) {
                if (!activeCubes.contains(neighbor) && !visited.contains(neighbor)) {
                    visited.add(neighbor);
                    if (getActiveNeighbors4d(neighbor, activeCubes) == 3) {
                        newActiveCubes.add(neighbor);
                    }
                }
            }
        }
        return newActiveCubes;
    }

    private int getActiveNeighbors3d(Vector3d cube, Set<Vector3d> activeCubes) {
        Set<Vector3d> neighbors = getNeighbors3d(cube);
        int activeNeighbors = 0;
        for (Vector3d neighbor : neighbors) {
            if (activeCubes.contains(neighbor)) {
                activeNeighbors++;
            }
        }
        return activeNeighbors;
    }

    private int getActiveNeighbors4d(Vector4d cube, Set<Vector4d> activeCubes) {
        Set<Vector4d> neighbors = getNeighbors4d(cube);
        int activeNeighbors = 0;
        for (Vector4d neighbor : neighbors) {
            if (activeCubes.contains(neighbor)) {
                activeNeighbors++;
            }
        }
        return activeNeighbors;
    }

    public Set<Vector3d> getNeighbors3d(Vector3d cube) {
        return IntStream.range(0, Utilities.NEIGHBORS_26_X.length)
            .mapToObj(k -> new Vector3d(
                cube.x() + Utilities.NEIGHBORS_26_X[k],
                cube.y() + Utilities.NEIGHBORS_26_Y[k],
                cube.z() + Utilities.NEIGHBORS_26_Z[k]))
            .collect(Collectors.toSet());
    }

    public Set<Vector4d> getNeighbors4d(Vector4d cube) {
        // Do it with an IntStream
        Set<Vector4d> neighbors = new HashSet<>();
        for (int w = -1; w <= 1; w++) {
            for (int x = -1; x <= 1 ; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        if (w == 0 && x == 0 && y == 0 && z == 0) {
                            continue;
                        }
                        neighbors.add(new Vector4d(
                            cube.x() + x,
                            cube.y() + y,
                            cube.z() + z,
                            cube.w() + w));
                    }
                }
            }
        }
        return neighbors;
    }

    public Set<Vector3d> parseInput3d(List<String> lines) {
        Set<Vector3d> activeCubes = new HashSet<>();
        for (int y = 0; y < lines.size(); y++) {
            for (int x = 0; x < lines.get(0).length(); x++) {
                if (lines.get(y).charAt(x) == '#') {
                    activeCubes.add(new Vector3d(x, y, 0));
                }
            }
        }
        return activeCubes;
    }

    public Set<Vector4d> parseInput4d(List<String> lines) {
        return parseInput3d(lines).stream().map(cube -> new Vector4d(cube.x(), cube.y(), cube.z(), 0)).collect(Collectors.toSet());
    }
}
