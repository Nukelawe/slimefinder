package slimefinder.core.mask;

import slimefinder.io.properties.MaskProperties;
import slimefinder.util.Position;

public class FloorMask extends AbstractMask {

    public FloorMask(MaskProperties pMask, int chunkx, int chunkZ) {
        super(pMask, chunkx, chunkZ);
    }

    public FloorMask(MaskProperties pMask, Position pos) {
        super(pMask, pos);
    }

    @Override
    public boolean isBlockInside(int blockX, int blockZ) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int dsqr = (blockX - x) * (blockX - x) + (blockZ - z) * (blockZ - z);
                if (despawnSphere && dsqr > rDespawn || exclusionSphere && dsqr <= rExclusion) return false;
            }
        }
        return true;
    }
}
