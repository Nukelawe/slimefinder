package slimefinder.search;

import slimefinder.Mask;
import slimefinder.cli.TrackableTask;
import slimefinder.properties.MaskProperties;
import slimefinder.properties.SearchProperties;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import slimefinder.cli.DataLogger;

public class Search extends TrackableTask {

    private ExtremumMask maxBlock, minBlock, maxChunk, minChunk;
    private final MaskProperties pMask;
    private final SearchProperties pSearch;
    private final SearchPath path;
    private final DataLogger logger;
    private long successCount;
    private Mask m;
    private int progress;

    public Search(SearchProperties searchProperties, MaskProperties maskProperties, DataLogger logger) {
        this.pSearch = searchProperties;
        this.pMask = maskProperties;
        this.logger = logger;
        this.path = new SearchPath(pSearch.posChunk, pSearch.minWidth, pSearch.maxWidth);
    }

    /**
     * Searches the area specified by the search properties for slime chunk
     * clusters that match given criteria.
     */
    @Override
    public void run() {
        setStartTime();
        successCount = 0;
        try {
            logger.start();
            search();
        } catch (IOException e) {
        } finally {
            logger.close();
            stop();
        }
    }

    public void search() throws IOException {
        if (pSearch.fineSearch) {
            for (int xIn = 0; xIn < 16; xIn++) {
                for (int zIn = 0; zIn < 16; zIn++) {
                    chunkSearch(xIn, zIn);
                    ++progress;
                }
            }
        } else {
            chunkSearch(pSearch.posIn.x, pSearch.posIn.z);
        }
    }

    /**
     * Searches only one block position in each chunk. Never moves the mask
     * within the chunk.
     */
    private void chunkSearch(int xIn, int zIn) throws IOException {
        path.initPosition();
        do {
            if (m == null) {
                initializeMasks(xIn, zIn);
            } else {
                m.moveTo(path.getPosition().x, path.getPosition().z, xIn, zIn);
                ExtremumMask[] extrema = {maxBlock, maxChunk, minBlock, minChunk};
                for (ExtremumMask extremum : extrema) extremum.moveTo(m);
            }
            if (matchesSearchCriteria(m.getChunkSize(), m.getBlockSize(), pSearch)) {
                ++successCount;
                logger.write(DataLogger.formatData(m));
            }
        } while (path.step());
    }

    /**
     * @param chunkSize
     * @param blockSize
     * @param pSearch
     * @return true if a mask with the given chunk and block sizes matches the given search criteria.
     * The mask matches if either the block or the chunk size is within the the range determined by the search
     * properties.
     */
    static boolean matchesSearchCriteria(int chunkSize, int blockSize, SearchProperties pSearch) {
        boolean chunkSizeCriteria = chunkSize >= pSearch.minChunkSize && chunkSize <= pSearch.maxChunkSize;
        boolean blockSizeCriteria = blockSize >= pSearch.minBlockSize && blockSize <= pSearch.maxBlockSize;
        return chunkSizeCriteria || blockSizeCriteria;
    }

    private void initializeMasks(int xIn, int zIn) {
        m = new Mask(pMask, path.getPosition().x, path.getPosition().z, xIn, zIn);
        maxBlock = new ExtremumMask(m) {
            @Override
            public boolean needsUpdate(Mask mask) { return this.getBlockSize() < mask.getBlockSize(); }
        };
        minBlock = new ExtremumMask(m) {
            @Override
            public boolean needsUpdate(Mask mask) { return this.getBlockSize() > mask.getBlockSize(); }
        };
        maxChunk = new ExtremumMask(m) {
            @Override
            public boolean needsUpdate(Mask mask) { return this.getChunkSize() < mask.getChunkSize(); }
        };
        minChunk = new ExtremumMask(m) {
            @Override
            public boolean needsUpdate(Mask mask) { return this.getChunkSize() > mask.getChunkSize(); }
        };
    }

    /**
     * @return total number of positions searched.
     */
    public synchronized long getProgress() {
        return path.getProgress() + this.progress * path.getPathLength();
    }

    public synchronized String getProgressInfo() {
        return successCount + " matches";
    }

    /**
     * @return total number of positions searched once the search is complete.
     */
    public synchronized long getMaxProgress() {
        long count = path.getPathLength();
        if (pSearch.fineSearch) count *= 256;
        return count;
    }

    public synchronized ExtremumMask getMaxBlock() {
        return maxBlock;
    }

    public synchronized ExtremumMask getMinBlock() {
        return minBlock;
    }

    public synchronized ExtremumMask getMaxChunk() {
        return maxChunk;
    }

    public synchronized ExtremumMask getMinChunk() {
        return minChunk;
    }
}
