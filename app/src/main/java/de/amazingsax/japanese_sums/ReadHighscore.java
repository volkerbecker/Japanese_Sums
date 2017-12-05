package de.amazingsax.japanese_sums;

import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

/**
 * Created by becker on 12/4/17.
 */ // Liest Daten vom Highscoreserver aus,
class ReadHighscore extends AsyncTask<Void, Void, Void> {
    private StartActivity startActivity;
    private boolean interneterror;
    private String[] s;

    public ReadHighscore(StartActivity startActivity) {
        this.startActivity = startActivity;
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            AndroidHttpClient client = AndroidHttpClient
                    .newInstance("japsum");
            HttpGet request = new HttpGet(Constants.highscoreserver
                    + "?game=" + Constants.game + "&max=1");
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
            s = answer.split(",");

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
            s = new String[2];
            s[0] = "???";
            s[1] = "???";
            startActivity.showToast(startActivity.getResources().getString(R.string.internetError));
        }
        String s2;

        s2 = startActivity.getResources().getString(R.string.highscore);
        if(s.length==2) {
            s2 += " " + s[1] + " (" + s[0] + ")";
            s2 = s2.replace("\n", "");
        } else {
          s2+="???";
        }
        TextView tv = (TextView) startActivity.findViewById(R.id.highscores);
        tv.setText(s2);

    }
}
