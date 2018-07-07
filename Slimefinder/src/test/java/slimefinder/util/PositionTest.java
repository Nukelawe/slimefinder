package slimefinder.util;


import static org.junit.Assert.*;
import static slimefinder.util.FormatHelper.CHUNK_SEP;
import static slimefinder.util.FormatHelper.COORD_SEP;

import org.junit.*;

public class PositionTest {

    @Test
    public void testParse() {
        String parsingFailures[] = {
            "",
            "h2,3",
            "--2,3",
            "-32,31,4",
            ",54,76",
            "5.2,4.0",
            "0:0,0:0:0",
            "-1000, 1000"};
        for (String parsingFailure : parsingFailures) {
            try {
                Position.parsePos(parsingFailure);
                fail("Expected a NumberFormatException to be thrown with the input " + parsingFailure);
            } catch (NumberFormatException e) {

            }
        }

        assertEquals(new Position(-1000, 1000), Position.parsePos("-1000,1000"));
        assertEquals(new Position(15, -50), Position.parsePos("\t  15,-50 \t "));
        assertEquals(new Position(3 * 16 + 3, -2 * 16 + 15), Position.parsePos("3" + CHUNK_SEP + "3" + COORD_SEP + "-2" + CHUNK_SEP + "15"));
    }

    @Test
    public void longMoveSequencesWork() {
        Position p = new Position(0, 0);
        int count = 500;

        for (int j = 0; j < count/2; j++) {
            p.moveBy(1,Direction.SOUTH);
            for (long i = 0; i < count; i++) {
                p.moveBy(1,Direction.EAST);
            }
            p.moveBy(1,Direction.SOUTH);
            for (long i = 0; i < count; i++) {
                p.moveBy(1,Direction.WEST);
            }
        }

        assertEquals(new Position(0, count), p);
    }
}
