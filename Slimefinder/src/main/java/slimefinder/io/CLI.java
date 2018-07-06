package slimefinder.io;

import slimefinder.core.TrackableTask;

import static slimefinder.util.FormatHelper.LN;
import static slimefinder.util.FormatHelper.CR;

/**
 * This class does all the reading and writing on the command line.
 */
public class CLI {

    public static String ERROR_PREFIX = "ERROR: ";
    public static String WARNING_PREFIX = "WARNING: ";

    /**
     * Stores the characters waiting to be printed to stdout
     */
    private StringBuffer printBuffer;

    /**
     * True if overwriting the previously written line is allowed
     */
    private boolean allowRewrite;

    /**
     * Width of the previous line that was printed to stdout.
     * This is used to make sure the line is completely rewritten by an update.
     */
    private int lineWidth;

    public CLI() {
        printBuffer = new StringBuffer();
    }

    private void flush(boolean rewrite) {
        System.out.print((rewrite ? CR : LN) + printBuffer);
        printBuffer.setLength(0);
    }

    public synchronized void flush() {
        flush(false);
    }

    public synchronized void printProgress(TrackableTask task) {
        String progressInfo = task.progressInfo();
        appendBuffer(progressInfo);
        flush(allowRewrite);
        allowRewrite = true;
        lineWidth = progressInfo.length();
    }

    public synchronized void printStartInfo(TrackableTask task) {
        appendBuffer(LN + task.startInfo());
        flush();
        allowRewrite = false;
    }

    public synchronized void printEndInfo(TrackableTask task) {
        printProgress(task);
        appendBuffer(task.endInfo());
        flush();
        allowRewrite = false;
    }

    public void printHelp() {
        info(
            "-h" + LN  +
            "  Display this message" + LN +
            LN +
            "-i" + LN +
            "  Read positions from a file and draw images of them" + LN +
            LN +
            "-s" + LN +
            "  Search for positions with specific slime chunk patterns and save them to a file"
        );
        flush();
    }

    public synchronized void info(String info) {
        appendBuffer(info);
    }

    public synchronized void warning(String warning) {
        appendBuffer(WARNING_PREFIX + warning);
    }

    public synchronized void error(String error) {
        appendBuffer(ERROR_PREFIX + error);
    }

    private void appendBuffer(String line) {
        printBuffer.append(printBuffer.length() > 0 ? LN : "");
        StringBuilder extendedLine = new StringBuilder(line);
        while (extendedLine.length() < lineWidth) extendedLine.append(" ");
        printBuffer.append(extendedLine);
        lineWidth = 0;
    }
}
