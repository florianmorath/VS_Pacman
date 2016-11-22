package ch.ethz.inf.vs.a4.fmorath.pac_man.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import ch.ethz.inf.vs.a4.fmorath.pac_man.R;

public class LobbyActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        boolean isHost = getIntent().getBooleanExtra(MainActivity.IS_HOST, false);
        if (!isHost)
            findViewById(R.id.button_start).setVisibility(View.INVISIBLE);
    }

    public void onStartButtonClick(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        startActivity(intent);
    }
}
