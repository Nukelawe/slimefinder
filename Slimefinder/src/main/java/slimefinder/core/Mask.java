package slimefinder.core;

import java.util.Collection;
import java.util.HashSet;

import slimefinder.io.properties.MaskProperties;
import slimefinder.util.*;

import static slimefinder.io.properties.MaskProperties.*;

public class Mask {

    /**
     * The radius of the mask in chunks. This defines the neighborhood of
     * the mask as a square of side length 2 * R_CHUNK + 1
     */
    public static final int R_CHUNK = 8;

    /**
     * The squares of the exclusion sphere and despawn sphere radii
     */
    private int rExclusion, rDespawn;

    /**
     * Total surface area under the mask in blocks and chunks
     */
    private int blockSurfaceArea, chunkSurfaceArea;
    /**
     * Total surface area of slime chunks under the mask in blocks and in chunks
     */
    private int blockSize, chunkSize;
    
    private boolean needsWeightUpdate, needsSizeUpdate;

    /**
     * Block, chunk and within-chunk positions of the center of the mask
     */
    public Position pos;

    /**
     * Each chunk in the neighborhood of the mask is given a weight that 
     * represents the number of blocks in the chunk that are inside the mask.
     * A weight 256 means that the entire chunk is inside the mask and 0
     * that the entire chunk is outside.
     */
    private int[][] chunkWeights;
    
    /**
     * The collection of slime chunks in the neighborhood of this mask. Each slime chunk is listed by its
     * absolute chunk coordinates.
     */
    private Collection<Point> slimeChunks;

    private MaskProperties pMask;

    private long worldSeed;
    private int chunkWeight;
    private boolean exclusionSphere, despawnSphere;

    private Mask(MaskProperties pMask) {
        this.pMask = pMask;
        int yOffset = pMask.getInt(OFFSET);
        worldSeed = pMask.getLong(SEED);
        chunkWeight = pMask.getInt(WEIGHT);
        exclusionSphere = pMask.getBoolean(EXCLUSION);
        despawnSphere = pMask.getBoolean(DESPAWN);
        slimeChunks = new HashSet<>();
        chunkWeights = new int[2 * R_CHUNK + 1][2 * R_CHUNK + 1];
        rExclusion = 24 * 24 - Math.min(yOffset * yOffset, 24 * 24);
        rDespawn = 128 * 128 - yOffset * yOffset;
        pos = new Position(0, 0);
        needsWeightUpdate = true;
        needsSizeUpdate = true;
    }
    
    public Mask(MaskProperties pMask, int blockX, int blockZ) {
        this(pMask);
        moveTo(blockX, blockZ);
    }

    public Mask(MaskProperties pMask, int chunkX, int chunkZ, int inX, int inZ) {
        this(pMask);
        moveTo(chunkX, chunkZ, inX, inZ);
    }

    /**
     * Creates a new mask with the properties of m at the position of m.
     * @param m
     */
    public Mask(Mask m) {
        this(m.pMask);
        moveTo(m);
    }

    public void moveTo(int blockX, int blockZ) {
        needsWeightUpdate = needsWeightUpdate || !(pos.in.x == (blockX & 15) && pos.in.z == (blockZ & 15));
        needsSizeUpdate = needsSizeUpdate || !(pos.block.x == blockX && pos.block.z == blockZ);
        pos.setPos(blockX, blockZ);
        update();
    }

    public void moveTo(int chunkX, int chunkZ, int inX, int inZ) {
        needsWeightUpdate = needsWeightUpdate || !(pos.in.x == inX && pos.in.z == inZ);
        needsSizeUpdate = needsSizeUpdate || !(pos.block.x == chunkX * 16 + inX && pos.block.z == chunkZ * 16 + inZ);
        pos.setPos(chunkX, chunkZ, inX, inZ);
        update();
    }

    public void moveTo(Position to) {
        moveTo(to.chunk.x, to.chunk.z, to.in.x, to.in.z);
    }

    /**
     * Sets the position of this mask to that of m. Copies all mask properties from m.
     * The weights and slime chunk neighborhood are not updated.
     *
     * @param m
     */
    public void moveTo(Mask m) {
        pos.setPos(m.pos);
        blockSize = m.blockSize;
        chunkSize = m.chunkSize;
        blockSurfaceArea = m.blockSurfaceArea;
        chunkSurfaceArea = m.chunkSurfaceArea;
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
                slimeChunks.remove(new Point(xChunk + pos.chunk.x, zChunk + pos.chunk.z));
            }
        }
        pos.moveBy(16, d);

        for (i = -R_CHUNK; i <= R_CHUNK; i++) {
            xChunk = (i * d.z) + d.x * R_CHUNK;
            zChunk = (i * d.x) + d.z * R_CHUNK;
            if (isSlimeChunk(xChunk, zChunk)) {
                slimeChunks.add(new Point(xChunk + pos.chunk.x, zChunk + pos.chunk.z));
            }
        }

        blockSize = 0;
        chunkSize = 0;
        for (Point point : slimeChunks) {
            blockSize += chunkWeights[point.x - pos.chunk.x + R_CHUNK][point.z - pos.chunk.z + R_CHUNK];
            if (isChunkInside(point.x- pos.chunk.x, point.z - pos.chunk.z)) {
                ++chunkSize;
            }
        }
    }

    public void update() {
        if (needsWeightUpdate) updateWeights();
        if (needsSizeUpdate) updateSize();
    }

    /**
     * Calculates the weight array that determines the weights for each chunk.
     * Calculates the surface area of this mask. This has to be called
     * whenever the position within the chunk (posIn) changes.
     */
    private void updateWeights() {
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
        needsWeightUpdate = false;
    }

    /**
     * Calculates the number of slime chunks in blocks and in chunks under the
     * mask.
     */
    private void updateSize() {
        int xChunk, zChunk;
        blockSize = 0;
        chunkSize = 0;
        slimeChunks.clear();
        for (xChunk = -R_CHUNK; xChunk <= R_CHUNK; xChunk++) {
            for (zChunk = -R_CHUNK; zChunk <= R_CHUNK; zChunk++) {
                if (isSlimeChunk(xChunk, zChunk)) {
                    slimeChunks.add(new Point(xChunk + pos.chunk.x, zChunk + pos.chunk.z));
                    blockSize += chunkWeights[xChunk + R_CHUNK][zChunk + R_CHUNK];
                    if (isChunkInside(xChunk, zChunk)) {
                        ++chunkSize;
                    }
                }
            }
        }
        needsSizeUpdate = false;
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
     * Checks if the block at position (xBlock,zBlock) with respect to the center of the
     * mask is inside the mask. To be considered inside the position has to be
     * within the despawn sphere and outside the exclusion sphere.
     *
     * @param xBlock
     * @param zBlock
     * @return true if the block at coordinates xBlock,zBlock is within the mask, false
     * otherwise
     */
    public boolean isBlockInside(int xBlock, int zBlock) {
        int dsqr = (xBlock - pos.in.x) * (xBlock - pos.in.x) + (zBlock - pos.in.z) * (zBlock - pos.in.z);
        if (despawnSphere && dsqr > rDespawn) {
            return false;
        }
        if (exclusionSphere && dsqr <= rExclusion) {
            return false;
        }
        return true;
    }
    
    /**
     * Determines if the chunk at (xChunk,zChunk) with respect to this mask's center
     * is a slime chunk.
     *
     * @param xChunk - relative chunk xBlock coordinate
     * @param zChunk - relative chunk zBlock coordinate
     * @return true if (xChunk, zChunk) with respect to the mask is a slime chunk, false otherwise
     */
    public boolean isSlimeChunk(int xChunk, int zChunk) {
        return Slimefinder.isSlimeChunk(worldSeed, pos.chunk.x + xChunk, pos.chunk.z + zChunk);
    }

    public int getBlockSurfaceArea() {
        return blockSurfaceArea;
    }

    public int getChunkSurfaceArea() {
        return chunkSurfaceArea;
    }

    public int getBlockSize() {
        return blockSize;
    }

    public int getChunkSize() {
        return chunkSize;
    }
}
