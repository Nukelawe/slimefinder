package slimefinder.properties;

import java.io.IOException;

public class SlimeProperties extends AbstractProperties {
	private static final long 
		serialVersionUID = 1L;
	public static final String 
		SEED = "world-seed",
		DESPAWN = "despawn-sphere",
		EXCLUSION = "exclusion-sphere",
		ELIGIBLE = "eligible-chunks",
		OFFSET = "y-offset",
		MIN_WEIGHT = "min-chunk-weight";
	private final static String[] propertyNames = {
		SEED,
		DESPAWN,
		EXCLUSION,
		ELIGIBLE,
		OFFSET,
		MIN_WEIGHT
	};
	public boolean 
		despawnSphere = true, 
		exclusionSphere = true, 
		eligibleChunks = true;
	public int 
		yOffset = 0, 
		minChunkWeight = 1;
	public long 
		worldSeed = 0;
	
	public boolean hasAllProperties() {
		for (int i = 0; i < propertyNames.length; i++) {
			if (!containsKey(propertyNames[i])) return false;
		}
		return true;
	}
	
	public void loadProperties(String filename) throws IOException {
		super.loadProperties(filename);
		
		try {worldSeed = Long.parseLong(getProperty(SEED));} catch (NumberFormatException e) {
			defaultWarning(SEED, "" + worldSeed);
		} try {despawnSphere = Boolean.parseBoolean(getProperty(DESPAWN));} catch (Exception e) {
			defaultWarning(DESPAWN, "" + despawnSphere);
		} try {exclusionSphere = Boolean.parseBoolean(getProperty(EXCLUSION));} catch (Exception e) {
			defaultWarning(EXCLUSION, "" + exclusionSphere);
		} try {eligibleChunks = Boolean.parseBoolean(getProperty(ELIGIBLE));} catch (Exception e) {
			defaultWarning(ELIGIBLE, "" + eligibleChunks);
		} try {yOffset = Integer.parseInt(getProperty(OFFSET));} catch (NumberFormatException e) {
			defaultWarning(OFFSET, "" + yOffset);
		} try {minChunkWeight = Integer.parseInt(getProperty(MIN_WEIGHT));} catch (NumberFormatException e) {
			defaultWarning(MIN_WEIGHT, "" + minChunkWeight);
		} 
	}
}
