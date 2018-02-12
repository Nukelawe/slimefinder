package slimefinder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Random;

import slimefinder.properties.SlimeProperties;
import slimefinder.util.Direction;
import slimefinder.util.Position;

public class Mask {
	/**
	 * The radii of the cluster in chunks and in blocks.
	 */
	public static final int 
		R_CHUNK = 8, 
		R_BLOCK = 128;
	
	private boolean needsWeightUpdate;
	
	/**
	 * The squares of the exclusion sphere and despawn sphere radii
	 */
	private int 
		rExclusion,
		rDespawn;
	
	/**
	 * Total surface area under the mask in blocks and chunks
	 */
	private int 
		blockSurfaceArea, 
		chunkSurfaceArea;
	/**
	 * Total surface area of slime chunks under the mask in blocks and chunks
	 */
	private int 
		blockSize, 
		chunkSize;
	
	/**
	 * Block, chunk and within-chunk positions of the center of the mask
	 */
	public Position 
		posBlock, 
		posChunk, 
		posIn;
	
	/**
	 * This is the area in which slime chunks are counted. The values in the array are the number of blocks in the chunk that are
	 * inside the area. I.e. 256 means that the entire chunk is inside the mask and 0 means that the entire chunk is outside.
	 */
	private int[][] chunkWeights;
	private SlimeProperties p;
	private Random r;
	private Collection<Position> slimeChunks;
	
	public Mask(SlimeProperties properties) {
		slimeChunks = new HashSet<>();
		chunkWeights = new int[2*R_CHUNK + 1][2*R_CHUNK + 1];
		p = properties;
		r = new Random();
		rExclusion = 24 * 24 - Math.min(p.yOffset * p.yOffset, 24 * 24);
        rDespawn = 128 * 128 - p.yOffset * p.yOffset;
        posBlock = Position.origin();
        posChunk = Position.origin();
        posIn = Position.origin();
        updateWeights();
	}
	
	public Mask(Mask m) {
		this(m.p);
		moveTo(m);
	}
	
	/**
	 * Calculates the weight array that determines the weights for each chunk. 
	 * Calculates the surface area of this cluster. This has to be called whenever the position within the chunk (posIn) changes.
	 */
	private void updateWeights() {
		int xIn, zIn, xChunk, zChunk, chunkWeight;
		needsWeightUpdate = false;
		blockSurfaceArea = 0;		
		chunkSurfaceArea = 0;
		for(xChunk = -R_CHUNK; xChunk <= R_CHUNK; xChunk++) {
			for(zChunk = -R_CHUNK; zChunk <= R_CHUNK; zChunk++) {
				chunkWeight = 0;
				for(xIn = 0; xIn <= 15; xIn++) {
					for(zIn = 0; zIn <= 15; zIn++) {
						if (isBlockInside(16 * xChunk + xIn, 16 * zChunk + zIn)) {
							++chunkWeight;
						}
					}
				}
				chunkWeights[xChunk+R_CHUNK][zChunk+R_CHUNK] = chunkWeight;
				blockSurfaceArea += chunkWeight;
				if (chunkWeight >= p.minChunkWeight) 
					++chunkSurfaceArea;
			}
		}
		updateSizes();
	}
	

	/**
	 * Calculates the number of slime chunks in blocks and in chunks under the mask.
	 */
	private void updateSizes() {
		int xChunk, zChunk;
		
		blockSize = 0;
		chunkSize = 0;
		slimeChunks.clear();
		for(xChunk = -R_CHUNK; xChunk <= R_CHUNK; xChunk++) {
			for(zChunk = -R_CHUNK; zChunk <= R_CHUNK; zChunk++) {
				if (isSlimeChunk(xChunk + posChunk.x, zChunk + posChunk.z)) {
					slimeChunks.add(new Position(xChunk + posChunk.x, zChunk + posChunk.z));
					blockSize += chunkWeights[xChunk + R_CHUNK][zChunk + R_CHUNK];
					if (isChunkInside(xChunk, zChunk)) ++chunkSize;
				}
			}
		}
	}
	
	/**
	 * Checks if the chunk at position (xChunk,zChunk) with respect to the center of the mask
	 * is inside the mask. To be considered inside the number of blocks in the chunk that are 
	 * inside the mask has to be larger than the minimum weight. The minimum weight
	 * can be adjusted in the properties-file. It can have values in the range [1, 256].
	 * @param xChunk
	 * @param zChunk
	*/
	public boolean isChunkInside(int xChunk, int zChunk) {
		return chunkWeights[xChunk + R_CHUNK][zChunk + R_CHUNK] >= p.minChunkWeight;
	}	

	/**
	 * Checks if the block at position (x,z) with respect to the center of the mask is inside the mask. 
	 * To be considered inside the position has to be within the despawn sphere, outside the exclusion 
	 * sphere and within eligible chunks. All three requirements can be turned off individually in the properties-file.
	 * @param x
	 * @param z
	*/
	public boolean isBlockInside(int x, int z) {
		int dsqr = (x - posIn.x) * (x - posIn.x) + (z - posIn.z) * (z - posIn.z);
		int dmax = Math.max(Math.abs(Math.floorDiv(x, 16)), Math.abs(Math.floorDiv(z, 16)));
		if (p.despawnSphere && dsqr > rDespawn) return false;
		if (p.exclusionSphere && dsqr <= rExclusion) return false;
		if (p.eligibleChunks && dmax >= R_CHUNK) return false;
		return true;
	}
	
	/**
	 * This method is copied directly from the source code of Minecraft
	 * @param xChunk - chunk x coordinate
	 * @param zChunk - chunk z coordinate
	 * @return true if (xChunk, zChunk) is a slime chunk, false otherwise
	 */
	public boolean isSlimeChunk(int xChunk, int zChunk) {
		r.setSeed(p.worldSeed + (long)(xChunk * xChunk * 4987142) + (long)(xChunk * 5947611) + (long)(zChunk * zChunk) * 4392871L + (long)(zChunk * 389711) ^ 987234911L);
    	return r.nextInt(10) == 0;
	}
	
	/**
	 * Sets the position of the mask in block coordinates.
	 * @param xChunk
	 * @param zChunk
	 */
	public void moveTo(int xChunk, int zChunk, int xIn, int zIn) {
		needsWeightUpdate = !(posIn.x == xIn && posIn.z == zIn);
		boolean needsSizeUpdate = !(posBlock.x == 16 * xChunk + xIn && posBlock.z == 16 * zChunk + zIn);
		posBlock.setPos(16 * xChunk + xIn, zChunk * 16 + zIn);
		posChunk.setPos(xChunk, zChunk);
		posIn.setPos(xIn, zIn);
		
		if (needsWeightUpdate) updateWeights();
		if (needsSizeUpdate) updateSizes();
	}
	
	/**
	 * Sets the position of the mask.
	 * @param m
	 */
	public void moveTo(Mask m) {
		needsWeightUpdate = posIn != m.posIn;
		posBlock.setPos(m.posBlock.x, m.posBlock.z);
		posChunk.setPos(m.posChunk.x, m.posChunk.z);
		posIn.setPos(m.posIn.x, m.posIn.z);
		blockSize = m.blockSize;
		chunkSize = m.chunkSize;
		blockSurfaceArea = m.blockSurfaceArea;
		chunkSurfaceArea = m.chunkSurfaceArea;
	}
	
	/** 
	 * Moves the mask by 1 chunk in the given direction.
	 * Recalculates the number of slime chunks using a more efficient algorithm.
	 * @param d - direction of movement
	*/	
	public void moveByChunk(Direction d) {
		int i, xChunk, zChunk;
		for (i = -R_CHUNK; i <= R_CHUNK; i++) {
			xChunk = (i*d.z) - d.x*R_CHUNK;
			zChunk = (i*d.x) - d.z*R_CHUNK;
			if (isSlimeChunk(xChunk + posChunk.x, zChunk + posChunk.z))
				slimeChunks.remove(new Position(xChunk + posChunk.x, zChunk + posChunk.z));
		}
		
		posChunk.move(1, d);
		posBlock.move(16, d);
		
		for (i = -R_CHUNK; i <= R_CHUNK; i++) {
			xChunk = (i*d.z) + d.x*R_CHUNK;
			zChunk = (i*d.x) + d.z*R_CHUNK;
			if (isSlimeChunk(xChunk + posChunk.x, zChunk + posChunk.z))
				slimeChunks.add(new Position(xChunk + posChunk.x, zChunk + posChunk.z));
		}
		
		blockSize = 0;
		chunkSize = 0;
		for (Position p : slimeChunks) {
			blockSize += chunkWeights[p.x - posChunk.x + R_CHUNK][p.z - posChunk.z + R_CHUNK];
			if (isChunkInside(p.x - posChunk.x, p.z - posChunk.z)) ++chunkSize;
		}
	}
	
	public int getBlockSurfaceArea() {
		return blockSurfaceArea;
	}
	
	public int getChunkSurfaceArea() {
		return chunkSurfaceArea;
	}

	public int getBlockSize() {
		return blockSize;
	}
	
	public int getChunkSize() {
		return chunkSize;
	}
}
