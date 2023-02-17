package com.scarwe.freechess;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.Button;

public class MainActivity extends Activity implements ChessDelegate {

    private final BoardGame board = new BoardGame();

    // tag for console logging
    MediaPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler((paramThread, paramThrowable) -> System.exit(2));

        setContentView(R.layout.activity_main);

        // finds the board view by its ID
        BoardView boardView = findViewById(R.id.board_view);
        boardView.chessDelegate = this;

        Button resetButton = findViewById(R.id.reset_button);

        resetButton.setOnClickListener(v -> {
            board.resetBoard();
            board.whiteCastled = false;
            board.blackCastled = false;
            boardView.invalidate();
        });

    }

    // required due to interface
    @Override
    public ChessPiece pieceLoc(Square square) {
        return BoardGame.pieceLoc(square);
    }

    @Override
    public void movePiece(Square from, Square to) throws CloneNotSupportedException {
        boolean checkmated = true;
        boolean stalemated = true;
        ChessPlayer white = BoardGame.whitePlayer;
        ChessPlayer black = BoardGame.blackPlayer;
        BoardView boardView = findViewById(R.id.board_view);
        if (white.turn) {
            if (BoardGame.whitePlayer.movePiece(from, to, false)) {
                if (BoardGame.gameMove == 0) {
                    BoardGame.gameMove++;
                }
                BoardGame.setCurrentPlayer(BoardGame.blackPlayer);
                BoardGame.whitePlayer.setTurn(false);
                BoardGame.blackPlayer.setTurn(true);
                black.findLegalMoves();
                black.isKingChecked();
                boardView.invalidate();
                if (player == null ) {
                    player = MediaPlayer.create(this, R.raw.move);
                }
                player.start();
                if (black.checked) {
                    System.out.println("black checked");
                    for (ChessPiece p : black.pieces) {
                        if (p.legalSquares.size() != 0) {
                            checkmated = false;
                        }
                    }
                    if (checkmated) {
                        System.out.println("black checkmated");
                    }
                } else {
                    for (ChessPiece p : black.pieces) {
                        if (p.legalSquares.size() != 0) {
                            stalemated = false;
                        }
                    }
                    if (stalemated) {
                        System.out.println("black stalemated");
                    }
                }
            }
        } else if (BoardGame.blackPlayer.turn) {
            if (BoardGame.blackPlayer.movePiece(from, to, false)) {
                BoardGame.setCurrentPlayer(BoardGame.whitePlayer);
                BoardGame.whitePlayer.setTurn(true);
                BoardGame.blackPlayer.setTurn(false);
                white.findLegalMoves();
                white.isKingChecked();
                boardView.invalidate();
                if (player == null) {
                    player = MediaPlayer.create(this, R.raw.move);
                }
                player.start();
                if (white.checked) {
                    System.out.println("white checked");
                    for (ChessPiece p : white.pieces) {
                        if (p.legalSquares.size() != 0) {
                            checkmated = false;
                        }
                    }
                    if (checkmated) {
                        System.out.println("white checkmated");
                    }
                } else {
                    for (ChessPiece p : white.pieces) {
                        if (p.legalSquares.size() != 0) {
                            stalemated = false;
                        }
                    }
                    if (stalemated) {
                        System.out.println("white stalemated");
                    }
                }
            }
        }
        findViewById(R.id.board_view).invalidate();
    }
}