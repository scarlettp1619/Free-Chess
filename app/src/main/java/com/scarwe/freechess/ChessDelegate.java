package com.scarwe.freechess;

public interface ChessDelegate {
    ChessPiece pieceLoc(int col, int row);

    void movePiece(int fromCol, int fromRow, int toCol, int toRow);
}
