package com.example.spaceproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A "Connect the Dots" view where dots must be tapped in order: 1, 2, 3...
 * This ensures the puzzle is always solvable and randomly generated.
 */
public class ConnectDotsView extends View {

    public interface OnAllConnectedListener {
        void onAllConnected();
    }

    private static class Dot {
        float x, y;
        int number;
        boolean connected = false;

        Dot(float x, float y, int number) {
            this.x = x;
            this.y = y;
            this.number = number;
        }
    }

    private final List<Dot> dots = new ArrayList<>();
    private int nextToTap = 1;
    private boolean gameOver = false;
    private OnAllConnectedListener listener;

    private final Paint dotPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public ConnectDotsView(Context ctx) { super(ctx); init(); }
    public ConnectDotsView(Context ctx, AttributeSet a) { super(ctx, a); init(); }

    private void init() {
        dotPaint.setColor(Color.WHITE);
        dotPaint.setStyle(Paint.Style.FILL);

        linePaint.setColor(Color.CYAN);
        linePaint.setStrokeWidth(8f);
        linePaint.setStyle(Paint.Style.STROKE);

        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(40f);
        textPaint.setTextAlign(Paint.Align.CENTER);
    }

    public void generateRandomPuzzle() {
        dots.clear();
        nextToTap = 1;
        gameOver = false;
        
        int w = getWidth();
        int h = getHeight();
        if (w == 0 || h == 0) return;

        Random r = new Random();
        int numDots = 8; // A reasonable number of dots
        for (int i = 1; i <= numDots; i++) {
            float x = 100 + r.nextInt(w - 200);
            float y = 100 + r.nextInt(h - 200);
            dots.add(new Dot(x, y, i));
        }
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        generateRandomPuzzle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Draw lines between connected dots
        for (int i = 0; i < dots.size() - 1; i++) {
            Dot d1 = dots.get(i);
            Dot d2 = dots.get(i + 1);
            if (d1.connected && d2.connected) {
                canvas.drawLine(d1.x, d1.y, d2.x, d2.y, linePaint);
            }
        }

        // Draw dots
        for (Dot d : dots) {
            dotPaint.setColor(d.connected ? Color.GREEN : Color.WHITE);
            canvas.drawCircle(d.x, d.y, 40, dotPaint);
            canvas.drawText(String.valueOf(d.number), d.x, d.y + 15, textPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gameOver || event.getAction() != MotionEvent.ACTION_DOWN) return true;

        float tx = event.getX();
        float ty = event.getY();

        for (Dot d : dots) {
            if (d.number == nextToTap) {
                double dist = Math.sqrt(Math.pow(tx - d.x, 2) + Math.pow(ty - d.y, 2));
                if (dist < 60) {
                    d.connected = true;
                    nextToTap++;
                    invalidate();
                    if (nextToTap > dots.size()) {
                        gameOver = true;
                        if (listener != null) listener.onAllConnected();
                    }
                    break;
                }
            }
        }
        return true;
    }

    public void setOnAllConnectedListener(OnAllConnectedListener l) { this.listener = l; }
    public void stopGame() { gameOver = true; }
}
