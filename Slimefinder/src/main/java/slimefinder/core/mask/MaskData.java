package slimefinder.core.mask;

import slimefinder.util.Point;

import java.util.Objects;

public class MaskData {

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

    public void collectData(AbstractMask m) {
        chunk.setPoint(m.chunk);
        if (m.in != null) {
            if (in != null) {
                in.setPoint(m.in);
            } else {
                in = new Point(m.in);
            }
        }
        blockSize = m.blockSize;
        chunkSize = m.chunkSize;
        blockSurfaceArea = m.blockSurfaceArea;
        chunkSurfaceArea = m.chunkSurfaceArea;
    }

    public MaskData(MaskData data) {
        chunk = new Point(data.chunk);
        if (data.in != null)
            in = new Point(data.in);
        blockSize = data.blockSize;
        chunkSize = data.chunkSize;
        blockSurfaceArea = data.blockSurfaceArea;
        chunkSurfaceArea = data.chunkSurfaceArea;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MaskData maskData = (MaskData) o;
        return blockSurfaceArea == maskData.blockSurfaceArea &&
            chunkSurfaceArea == maskData.chunkSurfaceArea &&
            blockSize == maskData.blockSize &&
            chunkSize == maskData.chunkSize &&
            Objects.equals(chunk, maskData.chunk) &&
            Objects.equals(in, maskData.in);
    }

    @Override
    public int hashCode() {
        return Objects.hash(chunk, in);
    }

    @Override
    public String toString() {
        return "MaskData{" +
            "chunk=" + chunk +
            ", in=" + in +
            ", blockSurfaceArea=" + blockSurfaceArea +
            ", chunkSurfaceArea=" + chunkSurfaceArea +
            ", blockSize=" + blockSize +
            ", chunkSize=" + chunkSize +
            '}';
    }
}
