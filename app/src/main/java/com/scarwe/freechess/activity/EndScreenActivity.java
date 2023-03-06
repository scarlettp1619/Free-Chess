package com.scarwe.freechess.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.scarwe.freechess.R;

public class EndScreenActivity extends AppCompatActivity {

    public static int gameState = 0;
    public static String winner = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_end);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int)(width*.7),(int)(height*.2));

        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.CENTER;
        params.x = 0;
        params.y = -20;

        getWindow().setAttributes(params);
        ((TextView) findViewById(R.id.end_game)).setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

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
        }
    }
}