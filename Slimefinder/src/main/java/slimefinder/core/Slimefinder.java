package slimefinder.core;

import java.io.IOException;
import java.util.Random;
import java.util.Scanner;

import slimefinder.core.image.ImageTask;
import slimefinder.core.search.SearchTask;
import slimefinder.io.CLI;
import slimefinder.io.DataLogger;
import slimefinder.io.properties.MaskProperties;
import slimefinder.io.properties.ImageProperties;
import slimefinder.io.properties.SearchProperties;
import slimefinder.io.properties.PropertyLoader;

import static slimefinder.util.FormatHelper.LN;

public class Slimefinder {

    private static final Random r = new Random();

    private CLI cli;

    private MaskProperties pMask;
    private SearchProperties pSearch;
    private ImageProperties pImage;

    boolean search, images, help;

    public static void main(String[] args) {
        Slimefinder slimefinder = new Slimefinder();
        slimefinder.parseArguments(args);
        slimefinder.execute();
    }

    public Slimefinder() {
        cli = new CLI();
        pMask = new MaskProperties();
        pSearch = new SearchProperties();
        pImage = new ImageProperties();
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
                default:
                    cli.warning("Invalid argument '" + arg + "'");
                    cli.flush();
                    help = true;
                    break;
            }
        }
        if (!search && !images) help = true;
    }

    public void execute() {
        try {
            if (help) {
                cli.printHelp();
                return;
            }

            PropertyLoader loader = new PropertyLoader(cli);
            boolean loadingFailed = loader.createProperties(pMask);
            if (search) loadingFailed = loader.createProperties(pSearch) || loadingFailed;
            if (images) loadingFailed = loader.createProperties(pImage) || loadingFailed;
            if (loadingFailed) return;
            cli.flush();

            if (search)
                runTask(new SearchTask(pSearch, pMask, new DataLogger(cli)));
            if (images)
                runTask(new ImageTask(pImage, pMask, cli));
        } catch (IOException ex) {
        } finally {
            cli.info(LN);
            cli.flush();
        }
    }

    private void runTask(TrackableTask task) {
        cli.printStartInfo(task);
        Thread taskThread = new Thread(task);
        Thread interrupter = new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while(!task.isInterrupted && !task.isFinished) {
                try {
                    if (System.in.available() > 0) {
                        String command = scanner.next();
                        if (command.equals("q")) {
                            task.interrupt(cli);
                        }
                    }
                } catch (IOException e) {
                }
            }
        });
        interrupter.start();
        taskThread.start();
        try {
            do {
                cli.printProgress(task);
                Thread.sleep(100);
            } while (taskThread.isAlive());
            interrupter.interrupt();
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
        r.setSeed(seed + (long) (chunkX * chunkX * 4987142) + (long) (chunkX * 5947611) + (long) (chunkZ * chunkZ) * 4392871L + (long) (chunkZ * 389711) ^ 987234911L);
        return r.nextInt(10) == 0;
    }
}
