package com.scarwe.freechess.game;

import com.scarwe.freechess.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Objects;

public class BoardGame {
    public boolean whiteCastled = false;
    public boolean blackCastled = false;

    public static ChessPlayer whitePlayer = new ChessPlayer(0);
    public static ChessPlayer blackPlayer = new ChessPlayer(1);
    public static ChessPlayer currentPlayer = whitePlayer;

    public static int gameMove = 1;

    public static ArrayList<ChessPiece> pieces = new ArrayList<>();
    public static ArrayList<String> pgnCheck = new ArrayList<>();


    public static ArrayList<PieceType> pawnMoveSet = new ArrayList<>();
    public static ArrayList<PieceType> knightMoveSet = new ArrayList<>();
    public static ArrayList<PieceType> bishopMoveSet = new ArrayList<>();
    public static ArrayList<PieceType> rookMoveSet = new ArrayList<>();
    public static ArrayList<PieceType> queenMoveSet = new ArrayList<>();
    public static ArrayList<PieceType> kingMoveSet = new ArrayList<>();


    public static StringBuilder pgnMoves = new StringBuilder("");
    public BufferedReader reader = null;

    public void resetBoard() throws IOException, CloneNotSupportedException {
        knightMoveSet.add(PieceType.KNIGHT);
        bishopMoveSet.add(PieceType.BISHOP);
        rookMoveSet.add(PieceType.ROOK);
        queenMoveSet.add(PieceType.QUEEN);
        kingMoveSet.add(PieceType.KING);
        // clears arraylist
        whitePlayer.pieces.clear();
        blackPlayer.pieces.clear();

        whitePlayer.setTurn(true);
        blackPlayer.setTurn(false);

        gameMove = 1;
        currentPlayer = whitePlayer;

        whitePlayer.checked = false;
        blackPlayer.checked = false;

        whitePlayer.sincePawnMoved = -1;
        whitePlayer.sinceCaptured = 0;

        blackPlayer.sincePawnMoved = -1;
        blackPlayer.sinceCaptured = 0;

        pgnMoves.setLength(0);
        pgnCheck.clear();

        pieces.addAll(whitePlayer.pieces);
        pieces.addAll(blackPlayer.pieces);

        String line = reader.readLine();
        String pawnMoves = null, knightMoves = null, bishopMoves = null,
                rookMoves = null, queenMoves = null, kingMoves = null;
        ArrayList<String> moveSet = new ArrayList<>();

        while (line != null) {
            String testLine = line.replaceAll("\\s","").replace("\"", "");
            if (testLine.startsWith("PawnMoves:")) pawnMoves = testLine;
            if (testLine.startsWith("KnightMoves:")) knightMoves = testLine;
            if (testLine.startsWith("BishopMoves:")) bishopMoves = testLine;
            if (testLine.startsWith("RookMoves:")) rookMoves = testLine;
            if (testLine.startsWith("QueenMoves:")) queenMoves = testLine;
            if (testLine.startsWith("KingMoves:")) kingMoves = testLine;
            line = reader.readLine();
        }

        moveSet.add(pawnMoves);
        moveSet.add(knightMoves);
        moveSet.add(bishopMoves);
        moveSet.add(rookMoves);
        moveSet.add(queenMoves);
        moveSet.add(kingMoves);

        for (int i = 0; i < moveSet.size(); i++) {
            String s = moveSet.get(i);
            String[] possibleMoves = s.split(":");
            ArrayList<PieceType> currentMoveSet = new ArrayList<>();

            if (i == 0) currentMoveSet = pawnMoveSet;
            if (i == 1) currentMoveSet = knightMoveSet;
            if (i == 2) currentMoveSet = bishopMoveSet;
            if (i == 3) currentMoveSet = rookMoveSet;
            if (i == 4) currentMoveSet = queenMoveSet;
            if (i == 5) currentMoveSet = kingMoveSet;

            if (possibleMoves.length == 2) {
                String[] findMoves = possibleMoves[1].split(",");
                for (String st : findMoves) {

                    if (Objects.equals(st, "pawn")) currentMoveSet.add(PieceType.PAWN);
                    if (Objects.equals(st, "knight")) currentMoveSet.add(PieceType.KNIGHT);
                    if (Objects.equals(st, "bishop")) currentMoveSet.add(PieceType.BISHOP);
                    if (Objects.equals(st, "rook")) currentMoveSet.add(PieceType.ROOK);
                    if (Objects.equals(st, "king")) currentMoveSet.add(PieceType.KING);
                }
            }
        }

        // draws all pieces to arraylist
        for (int i = 0; i <= 1; i++) {
            // rooks
            whitePlayer.pieces.add(new ChessPiece(i * 7, 0, whitePlayer, PieceType.ROOK, R.drawable.wr, rookMoveSet));
            blackPlayer.pieces.add(new ChessPiece(i * 7, 7, blackPlayer, PieceType.ROOK, R.drawable.br, rookMoveSet));
            // knights
            whitePlayer.pieces.add(new ChessPiece(1 + i * 5, 0, whitePlayer, PieceType.KNIGHT, R.drawable.wn, knightMoveSet));
            blackPlayer.pieces.add(new ChessPiece(1 + i * 5, 7, blackPlayer, PieceType.KNIGHT, R.drawable.bn, knightMoveSet));
            // bishops
            whitePlayer.pieces.add(new ChessPiece(2 + i * 3, 0, whitePlayer, PieceType.BISHOP, R.drawable.wb, bishopMoveSet));
            blackPlayer.pieces.add(new ChessPiece(2 + i * 3, 7, blackPlayer, PieceType.BISHOP, R.drawable.bb, bishopMoveSet));
        }
        for (int i = 0; i <= 7; i++) {
            // pawns
            whitePlayer.pieces.add(new ChessPiece(i, 1, whitePlayer, PieceType.PAWN, R.drawable.wp, pawnMoveSet));
            blackPlayer.pieces.add(new ChessPiece(i, 6, blackPlayer, PieceType.PAWN, R.drawable.bp, pawnMoveSet));
        }
        // queens
        whitePlayer.pieces.add(new ChessPiece(3, 0, whitePlayer, PieceType.QUEEN, R.drawable.wq, queenMoveSet));
        blackPlayer.pieces.add(new ChessPiece(3, 7, blackPlayer, PieceType.QUEEN, R.drawable.bq, queenMoveSet));
        // kings
        whitePlayer.pieces.add(new ChessPiece(4, 0, whitePlayer, PieceType.KING, R.drawable.wk, kingMoveSet));
        blackPlayer.pieces.add(new ChessPiece(4, 7, blackPlayer, PieceType.KING, R.drawable.bk, kingMoveSet));

        for (ChessPiece p : whitePlayer.pieces) {
            p.generateLegalSquares(new Square(p.col, p.row));
            p.generateDiscoveredSquares(new Square(p.col, p.row));
        }

        for (ChessPiece p : blackPlayer.pieces) {
            p.generateLegalSquares(new Square(p.col, p.row));
            p.generateDiscoveredSquares(new Square(p.col, p.row));
        }

        pgnCheck.add(pgnBoard());
    }

    public static ChessPiece pieceLoc(Square square) {
        return pieceLoc(square.getCol(), square.getRow());
    }

    // locate a piece using its position on the board
    static ChessPiece pieceLoc(int col, int row) {
        for (ChessPiece piece : whitePlayer.pieces) {
            if (col == piece.col && row == piece.row) {
                return piece;
            }
        }
        for (ChessPiece piece : blackPlayer.pieces) {
            if (col == piece.col && row == piece.row) {
                return piece;
            }
        }
        return null;
    }

    public static String pgnBoard() {
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

    public static String stringBoard() {
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

    public static String boardRow(int i) {
        StringBuilder desc = new StringBuilder();
            // j determines columns of model
        for (int j = 0; j < 8; j++) {
            ChessPiece piece = pieceLoc(j, i);
            if (piece == null) {
                desc.append(" .");
            } else {
                desc.append(" ");
                if (piece.type == PieceType.KING) {
                    if (piece.player == whitePlayer) desc.append("k");
                    else desc.append("K");
                }
                if (piece.type == PieceType.QUEEN) {
                    if (piece.player == whitePlayer) desc.append("q");
                    else desc.append("Q");
                }
                if (piece.type == PieceType.ROOK) {
                    if (piece.player == whitePlayer) desc.append("r");
                    else desc.append("R");
                }
                if (piece.type == PieceType.BISHOP) {
                    if (piece.player == whitePlayer) desc.append("b");
                    else desc.append("B");
                }
                if (piece.type == PieceType.KNIGHT) {
                    if (piece.player == whitePlayer) desc.append("n");
                    else desc.append("N");
                }
                if (piece.type == PieceType.PAWN) {
                    if (piece.player == whitePlayer) desc.append("p");
                    else desc.append("P");
                }
            }
        }
        return desc.toString();
    }
    public static void setCurrentPlayer(ChessPlayer cPlayer) {
        currentPlayer = cPlayer;
    }

    public static ArrayList<String> getPgn() {
        return pgnCheck;
    }
}
