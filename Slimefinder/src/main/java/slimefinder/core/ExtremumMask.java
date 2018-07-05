package slimefinder.core;

import slimefinder.util.Position;

public abstract class ExtremumMask {
    /**
     * Block, chunk and within-chunk positions of the center of the mask
     */
    public Position pos;
    public int blockSize, chunkSize, blockSurfaceArea, chunkSurfaceArea;

    public ExtremumMask(Mask m) {
        pos = new Position(0,0);
        setPos(m);
    }

    public void setPos(Mask m) {
        pos.setPos(m.pos);
        blockSize = m.getBlockSize();
        chunkSize = m.getChunkSize();
        blockSurfaceArea = m.getBlockSurfaceArea();
        chunkSurfaceArea = m.getChunkSurfaceArea();
    }

    public abstract boolean needsUpdate(Mask mask);
}