package slimefinder.core;

import java.io.IOException;
import java.util.Random;

import slimefinder.core.image.ImageTask;
import slimefinder.core.search.SearchTask;
import slimefinder.io.CLI;
import slimefinder.io.DataLogger;
import slimefinder.io.properties.*;

public class Slimefinder {

    CLI cli;

    boolean search, images, help;

    public static void main(String[] args) {
        Slimefinder slimefinder = new Slimefinder();
        slimefinder.parseArguments(args);
        slimefinder.execute();
    }

    public Slimefinder() {
        cli = new CLI();
    }

    public void parseArguments(String[] args) {
        for (String arg : args) {
            switch (arg) {
                case "-i":
                    images = true;
                    break;
                case "-s":
                    search = true;
                    break;
                case "-h":
                    help = true;
                    break;
                default:
                    cli.warning("Invalid argument '" + arg + "'");
                    cli.flush();
                    help = true;
                    break;
            }
        }

        if (!search && !images) {
            help = true;
        }
    }

    public void execute() {
        if (help) {
            cli.printHelp();
            return;
        }

        PropertyLoader loader = new PropertyLoader(cli);
        try {
            MaskProperties pMask = (MaskProperties) loader.createProperties(new MaskProperties());
            if (search)  {
                SearchProperties pSearch = (SearchProperties) loader.createProperties(new SearchProperties());
                DataLogger logger = new DataLogger(cli);
                SearchTask searchMasks = new SearchTask(pSearch, pMask, logger);
                runTask(searchMasks);
            }
            if (images) {
                ImageProperties pImage = (ImageProperties) loader.createProperties(new ImageProperties());
                ImageTask generateImgs = new ImageTask(pImage, pMask, cli);
                runTask(generateImgs);
            }
        } catch (IOException ex) {
            cli.info("");
            cli.flush();
        }
    }

    private void runTask(TrackableTask task) {
        cli.printStartInfo(task);
        Thread taskThread = new Thread(task);
        taskThread.start();
        try {
            do {
                cli.printProgress(task);
                Thread.sleep(50);
            } while (taskThread.isAlive());
        } catch (InterruptedException ie) {
        }
        cli.printEndInfo(task);
    }

    /**
     * This method is copied directly from the source code of Minecraft.
     * It determines which chunks are slime chunks.
     *
     * @param seed
     * @param chunkX - chunk x coordinate
     * @param chunkZ - chunk z coordinate
     * @return true if (chunkX, chunkZ) is a slime chunk, false otherwise
     */
    public static boolean isSlimeChunk(long seed, int chunkX, int chunkZ) {
        Random r = new Random();
        r.setSeed(seed + (long) (chunkX * chunkX * 4987142) + (long) (chunkX * 5947611) + (long) (chunkZ * chunkZ) * 4392871L + (long) (chunkZ * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }
}
