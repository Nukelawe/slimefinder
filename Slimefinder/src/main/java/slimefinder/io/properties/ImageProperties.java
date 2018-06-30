package slimefinder.io.properties;

import slimefinder.io.CLI;

import java.io.IOException;

public class ImageProperties extends AbstractProperties {

    public static final String INPUT_FILE = "input-file";
    public static final String OUTPUT_DIR = "output-dir";
    public static final String BLOCK_WIDTH = "block-width";
    public static final String GRID_WIDTH = "grid-width";
    public static final String DRAW_SLIME_CHUNKS = "draw-slime-chunks";
    public static final String DRAW_BLOCK_MASK = "draw-block-mask";
    public static final String DRAW_CHUNK_MASK = "draw-chunk-mask";
    public static final String DRAW_CENTER = "draw-center";
    
    public int wBlock, wGrid;
    public boolean drawSlimeChunks, drawBlockMask, drawChunkMask, drawCenter;
    public String inputFile, outputDir;

    public ImageProperties() {
    }

    public ImageProperties(String filename, CLI cli) throws IOException {
        super(filename, cli);
    }

    protected void setDefaults() {
        defaultValues.put(INPUT_FILE, "results.csv");
        defaultValues.put(OUTPUT_DIR, "images");
        defaultValues.put(GRID_WIDTH, "" + 1);
        defaultValues.put(BLOCK_WIDTH, "" + 1);
        defaultValues.put(DRAW_SLIME_CHUNKS, "" + true);
        defaultValues.put(DRAW_BLOCK_MASK, "" + true);
        defaultValues.put(DRAW_CHUNK_MASK, "" + true);
        defaultValues.put(DRAW_CENTER, "" + true);
    }

    @Override
    protected void parseProperties() {
        inputFile = this.getProperty(INPUT_FILE);
        outputDir = this.getProperty(OUTPUT_DIR);

        try {
            wBlock = Integer.parseInt(this.getProperty(BLOCK_WIDTH));
        } catch (NumberFormatException ex) {
            parsingError(BLOCK_WIDTH);
        }

        try {
            wGrid = Integer.parseInt(this.getProperty(GRID_WIDTH));
        } catch (NumberFormatException ex) {
            parsingError(GRID_WIDTH);
        }

        drawSlimeChunks = Boolean.parseBoolean(this.getProperty(DRAW_SLIME_CHUNKS));
        this.setProperty(DRAW_SLIME_CHUNKS, "" + drawSlimeChunks);

        drawBlockMask = Boolean.parseBoolean(this.getProperty(DRAW_BLOCK_MASK));
        this.setProperty(DRAW_BLOCK_MASK, "" + drawBlockMask);

        drawChunkMask = Boolean.parseBoolean(this.getProperty(DRAW_CHUNK_MASK));
        this.setProperty(DRAW_CHUNK_MASK, "" + drawChunkMask);

        drawCenter = Boolean.parseBoolean(this.getProperty(DRAW_CENTER));
        this.setProperty(DRAW_CENTER, "" + drawCenter);
    }

}
