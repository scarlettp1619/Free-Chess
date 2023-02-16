package com.scarwe.freechess;

import java.io.IOException;

public interface ChessDelegate {

    // required due to interface
    ChessPiece pieceLoc(Square square);

    void movePiece(Square from, Square to) throws CloneNotSupportedException, IOException;
}
