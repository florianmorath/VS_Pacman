package ch.ethz.inf.vs.a4.fmorath.pac_man.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import ch.ethz.inf.vs.a4.fmorath.pac_man.R;

public class LobbyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        List<String> players = new ArrayList<>();
        players.add("Linus");
        players.add("Markus");
        players.add("Johannes");
        players.add("Stefan");
        players.add("Florian");
        players.add("Tiziano");

        ListView listView = (ListView) findViewById(R.id.player_list);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, R.layout.player_list_item, players);
        listView.setAdapter(arrayAdapter);

        boolean isHost = getIntent().getBooleanExtra(MainActivity.IS_HOST, false);
        if (isHost)
            findViewById(R.id.text_waiting_for_host).setVisibility(View.GONE);
        else
            findViewById(R.id.button_start).setVisibility(View.GONE);
    }

    public void onStartButtonClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
