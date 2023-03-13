package com.scarwe.freechess.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.scarwe.freechess.R;
import com.scarwe.freechess.game.BoardGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends Activity {

    private final int bgColor = Color.parseColor("#393939");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setActivityBgColor();

        final Button startButton = findViewById(R.id.start_button);
        final Button newsButton = findViewById(R.id.news_button);
        final Button settingsButton = findViewById(R.id.settings_button);

        startButton.setOnClickListener((v) -> {
            Context context = MainActivity.this;

            Class<ChessActivity> destinationActivity = ChessActivity.class;
            File path = getApplicationContext().getFilesDir();
            File readFrom = new File(path, "config.json");
            StringBuilder config = new StringBuilder();
            if ((int) readFrom.length() == 0) {
                config.append("{");
                config.append("\n    \"PawnMoves\": \"" + "Pawn" + "\",");
                config.append("\n    \"KnightMoves\": \"" + "Knight" + "\",");
                config.append("\n    \"BishopMoves\": \"" + "Bishop" + "\",");
                config.append("\n    \"RookMoves\": \"" + "Rook" + "\",");
                config.append("\n    \"QueenMoves\": \"" + "Rook, Bishop" + "\",");
                config.append("\n    \"KingMoves\": \"" + "King" + "\"\n");
                config.append("}");
                writeConfig(config.toString());
                readFrom = new File(path, "config.json");
            }
            byte[] content = new byte[(int) readFrom.length()];
            try {
                FileInputStream stream = new FileInputStream(readFrom);
                stream.read(content);
                //System.out.println(new String(content));
                BoardGame board = new BoardGame();
                board.config = new String(content);
                board.resetBoard();
            } catch (IOException | CloneNotSupportedException e) {
                //
            }

            Intent intent = new Intent(context, destinationActivity);

            // moves to chess activity
            startActivity(intent);
        });

        newsButton.setOnClickListener((v) -> {
            Context context = MainActivity.this;
            Class<NewsActivity> destinationActivity = NewsActivity.class;
            Intent intent = new Intent(context, destinationActivity);
            startActivity(intent);
        });

        settingsButton.setOnClickListener((v) -> {
            Context context = MainActivity.this;
            Class<SettingsActivity> destinationActivity = SettingsActivity.class;
            Intent intent = new Intent(context, destinationActivity);
            startActivity(intent);
            finish();
        });
    }

    private void writeConfig(String data) {
        try {
            File path = getApplicationContext().getFilesDir();
            FileOutputStream writer = new FileOutputStream(new File(path, "config.json"));
            writer.write(data.getBytes());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void setActivityBgColor() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(bgColor);
    }
}