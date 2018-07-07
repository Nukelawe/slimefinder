package slimefinder.core.mask;

import slimefinder.core.Slimefinder;
import slimefinder.io.properties.MaskProperties;
import slimefinder.util.Direction;
import slimefinder.util.Point;
import slimefinder.util.Position;

import java.util.Collection;
import java.util.HashSet;

import static slimefinder.io.properties.MaskProperties.*;
import static slimefinder.io.properties.MaskProperties.DESPAWN;

public abstract class AbstractMask extends MaskData {

    /**
     * The radius of the mask in chunks. This defines the neighborhood of
     * the mask as a square of side length 2 * R_CHUNK + 1
     */
    public static final int R_CHUNK = 8;

    /**
     * The squares of the exclusion sphere and despawn sphere radii
     */
    protected int rExclusion, rDespawn;

    /**
     * Each chunk in the neighborhood of the mask is given a weight that
     * represents the number of blocks in the chunk that are inside the mask.
     * A weight 256 means that the entire chunk is inside the mask and 0
     * that the entire chunk is outside.
     */
    protected int[][] chunkWeights;

    /**
     * The collection of slime chunks in the neighborhood of this mask. Each slime chunk is listed by its
     * absolute chunk coordinates.
     */
    protected Collection<Point> slimeChunks;

    protected long worldSeed;
    protected int chunkWeight;
    protected boolean exclusionSphere, despawnSphere;

    protected AbstractMask(MaskProperties pMask) {
        worldSeed = pMask.getLong(SEED);
        chunkWeight = pMask.getInt(WEIGHT);
        exclusionSphere = pMask.getBoolean(EXCLUSION);
        despawnSphere = pMask.getBoolean(DESPAWN);
        int yOffset = pMask.getInt(OFFSET);

        slimeChunks = new HashSet<>();
        chunkWeights = new int[2 * R_CHUNK + 1][2 * R_CHUNK + 1];
        rExclusion = 24 * 24 - Math.min(yOffset * yOffset, 24 * 24);
        rDespawn = 128 * 128 - yOffset * yOffset;
        chunk = new Point(0, 0);
        in = null;
    }

    public AbstractMask(MaskProperties pMask, int chunkX, int chunkZ) {
        this(pMask);
        updateWeights();
        moveToChunk(chunkX, chunkZ);
    }

    public AbstractMask(MaskProperties pMask, Position pos) {
        this(pMask);
        moveToChunk(pos.chunk.x, pos.chunk.z);
    }

    public void moveToChunk(int chunkX, int chunkZ) {
        chunk.setPoint(chunkX, chunkZ);
        updateSize();
    }

    public void moveTo(Position to) {
        moveToChunk(to.chunk.x, to.chunk.z);
    }

    /**
     * Moves the mask by 1 chunk in the given direction. Recalculates the number
     * of slime chunks using a more efficient algorithm.
     *
     * @param d - direction of movement
     */
    public void moveByChunk(Direction d) {
        int i, xChunk, zChunk;
        for (i = -R_CHUNK; i <= R_CHUNK; i++) {
            xChunk = (i * d.z) - d.x * R_CHUNK;
            zChunk = (i * d.x) - d.z * R_CHUNK;
            if (isSlimeChunk(xChunk, zChunk)) {
                slimeChunks.remove(new Point(xChunk + chunk.x, zChunk + chunk.z));
            }
        }
        chunk.moveBy(1, d);

        for (i = -R_CHUNK; i <= R_CHUNK; i++) {
            xChunk = (i * d.z) + d.x * R_CHUNK;
            zChunk = (i * d.x) + d.z * R_CHUNK;
            if (isSlimeChunk(xChunk, zChunk)) {
                slimeChunks.add(new Point(xChunk + chunk.x, zChunk + chunk.z));
            }
        }

        blockSize = 0;
        chunkSize = 0;
        for (Point point : slimeChunks) {
            blockSize += chunkWeights[point.x - chunk.x + R_CHUNK][point.z - chunk.z + R_CHUNK];
            if (isChunkInside(point.x- chunk.x, point.z - chunk.z)) {
                ++chunkSize;
            }
        }
    }

    /**
     * Calculates the weight array that determines the weights for each chunk.
     * Calculates the surface area of this mask. This has to be called
     * whenever the position within the chunk (posIn) changes.
     */
    protected void updateWeights() {
        int xIn, zIn, xChunk, zChunk, weight;
        blockSurfaceArea = 0;
        chunkSurfaceArea = 0;
        for (xChunk = -R_CHUNK; xChunk <= R_CHUNK; xChunk++) {
            for (zChunk = -R_CHUNK; zChunk <= R_CHUNK; zChunk++) {
                weight = 0;
                for (xIn = 0; xIn <= 15; xIn++) {
                    for (zIn = 0; zIn <= 15; zIn++) {
                        if (isBlockInside(16 * xChunk + xIn, 16 * zChunk + zIn)) {
                            ++weight;
                        }
                    }
                }
                chunkWeights[xChunk + R_CHUNK][zChunk + R_CHUNK] = weight;
                blockSurfaceArea += weight;
                if (weight > chunkWeight) {
                    ++chunkSurfaceArea;
                }
            }
        }
    }

    /**
     * Calculates the number of slime chunks in blocks and in chunks under the
     * mask.
     */
    protected void updateSize() {
        int xChunk, zChunk;
        blockSize = 0;
        chunkSize = 0;
        slimeChunks.clear();
        for (xChunk = -R_CHUNK; xChunk <= R_CHUNK; xChunk++) {
            for (zChunk = -R_CHUNK; zChunk <= R_CHUNK; zChunk++) {
                if (isSlimeChunk(xChunk, zChunk)) {
                    slimeChunks.add(new Point(xChunk + chunk.x, zChunk + chunk.z));
                    blockSize += chunkWeights[xChunk + R_CHUNK][zChunk + R_CHUNK];
                    if (isChunkInside(xChunk, zChunk)) {
                        ++chunkSize;
                    }
                }
            }
        }
    }

    /**
     * Checks if the chunk at position (xChunk,zChunk) with respect to the
     * center of the mask is inside the mask. To be considered inside the number
     * of blocks in the chunk that are inside the mask has to be larger than the
     * chunk weight. The chunk weight can be adjusted in the
     * properties-file. It can have values in the range [0, 255].
     *
     * @param xChunk
     * @param zChunk
     * @return true if the chunk is considered to be inside the mask
     */
    public boolean isChunkInside(int xChunk, int zChunk) {
        return chunkWeights[xChunk + R_CHUNK][zChunk + R_CHUNK] > chunkWeight;
    }

    /**
     * Checks if the block at position (blockX,blockZ) with respect to the center of the
     * mask is inside the mask. To be considered inside the position has to be
     * within the despawn sphere and outside the exclusion sphere.
     *
     * @param blockX
     * @param blockZ
     * @return true if the block at coordinates blockX,blockZ is within the mask, false
     * otherwise
     */
    public abstract boolean isBlockInside(int blockX, int blockZ);

    /**
     * Determines if the chunk at (chunkX,chunkZ) with respect to this mask's center
     * is a slime chunk.
     *
     * @param chunkX - relative chunk xBlock coordinate
     * @param chunkZ - relative chunk zBlock coordinate
     * @return true if (chunkX, chunkZ) with respect to the mask is a slime chunk, false otherwise
     */
    public boolean isSlimeChunk(int chunkX, int chunkZ) {
        return Slimefinder.isSlimeChunk(worldSeed, chunk.x + chunkX, chunk.z + chunkZ);
    }
}