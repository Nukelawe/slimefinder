package slimefinder.properties;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;

import slimefinder.cli.CLI;

public abstract class AbstractProperties extends Properties {

    protected String filename;
    protected HashMap<String, String> defaultValues;

    /**
     * Constructs an AbstractProperties object by reading reading a propertiy file.
     * @param filename
     * @throws IOException
     */
    public AbstractProperties(String filename) throws IOException {
        this.filename = filename;
        defaultValues = new HashMap<>();
        setDefaults();
        loadProperties();
    }

    /**
     * Constructs an AbstractProperites object for testing that needs to be manually initialized.
     */
    public AbstractProperties() {
    }
    
    private void loadProperties() throws IOException {
        readFiles(filename);
        removeUnusedProperties();
        addMissingProperties();
        parseProperties();
        saveProperties(new FileOutputStream(filename));
    }

    protected abstract void setDefaults();
    
    void readFiles(String filename) throws IOException {
        try {
            FileInputStream in = new FileInputStream(filename);
            this.load(in);
            in.close();
        } catch (IOException ex) {
            CLI.error("Could not load properties from file '" + filename + "'");
            return;
        }

        CLI.info("Successfully loaded properties from file: '" + filename + "'");
    }
    
    void saveProperties(OutputStream out) throws IOException {
        try {
            this.store(out, null);
        } catch (IOException ex) {
            CLI.error("Could not save '" + filename + "'");
            throw ex;
        } finally {
            out.close();
        }
    }
    
    /**
     * Removes unused properties i.e. properties not defined in defaultValues
     * @return true if some properties were ignored
     */
    boolean removeUnusedProperties() {
        boolean removed = false;
        Iterator<Object> iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (!defaultValues.containsKey(key)) {
                removed = true;
                CLI.warning("Unused property, '" + key + "' in '" + filename + "'");
                iterator.remove();
            }
        }
        return removed;
    }
    
    /**
     * Adds missing properties for which defaultValues exist
     * @return true if missing properties were added
     */
    boolean addMissingProperties() {
        boolean missing = false;
        for (Object key : defaultValues.keySet()) {
            if (!this.containsKey(key)) {
                missing = true;
                String value = defaultValues.get(key);
                CLI.warning(key + " not specified. Using default (" + value + ")");
                this.setProperty((String) key, value);
            }
        }
        
        return missing;
    }
    
    protected void parsingError(String property) {
        String defString = defaultValues.get(property);
        this.setProperty(property, defString);
        CLI.warning("Parsing " + property + " failed. Using default (" + defString + ")");
    }
    
    protected abstract void parseProperties();
}
