package com.scarwe.freechess;

public interface ChessDelegate {

    // required due to interface
    ChessPiece pieceLoc(Square square);

    void movePiece(Square from, Square to);
}
