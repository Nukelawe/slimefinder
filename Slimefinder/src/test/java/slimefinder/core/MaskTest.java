package slimefinder.core;

import org.junit.Before;
import org.junit.Test;

import slimefinder.io.properties.MaskProperties;
import slimefinder.util.Direction;
import slimefinder.util.Position;

import static org.junit.Assert.*;

import static slimefinder.io.properties.MaskProperties.*;

public class MaskTest {
    
    private MaskProperties pMask;
    
    @Before
    public void setUp() {
        pMask = new MaskProperties();
        pMask.setProperty(SEED, 0L);
        pMask.setProperty(DESPAWN, true);
        pMask.setProperty(EXCLUSION, true);
        pMask.setProperty(WEIGHT, 0);
        pMask.setProperty(OFFSET, 0);
    }

    @Test
    public void chunkAndBlockConstructorsGiveSameMasks() {
        Mask mChunk = new Mask(pMask, -1, 0, 1, 2);
        Mask mBlock = new Mask(pMask, -15, 2);
        
        // Position
        assertEquals(mBlock.pos, mChunk.pos);

        // Sizes
        assertEquals(mBlock.getBlockSize(), mChunk.getBlockSize());
        assertEquals(mBlock.getChunkSize(), mChunk.getChunkSize());
        assertEquals(mBlock.getBlockSurfaceArea(), mChunk.getBlockSurfaceArea());
        assertEquals(mBlock.getChunkSurfaceArea(), mChunk.getChunkSurfaceArea());
    }
    
    @Test
    public void orthogonalChunkMovementWorks() {
        Mask m = new Mask(pMask, 0, 0);
        m.moveByChunk(Direction.SOUTH);
        m.moveByChunk(Direction.EAST);
        m.moveByChunk(Direction.NORTH);
        m.moveByChunk(Direction.NORTH);
        m.moveByChunk(Direction.WEST);
        m.moveByChunk(Direction.WEST);
        assertEquals(new Position(-1, -1).chunk, m.pos.chunk);
    }
    
    @Test
    public void arbitraryMovementWorks() {
        Mask m = new Mask(pMask, 0, 0);
        m.moveTo(1, 2, 3, 4);
        assertEquals(new Position(19, 36), m.pos);
    }
    
    @Test
    public void surfaceAreaIsCorrect() {
        Mask m = new Mask(pMask, 0, 0, 0, 0);
        assertEquals(49640, m.getBlockSurfaceArea());
        assertEquals(222, m.getChunkSurfaceArea());
    }

    @Test
    public void steppingAndJumpingMovementHaveSameEffects() {
        Mask mStep = new Mask(pMask, 0, 0, 0, 0);
        Mask mJump = new Mask(pMask, 0, 0, 0, 0);

        Direction stepPath[] = {
            Direction.EAST,
            Direction.EAST,
            Direction.SOUTH,
            Direction.SOUTH,
            Direction.SOUTH,
            Direction.SOUTH,
            Direction.NORTH,
            Direction.SOUTH,
            Direction.WEST,
            Direction.WEST,
            Direction.EAST
        };
        for (Direction direction : stepPath) {
            mStep.moveByChunk(direction);
        }
        mJump.moveTo(1,4,0,0);
        assertTrue(
            mStep.getBlockSize() + "==" + mJump.getBlockSize(),
            mStep.getBlockSize() == mJump.getBlockSize());
        for (int xChunk = -Mask.R_CHUNK; xChunk <= Mask.R_CHUNK; xChunk++) {
            for (int zChunk = -Mask.R_CHUNK; zChunk <= Mask.R_CHUNK; zChunk++) {
                assertEquals(
                    mStep.isSlimeChunk(xChunk, zChunk), mJump.isSlimeChunk(xChunk, zChunk)
                );
            }
        }
    }
}
