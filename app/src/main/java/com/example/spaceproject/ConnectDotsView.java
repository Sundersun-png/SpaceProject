package com.example.spaceproject;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class ConnectDotsView extends View {

    public interface OnAllConnectedListener {
        void onAllConnected();
    }

    // ── Puzzle definition ─────────────────────────────────────────
    // 7×7 grid, 7 color pairs  (harder mode)
    //
    //   Col:  0    1    2    3    4    5    6
    //   Row 0: R    .    .    .    B    .    G
    //   Row 1: .    Y    .    .    .    O    .
    //   Row 2: .    .    .    M    .    .    .
    //   Row 3: .    .    P    .    P    .    .
    //   Row 4: .    .    .    M    .    .    .
    //   Row 5: .    O    .    .    .    Y    .
    //   Row 6: G    .    B    .    .    .    R
    //
    private static final int GRID = 7;

    private static final int[] COLOR_VAL = {
            0xFFFF3333,  // 0 Red
            0xFF3399FF,  // 1 Blue
            0xFF44DD44,  // 2 Green
            0xFFFFDD00,  // 3 Yellow
            0xFFFF8800,  // 4 Orange
            0xFFFF44DD,  // 5 Magenta
            0xFFAA44FF,  // 6 Purple
    };

    // Each row: { col1, row1, col2, row2 }
    private static final int[][] ENDPOINTS = {
            {0, 0, 6, 6},  // Red   — opposite corners
            {4, 0, 2, 6},  // Blue  — crosses board
            {6, 0, 0, 6},  // Green — opposite corners
            {1, 1, 5, 5},  // Yellow — inner diagonal
            {5, 1, 1, 5},  // Orange — inner diagonal (crosses Yellow)
            {3, 2, 3, 4},  // Magenta — vertical pair, crosses Purple
            {2, 3, 4, 3},  // Purple  — horizontal pair, crosses Magenta
    };

    private static final int NUM_COLORS = COLOR_VAL.length;

    // ── State ─────────────────────────────────────────────────────
    // cellOwner[col][row] = color index (-1 = empty)
    private final int[][] cellOwner = new int[GRID][GRID];
    private final List<List<int[]>> paths = new ArrayList<>();
    private final boolean[] complete = new boolean[NUM_COLORS];
    private int activeColor = -1;
    private int lastCol = -1, lastRow = -1;
    private boolean gameOver = false;

    // ── Drawing ───────────────────────────────────────────────────
    private float cellSize, offX, offY;
    private final Paint gridLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint bgPaint       = new Paint();
    private final Paint[] tubePaint   = new Paint[NUM_COLORS];
    private final Paint[] dotPaint    = new Paint[NUM_COLORS];

    private OnAllConnectedListener listener;

    // ── Constructors ──────────────────────────────────────────────
    public ConnectDotsView(Context ctx)                        { super(ctx);      init(); }
    public ConnectDotsView(Context ctx, AttributeSet a)        { super(ctx, a);   init(); }
    public ConnectDotsView(Context ctx, AttributeSet a, int s) { super(ctx, a,s); init(); }

    private void init() {
        bgPaint.setColor(0xFF000000);
        bgPaint.setStyle(Paint.Style.FILL);

        gridLinePaint.setColor(0xFF222222);
        gridLinePaint.setStyle(Paint.Style.STROKE);
        gridLinePaint.setStrokeWidth(2f);

        for (int c = 0; c < NUM_COLORS; c++) {
            tubePaint[c] = new Paint(Paint.ANTI_ALIAS_FLAG);
            tubePaint[c].setColor(COLOR_VAL[c]);
            tubePaint[c].setStyle(Paint.Style.STROKE);
            tubePaint[c].setStrokeCap(Paint.Cap.ROUND);
            tubePaint[c].setStrokeJoin(Paint.Join.ROUND);

            dotPaint[c] = new Paint(Paint.ANTI_ALIAS_FLAG);
            dotPaint[c].setColor(COLOR_VAL[c]);
            dotPaint[c].setStyle(Paint.Style.FILL);

            paths.add(new ArrayList<>());
        }

        resetGrid();
    }

    private void resetGrid() {
        for (int c = 0; c < GRID; c++)
            for (int r = 0; r < GRID; r++)
                cellOwner[c][r] = -1;

        for (int i = 0; i < NUM_COLORS; i++) {
            paths.get(i).clear();
            complete[i] = false;
        }
        activeColor = -1;
        lastCol = lastRow = -1;
    }

    // ── Layout ────────────────────────────────────────────────────
    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);
        float side = Math.min(w, h) * 0.92f;
        cellSize = side / GRID;
        offX = (w - side) / 2f;
        offY = (h - side) / 2f;
        for (Paint p : tubePaint) p.setStrokeWidth(cellSize * 0.48f);
    }

    // ── Drawing ───────────────────────────────────────────────────
    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();

        // Background
        canvas.drawRect(0, 0, w, h, bgPaint);

        // Grid lines
        float gridSide = cellSize * GRID;
        for (int i = 0; i <= GRID; i++) {
            float x = offX + i * cellSize;
            float y = offY + i * cellSize;
            canvas.drawLine(x, offY, x, offY + gridSide, gridLinePaint);
            canvas.drawLine(offX, y, offX + gridSide, y, gridLinePaint);
        }

        // Draw paths (tubes)
        for (int ci = 0; ci < NUM_COLORS; ci++) {
            List<int[]> path = paths.get(ci);
            if (path.size() < 2) continue;
            Path p = new Path();
            float[] start = cellCenter(path.get(0));
            p.moveTo(start[0], start[1]);
            for (int k = 1; k < path.size(); k++) {
                float[] pt = cellCenter(path.get(k));
                p.lineTo(pt[0], pt[1]);
            }
            canvas.drawPath(p, tubePaint[ci]);
        }

        // Draw endpoint dots (on top of paths)
        float dotR = cellSize * 0.38f;
        float innerR = cellSize * 0.18f;
        Paint innerPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        innerPaint.setStyle(Paint.Style.FILL);

        for (int ci = 0; ci < NUM_COLORS; ci++) {
            int[] ep = ENDPOINTS[ci];
            drawDot(canvas, ep[0], ep[1], dotR, innerR, dotPaint[ci], innerPaint);
            drawDot(canvas, ep[2], ep[3], dotR, innerR, dotPaint[ci], innerPaint);
        }
    }

    private void drawDot(Canvas canvas, int col, int row,
                         float dotR, float innerR,
                         Paint fill, Paint inner) {
        float[] c = cellCenter(col, row);
        canvas.drawCircle(c[0], c[1], dotR, fill);
        // Bright inner circle for "glow" effect
        inner.setColor(brighten(fill.getColor()));
        canvas.drawCircle(c[0], c[1], innerR, inner);
    }

    /** Lightens a color toward white. */
    private int brighten(int color) {
        int r = Math.min(255, ((color >> 16) & 0xFF) + 80);
        int g = Math.min(255, ((color >> 8)  & 0xFF) + 80);
        int b = Math.min(255, ( color        & 0xFF) + 80);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    // ── Touch handling ────────────────────────────────────────────
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (gameOver) return true;

        float tx = ev.getX(), ty = ev.getY();
        int col = (int) ((tx - offX) / cellSize);
        int row = (int) ((ty - offY) / cellSize);
        col = Math.max(0, Math.min(GRID - 1, col));
        row = Math.max(0, Math.min(GRID - 1, row));

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startAt(col, row);
                break;

            case MotionEvent.ACTION_MOVE:
                if (activeColor >= 0 && (col != lastCol || row != lastRow)) {
                    extendTo(col, row);
                }
                break;

            case MotionEvent.ACTION_UP:
                activeColor = -1;
                break;
        }

        lastCol = col;
        lastRow = row;
        return true;
    }

    private void startAt(int col, int row) {
        // Check if the touched cell is an endpoint
        for (int ci = 0; ci < NUM_COLORS; ci++) {
            int[] ep = ENDPOINTS[ci];
            if ((col == ep[0] && row == ep[1]) || (col == ep[2] && row == ep[3])) {
                // Clear this color's path and start fresh
                clearPath(ci);
                activeColor = ci;
                complete[ci] = false;
                paths.get(ci).add(new int[]{col, row});
                cellOwner[col][row] = ci;
                lastCol = col;
                lastRow = row;
                invalidate();
                return;
            }
        }
        activeColor = -1;
    }

    private void extendTo(int col, int row) {
        List<int[]> path = paths.get(activeColor);
        if (path.isEmpty()) return;

        int[] last = path.get(path.size() - 1);

        // Must be adjacent (not diagonal)
        if (!adjacent(last[0], last[1], col, row)) return;

        // Backtracking: if moving to second-to-last cell, pop the last
        if (path.size() >= 2) {
            int[] prev = path.get(path.size() - 2);
            if (prev[0] == col && prev[1] == row) {
                cellOwner[last[0]][last[1]] = -1;
                path.remove(path.size() - 1);
                complete[activeColor] = false;
                invalidate();
                return;
            }
        }

        // Cell must be empty OR be the other endpoint of this color
        int[] ep = ENDPOINTS[activeColor];
        boolean isTarget = (col == ep[0] && row == ep[1]) || (col == ep[2] && row == ep[3]);

        if (cellOwner[col][row] == -1 || isTarget) {
            path.add(new int[]{col, row});
            cellOwner[col][row] = activeColor;

            // If we reached the other endpoint and path has > 1 cell, complete
            if (isTarget && path.size() > 1 && !isStartPoint(activeColor, col, row, path)) {
                complete[activeColor] = true;
                activeColor = -1;
                invalidate();
                checkWin();
                return;
            }
            invalidate();
        } else if (cellOwner[col][row] == activeColor) {
            // Truncate path back to this cell (allows drawing over own path)
            while (path.size() > 1) {
                int[] tail = path.get(path.size() - 1);
                if (tail[0] == col && tail[1] == row) break;
                cellOwner[tail[0]][tail[1]] = -1;
                path.remove(path.size() - 1);
            }
            complete[activeColor] = false;
            invalidate();
        }
    }

    /** True if (col,row) is the START of the current active path (not the target end). */
    private boolean isStartPoint(int ci, int col, int row, List<int[]> path) {
        int[] start = path.get(0);
        return start[0] == col && start[1] == row;
    }

    private void clearPath(int ci) {
        for (int[] cell : paths.get(ci)) {
            // Don't wipe endpoint cells (they belong to the dots permanently)
            // actually we should wipe so paths are clean
            cellOwner[cell[0]][cell[1]] = -1;
        }
        paths.get(ci).clear();
        complete[ci] = false;
    }

    private void checkWin() {
        for (boolean b : complete) if (!b) return;
        gameOver = true;
        invalidate();
        if (listener != null) listener.onAllConnected();
    }

    // ── Helpers ───────────────────────────────────────────────────
    private float[] cellCenter(int[] cell) { return cellCenter(cell[0], cell[1]); }
    private float[] cellCenter(int col, int row) {
        return new float[]{
                offX + col * cellSize + cellSize / 2f,
                offY + row * cellSize + cellSize / 2f
        };
    }

    private boolean adjacent(int c1, int r1, int c2, int r2) {
        return Math.abs(c1 - c2) + Math.abs(r1 - r2) == 1;
    }

    public void stopGame()  { gameOver = true; }
    public void setOnAllConnectedListener(OnAllConnectedListener l) { this.listener = l; }
}
