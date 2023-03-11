package com.scarwe.freechess.activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.scarwe.freechess.R;
import com.scarwe.freechess.game.ChessPiece;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.Console;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class SettingsActivity extends AppCompatActivity {

    private final int bgColor = Color.parseColor("#393939");
    private HashMap<TextView, ArrayList<Integer>> moveSets = new HashMap<>();

    private StringBuilder pawnMoves = new StringBuilder();
    private StringBuilder knightMoves = new StringBuilder();
    private StringBuilder bishopMoves = new StringBuilder();
    private StringBuilder rookMoves = new StringBuilder();
    private StringBuilder queenMoves = new StringBuilder();
    private StringBuilder kingMoves = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_settings);
        setActivityBgColor();

        TextView[] dropDowns = new TextView[]{findViewById(R.id.pawn_box), findViewById(R.id.knight_box),
                findViewById(R.id.bishop_box), findViewById(R.id.rook_box), findViewById(R.id.queen_box),
                findViewById(R.id.king_box)};

        String[] moves = {"Pawn", "Knight", "Bishop", "Rook", "King"};

        for (TextView t : dropDowns) {
            ArrayList<Integer> currentMoveSet = new ArrayList<>();
            boolean[] currentSelectedMoves = new boolean[moves.length];
            t.setOnClickListener(view -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setTitle("Select moves");
                builder.setCancelable(false);
                builder.setMultiChoiceItems(moves, currentSelectedMoves, (dialog, which, isChecked) -> {
                    if (isChecked) {
                        currentMoveSet.add(which);
                        Collections.sort(currentMoveSet);
                    } else {
                        currentMoveSet.remove(Integer.valueOf(which));
                    }
                });

                builder.setPositiveButton("OK", (dialog, which) -> {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (int j = 0; j < currentMoveSet.size(); j++)  {
                        stringBuilder.append(moves[currentMoveSet.get(j)]);
                        if (j != currentMoveSet.size() - 1) {
                            stringBuilder.append(", ");
                        }
                    }
                    if (t == findViewById(R.id.pawn_box)) {
                        pawnMoves = stringBuilder;
                        t.setText("Pawn: " + stringBuilder);
                    } else if (t == findViewById(R.id.bishop_box)) {
                        bishopMoves = stringBuilder;
                        t.setText("Bishop: " + stringBuilder);
                    } else if (t == findViewById(R.id.knight_box)) {
                        knightMoves = stringBuilder;
                        t.setText("Knight: " + stringBuilder);
                    } else if (t == findViewById(R.id.rook_box)) {
                        rookMoves = stringBuilder;
                        t.setText("Rook: " + stringBuilder);
                    } else if (t == findViewById(R.id.queen_box)) {
                        queenMoves = stringBuilder;
                        t.setText("Queen: " + stringBuilder);
                    } else if (t == findViewById(R.id.king_box)) {
                        kingMoves = stringBuilder;
                        t.setText("King: " + stringBuilder);
                    }
                    moveSets.put(t, currentMoveSet);
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

                builder.setNeutralButton("Clear All", (dialog, which) -> {
                    for (int j = 0; j < currentSelectedMoves.length; j++) {
                        currentSelectedMoves[j] = false;
                        currentMoveSet.clear();
                    }
                });

                builder.show();
            });
        }

        final Button applyButton = findViewById(R.id.apply_button);
        final Button cancelButton = findViewById(R.id.cancel_button);

        applyButton.setOnClickListener((v) -> {
            int count = 0;
            StringBuilder config = new StringBuilder();
            config.append("{");
            for (Map.Entry<TextView, ArrayList<Integer>> entry : moveSets.entrySet()) {
                count += 1;
            }
            if (count < 6) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("One or more of your pieces have no moves. Please select moves to continue.");
                alertDialogBuilder.setTitle("Error");
                alertDialogBuilder.setNegativeButton("OK", (dialogInterface, i) -> Log.d("internet","Ok btn pressed"));
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return;
            }

            config.append("\n    \"PawnMoves\": \"" + pawnMoves + "\",");
            config.append("\n    \"KnightMoves\": \"" + knightMoves + "\",");
            config.append("\n    \"BishopMoves\": \"" + bishopMoves + "\",");
            config.append("\n    \"RookMoves\": \"" + rookMoves + "\",");
            config.append("\n    \"QueenMoves\": \"" + queenMoves + "\",");
            config.append("\n    \"KingMoves\": \"" + kingMoves + "\"\n");
            config.append("}");

            writeConfig(config.toString());

            Context context = SettingsActivity.this;
            Class<MainActivity> destinationActivity = MainActivity.class;
            Intent intent = new Intent(context, destinationActivity);
            startActivity(intent);
            finish();
        });
        cancelButton.setOnClickListener((v) -> {
            Context context = SettingsActivity.this;
            Class<MainActivity> destinationActivity = MainActivity.class;
            Intent intent = new Intent(context, destinationActivity);
            finish();
            startActivity(intent);
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