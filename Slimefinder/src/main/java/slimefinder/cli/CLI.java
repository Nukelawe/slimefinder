package slimefinder.cli;

import slimefinder.search.Search;
import java.io.IOException;
import me.tongfei.progressbar.ProgressBar;
import me.tongfei.progressbar.ProgressBarStyle;
import slimefinder.ImageGenerator;
import slimefinder.properties.ImageProperties;
import slimefinder.properties.SearchProperties;
import slimefinder.properties.MaskProperties;

public class CLI {
    /**
     * Property file names
     */
    static final String SLIME_PROPERTIES = "mask.properties";
    static final String SEARCH_PROPERTIES = "search.properties";
    static final String IMAGE_PROPERTIES = "image.properties";
    
    private static MaskProperties pMask;
    private static SearchProperties pSearch;
    private static ImageProperties pImage;
    
    private boolean search, images, help;
    
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
                    Logger.warning("Invalid argument '" + arg + "'");
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
            Logger.helpMessage();
            return;
        }
        
        try {
            pMask = new MaskProperties();
            pMask.createProperties();
        
            if (search) {
                pSearch = new SearchProperties();
                pSearch.createProperties();
                search();
            }

            if (images) {
                pImage = new ImageProperties();
                pImage.createProperties();
                generateImages();
            }
            
        } catch (IOException ex) {
        }
    }
    
    private void generateImages() {        
        ImageGenerator d = new ImageGenerator(pImage, pMask);
        
        try {
            long time = System.nanoTime();
            long count = d.drawImages();
            time = System.nanoTime() - time;
            Logger.info("Generated " + count + " image" + ((count == 1) ? "" : "s") + ".");
            Logger.info("Took " + formatTime(time) + ((count > 0) ? " (" + time / count / 1000000 + " milliseconds per image)." : "."));
        
        } catch (NumberFormatException | IOException ex) {
        }
        
    }
    
    private void search() {
        try {
            long count = Math.max(pSearch.maxWidth * pSearch.maxWidth - pSearch.minWidth * pSearch.minWidth, 0);
            if (pSearch.fineSearch) {
                count *= 256;
            }
            ProgressBar progressBar = new ProgressBar("Searching...", count, 1, System.out, ProgressBarStyle.ASCII);
            progressBar.setExtraMessage("0 matches");
            Search s = new Search(pSearch, pMask, new Logger(pSearch, progressBar));
            Logger.info("Search criteria: " + pSearch.minChunkSize + " <= chunkSize <= " + pSearch.maxChunkSize + " or " + pSearch.minBlockSize + " <= blockSize <= " + pSearch.maxBlockSize + ".");
            long time = System.nanoTime();
            s.search();
            time = System.nanoTime() - time;
            Logger.info("Checked " + count + " position" + ((count == 1) ? ". " : "s. "));
            if (count > 0) {
                Logger.info("Took " + formatTime(time) + " (" + time / count + " nanoseconds per position).");
                Logger.info("Found the following extrema:");
                Logger.info(Logger.dataHeader());
                Logger.info(Logger.formatData(s.minBlock.mask) + "min-blockSize");
                Logger.info(Logger.formatData(s.maxBlock.mask) + "max-blockSize");
                Logger.info(Logger.formatData(s.minChunk.mask) + "min-chunkSize");
                Logger.info(Logger.formatData(s.maxChunk.mask) + "max-chunkSize");
            }
        } catch (IOException ex) {
        }
    }

    public boolean isSearch() {
        return search;
    }

    public boolean isImages() {
        return images;
    }

    public boolean isHelp() {
        return help;
    }
    private static String formatTime(long nanos) {
        long millis = nanos / 1000000;
        long secs = millis / 1000;
        long mins = secs / 60;
        long hours = mins / 60;
        long days = hours / 24;

        String d = (days == 0) ? "" : days + "d";
        String h = (hours == 0) ? "" : hours % 24 + "h ";
        String m = (mins == 0) ? "" : mins % 60 + "m ";
        String s = (secs == 0) ? "0." : secs % 60 + ".";
        String ms = String.format("%1$03d", millis % 1000) + "s";
        return d + h + m + s + ms;
    }
    
}
