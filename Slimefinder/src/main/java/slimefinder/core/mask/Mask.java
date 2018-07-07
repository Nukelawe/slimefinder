package slimefinder.core.mask;

import slimefinder.io.properties.MaskProperties;
import slimefinder.util.*;

public class Mask extends AbstractMask {

    private boolean needsWeightUpdate;

    public Mask(MaskProperties pMask, int chunkX, int chunkZ, int inX, int inZ) {
        super(pMask);
        in = new Point(inX, inZ);
        needsWeightUpdate = true;
        moveTo(chunkX, chunkZ, inX, inZ);
    }

    public Mask(MaskProperties pMask, Position pos) {
        this(pMask, pos.chunk.x, pos.chunk.z, pos.in.x, pos.in.z);
    }

    public void moveTo(int chunkX, int chunkZ, int inX, int inZ) {
        needsWeightUpdate = needsWeightUpdate || !(in.x == inX && in.z == inZ);
        chunk.setPoint(chunkX, chunkZ);
        in.setPoint(inX, inZ);
        if (needsWeightUpdate) updateWeights();
        updateSize();
        needsWeightUpdate = false;
    }

    @Override
    public void moveTo(Position to) {
        moveTo(to.chunk.x, to.chunk.z, to.in.x, to.in.z);
    }

    @Override
    public boolean isBlockInside(int blockX, int blockZ) {
        int dsqr = (blockX - in.x) * (blockX - in.x) + (blockZ - in.z) * (blockZ - in.z);
        if (despawnSphere && dsqr > rDespawn) return false;
        if (exclusionSphere && dsqr <= rExclusion) return false;
        return true;
    }
}
