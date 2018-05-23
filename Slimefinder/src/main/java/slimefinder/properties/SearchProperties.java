package slimefinder.properties;

import slimefinder.search.Mask;
import slimefinder.util.Position;

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
    
    public boolean append = false, fineSearch = false;
    public int minWidth = 0, maxWidth = 1;
    public int minChunkSize = 0, maxChunkSize = (2 * Mask.R_CHUNK + 1) * (2 * Mask.R_CHUNK + 1);
    public int minBlockSize = 0, maxBlockSize = maxChunkSize * 256;
    public String resultsFile = "results.dat";
    public Position posChunk = Position.origin(), posIn = Position.origin();

    public SearchProperties() {
        filename = "search.properties";
        defaults.setProperty(START_POS, new Position(posChunk.x * 16 + posIn.x, posChunk.z * 16 + posIn.z).toString());
        defaults.setProperty(APPEND, "" + append);
        defaults.setProperty(FINE_SEARCH, "" + fineSearch);
        defaults.setProperty(RESULTS, resultsFile);
        defaults.setProperty(MAX_WIDTH, "" + maxWidth);
        defaults.setProperty(MIN_WIDTH, "" + minWidth);
        defaults.setProperty(MAX_CHUNK_SZ, "" + maxChunkSize);
        defaults.setProperty(MIN_CHUNK_SZ, "" + minChunkSize);
        defaults.setProperty(MAX_BLOCK_SZ, "" + maxBlockSize);
        defaults.setProperty(MIN_BLOCK_SZ, "" + minBlockSize);
    }

    @Override
    protected void parseProperties() {
        try {
            Position posBlock = Position.parsePos(properties.getProperty(START_POS));
            posChunk.setPos(Math.floorDiv(posBlock.x, 16), Math.floorDiv(posBlock.z, 16));
            posIn.setPos(posBlock.x & 15, posBlock.z & 15);
        } catch (NumberFormatException ex) {
            parsingError(START_POS);
        }
        
        resultsFile = properties.getProperty(RESULTS);
        append = Boolean.parseBoolean(properties.getProperty(APPEND));
        fineSearch = Boolean.parseBoolean(properties.getProperty(FINE_SEARCH));
        
        try {
            maxWidth = Integer.parseInt(properties.getProperty(MAX_WIDTH));
        } catch (NumberFormatException ex) {
            parsingError(MAX_WIDTH);
        }
        
        try {
            minWidth = Integer.parseInt(properties.getProperty(MIN_WIDTH));
        } catch (NumberFormatException ex) {
            parsingError(MIN_WIDTH);
        }
        
        try {
            minBlockSize = Integer.parseInt(properties.getProperty(MIN_BLOCK_SZ));
        } catch (NumberFormatException ex) {
            parsingError(MIN_BLOCK_SZ);
        }
        
        try {
            maxBlockSize = Integer.parseInt(properties.getProperty(MAX_BLOCK_SZ));
        } catch (NumberFormatException ex) {
            parsingError(MAX_BLOCK_SZ);
        }
        
        try {
            minChunkSize = Integer.parseInt(properties.getProperty(MIN_CHUNK_SZ));
        } catch (NumberFormatException ex) {
            parsingError(MIN_CHUNK_SZ);
        }
        
        try {
            maxChunkSize = Integer.parseInt(properties.getProperty(MAX_CHUNK_SZ));
        } catch (NumberFormatException ex) {
            parsingError(MAX_CHUNK_SZ);
        }
    }
}
