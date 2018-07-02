package slimefinder.core.image;

import slimefinder.core.Mask;
import slimefinder.core.TrackableTask;
import slimefinder.io.CLI;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import slimefinder.io.properties.*;
import slimefinder.util.Position;


import static slimefinder.util.FormatHelper.*;
import static slimefinder.util.FormatHelper.LN;

public class ImageTask extends TrackableTask {

    private final MaskProperties pSlime;
    private final ImageProperties pImage;
    private Scanner scanner;
    private long bytesRead;
    private long imagesGenerated;
    private CLI cli;
    private long linesParsed;
    private long parsingErrors;

    private File inputFile;

    private ImageGenerator generator;

    public ImageTask(
        ImageProperties imageProperties,
        MaskProperties slimeProperties
    ) throws FileNotFoundException {
        this.cli = CLI.getCLI();
        pSlime = slimeProperties;
        pImage = imageProperties;
        inputFile = new File(pImage.inputFile);
        generator = new ImageGenerator(imageProperties);
        try {
            scanner = new Scanner(new File(pImage.inputFile));
        } catch (IOException e) {
            cli.error("Could not open file: '" + pImage.inputFile + "'");
            throw e;
        }
    }

    /**
     * Draws images of positions specified in the input file and saves them to .png-files.
     */
    @Override
    public void run() {
        setStartTime();
        try {
            Mask mask = new Mask(pSlime, 0, 0);
            while (scanner.hasNextLine()) {
                Position pBlock = readLine(scanner.nextLine());
                if (pBlock == null) continue;
                mask.moveTo(pBlock.x, pBlock.z);
                saveImage(generator.draw(mask), getFilename(mask));
            }
        } catch (IOException e) {
        } finally {
            scanner.close();
            stop();
        }
    }

    private Position readLine(String line) {
        ++linesParsed;
        bytesRead += line.getBytes().length;
        line = line.trim();
        if (line.length() == 0) return null;
        if (line.charAt(0) == '#') return null;
        String fields[] = line.split(";", -1);
        Position out = null;
        for (int i = 0; i < fields.length; i++) {
            try {
                out = Position.parsePos(fields[i]);
            } catch (NumberFormatException e) {
            }
            if (out != null) return out;
        }
        if (linesParsed == 1) return null; // Header line should not give warning
        cli.warning("Parsing line " + linesParsed + " failed");
        ++parsingErrors;
        return null;
    }

    private void saveImage(BufferedImage image, String filename) throws IOException {
        try {
            File outputFile = new File(pImage.outputDir + "/" + filename);
            File parentDir = outputFile.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            ImageIO.write(image, "png", outputFile);
        } catch (IOException e) {
            cli.error("Failed to save image '" + filename + "'");
            throw e;
        }

        imagesGenerated++;
    }

    private static String getFilename(Mask m) {
        return
            m.posBlock.x + "x_" + m.posBlock.z + "z_"
            + m.getChunkSize() + "c" + m.getChunkSurfaceArea() + "_"
            + m.getBlockSize() + "b" + m.getBlockSurfaceArea() + ".png";
    }

    /**
     * Generating images of masks listed in file: 'results.csv'"
     * Saving generated images to /home/users/nukelawe/slimefinder/images/
     */
    public String startInfo() {
        return
            "Generating images of masks listed in file: '" + pImage.inputFile + "'" + LN +
            "Saving generated images to: '" + pImage.outputDir + "'";
    }

    /**
     * [  82.1% ]  96 images generated, 100 lines parsed, 2 errors, 00:00:09 elapsed, 43:12:55 remaining
     */
    public synchronized String progressInfo() {
        long bytesRead = this.bytesRead;
        long totalBytes = inputFile.length();
        long time = getDuration();
        float progress = (float) bytesRead / totalBytes;
        if (isFinished) progress = 1f;
        return
            "[ " + String.format("%1$5.1f", progress * 100) + "% ]  " +
            imagesGenerated + " images generated, " +
            linesParsed + " lines parsed, " +
            parsingErrors + " errors, " +
            formatTime(time) + " elapsed, " +
            formatTime((long) (time / progress - time)) + " remaining";
    }

    /**
     * 45 milliseconds per image
     */
    public String endInfo() {
        if (imagesGenerated<= 0) return "";
        return getDuration() / imagesGenerated / 1000000 + " milliseconds per image";
    }
}
