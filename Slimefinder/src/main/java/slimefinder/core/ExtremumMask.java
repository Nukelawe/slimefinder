package slimefinder.core;

public abstract class ExtremumMask extends Mask {
    public ExtremumMask(Mask m) {
        super(m);
    }

    public void moveTo(Mask m) {
        super.moveTo(m);
    }


    public abstract boolean needsUpdate(Mask mask);
}