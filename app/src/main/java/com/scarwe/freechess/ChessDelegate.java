package com.scarwe.freechess;

public interface ChessDelegate {
    ChessPiece pieceLoc(Square square);
    void movePiece(Square from, Square to);
}
