package slimefinder.io.properties;

import java.io.*;
import java.util.Iterator;

import slimefinder.io.CLI;
import slimefinder.util.Position;

public class PropertyLoader {

    protected CLI cli;

    public PropertyLoader(CLI cli) {
        this.cli = cli;
    }

    public AbstractProperties createProperties(AbstractProperties properties) throws IOException {
        readProperties(properties, openInputStream(properties.filename));
        writeProperties(properties, openOutputStream(properties.filename));
        return properties;
    }

    private InputStream openInputStream(String filename) {
        try {
            return new FileInputStream(filename);
        } catch (FileNotFoundException e) {
            cli.info("Could not find file '" + filename + "'. Generating defaults...");
            return null;
        }
    }

    private OutputStream openOutputStream(String filename) throws FileNotFoundException {
        try {
            return new FileOutputStream(filename);
        } catch (FileNotFoundException e) {
            cli.error("Could not open file '" + filename + "'");
            throw e;
        }
    }

    public void readProperties(AbstractProperties properties, InputStream in) {
        if (in != null) {
            loadProperties(properties, in);
            removeUnusedProperties(properties);
        }
        addMissingProperties(properties, in != null);
        parseProperties(properties);
    }

    public void writeProperties(AbstractProperties properties, OutputStream out) throws IOException {
        try {
            properties.store(out, null);
        } catch (IOException e) {
            cli.error("Could not save '" + properties.filename + "'");
            throw e;
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                cli.error("Could not close file '" + properties.filename + "' after writing");
                throw e;
            }
        }
    }

    /**
     * Reads the property strings in from an InputStream and closes the InputStream.
     * @param properties
     * @param in
     */
    private void loadProperties(AbstractProperties properties, InputStream in) {
        try {
            properties.load(in);
        } catch (IOException | IllegalArgumentException e) {
            cli.error("Could not load properties from file '" + properties.filename + "'");
            return;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                cli.error("Could not close file '" + properties.filename + "' after reading");
            }
        }
        cli.info("Successfully loaded properties from file: '" + properties.filename + "'");
    }

    /**
     * Removes unused properties i.e. properties not defined in defaultValues
     */
    private void removeUnusedProperties(AbstractProperties properties) {
        Iterator<Object> iterator = properties.keySet().iterator();
        while (iterator.hasNext()) {
            String key = iterator.next().toString();
            if (!properties.defaultValues.containsKey(key)) {
                cli.warning("Unused property, '" + key + "' in '" + properties.filename + "'");
                iterator.remove();
            }
        }
    }

    /**
     * Tries to parse values from the property-strings read from the input file
     * If parsing fails, the value for that property is taken from the defaults.
     * @param properties
     */
    private void parseProperties(AbstractProperties properties) {
        for (Object key : properties.defaultValues.keySet()) {
            String property = (String) properties.get(key);
            Class klass = properties.defaultValues.get(key).getClass();
            Object value;
            try {
                if (klass.equals(Integer.class)) {
                    value = Integer.parseInt(property);
                } else if (klass.equals(Boolean.class)) {
                    value = Boolean.parseBoolean(property);
                    properties.setProperty((String) key, value.toString());
                } else if (klass.equals(Long.class)) {
                    value = Long.parseLong(property);
                } else if (klass.equals(Position.class)) {
                    value = Position.parsePos(property);
                } else {
                    value = property;
                }
            } catch (NumberFormatException e) {
                Object defaultValue = properties.defaultValues.get(key);
                properties.setProperty((String) key, defaultValue.toString());
                properties.values.put((String) key, defaultValue);
                cli.warning("Parsing " + key + " failed. Using default (" + defaultValue + ")");
                continue;
            }
            properties.values.put((String) key, value);
        }
    }

    /**
     * Adds missing properties for which defaultValues exist
     */
    private void addMissingProperties(AbstractProperties properties, boolean warn) {
        for (Object key : properties.defaultValues.keySet()) {
            if (!properties.containsKey(key)) {
                String value = properties.defaultValues.get(key).toString();
                if (warn) cli.warning(key + " not specified. Using default (" + value + ")");
                properties.setProperty((String) key, value);
            }
        }
    }
}
