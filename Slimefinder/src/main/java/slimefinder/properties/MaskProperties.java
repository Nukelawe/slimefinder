package slimefinder.properties;

public class MaskProperties extends AbstractProperties{
    
    private static final String SEED = "world-seed";
    private static final String DESPAWN = "despawn-sphere";
    private static final String EXCLUSION = "exclusion-sphere";
    private static final String OFFSET = "y-offset";
    private static final String WEIGHT = "chunk-weight";
    
    public boolean despawnSphere = true, exclusionSphere = true;
    public int yOffset = 0, chunkWeight = 0;
    public long worldSeed = 0;

    public MaskProperties() {
        filename = "mask.properties";
        defaults.setProperty(SEED, "" + worldSeed);
        defaults.setProperty(DESPAWN, "" + despawnSphere);
        defaults.setProperty(EXCLUSION, "" + exclusionSphere);
        defaults.setProperty(OFFSET, "" + yOffset);
        defaults.setProperty(WEIGHT, "" + chunkWeight);
    }
    
    @Override
    protected void parseProperties() throws NumberFormatException {
        try {
            worldSeed = Long.parseLong(properties.getProperty(SEED));
        } catch (NumberFormatException ex) {
            parsingError(SEED);
        }
        
        try {
            yOffset = Integer.parseInt(properties.getProperty(OFFSET));
        } catch (NumberFormatException ex) {
            parsingError(OFFSET);
        }
        
        try {
            chunkWeight = Integer.parseInt(properties.getProperty(WEIGHT));
        } catch (NumberFormatException ex) {
            parsingError(WEIGHT);
        }
        
        despawnSphere = Boolean.parseBoolean(properties.getProperty(DESPAWN));
        properties.setProperty(DESPAWN, "" + despawnSphere);
        
        exclusionSphere = Boolean.parseBoolean(properties.getProperty(EXCLUSION));
        properties.setProperty(EXCLUSION, "" + exclusionSphere);
    }
}
