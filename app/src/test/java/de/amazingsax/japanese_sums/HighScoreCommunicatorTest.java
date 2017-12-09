package de.amazingsax.japanese_sums;

import org.junit.Test;
import static org.junit.Assert.*;
/**
 * Created by becker on 12/8/17.
 */

public class HighScoreCommunicatorTest {

    @Test
    public void testReadHighscore() throws Exception{
        HighScoreCommunicator communicator = HighScoreCommunicator.getInstace();
        communicator.readHighscore(10,0);
        assertTrue(communicator.isSucces());
    }

    @Test
    public void testLoginAsUser() throws Exception{
        HighScoreCommunicator communicator = HighScoreCommunicator.getInstace();
        communicator.loginAsUser("amazing sax"); // exisiting usernam
        assertTrue(communicator.isSucces());
        communicator.loginAsUser("absurderNamemimi"); // exisiting usernam
        assertTrue(communicator.isSucces());
    }
}
