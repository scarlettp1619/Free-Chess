package com.scarwe.freechess.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ActivityInfo;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;

public class ChessActivity extends Activity implements ChessDelegate {

    private final BoardGame board = new BoardGame();
    private final int bgColor = Color.parseColor("#252525");
    private int gameState = 0;
    // for pop up when winning
    private String winner = "";

    ChessPlayer white = BoardGame.whitePlayer;
    ChessPlayer black = BoardGame.blackPlayer;

    MediaPlayer player;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // finds the board view by its ID
        BoardView boardView = findViewById(R.id.board_view);
        boardView.chessDelegate = this;

        setActivityBgColor();

        Button resetButton = findViewById(R.id.reset_button);

        resetButton.setOnClickListener(v -> {
            try {
                // load config file
                board.reader = new BufferedReader(new InputStreamReader(getAssets().open("config.json")));
                board.resetBoard();
                gameState = 0;
            } catch (IOException | CloneNotSupportedException e) {
                //
            }
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
        // automatically true and checked if false, easier to check this way
        boolean checkmated = true;
        boolean stalemated = true;
        boolean drawByRepetition = false;
        boolean insufficientMaterial;
        boolean fiftyMoveRule = false;

        int count = 0;

        // if whites turn and gam eplayable
        if (white.turn && gameState == 0) {
            // for capture moves
            int piecesSize = BoardGame.blackPlayer.pieces.size();
            for (ChessPiece p : BoardGame.whitePlayer.pieces) {
                if (p.getType() == PieceType.KING) {
                    p.resID = R.drawable.wk;
                    break;
                }
            }
            // if can move piece
            if (BoardGame.whitePlayer.movePiece(from, to, false)) {
                int newPiecesSize = BoardGame.blackPlayer.pieces.size();
                BoardGame.setCurrentPlayer(BoardGame.blackPlayer);

                // ensures correct turn turn
                BoardGame.whitePlayer.setTurn(false);
                BoardGame.blackPlayer.setTurn(true);

                // for first move
                if (player == null) {
                    player = MediaPlayer.create(this, R.raw.move);
                }
                player.start();

                if (piecesSize == newPiecesSize) {
                    white.sinceCaptured++;
                } else {
                    white.sinceCaptured = 0;
                    black.sinceCaptured = 0;

                }
                // find legal moves for players
                for (ChessPiece p : white.pieces) {
                    p.generateLegalSquares(new Square(p.col, p.row));
                }

                for (ChessPiece p : black.pieces) {
                    p.generateLegalSquares(new Square(p.col, p.row));
                }

                black.findLegalMoves();
                if (BoardGame.gameMove > 3) black.isKingChecked(null, null);

                // see if current state has been repeated
                for (String s : BoardGame.getPgn()) count = Collections.frequency(BoardGame.getPgn(), s);
                if (count > 2) drawByRepetition = true;

                if (drawByRepetition) {
                    // will probably make a button to claim a draw at some point, instead of automatic
                    gameState = 3;
                    System.out.println("draw by repetition");
                }

                insufficientMaterial = checkInsufficient();
                if (white.pieces.size() == 1 && black.pieces.size() == 1) insufficientMaterial = true;

                if (insufficientMaterial) {
                    gameState = 4;
                    System.out.println("draw by insufficient material");
                }

                if (white.getSincePawnMoved() > 49 && white.getSinceCaptured() > 49
                        && black.getSincePawnMoved() > 49 && black.getSinceCaptured() > 49) {
                    fiftyMoveRule = true;
                }

                if (fiftyMoveRule) {
                    gameState = 5;
                    System.out.println("fifty move rule reached");
                }

                if (black.checked) {
                    System.out.println("black checked");
                    for (ChessPiece p : black.pieces) {
                        if (p.getType() == PieceType.KING) {
                            p.resID = R.drawable.cbk;
                            break;
                        }
                        // if there are any legal moves, checkmated is false
                        if (p.legalSquares.size() != 0) {
                            checkmated = false;
                            break;
                        }
                    }
                    if (checkmated) {
                        gameState = 1;
                        winner = "White";
                        BoardGame.pgnMoves.append("# 1-0");
                        System.out.println("black checkmated");
                    } else {
                        BoardGame.pgnMoves.append("+");
                    }
                } else {
                    for (ChessPiece p : black.pieces) {
                        // if not in check and there are legal moves
                        if (p.legalSquares.size() != 0) {
                            stalemated = false;
                            break;
                        }
                    }
                    if (stalemated) {
                        gameState = 2;
                        System.out.println("black stalemated");
                        BoardGame.pgnMoves.append(" 1-2/1-2");
                    }
                }
            }
        // everything the same for black, should probably simplify this at some point
        } else if (BoardGame.blackPlayer.turn && gameState == 0) {
            for (ChessPiece p : BoardGame.blackPlayer.pieces) {
                if (p.getType() == PieceType.KING) {
                    p.resID = R.drawable.bk;
                    break;
                }
            }
            int piecesSize = BoardGame.whitePlayer.pieces.size();
            if (BoardGame.blackPlayer.movePiece(from, to, false)) {
                int newPiecesSize = BoardGame.whitePlayer.pieces.size();
                BoardGame.setCurrentPlayer(BoardGame.whitePlayer);
                BoardGame.whitePlayer.setTurn(true);
                BoardGame.blackPlayer.setTurn(false);

                if (player == null) {
                    player = MediaPlayer.create(this, R.raw.move);
                }
                player.start();
                if (piecesSize == newPiecesSize) {
                    black.sinceCaptured++;
                } else {
                    white.sinceCaptured = 0;
                    black.sinceCaptured = 0;
                }

                for (ChessPiece p : white.pieces) {
                    p.generateLegalSquares(new Square(p.col, p.row));
                }

                for (ChessPiece p : black.pieces) {
                    p.generateLegalSquares(new Square(p.col, p.row));
                }

                white.findLegalMoves();
                if (BoardGame.gameMove > 3) white.isKingChecked(null, null);

                for (String s : BoardGame.getPgn()) count = Collections.frequency(BoardGame.getPgn(), s);
                if (count > 2) drawByRepetition = true;

                insufficientMaterial = checkInsufficient();
                if (white.pieces.size() == 1 && black.pieces.size() == 1) insufficientMaterial = true;

                if (insufficientMaterial) {
                    gameState = 4;
                    System.out.println("draw by insufficient material");
                }

                if (drawByRepetition) {
                    gameState = 3;
                    System.out.println("draw by repetition");
                }

                if (white.getSincePawnMoved() > 49 && white.getSinceCaptured() > 49
                        && black.getSincePawnMoved() > 49 && black.getSinceCaptured() > 49) {
                    fiftyMoveRule = true;
                }

                if (fiftyMoveRule) {
                    gameState = 5;
                    System.out.println("fifty move rule reached");
                }

                if (white.checked) {
                    System.out.println("white checked");
                    for (ChessPiece p : white.pieces) {
                        if (p.getType() == PieceType.KING) {
                            p.resID = R.drawable.cwk;
                            break;
                        }
                        if (p.legalSquares.size() != 0) {
                            checkmated = false;
                        }
                    }
                    if (checkmated) {
                        gameState = 1;
                        winner = "Black";
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
                        gameState = 2;
                        System.out.println("white stalemated");
                        BoardGame.pgnMoves.append("1-2/1-2");
                    }
                }
            }
        }
        System.out.println(BoardGame.pgnMoves);
        findViewById(R.id.board_view).invalidate();
        if (gameState != 0) {
            System.out.println("game end");
        }
    }

    // set background colour
    private void setActivityBgColor() {
        View view = this.getWindow().getDecorView();
        view.setBackgroundColor(bgColor);
    }

    // find insufficient material
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