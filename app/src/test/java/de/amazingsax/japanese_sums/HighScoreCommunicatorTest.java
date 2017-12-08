package de.amazingsax.japanese_sums;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Created by becker on 12/8/17.
 */

public class HighScoreCommunicatorTest {

    @Test
    public void testReadHighscore() {
        HighScoreCommunicator communicator = new HighScoreCommunicator();
        communicator.readHighscore(10,0);
    }
}
