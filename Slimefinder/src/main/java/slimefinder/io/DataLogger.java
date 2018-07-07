package slimefinder.io;

import java.io.FileWriter;
import java.io.IOException;

import slimefinder.core.mask.MaskData;
import slimefinder.util.FormatHelper;

import static slimefinder.util.FormatHelper.LN;

public class DataLogger implements IDataLogger {
    
    private FileWriter w;
    public static final String DELIMITER = ";";
    private CLI cli;
    
    public DataLogger(CLI cli) {
        this.cli = cli;
    }

    @Override
    public void write(MaskData m) throws IOException {
        write(
            FormatHelper.blockPosFormat(m.chunk, m.in),
            FormatHelper.chunkPosFormat(m.chunk, m.in),
            m.blockSize + "/" + m.blockSurfaceArea,
            m.chunkSize + "/" + m.chunkSurfaceArea
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
    
    public void start(String filename, boolean append) throws IOException {
        try {
            w = new FileWriter(filename, append);
        } catch (IOException ex) {
            cli.error("Could not open the output file");
            throw ex;
        }
        if (!append)
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
