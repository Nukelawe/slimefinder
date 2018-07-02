package slimefinder.io.properties;

import java.io.*;
import java.util.*;

import slimefinder.io.CLI;

public abstract class AbstractProperties extends Properties {

    protected HashMap<String, String> defaultValues;
    private CLI cli;
    private InputStream input;
    private OutputStream output;
    private String filename;

    /**
     * Constructs an AbstractProperties object by reading reading a propertiy file. For testing.
     */
    public AbstractProperties(InputStream input, OutputStream output, String filename) throws IOException {
        this.input = input;
        this.output = output;
        setUp(filename);
    }

    /**
     * Constructs an AbstractProperties object by reading reading a propertiy file.
     * @param filename
     * @throws IOException
     */
    public AbstractProperties(String filename) throws IOException {
        setUp(filename);
    }

    /**
     * Constructs an AbstractProperites object for testing that needs to be manually initialized.
     */
    public AbstractProperties() {
    }
    
    private void setUp(String filename) throws IOException{
        this.filename = filename;
        this.cli = CLI.getCLI();
        defaultValues = new HashMap<>();
        setDefaults();
        readFiles(filename);
        removeUnusedProperties();
        addMissingProperties();
        parseProperties();
        saveProperties(filename);
    }

    protected abstract void setDefaults();
    
    protected void readFiles(String filename) throws IOException {
        try {
            if (input == null) input = new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            cli.error("Could not find file '" + filename + "'");
            return;
        } catch (SecurityException e) {
            cli.error("Could not open file '" + filename + "'. Insufficient read permissions");
            return;
        }
        try {
            load(input);
        } catch (IOException e) {
            cli.error("Could not load properties from file '" + filename + "'");
            return;
        } catch (IllegalArgumentException e) {
            cli.error("File '" + filename + "' contained a malformed unicode sequence");
            return;
        } finally {
            input.close();
        }
        cli.info("Successfully loaded properties from file: '" + filename + "'");
    }
    
    /**
     * Removes unused properties i.e. properties not defined in defaultValues
     * @return true if some properties were ignored
     */
    private boolean removeUnusedProperties() {
        boolean removed = false;
        Iterator<Object> iterator = this.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (!defaultValues.containsKey(key)) {
                removed = true;
                cli.warning("Unused property, '" + key + "' in '" + filename + "'");
                iterator.remove();
            }
        }
        return removed;
    }
    
    /**
     * Adds missing properties for which defaultValues exist
     * @return true if missing properties were added
     */
    private boolean addMissingProperties() {
        boolean missing = false;
        for (Object key : defaultValues.keySet()) {
            if (!this.containsKey(key)) {
                missing = true;
                String value = defaultValues.get(key);
                cli.warning(key + " not specified. Using default (" + value + ")");
                this.setProperty((String) key, value);
            }
        }
        return missing;
    }

    /**
     * Parses variables from the property strings.
     */
    protected abstract void parseProperties();

    protected void parsingError(String property) {
        String defString = defaultValues.get(property);
        this.setProperty(property, defString);
        cli.warning("Parsing " + property + " failed. Using default (" + defString + ")");
    }

    //TODO add info msg for generating new files
    private void saveProperties(String filename) {
        try {
            if (output == null) output = new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            cli.error("Could not find file '" + filename + "'");
        } catch (SecurityException e) {
            cli.error("Could not open file '" + filename + "'. Insufficient write permissions");
        }
        try {
            this.store(output, "TODO");
        } catch (IOException e) {
            cli.error("Could not write on '" + filename + "'");
        } finally {
            try {
                output.close();
            } catch (IOException e) {
                cli.error("Could not save file '" + filename + "'");
            }
        }
    }

    /*
    public Enumeration keys() {
        Enumeration keysEnum = this.keys();
        Vector<String> keyList = new Vector<>();
        while (keysEnum.hasMoreElements()){
            keyList.add((String)keysEnum.nextElement());
        }
        Collections.sort(keyList);
        return keyList.elements();
    }
    */
}
