package slimefinder.cli;

import slimefinder.search.Search;

import slimefinder.properties.SearchProperties;

/**
 * This class does all the reading and writing on the command line.
 */
public class CLI {

    boolean search, images, help;

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
                    warning("Invalid argument '" + arg + "'");
                    help = true;
                    break;
            }
        }

        if (!search && !images) {
            help = true;
        }
    }

    public void refresh(TrackableTask task) {
        System.out.print("\r");
        System.out.print(task.getProgressInfo() + ",  Checked " + task.getProgress() + "/" + task.getMaxProgress() + " (" +
                String.format("%1$-4.1f", ((double)task.getProgress() / task.getMaxProgress()) * 100) + "%)");
    }

    public void printSearchStartInfo(SearchProperties pSearch) {
        info("Search criteria: " + pSearch.minChunkSize + " <= chunkSize <= " + pSearch.maxChunkSize +
                " or " + pSearch.minBlockSize + " <= blockSize <= " + pSearch.maxBlockSize + ".");
    }

    public void printSearchEndInfo(Search search) {
        long count = search.getMaxProgress();
        long duration = search.getDuration();
        info("Checked " + count + " position" + ((count == 1) ? ". " : "s. "));
        if (count <= 0) return;
        info("Took " + DataLogger.formatTime(duration) + " (" + duration / count + " nanoseconds per position).");
        info("Found the following extrema:");
        info(DataLogger.dataHeader());
        info(DataLogger.formatData(search.getMinBlock()) + "min-blockSize");
        info(DataLogger.formatData(search.getMaxBlock()) + "max-blockSize");
        info(DataLogger.formatData(search.getMinChunk()) + "min-chunkSize");
        info(DataLogger.formatData(search.getMaxChunk()) + "max-chunkSize");
    }

    /**
     * Prints the help message
     */
    public void helpMessage() {
        info("-h");
        info("  Display this message");
        info("");
        info("-i");
        info("  Read positions from a file and draw images of them");
        info("");
        info("-s");
        info("  Search for positions with specific slime chunk patterns and save them to a file");
    }

    public static void info(String info) {
        System.out.println(info);
    }

    public static void warning(String warning) {
        System.out.println("WARNING: " + warning);
    }

    public static void error(String error) {
        System.out.println("ERROR: " + error);
    }
}
