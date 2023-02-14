package com.scarwe.freechess;

public class ChessPiece {
    int row, col, resID;
    ChessPlayer player;
    PieceType type;
    public ChessPiece(int col, int row, ChessPlayer player, PieceType type, int resID) {
        this.col = col;
        this.row = row;
        this.type = type;
        this.player = player;
        this.resID = resID;
    }
}
