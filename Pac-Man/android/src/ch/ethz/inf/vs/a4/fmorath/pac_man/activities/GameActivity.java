package ch.ethz.inf.vs.a4.fmorath.pac_man.activities;

import android.content.Intent;
import android.os.Bundle;

import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;

import ch.ethz.inf.vs.a4.fmorath.pac_man.Game;

public class GameActivity extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		final Game game = Game.getInstance();
		initialize(game, config);

		final Intent intent = new Intent(this, ScoreActivity.class);
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (!game.hasEnded()) {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				startActivity(intent);
			}
		}).start();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent(this, MainActivity.class);
		startActivity(intent);
		// TODO: Disconnect client from server / stop server
	}
}
