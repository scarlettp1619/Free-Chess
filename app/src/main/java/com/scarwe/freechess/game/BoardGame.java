package com.scarwe.freechess.game;

import com.scarwe.freechess.R;

import java.util.ArrayList;

public class BoardGame {
    public boolean whiteCastled = false;
    public boolean blackCastled = false;

    public static ChessPlayer whitePlayer = new ChessPlayer(0);
    public static ChessPlayer blackPlayer = new ChessPlayer(1);
    public static ChessPlayer currentPlayer = whitePlayer;

    public static int gameMove = 1;

    public static ArrayList<ChessPiece> pieces = new ArrayList<>();
    public static StringBuilder pgnMoves = new StringBuilder("");
    public static ArrayList<String> pgnCheck = new ArrayList<>();

    {
        whitePlayer.setTurn(true);
        resetBoard();
    }
    public void resetBoard() {
        // clears arraylist
        whitePlayer.pieces.clear();
        blackPlayer.pieces.clear();

        whitePlayer.setTurn(true);
        blackPlayer.setTurn(false);

        gameMove = 1;
        currentPlayer = whitePlayer;

        whitePlayer.checked = false;
        blackPlayer.checked = false;

        pgnMoves.setLength(0);
        pgnCheck.clear();

        pieces.addAll(whitePlayer.pieces);
        pieces.addAll(blackPlayer.pieces);
        // draws all pieces to arraylist
        for (int i = 0; i <= 1; i++) {
            // rooks
            whitePlayer.pieces.add(new ChessPiece(i * 7, 0, whitePlayer, PieceType.ROOK, R.drawable. wr));
            blackPlayer.pieces.add(new ChessPiece(i * 7, 7, blackPlayer, PieceType.ROOK, R.drawable.br));
            // knights
            whitePlayer.pieces.add(new ChessPiece(1 + i * 5, 0, whitePlayer, PieceType.KNIGHT, R.drawable.wn));
            blackPlayer.pieces.add(new ChessPiece(1 + i * 5, 7, blackPlayer, PieceType.KNIGHT, R.drawable.bn));
            // bishops
            whitePlayer.pieces.add(new ChessPiece(2 + i * 3, 0, whitePlayer, PieceType.BISHOP, R.drawable.wb));
            blackPlayer.pieces.add(new ChessPiece(2 + i * 3, 7, blackPlayer, PieceType.BISHOP, R.drawable.bb));
        }
        for (int i = 0; i <= 7; i++) {
            // pawns
            whitePlayer.pieces.add(new ChessPiece(i, 1, whitePlayer, PieceType.PAWN, R.drawable.wp));
            blackPlayer.pieces.add(new ChessPiece(i, 6, blackPlayer, PieceType.PAWN, R.drawable.bp));
        }
        // queens
        whitePlayer.pieces.add(new ChessPiece(3, 0, whitePlayer, PieceType.QUEEN, R.drawable.wq));
        blackPlayer.pieces.add(new ChessPiece(3, 7, blackPlayer, PieceType.QUEEN, R.drawable.bq));
        // kings
        whitePlayer.pieces.add(new ChessPiece(4, 0, whitePlayer, PieceType.KING, R.drawable.wk));
        blackPlayer.pieces.add(new ChessPiece(4, 7, blackPlayer, PieceType.KING, R.drawable.bk));

        pgnCheck.add(pgnBoard());
    }

    public static ChessPiece pieceLoc(Square square) {
        return pieceLoc(square.getCol(), square.getRow());
    }

    // locate a piece using its position on the board
    static ChessPiece pieceLoc(int col, int row) {
        for (ChessPiece piece : whitePlayer.pieces) {
            if (col == piece.col && row == piece.row) {
                return piece;
            }
        }
        for (ChessPiece piece : blackPlayer.pieces) {
            if (col == piece.col && row == piece.row) {
                return piece;
            }
        }
        return null;
    }

    public static String pgnBoard() {
        StringBuilder desc = new StringBuilder(" \n");
        desc.append("  a b c d e f g h\n");
        // i determines rows of model
        for (int i = 7; i >= 0; i--) {
            desc.append(i + 1);
            desc.append(boardRow(i));
            desc.append(" ").append(i + 1);
            desc.append("\n");
        }
        desc.append("  a b c d e f g h");
        return desc.toString();
    }

    public static String stringBoard() {
        StringBuilder desc = new StringBuilder(" \n");
        // i determines rows of model
        for (int i = 7; i >= 0; i--) {
            desc.append(i);
            desc.append(boardRow(i));
            desc.append("\n");
        }
        desc.append("  0 1 2 3 4 5 6 7");
        return desc.toString();
    }

    public static String boardRow(int i) {
        StringBuilder desc = new StringBuilder();
            // j determines columns of model
        for (int j = 0; j < 8; j++) {
            ChessPiece piece = pieceLoc(j, i);
            if (piece == null) {
                desc.append(" .");
            } else {
                desc.append(" ");
                if (piece.type == PieceType.KING) {
                    if (piece.player == whitePlayer) desc.append("k");
                    else desc.append("K");
                }
                if (piece.type == PieceType.QUEEN) {
                    if (piece.player == whitePlayer) desc.append("q");
                    else desc.append("Q");
                }
                if (piece.type == PieceType.ROOK) {
                    if (piece.player == whitePlayer) desc.append("r");
                    else desc.append("R");
                }
                if (piece.type == PieceType.BISHOP) {
                    if (piece.player == whitePlayer) desc.append("b");
                    else desc.append("B");
                }
                if (piece.type == PieceType.KNIGHT) {
                    if (piece.player == whitePlayer) desc.append("n");
                    else desc.append("N");
                }
                if (piece.type == PieceType.PAWN) {
                    if (piece.player == whitePlayer) desc.append("p");
                    else desc.append("P");
                }
            }
        }
        return desc.toString();
    }
    public static void setCurrentPlayer(ChessPlayer cPlayer) {
        currentPlayer = cPlayer;
    }

    public static ArrayList<String> getPgn() {
        return pgnCheck;
    }
}
