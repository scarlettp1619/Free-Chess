package com.scarwe.freechess;

public class ChessPiece implements Cloneable{

    ChessPlayer player;
    PieceType type;

    int row, col, resID;
    public boolean hasMoved = false;

    public ChessPiece(int col, int row, ChessPlayer player, PieceType type, int resID) {
        this.col = col;
        this.row = row;
        this.type = type;
        this.player = player;
        this.resID = resID;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
