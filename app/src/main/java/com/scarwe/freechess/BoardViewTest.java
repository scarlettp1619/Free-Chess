package com.scarwe.freechess;

import android.annotation.SuppressLint;
import android.graphics.Canvas;
import android.view.MotionEvent;

public interface BoardViewTest {
    @SuppressLint("ClickableViewAccessibility")
        // when player touches screen
    boolean onTouchEvent(MotionEvent e, Canvas c);
}
