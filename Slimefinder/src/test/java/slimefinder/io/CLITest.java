package slimefinder.io;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import slimefinder.core.TrackableTask;

import org.junit.Assert;
import static slimefinder.util.FormatHelper.LN;
import static slimefinder.util.FormatHelper.CR;

public class CLITest {

    private CLI cli;
    private TrackableTask task;

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();

    private String showSpecialCharacters(String string) {
        StringBuffer out = new StringBuffer();
        for (int i = 0; i < string.length(); i++) {
            char c = string.charAt(i);
            switch (c) {
                case '\n':
                    out.append("\\n");
                    break;
                case '\r':
                    out.append("\\r");
                    break;
                case ' ':
                    out.append('.');
                    break;
                default:
                    out.append(c);
            }
        }
        return out.toString();
    }

    private void assertEquals(String expected, String actual) {
        Assert.assertEquals(showSpecialCharacters(expected), showSpecialCharacters(actual));
    }

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
        cli = new CLI();
        task = new TrackableTask() {
            @Override
            public String startInfo() {
                return "started";
            }

            @Override
            public String progressInfo() {
                return "progressed";
            }

            @Override
            public String endInfo() {
                return "ended";
            }

            @Override
            public void run() {
            }
        };
    }

    @Test
    public void loggerMethodsDontPrintOnStdout() {
        cli.info("info");
        cli.warning("warning");
        cli.error("error");

        assertEquals("", outContent.toString());
    }

    @Test
    public void flushPrintsOnStdout() {
        cli.info("info");
        cli.warning("warning");
        cli.error("error");
        cli.flush();

        assertEquals(LN + "info" + LN + "WARNING: warning" + LN + "ERROR: error", outContent.toString());
    }

    @Test
    public void printStartInfoOnStdout() {
        cli.info("info");
        cli.printStartInfo(task);

        assertEquals(LN + "info" + LN + "started", outContent.toString());
    }

    @Test
    public void printProgressOnStdout() {
        cli.info("info");
        cli.printProgress(task);

        assertEquals(LN + "info" + LN + "progressed", outContent.toString());
    }

    @Test
    public void printEndInfoOnStdout() {
        cli.info("info");
        cli.printEndInfo(task);

        assertEquals(LN + "info" + LN + "progressed" + LN + "ended     " + LN, outContent.toString());
    }

    @Test
    public void multipleConsecutiveProgressUpdatesOnlyShowLatest() {
        cli.info("info");
        cli.printProgress(task);
        cli.printProgress(task);
        cli.printProgress(task);

        assertEquals(LN + "info" + LN + "progressed" + CR + "progressed" + CR + "progressed", outContent.toString());
    }

    @Test
    public void progressUpdateDoesntOverrideStartInfo() {
        cli.info("info");
        cli.printStartInfo(task);
        cli.printProgress(task);

        assertEquals(LN + "info" + LN + "started" + LN + "progressed", outContent.toString());
    }

    @Test
    public void extraMessagesDontInterruptProgressFlow() {
        cli.printProgress(task);
        cli.info("info");
        cli.printProgress(task);

        assertEquals(LN + "progressed" + CR + "info      " + LN + "progressed", outContent.toString());
    }

    @Test
    public void endInfoAfterProgressUpdate() {
        cli.printProgress(task);
        cli.printEndInfo(task);

        assertEquals(LN + "progressed" + CR + "progressed" + LN + "ended     " + LN, outContent.toString());
    }

}
