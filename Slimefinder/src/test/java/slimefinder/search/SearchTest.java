package slimefinder.search;

import org.junit.Test;
import static org.junit.Assert.*;
import slimefinder.properties.SearchProperties;

public class SearchTest {

    @Test
    public void chunkAndBlockCriteriaAreORd() {
        SearchProperties pSearch = new SearchProperties();
        pSearch.maxBlockSize = 30;
        pSearch.minBlockSize = 20;

        pSearch.maxChunkSize = 7;
        pSearch.minChunkSize = 3;
        // Only chunkSize matches
        assertTrue(Search.matchesSearchCriteria(5, 100, pSearch));
        // Only blockSize matches
        assertTrue(Search.matchesSearchCriteria(8, 25, pSearch));
        // Both block and chunk sizes match
        assertTrue(Search.matchesSearchCriteria(5, 25, pSearch));
        // Neither block nor chunk sizes match
        assertFalse(Search.matchesSearchCriteria(8, 100, pSearch));
    }

    @Test
    public void rangeEdgesAreInclusive() {
        SearchProperties pSearch = new SearchProperties();
        pSearch.maxBlockSize = 1;
        pSearch.minBlockSize = 1;

        pSearch.maxChunkSize = 1;
        pSearch.minChunkSize = 1;
        // chunkSize
        assertTrue(Search.matchesSearchCriteria(1, 0, pSearch));
        // blockSize
        assertTrue(Search.matchesSearchCriteria(0, 1, pSearch));
    }
}
