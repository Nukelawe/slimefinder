package slimefinder;

import org.junit.Before;
import org.junit.Test;
import slimefinder.core.mask.Mask;
import slimefinder.core.search.SearchPath;
import slimefinder.io.properties.MaskProperties;
import slimefinder.util.Point;

import static slimefinder.io.properties.MaskProperties.*;
import static slimefinder.io.properties.MaskProperties.OFFSET;

public class PerformanceTest {

    private MaskProperties pMask;

    @Before
    public void setUp() {
        pMask = new MaskProperties();
        pMask.setProperty(SEED, 0L);
        pMask.setProperty(DESPAWN, true);
        pMask.setProperty(EXCLUSION, true);
        pMask.setProperty(WEIGHT, 0);
        pMask.setProperty(OFFSET, 0);
    }
    @Test
    public void MaskMoveToChunk() {
        Mask m = new Mask(pMask, 0, 0, 7, 7);
        int count = 500000;
        long time = System.nanoTime();
        for (int i = 0; i < count; i++) {
            m.moveToChunk(i, 0);
        }
        time = System.nanoTime() - time;
        long nanos = time / count;
        System.err.println("AbstractMask.moveToChunk took " + nanos + "ns per step on average");
    }

    @Test
    public void SearchPathStep() {
        long time = System.nanoTime();
        int width = 1000;
        SearchPath path = new SearchPath(new Point(0, 0), 0, width);
        while (path.step());
        time = System.nanoTime() - time;
        long nanos = time/(width * width);
        System.err.println("SearchPath.step took " + nanos + "ns per step on average");
    }

}
