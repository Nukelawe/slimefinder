package slimefinder.properties;

public class ImageProperties extends AbstractProperties {

    public static final String INPUT_FILE = "input-file";
    public static final String OUTPUT_DIR = "output-dir";
    public static final String BLOCK_WIDTH = "block-width";
    public static final String GRID_WIDTH = "grid-width";
    public static final String DRAW_SLIME_CHUNKS = "draw-slime-chunks";
    public static final String DRAW_BLOCK_MASK = "draw-block-mask";
    public static final String DRAW_CHUNK_MASK = "draw-chunk-mask";
    public static final String DRAW_CENTER = "draw-center";
    
    public int wBlock = 5, wGrid = 1;
    public boolean drawSlimeChunks = true, drawBlockMask = true, drawChunkMask = true, drawCenter = true;
    public String inputFile = "results.dat", outputDir = "images";

    public ImageProperties() {
        filename = "image.properties";
        defaults.setProperty(INPUT_FILE, inputFile);
        defaults.setProperty(OUTPUT_DIR, outputDir);
        defaults.setProperty(GRID_WIDTH, "" + wGrid);
        defaults.setProperty(BLOCK_WIDTH, "" + wBlock);
        defaults.setProperty(DRAW_SLIME_CHUNKS, "" + drawSlimeChunks);
        defaults.setProperty(DRAW_BLOCK_MASK, "" + drawBlockMask);
        defaults.setProperty(DRAW_CHUNK_MASK, "" + drawChunkMask);
        defaults.setProperty(DRAW_CENTER, "" + drawCenter);
    }

    @Override
    protected void parseProperties() {
        inputFile = properties.getProperty(INPUT_FILE);
        outputDir = properties.getProperty(OUTPUT_DIR);
        
        try {
            wBlock = Integer.parseInt(properties.getProperty(BLOCK_WIDTH));
        } catch (NumberFormatException ex) {
            parsingError(BLOCK_WIDTH);
        }
        
        try {
            wGrid = Integer.parseInt(properties.getProperty(GRID_WIDTH));
        } catch (NumberFormatException ex) {
            parsingError(GRID_WIDTH);
        }
        
        drawSlimeChunks = Boolean.parseBoolean(properties.getProperty(DRAW_SLIME_CHUNKS));
        properties.setProperty(DRAW_SLIME_CHUNKS, "" + drawSlimeChunks);
        
        drawBlockMask = Boolean.parseBoolean(properties.getProperty(DRAW_BLOCK_MASK));
        properties.setProperty(DRAW_BLOCK_MASK, "" + drawBlockMask);
        
        drawChunkMask = Boolean.parseBoolean(properties.getProperty(DRAW_CHUNK_MASK));
        properties.setProperty(DRAW_CHUNK_MASK, "" + drawChunkMask);
        
        drawCenter = Boolean.parseBoolean(properties.getProperty(DRAW_CENTER));
        properties.setProperty(DRAW_CENTER, "" + drawCenter);
    }

}
