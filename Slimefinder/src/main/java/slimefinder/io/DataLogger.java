package slimefinder.io;

import slimefinder.core.Mask;
import java.io.FileWriter;
import java.io.IOException;

import slimefinder.io.properties.SearchProperties;
import slimefinder.util.FormatHelper;

import static slimefinder.util.FormatHelper.LN;

public class DataLogger implements IDataLogger {
    
    private FileWriter w;
    private static final String DELIMITER = ";";
    private CLI cli;
    private SearchProperties pSearch;
    
    public DataLogger(SearchProperties pSearch, CLI cli) {
        this.cli = cli;
        this.pSearch = pSearch;
    }

    @Override
    public void write(Mask m) throws IOException {
        write(
            m.posBlock.toString(),
            FormatHelper.chunkPos(m),
            m.getBlockSize() + "/" + m.getBlockSurfaceArea(),
            m.getChunkSize() + "/" + m.getChunkSurfaceArea()
        );
    }

    private void write(String... fields) throws IOException {
        String str = "";
        for (int i = 0; i < fields.length - 1; i++) {
            str += fields[i] + DELIMITER;
        }
        write(str + fields[fields.length - 1]);
    }

    private void write(String str) throws IOException {
        try {
            w.write(str + LN);
        } catch (IOException ex) {
            cli.error("Could not write on the output file");
            throw ex;
        }
        
        try {
            w.flush();
        } catch (IOException ex) {
            cli.error("Could not save the output file");
            throw ex;
        }
    }
    
    public void start() throws IOException {
        try {
            w = new FileWriter(pSearch.resultsFile, pSearch.append);
        } catch (IOException ex) {
            cli.error("Could not open the output file");
            throw ex;
        }
        if (!pSearch.append)
            write("block-position", "chunk-position", "blockSize", "chunkSize");
    }
    
    public void close() {
        try {
            w.close();
        } catch (IOException ex) {
            cli.error("Could not save the output file");
        }
    }
}
