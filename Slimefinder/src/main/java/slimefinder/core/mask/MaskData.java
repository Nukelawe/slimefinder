package slimefinder.core.mask;

import slimefinder.util.Point;

import java.util.Objects;

public abstract class MaskData {

    /**
     * The chunk and within-chunk positions of the mask
     */
    public Point chunk, in;

    /**
     * Total surface area under the mask in blocks and chunks
     */
    public int blockSurfaceArea, chunkSurfaceArea;

    /**
     * Total surface area of slime chunks under the mask in blocks and in chunks
     */
    public int blockSize, chunkSize;

    public MaskData() {

    }

    public MaskData(MaskData data) {
        chunk = new Point(data.chunk);
        in = new Point(data.chunk);
        blockSize = data.blockSize;
        chunkSize = data.chunkSize;
        blockSurfaceArea = data.blockSurfaceArea;
        chunkSurfaceArea = data.chunkSurfaceArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || !getClass().isAssignableFrom(MaskData.class)) return false;
        MaskData maskData = (MaskData) o;
        return Objects.equals(chunk, maskData.chunk) &&
            Objects.equals(in, maskData.in);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunk, in);
    }
}
