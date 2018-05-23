package slimefinder.cli;

import slimefinder.search.Mask;
import java.io.FileWriter;
import java.io.IOException;
import me.tongfei.progressbar.ProgressBar;
import slimefinder.properties.SearchProperties;

public class Logger {
    
    private FileWriter w;
    
    public ProgressBar progressBar;
    
    public Logger(SearchProperties pSearch, ProgressBar progressBar) throws IOException {
        try {
            w = new FileWriter(pSearch.resultsFile, pSearch.append);
        } catch (IOException ex) {
            error("Could not open the output file");
            throw ex;
        }
        this.progressBar = progressBar;
    }
    
    /**
     * Gives the mask data format of the given strings.
     * @param data
     * @return 
     */
    public static String formatData(String... data) {
        String s1 = String.format("%1$-24s", data[0]);
        String s2 = String.format("%1$-24s", data[1]);
        String s3 = String.format("%1$-16s", data[2]);
        String s4 = String.format("%1$-16s", data[3]);
        return s1 + s2 + s3 + s4;
    }
    
    public static String dataHeader() {
        return formatData("#block-position", "chunk-position", "blockSize", "chunkSize") + "extrema";
    }
    
    public void write(String str) throws IOException {
        try {
            w.write(str + String.format("%n"));
        } catch (IOException ex) {
            error("Could not write on the output file");
            throw ex;
        }
        
        try {
            w.flush();
        } catch (IOException ex) {
            error("Could not save the output file");
            throw ex;
        }
    }
    
    public static String formatData(Mask m) {
        String s1 = m.posBlock.toString();
        String s2 = m.posChunk.x + ":" + m.posIn.x + "," + m.posChunk.z + ":" + m.posIn.z;
        String s3 = m.getBlockSize() + "/" + m.getBlockSurfaceArea();
        String s4 = m.getChunkSize() + "/" + m.getChunkSurfaceArea();
        return formatData(s1, s2, s3, s4);
    }
    
    public void start() throws IOException {
        progressBar.start();
        write(dataHeader());
    }
    
    public void stop() throws IOException {
        progressBar.stop();
        try {
            w.close();
        } catch (IOException ex) {
            error("Could not save the output file");
            throw ex;
        }
    }
    
    /**
     * Prints the help message
     */
    public static void helpMessage() {
        info("-h");
        info("  Display this message");
        info("");
        info("-i");
        info("  Read positions from a file and draw images of them");
        info("");
        info("-s");
        info("  Search for positions with specific slime chunk patterns and save them to a file");
    }
    
    public static void propertyLoadingError(String filename) {
        error("Could not load properties from file '" + filename + "'");
    }
    
    public static void error(String error) {
        System.out.println("ERROR: " + error);
    }
    public static void warning(String warning) {
        System.out.println("WARNING: " + warning);
    }
    
    public static void info(String str) {
        System.out.println(str);
    }
    
    public static void propertyParsingError(String property, String defString) {
        warning("Parsing " + property + " failed. Using default (" + defString + ")");
    }
}
