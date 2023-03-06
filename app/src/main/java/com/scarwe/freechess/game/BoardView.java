package com.scarwe.freechess.game;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import com.scarwe.freechess.R;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class BoardView extends View{

    // class to paint pieces and board
    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private float cellSize = 130f;
    private float originY = 200f;
    private float originX = 20f;
    // determines light square & dark square colours

    private final int lightColor = Color.parseColor("#F0D9B5");
    private final int darkColor = Color.parseColor("#B58863");

    // loads all piece images (will soon be replaced with config)
    private static final int[] images = {R.drawable.bb, R.drawable.bk, R.drawable.bn, R.drawable.bp,
            R.drawable.bq, R.drawable.br, R.drawable.wb, R.drawable.wk, R.drawable.wn, R.drawable.wp,
            R.drawable.wq, R.drawable.wr, R.drawable.cwk, R.drawable.cbk};

    private final HashMap<Integer, Bitmap> bitmaps = new HashMap<>();
    private final Paint paint = new Paint();
    private final TextPaint textPaint = new TextPaint();

    // setting up stuff for drawing
    private Bitmap movingPieceBitmap;
    private int fromCol = -1;
    private int fromRow = -1;
    private int col = -1;
    private int row = -1;
    private int tempCol = -1;
    private int tempRow = -1;
    private boolean moved = false;
    private float movingPieceX = -1f;
    private float movingPieceY = -1f;

    public ChessDelegate chessDelegate = null;

    // init
    {
        loadImages();
    }

    @Override
    // used to scale the screen
    protected void onMeasure(int widthMeasure, int heightMeasure) {
        super.onMeasure(widthMeasure, heightMeasure);
        int small = Math.min(widthMeasure, heightMeasure);
        setMeasuredDimension(small, small);
    }

    @Override
    protected void onDraw(Canvas canvas){
        scaleSize();
        drawBoard(canvas);
        drawPieces(canvas);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    // when player touches screen
    public boolean onTouchEvent(MotionEvent e) {
        // holding finger down
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            moved = false;
            // locate position, math.floor required as rectangles are floats
            fromCol = (int) Math.floor((e.getX() - originX) / cellSize);
            fromRow = 7 - ((int) Math.floor((e.getY() - originY) / cellSize));
            try {
                ChessPiece movingPiece = chessDelegate.pieceLoc(new Square(fromCol, fromRow));
                movingPieceBitmap = bitmaps.get(movingPiece.resID);
            } catch (Exception ex) {
                // do nothing
            }
        }
        if (e.getAction() == MotionEvent.ACTION_MOVE) {
            movingPieceX = e.getX();
            movingPieceY = e.getY();
            invalidate();
        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            // locate position
            col = (int) Math.floor((e.getX() - originX) / cellSize);
            row = 7 - ((int) Math.floor((e.getY() - originY) / cellSize));
            // move the piece to the new position
            try {
                chessDelegate.movePiece(new Square(fromCol, fromRow), new Square(col, row));
                moved = BoardGame.pieceLoc(fromCol, fromRow) != BoardGame.pieceLoc(col, row);
            } catch (CloneNotSupportedException | IOException ex) {
                throw new RuntimeException(ex);
            }
            // deletes moving piece image (drag and move)
            movingPieceBitmap = null;
            tempCol = fromCol;
            tempRow = fromRow;
            fromCol = -1;
            fromRow = -1;
        }
        return true;
    }

    // scales the board properly
    private void scaleSize() {
        float scaleF = 1f;
        float boardSize = Math.min(getWidth(), getHeight()) * scaleF;
        cellSize = boardSize / 8f;
        originX = (getWidth() - boardSize) / 2f;
        originY = (getHeight() - boardSize) / 2f;
    }

    private void drawPieces(Canvas canvas){
        ChessPiece piece, movePiece = null;

        piece = chessDelegate.pieceLoc(new Square(fromCol, fromRow));
        if (col != -1 && row != -1) {
            movePiece = chessDelegate.pieceLoc(new Square(col, row));
        }

        // if the piece selected isn't an empty square
        if (piece != null) {
            String legalLightColor = "#C7AFF3";
            String legalDarkColor = "#A874E5";
            drawLegalMoves(canvas, fromRow, fromCol, legalLightColor, legalDarkColor);
        }

        // if piece moving isn't null
        if (movePiece != null && moved) {
            // initialise colours
            String moveLightColor = "#BFC6F5";
            String moveDarkColor = "#8095EC";
            String placeLightColor = "#A3A3F0";
            String placeDarkColor = "#717ADA";
            drawLegalMoves(canvas, row, col, placeLightColor, placeDarkColor);
            drawLegalMoves(canvas, tempRow, tempCol, moveLightColor, moveDarkColor);
        }


        if (piece != null) {
            for (Square s : piece.protectedSquares) {
                    String legalLightColor = "#F0B095";
                    String legalDarkColor = "#C47358";
                    drawLegalMoves(canvas, s.getRow(), s.getCol(), legalLightColor, legalDarkColor);
            }
        }

        drawNotation(canvas);

        if (piece != null) {
            // get bitmap of piece and draw
            Bitmap bitmap = bitmaps.get(piece.resID);
            canvas.drawBitmap(bitmap, null, new RectF(movingPieceX - cellSize / 2, movingPieceY - cellSize / 2f,
                    movingPieceX + cellSize / 2f, movingPieceY + cellSize / 2f), paint);
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i != fromRow || j != fromCol) {
                    piece = chessDelegate.pieceLoc(new Square(j, i));
                    if (piece != null) {
                        drawPieceLoc(canvas, j, i, piece.resID);
                    }
                }
            }
        }

        try {
            // draws piece while moving
            canvas.drawBitmap(movingPieceBitmap, null, new RectF(movingPieceX - cellSize / 2, movingPieceY - cellSize / 2,
                    movingPieceX + cellSize / 2f, movingPieceY + cellSize / 2f), paint);
        } catch (Exception ex) {
            // do nothing
        }
    }

    private void drawPieceLoc(Canvas canvas, int col, int row, int resID){
        Bitmap bitmap = bitmaps.get(resID);
        try {
            canvas.drawBitmap(bitmap, null, new RectF(originX + col * cellSize, originY + (7 - row) * cellSize,
                    originX + (col + 1) * cellSize, originY + ((7 - row) + 1) * cellSize), paint);
        } catch (Exception ex) {
            // do nothing
        }
    }

    private void drawBoard(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) paint.setColor(lightColor);
                else paint.setColor(darkColor);
                // draws board using rectangles
                canvas.drawRect(originX + i * cellSize, originY + j * cellSize,
                        originX + (i + 1) * cellSize, originY + (j + 1) * cellSize, paint);
            }
        }
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
    }

    private void drawNotation(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            // makes sure colours are correct
            if (i % 2 == 0) {
                textPaint.setColor(Color.parseColor("#F9E2BD")); // dark square
            } else {
                textPaint.setColor(Color.parseColor("#9A7455"));
            }
            // setting up text painter
            textPaint.setFakeBoldText(true);
            textPaint.setTextSize(cellSize / 5);
            canvas.drawText(intToString(i + 1).toLowerCase(Locale.ROOT), i * cellSize +
                    (cellSize / 16), (float) (cellSize + cellSize * 6.93), textPaint);
        }

        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                textPaint.setColor(Color.parseColor("#F9E2BD")); // dark square
            } else {
                textPaint.setColor(Color.parseColor("#9A7455"));
            }
            textPaint.setFakeBoldText(true);
            textPaint.setTextSize(cellSize / 5);
            canvas.drawText(Integer.toString(8 - (i)), (float) (cellSize + cellSize * 6.817), (float) (i * cellSize + cellSize / 4.3), textPaint);
        }
    }

    private void drawLegalMoves(Canvas canvas, int j, int i, String lightColor, String darkColor) {
        if ((i + j) % 2 == 0) paint.setColor(Color.parseColor(darkColor));
        else paint.setColor(Color.parseColor(lightColor));
        canvas.drawRect(originX + i * cellSize, originY + (7 - j) * cellSize,
                originX + (i + 1) * cellSize, originY + ((7 - j) + 1) * cellSize, paint);
    }

    private void loadImages() {
        for (int i : images) {
            bitmaps.put(i, BitmapFactory.decodeResource(getResources(), i));
        }
    }

    public String intToString(int i)  {
        return i > 0 && i < 27 ? String.valueOf((char)(i+64)) : null;
    }
}