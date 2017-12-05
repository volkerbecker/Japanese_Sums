package de.amazingsax.japanese_sums;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by becker on 12/4/17.
 */ //addiert akutelle Punkte auf Highscoreserver zu bestehenden Punkten
//todo die ganzen Internetkommunikation in eigene Klasse packen
class addHighscore extends AsyncTask<Void, Void, Void> {
    private StartActivity startActivity;
    private boolean interneterror;
    private String[] s;
    private String username;
    private int pointsToAdd;

    public addHighscore(StartActivity startActivity) {
        this.startActivity = startActivity;
    }

    public void setPointsToAdd(int pointsToAdd) {
        this.pointsToAdd = pointsToAdd;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            AndroidHttpClient client = AndroidHttpClient
                    .newInstance("japsum");
            username=username.replace(" ","+");
            HttpGet request = new HttpGet(Constants.highscoreserver
                    + "?game=" + Constants.game + "&name=" + username + "&points=" + Integer.toString(pointsToAdd));
            HttpResponse response = client.execute(request);
            HttpEntity entity = response.getEntity();
            InputStreamReader reader = new InputStreamReader(
                    entity.getContent(), "utf-8");

            String answer = "";
            int c = reader.read();
            while (c != -1) {
                answer += (char) c;
                c = reader.read();
            }
            if (answer.equals("done\n")) {
                interneterror = false;
            } else {
                interneterror = true;
            }

        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalStateException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();


        } catch (IOException e) {
            interneterror = true;
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void result) {

        if (interneterror == true) {
            startActivity.showToast(startActivity.getResources().getString(R.string.internetError));
        }
    }
}
