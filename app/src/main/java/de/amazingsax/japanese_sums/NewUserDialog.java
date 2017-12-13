package de.amazingsax.japanese_sums;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Zum Anmelden eines neuen Users auf dem Highscoreserver
 * bzw um sich als anderen user anzumelden
 */

public class NewUserDialog extends DialogFragment implements OnClickListener, OnHighscoreChangeListener {

    Context context;
    String username;
    EditText usernameField;
    boolean sucess = false;
    boolean interneterror = false;
    int points;
    HighScoreCommunicator highScoreCommunicator;

    public NewUserDialog() {
    }

    public static NewUserDialog newInstance(Context context) {
        NewUserDialog f = new NewUserDialog();
        f.highScoreCommunicator = HighScoreCommunicator.getInstace();
        f.highScoreCommunicator.addOnHighscoreChangeListener(f);
        f.context = context;
        return f;
    }

    @Override
    public void highScoreChanged(HighScoreEvent event) {
        if (event.getResponse() == null) {
            Toast toast = Toast.makeText(context, R.string.internetError, Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (event.getEtype() == HighScoreEvent.HEventType.POINTS) {


            if (event.getResponse().equals("no such user\n")) {
                showToast(getResources().getString(R.string.nosuchUser));
            } else {
                username = username.replace("+", " ");
                showToast(getResources().getString(R.string.angemeldet) + " " + username);
                points = 0;
                saveUsernameLokal();
                NewUserDialog.this.dismiss();
            }
        } else if (event.getEtype() == HighScoreEvent.HEventType.NEWUSER) {
            if (event.getResponse().equals("true\n")) {
                username = username.replace("+", " ");
                showToast(getResources().getString(R.string.angemeldet) + " " + username);
                points = 0;
                saveUsernameLokal();
                NewUserDialog.this.dismiss();
            } else {
                showToast(getResources().getString(R.string.nameExisitiert));
            }
        }
    }

  

    /**
     * Gibt Medung aus
     *
     * @param s Die Meldung
     */
    void showToast(String s) {
        Toast toast = Toast.makeText(context, s, Toast.LENGTH_LONG);
        toast.show();
    }

    /**
     * Intialisiert die Vie
     *
     * @param inflater           Android System parameter
     * @param container          Android System paramter
     * @param savedInstanceState Android System Parameter
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getDialog().setTitle(R.string.userName);
        View view = inflater.inflate(R.layout.newuserdialog, container,
                false);
        Button newuserButton = (Button) view.findViewById(R.id.newuserButton);
        Button loginButton = (Button) view.findViewById(R.id.loginButton);
        Button offlineButton = (Button) view.findViewById(R.id.offlineButton);
        usernameField = (EditText) view.findViewById(R.id.usernameEdit);
        StartActivity start = (StartActivity) context;
        username = start.getUsername();
        if (username == "Anonymous") username = "";
        usernameField.setText(username);
        newuserButton.setOnClickListener(this);
        loginButton.setOnClickListener(this);
        offlineButton.setOnClickListener(this);


        return view;
    }


    /**
     * Wertet klick aus und führt entsprechnende Aktion durch
     *
     * @param v View die Event auslöst (standard Android)
     */
    @Override
    public void onClick(View v) {
        username = usernameField.getText().toString();
        username = username.replace(" ", "+");
        switch (v.getId()) {
            case R.id.newuserButton:
                if (username.length() > 0)
                    addNewUser();
                break;
            case R.id.loginButton:
                if (username.length() > 0)
                    loginasuser();
                break;
            case R.id.offlineButton:
                setOfflineGame();
                username = null;
                this.dismiss();
        }

    }


    //startet async Task für Anmeldung als bestehender User users
    private void loginasuser() {
        //new LoginAsUser().execute();

        highScoreCommunicator.loginAsUser(username);
    }

    //Startet Async Task für Anmeldung als neuer User
    private void addNewUser() {
        highScoreCommunicator.tryAddNewUser(username);
    }


    /**
     * uPDATE Activity und beendet Dialog
     *
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        StartActivity start = (StartActivity) context;
        start.updateUserdata();
        highScoreCommunicator.removeOnHighscoreChangeListener(this);
        super.onDismiss(dialog);
    }

    /**
     * Speichere aktuellen Usernamen auf Gerät
     */
    public void saveUsernameLokal() {
        SharedPreferences pref = context.getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("ONLINE", true);
        editor.putString("USERNAME", username);
        editor.putInt("POINTS", points);
        editor.commit();
    }


    /**
     * Offline Spiel ohne Highscoreliste starten
     */
    public void setOfflineGame() {
        SharedPreferences pref = context.getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putBoolean("ONLINE", false);
        editor.commit();

    }

}
