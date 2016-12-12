package ch.ethz.inf.vs.a4.fmorath.pac_man.activities;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.IOException;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Game;
import ch.ethz.inf.vs.a4.fmorath.pac_man.Player;
import ch.ethz.inf.vs.a4.fmorath.pac_man.R;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Client;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.Server;
import ch.ethz.inf.vs.a4.fmorath.pac_man.communication.StartSignalHandler;

public class LobbyActivity extends Activity implements StartSignalHandler  {

    Server server = null;
    Client client = null;
    Game game;

    boolean started = false;

    ArrayAdapter<Player> arrayAdapter;

    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);

        game = new Game();

        ListView listView = (ListView) findViewById(R.id.player_list);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.player_list_item, game.getPlayers().toArray());
        listView.setAdapter(arrayAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean isHost = getIntent().getBooleanExtra(MainActivity.IS_HOST, false);
        if (isHost) {
            findViewById(R.id.text_waiting_for_host).setVisibility(View.GONE);
            server = new Server(Integer.parseInt(prefs.getString("host_port","8978")), prefs.getString("username","Jim"));
            server.setStartSignalHandler(this);
            game.setCommunicator(server);
            server.start();
        }
        else {
            findViewById(R.id.button_start).setVisibility(View.GONE);
            String portString = prefs.getString("join_port","8978");
            client = new Client(Integer.parseInt(portString), prefs.getString("username","Jim"));
            client.setStartSignalHandler(this);
            game.setCommunicator(client);
                new Thread(new Runnable(){

                    @Override
                    public void run() {

                        try {
                            client.connectAndStartGame(prefs.getString("join_ip_address", "127.0.0.1"));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();

        }
    }

    /*
    @Override
    protected void onStop() {
        super.onStop();
        if(server != null && !started){
            server.stop();
            server = null;
        }

        client = null;

    } */

    public void onStartButtonClick(View view) {
        server.startGame();
    }

    @Override
    public void receivedStartSignal() {

        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }

    @Override
    public void receivedNewPlayer(String name, int id, boolean isLocal) {
        game.addPlayer(new Player(game, name, isLocal, id));
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                ListView listView = (ListView) findViewById(R.id.player_list);
                arrayAdapter = new ArrayAdapter<Player>(LobbyActivity.this, R.layout.player_list_item, game.getPlayers().toArray());
                listView.setAdapter(arrayAdapter);
            }
        });
    }
}
