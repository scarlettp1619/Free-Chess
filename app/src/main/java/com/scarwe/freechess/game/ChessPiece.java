package com.scarwe.freechess.game;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class ChessPiece implements Cloneable{

    public ChessPlayer player;
    public PieceType type;

    public int row, col, resID;
    public int sinceMoved = -1;
    public int currentMove = 0;
    // to check for stalemates by insufficient material
    public int bishopColour = -1;
    public int kingID = 0;
    public boolean hasMoved = false;

    public LinkedHashSet<Square> legalSquares = new LinkedHashSet<>();
    // literally only exists cause pawns exist
    public LinkedHashSet<Square> protectedSquares = new LinkedHashSet<>();
    public LinkedHashSet<Square> discoveredSquares = new LinkedHashSet<>();

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

        if (this.type == PieceType.KING) {
            kingID = 1;
        }
    }

    @NonNull
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    // generates possible moves for the piece,
    // not gonna explain the math it's very hard coded you can figure out
    public void generateLegalSquares(Square currentSquare) {
        ArrayList<Square> currentSquares = new ArrayList<>();
        ArrayList<Square> currentProtectedSquares = new ArrayList<>();
        ArrayList<Square> currentDiscoveredSquares = new ArrayList<>();
        if (moveSet.contains(PieceType.KING)) {
            for (int i = this.row - 1 ; i <= this.row + 1; i++) {
                for (int j = this.col - 3; j <= this.col + 3; j++) {
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
            for (int i = this.row - 1; i <= this.row + 1; i++) {
                for (int j = this.col - 1; j <= this.col + 1; j++) {
                    Square testSquare = new Square(j, i);
                    currentProtectedSquares.add(testSquare);
                    currentDiscoveredSquares.add(testSquare);
                }
            }
        } if (moveSet.contains(PieceType.PAWN)) {
            int rowModifier;
            int captureModifier;

            if (this.getPlayer().colour == 0) captureModifier = 1;
            else captureModifier = -1;

            if (currentMove == 0) rowModifier = 2;
            else rowModifier = 1;

            Square leftCapture = new Square(col - 1, row + captureModifier);
            Square rightCapture = new Square(col + 1, row + captureModifier);

            currentProtectedSquares.add(leftCapture);
            currentProtectedSquares.add(rightCapture);
            discoveredSquares.add(leftCapture);
            discoveredSquares.add(rightCapture);
            for (int i = this.row - rowModifier; i <= this.row + rowModifier; i++) {
                for (int j = this.col - 1; j <= this.col + 1; j++) {
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
            boolean kingInArea = false;
            ChessPlayer opponent;
            if (this.getPlayer().colour == 0) opponent = BoardGame.blackPlayer;
            else opponent = BoardGame.whitePlayer;
            int kingCol = -1, kingRow = -1;
            for (int i = 0; i <= 7; i++) {
                Square testSquare = new Square(this.col, i);
                if (BoardGame.pieceLoc(this.col, i) != null) {
                    if (BoardGame.pieceLoc(this.col, i).getType() == PieceType.KING
                            && BoardGame.pieceLoc(this.col, i).getPlayer() != this.player){
                        kingInArea = true;
                        kingRow = i;
                    }
                }
                if (player.canMove(currentSquare, testSquare)) {
                    currentSquares.add(testSquare);
                    currentProtectedSquares.add(testSquare);
                }
                for (ChessPiece piece : player.pieces) {
                    if (testSquare.col == piece.col && testSquare.row == piece.row) {
                        currentSquares.remove(testSquare);
                    }
                }
            }
            if (kingInArea) {
                if (kingRow > this.row) {
                    int piecesDiscovered = 0;
                    for (int i = this.row; i <= kingRow; i++) {
                        Square testSquare = new Square(this.col, i);
                        if (piecesDiscovered < 2) {
                            currentDiscoveredSquares.add(testSquare);
                        }
                        for (ChessPiece piece : opponent.pieces) {
                            if (testSquare.col == piece.col && testSquare.row == piece.row) {
                                piecesDiscovered++;
                                break;
                            }
                        }
                    }
                } else {
                    int piecesDiscovered = 0;
                    for (int i = this.row; i >= kingRow; i--) {
                        Square testSquare = new Square(this.col, i);
                        if (piecesDiscovered < 2) {
                            currentDiscoveredSquares.add(testSquare);
                        }
                        for (ChessPiece piece : opponent.pieces) {
                            if (testSquare.col == piece.col && testSquare.row == piece.row) {
                                piecesDiscovered++;
                                break;
                            }
                        }
                    }
                }
            }
            kingInArea = false;
            for (int j = 0; j <= 7; j++) {
                if (BoardGame.pieceLoc(j, this.row) != null) {
                    if (BoardGame.pieceLoc(j, this.row).getType() == PieceType.KING
                            && BoardGame.pieceLoc(j, this.row).getPlayer() != this.player) {
                        kingInArea = true;
                        kingCol = j;
                    }
                }
                Square testSquare = new Square(j, this.row);
                if (player.canMove(currentSquare, testSquare)) {
                    currentSquares.add(testSquare);
                    currentProtectedSquares.add(testSquare);
                }
                for (ChessPiece piece : player.pieces) {
                    if (testSquare.col == piece.col && testSquare.row == piece.row) {
                        currentSquares.remove(testSquare);
                    }
                }
            }
            if (kingInArea) {
                if (kingCol > this.col) {
                    int piecesDiscovered = 0;
                    for (int i = this.col; i <= kingCol; i++) {
                        Square testSquare = new Square(i, this.row);
                        if (piecesDiscovered < 2) {
                            currentDiscoveredSquares.add(testSquare);
                        }
                        for (ChessPiece piece : opponent.pieces) {
                            if (testSquare.col == piece.col && testSquare.row == piece.row) {
                                piecesDiscovered++;
                                break;
                            }
                        }
                    }
                } else {
                    int piecesDiscovered = 0;
                    for (int i = this.col; i >= kingCol; i--) {
                        Square testSquare = new Square(i, this.row);
                        if (piecesDiscovered < 2) {
                            currentDiscoveredSquares.add(testSquare);
                        }
                        for (ChessPiece piece : opponent.pieces) {
                            if (testSquare.col == piece.col && testSquare.row == piece.row) {
                                piecesDiscovered++;
                                break;
                            }
                        }
                    }
                }
            }
        }
        //Math.abs(from.getCol() - to.getCol()) == Math.abs(from.getRow() - to.getRow())
        if (moveSet.contains(PieceType.BISHOP)) {
            ChessPlayer opponent;
            if (this.getPlayer().colour == 0) opponent = BoardGame.blackPlayer;
            else opponent = BoardGame.whitePlayer;
            boolean kingInArea1 = false;
            boolean kingInArea2 = false;
            boolean kingInArea3 = false;
            boolean kingInArea4 = false;
            for (int i = 0; i <= 7; i++) {
                Square testSquare1 = new Square(this.col + i, this.row + i);
                if (BoardGame.pieceLoc(testSquare1) != null) {
                    if(BoardGame.pieceLoc(testSquare1).getType() == PieceType.KING
                            && BoardGame.pieceLoc(testSquare1).getPlayer() != this.player) {
                        kingInArea1 = true;
                    }
                }
                Square testSquare2 = new Square(this.col - i, this.row - i);
                if (BoardGame.pieceLoc(testSquare2) != null) {
                    if(BoardGame.pieceLoc(testSquare2).getType() == PieceType.KING
                            && BoardGame.pieceLoc(testSquare2).getPlayer() != this.player) {
                        kingInArea2 = true;
                    }
                }
                Square testSquare3 = new Square(this.col - i, this.row + i);
                if (BoardGame.pieceLoc(testSquare3) != null) {
                    if(BoardGame.pieceLoc(testSquare3).getType() == PieceType.KING
                            && BoardGame.pieceLoc(testSquare3).getPlayer() != this.player) {
                        kingInArea3 = true;
                    }
                }
                Square testSquare4 = new Square(this.col + i, this.row - i);
                if (BoardGame.pieceLoc(testSquare4) != null) {
                    if(BoardGame.pieceLoc(testSquare4).getType() == PieceType.KING
                            && BoardGame.pieceLoc(testSquare4).getPlayer() != this.player) {
                        kingInArea4 = true;
                    }
                }
                if (player.canMove(currentSquare, testSquare1)) {
                    currentSquares.add(testSquare1);
                    currentProtectedSquares.add(testSquare1);
                }
                if (player.canMove(currentSquare, testSquare2)) {
                    currentSquares.add(testSquare2);
                    currentProtectedSquares.add(testSquare2);
                }
                if (player.canMove(currentSquare, testSquare3)) {
                    currentSquares.add(testSquare3);
                    currentProtectedSquares.add(testSquare3);
                }
                if (player.canMove(currentSquare, testSquare4)) {
                    currentSquares.add(testSquare4);
                    currentProtectedSquares.add(testSquare4);
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
            if (kingInArea1) {
                int piecesDiscovered = 0;
                for (int i = 0; i <= 7; i++) {
                    Square testSquare = new Square(this.col + i, this.row + i);
                    if (piecesDiscovered < 2) {
                        currentDiscoveredSquares.add(testSquare);
                    }
                    for (ChessPiece piece : opponent.pieces) {
                        if (testSquare.col == piece.col && testSquare.row == piece.row) {
                            piecesDiscovered++;
                            break;
                        }
                    }
                }

            }
            else if (kingInArea2) {
                int piecesDiscovered = 0;
                for (int i = 0; i <= 7; i++) {
                    Square testSquare = new Square(this.col - i, this.row - i);
                    if (piecesDiscovered < 2) {
                        currentDiscoveredSquares.add(testSquare);
                    }
                    for (ChessPiece piece : opponent.pieces) {
                        if (testSquare.col == piece.col && testSquare.row == piece.row) {
                            piecesDiscovered++;
                            break;
                        }
                    }
                }
            }
            else if (kingInArea3) {
                int piecesDiscovered = 0;
                for (int i = 0; i <= 7; i++) {
                    Square testSquare = new Square(this.col - i, this.row + i);
                    if (piecesDiscovered < 2) {
                        currentDiscoveredSquares.add(testSquare);
                    }
                    for (ChessPiece piece : opponent.pieces) {
                        if (testSquare.col == piece.col && testSquare.row == piece.row) {
                            piecesDiscovered++;
                            break;
                        }
                    }
                }
            }
            else if (kingInArea4) {
                int piecesDiscovered = 0;
                for (int i = 0; i <= 7; i++) {
                    Square testSquare = new Square(this.col + i, this.row - i);
                    if (piecesDiscovered < 2) {
                        currentDiscoveredSquares.add(testSquare);
                    }
                    for (ChessPiece piece : opponent.pieces) {
                        if (testSquare.col == piece.col && testSquare.row == piece.row) {
                            piecesDiscovered++;
                            break;
                        }
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
                        currentDiscoveredSquares.add(testSquare);
                        currentProtectedSquares.add(testSquare);
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
        protectedSquares.clear();
        protectedSquares.addAll(currentProtectedSquares);
        discoveredSquares.clear();
        discoveredSquares.addAll(currentDiscoveredSquares);
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
}