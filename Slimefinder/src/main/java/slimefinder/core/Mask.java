package slimefinder.core;

import java.util.Collection;
import java.util.HashSet;

import slimefinder.io.properties.MaskProperties;
import slimefinder.util.*;

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
    public Position posBlock, posChunk, posIn;

    /**
     * Each chunk in the neighborhood of the mask is given a weight that 
     * represents the number of blocks in the chunk that are inside the mask.
     * A weight 256 means that the entire chunk is inside the mask and 0
     * that the entire chunk is outside.
     */
    private int[][] chunkWeights;
    
    /**
     * An object containing the user defined parameters
     */
    private MaskProperties pMask;
    
    /**
     * The collection of slime chunks in the neighborhood of this mask. Each slime chunk is listed by its
     * absolute chunk coordinates.
     */
    private Collection<Position> slimeChunks;

    private Mask(MaskProperties pMask) {
        this.pMask = pMask;
        slimeChunks = new HashSet<>();
        chunkWeights = new int[2 * R_CHUNK + 1][2 * R_CHUNK + 1];
        rExclusion = 24 * 24 - Math.min(pMask.yOffset * pMask.yOffset, 24 * 24);
        rDespawn = 128 * 128 - pMask.yOffset * pMask.yOffset;
        posBlock = Position.origin();
        posChunk = Position.origin();
        posIn = Position.origin();
        needsWeightUpdate = true;
        needsSizeUpdate = true;
    }
    
    public Mask(MaskProperties pMask, int xBlock, int zBlock) {
        this(pMask);
        moveTo(Math.floorDiv(xBlock, 16), Math.floorDiv(zBlock, 16), xBlock & 15, zBlock & 15);
    }
    
    public Mask(MaskProperties pMask, int xChunk, int zChunk, int xIn, int zIn) {
        this(pMask);
        moveTo(xChunk, zChunk, xIn, zIn);
    }

    /**
     * Creates a new mask with the properties of m at the position of m.
     * @param m
     */
    public Mask(Mask m) {
        this(m.pMask);
        moveTo(m);
    }

    /**
     * Sets the position of the mask in block coordinates.
     *
     * @param xChunk
     * @param zChunk
     * @param xIn
     * @param zIn
     */
    public void moveTo(int xChunk, int zChunk, int xIn, int zIn) {
        needsWeightUpdate = needsWeightUpdate || !(posIn.x == xIn && posIn.z == zIn);
        needsSizeUpdate = needsSizeUpdate || !(posBlock.x == 16 * xChunk + xIn && posBlock.z == 16 * zChunk + zIn);
        posBlock.setPos(16 * xChunk + xIn, zChunk * 16 + zIn);
        posChunk.setPos(xChunk, zChunk);
        posIn.setPos(xIn, zIn);

        if (needsWeightUpdate) {
            updateWeights();
        }

        if (needsSizeUpdate) {
            updateSize();
        }
    }

    /**
     * Sets the position of this mask to that of m. Copies all mask properties from m.
     * The weights and slime chunk neighborhood are not updated.
     *
     * @param m
     */
    public void moveTo(Mask m) {
        posBlock.setPos(m.posBlock.x, m.posBlock.z);
        posChunk.setPos(m.posChunk.x, m.posChunk.z);
        posIn.setPos(m.posIn.x, m.posIn.z);
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
                slimeChunks.remove(new Position(xChunk + posChunk.x, zChunk + posChunk.z));
            }
        }

        posChunk.move(1, d);
        posBlock.move(16, d);

        for (i = -R_CHUNK; i <= R_CHUNK; i++) {
            xChunk = (i * d.z) + d.x * R_CHUNK;
            zChunk = (i * d.x) + d.z * R_CHUNK;
            if (isSlimeChunk(xChunk, zChunk)) {
                slimeChunks.add(new Position(xChunk + posChunk.x, zChunk + posChunk.z));
            }
        }

        blockSize = 0;
        chunkSize = 0;
        for (Position pos : slimeChunks) {
            blockSize += chunkWeights[pos.x - posChunk.x + R_CHUNK][pos.z - posChunk.z + R_CHUNK];
            if (isChunkInside(pos.x - posChunk.x, pos.z - posChunk.z)) {
                ++chunkSize;
            }
        }
    }

    /**
     * Calculates the weight array that determines the weights for each chunk.
     * Calculates the surface area of this mask. This has to be called
     * whenever the position within the chunk (posIn) changes.
     */
    private void updateWeights() {
        int xIn, zIn, xChunk, zChunk, chunkWeight;
        blockSurfaceArea = 0;
        chunkSurfaceArea = 0;
        for (xChunk = -R_CHUNK; xChunk <= R_CHUNK; xChunk++) {
            for (zChunk = -R_CHUNK; zChunk <= R_CHUNK; zChunk++) {
                chunkWeight = 0;
                for (xIn = 0; xIn <= 15; xIn++) {
                    for (zIn = 0; zIn <= 15; zIn++) {
                        if (isBlockInside(16 * xChunk + xIn, 16 * zChunk + zIn)) {
                            ++chunkWeight;
                        }
                    }
                }
                chunkWeights[xChunk + R_CHUNK][zChunk + R_CHUNK] = chunkWeight;
                blockSurfaceArea += chunkWeight;
                if (chunkWeight > pMask.chunkWeight) {
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
                    slimeChunks.add(new Position(xChunk + posChunk.x, zChunk + posChunk.z));
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
        return chunkWeights[xChunk + R_CHUNK][zChunk + R_CHUNK] > pMask.chunkWeight;
    }

    /**
     * Checks if the block at position (x,z) with respect to the center of the
     * mask is inside the mask. To be considered inside the position has to be
     * within the despawn sphere and outside the exclusion sphere.
     *
     * @param x
     * @param z
     * @return true if the block at coordinates x,z is within the mask, false 
     * otherwise
     */
    public boolean isBlockInside(int x, int z) {
        int dsqr = (x - posIn.x) * (x - posIn.x) + (z - posIn.z) * (z - posIn.z);
        if (pMask.despawnSphere && dsqr > rDespawn) {
            return false;
        }
        if (pMask.exclusionSphere && dsqr <= rExclusion) {
            return false;
        }
        return true;
    }
    
    /**
     * Determines if the chunk at (xChunk,zChunk) with respect to this mask's center
     * is a slime chunk.
     *
     * @param xChunk - relative chunk x coordinate
     * @param zChunk - relative chunk z coordinate
     * @return true if (xChunk, zChunk) with respect to the mask is a slime chunk, false otherwise
     */
    public boolean isSlimeChunk(int xChunk, int zChunk) {
        return Slimefinder.isSlimeChunk(pMask.worldSeed, posChunk.x + xChunk, posChunk.z + zChunk);
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
