package slimefinder.core;

import slimefinder.core.mask.AbstractMask;
import slimefinder.core.mask.MaskData;
import slimefinder.util.Point;

public abstract class ExtremumData extends MaskData {

    public ExtremumData(AbstractMask m) {
        chunk = new Point(0,0);
        collectData(m);
    }

    public abstract boolean needsUpdate(AbstractMask mask);
}