package slimefinder.core.mask;

import slimefinder.io.properties.MaskProperties;
import slimefinder.util.Position;

public class CeilMask extends AbstractMask {

    public CeilMask(MaskProperties pMask, int chunkX, int chunkZ) {
        super(pMask, chunkX, chunkZ);
    }

    public CeilMask(MaskProperties pMask, Position pos) {
        super(pMask, pos);
    }

    @Override
    public boolean isBlockInside(int xBlock, int zBlock) {
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int dsqr = (xBlock - x) * (xBlock - x) + (zBlock - z) * (zBlock - z);
                if (despawnSphere && dsqr > rDespawn || exclusionSphere && dsqr <= rExclusion) continue;
                return true;
            }
        }
        return false;
    }
}
