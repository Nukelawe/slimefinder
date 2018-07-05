package slimefinder.util;

public class FormatHelper {

    public static final String LN = String.format("%n");
    public static final String CR = "\r";

    public static String timeFormat(long nanos) {
        long millis = nanos / 1000000;
        long secs = millis / 1000;
        long mins = secs / 60;
        long hours = mins / 60;
        return String.format("%1$02d:%2$02d:%3$02d", hours, mins % 60, secs % 60);
    }

    public static String chunkFormat(Position pos) {
        return pos.chunk.x + ":" + pos.in.x + "," + pos.chunk.z + ":" + pos.in.z;
    }

    public static String blockFormat(Position pos) {
        return pos.block.x + "," + pos.block.z;
    }
}
