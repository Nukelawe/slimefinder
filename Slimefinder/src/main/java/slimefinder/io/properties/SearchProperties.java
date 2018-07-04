package slimefinder.io.properties;

import slimefinder.util.Position;

import static slimefinder.core.Mask.R_CHUNK;

public class SearchProperties extends AbstractProperties {
    
    public static final String CENTER_POS = "center-pos";
    public static final String MIN_WIDTH = "min-width";
    public static final String MAX_WIDTH = "max-width";
    public static final String FINE_SEARCH = "fine-search";
    public static final String MIN_BLOCK_SZ = "min-block-size";
    public static final String MAX_BLOCK_SZ = "max-block-size";
    public static final String MIN_CHUNK_SZ = "min-chunk-size";
    public static final String MAX_CHUNK_SZ = "max-chunk-size";
    public static final String RESULTS = "output-file";
    public static final String APPEND = "append";

    public SearchProperties() {
        super("search");
    }

    protected void setDefaults() {
        defaultValues.put(APPEND, false);
        defaultValues.put(FINE_SEARCH, false);
        defaultValues.put(RESULTS, "results.csv");
        defaultValues.put(CENTER_POS, new Position(0, 0));
        defaultValues.put(MAX_WIDTH, 1);
        defaultValues.put(MIN_WIDTH, 0);
        defaultValues.put(MAX_BLOCK_SZ, (2 * R_CHUNK + 1) * (2 * R_CHUNK + 1) * 256);
        defaultValues.put(MIN_BLOCK_SZ, 0);
        defaultValues.put(MAX_CHUNK_SZ, (2 * R_CHUNK + 1) * (2 * R_CHUNK + 1));
        defaultValues.put(MIN_CHUNK_SZ, 0);
    }
}
