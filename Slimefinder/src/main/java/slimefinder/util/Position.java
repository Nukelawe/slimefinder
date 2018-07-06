package slimefinder.util;

import java.util.Objects;

/**
 * Position represents a 2-dimensional position in a Minecraft world,
 * storing its block, chunk and within-chunk coordinates.
 */
public final class Position {

    public Point block, chunk, in;

    /**
     * Creates a new Position object by reading a string representation of a position
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
                return new Position(
                    Integer.parseInt(x[0]),
                    Integer.parseInt(z[0])
                );
            } else if (x.length == 2 && z.length == 2) {
                return new Position(
                    Integer.parseInt(x[0]),
                    Integer.parseInt(z[0]),
                    Integer.parseInt(x[1]),
                    Integer.parseInt(z[1])
                );
            }
        } catch (NumberFormatException e) {
        }
        throw exception;
    }

    private Position() {
        block = new Point(0, 0);
        chunk = new Point(0, 0);
        in = new Point(0, 0);
    }

    public Position(int blockX, int blockZ) {
        this();
        setPos(blockX, blockZ);
    }

    public Position(int chunkX, int chunkZ, int inX, int inZ) {
        this();
        setPos(chunkX, chunkZ, inX, inZ);
    }

    public Position(Position pos) {
        this(pos.chunk.x, pos.chunk.z, pos.in.x, pos.in.z);
    }

    public void setPos(int blockX, int blockZ) {
        this.block.setPoint(blockX, blockZ);
        this.chunk.setPoint(Math.floorDiv(blockX, 16), Math.floorDiv(blockZ, 16));
        this.in.setPoint(blockX & 15, blockZ & 15);
    }

    public void setPos(int chunkX, int chunkZ, int inX, int inZ) {
        this.block.setPoint(chunkX * 16 + inX, chunkZ * 16 + inZ);
        this.chunk.setPoint(chunkX, chunkZ);
        this.in.setPoint(inX, inZ);
    }

    public void setPos(Position pos) {
        setPos(pos.block.x, pos.block.z);
    }

    /**
     * Moves the position in the given direction by the given number of blocks
     */
    public void moveBy(int blockCount, Direction direction) {
        setPos(block.x + direction.x * blockCount, block.z + direction.z * blockCount);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(block, position.block);
    }

    @Override
    public int hashCode() {
        return Objects.hash(block);
    }

    @Override
    public String toString() {
        return chunk.x + ":" + in.x + "," + chunk.z + ":" + in.z;
    }
}
