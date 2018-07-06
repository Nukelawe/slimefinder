package slimefinder.core;

import slimefinder.core.mask.AbstractMask;
import slimefinder.core.mask.MaskData;
import slimefinder.util.Point;

public abstract class ExtremumData extends MaskData {

    public ExtremumData(AbstractMask m) {
        chunk = new Point(0,0);
        collectData(m);
    }

    public void collectData(AbstractMask m) {
        chunk.setPoint(m.chunk);
        if (m.in != null) {
            if (in != null) {
                in.setPoint(m.in);
            } else {
                in = new Point(m.in);
            }
        }
        blockSize = m.blockSize;
        chunkSize = m.chunkSize;
        blockSurfaceArea = m.blockSurfaceArea;
        chunkSurfaceArea = m.chunkSurfaceArea;
    }

    public abstract boolean needsUpdate(AbstractMask mask);
}