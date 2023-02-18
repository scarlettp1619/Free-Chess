package com.scarwe.freechess.game;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ChessPiece implements Cloneable{

    public ChessPlayer player;
    public PieceType type;

    public int row, col, resID;
    public int sinceMoved = -1;
    public int currentMove = 0;
    public boolean hasMoved = false;
    public LinkedHashSet<Square> legalSquares = new LinkedHashSet<>();

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
            for (int i = this.row - 1 ; i <= this.row + 1; i++) {
                for (int j = 0; j <= 7 ; j++) {
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

    public ChessPlayer getPlayer () {
        return this.player;
    }

    public PieceType getType () {
        return this.type;
    }

    public int getCurrentMove() {
        return this.currentMove;
    }

    public int getSinceMoved() {
        return this.sinceMoved;
    }

    public void addSinceMoved(int i) {
        this.sinceMoved += i;
    }

    public boolean getHasMoved() {
        return this.hasMoved;
    }

    public void setPieceType(PieceType p) {
        this.type = p;
    }

    public void setResId(int id) {
        this.resID = id;
    }

    public ChessPiece getPiecesOfType(PieceType pType, ChessPiece p) {
        if (this.type == pType && p != this) {
            return this;
        }
        return null;
    }
}