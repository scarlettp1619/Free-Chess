package com.scarwe.freechess;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class BoardView extends View {

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    private float cellSize = 130f;
    private float originY = 200f;
    private float originX = 20f;
    private final int lightColor = Color.parseColor("#F0D9B5");
    private final int darkColor = Color.parseColor("#B58863");

    private static final int[] images = {R.drawable.bb, R.drawable.bk, R.drawable.bn, R.drawable.bp,
            R.drawable.bq, R.drawable.br, R.drawable.wb, R.drawable.wk, R.drawable.wn, R.drawable.wp,
            R.drawable.wq, R.drawable.wr};

    private final HashMap<Integer, Bitmap> bitmaps = new HashMap<>();
    private final Paint paint = new Paint();

    private Bitmap movingPieceBitmap;
    private ChessPiece movingPiece;
    private int fromCol = -1;
    private int fromRow = -1;
    private float movingPieceX = -1f;
    private float movingPieceY = -1f;

    public ChessDelegate chessDelegate = null;

    {
        loadImages();
    }

    @Override
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
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            fromCol = (int) Math.floor((e.getX() - originX) / cellSize);
            fromRow = 7 - ((int) Math.floor((e.getY() - originY) / cellSize));
            Log.d(MainActivity.tag, "down at " + fromCol + ", " + fromRow);

            try {
                movingPiece = chessDelegate.pieceLoc(fromCol, fromRow);
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
            int col = (int) Math.floor((e.getX() - originX) / cellSize);
            int row = 7 - ((int) Math.floor((e.getY() - originY) / cellSize));
            try {
                chessDelegate.movePiece(fromCol, fromRow, col, row);
                movingPieceBitmap = null;
                fromCol = -1;
                fromRow = -1;

            } catch(Exception ex) {
                // do nothing
            }
        }
        return true;
    }

    private void scaleSize() {
        float scaleF = 1f;
        float boardSize = Math.min(getWidth(), getHeight()) * scaleF;
        cellSize = boardSize / 8f;
        originX = (getWidth() - boardSize) / 2f;
        originY = (getHeight() - boardSize) / 2f;
    }

    private void drawPieces(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if (i != fromRow || j != fromCol) {
                    ChessPiece piece = chessDelegate.pieceLoc(j, i);
                    if (piece != null) {
                        drawPieceLoc(canvas, j, i, piece.resID);
                    }
                }
            }
        }
        try {
            canvas.drawBitmap(movingPieceBitmap, null, new RectF(movingPieceX - cellSize / 2, movingPieceY - cellSize / 2,
                    movingPieceX + cellSize / 2, movingPieceY + cellSize / 2), paint);
        } catch (Exception ex) {
            //
        }

        ChessPiece piece = chessDelegate.pieceLoc(fromCol, fromRow);
        if (piece != null) {
            Bitmap bitmap = bitmaps.get(piece.resID);
            canvas.drawBitmap(bitmap, null, new RectF(movingPieceX - cellSize / 2, movingPieceY - cellSize / 2,
                    movingPieceX + cellSize / 2, movingPieceY + cellSize / 2), paint);
        }
    }

    private void drawPieceLoc(Canvas canvas, int col, int row, int resID) {
        Bitmap bitmap = bitmaps.get(resID);
        canvas.drawBitmap(bitmap, null, new RectF(originX + col * cellSize, originY + (7 - row) * cellSize,
                originX + (col + 1) * cellSize, originY + ((7 - row) + 1) * cellSize), paint);
    }

    private void drawBoard(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) paint.setColor(lightColor);
                else paint.setColor(darkColor);
                canvas.drawRect(originX + i * cellSize, originY + j * cellSize,
                        originX + (i + 1) * cellSize, originY + (j + 1) * cellSize, paint);
            }
        }
        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);
    }

    private void loadImages() {
        for (int i : images) {
            bitmaps.put(i, BitmapFactory.decodeResource(getResources(), i));
        }
    }
}
