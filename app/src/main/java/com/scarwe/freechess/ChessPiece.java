package com.scarwe.freechess;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ChessPiece implements Cloneable{

    ChessPlayer player;
    PieceType type;

    int row, col, resID;
    public int sinceMoved = -1;
    public int currentMove = 0;
    public boolean hasMoved = false;
    LinkedHashSet<Square> legalSquares = new LinkedHashSet<>();

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

    public void generateLegalSquares(Square currentSquare) throws CloneNotSupportedException {
        ArrayList<Square> currentSquares = new ArrayList<>();
        for (int i = 0; i <= 7; i++) {
            for (int j = 0; j <= 7; j++) {
                Square testSquare = new Square(j, i);
                if (player.canMove(currentSquare, testSquare)) {
                    currentSquares.add(testSquare);
                }
                for (ChessPiece piece : player.pieces) {
                    if (testSquare.col == piece.col && testSquare.row == piece.row) {
                        currentSquares.remove(testSquare);
                    }
                }
            }
        }
        legalSquares.clear();
        legalSquares.addAll(currentSquares);
    }
}
