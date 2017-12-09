package de.amazingsax.japanese_sums;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import java.util.EventObject;

import static android.text.TextUtils.split;

/**
 * Die Activity mit der dioe App startet, hier werden alle Einstellungen vorgenommen
 */
public class StartActivity extends Activity implements OnClickListener, OnHighscoreChangeListener {

    final int maxplayfieldSize = 15; // gibt eignetlich keinen zwingenden Grund
    final int maxnumber = 9; // aber wenn zweistellige Zahlen erlaubt waeren wirds unübersichtlich

    String username = "";
    int points = 0;
    HighScoreCommunicator highScoreCommunicator;


    boolean online = false;

    NumberPicker pickerPlayfieldSize;
    NumberPicker pickerMaxNumber;

    Button playButton;
    Button solveButton;


    void showToast(String s) {
        Toast toast = Toast.makeText(this, s, Toast.LENGTH_LONG);
        toast.show();
    }


    public String getUsername() {
        return username;
    }


    public void setUsername(String username) {
        this.username = username;
    }


    public boolean isOnline() {
        return online;
    }


    public void setOnline(boolean online) {
        this.online = online;
    }


    OnValueChangeListener valueChangeListener = new OnValueChangeListener() {

        @Override
        public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
            switch (picker.getId()) {
                case R.id.playfieldSizePicker:
                    int newMaxNumberValue = newVal > 10 ? 9 : newVal - 1;
                    pickerMaxNumber.setValue(newMaxNumberValue);
                    // pickerMaxNumber.setMaxValue(newMaxNumberValue); // Die
                    // maximale Ziffernanzahl darf nicht groesser als das Spielfeld
                    // sein
            } // Eigentlich eine unnötige einschränkung - deshalb auskommentiert
        }
    };

    /**
     * aCTIVITY INITIALISIEREN
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        pickerPlayfieldSize = (NumberPicker) findViewById(R.id.playfieldSizePicker);
        pickerMaxNumber = (NumberPicker) findViewById(R.id.maxNumberPicker);

        //Voreingestellte Werte setzen
        pickerPlayfieldSize.setMinValue(3);
        pickerPlayfieldSize.setMaxValue(maxplayfieldSize);
        pickerPlayfieldSize.setValue(6);
        pickerPlayfieldSize.setWrapSelectorWheel(false);
        pickerPlayfieldSize.setOnValueChangedListener(valueChangeListener);

        pickerMaxNumber.setMinValue(1);
        pickerMaxNumber.setMaxValue(maxnumber);
        pickerMaxNumber.setValue(5);
        pickerMaxNumber.setWrapSelectorWheel(false);

        playButton = (Button) findViewById(R.id.newGameButton);
        solveButton = (Button) findViewById(R.id.solveButton2);
        playButton.setOnClickListener(this);
        solveButton.setOnClickListener(this);

        highScoreCommunicator = HighScoreCommunicator.getInstace();
        highScoreCommunicator.addOnHighscoreChangeListener(this);

        Runtime rt = Runtime.getRuntime();
        long maxMemory = rt.maxMemory();
        Log.v("amazing", "maxMemory:" + Long.toString(maxMemory));

        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        int memoryClass = am.getMemoryClass();
        Log.v("amazing", "memoryClass:" + Integer.toString(memoryClass));

        loadSettings();
        if (online == true  && username == null) {
            showNewUserDialog();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.settingsmenu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.loginoption:
                showNewUserDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showNewUserDialog() {
        FragmentManager fm = getFragmentManager();
        NewUserDialog newUser = NewUserDialog.newInstance(this);
        newUser.setCancelable(false);
        newUser.show(fm, "new user");
    }


    @Override
    protected void onResume() {
        super.onResume();
        updateUserdata();

    }

    private void loadSettings() {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        username = pref.getString("USERNAME", null);
        online = pref.getBoolean("ONLINE", true);
        if (!online) {
            points = 0;
        } else {
            highScoreCommunicator.readHighscore(2, 1);
            highScoreCommunicator.loginAsUser(username);
        }
    }

    public void updateUserdata() {
        loadSettings();
        TextView tv1 = (TextView) findViewById(R.id.highscores);
        TextView yourscore = (TextView) findViewById(R.id.textView4);
        if (!online) {
            username = "Anonymous";
            tv1.setVisibility(View.INVISIBLE);
            yourscore.setVisibility(View.INVISIBLE);
        } else {
            tv1.setVisibility(View.VISIBLE);
            yourscore.setVisibility(View.VISIBLE);
        }

        TextView welcome = (TextView) findViewById(R.id.welcome);
        welcome.setText(getResources().getString(R.string.welcome) + " " + username);

        if (!online) ;
        else {
            tv1.setVisibility(View.VISIBLE);
        }
    }


    private int readHighscore() {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        return pref.getInt("HIGHSCORE", 0);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            addHighscore updatePoints = new addHighscore(this);
            updatePoints.setPointsToAdd(resultCode);
            updatePoints.setUsername(username);
            if (online) updatePoints.execute();
            points += resultCode;
            saveSettings();
            updateUserdata();
        }
    }


    private void saveSettings() {
        SharedPreferences pref = getSharedPreferences("GAME", 0);
        SharedPreferences.Editor editor = pref.edit();
        if (points > readHighscore()) {
            editor.putInt("HIGHSCORE", readHighscore());
        }
        editor.putInt("POINTS", points);
        editor.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.solveButton2:
                Intent intent = new Intent(this, PlayfieldActivity.class);
                intent.putExtra("playfieldSize", pickerPlayfieldSize.getValue());
                intent.putExtra("maxNumber", pickerMaxNumber.getValue());
                intent.putExtra("playAsGame", false);
                startActivity(intent); // Activity Spielfeld als löser starten.
                break;
            case R.id.newGameButton:
                Intent intent2 = new Intent(this, PlayfieldActivity.class);
                intent2.putExtra("playfieldSize", pickerPlayfieldSize.getValue());
                intent2.putExtra("maxNumber", pickerMaxNumber.getValue());
                intent2.putExtra("playAsGame", true);
                startActivityForResult(intent2, 1); // Activity Spielfeld als Rätsel starten
//			Toast toast = Toast.makeText(this,R.string.notimplementes,Toast.LENGTH_LONG);
//			toast.show();
                break;
            default:
                break;
        }

    }

    @Override
    public void highScoreChanged(HighScoreEvent event) {
        String answer = event.getResponse();
        if (event.getEtype() == HighScoreEvent.HEventType.HIGHSCORE) {
            String result[] = split(answer, "\n");
            String champion[] = split(result[0], ",");
            String s2 = this.getResources().getString(R.string.highscore);
            if (champion.length == 2) {
                s2 += " " + champion[1] + " (" + champion[0] + ")";
                s2 = s2.replace("\n", "");
            } else {
                s2 += "???";
            }
            TextView tv = (TextView) findViewById(R.id.highscores);
            tv.setText(s2);
        } else if (event.getEtype() == HighScoreEvent.HEventType.POINTS) {
            if (answer != "no such user") {
                answer=answer.replace("\n","");
                points = Integer.parseInt(answer);
                TextView yourscore = (TextView) findViewById(R.id.textView4);
                String s = getResources().getString(R.string.yourscore) + " " + Integer.toString(points);
                yourscore.setText(s);
            }
        }


    }
}


