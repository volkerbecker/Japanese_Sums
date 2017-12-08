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

/**
 * Klasse zur organisation der Internetzugriffe
 */

public class HighScoreCommunicator {

    private String answer;
    private static final String highscoreserver = "http://august-valor-655.appspot.com/myhighscoreserver";
    private static final String baseParameter = "game=japsum";

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
    public void loginAsUser(String u) {
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
                //Log.d("amazing",answer);
                System.out.println(answer);
                return null;
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