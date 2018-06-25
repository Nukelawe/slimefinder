package slimefinder.cli;

import slimefinder.cli.CLI;
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
    private CLI cli;
    
    @Before
    public void setUpStreams() {
        System.setOut(new PrintStream(outContent));
        cli = new CLI();
    }

    @After
    public void restoreStream() {
        System.setOut(System.out);
    }
    
    @Test
    public void helpArgumentIsDetected() {
        String[] arg = {"-h"};
        cli.parseArguments(arg);
        assertTrue(cli.help);
        assertFalse(cli.images);
        assertFalse(cli.search);

    }
    
    @Test
    public void searchArgumentIsDetected() {
        String[] arg = {"-s"};
        cli.parseArguments(arg);
        assertFalse(cli.help);
        assertFalse(cli.images);
        assertTrue(cli.search);
    }
    
    @Test
    public void imagesArgumentIsDetected() {
        String[] arg = {"-i"};
        cli.parseArguments(arg);
        assertFalse(cli.help);
        assertTrue(cli.images);
        assertFalse(cli.search);
    }
    
    @Test
    public void invalidArgumentIsAnnounced() {
        String[] arg = {"invalidArgument"};
        cli.parseArguments(arg);
        assertTrue(outContent.toString().contains("invalidArgument"));
        assertTrue(cli.help);
        assertFalse(cli.images);
        assertFalse(cli.search);
    }
    
    @Test
    public void noArgumentsGivesHelp() {
        String[] arg = {};
        cli.parseArguments(arg);
        assertTrue(cli.help);
        assertFalse(cli.images);
        assertFalse(cli.search);
    }
}
