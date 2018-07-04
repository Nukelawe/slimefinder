package slimefinder.io.properties;

public class ImageProperties extends AbstractProperties {

    public static final String INPUT_FILE = "input-file";
    public static final String OUTPUT_DIR = "output-dir";
    public static final String BLOCK_WIDTH = "block-width";
    public static final String GRID_WIDTH = "grid-width";
    public static final String DRAW_SLIME_CHUNKS = "draw-slime-chunks";
    public static final String DRAW_BLOCK_MASK = "draw-block-mask";
    public static final String DRAW_CHUNK_MASK = "draw-chunk-mask";
    public static final String DRAW_CENTER = "draw-center";

    public ImageProperties() {
        super("image");
    }

    protected void setDefaults() {
        defaultValues.put(INPUT_FILE, "results.csv");
        defaultValues.put(OUTPUT_DIR, "images");
        defaultValues.put(GRID_WIDTH, 1);
        defaultValues.put(BLOCK_WIDTH, 1);
        defaultValues.put(DRAW_SLIME_CHUNKS, true);
        defaultValues.put(DRAW_BLOCK_MASK, true);
        defaultValues.put(DRAW_CHUNK_MASK, true);
        defaultValues.put(DRAW_CENTER, true);
    }
}
