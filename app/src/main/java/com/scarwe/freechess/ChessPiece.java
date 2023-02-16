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
        if (type == PieceType.KING) {
            int colModifierRight, colModifierLeft;
            colModifierRight = player.canCastleKingSideTest(currentSquare);
            colModifierLeft = player.canCastleQueenSideTest(currentSquare);
            for (int i = this.row + colModifierLeft ; i <= this.row + colModifierRight; i++) {
                for (int j = this.col - 3; j <= this.col + 3; j++) {
                    if (j > 7 || j < 0 || i > 7 || i < 0) break;
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
        } else if (type == PieceType.PAWN) {
            int rowModifier;
            if (currentMove == 0) rowModifier = 2;
            else rowModifier = 1;
            for (int i = this.row - rowModifier; i <= this.row + rowModifier; i++) {
                for (int j = this.col - 1; j <= this.col + 1; j++) {
                    if (j > 7 || j < -1 || i > 7 || i < -1) break;
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
        }
        else if (type == PieceType.ROOK) {
            for (int i = 0; i <= 7; i++) {
                Square testSquare = new Square(this.col, i);
                if (player.canMove(currentSquare, testSquare)) {
                    currentSquares.add(testSquare);
                }
                for (ChessPiece piece : player.pieces) {
                    if (testSquare.col == piece.col && testSquare.row == piece.row) {
                        currentSquares.remove(testSquare);
                    }
                }
            }
            for (int j = 0; j <= 7; j++) {
                Square testSquare = new Square(j, this.row);
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
        //Math.abs(from.getCol() - to.getCol()) == Math.abs(from.getRow() - to.getRow())
        else if (type != null){
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
        }
        legalSquares.clear();
        legalSquares.addAll(currentSquares);
    }
}
