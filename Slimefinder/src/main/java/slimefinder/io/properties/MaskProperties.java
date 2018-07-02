package slimefinder.io.properties;

import slimefinder.io.CLI;

import java.io.IOException;

public class MaskProperties extends AbstractProperties{
    
    private static final String SEED = "world-seed";
    private static final String DESPAWN = "despawn-sphere";
    private static final String EXCLUSION = "exclusion-sphere";
    private static final String OFFSET = "y-offset";
    private static final String WEIGHT = "chunk-weight";
    
    public boolean despawnSphere, exclusionSphere;
    public int yOffset, chunkWeight;
    public long worldSeed;

    public MaskProperties() {
    }

    public MaskProperties(String filename) throws IOException {
        super(filename);
    }

    protected void setDefaults() {
        defaultValues.put(SEED, "" + 0);
        defaultValues.put(DESPAWN, "" + true);
        defaultValues.put(EXCLUSION, "" + true);
        defaultValues.put(OFFSET, "" + 0);
        defaultValues.put(WEIGHT, "" + 0);
    }

    @Override
    protected void parseProperties() throws NumberFormatException {
        try {
            worldSeed = Long.parseLong(this.getProperty(SEED));
        } catch (NumberFormatException ex) {
            parsingError(SEED);
        }
        
        try {
            yOffset = Integer.parseInt(this.getProperty(OFFSET));
        } catch (NumberFormatException ex) {
            parsingError(OFFSET);
        }
        
        try {
            chunkWeight = Integer.parseInt(this.getProperty(WEIGHT));
        } catch (NumberFormatException ex) {
            parsingError(WEIGHT);
        }
        
        despawnSphere = Boolean.parseBoolean(this.getProperty(DESPAWN));
        this.setProperty(DESPAWN, "" + despawnSphere);
        
        exclusionSphere = Boolean.parseBoolean(this.getProperty(EXCLUSION));
        this.setProperty(EXCLUSION, "" + exclusionSphere);
    }
}
