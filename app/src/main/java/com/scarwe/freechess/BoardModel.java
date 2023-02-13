package com.scarwe.freechess;

import java.util.ArrayList;

public class BoardModel {
    ArrayList<ChessPiece> pieces = new ArrayList<>();

    {
        resetBoard();
    }

    public void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        if (fromCol == toCol && fromRow == toRow) return;

        ChessPiece movingPiece = pieceLoc(fromCol, fromRow);
        ChessPiece removePiece = pieceLoc(toCol, toRow);

        System.out.println(movingPiece);
        System.out.println(removePiece);
        try {
            if (movingPiece.player == removePiece.player) {
                return;
            }
            pieces.remove(removePiece);
        } catch (Exception ex) {
            // do nothing
        }

        pieces.remove(removePiece);
        pieces.remove(movingPiece);
        pieces.add(new ChessPiece(toCol, toRow, movingPiece.player, movingPiece.type, movingPiece.resID));

    }

    public void resetBoard() {
        pieces.clear();
        for (int i = 0; i <= 1; i++) {
            // rooks
            pieces.add(new ChessPiece(i * 7, 0, ChessPlayer.WHITE, PieceType.ROOK, R.drawable. wr));
            pieces.add(new ChessPiece(i * 7, 7, ChessPlayer.BLACK, PieceType.ROOK, R.drawable.br));
            // knights
            pieces.add(new ChessPiece(1 + i * 5, 0, ChessPlayer.WHITE, PieceType.KNIGHT, R.drawable.wn));
            pieces.add(new ChessPiece(1 + i * 5, 7, ChessPlayer.BLACK, PieceType.KNIGHT, R.drawable.bn));
            // bishops
            pieces.add(new ChessPiece(2 + i * 3, 0, ChessPlayer.WHITE, PieceType.BISHOP, R.drawable.wb));
            pieces.add(new ChessPiece(2 + i * 3, 7, ChessPlayer.BLACK, PieceType.BISHOP, R.drawable.bb));
        }
        for (int i = 0; i <= 7; i++) {
            // pawns
            pieces.add(new ChessPiece(i, 1, ChessPlayer.WHITE, PieceType.PAWN, R.drawable.wp));
            pieces.add(new ChessPiece(i, 6, ChessPlayer.BLACK, PieceType.PAWN, R.drawable.bp));
        }
        // queens
        pieces.add(new ChessPiece(3, 0, ChessPlayer.WHITE, PieceType.QUEEN, R.drawable.wq));
        pieces.add(new ChessPiece(3, 7, ChessPlayer.BLACK, PieceType.QUEEN, R.drawable.bq));
        // kings
        pieces.add(new ChessPiece(4, 0, ChessPlayer.WHITE, PieceType.KING, R.drawable.wk));
        pieces.add(new ChessPiece(4, 7, ChessPlayer.BLACK, PieceType.KING, R.drawable.bk));
    }
    public ChessPiece pieceLoc(int col, int row) {
        for (ChessPiece piece : pieces) {
            if(col == piece.col && row == piece.row) {
                return piece;
            }
        }
        return null;
    }
    public String stringBoard() {
        StringBuilder desc = new StringBuilder(" \n");
        // i determines rows of model
        for (int i = 7; i >= 0; i--) {
            desc.append(i);
            // j determines columns of model
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = pieceLoc(j, i);
                if (piece == null) {
                    desc.append(" .");
                } else {
                    desc.append(" ");
                    if(piece.getType() == PieceType.KING) {
                        if (piece.player == ChessPlayer.WHITE) desc.append("k");
                        else desc.append("K");
                    }
                    if(piece.getType() == PieceType.QUEEN) {
                        if (piece.player == ChessPlayer.WHITE) desc.append("q");
                        else desc.append("Q");
                    }
                    if(piece.getType() == PieceType.ROOK) {
                        if (piece.player == ChessPlayer.WHITE) desc.append("r");
                        else desc.append("R");
                    }
                    if(piece.getType() == PieceType.BISHOP) {
                        if (piece.player == ChessPlayer.WHITE) desc.append("b");
                        else desc.append("B");
                    }
                    if(piece.getType() == PieceType.KNIGHT) {
                        if (piece.player == ChessPlayer.WHITE) desc.append("n");
                        else desc.append("N");
                    }
                    if(piece.getType() == PieceType.PAWN) {
                        if (piece.player == ChessPlayer.WHITE) desc.append("p");
                        else desc.append("P");
                    }
                }
            }
            desc.append("\n");
        }
        desc.append("  a b c d e f g h");
        return desc.toString();
    }
}
