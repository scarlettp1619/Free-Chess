package com.scarwe.freechess.game;

import com.scarwe.freechess.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class ChessPlayer {

    public boolean turn = false;
    public boolean checked = false;

    public int colour;
    public int castled;

    public ArrayList<ChessPiece> pieces = new ArrayList<>();

    public ArrayList<ChessPiece> tempWhitePieces = new ArrayList<>();
    public ArrayList<ChessPiece> tempBlackPieces = new ArrayList<>();

    public ChessPlayer(int colour) {
        this.colour = colour;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public boolean pawnJustCaptured = false;
    public boolean captureMove = false;

    {
        if (colour == 0) turn = true;
    }

    private boolean canEnPassant(Square from, Square to) {
        int movementCol, movementRow, enPassantRow;
        if (from.getCol() - to.getCol() == 1) movementCol = -1;
        else movementCol = 1;

        if (this.colour == 0) {
            movementRow = 1;
            enPassantRow = 4;
        }
        else {
            movementRow = -1;
            enPassantRow = 3;
        }

        Square square = new Square(from.getCol() + movementCol, from.getRow());

        try {
            if ((BoardGame.pieceLoc(square).getPlayer().colour != this.colour)
                    && (BoardGame.pieceLoc(square).getType() == PieceType.PAWN)
                    && (BoardGame.pieceLoc(new Square(from.getCol() + movementCol, from.getRow() + movementRow))) == null
                    && (BoardGame.pieceLoc(square).getSinceMoved() == 0)
                    && (BoardGame.pieceLoc(square).getCurrentMove() == 1)
                    && (from.getRow() == enPassantRow))
            {
                return (to.getRow() - from.getRow() == movementRow && to.getCol() - from.getCol() == movementCol);
            }

        } catch (Exception e) {
            // do nothing
        }

        return false;
    }

    private boolean canPawnRegularMove(Square from, Square to) {
        pawnJustCaptured = false;
        if (from.getCol() == to.getCol() && BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) == null) {
            if (BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player == this) {
                if (from.getRow() == 1 && this.colour == 0) return (to.getRow() == 2 || to.getRow() == 3)
                        && isClearVertically(from, to) && this.colour == 0;
                else if (from.getRow() == 6 && this.colour == 1) return (to.getRow() == 5 || to.getRow() == 4)
                        && isClearVertically(from, to) && this.colour == 1;
                else if (this.colour == 0 && to.getRow() - from.getRow() == 1) return true;
                else return this.colour == 1 && to.getRow() - from.getRow() == -1;
            }
        }
        else if (from.getCol() == to.getCol() - 1 && to.getRow() - from.getRow() == 1 &&
                BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) != null
                && BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player.colour == 0) {
            pawnJustCaptured = true;
            return true;
        }
        else if (from.getCol() == to.getCol() + 1 && to.getRow() - from.getRow() == 1 &&
                BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) != null &&
                BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player.colour == 0) {
            pawnJustCaptured = true;
            return true;
        }
        else if (from.getCol() == to.getCol() - 1 && to.getRow() - from.getRow() == -1 &&
                BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) != null &&
                BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player.colour == 1) {
            pawnJustCaptured = true;
            return true;
        } else if (from.getCol() == to.getCol() + 1 && to.getRow() - from.getRow() == -1 &&
                BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) != null
                && BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player.colour == 1) {
            pawnJustCaptured = true;
            return true;
        }
        return false;
    }

    public boolean canPawnMove(Square from, Square to) {
        return canPawnRegularMove(from, to) || canEnPassant(from, to);
    }

    public boolean canKnightMove(Square from, Square to) {
        // calculate possible steps
        return Math.abs(from.getCol() - to.getCol()) == 2 && Math.abs(from.getRow() - to.getRow()) == 1
                || Math.abs(from.getCol() - to.getCol()) == 1 && Math.abs(from.getRow() - to.getRow()) == 2;
    }

    public boolean canBishopMove(Square from, Square to) {
        if (Math.abs(from.getCol() - to.getCol()) == Math.abs(from.getRow() - to.getRow())) {
            return isClearDiagonally(from, to);
        }
        return false;
    }

    public boolean canRookMove(Square from, Square to) {
        return from.getCol() == to.getCol() && isClearVertically(from, to) ||
                from.getRow() == to.getRow() && isClearHorizontally(from, to);
    }

    public boolean canQueenMove(Square from, Square to) {
        return canRookMove(from, to) || canBishopMove(from, to);
    }

    public boolean canKingMove(Square from, Square to){
        if(canQueenMove(from, to)) {
            if(canCastleKingSide(from, to)) {
                return true;
            }
            if(canCastleQueenSide(from, to)) {
                return true;
            }
            return Math.abs(from.row - to.row) == 1 &&
                    (Math.abs(from.row - to.row) == 0 || Math.abs(from.row - to.row) == 1) ||
                    Math.abs(from.col - to.col) == 1 &&
                            (Math.abs(from.row - to.row) == 0 || Math.abs(from.row - to.row) == 1);
        }
        return false;
    }

    public boolean canCastleKingSide(Square from, Square to) {
        Square kingSquare;
        ArrayList<ChessPiece> tempPieces;
        if (colour == 0) {
            kingSquare = new Square(4, 0);
            tempPieces = BoardGame.blackPlayer.pieces;
        }
        else {
            kingSquare = new Square(4, 7);
            tempPieces = BoardGame.whitePlayer.pieces;
        }
        try {
            if (BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).getPlayer() == this) {
                if (BoardGame.pieceLoc(new Square(from.getCol() + 1, from.getRow())) == null
                        && BoardGame.pieceLoc(new Square(from.getCol() + 2, from.getRow())) == null
                        && BoardGame.pieceLoc(new Square(from.getCol() + 3, from.getRow())).getType() == PieceType.ROOK
                        && !BoardGame.pieceLoc(new Square(from.getCol() + 3, from.getRow())).getHasMoved()
                        && BoardGame.pieceLoc(new Square(from.getCol() + 3, from.getRow())).getType() != null
                        && !BoardGame.pieceLoc(kingSquare).getHasMoved()
                        && BoardGame.pieceLoc(kingSquare).getType() == PieceType.KING) {
                        for (ChessPiece p : tempPieces) {
                            try {
                                for (Square s : p.legalSquares) {
                                    if (s.col == from.getCol() + 1 && s.row == from.getRow()
                                            || s.col == from.getCol() + 2 && s.row == from.getRow()) {
                                        return false;
                                    }
                                }
                            } catch (Exception eg) {
                                //
                            }
                        }
                    return to.getCol() - from.getCol() == 2;
                }
            }
        } catch (Exception ex) {
            // do a little bit
        }
        return false;
    }

    public boolean canCastleQueenSide(Square from, Square to) {
        Square kingSquare;
        ArrayList<ChessPiece> tempPieces;
        if (colour == 0) {
            kingSquare = new Square(4, 0);
            tempPieces = BoardGame.blackPlayer.pieces;
        }
        else {
            kingSquare = new Square(4, 7);
            tempPieces = BoardGame.whitePlayer.pieces;
        }
        try {
            if (BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).getPlayer() == this) {
                if (BoardGame.pieceLoc(new Square(from.getCol() - 1, from.getRow())) == null
                        && BoardGame.pieceLoc(new Square(from.getCol() - 2, from.getRow())) == null
                        && BoardGame.pieceLoc(new Square(from.getCol() - 3, from.getRow())) == null
                        && BoardGame.pieceLoc(new Square(from.getCol() - 4, from.getRow())).getType() == PieceType.ROOK
                        && BoardGame.pieceLoc(new Square(from.getCol() - 4, from.getRow())).getType() != null
                        && !BoardGame.pieceLoc(new Square(from.getCol() - 4, from.getRow())).getHasMoved()
                        && !BoardGame.pieceLoc(kingSquare).getHasMoved()
                        && BoardGame.pieceLoc(kingSquare).getType() == PieceType.KING) {
                    for (ChessPiece p : tempPieces) {
                        try {
                            for (Square s : p.legalSquares) {
                                if (s.col == from.getCol() - 1 && s.row == from.getRow()
                                        || s.col == from.getCol() - 2 && s.row == from.getRow()
                                        || s.col == from.getCol() - 3 && s.row == from.getRow()) {
                                    return false;
                                }
                            }
                        } catch (Exception eg) {
                            //
                        }
                    }
                    return to.getCol() - from.getCol() == -2;
                }
            }
        } catch (Exception ex) {
            // do a little bit
        }
        return false;
    }

    // isClear methods ensure pieces can't jump over one another
    public boolean isClearVertically(Square from, Square to) {
        if(from.getCol() != to.getCol()) return false;
        int gap = Math.abs(from.getRow() - to.getRow()) - 1;
        if(gap == 0) return true;
        for (int i = 1; i <= gap; i++) {
            int nextRow;
            if (to.getRow() > from.getRow()) nextRow = from.getRow() + i;
            else nextRow = from.getRow() - i;
            if (BoardGame.pieceLoc(new Square(from.getCol(), nextRow)) != null) {
                return false;
            }
        }
        return true;

    }

    public boolean isClearHorizontally(Square from, Square to) {
        if(from.getRow() != to.getRow()) return false;
        int gap = Math.abs(from.getCol() - to.getCol()) - 1;
        if(gap == 0) return true;
        for (int i = 1; i <= gap; i++) {
            int nextCol;
            if (to.getCol() > from.getCol()) nextCol = from.getCol() + i;
            else nextCol = from.getCol() - i;
            if (BoardGame.pieceLoc(new Square(nextCol, from.getRow())) != null) {
                return false;
            }
        }
        return true;
    }

    private boolean isClearDiagonally(Square from, Square to) {
        if (Math.abs(from.getCol() - to.getCol()) != Math.abs(from.getRow() - to.getRow())) return false;
        int gap = Math.abs(from.getCol() - to.getCol()) - 1;
        for (int i = 1; i <= gap; i++) {
            int nextCol, nextRow;
            if (to.getCol() > from.getCol()) nextCol = from.getCol() + i;
            else nextCol = from.getCol() - i;
            if (to.getRow() > from.getRow()) nextRow = from.getRow() + i;
            else nextRow = from.getRow() - i;
            if (BoardGame.pieceLoc(nextCol, nextRow) != null) return false;
        }
        return true;
    }

    public void isKingChecked() throws CloneNotSupportedException {
        LinkedHashSet<Square> opponentLegalMoves = new LinkedHashSet<>();
        checked = false;
        if (colour == 0) {
            for (ChessPiece p : BoardGame.blackPlayer.pieces) {
                Square tempSquare = new Square(p.col, p.row);
                p.generateLegalSquares(tempSquare);
                opponentLegalMoves.addAll(p.legalSquares);
            }
        } else {
            for (ChessPiece p : BoardGame.whitePlayer.pieces) {
                Square tempSquare = new Square(p.col, p.row);
                p.generateLegalSquares(tempSquare);
                opponentLegalMoves.addAll(p.legalSquares);
            }
        }
        Square[] squareSet = new Square[opponentLegalMoves.size()];
        squareSet = opponentLegalMoves.toArray(squareSet);
        for (int i = 0; i < opponentLegalMoves.size(); i++) {
            int col = squareSet[i].getCol();
            int row = squareSet[i].getRow();
            ChessPiece tempPiece = BoardGame.pieceLoc(new Square(col, row));
            if (tempPiece != null && tempPiece.type == PieceType.KING && tempPiece.player.colour == this.colour) {
                checked = true;
            }
        }
    }

    public void findLegalMoves() throws CloneNotSupportedException {
        float startTime = System.nanoTime();
        HashMap<ChessPiece, LinkedHashSet<Square>> legalMoves = new HashMap<>();
        LinkedHashSet<Square> newLegalMoves = new LinkedHashSet<>();
        ArrayList<ChessPiece> tempPieces;
        if (colour == 0) tempPieces = BoardGame.whitePlayer.pieces;
        else tempPieces = BoardGame.blackPlayer.pieces;

        for (ChessPiece p : tempPieces) {
            legalMoves.put(p, p.legalSquares);
            legalMoves.entrySet().removeIf(ent -> ent.getValue().isEmpty());
        }

        for (Map.Entry<ChessPiece, LinkedHashSet<Square>> entry : legalMoves.entrySet()) {
            ChessPiece p = entry.getKey();
            LinkedHashSet<Square> legalSquares = entry.getValue();
            newLegalMoves.clear();
            for (Square s : legalSquares) {
                if (movePiece(new Square(p.col, p.row), s, true)) {
                    newLegalMoves.add(s);
                    resetPieces();
                    //System.out.println(p.type + " " + s.getRowToString(s.col + 1) + (s.row + 1) + " " + colour);
                }
            }
            p.legalSquares.clear();
            p.legalSquares.addAll(newLegalMoves);
        }

        float endTime = System.nanoTime();
        float totalTime = endTime - startTime;
        System.out.println((totalTime/1000000000) + " seconds to calculate legal moves");
    }

    // check if pieces are able to move, if not don't move them
    public boolean canMove(Square from, Square to){
        try {
            ChessPiece movingPiece = BoardGame.pieceLoc(from);

            // if you attempt to move back to own square, it won't count turn
            if (from.getCol() == to.getCol() && from.getRow() == to.getRow()) {
                return false;
            }
            if (to.getCol() < 0 || to.getCol() > 7 || to.getRow() < 0 || to.getRow() > 7) {
                return false;
            }
            switch (movingPiece.type) {
                case PAWN:
                    return canPawnMove(from, to);
                case KNIGHT:
                    return canKnightMove(from, to);
                case BISHOP:
                    return canBishopMove(from, to);
                case ROOK:
                    return canRookMove(from, to);
                case QUEEN:
                    return canQueenMove(from, to);
                case KING:
                    return canKingMove(from, to);
            }
        } catch  (Exception ex) {
            //
        }
        return false;
    }

    public boolean movePiece(Square from, Square to, boolean testMove) throws CloneNotSupportedException {
        try {
            if (canMove(from, to) && turn) {
                clearTempPieces();
                if (movePiece(from.getCol(), from.getRow(), to.getCol(), to.getRow(), testMove)) {
                    isKingChecked();
                    if (checked) {
                        resetPieces();
                        return false;
                    } else {
                        return true;
                    }
                }
            }
        } catch (Exception ex) {
            //
        }
        return false;
    }

    private boolean movePiece(int fromCol, int fromRow, int toCol, int toRow, boolean testMove) throws CloneNotSupportedException {

        int piecesSize;

        if (fromCol == toCol && fromRow == toRow) return false;
        // get pieces
        ChessPiece movingPiece = BoardGame.pieceLoc(fromCol, fromRow);
        ChessPiece removePiece = BoardGame.pieceLoc(toCol, toRow);

        captureMove = false;
        ArrayList<ChessPiece> tempPieces;

        if (colour == 0) tempPieces = BoardGame.blackPlayer.pieces;
        else tempPieces = BoardGame.whitePlayer.pieces;

        piecesSize = tempPieces.size();

        assert movingPiece != null;
        if (movingPiece.player == this) {
            try {
                // players can't capture their own pieces
                if (movingPiece.player == removePiece.player) {
                    return false;
                }
            } catch (Exception ex) {
                // do nothing
            }
            // ensures players can't move null squares (no pieces on them)
            BoardGame.currentPlayer.pieces.remove(removePiece);
            BoardGame.currentPlayer.pieces.remove(movingPiece);

            if (BoardGame.currentPlayer == BoardGame.whitePlayer) {
                BoardGame.blackPlayer.pieces.remove(removePiece);
                BoardGame.blackPlayer.pieces.remove(movingPiece);
            } else {
                BoardGame.whitePlayer.pieces.remove(removePiece);
                BoardGame.whitePlayer.pieces.remove(movingPiece);
            }

            ChessPiece tempPiece = (ChessPiece) movingPiece.clone();

            tempPiece.col = toCol;
            tempPiece.row = toRow;
            tempPiece.hasMoved = true;
            tempPiece.currentMove += 1;
            pieces.add(tempPiece);

            for (ChessPiece p : BoardGame.whitePlayer.pieces) {
                if (p.getType() == PieceType.PAWN && p.getCurrentMove() == 1) {
                    if (!testMove) p.addSinceMoved(1);
                }
            }
            for (ChessPiece p : BoardGame.blackPlayer.pieces) {
                if (p.getType() == PieceType.PAWN && p.getCurrentMove() == 1) {
                    if (!testMove) p.addSinceMoved(1);
                }
            }

            if (tempPiece.type == PieceType.KING && toCol - fromCol == 2 && !testMove) {
                if (this.colour == 0) {
                    movePiece(7, 0, 5, 0, true); // rook
                    castled = 1; // kingside
                }
                if (this.colour == 1) {
                    movePiece(7, 7, 5, 7, true);
                    castled = 1; // kingside
                }
            }
            if (tempPiece.type == PieceType.KING && toCol - fromCol == -2 && !testMove) {
                if (this.colour == 0) {
                    movePiece(0, 0, 3, 0, true); // rook
                    castled = 2; // kingside
                }
                if (this.colour == 1) {
                    movePiece(0, 7, 3, 7, true);
                    castled = 2; // kingside
                }
            }

            try {
                if (tempPiece.type == PieceType.PAWN && Math.abs(toCol - fromCol) == 1 && toRow - fromRow == 1 && !testMove
                        && BoardGame.pieceLoc(toCol, fromRow).getType() == PieceType.PAWN
                        && BoardGame.pieceLoc(toCol,fromRow).getPlayer().colour != colour && !pawnJustCaptured) {
                    ChessPiece removeEnPassant = BoardGame.pieceLoc(toCol, fromRow);
                    BoardGame.whitePlayer.pieces.remove(removeEnPassant);
                    BoardGame.blackPlayer.pieces.remove(removeEnPassant);
                }
            } catch (Exception ex) {
                //
            }

            try {
                if (tempPiece.type == PieceType.PAWN && Math.abs(toCol - fromCol) == 1 && toRow - fromRow == -1 && !testMove
                        && BoardGame.pieceLoc(toCol, fromRow).getType() == PieceType.PAWN &&
                        BoardGame.pieceLoc(toCol,fromRow).getPlayer().colour != colour && !pawnJustCaptured) {
                    ChessPiece removeEnPassant = BoardGame.pieceLoc(toCol, fromRow);
                    BoardGame.whitePlayer.pieces.remove(removeEnPassant);
                    BoardGame.blackPlayer.pieces.remove(removeEnPassant);
                }
            } catch (Exception ex) {
                //
            }

            if (tempPiece.getType() == PieceType.PAWN && tempPiece.getPlayer().colour == 0 && toRow == 7) {
                tempPiece.setPieceType(PieceType.QUEEN);
                tempPiece.setResId(R.drawable.wq);
            }

            if (tempPiece.getType() == PieceType.PAWN && tempPiece.getPlayer().colour == 1 && toRow == 0) {
                tempPiece.setPieceType(PieceType.QUEEN);
                tempPiece.setResId(R.drawable.bq);
            }

            int newPiecesSize = tempPieces.size();

            if (piecesSize > newPiecesSize) {
                captureMove = true;
            }

            if (!testMove) {
                BoardGame.gameMove++;
                addToPgn(new Square(fromCol, fromRow), new Square(toCol, toRow), tempPiece.getType(), tempPiece, captureMove, castled);
            }

            return true;
        }

        return false;
    }

    public void resetPieces() {
        BoardGame.whitePlayer.pieces.clear();
        BoardGame.blackPlayer.pieces.clear();
        BoardGame.whitePlayer.pieces.addAll(tempWhitePieces);
        BoardGame.blackPlayer.pieces.addAll(tempBlackPieces);
    }

    public void clearTempPieces() {
        tempWhitePieces.clear();
        tempBlackPieces.clear();
        tempWhitePieces.addAll(BoardGame.whitePlayer.pieces);
        tempBlackPieces.addAll(BoardGame.blackPlayer.pieces);
    }

    public void addToPgn(Square from, Square to, PieceType type, ChessPiece piece, boolean capture, int castled) {
        boolean overlap = false;
        ArrayList<ChessPiece> typePieces = new ArrayList<>();
        ArrayList<Square> overlapMoves = new ArrayList<>();

        if (type == PieceType.KNIGHT || type == PieceType.ROOK) {
            for (ChessPiece p : pieces) {
                typePieces.add(p.getPiecesOfType(type, piece));
            }
        }

        for (ChessPiece p : typePieces) {
            if (p != null) {
                overlapMoves.addAll(p.legalSquares);
            }
        }

        typePieces.removeAll(Collections.singleton(null));

        if (BoardGame.gameMove % 2 == 0) {
            if (BoardGame.pgnMoves.length() > 0) BoardGame.pgnMoves.append(" ");
            BoardGame.pgnMoves.append(Math.round((float) BoardGame.gameMove / 2)).append(".");
        }

        if (type == PieceType.PAWN) {
            BoardGame.pgnMoves.append(" ");
            BoardGame.pgnMoves.append(from.getValToString(from.getCol()).toLowerCase());
            if (capture)
                BoardGame.pgnMoves.append("x").append(to.getValToString(to.getCol()).toLowerCase());
            BoardGame.pgnMoves.append(to.getRow() + 1);
        }

        if (type == PieceType.KNIGHT) {
            BoardGame.pgnMoves.append(" ");
            BoardGame.pgnMoves.append("N");
            for (Square s : overlapMoves) {
                if (to.getCol() == s.getCol() && to.getRow() == s.getRow()) {
                    overlap = true;
                    break;
                }
            }
            if (overlap) {
                if (capture) BoardGame.pgnMoves.append("x");
                else BoardGame.pgnMoves.append(from.getValToString(to.getCol()).toLowerCase()); // g
            }
            if (capture) BoardGame.pgnMoves.append("x");
            BoardGame.pgnMoves.append(from.getValToString(to.getCol()).toLowerCase()).append(to.getRow() + 1);
        }

        if (type == PieceType.BISHOP) {
            BoardGame.pgnMoves.append(" ").append("B");
            if (capture) BoardGame.pgnMoves.append("x");
            BoardGame.pgnMoves.append(from.getValToString(to.getCol()).toLowerCase()).append(to.getRow() + 1);
        }

        if (type == PieceType.ROOK) {
            BoardGame.pgnMoves.append(" ");
            BoardGame.pgnMoves.append("R");
            for (Square s : overlapMoves) {
                if (to.getCol() == s.getCol() && to.getRow() == s.getRow()) {
                    overlap = true;
                    break;
                }
            }
            if (overlap) {
                if (capture) BoardGame.pgnMoves.append("x");
                else BoardGame.pgnMoves.append(from.getValToString(to.getCol()).toLowerCase()); // g
            }
            if (capture) BoardGame.pgnMoves.append("x");
            BoardGame.pgnMoves.append(from.getValToString(to.getCol()).toLowerCase());
            BoardGame.pgnMoves.append(to.getRow() + 1);
        }

        if (type == PieceType.QUEEN) {
            BoardGame.pgnMoves.append(" ").append("Q");
            if (capture) BoardGame.pgnMoves.append("x");
            BoardGame.pgnMoves.append(from.getValToString(to.getCol()).toLowerCase()).append(to.getRow() + 1);
        }

        if (type == PieceType.KING) {
            BoardGame.pgnMoves.append(" ");
            System.out.println(castled);
            if (castled == 1) BoardGame.pgnMoves.append("O-O");
            else if (castled == 2) BoardGame.pgnMoves.append("O-O-O");
            else {
                System.out.println(castled);
                BoardGame.pgnMoves.append("K");
                if (capture) BoardGame.pgnMoves.append("x");
                BoardGame.pgnMoves.append(from.getValToString(to.getCol()).toLowerCase()).append(to.getRow() + 1);
            }
        }
    }
}