package slimefinder.cli;

import slimefinder.Mask;
import java.io.FileWriter;
import java.io.IOException;
import slimefinder.properties.SearchProperties;

public class DataLogger {
    
    private FileWriter w;
    private StringBuffer messages;
    private static final String DELIMITER = ";";
    
    public DataLogger(SearchProperties pSearch) throws IOException {
        try {
            w = new FileWriter(pSearch.resultsFile, pSearch.append);
            messages = new StringBuffer();
        } catch (IOException ex) {
            CLI.error("Could not open the output file");
            throw ex;
        }
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

    public static String formatData(Mask m) {
        String s1 = m.posBlock.toString();
        String s2 = m.posChunk.x + ":" + m.posIn.x + "," + m.posChunk.z + ":" + m.posIn.z;
        String s3 = m.getBlockSize() + "/" + m.getBlockSurfaceArea();
        String s4 = m.getChunkSize() + "/" + m.getChunkSurfaceArea();
        return formatData(s1, s2, s3, s4);
    }

    public void write(Mask m) throws IOException {
        write(
                m.posBlock.toString(),
                m.posChunk.x + ":" + m.posIn.x + "," + m.posChunk.z + ":" + m.posIn.z,
                m.getBlockSize() + "/" + m.getBlockSurfaceArea(),
                m.getChunkSize() + "/" + m.getChunkSurfaceArea()
        );
    }

    public void write(String... fields) throws IOException {
        String str = "";
        for (int i = 0; i < fields.length - 1; i++) {
            str += fields[i] + DELIMITER;
        }
        write(str + fields[fields.length - 1]);
    }

    public void write(String str) throws IOException {
        try {
            w.write(str + String.format("%n"));
        } catch (IOException ex) {
            CLI.error("Could not write on the output file");
            throw ex;
        }
        
        try {
            w.flush();
        } catch (IOException ex) {
            CLI.error("Could not save the output file");
            throw ex;
        }
    }
    
    public void start() throws IOException {
        write(dataHeader());
    }
    
    public void close() {
        try {
            w.close();
        } catch (IOException ex) {
            CLI.error("Could not save the output file");
        }
    }

    public static String formatTime(long nanos) {
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
