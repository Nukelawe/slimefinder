package slimefinder.core.search;

import slimefinder.core.ExtremumMask;
import slimefinder.core.Mask;
import slimefinder.core.TrackableTask;
import slimefinder.io.CLI;
import slimefinder.io.DataLogger;
import slimefinder.io.properties.MaskProperties;
import slimefinder.io.properties.SearchProperties;
import static slimefinder.util.FormatHelper.*;

import java.io.IOException;

import slimefinder.io.IDataLogger;

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

    public SearchTask(
        SearchProperties searchProperties,
        MaskProperties maskProperties
    ) {
        this.pSearch = searchProperties;
        this.pMask = maskProperties;
        this.logger = new DataLogger(pSearch, CLI.getCLI());
        this.path = new SearchPath(pSearch.posChunk, pSearch.minWidth, pSearch.maxWidth);
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
            logger.start();
            search();
        } catch (IOException | InterruptedException e) {
        } finally {
            logger.close();
            stop();
        }
    }

    private void search() throws IOException, InterruptedException {
        if (pSearch.fineSearch) {
            for (int xIn = 0; xIn < 16; xIn++) {
                for (int zIn = 0; zIn < 16; zIn++) {
                    chunkSearch(xIn, zIn);
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
    private void chunkSearch(int xIn, int zIn) throws IOException, InterruptedException {
        while (path.step()) {
            if (m == null) {
                initializeMasks(xIn, zIn);
            } else {
                m.moveTo(path.getPosition().x, path.getPosition().z, xIn, zIn);
                ExtremumMask[] extrema = {maxBlock, maxChunk, minBlock, minChunk};
                for (ExtremumMask extremum : extrema) if (extremum.needsUpdate(m)) extremum.moveTo(m);
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
        if (pSearch.fineSearch) count *= 256;
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
            pSearch.minBlockSize + " <= blockSize <= " + pSearch.maxBlockSize + " or " +
            pSearch.minChunkSize + " <= chunkSize <= " + pSearch.maxChunkSize + LN +
            "Saving results in file: '" + pSearch.resultsFile + "'";
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
            formatTime(time) + " elapsed, " +
            formatTime((long) (time / progress -  time)) + " remaining";
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
            "smallest block size: " + minBlock.getBlockSize() + "/" + minBlock.getBlockSurfaceArea() + " at " +
                chunkPos(minBlock) + " (" + blockPos(minBlock) + ")" + LN +
            "largest  block size: " + maxBlock.getBlockSize() + "/" + maxBlock.getBlockSurfaceArea() + " at " +
                chunkPos(maxBlock) + " (" + blockPos(maxBlock) + ")" + LN +
            "smallest chunk size: " + minChunk.getChunkSize() + "/" + minChunk.getChunkSurfaceArea() + " at " +
                chunkPos(minChunk) + " (" + blockPos(minChunk) + ")" + LN +
            "largest  chunk size: " + maxChunk.getChunkSize() + "/" + maxChunk.getChunkSurfaceArea() + " at " +
                chunkPos(maxChunk) + " (" + blockPos(maxChunk) + ")";
    }
}
