/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slimefinder.search;

import slimefinder.Mask;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import slimefinder.properties.MaskProperties;
import slimefinder.util.Direction;
import slimefinder.util.Position;

/**
 *
 * @author Matias Ruotsalainen
 */
public class MaskTest {
    
    private MaskProperties p;
    
    public MaskTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        p = new MaskProperties();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void chunkAndBlockConstructorsGiveSameMasks() {
        Mask mChunk = new Mask(p, -1, 0, 1, 2);
        Mask mBlock = new Mask(p, -15, 2);
        
        // Chunk positions
        assertEquals(mBlock.posChunk, mChunk.posChunk);
        
        // Positions within chunk
        assertEquals(mBlock.posIn, mChunk.posIn);
        
        // Block positions
        assertEquals(mBlock.posBlock, mChunk.posBlock);
        
        // Sizes
        assertEquals(mBlock.getBlockSize(), mChunk.getBlockSize());
        assertEquals(mBlock.getChunkSize(), mChunk.getChunkSize());
        assertEquals(mBlock.getBlockSurfaceArea(), mChunk.getBlockSurfaceArea());
        assertEquals(mBlock.getChunkSurfaceArea(), mChunk.getChunkSurfaceArea());
    }
    
    @Test
    public void blockPosConstructorInitializesPositionsCorrectly() {
        Mask m = new Mask(p, -15, 2);
        
        assertEquals(new Position(-1, 0), m.posChunk);
        assertEquals(new Position(1, 2), m.posIn);
        assertEquals(new Position(-15, 2), m.posBlock);
    }
    
    @Test
    public void orthogonalChunkMovementWorks() {
        Mask m = new Mask(p, 0, 0);
        m.moveByChunk(Direction.SOUTH);
        m.moveByChunk(Direction.EAST);
        m.moveByChunk(Direction.NORTH);
        m.moveByChunk(Direction.NORTH);
        m.moveByChunk(Direction.WEST);
        m.moveByChunk(Direction.WEST);
        assertEquals(new Position(-1, -1), m.posChunk);
    }
    
    @Test
    public void arbitraryMovementWorks() {
        Mask m = new Mask(p, 0, 0);
        m.moveTo(1, 2, 3, 4);
        assertEquals(new Position(19, 36), m.posBlock);
    }
    
    @Test
    public void surfaceAreaIsCorrect() {
        p.yOffset = 0;
        p.despawnSphere = true;
        p.exclusionSphere = true;
        Mask m = new Mask(p, 0, 0, 0, 0);
        assertEquals(49640, m.getBlockSurfaceArea());
        assertEquals(222, m.getChunkSurfaceArea());
    }
        
    @Test
    public void isSlimeChunkTest() {
        long seed = 0;
        assertTrue(Mask.isSlimeChunk(seed, 1,-3));
        assertFalse(Mask.isSlimeChunk(seed, 10,-6));
    }
}
