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
        String message = "Failed to parse position from string: '" + parseText + "'\nExpected format 'xBlock,zBlock' or 'xChunk:xIn,zChunk:zIn'.";
        parseText = parseText.trim();
        if (parseText == null) {
            throw new NumberFormatException(message);
        }
        if (parseText.equals("")) {
            throw new NumberFormatException(message);
        }
        String[] coords = parseText.split(",", 2);
        if (coords.length != 2) {
            throw new NumberFormatException(message);
        }
        String[] xCoord = coords[0].split(":", 2);
        String[] zCoord = coords[1].split(":", 2);
        try {
            if (xCoord.length == 1 && zCoord.length == 1) {
                return new Position(Integer.parseInt(xCoord[0]), Integer.parseInt(zCoord[0]));
            } else if (xCoord.length == 2 && zCoord.length == 2) {
                return new Position(
                        Integer.parseInt(xCoord[0]) * 16 + Integer.parseInt(xCoord[1]),
                        Integer.parseInt(zCoord[0]) * 16 + Integer.parseInt(zCoord[1]));
            } else {
                throw new NumberFormatException(message);
            }
        } catch (NumberFormatException e) {
            throw new NumberFormatException(message + e.getMessage());
        }
    }

    public Position(int x, int z) {
        setPos(x, z);
    }

    /**
     * Moves the position n steps in the direction specified
     *
     * @param n - amount of movement
     * @param d - direction of movement
     */
    public void move(int n, Direction d) {
        x += n * d.x;
        z += n * d.z;
    }

    /**
     * Sets the coordinates of the position object
     *
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
