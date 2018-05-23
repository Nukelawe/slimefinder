package slimefinder.search;

public abstract class Extremum {
    private final String info;
    public Mask mask;
    public abstract boolean needsUpdate(Mask mask);

    public Extremum(String infoText, Mask m) {
        this.info = infoText;
        this.mask = new Mask(m);
    }

    public String getInfo() {
        return info;
    }
}