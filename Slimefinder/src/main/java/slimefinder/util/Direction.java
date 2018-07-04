package slimefinder.util;

public class Direction {

    /**
     * 1,0
     */
    public static final Direction EAST = new Direction(1, 0);
    /**
     * 0,1
     */
    public static final Direction SOUTH = new Direction(0, 1);
    /**
     * -1,0
     */
    public static final Direction WEST = new Direction(-1, 0);
    /**
     * 0,-1
     */
    public static final Direction NORTH = new Direction(0, -1);

    public int x, z;

    private Direction(int dx, int dz) {
        this.x = (dx == 0) ? 0 : (dx > 0) ? 1 : -1;
        this.z = (dz == 0) ? 0 : (dz > 0) ? 1 : -1;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Direction other = (Direction) obj;
        if (x != other.x) {
            return false;
        }
        if (z != other.z) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 31 * hash + this.x;
        hash = 31 * hash + this.z;
        return hash;
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
