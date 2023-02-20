package com.scarwe.freechess.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;

import com.scarwe.freechess.R;
import com.scarwe.freechess.game.BoardGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.Buffer;

public class MainActivity extends Activity {

    /*public MainActivity() throws IOException {
    }*/

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        final Button startButton = (Button) findViewById(R.id.start_button);

        startButton.setOnClickListener((v) -> {
            Context context = MainActivity.this;

            Class<ChessActivity> destinationActivity = ChessActivity.class;

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(getAssets().open("config.json")));
                BoardGame board = new BoardGame();
                board.reader = reader;
                board.resetBoard();
            } catch (IOException | CloneNotSupportedException e) {
                //
            }

            Intent intent = new Intent(context, destinationActivity);

            startActivity(intent);
        });
    }
}