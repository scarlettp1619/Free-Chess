package com.scarwe.freechess;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends AppCompatActivity implements ChessDelegate {

    BoardModel board = new BoardModel();
    public static String tag = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BoardView boardView = findViewById(R.id.board_view);
        boardView.chessDelegate = this;

        Button resetButton = findViewById(R.id.reset_button);
        resetButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                board.resetBoard();
                boardView.invalidate();
            }
        });

    }

    @Override
    public ChessPiece pieceLoc(int col, int row) {
        return board.pieceLoc(col, row);
    }

    @Override
    public void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        board.movePiece(fromCol, fromRow, toCol, toRow);
        findViewById(R.id.board_view).invalidate();
    }
}