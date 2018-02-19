package slimefinder.properties;

import java.io.IOException;

import slimefinder.Mask;
import slimefinder.util.Position;

public class SearchProperties extends AbstractProperties {
	
	private static final long 
		serialVersionUID = -2548006445908326173L;
	public static final String 
		POS_BLOCK = "pos-block",
		POS_CHUNK = "pos-chunk",
		POS_IN = "pos-in",
		MIN_WIDTH = "min-width",
		MAX_WIDTH = "max-width",
		FINE_SEARCH = "fine-search",
		MIN_BLOCK_SZ = "min-block-size",
		MAX_BLOCK_SZ = "max-block-size",
		MIN_CHUNK_SZ = "min-chunk-size",
		MAX_CHUNK_SZ = "max-chunk-size",
		OUTPUT = "output-file",
		APPEND = "append";
	private final static String[] propertyNames = {
		POS_BLOCK,
		POS_CHUNK,
		POS_IN,
		MIN_WIDTH,
		MAX_WIDTH,
		FINE_SEARCH,
		MIN_BLOCK_SZ,
		MAX_BLOCK_SZ,
		MIN_CHUNK_SZ,
		MAX_CHUNK_SZ,
		OUTPUT,
		APPEND
	};
	public boolean
		append = false,
		fineSearch = false;
	public int 
		minChunkSize = 0, 
		maxChunkSize = (2 * Mask.R_CHUNK + 1) * (2 * Mask.R_CHUNK + 1), 
		minBlockSize = 0, 
		maxBlockSize = maxChunkSize * 256,
		minWidth = 0,
		maxWidth = 1;
	public String 
		outputDir;
	public Position 
		posChunk = null, 
		posIn = null;
	
	public boolean hasAllProperties() {
		for (int i = 0; i < propertyNames.length; i++) {
			if (!containsKey(propertyNames[i])) return false;
		}
		return true;
	}
	
	public void loadProperties(String filename) throws IOException  {
		super.loadProperties(filename);
		
		Position posBlock = null;
		NumberFormatException ePos = null;
		try {posBlock = Position.parsePos(getProperty(POS_BLOCK));} catch (NumberFormatException e) {
			ePos = e;
		} try {posChunk = Position.parsePos(getProperty(POS_CHUNK));} catch (NumberFormatException e) {
			ePos = e;
		} try {posIn = Position.parsePos(getProperty(POS_IN));} catch (NumberFormatException e) {
			ePos = e;
		}
		
		if (posBlock != null) {
			if (posChunk != null)
				System.out.println("Warning! Extra starting position information given. Ignoring " + POS_CHUNK + ".");
			if (posIn != null)
				System.out.println("Warning! Extra starting position information given. Ignoring " + POS_IN + ".");
			posChunk = new Position(Math.floorDiv(posBlock.x, 16), Math.floorDiv(posBlock.z, 16));
			posIn = new Position(posBlock.x & 15, posBlock.z & 15);
		} else if (posChunk == null || posIn == null) {
			if (ePos != null) throw ePos;
			throw new IOException("Incomplete starting position information. Starting position not specified.");
		}
		
		if ((outputDir = getProperty(OUTPUT)) == null) {
			throw new IOException("Output directory not specified.");
		}
		try {append = Boolean.parseBoolean(getProperty(APPEND));} catch (Exception e) {
			defaultWarning(APPEND, "" + append);
		} try {fineSearch = Boolean.parseBoolean(getProperty(FINE_SEARCH));} catch (Exception e) {
			defaultWarning(FINE_SEARCH, "" + fineSearch);
		}
		
		try {maxWidth = Integer.parseInt(getProperty(MAX_WIDTH));} catch (NumberFormatException e) {
			defaultWarning(MAX_WIDTH, "" + maxWidth);
		} try {minWidth = Integer.parseInt(getProperty(MIN_WIDTH));} catch (NumberFormatException e) {
			defaultWarning(MIN_WIDTH, "" + minWidth);
		} try {minBlockSize = Integer.parseInt(getProperty(MIN_BLOCK_SZ));} catch (NumberFormatException e) {
		} try {maxBlockSize = Integer.parseInt(getProperty(MAX_BLOCK_SZ));} catch (NumberFormatException e) {
		} try {minChunkSize = Integer.parseInt(getProperty(MIN_CHUNK_SZ));} catch (NumberFormatException e) {
		} try {maxChunkSize = Integer.parseInt(getProperty(MAX_CHUNK_SZ));} catch (NumberFormatException e) {
		}
	}
}
