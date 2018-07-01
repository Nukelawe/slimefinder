/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package slimefinder.core.search;

import org.junit.Test;
import static org.junit.Assert.*;

import slimefinder.util.Position;

/**
 *
 * @author Matias Ruotsalainen
 */
public class SearchPathTest {

    @Test
    public void positionIsNullBeforeSteps() {
        SearchPath path = new SearchPath(new Position(0, 0), 0, 3);
        assertNull(path.getPosition());
    }
    
    @Test
    public void startPosIsCorrectNoSkip() {
        SearchPath path = new SearchPath(new Position(0, 0), 0, 3);
        path.step();
        assertEquals(new Position(0, 0), path.getPosition());
    }
    
    @Test
    public void startPosIsCorrectWithSkip() {
        SearchPath path = new SearchPath(new Position(0, 0), 2, 3);
        path.step();
        assertEquals(new Position(-1, 1), path.getPosition());
    }
    
    @Test
    public void firstStepReturnsTrue() {
        SearchPath path = new SearchPath(new Position(0, 0), 0, 3);
        assertTrue(path.step());
    }
    
    @Test
    public void firstStepReturnsFalseIfSearchAreaIsEmpty() {
        SearchPath path = new SearchPath(new Position(0, 0), 3, 3);
        assertFalse(path.step());
    }
    
    
    
    @Test
    public void stepReturnsFalseWhenExitingSearchArea() {
        SearchPath path = new SearchPath(new Position(0, 0), 0, 3); // 3 by 3 area around (0,0)
        for (int i = 0; i < 9; i++) {
            path.step();
        }
        assertFalse(path.step());
    }

    @Test
    public void pathLengthIsCorrect() {
        SearchPath path = new SearchPath(new Position(0, 0), 0, 3);
        assertEquals(9, path.getPathLength());
    }
    
    @Test
    public void pathIsClockwiseSpiralNoSkip() {
        /* Path shape. 1 marks start position.
            7 8 9
            6 1 2
            5 4 3
        */
        SearchPath path = new SearchPath(new Position(0, 0), 0, 3);
        String actualPath = "";
        String expectedPath = 
                new Position(0, 0).toString() + " " +
                new Position(1, 0).toString() + " " +
                new Position(1, 1).toString() + " " +
                new Position(0, 1).toString() + " " +
                new Position(-1, 1).toString() + " " +
                new Position(-1, 0).toString() + " " +
                new Position(-1, -1).toString() + " " +
                new Position(0, -1).toString() + " " +
                new Position(1, -1).toString() + " ";
        
        for (int i = 0; i < 9; i++) {
            path.step();
            actualPath += path.getPosition().toString() +  " ";
        }
        
        assertEquals(expectedPath, actualPath);
    }
    
    @Test
    public void pathIsClockwiseSpiralWithSkip() {
        /* Path shape. 1 marks start position. o marks center position
            3 4 5
            2 o -
            1 - -
        */
        SearchPath path = new SearchPath(new Position(0, 0), 2, 3);
        String actualPath = "";
        String expectedPath = 
                new Position(-1, 1).toString() + " " +
                new Position(-1, 0).toString() + " " +
                new Position(-1, -1).toString() + " " +
                new Position(0, -1).toString() + " " +
                new Position(1, -1).toString() + " ";
        
        for (int i = 0; i < 5; i++) {
            path.step();
            actualPath += path.getPosition().toString() +  " ";
        }
        
        assertEquals(expectedPath, actualPath);
    }
    
    @Test
    public void progressReturnsNumberOfSteps() {
        SearchPath path = new SearchPath(new Position(0, 0), 0, 3);
        for (int i = 0; i < 7; i++) {
            path.step();
        }
        
        assertEquals(7L, path.getProgress());
    }

    @Test
    public void pathWorksIdenticallyAfterReset() {
        SearchPath path1 = new SearchPath(new Position(0, 0), 0, 3);
        SearchPath path2 = new SearchPath(new Position(0, 0), 0, 3);
        for (int i = 0; i < 2; i++) {
            path1.step();
            path2.step();
        }
        for (int i = 0; i < 10; i++) {
            path1.step();
        }

        assertEquals(path2.getProgress(), path1.getProgress());
        assertEquals(path2.step(), path1.step());
        assertEquals(path2.getPosition(), path2.getPosition());
    }


    @Test
    public void largePathsFinishNormally() {
        int maxWidht = 50000;
        SearchPath path = new SearchPath(new Position(0, 0), 0, maxWidht);

        for (long i = 0; i < (long) maxWidht * (long) maxWidht; i++) {
            //System.err.println("position=" + path.getPosition() + ", progress=" + path.getProgress());
            path.step();
        }

        Position p = path.getPosition();
        assertEquals(new Position(-(maxWidht / 2 - 1), maxWidht/2), p);
    }
}
