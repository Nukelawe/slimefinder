package slimefinder.core.search;

import java.io.IOException;

import slimefinder.core.ExtremumMask;
import slimefinder.core.Mask;
import slimefinder.core.TrackableTask;
import slimefinder.io.properties.MaskProperties;
import slimefinder.io.properties.SearchProperties;
import slimefinder.io.IDataLogger;
import slimefinder.util.Position;

import static slimefinder.io.properties.SearchProperties.*;
import static slimefinder.util.FormatHelper.*;

public class SearchTask extends TrackableTask {

    private ExtremumMask maxBlock, minBlock, maxChunk, minChunk;
    private final MaskProperties pMask;
    public final SearchProperties pSearch;
    private final SearchPath path;
    private final IDataLogger logger;
    private long matches;
    private Mask m;
    private int progress;
    private boolean interrupted;

    private final boolean fineSearch;
    private final String resultsFile;
    private final int minBlockSize, maxBlockSize, minChunkSize, maxChunkSize;
    private final Position centerPos;

    public SearchTask(
        SearchProperties pSearch,
        MaskProperties pMask,
        IDataLogger logger
    ) {
        fineSearch = pSearch.getBoolean(FINE_SEARCH);
        resultsFile = pSearch.getString(RESULTS);
        minBlockSize = pSearch.getInt(MIN_BLOCK_SZ);
        maxBlockSize = pSearch.getInt(MAX_BLOCK_SZ);
        minChunkSize = pSearch.getInt(MIN_CHUNK_SZ);
        maxChunkSize = pSearch.getInt(MAX_CHUNK_SZ);
        centerPos = pSearch.getPosition(CENTER_POS);
        int minWidth = pSearch.getInt(MIN_WIDTH);
        int maxWidth = pSearch.getInt(MAX_WIDTH);
        this.pSearch = pSearch;
        this.pMask = pMask;
        this.logger = logger;
        this.path = new SearchPath(centerPos.chunk, minWidth, maxWidth);
    }

    /**
     * Searches the area specified by the search properties for slime chunk
     * clusters that match given criteria.
     */
    @Override
    public void run() {
        setStartTime();
        matches = 0;
        try {
            logger.start(resultsFile, pSearch.getBoolean(APPEND));
            search();
        } catch (IOException | InterruptedException e) {
        } finally {
            logger.close();
            stop();
        }
    }

    private void search() throws IOException, InterruptedException {
        if (fineSearch) {
            for (int inX = 0; inX < 16; inX++) {
                for (int inZ = 0; inZ < 16; inZ++) {
                    chunkSearch(inX, inZ);
                }
            }
        } else {
            chunkSearch(centerPos.in.x, centerPos.in.z);
        }
    }

    /**
     * Searches only one block position in each chunk. Never moves the mask
     * within the chunk.
     */
    private void chunkSearch(int inX, int inZ) throws IOException, InterruptedException {
        while (path.step()) {
            if (m == null) {
                initializeMasks(inX, inZ);
            } else {
                m.moveTo(path.getPoint().x, path.getPoint().z, inX, inZ);
                ExtremumMask[] extrema = {maxBlock, maxChunk, minBlock, minChunk};
                for (ExtremumMask extremum : extrema) if (extremum.needsUpdate(m)) extremum.setPos(m);
            }
            if (matchesSearchCriteria(m.getChunkSize(), m.getBlockSize())) {
                ++matches;
                logger.write(m);
            }
            if (interrupted) throw new InterruptedException(); //TODO: implement runtime interrupt
        }
        ++progress;
    }

    /**
     * @param chunkSize
     * @param blockSize
     * @return true if a mask with the given chunk and block sizes matches the given search criteria.
     * The mask matches if either the block or the chunk size is within the the range determined by the search
     * properties.
     */
    private boolean matchesSearchCriteria(int chunkSize, int blockSize) {
        boolean chunkSizeCriteria = chunkSize >= minChunkSize && chunkSize <= maxChunkSize;
        boolean blockSizeCriteria = blockSize >= minBlockSize && blockSize <= maxBlockSize;
        return chunkSizeCriteria || blockSizeCriteria;
    }

    private void initializeMasks(int inX, int inZ) {
        m = new Mask(pMask, path.getPoint().x, path.getPoint().z, inX, inZ);
        maxBlock = new ExtremumMask(m) {
            @Override
            public boolean needsUpdate(Mask mask) { return this.blockSize < mask.getBlockSize(); }
        };
        minBlock = new ExtremumMask(m) {
            @Override
            public boolean needsUpdate(Mask mask) { return this.blockSize > mask.getBlockSize(); }
        };
        maxChunk = new ExtremumMask(m) {
            @Override
            public boolean needsUpdate(Mask mask) { return this.chunkSize < mask.getChunkSize(); }
        };
        minChunk = new ExtremumMask(m) {
            @Override
            public boolean needsUpdate(Mask mask) { return this.chunkSize > mask.getChunkSize(); }
        };
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

    /**
     * @return total number of positions searched so far.
     */
    public synchronized long positionsChecked() {
        return path.getProgress() + this.progress * path.getPathLength();
    }

    /**
     * @return total number of positions searched once the search is complete.
     */
    public synchronized long positionsTotal() {
        long count = path.getPathLength();
        if (fineSearch) count *= 256;
        return count;
    }

    /**
     * @return total number of positions matching the search criteria given so far
     */
    public synchronized long matches() {
        return matches;
    }

    /**
     * Searching mask positions with criteria: 0 <= blockSize <= -1 or 35 <= chunkSize <= 289
     * Saving results in file: 'results.csv'
     */
    public String startInfo() {
        return
            "Searching mask positions with criteria: " +
            minBlockSize + " <= blockSize <= " + maxBlockSize + " or " +
            minChunkSize + " <= chunkSize <= " + maxChunkSize + LN +
            "Saving results to file: '" + resultsFile + "'";
    }

    /**
     * [  82.1% ]  2 matches, 54777 of 256000000 positions checked, 00:00:09 elapsed, 43:12:55 remaining
     */
    public synchronized String progressInfo() {
        long checked = positionsChecked();
        long total = positionsTotal();
        long time = getDuration();
        float progress = (float) checked / total;
        return
            "[ " + String.format("%1$5.1f", progress * 100) + "% ]  " +
            matches() + " matches, " +
            checked + " of " + total + " positions checked, " +
            timeFormat(time) + " elapsed, " +
            timeFormat((long) (time / progress -  time)) + " remaining";
    }

    /**
     * 459441 nanoseconds per position
     * smallest block size: 132/79000 at 252:12,-1599:3 (15032,-45201)
     * largest  block size: 132/79000 at 252:12,-1599:3 (15032,-45201)
     * smallest chunk size: 132/79000 at 252:12,-1599:3 (15032,-45201)
     * largest  chunk size: 132/79000 at 252:12,-1599:3 (15032,-45201)
     */
    public String endInfo() {
        if (positionsChecked() <= 0) return "";
        return
            getDuration() / positionsChecked() + " nanoseconds per position" + LN +
            "smallest block size: " + minBlock.blockSize + "/" + minBlock.blockSurfaceArea + " at " +
                chunkFormat(minBlock.pos) + " (" + blockFormat(minBlock.pos) + ")" + LN +
            "largest  block size: " + maxBlock.blockSize + "/" + maxBlock.blockSurfaceArea + " at " +
                chunkFormat(maxBlock.pos) + " (" + blockFormat(maxBlock.pos) + ")" + LN +
            "smallest chunk size: " + minChunk.chunkSize + "/" + minChunk.chunkSurfaceArea + " at " +
                chunkFormat(minChunk.pos) + " (" + blockFormat(minChunk.pos) + ")" + LN +
            "largest  chunk size: " + maxChunk.chunkSize + "/" + maxChunk.chunkSurfaceArea + " at " +
                chunkFormat(maxChunk.pos) + " (" + blockFormat(maxChunk.pos) + ")";
    }
}
