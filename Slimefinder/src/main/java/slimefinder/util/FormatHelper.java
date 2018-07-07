package slimefinder.util;

import slimefinder.core.mask.MaskData;

public class FormatHelper {

    public static final String LN = String.format("%n");
    public static final String CR = "\r";
    public static final String CHUNK_SEP = "c";
    public static final String COORD_SEP = ",";

    public static String timeFormat(long nanos) {
        long millis = nanos / 1000000;
        long secs = millis / 1000;
        long mins = secs / 60;
        long hours = mins / 60;
        return String.format("%1$02d:%2$02d:%3$02d", hours, mins % 60, secs % 60);
    }

    /**
     * If exact position is known:      14c5,-2c0
     * If only chunk position is known: 14c,-2c
     */
    public static String chunkPosFormat(Point chunk, Point in) {
        if (in == null) return
            chunk.x + CHUNK_SEP + COORD_SEP + chunk.z + CHUNK_SEP;
        return
            chunk.x + CHUNK_SEP + in.x + COORD_SEP + chunk.z + CHUNK_SEP + in.z;
    }

    /**
     * If exact position is known:      -44,12
     * If only chunk position is known: -48,0 to -33,15
     */
    public static String blockPosFormat(Point chunk, Point in) {
        if (in == null) return
            chunk.x * 16 + COORD_SEP + chunk.z * 16 +
            " to " + ((chunk.x + 1) * 16 - 1) + COORD_SEP + ((chunk.z + 1) * 16 - 1);
        return
            (chunk.x * 16 + in.x) + COORD_SEP + (chunk.z * 16 + in.z);
    }

    public static String formatMaskData(MaskData m) {
        return
            FormatHelper.blockPosFormat(m.chunk, m.in) + LN +
            FormatHelper.chunkPosFormat(m.chunk, m.in) + LN +
            m.blockSize + "/" + m.blockSurfaceArea + LN +
            m.chunkSize + "/" + m.chunkSurfaceArea;
    }
}
