package slimefinder.util;

/**
 * This enum defines objects representing the 4 cardinal directions.
 */
public enum Direction {
    EAST(1, 0),
    SOUTH(0, 1),
    WEST(-1, 0),
    NORTH(0, -1);

    public int x, z;

    Direction(int dx, int dz) {
        this.x = (dx == 0) ? 0 : (dx > 0) ? 1 : -1;
        this.z = (dz == 0) ? 0 : (dz > 0) ? 1 : -1;
    }

    @Override
    public String toString() {
        if (this == Direction.EAST) {
            return "east";
        }
        if (this == Direction.WEST) {
            return "west";
        }
        if (this == Direction.SOUTH) {
            return "south";
        }
        if (this == Direction.NORTH) {
            return "north";
        }
        return "direction(" + this.x + "," + this.z + ")";
    }
}
