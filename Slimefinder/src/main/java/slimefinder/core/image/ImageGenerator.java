package slimefinder.core.image;

import java.awt.*;
import java.awt.image.BufferedImage;

import slimefinder.core.mask.Mask;
import slimefinder.io.properties.ImageProperties;

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
        centerMarkerThickness = 1,
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
        int wImage = wChunk * (2 * Mask.R_CHUNK + 1) - wGrid;
        buff = new BufferedImage(wImage, wImage, BufferedImage.TYPE_INT_BGR);
    }

    /**
     * Draws a map of the mask on a png file
     * @param m - mask being drawn
     */
    public BufferedImage draw(Mask m) {
        for (int xChunk = -Mask.R_CHUNK; xChunk <= Mask.R_CHUNK; xChunk++) {
            for (int zChunk = -Mask.R_CHUNK; zChunk <= Mask.R_CHUNK; zChunk++) {

                int xchunkWidth = (xChunk < Mask.R_CHUNK) ? 17 : 16;
                int zchunkWidth = (zChunk < Mask.R_CHUNK) ? 17 : 16;
                for (int xIn = 0; xIn < xchunkWidth; xIn++) {
                    for (int zIn = 0; zIn < zchunkWidth; zIn++) {

                        int xblockWidth = (xIn < 16) ? wBlock : wGrid;
                        int zblockWidth = (zIn < 16) ? wBlock : wGrid;
                        for (int xPix = 0; xPix < xblockWidth; xPix++) {
                            for (int zPix = 0; zPix < zblockWidth; zPix++) {

                                // Pixel coordinates on the image
                                int x = (xChunk + Mask.R_CHUNK) * wChunk + xIn * wBlock + xPix;
                                int z = (zChunk + Mask.R_CHUNK) * wChunk + zIn * wBlock + zPix;

                                // Chunk background
                                buff.setRGB(x, z, backgroundColor.getRGB());

                                // Slime chunks
                                if (drawSlimeChunks && m.isSlimeChunk(xChunk, zChunk)) {
                                    buff.setRGB(x, z, slimeColor.getRGB());
                                }

                                // Block mask
                                if (drawBlockMask && !m.isBlockInside(16 * xChunk + xIn, 16 * zChunk + zIn)) {
                                    buff.setRGB(x, z, scaleRGB(buff.getRGB(x, z), blockMaskTransparency));
                                }

                                // Chunk mask
                                if (drawChunkMask && !m.isChunkInside(xChunk, zChunk)) {
                                    buff.setRGB(x, z, scaleRGB(buff.getRGB(x, z), chunkMaskTransparency));
                                }

                                // Grid
                                if (xIn == 16 || zIn == 16) {
                                    buff.setRGB(x, z, gridColor.getRGB());
                                }

                                // Center marker
                                if (drawCenter && isCenter(xChunk, zChunk, xIn - m.in.x, zIn - m.in.z, centerMarkerWidth, centerMarkerThickness)) {
                                    buff.setRGB(x, z, centerColor.getRGB());
                                }
                            }
                        }

                    }
                }

            }
        }
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

    private boolean isCenter(int xChunk, int zChunk, int xIn, int zIn, int width, int thickness) {
        int x = 16 * xChunk + xIn;
        int z = 16 * zChunk + zIn;
        if (Math.abs(x) <= width && Math.abs(z) < thickness) {
            return true;
        }
        if (Math.abs(z) <= width && Math.abs(x) < thickness) {
            return true;
        }
        return false;
    }
}
