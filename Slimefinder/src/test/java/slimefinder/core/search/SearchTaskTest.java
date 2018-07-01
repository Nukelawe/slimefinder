package slimefinder.core.search;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import slimefinder.core.Mask;
import slimefinder.io.properties.MaskProperties;
import slimefinder.io.properties.SearchProperties;
import slimefinder.io.IDataLogger;
import slimefinder.util.Position;

import java.io.IOException;
import java.util.LinkedList;

public class SearchTaskTest {

    private SearchProperties pSearch;
    private MaskProperties pMask;
    private TestDataLogger l;

    private class TestDataLogger implements IDataLogger {

        public LinkedList<Mask> masks;

        public TestDataLogger() {
            masks = new LinkedList<>();
        }

        @Override
        public void start() throws IOException {

        }

        @Override
        public void close() {

        }

        @Override
        public void write(Mask m) throws IOException {
            masks.add(new Mask(m));
        }
    }

    @Before
    public void setUp() {
        pMask = new MaskProperties();
        pMask.worldSeed = 0;
        pMask.despawnSphere = true;
        pMask.exclusionSphere = true;
        pMask.chunkWeight = 0;
        pMask.yOffset = 0;

        pSearch = new SearchProperties();
        pSearch.posChunk = Position.origin();
        pSearch.posIn = Position.origin();

        // Nothing is matched by default
        pSearch.maxBlockSize = -1;
        pSearch.minBlockSize = 0;
        pSearch.maxChunkSize = -1;
        pSearch.minChunkSize = 0;

        l = new TestDataLogger();
    }

    @Test
    public void totalPositionCountIsCorrect() {
        pSearch.fineSearch = true;
        pSearch.maxWidth = 5;
        pSearch.minWidth = 2;
        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();
        assertEquals(256 * (5 * 5 - 2 * 2), search.positionsTotal());
        assertEquals(256 * (5 * 5 - 2 * 2), search.positionsChecked());
    }

    @Test
    public void chunkAndBlockCriteriaAreORd() {
        pSearch.maxChunkSize = 30;
        pSearch.minChunkSize = 20;

        pSearch.maxBlockSize = 7680;
        pSearch.minBlockSize = 5120;

        pSearch.minWidth = 0;
        pSearch.maxWidth = 100;

        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        for (Mask m : l.masks) {
            assertTrue(
                (m.getBlockSize() <= pSearch.maxBlockSize && m.getBlockSize() >= pSearch.minBlockSize) ||
                    (m.getChunkSize() <= pSearch.maxChunkSize && m.getChunkSize() >= pSearch.minChunkSize)
            );
        }
    }

    @Test
    public void rangeEdgesAreInclusive() {
        pSearch.maxBlockSize = 6600;
        pSearch.minBlockSize = 6600;

        pSearch.maxChunkSize = 22;
        pSearch.minChunkSize = 22;

        pSearch.minWidth = 0;
        pSearch.maxWidth = 200;


        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        assertFalse(l.masks.isEmpty());
    }

    @Test
    public void firstPositionIsExtremumInEverything() {
        pSearch.posChunk.setPos(12, 40);
        pSearch.posIn.setPos(7,4);
        pSearch.minWidth = 0;
        pSearch.maxWidth = 1;

        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        Position start = new Position(pSearch.posChunk, pSearch.posIn);

        assertEquals(start, search.getMaxBlock().posBlock);
        assertEquals(start, search.getMinBlock().posBlock);
        assertEquals(start, search.getMaxChunk().posBlock);
        assertEquals(start, search.getMinChunk().posBlock);
    }

    @Test
    public void correctAmountOfMaskPositionsAreChecked() {
        pSearch.minWidth = 0;
        pSearch.maxWidth = 100;

        pSearch.minChunkSize = 43;
        pSearch.maxChunkSize = 289;

        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        assertEquals(10000, search.positionsChecked());
    }

    @Test
    public void largeSearchAreasDontCauseInvalidMatchesInFineSearches() {
        pSearch.minWidth = 0;
        pSearch.maxWidth = 38;

        pSearch.fineSearch = true;
        pSearch.minChunkSize = 43;
        pSearch.maxChunkSize = 289;

        SearchTask search = new SearchTask(pSearch, pMask, l);
        search.run();

        for (Mask m: l.masks) {
            assertTrue(
                m.getChunkSize() + ">=" + pSearch.minChunkSize,
                m.getChunkSize() >= pSearch.minChunkSize
            );
        }

        if (l.masks.isEmpty()) assertTrue("", search.getMaxChunk().getChunkSize() < pSearch.minChunkSize);
    }
}
