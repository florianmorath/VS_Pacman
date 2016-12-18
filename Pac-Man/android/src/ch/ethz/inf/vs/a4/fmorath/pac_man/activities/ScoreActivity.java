package ch.ethz.inf.vs.a4.fmorath.pac_man.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Formatter;
import java.util.List;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Game;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.R;

public class ScoreActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        Game game = Game.getInstance();
        Player[] players = game.getPlayers().toArray(Player.class);
        Arrays.sort(players);

        ListView listView = (ListView) findViewById(R.id.score_list);
        ArrayAdapter<Player> adapter = new ArrayAdapter<Player>(this, R.layout.player_list_item, players) {
            @Override
            public View getView(int position, View view, ViewGroup root) {
                if (view == null) {
                    LayoutInflater inflater = LayoutInflater.from(getContext());
                    view = inflater.inflate(R.layout.score_list_item, root, false);
                }
                Player player = getItem(position);
                if (player != null) {
                    ((TextView) view.findViewById(R.id.text_place)).setText(String.format("%1$s.", position + 1));
                    ((TextView) view.findViewById(R.id.text_name)).setText(player.toString());
                    ((TextView) view.findViewById(R.id.text_score)).setText(String.format("%1$s", player.getScore()));
                }
                return view;
            }
        };
        listView.setAdapter(adapter);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
