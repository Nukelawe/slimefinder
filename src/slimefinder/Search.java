package slimefinder;

import slimefinder.properties.SlimeProperties;
import slimefinder.util.Direction;

import java.io.IOException;

import slimefinder.properties.SearchProperties;

public class Search {		
	private SlimeProperties pSlime;
	public Mask m, maxBlock, minBlock, maxChunk, minChunk;
	
	/**
	 * The current edge length
	 */
	private int edge;
	
	/**
	 * Number of steps taken on the current edge
	 */
	private int steps;
	
	/**
	 * Number of turns since the last edge length increase
	 */
	private int turns;
	

	private SearchProperties pSearch;
	/**
	 * The direction of the next step
	 */
	private Direction dir;
	
	public Search(SearchProperties searchProperties, SlimeProperties slimeProperties) {
		pSearch = searchProperties;
		pSlime = slimeProperties;
		m = new Mask(pSlime);
	}
	/**
	 * Searches the area specified by the search properties for slime chunk clusters that match given criteria.
	 * @return number of positions matching the criteria
	 * @throws IOException 
	 */
	public int search() throws IOException {
		if (pSearch.minWidth >= pSearch.maxWidth) return 0;
		int successCount = 0;
		setStartPos(m, pSearch.posIn.x, pSearch.posIn.z);
		
		if (pSearch.thorough) {
			for (int xIn = 0; xIn < 16; xIn++) {
				for (int zIn = 0; zIn < 16; zIn++) {
					setStartPos(m, xIn, zIn);
					successCount += chunkSearch();
				}
			}
		} else {
			successCount = chunkSearch();
		}
		return successCount;
	}
	
	/**
	 * Searches only one block position in each chunk. Never moves the mask within the chunk.
	 * @return number of positions matching the criteria
	 * @throws IOException
	 */
	public int chunkSearch() throws IOException {
		boolean chunkCriteria, blockCriteria;
		int chunkSize, blockSize, successCount = 0;
		String comment = "";
		if (maxBlock == null) {
			maxBlock = new Mask(m);
			minBlock = new Mask(m);
			maxChunk = new Mask(m);
			minChunk = new Mask(m);
			comment = "maxB,minB,maxC,minC";
		}
		
		while (true) {
			chunkSize = m.getChunkSize();
			blockSize = m.getBlockSize();
			if (maxBlock.getBlockSize() < blockSize) {
				maxBlock.moveTo(m);
				comment += ((comment.length() > 0) ? "," : "") + "maxB";
			}
			if (minBlock.getBlockSize() > blockSize) {
				minBlock.moveTo(m);
				comment += ((comment.length() > 0) ? "," : "") + "minB";
			}
			if (maxChunk.getChunkSize() < chunkSize) {
				maxChunk.moveTo(m);
				comment += ((comment.length() > 0) ? "," : "") + "maxC";
			}
			if (minChunk.getChunkSize() > chunkSize) {
				minChunk.moveTo(m);
				comment += ((comment.length() > 0) ? "," : "") + "minC";
			}
			
			chunkCriteria = chunkSize >= pSearch.minChunkSize && chunkSize <= pSearch.maxChunkSize;
			blockCriteria = blockSize >= pSearch.minBlockSize && blockSize <= pSearch.maxBlockSize;
			
			if (chunkCriteria || blockCriteria) {
				++successCount;
				CLI.println(CLI.data(m), comment, true, true, false);
			}
			comment = "";
			
			if (!stepChunk(m)) break;
		}
		return successCount;
	}
	
	public long timeEstimate(int length) {
		Mask test = new Mask(pSlime);
		test.moveTo(0, 0, 0, 0);
		long time = System.nanoTime();
		for (int i = 0; i < length; i++) {
			test.moveByChunk(Direction.EAST);
		}
		return (System.nanoTime() - time) / length;
	}
	
	/**
	 * Moves the mask by 1 chunk along a spiral path around the starting position.
	 * @return false when moving outside the search region, true otherwise.
	 */
	public boolean stepChunk(Mask m) {		
		m.moveByChunk(dir);
		++steps;
		
		if (edge >= pSearch.maxWidth && steps >= edge)
			return false;
		if (steps >= edge) {
			steps = 0;
			turn();
			if (turns > 1) {
				turns = 0;
				++edge;
			}
		}
		return true;
	}
	
	private void setStartPos(Mask m, int xIn, int zIn) {		
		steps = 0;
		int dx, dz;
		if (pSearch.minWidth <= 0) {
			dir = Direction.EAST;
			dx = 0;
			dz = 0;
			edge = pSearch.minWidth + 1;
			turns = 0;
		} else {
			if (pSearch.minWidth % 2 == 0) {
				dir = Direction.NORTH;
				dx = -pSearch.minWidth/2 * 16;
				dz = pSearch.minWidth/2 * 16;
			} else {
				dir = Direction.SOUTH;
				dx = (pSearch.minWidth+1)/2 * 16;
				dz = -(pSearch.minWidth-1)/2 * 16;
			}
			edge = pSearch.minWidth;
			turns = 1;
		}
		
		m.moveTo(pSearch.posChunk.x + dx, pSearch.posChunk.z + dz, xIn, zIn);
	}
	
	/**
	 * Rotates the direction of the next step clockwise
	 */
	private void turn() {
		if (dir == Direction.NORTH) {
			dir = Direction.EAST;
		} else if (dir == Direction.EAST) {
			dir = Direction.SOUTH;
		} else if (dir == Direction.SOUTH) {
			dir = Direction.WEST;
		} else if (dir == Direction.WEST) {
			dir = Direction.NORTH;
		}
		++turns;
	}
	

}
