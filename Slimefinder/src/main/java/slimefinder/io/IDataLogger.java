package slimefinder.io;

import java.io.IOException;

import slimefinder.core.mask.Mask;

public interface IDataLogger {
    void start(String filename, boolean append) throws IOException;
    void close();
    void write(Mask m) throws IOException;
}
