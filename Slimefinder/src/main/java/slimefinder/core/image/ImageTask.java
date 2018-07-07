package slimefinder.core.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.Scanner;
import javax.imageio.ImageIO;

import slimefinder.core.mask.AbstractMask;
import slimefinder.core.mask.Mask;
import slimefinder.core.TrackableTask;
import slimefinder.io.CLI;
import slimefinder.io.DataLogger;
import slimefinder.io.properties.*;
import slimefinder.util.Position;

import static slimefinder.util.FormatHelper.*;
import static slimefinder.io.properties.ImageProperties.*;

public class ImageTask extends TrackableTask {

    private final MaskProperties pSlime;
    private Scanner scanner;
    private long bytesRead;
    private long imagesGenerated;
    private CLI cli;
    private long linesParsed;
    private long parsingErrors;

    private File input;

    private ImageGenerator generator;

    private final String filename;
    private final String outDir;

    public ImageTask(
        ImageProperties pImage,
        MaskProperties pSlime,
        CLI cli
    ) throws FileNotFoundException {
        filename = pImage.getProperty(INPUT_FILE);
        outDir = pImage.getString(OUTPUT_DIR);
        this.cli = cli;
        this.pSlime = pSlime;
        input = new File(filename);
        generator = new ImageGenerator(pImage);
        try {
            scanner = new Scanner(new File(filename));
        } catch (IOException e) {
            cli.error("Could not open file: '" + filename + "'");
            throw e;
        }
    }

    /**
     * Draws images of positions listed in a file and saves them as .png-files.
     */
    @Override
    public void run() {
        setStartTime();
        try {
            AbstractMask mask = new Mask(pSlime, 0, 0, 0, 0);
            while (scanner.hasNextLine()) {
                if (isInterrupted) throw new InterruptedException();
                Position pBlock = readLine(scanner.nextLine());
                if (pBlock == null) continue;
                mask.moveTo(pBlock);
                saveImage(generator.draw(mask), getFilename(mask));
            }
            isFinished = true;
        } catch (IOException | InterruptedException e) {
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
        String fields[] = line.split(DataLogger.DELIMITER, -1);
        Position out = null;
        for (String field : fields) {
            try {
                out = Position.parsePos(field);
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
            File outputFile = new File(outDir + "/" + filename);
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

    private static String getFilename(AbstractMask m) {
        int blockX = m.chunk.x * 16;
        int blockZ = m.chunk.z * 16;
        if (m.in != null) {
            blockX += m.in.x;
            blockZ += m.in.z;
        }
        return
            blockX + "x_" + blockZ + "x_"
            + m.chunkSize + "c" + m.chunkSurfaceArea + "_"
            + m.blockSize + "b" + m.blockSurfaceArea + ".png";
    }

    /**
     * Generating images of masks listed in file: 'results.csv'"
     * Saving generated images to /home/users/nukelawe/slimefinder/images/
     */
    public String startInfo() {
        return
            "Generating images of masks listed in file: '" + filename + "'" + LN +
            "Saving generated images to directory: '" + outDir + "'";
    }

    /**
     * [  82.1% ]  96 images generated, 100 lines parsed, 2 errors, 00:00:09 elapsed, 43:12:55 remaining
     */
    public synchronized String progressInfo() {
        long bytesRead = this.bytesRead;
        long totalBytes = input.length();
        long time = getDuration();
        float progress = (float) bytesRead / totalBytes;
        if (isFinished) progress = 1f;
        return
            "[ " + String.format("%1$5.1f", progress * 100) + "% ]  " +
            imagesGenerated + " images generated, " +
            linesParsed + " lines parsed, " +
            parsingErrors + " errors, " +
            timeFormat(time) + " elapsed, " +
            timeFormat((long) (time / progress - time)) + " remaining";
    }

    /**
     * 45 milliseconds per image
     */
    public String endInfo() {
        if (imagesGenerated<= 0) return "";
        return getDuration() / imagesGenerated / 1000000 + " milliseconds per image";
    }
}
