package slimefinder.io;

import slimefinder.core.TrackableTask;

/**
 * This class does all the reading and writing on the command line.
 */
public class CLI {

    private StringBuffer printBuffer;

    /**
     * System-independent newline character
     */
    public static final String LN = String.format("%n");

    public static final String CR = "\r";

    private boolean allowRewrite;

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
        appendBuffer(task.progressInfo());
        flush(allowRewrite);
        allowRewrite = true;
    }

    public synchronized void printStartInfo(TrackableTask task) {
        appendBuffer(task.startInfo());
        flush();
        allowRewrite = false;
    }

    public synchronized void printEndInfo(TrackableTask task) {
        printProgress(task);
        appendBuffer(task.endInfo() + LN);
        flush();
        allowRewrite = false;
    }

    /**
     * Prints the help message
     */
    public synchronized void helpMessage() {
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
        appendBuffer("WARNING: " + warning);
    }

    public synchronized void error(String error) {
        appendBuffer("ERROR: " + error);
    }

    private void appendBuffer(String line) {
        printBuffer.append((printBuffer.length() > 0 ? LN : "") + line);
    }
}
