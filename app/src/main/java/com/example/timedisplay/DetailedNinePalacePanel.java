package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class DetailedNinePalacePanel extends View {
    private Paint gridPaint;
    private Paint textPaint;
    private Paint bgPaint;
    private Paint borderPaint;
    private String[][] palaceData;
    private String[] luckData;
    private String[] palaceTips;
    private float brightness = 1.0f;
    
    private static final int COLOR_BG_CARD = 0xFF191C26;
    private static final int COLOR_BG_PRIMARY = 0xFF0F1219;
    private static final int COLOR_BORDER = 0xFF262A36;
    private static final int COLOR_GOLD = 0xFFE6C46A;
    private static final int COLOR_GREEN = 0xFF3FA34D;
    private static final int COLOR_RED = 0xFFE0593B;

    private static final int[][] PALACE_POSITIONS = {
        {0, 1}, {2, 0}, {1, 2}, {2, 2}, {1, 1}, {0, 0}, {1, 0}, {0, 2}, {2, 1}
    };

    public DetailedNinePalacePanel(Context context) {
        super(context);
        init();
    }

    public DetailedNinePalacePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DetailedNinePalacePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gridPaint = new Paint();
        gridPaint.setColor(Color.argb((int)(brightness * 120), 160, 174, 192));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2);
        gridPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(2);

        palaceData = new String[9][3];
        luckData = new String[9];
        palaceTips = new String[9];
        for (int i = 0; i < 9; i++) {
            palaceData[i][0] = "";
            palaceData[i][1] = "";
            palaceData[i][2] = "";
            luckData[i] = "平";
            palaceTips[i] = "";
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = Math.min(width, height);
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();
        float padding = 10f;
        int cellSize = (int)((Math.min(width, height) - padding * 2) / 3.0);
        float offsetX = (width - cellSize * 3) / 2;
        float offsetY = (height - cellSize * 3) / 2;
        float innerPadding = 6f;
        float radius = 8f;

        for (int i = 0; i < 9; i++) {
            int row = PALACE_POSITIONS[i][0];
            int col = PALACE_POSITIONS[i][1];
            
            float left = offsetX + col * cellSize + innerPadding;
            float top = offsetY + row * cellSize + innerPadding;
            float right = offsetX + (col + 1) * cellSize - innerPadding;
            float bottom = offsetY + (row + 1) * cellSize - innerPadding;

            bgPaint.setShader(new LinearGradient(left, top, right, bottom, 
                COLOR_BG_CARD, COLOR_BG_PRIMARY, Shader.TileMode.CLAMP));
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, bgPaint);

            String luck = luckData[i] != null ? luckData[i] : "平";
            if (i == 4) {
                borderPaint.setColor(COLOR_GOLD);
            } else if (luck.equals("大吉")) {
                borderPaint.setColor(Color.argb((int)(brightness * 220), 52, 168, 83));
            } else if (luck.equals("吉")) {
                borderPaint.setColor(Color.argb((int)(brightness * 200), 74, 175, 94));
            } else if (luck.equals("平吉")) {
                borderPaint.setColor(Color.argb((int)(brightness * 180), 126, 186, 139));
            } else if (luck.equals("大凶")) {
                borderPaint.setColor(Color.argb((int)(brightness * 220), 220, 38, 38));
            } else if (luck.equals("凶")) {
                borderPaint.setColor(Color.argb((int)(brightness * 200), 239, 68, 68));
            } else if (luck.equals("平凶")) {
                borderPaint.setColor(Color.argb((int)(brightness * 180), 239, 108, 108));
            } else {
                borderPaint.setColor(COLOR_BORDER);
            }
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, borderPaint);

            float x = offsetX + (col + 0.5f) * cellSize;
            float y = offsetY + (row + 0.22f) * cellSize;

            if (luck.equals("大吉")) {
                textPaint.setColor(Color.argb((int)(brightness * 240), 34, 197, 94));
            } else if (luck.equals("吉")) {
                textPaint.setColor(Color.argb((int)(brightness * 220), 52, 211, 153));
            } else if (luck.equals("平吉")) {
                textPaint.setColor(Color.argb((int)(brightness * 200), 147, 197, 114));
            } else if (luck.equals("大凶")) {
                textPaint.setColor(Color.argb((int)(brightness * 240), 239, 68, 68));
            } else if (luck.equals("凶")) {
                textPaint.setColor(Color.argb((int)(brightness * 220), 248, 113, 113));
            } else if (luck.equals("平凶")) {
                textPaint.setColor(Color.argb((int)(brightness * 200), 251, 146, 60));
            } else {
                textPaint.setColor(Color.argb((int)(brightness * 220), 107, 114, 128));
            }

            textPaint.setTextSize(cellSize * 0.14f);
            canvas.drawText(palaceData[i][0], x, y, textPaint);

            y += cellSize * 0.22f;
            textPaint.setTextSize(cellSize * 0.12f);
            textPaint.setColor(Color.argb((int)(brightness * 200), 191, 160, 85));
            canvas.drawText(palaceData[i][1], x, y, textPaint);

            if (!palaceData[i][2].isEmpty()) {
                y += cellSize * 0.22f;
                textPaint.setTextSize(cellSize * 0.11f);
                textPaint.setColor(Color.argb((int)(brightness * 180), 216, 212, 200));
                canvas.drawText(palaceData[i][2], x, y, textPaint);
            }

            if (palaceTips[i] != null && !palaceTips[i].isEmpty()) {
                y += cellSize * 0.20f;
                textPaint.setTextSize(cellSize * 0.09f);
                textPaint.setColor(Color.argb((int)(brightness * 160), 255, 215, 0));
                canvas.drawText(palaceTips[i], x, y, textPaint);
            }
        }
    }

    public void setPalaceData(String[][] data) {
        if (data != null && data.length == 9) {
            for (int i = 0; i < 9; i++) {
                if (data[i].length >= 1) palaceData[i][0] = data[i][0];
                if (data[i].length >= 2) palaceData[i][1] = data[i][1];
                if (data[i].length >= 3) palaceData[i][2] = data[i][2];
            }
            invalidate();
        }
    }

    public void setLuckData(String[] luck) {
        if (luck != null && luck.length == 9) {
            System.arraycopy(luck, 0, luckData, 0, 9);
            invalidate();
        }
    }

    public void setPalaceTips(String[] tips) {
        if (tips != null && tips.length == 9) {
            System.arraycopy(tips, 0, palaceTips, 0, 9);
            invalidate();
        }
    }

    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, brightness));
        gridPaint.setColor(Color.argb((int)(brightness * 120), 160, 174, 192));
        invalidate();
    }
}
