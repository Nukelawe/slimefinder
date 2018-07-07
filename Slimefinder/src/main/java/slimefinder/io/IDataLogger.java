package slimefinder.io;

import java.io.IOException;

import slimefinder.core.mask.MaskData;

public interface IDataLogger {
    void start(String filename, boolean append) throws IOException;
    void close();
    void write(MaskData m) throws IOException;
}
