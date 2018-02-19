package slimefinder.properties;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public abstract class AbstractProperties extends Properties {

	private static final long serialVersionUID = 1L;
	
	public void loadProperties(String filename) throws IOException {
		FileReader reader = new FileReader(filename);
		load(reader);
		reader.close();
		System.out.println("Successfully loaded properties from file: '" + filename +"'");
	}
	
	protected void defaultWarning(String property, String value) {
		System.out.println("Warning! " + property + " not specified. Using " + value + " (default).");
	}
	
	abstract boolean hasAllProperties();
	
}
