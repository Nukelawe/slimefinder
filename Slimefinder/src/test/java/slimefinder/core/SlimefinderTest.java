package slimefinder.core;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import slimefinder.core.Slimefinder;

/**
 *
 * @author Matias Ruotsalainen
 */
public class SlimefinderTest {

    private Slimefinder slimefinder;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStreams() {
        System.setOut(System.out);
    }

    @Before
    public void setUp() {
        slimefinder = new Slimefinder();
    }
    
    @Test
    public void searchArgumentIsDetected() {
        String[] arg = {"-s"};
        slimefinder.parseArguments(arg);
        assertFalse(slimefinder.help);
        assertFalse(slimefinder.images);
        assertTrue(slimefinder.search);
    }
    
    @Test
    public void imagesArgumentIsDetected() {
        String[] arg = {"-i"};
        slimefinder.parseArguments(arg);
        assertFalse(slimefinder.help);
        assertTrue(slimefinder.images);
        assertFalse(slimefinder.search);
    }
    
    @Test
    public void invalidArgumentIsAnnounced() {
        String[] arg = {"invalidArgument"};
        slimefinder.parseArguments(arg);
        assertTrue(outContent.toString().contains("invalidArgument"));
        assertTrue(slimefinder.help);
        assertFalse(slimefinder.images);
        assertFalse(slimefinder.search);
    }
    
    @Test
    public void noArgumentsGivesHelp() {
        String[] arg = {};
        slimefinder.parseArguments(arg);
        assertTrue(slimefinder.help);
        assertFalse(slimefinder.images);
        assertFalse(slimefinder.search);
    }

    @Test
    public void slimeChunkPseudoRandomPositioningWorksAsExpected() {
        long seed = 0;
        assertTrue(Slimefinder.isSlimeChunk(seed, 1,-3));
        assertFalse(Slimefinder.isSlimeChunk(seed, 10,-6));
    }
}
