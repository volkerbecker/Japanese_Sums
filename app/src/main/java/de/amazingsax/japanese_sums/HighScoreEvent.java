package de.amazingsax.japanese_sums;

import java.util.EventObject;

/**
 * Created by becker on 12/8/17.
 */

public class HighScoreEvent extends EventObject {
    public static enum HEventType {
        HIGHSCORE , POINTS
    }
    private String response;
    private HighScoreCommunicator communicator;
    private HEventType etype;

    public HEventType getEtype() {
        return etype;
    }

    public void setEtype(HEventType etype) {
        this.etype = etype;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public HighScoreEvent(HighScoreCommunicator communicator) {
        super(communicator);
        this.communicator=communicator;
    }

}
