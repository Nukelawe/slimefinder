package slimefinder.properties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Properties;
import slimefinder.cli.Logger;
import static slimefinder.cli.Logger.warning;

public abstract class AbstractProperties {
    
    protected Properties defaults, properties;
    
    protected String filename;

    public AbstractProperties() {
        properties = new Properties();
        defaults = new Properties();
    }
    
    public void createProperties() throws IOException {
        loadProperties(filename);
        removeUnusedProperties();
        addMissingProperties();
        parseProperties();
        saveProperties(new FileOutputStream(filename));
    }
    
    void loadProperties(String filename) throws IOException {
        try {
            FileInputStream in = new FileInputStream(filename);
            properties.load(in);
            in.close();
        } catch (IOException ex) {
            Logger.error("Could not load properties from file '" + filename + "'");
            return;
        }

        Logger.info("Successfully loaded properties from file: '" + filename + "'");
    }
    
    void saveProperties(OutputStream out) throws IOException {
        try {
            properties.store(out, null);
        } catch (IOException ex) {
            Logger.error("Could not save '" + filename + "'");
            throw ex;
        } finally {
            out.close();
        }
    }
    
    /**
     * Removes unused properties i.e. properties not defined in defaults
     * @return true if some properties were ignored
     */
    boolean removeUnusedProperties() {
        boolean removed = false;
        Iterator<Object> iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (!defaults.containsKey(key)) {
                removed = true;
                Logger.warning("Unused property, '" + key + "' in '" + filename + "'");
                iterator.remove();
            }
        }
        return removed;
    }
    
    /**
     * Adds missing properties for which defaults exist
     * @return true if missing properties were added
     */
    boolean addMissingProperties() {
        boolean missing = false;
        for (Object key : defaults.keySet()) {
            if (!properties.containsKey(key)) {
                missing = true;
                String value = defaults.getProperty((String) key);
                Logger.warning(key + " not specified. Using default (" + value + ")");
                properties.setProperty((String) key, value);
            }
        }
        
        return missing;
    }
    
    protected void parsingError(String property) {
        String defString = defaults.getProperty(property);
        properties.setProperty(property, defString);
        warning("Parsing " + property + " failed. Using default (" + defString + ")");
    }
    
    protected abstract void parseProperties();
}
