package slimefinder.core.image;

import java.awt.*;
import java.awt.image.BufferedImage;

import slimefinder.core.Mask;
import slimefinder.io.properties.ImageProperties;

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

    /**
     * Width of the whole image in pixels
     */
    private final int wImage;

    private final ImageProperties pImage;

    BufferedImage buff;

    public ImageGenerator(ImageProperties imageProperties) {
        wChunk = 16 * imageProperties.wBlock + imageProperties.wGrid;
        wImage = wChunk * (2 * Mask.R_CHUNK + 1) - imageProperties.wGrid;
        pImage = imageProperties;
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

                        int xblockWidth = (xIn < 16) ? pImage.wBlock : pImage.wGrid;
                        int zblockWidth = (zIn < 16) ? pImage.wBlock : pImage.wGrid;
                        for (int xPix = 0; xPix < xblockWidth; xPix++) {
                            for (int zPix = 0; zPix < zblockWidth; zPix++) {

                                // Pixel coordinates on the image
                                int x = (xChunk + Mask.R_CHUNK) * wChunk + xIn * pImage.wBlock + xPix;
                                int z = (zChunk + Mask.R_CHUNK) * wChunk + zIn * pImage.wBlock + zPix;

                                // Chunk background
                                buff.setRGB(x, z, backgroundColor.getRGB());

                                // Slime chunks
                                if (pImage.drawSlimeChunks && m.isSlimeChunk(xChunk, zChunk)) {
                                    buff.setRGB(x, z, slimeColor.getRGB());
                                }

                                // Block mask
                                if (pImage.drawBlockMask && !m.isBlockInside(16 * xChunk + xIn, 16 * zChunk + zIn)) {
                                    buff.setRGB(x, z, scaleRGB(buff.getRGB(x, z), blockMaskTransparency));
                                }

                                // Chunk mask
                                if (pImage.drawChunkMask && !m.isChunkInside(xChunk, zChunk)) {
                                    buff.setRGB(x, z, scaleRGB(buff.getRGB(x, z), chunkMaskTransparency));
                                }

                                // Grid
                                if (xIn == 16 || zIn == 16) {
                                    buff.setRGB(x, z, gridColor.getRGB());
                                }

                                // Center marker
                                if (pImage.drawCenter && isCenter(xChunk, zChunk, xIn - m.posIn.x, zIn - m.posIn.z, centerMarkerWidth, centerMarkerThickness)) {
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
