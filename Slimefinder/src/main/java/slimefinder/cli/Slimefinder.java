package slimefinder.cli;

import slimefinder.ImageGenerator;
import slimefinder.properties.ImageProperties;
import slimefinder.properties.MaskProperties;
import slimefinder.properties.SearchProperties;
import slimefinder.search.Search;

import java.io.IOException;

public class Slimefinder {

    private CLI cli;

    public static void main(String[] args) {
        Slimefinder slimefinder = new Slimefinder(args);
        slimefinder.execute();
    }

    public Slimefinder(String[] args) {
        cli = new CLI();
        cli.parseArguments(args);
    }

    public void execute() {
        if (cli.help) {
            cli.helpMessage();
            return;
        }

        try {
            MaskProperties pMask = new MaskProperties("mask.properties");
            if (cli.search) searchMasks(new SearchProperties("search.properties"), pMask);
            if (cli.images) generateImages(new ImageProperties("image.properties"), pMask);
        } catch (IOException ex) {
        }
    }

    void searchMasks(SearchProperties pSearch, MaskProperties pMask) {
        DataLogger logger;
        try {
            logger = new DataLogger(pSearch);
        } catch (IOException e) {
            return;
        }
        Search search = new Search(pSearch, pMask, logger);
        cli.printSearchStartInfo(pSearch);
        runTask(search);
        cli.printSearchEndInfo(search);
    }

    private void generateImages(ImageProperties pImage, MaskProperties pMask) {
        ImageGenerator d = new ImageGenerator(pImage, pMask);

        try {
            long time = System.nanoTime();
            d.run();
            time = System.nanoTime() - time;
            long count = Long.parseLong(d.getProgressInfo());
            cli.info("Generated " + count + " image" + ((count == 1) ? "" : "s") + ".");
            cli.info("Took " + DataLogger.formatTime(time) + ((count > 0) ? " (" + time / count / 1000000 +
                    " milliseconds per image)." : "."));

        } catch (NumberFormatException ex) {
        }

    }

    private void runTask(TrackableTask task) {
        Thread thread = new Thread(task);
        thread.start();
        do {
            try {
                Thread.sleep(50);
            } catch (InterruptedException ie) {
            }
            cli.refresh(task);
        } while (thread.isAlive());
    }
}
