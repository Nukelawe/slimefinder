package slimefinder.util;

public final class Position {

    public static Position origin() {
        return new Position(0, 0);
    }

    public int x, z;

    /**
     * Creates a new Position object by reading a comma separated pair of
     * integers from a string
     *
     * @param parseText - The string to be parsed
     * @return Position object parsed from the string, null if the string is
     * empty or only whitespace.
     * @throws NumberFormatException if parsing the string fails.
     */
    public static Position parsePos(String parseText) throws NumberFormatException {
        NumberFormatException exception = new NumberFormatException(
            "Failed to parse position from string: '" + parseText +
            "'. Expected format 'xBlock,zBlock' or 'xChunk:xIn,zChunk:zIn'"
        );
        if (parseText == null) throw exception;
        parseText = parseText.trim();
        if (parseText.equals("")) throw exception;
        String[] coords = parseText.split(",", 2);
        if (coords.length != 2) throw exception;
        String[] x = coords[0].split(":", -1);
        String[] z = coords[1].split(":", -1);
        try {
            if (x.length == 1 && z.length == 1) {
                return new Position(Integer.parseInt(x[0]), Integer.parseInt(z[0]));
            } else if (x.length == 2 && z.length == 2) {
                return new Position(
                        Integer.parseInt(x[0]) * 16 + Integer.parseInt(x[1]),
                        Integer.parseInt(z[0]) * 16 + Integer.parseInt(z[1])
                );
            }
        } catch (NumberFormatException e) {
        }
        throw exception;
    }

    public Position(int x, int z) {
        setPos(x, z);
    }

    public Position(Position pChunk, Position pIn) {
        setPos(pChunk.x * 16 + pIn.x, pChunk.z * 16 + pIn.z);
    }

    /**
     * Moves the position count steps in the direction specified
     * @param count - amount of movement
     * @param d - direction of movement
     */
    public void move(int count, Direction d) {
        x += count * d.x;
        z += count * d.z;
    }

    /**
     * Sets the coordinates of the position object
     * @param x
     * @param z
     */
    public void setPos(int x, int z) {
        this.x = x;
        this.z = z;
    }

    @Override
    public String toString() {
        return x + "," + z;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + z;
        return result;
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
        Position other = (Position) obj;
        if (x != other.x) {
            return false;
        }
        return z == other.z;
    }
}
