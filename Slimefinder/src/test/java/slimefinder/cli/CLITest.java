package slimefinder.cli;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Matias Ruotsalainen
 */
public class CLITest {
    
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    
    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @After
    public void restoreStream() {
        System.setOut(System.out);
    }
    
    @Test
    public void helpArgumentIsDetected() {
        String[] arg = {"-h"};
        CLI.parseArguments(arg);
        assertTrue(CLI.help);
        assertFalse(CLI.images);
        assertFalse(CLI.search);

    }
    
    @Test
    public void searchArgumentIsDetected() {
        String[] arg = {"-s"};
        CLI.parseArguments(arg);
        assertFalse(CLI.help);
        assertFalse(CLI.images);
        assertTrue(CLI.search);
    }
    
    @Test
    public void imagesArgumentIsDetected() {
        String[] arg = {"-i"};
        CLI.parseArguments(arg);
        assertFalse(CLI.help);
        assertTrue(CLI.images);
        assertFalse(CLI.search);
    }
    
    @Test
    public void invalidArgumentIsAnnounced() {
        String[] arg = {"invalidArgument"};
        CLI.parseArguments(arg);
        assertTrue(outContent.toString().contains("invalidArgument"));
        assertTrue(CLI.help);
        assertFalse(CLI.images);
        assertFalse(CLI.search);
    }
    
    @Test
    public void noArgumentsGivesHelp() {
        String[] arg = {};
        CLI.parseArguments(arg);
        assertTrue(CLI.help);
        assertFalse(CLI.images);
        assertFalse(CLI.search);
    }
}
