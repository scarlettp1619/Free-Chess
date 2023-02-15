package com.scarwe.freechess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class ChessPlayer {

    public boolean turn = false;
    public boolean checked = false;
    public boolean checkmated = false;
    public boolean stalemated = false;
    private boolean castled = false;

    public int colour;
    public ArrayList<ChessPiece> pieces = new ArrayList<>();

    public ArrayList<ChessPiece> tempWhitePieces = new ArrayList<>();
    public ArrayList<ChessPiece> tempBlackPieces = new ArrayList<>();

    public ArrayList<Square> illegalSquares = new ArrayList<>();

    public ChessPlayer(int colour) {
        this.colour = colour;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public void setCastled(boolean castled) { this.castled = castled; }

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
            if ((BoardGame.pieceLoc(square).player.colour != this.colour)
            && (BoardGame.pieceLoc(square).type == PieceType.PAWN)
            && (BoardGame.pieceLoc(new Square(from.getCol() + movementCol, from.getRow() + movementRow))) == null
            && (BoardGame.pieceLoc(square).sinceMoved == 0)
            && (BoardGame.pieceLoc(square).currentMove == 1)
            && (from.getRow() == enPassantRow))
            {
                return (to.getRow() - from.getRow() == movementRow && to.getCol() - from.getCol() == + movementCol);
            }

        } catch (Exception e) {
            // do nothing
        }

        return false;
    }

    private boolean canPawnRegularMove(Square from, Square to) {
        if (from.getCol() == to.getCol() && BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) == null) {
            if (BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player == this) {
                if (from.getRow() == 1) return (to.getRow() == 2 || to.getRow() == 3)
                        && isClearVertically(from, to) && this.colour == 0;
                else if (from.getRow() == 6) return (to.getRow() == 5 || to.getRow() == 4)
                        && isClearVertically(from, to) && this.colour == 1;
                else if (this.colour == 0 && to.getRow() - from.getRow() == 1) return true;
                else if (this.colour == 1 && to.getRow() - from.getRow() == -1) return true;
            }
        }
        if (from.getCol() == to.getCol() - 1 && to.getRow() - from.getRow() == 1 &&
                BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) != null
                && BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player.colour == 0) {
            return true;
        }
        if (from.getCol() == to.getCol() + 1 && to.getRow() - from.getRow() == 1 &&
                BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) != null &&
                BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player.colour == 0) {
            return true;
        }
        if (from.getCol() == to.getCol() - 1 && to.getRow() - from.getRow() == -1 &&
                BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) != null &&
                BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player.colour == 1) {
            return true;
        }
        return from.getCol() == to.getCol() + 1 && to.getRow() - from.getRow() == -1 &&
                BoardGame.pieceLoc(new Square(to.getCol(), to.getRow())) != null
                && BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player.colour == 1;
    }

    private boolean canPawnMove(Square from, Square to) {
        return canPawnRegularMove(from, to) || canEnPassant(from, to);
    }

    private boolean canKnightMove(Square from, Square to) {
        // calculate possible steps
        return Math.abs(from.getCol() - to.getCol()) == 2 && Math.abs(from.getRow() - to.getRow()) == 1
                || Math.abs(from.getCol() - to.getCol()) == 1 && Math.abs(from.getRow() - to.getRow()) == 2;
    }

    private boolean canBishopMove(Square from, Square to) {
        if (Math.abs(from.getCol() - to.getCol()) == Math.abs(from.getRow() - to.getRow())) {
            return isClearDiagonally(from, to);
        }
        return false;
    }

    private boolean canRookMove(Square from, Square to) {
        return from.getCol() == to.getCol() && isClearVertically(from, to) ||
                from.getRow() == to.getRow() && isClearHorizontally(from, to);
    }

    private boolean canQueenMove(Square from, Square to) {
        return canRookMove(from, to) || canBishopMove(from, to);
    }

    private boolean canKingMove(Square from, Square to){
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

    private boolean canCastleKingSide(Square from, Square to) {
        Square kingSquare;
        if (colour == 0) kingSquare = new Square(4, 0);
        else kingSquare = new Square(4, 7);
        if (BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player == this && !castled) {
            if (BoardGame.pieceLoc(new Square(from.getCol() + 1, from.getRow())) == null
                    && BoardGame.pieceLoc(new Square(from.getCol() + 2, from.getRow())) == null
                    && BoardGame.pieceLoc(new Square(from.getCol() + 3, from.getRow())).type == PieceType.ROOK
                    && !BoardGame.pieceLoc(new Square(from.getCol() + 3, from.getRow())).hasMoved
                    && BoardGame.pieceLoc(new Square(from.getCol() + 3, from.getRow())).type != null
                    && !BoardGame.pieceLoc(kingSquare).hasMoved
                    && BoardGame.pieceLoc(kingSquare).type == PieceType.KING) {
                return to.getCol() - from.getCol() == 2;
            }
        }
        return false;
    }

    private boolean canCastleQueenSide(Square from, Square to) {
        Square kingSquare;
        if (colour == 0) kingSquare = new Square(4, 0);
        else kingSquare = new Square(4, 7);
        if (BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player == this && !castled) {
            if (BoardGame.pieceLoc(new Square(from.getCol() - 1, from.getRow())) == null
                    && BoardGame.pieceLoc(new Square(from.getCol() - 2, from.getRow())) == null
                    && BoardGame.pieceLoc(new Square(from.getCol() - 3, from.getRow())) == null
                    && BoardGame.pieceLoc(new Square(from.getCol() - 4, from.getRow())).type == PieceType.ROOK
                    && BoardGame.pieceLoc(new Square(from.getCol() - 4, from.getRow())).type != null
                    && !BoardGame.pieceLoc(new Square(from.getCol() - 4, from.getRow())).hasMoved
                    && !BoardGame.pieceLoc(kingSquare).hasMoved
                    && BoardGame.pieceLoc(kingSquare).type == PieceType.KING) {
                return to.getCol() - from.getCol() == -2;
            }
        }
        return false;
    }

    // isClear methods ensure pieces can't jump over one another
    private boolean isClearVertically(Square from, Square to) {
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

    private boolean isClearHorizontally(Square from, Square to) {
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
        ArrayList<Square> opponentLegalMoves = new ArrayList<>();
        checked = false;
        if (colour == 0) {
            for (ChessPiece p : BoardGame.blackPlayer.pieces) {
                Square tempSquare = new Square(p.col, p.row);
                opponentLegalMoves.addAll(p.generateLegalSquares(tempSquare));
            }
        } else {
            for (ChessPiece p : BoardGame.whitePlayer.pieces) {
                Square tempSquare = new Square(p.col, p.row);
                opponentLegalMoves.addAll(p.generateLegalSquares(tempSquare));
            }
        }
        for (int i = 0; i < opponentLegalMoves.size(); i++) {
            int col = opponentLegalMoves.get(i).getCol();
            int row = opponentLegalMoves.get(i).getRow();
            ChessPiece tempPiece = BoardGame.pieceLoc(new Square(col, row));
            if (tempPiece != null && tempPiece.type == PieceType.KING && tempPiece.player.colour == this.colour) {
                checked = true;
            }
            //System.out.println(opponentLegalMoves.get(i).getRowToString(col + 1) + (row + 1));
        }
    }

    public boolean isKingCheckmated() throws CloneNotSupportedException {
        HashMap<ChessPiece, ArrayList<Square>> legalMoves = new HashMap<>();
        ArrayList<ChessPiece> tempPieces;
        checkmated = true;
        if (colour == 0) tempPieces = BoardGame.whitePlayer.pieces;
        else tempPieces = BoardGame.blackPlayer.pieces;
        for (ChessPiece p : tempPieces) {
            Square tempSquare = new Square(p.col, p.row);
            legalMoves.put(p, p.generateLegalSquares(tempSquare));
            legalMoves.entrySet().removeIf(ent -> ent.getValue().isEmpty());
        }
        for (Map.Entry<ChessPiece, ArrayList<Square>> entry : legalMoves.entrySet()) {
            if (!checkmated) {
                break;
            }
            ChessPiece p = entry.getKey();
            ArrayList<Square> legalSquares = entry.getValue();
            for (Square s : legalSquares) {
                System.out.println(s.getRowToString(s.col + 1) + (s.row + 1));
                if (movePiece(new Square(p.col, p.row), s)) {
                    if (!checkmated) {
                        break;
                    }
                    isKingChecked();
                    if (!checked) {
                        BoardGame.whitePlayer.pieces.clear();
                        BoardGame.blackPlayer.pieces.clear();
                        BoardGame.whitePlayer.pieces.addAll(tempWhitePieces);
                        BoardGame.blackPlayer.pieces.addAll(tempBlackPieces);
                        checkmated = false;
                        break;
                    }
                }
            }
        }
        return checkmated;
    }

    // check if pieces are able to move, if not don't move them
    public boolean canMove(Square from, Square to){
        ChessPiece movingPiece = BoardGame.pieceLoc(from);

        // if you attempt to move back to own square, it won't count turn
        if (from.getCol() == to.getCol() && from.getRow() == to.getRow()) {
            return false;
        }
        if (to.getCol() < 0 || to.getCol() > 7 || to.getRow() < 0 || to.getRow() > 8) {
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
        return false;
    }

    public boolean movePiece(Square from, Square to) throws CloneNotSupportedException {
        if (canMove(from, to) && turn) {
            tempWhitePieces.clear();
            tempBlackPieces.clear();
            tempWhitePieces.addAll(BoardGame.whitePlayer.pieces);
            tempBlackPieces.addAll(BoardGame.blackPlayer.pieces);
            if (movePiece(from.getCol(), from.getRow(), to.getCol(), to.getRow())) {
                isKingChecked();
                if (checked) {
                    BoardGame.whitePlayer.pieces.clear();
                    BoardGame.blackPlayer.pieces.clear();
                    BoardGame.whitePlayer.pieces.addAll(tempWhitePieces);
                    BoardGame.blackPlayer.pieces.addAll(tempBlackPieces);
                    return false;
                } else {
                    return true;
                }
            }
        }
        return true;
    }

    private boolean movePiece(int fromCol, int fromRow, int toCol, int toRow) throws CloneNotSupportedException {

        if (fromCol == toCol && fromRow == toRow) return false;

        // get pieces
        ChessPiece movingPiece = BoardGame.pieceLoc(fromCol, fromRow);
        ChessPiece removePiece = BoardGame.pieceLoc(toCol, toRow);

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
                if (p.type == PieceType.PAWN && p.currentMove == 1) {
                    p.sinceMoved += 1;
                }
            }
            for (ChessPiece p : BoardGame.blackPlayer.pieces) {
                if (p.type == PieceType.PAWN && p.currentMove == 1) {
                    p.sinceMoved += 1;
                }
            }
            if (tempPiece.type == PieceType.KING && toCol - fromCol == 2) {
                castled = true;
                if (this.colour == 0) movePiece(7, 0, 5, 0); // rook
                if (this.colour == 1) movePiece(7, 7, 5, 7);
            }

            if (tempPiece.type == PieceType.KING && toCol - fromCol == -2) {
                castled = true;
                if (this.colour == 0) movePiece(0, 0, 3, 0); // rook
                if (this.colour == 1) movePiece(0, 7, 3, 7);
            }

            if (tempPiece.type == PieceType.PAWN && fromCol - toCol == 1 && Math.abs(toRow - fromRow) == 1) {
                ChessPiece removeEnPassant = BoardGame.pieceLoc(fromCol - 1, fromRow);
                BoardGame.whitePlayer.pieces.remove(removeEnPassant);
                BoardGame.blackPlayer.pieces.remove(removeEnPassant);
            }

            if (tempPiece.type == PieceType.PAWN && fromCol - toCol == -1 && Math.abs(toRow - fromRow) == 1) {
                ChessPiece removeEnPassant = BoardGame.pieceLoc(fromCol + 1, fromRow);
                BoardGame.whitePlayer.pieces.remove(removeEnPassant);
                BoardGame.blackPlayer.pieces.remove(removeEnPassant);
            }
            return true;
        }

        return false;
    }

    public boolean getCastled() {
        return castled;
    }

}
