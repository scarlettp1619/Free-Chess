package com.scarwe.freechess;

import java.util.ArrayList;

public class ChessPlayer {

    public int colour;
    public boolean turn = false;
    private boolean castled = false;
    public ArrayList<ChessPiece> pieces = new ArrayList<>();

    public ChessPlayer(int colour) {
        this.colour = colour;
    }

    public void setTurn(boolean turn) {
        this.turn = turn;
    }

    public void setCastled(boolean castled) { this.castled = castled; }


    private boolean canPawnMove(Square from, Square to) {
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

    private boolean canKingMove(Square from, Square to) {
        if(canCastleKingSide(from, to)) {
            return true;
        }
        if(canCastleQueenSide(from, to)) {
            return true;
        }
        return Math.abs(from.getCol() - to.getCol()) <= 1 && (Math.abs(from.getRow() - to.getRow())) <= 1;
    }

    private boolean canCastleKingSide(Square from, Square to) {
        Square kingSquare;
        if (colour == 0) kingSquare = new Square(4, 0);
        else kingSquare = new Square(4, 7);
        if (BoardGame.pieceLoc(new Square(from.getCol(), from.getRow())).player == this && !castled) {
            if (BoardGame.pieceLoc(new Square(from.getCol() + 1, from.getRow())) == null
                    && BoardGame.pieceLoc(new Square(from.getCol() + 2, from.getRow())) == null
                    // finds rook to castle with
                    && BoardGame.pieceLoc(new Square(from.getCol() + 3, from.getRow())).type == PieceType.ROOK
                    && BoardGame.pieceLoc(kingSquare).type == PieceType.KING) {
                if (to.getCol() - from.getCol() == 2) {
                    // moves rook
                    if(this.colour == 0) {
                        movePiece(7, 0, 5, 0);
                        this.castled = true;
                    }
                    if(this.colour == 1) {
                        movePiece(7, 7, 5, 7);
                        this.castled = true;
                    };
                    return true;
                }
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
                    && BoardGame.pieceLoc(kingSquare).type == PieceType.KING) {
                if (to.getCol() - from.getCol() == -2) {
                    // moves rook
                    if(this.colour == 0) {
                        movePiece(0, 0, 3, 0); // rook
                        this.castled = true;
                    }
                    if(this.colour == 1) {
                        movePiece(0, 7, 3, 7);
                        this.castled = true;
                    };
                    return true;
                }
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

    // check if pieces are able to move, if not don't move them
    public boolean canMove(Square from, Square to) {
        ChessPiece movingPiece = BoardGame.pieceLoc(from);
        // if you attempt to move back to own square, it won't count turn
        if (from.getCol() == to.getCol() && from.getRow() == to.getRow()) {
            return false;
        }
        if (to.getCol() < 0 || to.getCol() > 7 || to.getRow() < 0 || to.getRow() > 8) {
            return false;
        }
        switch(movingPiece.type) {
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
        return true;
    }

    public boolean movePiece(Square from, Square to) {
        if (canMove(from, to) && turn) {
            return movePiece(from.getCol(), from.getRow(), to.getCol(), to.getRow());
        }
        return false;
    }

    private boolean movePiece(int fromCol, int fromRow, int toCol, int toRow) {
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
                ex.printStackTrace();
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
            pieces.add(new ChessPiece(toCol, toRow, BoardGame.currentPlayer, movingPiece.type, movingPiece.resID));
            System.out.println(BoardGame.pgnBoard());
            return true;
        }
        return false;
    }

}
