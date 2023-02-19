package com.scarwe.freechess.game;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ChessPiece implements Cloneable{

    public ChessPlayer player;
    public PieceType type;

    public int row, col, resID;
    public int sinceMoved = -1;
    public int currentMove = 0;
    public int bishopColour = -1;
    public boolean hasMoved = false;

    public LinkedHashSet<Square> legalSquares = new LinkedHashSet<>();
    public ArrayList<PieceType> moveSet;

    public ChessPiece(int col, int row, ChessPlayer player, PieceType type, int resID, ArrayList<PieceType> moveSet) {
        this.col = col;
        this.row = row;
        this.type = type;
        this.player = player;
        this.resID = resID;
        this.moveSet = moveSet;

        if (col == 2 && row == 0) bishopColour = 1; // dark square
        if (col == 5 && row == 0) bishopColour = 0; // light square
        if (col == 2 && row == 7) bishopColour = 1;
        if (col == 5 && row == 7) bishopColour = 0;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void generateLegalSquares(Square currentSquare) throws CloneNotSupportedException {
        ArrayList<Square> currentSquares = new ArrayList<>();
        if (moveSet.contains(PieceType.KING)) {
            for (int i = this.row - 1 ; i <= this.row + 1; i++) {
                for (int j = this.col - 4; j <= this.col + 4; j++) {
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
        } if (moveSet.contains(PieceType.PAWN)) {
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
        if (moveSet.contains(PieceType.ROOK)) {
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
        if (moveSet.contains(PieceType.BISHOP)) {
            for (int i = 1; i <= 7; i++) {
                Square testSquare1 = new Square(this.col + i, this.row + i);
                Square testSquare2 = new Square(this.col - i, this.row - i);
                Square testSquare3 = new Square(this.col - i, this.row + i);
                Square testSquare4 = new Square(this.col + i, this.row - i);
                if (player.canMove(currentSquare, testSquare1)) {
                    currentSquares.add(testSquare1);
                }
                if (player.canMove(currentSquare, testSquare2)) {
                    currentSquares.add(testSquare2);
                }
                if (player.canMove(currentSquare, testSquare3)) {
                    currentSquares.add(testSquare3);
                }
                if (player.canMove(currentSquare, testSquare4)) {
                    currentSquares.add(testSquare4);
                }
                for (ChessPiece piece : player.pieces) {
                    if (testSquare1.col == piece.col && testSquare1.row == piece.row) {
                        currentSquares.remove(testSquare1);
                    }
                    if (testSquare2.col == piece.col && testSquare2.row == piece.row) {
                        currentSquares.remove(testSquare2);
                    }
                    if (testSquare3.col == piece.col && testSquare3.row == piece.row) {
                        currentSquares.remove(testSquare3);
                    }
                    if (testSquare4.col == piece.col && testSquare4.row == piece.row) {
                        currentSquares.remove(testSquare4);
                    }
                }
            }
        }
        if (moveSet.contains(PieceType.QUEEN)) {
            // Bishop-like moves
            for (int i = -7; i <= 7; i++) {
                if (i == 0) {
                    continue;
                }
                Square testSquare = new Square(this.col + i, this.row + i);
                if (player.canMove(currentSquare, testSquare)) {
                    currentSquares.add(testSquare);
                }
                for (ChessPiece piece : player.pieces) {
                    if (testSquare.col == piece.col && testSquare.row == piece.row) {
                        currentSquares.remove(testSquare);
                    }
                }
                testSquare = new Square(this.col - i, this.row - i);
                if (player.canMove(currentSquare, testSquare)) {
                    currentSquares.add(testSquare);
                }
                for (ChessPiece piece : player.pieces) {
                    if (testSquare.col == piece.col && testSquare.row == piece.row) {
                        currentSquares.remove(testSquare);
                    }
                }
                testSquare = new Square(this.col + i, this.row - i);
                if (player.canMove(currentSquare, testSquare)) {
                    currentSquares.add(testSquare);
                }
                for (ChessPiece piece : player.pieces) {
                    if (testSquare.col == piece.col && testSquare.row == piece.row) {
                        currentSquares.remove(testSquare);
                    }
                }
                testSquare = new Square(this.col - i, this.row + i);
                if (player.canMove(currentSquare, testSquare)) {
                    currentSquares.add(testSquare);
                }
                for (ChessPiece piece : player.pieces) {
                    if (testSquare.col == piece.col && testSquare.row == piece.row) {
                        currentSquares.remove(testSquare);
                    }
                }
            }
            // Rook-like moves
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
        if (moveSet.contains(PieceType.KNIGHT)) {
            int[][] possibleOffsets = {{-2, 1}, {-1, 2}, {1, 2}, {2, 1}, {2, -1}, {1, -2}, {-1, -2}, {-2, -1}};
            for (int[] offset : possibleOffsets) {
                int testCol = this.col + offset[0];
                int testRow = this.row + offset[1];
                if (testCol >= 0 && testCol <= 7 && testRow >= 0 && testRow <= 7) {
                    Square testSquare = new Square(testCol, testRow);
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

    public int getBishopColour() {
        return this.bishopColour;
    }

    public ChessPiece getPiecesOfType(PieceType pType, ChessPiece p) {
        if (this.type == pType && p != this) {
            return this;
        }
        return null;
    }

    public ArrayList<PieceType> getMoveSet() {
        return this.moveSet;
    }
}
