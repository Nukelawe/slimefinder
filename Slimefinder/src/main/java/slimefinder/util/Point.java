package slimefinder.util;

import java.util.Objects;

/**
 * Represents 2 dimensional integer coordinates.
 */
public class Point {

    public int x;
    public int z;

    public Point(int x, int z) {
        setPoint(x, z);
    }

    public Point(Point point) {
        setPoint(point);
    }

    public void setPoint(int x, int z) {
        this.x = x;
        this.z = z;
    }

    public void setPoint(Point point) {
        this.x = point.x;
        this.z = point.z;
    }

    /**
     * Move this point in the given direction by the given amount
     */
    public void moveBy(int count, Direction direction) {
        setPoint(x + count * direction.x, z + count * direction.z);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Point point = (Point) o;
        return x == point.x &&
            z == point.z;
    }

    @Override
    public int hashCode() {
        return Objects.hash(x, z);
    }

    public String toString() {
        return x + "," + z;
    }
}
