package adventofcode.utils.enums;

public enum Direction {
    NORTH, EAST, SOUTH, WEST;

    public Direction rotateLeft(int turns) {
        int totalDirections = Direction.values().length;
        return Direction.values()[(totalDirections + this.ordinal() - turns) % totalDirections];
    }

    public Direction rotateRight(int turns) {
        int totalDirections = Direction.values().length;
        return Direction.values()[(totalDirections + this.ordinal() + turns) % totalDirections];
    }
}
