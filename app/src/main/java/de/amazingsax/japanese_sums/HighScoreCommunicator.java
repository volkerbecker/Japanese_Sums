package de.amazingsax.japanese_sums;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * Klasse zur organisation der Internetzugriffe
 */

public class HighScoreCommunicator {

    private static HighScoreCommunicator highScoreCommunicator;
    private String answer;
    private static final String highscoreserver = "http://august-valor-655.appspot.com/myhighscoreserver";
    private static final String baseParameter = "game=japsum";
    private boolean succes;

    private ArrayList<OnHighscoreChangeListener> listeners = new ArrayList<OnHighscoreChangeListener>();

    private HighScoreCommunicator() {
    }

    ; // Make the constructor private since this is a singleton class

    public boolean isSucces() {
        return succes;
    }

    /**
     * Make sure that there is onle one instance of the highscorecommunicator
     *
     * @return
     */
    public static HighScoreCommunicator getInstace() {
        if (highScoreCommunicator == null) {
            highScoreCommunicator = new HighScoreCommunicator();
        }
        return highScoreCommunicator;
    }

    public boolean addOnHighscoreChangeListener(OnHighscoreChangeListener listener) {
        return listeners.add(listener);
    }

    public boolean removeOnHighscoreChangeListener(OnHighscoreChangeListener listener) {
        return listeners.remove(listener);
    }

    private void onHighscoreChanged(HighScoreEvent event) {
        for (OnHighscoreChangeListener listner : listeners) {
            listner.highScoreChanged(event);
        }
    }


    /**
     * send a request to the server
     *
     * @param requestParameter the request parameter
     * @return returns the servers response
     */
    @org.jetbrains.annotations.Nullable
    private String httpRequest(String requestParameter) {
        String requestString = highscoreserver + "?" + requestParameter;
        String answerString = "";
        BufferedReader buffReader = null;
        HttpURLConnection httpURLConnection = null;


        try {
            URL url = new URL(requestString);
            httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            buffReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = buffReader.readLine()) != null) {
                answerString += line + "\n";
            }
        } catch (MalformedURLException e) {
            Log.e("amazing", "Error ", e);
            return null;

        } catch (IOException e) {
            Log.e("amazing", "Error ", e);
            return null;

        } finally {
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
            try {
                if (buffReader != null) {
                    buffReader.close();
                }
            } catch (final IOException e) {
                Log.e("amazing", "Error closing stream", e);
            }

        }
        return answerString;
    }

    /**
     * Add a new user to the highscoreserver
     *
     * @param u username
     */
    public void addNewUser(String u) {
    }

    ;

    /**
     * login as user and query the user score
     *
     * @param u username
     */
    public void loginAsUser(final String u) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                String username=u;
                username=username.replace(" ","+");
                String paramter = baseParameter + "&getuserpoints=" + username;
                answer = httpRequest(paramter);
                System.out.println(answer);
                return null;
            }

            @Override
            protected void onPostExecute(Void params) {
                succes = (answer != null) && (answer != "");
                if (succes) {
                    HighScoreEvent event = new HighScoreEvent(HighScoreCommunicator.this);
                    HighScoreEvent.HEventType type = HighScoreEvent.HEventType.POINTS;
                    event.setEtype(type);
                    event.setResponse(answer);
                    onHighscoreChanged(event);
                }
            }

        }.execute();
    }

    ;

    /**
     * Read the highscorelist, e. g. readhigscore(10,20) returns a
     * list starting by the 10th best user to the 20th best user
     *
     * @param number number of entrys to read
     * @param offset number of first entry (has no effect yet)
     */
    public void readHighscore(final int number, int offset) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {


                String paramter = baseParameter + "&" + "max=" + Integer.toString(number);
                answer = httpRequest(paramter);
                System.out.println(answer);
                return null;
            }

            @Override
            protected void onPostExecute(Void Result) {
                succes = (answer != null);
                if (succes) {
                    HighScoreEvent event = new HighScoreEvent(HighScoreCommunicator.this);
                    HighScoreEvent.HEventType type = HighScoreEvent.HEventType.HIGHSCORE;
                    event.setEtype(type);
                    event.setResponse(answer);
                    onHighscoreChanged(event);
                }
                super.onPostExecute(Result);
            }

        }.execute();
    }


    ;

    /**
     * adds archieved points to the users score
     *
     * @param u     username
     * @param score new points
     */
    public void addScore(String u, int score) {
    }


}