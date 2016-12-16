package ch.ethz.inf.vs.a4.fmorath.pac_man.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import ch.ethz.inf.vs.a4.fmorath.pac_man.R;

public class MainActivity extends Activity {

    public static final String IS_HOST = "is host";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onHostButtonClick(View view) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra(IS_HOST, true);
        startActivity(intent);
    }

    public void onJoinButtonClick(View view) {
        Intent intent = new Intent(this, LobbyActivity.class);
        intent.putExtra(IS_HOST, false);
        startActivity(intent);
    }

    public void onSettingsButtonClick(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
