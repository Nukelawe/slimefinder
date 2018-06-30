package slimefinder.util;

import slimefinder.core.Mask;

public class FormatHelper {
    public static String formatTime(long nanos) {
        long millis = nanos / 1000000;
        long secs = millis / 1000;
        long mins = secs / 60;
        long hours = mins / 60;
        return String.format("%1$02d:%2$02d:%3$02d", hours, mins % 60, secs % 60);
    }

    public static String chunkPos(Mask m) {
        return m.posChunk.x + ":" + m.posIn.x + "," + m.posChunk.z + ":" + m.posIn.z;
    }

    public static String blockPos(Mask m) {
        return m.posBlock.x + "," + m.posBlock.z;
    }
}
