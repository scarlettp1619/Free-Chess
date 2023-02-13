package com.scarwe.freechess;

import java.util.ArrayList;

public class BoardModel {
    ArrayList<ChessPiece> pieces = new ArrayList<>();

    {
        resetBoard();
    }

    private boolean canPawnMove(Square from, Square to) {
        if (from.getCol() == to.getCol() && pieceLoc(new Square(to.getCol(), to.getRow())) == null) {
            System.out.println(pieceLoc(new Square(to.getCol(), to.getRow())));
            // check if piece is white player (can only move up)
            if (pieceLoc(new Square(from.getCol(), from.getRow())).player == ChessPlayer.WHITE) {
                if (from.getRow() == 1) return (to.getRow() == 2 || to.getRow() == 3)
                        && isClearVertically(from, to);
                else return to.getRow() - from.getRow() == 1;
            }
            // check for black (can only go down)
            if (pieceLoc(new Square(from.getCol(), from.getRow())).player == ChessPlayer.BLACK) {
                if (from.getRow() == 6) return (to.getRow() == 5 || to.getRow() == 4)
                        && isClearVertically(from, to);
                else return from.getRow() - to.getRow() == 1;
            }
        }
        return false;
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
        return (Math.abs(from.getCol() - to.getCol()) <= 1 && (Math.abs(from.getRow() - to.getRow())) <= 1);
    }

    private boolean isClearVertically(Square from, Square to) {
        if(from.getCol() != to.getCol()) return false;
        int gap = Math.abs(from.getRow() - to.getRow()) - 1;
        if(gap == 0) return true;
        for (int i = 1; i <= gap; i++) {
            int nextRow;
            if (to.getRow() > from.getRow()) nextRow = from.getRow() + i;
            else nextRow = from.getRow() - i;
            if (pieceLoc(new Square(from.getCol(), nextRow)) != null) {
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
            if (pieceLoc(new Square(nextCol, from.getRow())) != null) {
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
            if (pieceLoc(nextCol, nextRow) != null) return false;
        }
        return true;
    }

    // check if pieces are able to move, if not don't move them
    public boolean canMove(Square from, Square to) {
        ChessPiece movingPiece = pieceLoc(from);
        // prevents user from capturing pieces using empty squares (very silly bug)
        if (movingPiece == null) {
            return false;
        }
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

    public void movePiece(Square from, Square to) {
        if(canMove(from, to)) {
            movePiece(from.getCol(), from.getRow(), to.getCol(), to.getRow());
        }
    }
    private void movePiece(int fromCol, int fromRow, int toCol, int toRow) {
        if (fromCol == toCol && fromRow == toRow) return;

        // get pieces
        ChessPiece movingPiece = pieceLoc(fromCol, fromRow);
        ChessPiece removePiece = pieceLoc(toCol, toRow);
        try {
            // players can't capture their own pieces
            if (movingPiece.player == removePiece.player) {
                return;
            }
        } catch (Exception ex) {
            // do nothing
        }
        // ensures players can't move null squares (no pieces on them)
        if (movingPiece != null) {
            pieces.remove(removePiece);
            pieces.remove(movingPiece);
            pieces.add(new ChessPiece(toCol, toRow, movingPiece.player, movingPiece.type, movingPiece.resID));
        }

    }

    public void resetBoard() {
        // clears arraylist
        pieces.clear();
        // draws all pieces to arraylist
        for (int i = 0; i <= 1; i++) {
            // rooks
            pieces.add(new ChessPiece(i * 7, 0, ChessPlayer.WHITE, PieceType.ROOK, R.drawable. wr));
            pieces.add(new ChessPiece(i * 7, 7, ChessPlayer.BLACK, PieceType.ROOK, R.drawable.br));
            // knights
            pieces.add(new ChessPiece(1 + i * 5, 0, ChessPlayer.WHITE, PieceType.KNIGHT, R.drawable.wn));
            pieces.add(new ChessPiece(1 + i * 5, 7, ChessPlayer.BLACK, PieceType.KNIGHT, R.drawable.bn));
            // bishops
            pieces.add(new ChessPiece(2 + i * 3, 0, ChessPlayer.WHITE, PieceType.BISHOP, R.drawable.wb));
            pieces.add(new ChessPiece(2 + i * 3, 7, ChessPlayer.BLACK, PieceType.BISHOP, R.drawable.bb));
        }
        for (int i = 0; i <= 7; i++) {
            // pawns
            pieces.add(new ChessPiece(i, 1, ChessPlayer.WHITE, PieceType.PAWN, R.drawable.wp));
            pieces.add(new ChessPiece(i, 6, ChessPlayer.BLACK, PieceType.PAWN, R.drawable.bp));
        }
        // queens
        pieces.add(new ChessPiece(3, 0, ChessPlayer.WHITE, PieceType.QUEEN, R.drawable.wq));
        pieces.add(new ChessPiece(3, 7, ChessPlayer.BLACK, PieceType.QUEEN, R.drawable.bq));
        // kings
        pieces.add(new ChessPiece(4, 0, ChessPlayer.WHITE, PieceType.KING, R.drawable.wk));
        pieces.add(new ChessPiece(4, 7, ChessPlayer.BLACK, PieceType.KING, R.drawable.bk));
    }

    public ChessPiece pieceLoc(Square square) {
        return pieceLoc(square.getCol(), square.getRow());
    }

    // locate a piece using its position on the board
    private ChessPiece pieceLoc(int col, int row) {
        for (ChessPiece piece : pieces) {
            if(col == piece.col && row == piece.row) {
                return piece;
            }
        }
        return null;
    }

    public String pgnBoard() {
        StringBuilder desc = new StringBuilder(" \n");
        desc.append("  a b c d e f g h\n");
        // i determines rows of model
        for (int i = 7; i >= 0; i--) {
            desc.append(i + 1);
            desc.append(boardRow(i));
            desc.append(" ").append(i + 1);
            desc.append("\n");
        }
        desc.append("  a b c d e f g h");
        return desc.toString();
    }
    public String stringBoard() {
        StringBuilder desc = new StringBuilder(" \n");
        // i determines rows of model
        for (int i = 7; i >= 0; i--) {
            desc.append(i);
            desc.append(boardRow(i));
            desc.append("\n");
        }
        desc.append("  0 1 2 3 4 5 6 7");
        return desc.toString();
    }

    public String boardRow(int i) {
        StringBuilder desc = new StringBuilder("");
            // j determines columns of model
        for (int j = 0; j < 8; j++) {
            ChessPiece piece = pieceLoc(i, j);
            if (piece == null) {
                desc.append(" .");
            } else {
                desc.append(" ");
                if (piece.getType() == PieceType.KING) {
                    if (piece.player == ChessPlayer.WHITE) desc.append("k");
                    else desc.append("K");
                }
                if (piece.getType() == PieceType.QUEEN) {
                    if (piece.player == ChessPlayer.WHITE) desc.append("q");
                    else desc.append("Q");
                }
                if (piece.getType() == PieceType.ROOK) {
                    if (piece.player == ChessPlayer.WHITE) desc.append("r");
                    else desc.append("R");
                }
                if (piece.getType() == PieceType.BISHOP) {
                    if (piece.player == ChessPlayer.WHITE) desc.append("b");
                    else desc.append("B");
                }
                if (piece.getType() == PieceType.KNIGHT) {
                    if (piece.player == ChessPlayer.WHITE) desc.append("n");
                    else desc.append("N");
                }
                if (piece.getType() == PieceType.PAWN) {
                    if (piece.player == ChessPlayer.WHITE) desc.append("p");
                    else desc.append("P");
                }
            }
        }
        return desc.toString();
    }
}
