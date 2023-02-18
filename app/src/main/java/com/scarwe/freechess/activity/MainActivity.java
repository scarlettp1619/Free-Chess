package com.scarwe.freechess.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.scarwe.freechess.R;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button startButton = (Button) findViewById(R.id.start_button);

        startButton.setOnClickListener((v) -> {
            Context context = MainActivity.this;

            Class<ChessActivity> destinationActivity = ChessActivity.class;

            Intent intent = new Intent(context, destinationActivity);

            startActivity(intent);
        });
    }
}