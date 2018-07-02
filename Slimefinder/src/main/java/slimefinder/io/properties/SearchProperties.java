package slimefinder.io.properties;

import slimefinder.core.Mask;
import slimefinder.io.CLI;
import slimefinder.util.Position;

import java.io.IOException;

public class SearchProperties extends AbstractProperties {
    
    public static final String START_POS = "start-pos";
    public static final String MIN_WIDTH = "min-width";
    public static final String MAX_WIDTH = "max-width";
    public static final String FINE_SEARCH = "fine-search";
    public static final String MIN_BLOCK_SZ = "min-block-size";
    public static final String MAX_BLOCK_SZ = "max-block-size";
    public static final String MIN_CHUNK_SZ = "min-chunk-size";
    public static final String MAX_CHUNK_SZ = "max-chunk-size";
    public static final String RESULTS = "output-file";
    public static final String APPEND = "append";
    
    public boolean append, fineSearch;
    public int minWidth, maxWidth;
    public int minChunkSize, maxChunkSize;
    public int minBlockSize, maxBlockSize;
    public String resultsFile;
    public Position posChunk, posIn;

    public SearchProperties() {
    }

    public SearchProperties(String filename, CLI cli) throws IOException {
        super(filename, cli);
    }

    protected void setDefaults() {
        defaultValues.put(APPEND, "" + false);
        defaultValues.put(FINE_SEARCH, "" + false);
        defaultValues.put(RESULTS, "results.csv");
        defaultValues.put(START_POS, new Position(0,0).toString());
        defaultValues.put(MAX_WIDTH, "" + 1);
        defaultValues.put(MIN_WIDTH, "" + 0);
        defaultValues.put(MAX_BLOCK_SZ, "" + (2 * Mask.R_CHUNK + 1) * (2 * Mask.R_CHUNK + 1) * 256);
        defaultValues.put(MIN_BLOCK_SZ, "" + 0);
        defaultValues.put(MAX_CHUNK_SZ, "" + (2 * Mask.R_CHUNK + 1) * (2 * Mask.R_CHUNK + 1));
        defaultValues.put(MIN_CHUNK_SZ, "" + 0);
    }

    @Override
    protected void parseProperties() {
        try {
            Position posBlock = Position.parsePos(this.getProperty(START_POS));
            posChunk = Position.origin();
            posIn = Position.origin();
            posChunk.setPos(Math.floorDiv(posBlock.x, 16), Math.floorDiv(posBlock.z, 16));
            posIn.setPos(posBlock.x & 15, posBlock.z & 15);
        } catch (NumberFormatException ex) {
            parsingError(START_POS);
        }
        
        resultsFile = this.getProperty(RESULTS);
        append = Boolean.parseBoolean(this.getProperty(APPEND));
        fineSearch = Boolean.parseBoolean(this.getProperty(FINE_SEARCH));
        
        try {
            maxWidth = Integer.parseInt(this.getProperty(MAX_WIDTH));
        } catch (NumberFormatException ex) {
            parsingError(MAX_WIDTH);
        }
        
        try {
            minWidth = Integer.parseInt(this.getProperty(MIN_WIDTH));
        } catch (NumberFormatException ex) {
            parsingError(MIN_WIDTH);
        }
        
        try {
            minBlockSize = Integer.parseInt(this.getProperty(MIN_BLOCK_SZ));
        } catch (NumberFormatException ex) {
            parsingError(MIN_BLOCK_SZ);
        }
        
        try {
            maxBlockSize = Integer.parseInt(this.getProperty(MAX_BLOCK_SZ));
        } catch (NumberFormatException ex) {
            parsingError(MAX_BLOCK_SZ);
        }
        
        try {
            minChunkSize = Integer.parseInt(this.getProperty(MIN_CHUNK_SZ));
        } catch (NumberFormatException ex) {
            parsingError(MIN_CHUNK_SZ);
        }
        
        try {
            maxChunkSize = Integer.parseInt(this.getProperty(MAX_CHUNK_SZ));
        } catch (NumberFormatException ex) {
            parsingError(MAX_CHUNK_SZ);
        }
    }
}
