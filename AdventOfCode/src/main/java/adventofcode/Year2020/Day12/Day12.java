package adventofcode.Year2020.Day12;

import adventofcode.utils.Day;
import adventofcode.utils.Vector2d;
import adventofcode.utils.enums.Direction;

import java.io.IOException;
import java.util.List;

public class Day12 extends Day<Integer> {

    public static void main(String[] args) throws IOException {
        Day12 day = new Day12();

        day.printPart1("sample-input", 25);
        day.printPart1("input", 1603);

        day.printPart2("sample-input", 286);
        day.printPart2("input", 52866);
    }

    @Override
    public Integer part1(List<String> lines) throws IOException {
        Ship ship = new Ship(0, 0, Direction.EAST);
        for (String instruction : lines) {
            ship = ship.handleInstruction(instruction);
        }
        return Math.abs(ship.i()) + Math.abs(ship.j());
    }

    @Override
    public Integer part2(List<String> lines) throws IOException {
        Vector2d ship = new Vector2d(0, 0);
        Vector2d waypoint = new Vector2d(10, 1);
        for (String instruction : lines) {
            char action = instruction.charAt(0);
            int value = Integer.parseInt(instruction.substring(1));
            if (action == 'F') {
                ship = new Vector2d(ship.x() + waypoint.x() * value, ship.y() + waypoint.y() * value);
            } else {
                waypoint = moveWaypoint(waypoint, action, value);
            }
        }
        return Math.abs(ship.x()) + Math.abs(ship.y());
    }

    public Vector2d moveWaypoint(Vector2d waypoint, char action, int value) {
        return switch (action) {
            case 'N' -> new Vector2d(waypoint.x(), waypoint.y() + value);
            case 'S' -> new Vector2d(waypoint.x(), waypoint.y() - value);
            case 'E' -> new Vector2d(waypoint.x() + value, waypoint.y());
            case 'W' -> new Vector2d(waypoint.x() - value, waypoint.y());
            case 'L' -> switch (value) {
                case 90 -> new Vector2d(-waypoint.y(), waypoint.x());
                case 180 -> new Vector2d(-waypoint.x(), -waypoint.y());
                case 270 -> new Vector2d(waypoint.y(), -waypoint.x());
                default -> null;
            };
            case 'R' -> switch (value) {
                case 90 -> new Vector2d(waypoint.y(), -waypoint.x());
                case 180 -> new Vector2d(-waypoint.x(), -waypoint.y());
                case 270 -> new Vector2d(-waypoint.y(), waypoint.x());
                default -> null;
            };
            default -> waypoint;
        };
    }

    public record Ship(int i, int j, Direction direction) {
        public Ship handleInstruction(String instruction) {
            char action = instruction.charAt(0);
            int value = Integer.parseInt(instruction.substring(1));
            return switch (action) {
                case 'N' -> new Ship(i - value, j, direction);
                case 'S' -> new Ship(i + value, j, direction);
                case 'E' -> new Ship(i, j + value, direction);
                case 'W' -> new Ship(i, j - value, direction);
                case 'L' -> new Ship(i, j, direction.rotateLeft(value / 90));
                case 'R' -> new Ship(i, j, direction.rotateRight(value / 90));
                case 'F' -> move(value);
                default -> this;
            };
        }

        public Ship move(int value) {
            return switch (direction) {
                case NORTH -> new Ship(i - value, j, direction);
                case SOUTH -> new Ship(i + value, j, direction);
                case EAST -> new Ship(i, j + value, direction);
                case WEST -> new Ship(i, j - value, direction);
            };
        }
    }
}
