package slimefinder.search;

import slimefinder.properties.MaskProperties;
import slimefinder.properties.SearchProperties;

import java.io.IOException;
import slimefinder.cli.Logger;

public class Search {

    public Extremum maxBlock, minBlock, maxChunk, minChunk;
    
    private final MaskProperties pMask;
    
    private final SearchProperties pSearch;
    
    private final Logger logger;

    private String extremumMessage;
    
    private long successCount;
    
    private Mask m;
    
    public Search(SearchProperties searchProperties, MaskProperties maskProperties, Logger logger) {
        pSearch = searchProperties;
        pMask = maskProperties;
        this.logger = logger;
        extremumMessage = "";
    }

    /**
     * Searches the area specified by the search properties for slime chunk
     * clusters that match given criteria.
     *
     * @throws java.io.IOException
     */
    public void search() throws IOException {
        logger.start();
        successCount = 0;
        if (pSearch.fineSearch) {
            for (int xIn = 0; xIn < 16; xIn++) {
                for (int zIn = 0; zIn < 16; zIn++) {
                    chunkSearch(xIn, zIn);
                }
            }
        } else {
            chunkSearch(pSearch.posIn.x, pSearch.posIn.z);
        }
        logger.stop();
    }

    /**
     * Searches only one block position in each chunk. Never moves the mask
     * within the chunk.
     *
     * @throws java.io.IOException
     */
    private void chunkSearch(int xIn, int zIn) throws IOException {
        SearchPath path = new SearchPath(pSearch.posChunk, pSearch.minWidth, pSearch.maxWidth);
        while (path.step()) {
            logger.progressBar.stepBy(1);
            if (m == null) {
                m = new Mask(pMask, path.getPosition().x, path.getPosition().z, xIn, zIn);
            } else {
                m.moveTo(path.getPosition().x, path.getPosition().z, xIn, zIn);
            }
            updateExtrema();
            if (matchesSearchCriteria()) {
                ++successCount;
                logger.write(Logger.formatData(m) + extremumMessage);
                logger.progressBar.setExtraMessage(successCount + " matches");
            }
        }
    }
    
    private boolean matchesSearchCriteria() {
        int chunkSize = m.getChunkSize();
        int blockSize = m.getBlockSize();
        boolean chunkSizeCriteria = chunkSize >= pSearch.minChunkSize && chunkSize <= pSearch.maxChunkSize;
        boolean blockSizeCriteria = blockSize >= pSearch.minBlockSize && blockSize <= pSearch.maxBlockSize;
        return chunkSizeCriteria || blockSizeCriteria;
    }
    
    private void updateExtrema() {
        extremumMessage = "";
        if (maxBlock == null) initializeExtrema();
        Extremum[] extrema = {maxBlock, minBlock, maxChunk, minChunk};
        for (Extremum extremum : extrema) {
            if (extremum.needsUpdate(m)) {
                extremum.mask.moveTo(m);
                appendExtremumMessage(extremum.getInfo());
            }
        }
    }

    private void initializeExtrema() {
        maxBlock = new Extremum("maxB", m) {
            @Override
            public boolean needsUpdate(Mask mask) {
                return this.mask.getBlockSize() < mask.getBlockSize();
            }
        };
        minBlock = new Extremum("minB", m) {
            @Override
            public boolean needsUpdate(Mask mask) {
                return this.mask.getBlockSize() > mask.getBlockSize();
            }
        };
        maxChunk = new Extremum("maxC", m) {
            @Override
            public boolean needsUpdate(Mask mask) {
                return this.mask.getChunkSize() < mask.getChunkSize();
            }
        };
        minChunk = new Extremum("minC", m) {
            @Override
            public boolean needsUpdate(Mask mask) {
                return this.mask.getChunkSize() > mask.getChunkSize();
            }
        };
        
        Extremum[] extrema = {maxBlock, minBlock, maxChunk, minChunk};
        for (Extremum extremum : extrema) {
            appendExtremumMessage(extremum.getInfo());
        }
    }
    
    /**
     * Builds a comma separated list of extremum information
     * @param str - extremum info to be appended
     */
    private void appendExtremumMessage(String str) {
        extremumMessage += ((extremumMessage.length() > 0) ? "," : "") + str;
    }
}
