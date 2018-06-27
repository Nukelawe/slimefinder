package slimefinder;

import slimefinder.cli.CLI;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import slimefinder.cli.TrackableTask;
import slimefinder.properties.*;
import slimefinder.util.Position;

public class ImageGenerator extends TrackableTask {

    private final Color gridColor = Color.BLACK,
            slimeColor = Color.GREEN,
            backgroundColor = Color.WHITE,
            centerColor = Color.RED;
    private final float[] blockMaskTransparency = {0.55F, 0.55F, 0.55F},
            chunkMaskTransparency = {0.55F, 0.55F, 0.55F};
    /**
     * Width of a single chunk and the whole image in pixels
     */
    private final int wChunk,
            wImage;

    private final MaskProperties pSlime;
    private final ImageProperties pImage;
    private final BufferedImage b;
    private Scanner scanner;
    private long bytesRead;
    private long successCount;
    private File inputFile;

    public ImageGenerator(ImageProperties imageProperties, MaskProperties slimeProperties) {
        wChunk = 16 * imageProperties.wBlock + imageProperties.wGrid;
        wImage = wChunk * (2 * Mask.R_CHUNK + 1) - imageProperties.wGrid;
        this.pSlime = slimeProperties;
        this.pImage = imageProperties;
        b = new BufferedImage(wImage, wImage, BufferedImage.TYPE_INT_RGB);
        inputFile = new File(pImage.inputFile);
    }

    /**
     * Draws images of positions specified in the input file and saves them to a
     * .png-files.
     *
     * @return number of images generated
     * @throws NumberFormatException
     * @throws IOException
     */
    @Override
    public void run() {
        setStartTime();
        Mask m = null;

        try {
            createScanner();

            CLI.info("Generating images...");
            String line;
            while (scanner.hasNextLine()) {
                line = scanner.nextLine();
                bytesRead += line.getBytes().length;
                if (line.length() < 1) continue;
                Position pBlock;
                try {
                    pBlock = Position.parsePos(line.split("[;\\s]")[0]);
                } catch (NumberFormatException e) {
                    CLI.warning(e.getMessage());
                    continue;
                }
                if (m == null) {
                    m = new Mask(pSlime, pBlock.x, pBlock.z);
                } else {
                    m.moveTo(Math.floorDiv(pBlock.x, 16), Math.floorDiv(pBlock.z, 16), pBlock.x & 15, pBlock.z & 15);
                }
                draw(m);
                successCount++;
            }
        } catch (IOException e) {
        } finally {
            scanner.close();
            stop();
        }
    }

    private void createScanner() throws FileNotFoundException {
        try {
            scanner = new Scanner(new File(pImage.inputFile));
        } catch (IOException ex) {
            CLI.error("Could not open file: '" + pImage.inputFile + "'");
            throw ex;
        }
    }

    /**
     * Draws a map of the mask on a png file
     *
     * @param m - mask being drawn
     * @throws IOException
     */
    public void draw(Mask m) throws IOException {
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

                                // Grid
                                if (xIn == 16 || zIn == 16) {
                                    b.setRGB(x, z, gridColor.getRGB());
                                    continue;
                                }

                                // Chunk background
                                b.setRGB(x, z, backgroundColor.getRGB());

                                // Slime chunks
                                if (pImage.drawSlimeChunks && m.isSlimeChunk(xChunk, zChunk)) {
                                    b.setRGB(x, z, slimeColor.getRGB());
                                }

                                // Center block
                                if (pImage.drawCenter && isCenter(xChunk, zChunk, xIn - m.posIn.x, zIn - m.posIn.z, 3, 1)) {
                                    b.setRGB(x, z, centerColor.getRGB());
                                    continue;
                                }

                                // Block mask
                                if (pImage.drawBlockMask && !m.isBlockInside(16 * xChunk + xIn, 16 * zChunk + zIn)) {
                                    b.setRGB(x, z, scaleRGB(b.getRGB(x, z), blockMaskTransparency));
                                }

                                // Chunk mask
                                if (pImage.drawChunkMask && !m.isChunkInside(xChunk, zChunk)) {
                                    b.setRGB(x, z, scaleRGB(b.getRGB(x, z), chunkMaskTransparency));
                                }
                            }
                        }

                    }
                }

            }
        }

        // Save image
        String filename = getFilename(m);
        try {
            File outputFile = new File(pImage.outputDir + "/" + filename);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            ImageIO.write(b, "png", outputFile);
        } catch (IOException e) {
            CLI.error("Failed to save image '" + filename + "'");
            throw e;
        }
    }

    private String getFilename(Mask m) {
        return "(" + m.posBlock.x + "," + m.posBlock.z + ")_"
                + m.getChunkSize() + "c" + m.getChunkSurfaceArea() + "_"
                + m.getBlockSize() + "b" + m.getBlockSurfaceArea() + ".png";
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

    @Override
    public synchronized long getProgress() {
        return bytesRead;
    }

    @Override
    public synchronized long getMaxProgress() {
        return inputFile.length();
    }

    @Override
    public synchronized String getProgressInfo() {
        return "" + successCount;
    }
}
