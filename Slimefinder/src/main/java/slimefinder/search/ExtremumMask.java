package slimefinder.search;

import slimefinder.Mask;

public abstract class ExtremumMask extends Mask {
    public ExtremumMask(Mask m) {
        super(m);
    }

    public void moveTo(Mask m) {
        if (needsUpdate(m)) {
            super.moveTo(m);
        }
    }

    protected abstract boolean needsUpdate(Mask mask);
}