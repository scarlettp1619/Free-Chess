package com.scarwe.freechess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Window;

public class MainActivity extends AppCompatActivity {

    BoardModel board = new BoardModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String tag = "MainActivity";
        Log.d(tag, board.stringBoard());
    }
}