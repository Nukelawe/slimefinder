package slimefinder.core.image;

import java.awt.Color;
import java.awt.image.BufferedImage;

import slimefinder.core.mask.AbstractMask;
import slimefinder.io.properties.ImageProperties;

import static slimefinder.core.mask.AbstractMask.R_CHUNK;
import static slimefinder.io.properties.ImageProperties.*;

public class ImageGenerator {
    private final Color
        gridColor = Color.BLACK,
        slimeColor = Color.GREEN,
        backgroundColor = Color.WHITE,
        centerColor = Color.RED;

    private final float[]
        blockMaskTransparency = {0.55F, 0.55F, 0.55F},
        chunkMaskTransparency = {0.55F, 0.55F, 0.55F};

    private final int
        centerMarkerWidth = 3;

    /**
     * Width of a single chunk in pixels
     */
    private final int wChunk;

    private BufferedImage buff;

    private final int wBlock, wGrid;
    private final boolean drawSlimeChunks, drawBlockMask, drawChunkMask, drawCenter;

    public ImageGenerator(ImageProperties pImage) {
        wBlock = pImage.getInt(BLOCK_WIDTH);
        wGrid = pImage.getInt(GRID_WIDTH);
        drawSlimeChunks = pImage.getBoolean(DRAW_SLIME_CHUNKS);
        drawBlockMask = pImage.getBoolean(DRAW_BLOCK_MASK);
        drawChunkMask = pImage.getBoolean(DRAW_CHUNK_MASK);
        drawCenter = pImage.getBoolean(DRAW_CENTER);

        wChunk = 16 * wBlock + wGrid;
        int wImage = wChunk * (2 * R_CHUNK + 1) - wGrid;
        buff = new BufferedImage(wImage, wImage, BufferedImage.TYPE_INT_BGR);
    }

    /**
     * Draws a map of the given mask
     */
    public BufferedImage draw(AbstractMask m) {
        for (int xChunk = -R_CHUNK; xChunk <= R_CHUNK; xChunk++) {
            for (int zChunk = -R_CHUNK; zChunk <= R_CHUNK; zChunk++) {

                int xchunkWidth = (xChunk < R_CHUNK) ? 17 : 16;
                int zchunkWidth = (zChunk < R_CHUNK) ? 17 : 16;
                for (int xIn = 0; xIn < xchunkWidth; xIn++) {
                    for (int zIn = 0; zIn < zchunkWidth; zIn++) {

                        int xblockWidth = (xIn < 16) ? wBlock : wGrid;
                        int zblockWidth = (zIn < 16) ? wBlock : wGrid;
                        for (int xPix = 0; xPix < xblockWidth; xPix++) {
                            for (int zPix = 0; zPix < zblockWidth; zPix++) {

                                // Pixel coordinates on the image
                                int x = (xChunk + R_CHUNK) * wChunk + xIn * wBlock + xPix;
                                int z = (zChunk + R_CHUNK) * wChunk + zIn * wBlock + zPix;

                                // Chunk background
                                buff.setRGB(x, z, backgroundColor.getRGB());

                                // Slime chunks
                                if (drawSlimeChunks && m.isSlimeChunk(xChunk, zChunk))
                                    buff.setRGB(x, z, slimeColor.getRGB());

                                // Block mask
                                if (drawBlockMask && !m.isBlockInside(16 * xChunk + xIn, 16 * zChunk + zIn))
                                    buff.setRGB(x, z, scaleRGB(buff.getRGB(x, z), blockMaskTransparency));

                                // Chunk mask
                                if (drawChunkMask && !m.isChunkInside(xChunk, zChunk))
                                    buff.setRGB(x, z, scaleRGB(buff.getRGB(x, z), chunkMaskTransparency));

                                // Grid
                                if (xIn == 16 || zIn == 16)
                                    buff.setRGB(x, z, gridColor.getRGB());
                            }
                        }

                    }
                }

            }
        }

        // Center marker
        if (drawCenter) drawCenter(m);

        return buff;
    }

    private int scaleRGB(int rgb, float transparency[]) {
        int red = (rgb >> 16) & 0x0ff;
        red *= transparency[0];
        int green = (rgb >> 8) & 0x0ff;
        green *= transparency[1];
        int blue = (rgb) & 0x0ff;
        blue *= transparency[2];
        return (red << 16) + (green << 8) + blue;
    }

    private void drawCenter(AbstractMask m) {
        int xmin = (R_CHUNK - 1) * wChunk + 16 * wBlock;
        int zmin = (R_CHUNK - 1) * wChunk + 16 * wBlock;
        int xmax = (R_CHUNK + 1) * wChunk - 1;
        int zmax = (R_CHUNK + 1) * wChunk - 1;
        if (m.in == null) {
            for (int x = xmin; x <= xmax; x++) {
                for (int z = zmin; z <= zmax; z++) {
                    if (
                        Math.abs(x - xmin) < wGrid ||
                        Math.abs(z - zmin) < wGrid ||
                        Math.abs(x - xmax) < wGrid ||
                        Math.abs(z - zmax) < wGrid
                    ) {
                        buff.setRGB(x, z, centerColor.getRGB());
                    }
                }
            }
            return;
        }

        int xcmin = R_CHUNK * wChunk + m.in.x * wBlock;
        int zcmin = R_CHUNK * wChunk + m.in.z * wBlock;
        int xcmax = R_CHUNK * wChunk + m.in.x * wBlock + wBlock - 1;
        int zcmax = R_CHUNK * wChunk + m.in.z * wBlock + wBlock - 1;
        for (int x = xcmin - centerMarkerWidth * wBlock; x <= xcmax + centerMarkerWidth * wBlock; x++) {
            for (int z = zcmin - centerMarkerWidth * wBlock; z <= zcmax + centerMarkerWidth* wBlock; z++) {
                if (
                    xcmin <= x && xcmax >= x || zcmin <= z && zcmax >= z
                ) {
                    buff.setRGB(x, z, centerColor.getRGB());
                }
            }
        }
    }
}
