package com.scarwe.freechess.activity;

import android.app.Activity;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.scarwe.freechess.game.BoardGame;
import com.scarwe.freechess.game.BoardView;
import com.scarwe.freechess.game.ChessDelegate;
import com.scarwe.freechess.game.ChessPiece;
import com.scarwe.freechess.game.ChessPlayer;
import com.scarwe.freechess.R;
import com.scarwe.freechess.game.PieceType;
import com.scarwe.freechess.game.Square;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ChessActivity extends Activity implements ChessDelegate {

    private final BoardGame board = new BoardGame();
    private final int bgColor = Color.parseColor("#252525");

    ChessPlayer white = BoardGame.whitePlayer;
    ChessPlayer black = BoardGame.blackPlayer;

    // tag for console logging
    MediaPlayer player;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_chess);

        // finds the board view by its ID
        BoardView boardView = findViewById(R.id.board_view);
        boardView.chessDelegate = this;

        setActivityBgColor();

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
        boolean drawByRepetition = false;
        boolean insufficientMaterial = false;
        boolean blackHasBishop = false;
        boolean whiteHasBishop = false;

        int count = 0;

        if (white.turn) {
            if (BoardGame.whitePlayer.movePiece(from, to, false)) {
                BoardGame.setCurrentPlayer(BoardGame.blackPlayer);
                BoardGame.whitePlayer.setTurn(false);
                BoardGame.blackPlayer.setTurn(true);
                black.findLegalMoves();
                black.isKingChecked();
                if (player == null) {
                    player = MediaPlayer.create(this, R.raw.move);
                }
                player.start();

                for (String s : BoardGame.getPgn()) count = Collections.frequency(BoardGame.getPgn(), s);
                if (count > 2) drawByRepetition = true;

                if (drawByRepetition) {
                    System.out.println("draw by repetition");
                }

                insufficientMaterial = checkInsufficient();
                if (white.pieces.size() == 1 && black.pieces.size() == 1) insufficientMaterial = true;

                if (insufficientMaterial) {
                    System.out.println("draw by insufficient material");
                }

                if (black.checked) {
                    System.out.println("black checked");
                    for (ChessPiece p : black.pieces) {
                        if (p.legalSquares.size() != 0) {
                            checkmated = false;
                        }
                    }
                    if (checkmated) {
                        BoardGame.pgnMoves.append("# 1-0");
                        System.out.println("black checkmated");
                    } else {
                        BoardGame.pgnMoves.append("+");
                    }
                } else {
                    for (ChessPiece p : black.pieces) {
                        if (p.legalSquares.size() != 0) {
                            stalemated = false;
                        }
                    }
                    if (stalemated) {
                        System.out.println("black stalemated");
                        BoardGame.pgnMoves.append("1-2/1-2");
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
                if (player == null) {
                    player = MediaPlayer.create(this, R.raw.move);
                }
                player.start();
                for (String s : BoardGame.getPgn()) count = Collections.frequency(BoardGame.getPgn(), s);
                if (count > 2) drawByRepetition = true;

                insufficientMaterial = checkInsufficient();
                if (white.pieces.size() == 1 && black.pieces.size() == 1) insufficientMaterial = true;

                if (insufficientMaterial) {
                    System.out.println("draw by insufficient material");
                }

                if (drawByRepetition) {
                    System.out.println("draw by repetition");
                }

                if (white.checked) {
                    System.out.println("white checked");
                    for (ChessPiece p : white.pieces) {
                        if (p.legalSquares.size() != 0) {
                            checkmated = false;
                        }
                    }
                    if (checkmated) {
                        System.out.println("white checkmated");
                        BoardGame.pgnMoves.append("# 0-1");
                    } else {
                        BoardGame.pgnMoves.append("+");
                    }
                } else {
                    for (ChessPiece p : white.pieces) {
                        if (p.legalSquares.size() != 0) {
                            stalemated = false;
                        }
                    }
                    if (stalemated) {
                        System.out.println("white stalemated");
                        BoardGame.pgnMoves.append("1-2/1-2");
                    }
                }
            }
        }
        System.out.println(BoardGame.pgnMoves);
        findViewById(R.id.board_view).invalidate();
    }

    private void setActivityBgColor() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(bgColor);
    }

    private boolean checkInsufficient() {
        if (white.pieces.size() < 3 && black.pieces.size() < 3) {
            for (ChessPiece p : white.pieces) {
                if (p.getType() == PieceType.KNIGHT) {
                    return true;
                }
                if (p.getType() == PieceType.BISHOP) {
                    for (ChessPiece b : black.pieces) {
                        if (b.getType() == PieceType.BISHOP && b.getBishopColour() != p.getBishopColour()) {
                            return true;
                        }
                    }
                }
            }
            for (ChessPiece p : white.pieces) {
                if (p.getType() == PieceType.KNIGHT) {
                    return true;
                }
                if (p.getType() == PieceType.BISHOP) {
                    for (ChessPiece b : black.pieces) {
                        if (b.getType() == PieceType.BISHOP && b.getBishopColour() != p.getBishopColour()) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}