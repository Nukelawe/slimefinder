package slimefinder.core.search;

import org.junit.Before;
import org.junit.Test;

import java.util.LinkedList;

import slimefinder.core.mask.Mask;
import slimefinder.core.mask.MaskData;
import slimefinder.io.CLI;
import slimefinder.io.properties.MaskProperties;
import slimefinder.io.properties.SearchProperties;
import slimefinder.io.IDataLogger;
import slimefinder.util.FormatHelper;
import slimefinder.util.Position;

import static org.junit.Assert.*;

import static slimefinder.io.properties.MaskProperties.*;
import static slimefinder.io.properties.SearchProperties.*;
import static slimefinder.util.FormatHelper.LN;

public class SearchTaskTest {

    private SearchProperties pSearch;
    private MaskProperties pMask;
    private TestDataLogger l;
    private CLI cli;

    private class TestDataLogger implements IDataLogger {

        public LinkedList<MaskData> masks;

        public TestDataLogger() {
            masks = new LinkedList<>();
        }

        @Override
        public void start(String filename, boolean append) {

        }

        @Override
        public void close() {

        }

        @Override
        public void write(Mask m) {
            masks.add(new MaskData(m) {});
        }
    }

    @Before
    public void setUp() {
        this.cli = new CLI();

        pMask = new MaskProperties();
        pMask.setProperty(SEED, 0L);
        pMask.setProperty(DESPAWN, true);
        pMask.setProperty(EXCLUSION, true);
        pMask.setProperty(WEIGHT, 0);
        pMask.setProperty(OFFSET, 0);

        pSearch = new SearchProperties();
        pSearch.setProperty(CENTER_POS, new Position(0, 0));
        // Nothing is matched by default
        pSearch.setProperty(MAX_BLOCK_SZ, -1);
        pSearch.setProperty(MIN_BLOCK_SZ, 0);
        pSearch.setProperty(MAX_CHUNK_SZ, -1);
        pSearch.setProperty(MIN_CHUNK_SZ, 0);
        pSearch.setProperty(FINE_SEARCH, false);
        pSearch.setProperty(MAX_WIDTH, 1);
        pSearch.setProperty(MIN_WIDTH, 0);
        pSearch.setProperty(RESULTS, "");
        pSearch.setProperty(APPEND, false);

        l = new TestDataLogger();
    }

    @Test
    public void totalPositionCountIsCorrect() {
        pSearch.setProperty(FINE_SEARCH, true);
        pSearch.setProperty(MAX_WIDTH, 5);
        pSearch.setProperty(MIN_WIDTH, 2);
        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();
        assertEquals(256 * (5 * 5 - 2 * 2), search.positionsTotal());
        assertEquals(256 * (5 * 5 - 2 * 2), search.positionsChecked());
    }

    @Test
    public void chunkAndBlockCriteriaAreORd() {
        pSearch.setProperty(MAX_CHUNK_SZ, 30);
        pSearch.setProperty(MIN_CHUNK_SZ,20);

        pSearch.setProperty(MAX_BLOCK_SZ, 7680);
        pSearch.setProperty(MIN_BLOCK_SZ, 5120);

        pSearch.setProperty(MAX_WIDTH, 100);

        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        for (MaskData m : l.masks) {
            assertTrue(
                "The mask " + m + " should match search criteria",
                (m.blockSize <= 7680 && m.blockSize >= 5120) ||
                    (m.chunkSize <= 30 && m.chunkSize >= 20)
            );
        }
    }

    @Test
    public void rangeEdgesAreInclusive() {
        pSearch.setProperty(MAX_CHUNK_SZ, 22);
        pSearch.setProperty(MIN_CHUNK_SZ,22);

        pSearch.setProperty(MAX_BLOCK_SZ, 6600);
        pSearch.setProperty(MIN_BLOCK_SZ, 6600);

        pSearch.setProperty(MAX_WIDTH, 200);

        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        assertFalse(
            "List of found mask positions shouldn't be empty",
            l.masks.isEmpty()
        );
    }

    @Test
    public void firstPositionIsExtremumInEverything() {
        Position center = new Position(12, 40, 7, 4);
        pSearch.setProperty(CENTER_POS, center);

        Mask extremum = new Mask(pMask, 12, 40, 7, 4);

        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        assertTrue(
            "Expected: " + FormatHelper.formatMaskData(extremum) +
            LN + " was: " + LN + FormatHelper.formatMaskData(search.getMaxBlock()),
            equals(search.getMaxBlock(), extremum)
        );
        assertTrue(
            "Expected: " + FormatHelper.formatMaskData(extremum) +
            LN + " was: " + LN + FormatHelper.formatMaskData(search.getMinBlock()),
            equals(search.getMinBlock(), extremum)
        );
        assertTrue(
            "Expected: " + FormatHelper.formatMaskData(extremum) +
            LN + " was: " + LN + FormatHelper.formatMaskData(search.getMaxChunk()),
            equals(search.getMaxChunk(), extremum)
        );
        assertTrue(
            "Expected: " + FormatHelper.formatMaskData(extremum) +
            LN + " was: " + LN + FormatHelper.formatMaskData(search.getMinChunk()),
            equals(search.getMinChunk(), extremum)
        );
    }

    @Test
    public void correctAmountOfMaskPositionsAreChecked() {
        pSearch.setProperty(MAX_WIDTH, 100);

        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        assertEquals(10000, search.positionsChecked());
    }

    @Test
    public void largeSearchAreasDontCauseInvalidMatchesInFineSearches() {
        pSearch.setProperty(MAX_WIDTH, 38);
        pSearch.setProperty(FINE_SEARCH, true);
        pSearch.setProperty(MAX_CHUNK_SZ, 289);
        pSearch.setProperty(MIN_CHUNK_SZ,43);
        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        for (MaskData m: l.masks) {
            assertTrue(
                "chunk size should be >= 43 but was " + m.chunkSize,
                m.chunkSize >= 43
            );
        }

        if (l.masks.isEmpty()) assertTrue(search.getMaxChunk().chunkSize < 43);
    }

    private boolean equals(MaskData m1, MaskData data) {
        return
            m1.chunk.equals(data.chunk) &&
            m1.in.equals(data.in) &&
            m1.blockSize == data.blockSize &&
            m1.chunkSize == data.chunkSize &&
            m1.blockSurfaceArea == data.blockSurfaceArea &&
            m1.chunkSurfaceArea == data.chunkSurfaceArea;
    }
}
