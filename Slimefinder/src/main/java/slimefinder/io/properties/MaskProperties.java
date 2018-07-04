package slimefinder.io.properties;

public class MaskProperties extends AbstractProperties {

    public static final String SEED = "world-seed";
    public static final String DESPAWN = "despawn-sphere";
    public static final String EXCLUSION = "exclusion-sphere";
    public static final String OFFSET = "y-offset";
    public static final String WEIGHT = "chunk-weight";

    public MaskProperties() {
        super("mask");
    }

    protected void setDefaults() {
        defaultValues.put(SEED, 0L);
        defaultValues.put(DESPAWN, true);
        defaultValues.put(EXCLUSION, true);
        defaultValues.put(OFFSET, 0);
        defaultValues.put(WEIGHT, 0);
    }
}
