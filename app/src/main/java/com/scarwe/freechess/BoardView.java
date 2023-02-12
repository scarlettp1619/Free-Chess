package com.scarwe.freechess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.HashMap;

public class BoardView extends View {

    public BoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    final int cellSize = 130;
    final int originY = 200;
    final int originX = 20;

    private static final int[] images = {R.drawable.bb, R.drawable.bk, R.drawable.bn, R.drawable.bp,
            R.drawable.bq, R.drawable.br, R.drawable.wb, R.drawable.wk, R.drawable.wn, R.drawable.wp,
            R.drawable.wq, R.drawable.wr};

    private final HashMap<Integer, Bitmap> bitmaps = new HashMap<>();
    private final Paint paint = new Paint();

    {
        loadImages();
    }

    protected void onDraw(Canvas canvas){
        drawBoard(canvas);

        paint.setAntiAlias(true);
        paint.setFilterBitmap(true);
        paint.setDither(true);

        drawPieces(canvas);
    }

    private void drawPieces(Canvas canvas) {
        BoardModel board = new BoardModel();
        board.resetBoard();

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                ChessPiece piece = board.pieceLoc(j, i);
                if (piece != null) {
                    drawPieceLoc(canvas, j, i, piece.resID);
                }
            }
        }
    }

    private void drawPieceLoc(Canvas canvas, int col, int row, int resID) {

        Bitmap bitmap = bitmaps.get(resID);
        canvas.drawBitmap(bitmap, null, new Rect(originX + col * cellSize, originY + (7 - row) * cellSize,
                originX + (col + 1) * cellSize, originY + ((7 - row) + 1) * cellSize), paint);
    }

    private void drawBoard(Canvas canvas) {
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                if ((i + j) % 2 == 0) paint.setColor(Color.parseColor("#F0D9B5"));
                else paint.setColor(Color.parseColor("#B58863"));
                canvas.drawRect(originX + i * cellSize, originY + j * cellSize, originX + (i + 1) * cellSize, originY + (j + 1) * cellSize, paint);
            }
        }
    }

    private void loadImages() {
        for (int i : images) {
            bitmaps.put(i, BitmapFactory.decodeResource(getResources(), i));
        }
    }
}
