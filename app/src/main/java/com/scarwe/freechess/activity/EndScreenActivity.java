package com.scarwe.freechess.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.scarwe.freechess.R;
import com.scarwe.freechess.game.BoardGame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class EndScreenActivity extends AppCompatActivity {

    public static int gameState = 0;
    public static String winner = "";
    private final BoardGame board = new BoardGame();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.7),(int)(height*.3));

        Button resetButton = findViewById(R.id.rematch_button);
        Button exitButton = findViewById(R.id.exit_button);

        resetButton.setOnClickListener(v -> {
            try {
                board.reader = new BufferedReader(new InputStreamReader(getAssets().open("config.json")));
                board.resetBoard();
            } catch (IOException | CloneNotSupportedException e) {
                throw new RuntimeException(e);
            }
            gameState = 0;
            Intent intent = new Intent(this, ChessActivity.class);
            startActivity(intent);
            finish();
        });

        exitButton.setOnClickListener(v -> finish());

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
        findViewById(R.id.end_game).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

        if (gameState == 1) {
            ((TextView) findViewById(R.id.end_game)).setText(String.format("%s wins\nby Checkmate", winner));
        }
        else if (gameState == 2) {
            ((TextView) findViewById(R.id.end_game)).setText("Draw\nby Stalemate");
        }
        else if (gameState == 3) {
            ((TextView) findViewById(R.id.end_game)).setText("Draw\nby Repetition");
        }
        else if (gameState == 4) {
            ((TextView) findViewById(R.id.end_game)).setText("Draw\nby Insufficient Material");
        }
        else if (gameState == 5) {
            ((TextView) findViewById(R.id.end_game)).setText("Draw\nby Fifty Move Rule reached");
        } else if (gameState == 6) {
            ((TextView) findViewById(R.id.end_game)).setText(String.format("%s wins\nby Resignation", winner));
        }
    }
}