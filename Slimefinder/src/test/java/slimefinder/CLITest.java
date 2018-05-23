/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slimefinder;

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
        assertTrue(cli.isHelp());
        assertFalse(cli.isImages());
        assertFalse(cli.isSearch());
        
    }
    
    @Test
    public void searchArgumentIsDetected() {
        String[] arg = {"-s"};
        cli.parseArguments(arg);
        assertFalse(cli.isHelp());
        assertFalse(cli.isImages());
        assertTrue(cli.isSearch());
    }
    
    @Test
    public void imagesArgumentIsDetected() {
        String[] arg = {"-i"};
        cli.parseArguments(arg);
        assertFalse(cli.isHelp());
        assertTrue(cli.isImages());
        assertFalse(cli.isSearch());
    }
    
    @Test
    public void invalidArgumentIsAnnounced() {
        String[] arg = {"invalidArgument"};
        cli.parseArguments(arg);
        assertTrue(outContent.toString().contains("invalidArgument"));
        assertTrue(cli.isHelp());
        assertFalse(cli.isImages());
        assertFalse(cli.isSearch());
    }
    
    @Test
    public void noArgumentsGivesHelp() {
        String[] arg = {};
        cli.parseArguments(arg);
        assertTrue(cli.isHelp());
        assertFalse(cli.isImages());
        assertFalse(cli.isSearch());
    }
}
