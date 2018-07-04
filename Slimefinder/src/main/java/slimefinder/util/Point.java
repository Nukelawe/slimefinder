package slimefinder.util;

import java.util.Objects;

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

    public void moveBy(int count, Direction d) {
        setPoint(x + count * d.x, z + count * d.z);
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
