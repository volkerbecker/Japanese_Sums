package de.amazingsax.japanese_sums;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;


public class HighScoreListActivity extends Activity implements OnHighscoreChangeListener {
    String[] highScoreList;
    ListView highScoreListView;
    ArrayAdapter<String> highScoreListAdapter;
    HighScoreCommunicator communicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score_list);
        communicator = HighScoreCommunicator.getInstace();
        highScoreListView = findViewById(R.id.listview_highscoreliste);
    }

    @Override
    protected void onResume(){
        super.onResume();
        communicator.addOnHighscoreChangeListener(this);
        communicator.readHighscore(100, 0);
    }

    public void highScoreChanged(HighScoreEvent event) {
        if (event.getEtype() == HighScoreEvent.HEventType.HIGHSCORE) {
            String answer=event.getResponse();
            answer=answer.replace(",",": ");
            highScoreList=answer.split("\n");
            for(int i=0; i< highScoreList.length;++i ) {
                highScoreList[i] = Integer.toString(i+1) + ". " + highScoreList[i];
            }

            highScoreListAdapter =
                    new ArrayAdapter<>(
                            this, // Die aktuelle Umgebung (diese Activity)
                            R.layout.list_item_higscoreliste, // ID der XML-Layout Datei
                            R.id.list_item_highscoreliste, // ID des TextViews
                            highScoreList); // Beispieldaten in einer ArrayList
            highScoreListView.setAdapter(highScoreListAdapter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        communicator.removeOnHighscoreChangeListener(this);
    }
}
