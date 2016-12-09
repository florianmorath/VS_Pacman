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

    Server server;
    Client client;
    Game game;

    SharedPreferences prefs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        game = new Game();
/*
        game.addPlayer(new Player(game, "Linus", true));
        game.addPlayer(new Player(game, "Markus", false));
        game.addPlayer(new Player(game, "Johannes", false));
        game.addPlayer(new Player(game, "Stefan", false));
        game.addPlayer(new Player(game, "Florian", false));
*/

        ListView listView = (ListView) findViewById(R.id.player_list);
        ArrayAdapter<Player> arrayAdapter = new ArrayAdapter<>(this, R.layout.player_list_item, game.getPlayers().toArray());
        listView.setAdapter(arrayAdapter);

        boolean isHost = getIntent().getBooleanExtra(MainActivity.IS_HOST, false);
        if (isHost) {
            findViewById(R.id.text_waiting_for_host).setVisibility(View.GONE);
            server = new Server();
            server.setStartSignalHandler(this);
            game.setCommunicator(server);
            server.start();
        }
        else {
            findViewById(R.id.button_start).setVisibility(View.GONE);
            client = new Client();
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



    public void onStartButtonClick(View view) {
        server.startGame();
    }

    @Override
    public void receivedStartSignal(int id, int numPlayers) {
        for(int i=0; i<numPlayers;++i){
            game.addPlayer(new Player(game, "Player " + i, i == id, i ));
        }
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
