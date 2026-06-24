package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class FullNinePalacePanel extends View {
    private Paint gridPaint;
    private Paint textPaint;
    private Paint centerPaint;
    private Paint bgPaint;
    private Paint borderPaint;
    private String[][] palaceData;
    private String[] luckData;
    private float brightness = 1.0f;
    private float scale = 1f;
    
    private static final int COLOR_BG_CARD = 0xFF191C26;
    private static final int COLOR_BG_PRIMARY = 0xFF0F1219;
    private static final int COLOR_BORDER = 0xFF262A36;
    private static final int COLOR_GOLD = 0xFFBFA055;
    private static final int COLOR_GREEN = 0xFF7A9A60;
    private static final int COLOR_RED = 0xFFC47B5E;

    private static final int[][] PALACE_POSITIONS = {
        {0, 1}, {2, 0}, {1, 2}, {2, 2}, {1, 1}, {0, 0}, {1, 0}, {0, 2}, {2, 1}
    };

    public FullNinePalacePanel(Context context) {
        super(context);
        init();
    }

    public FullNinePalacePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FullNinePalacePanel(Context context, AttributeSet attrs, int defStyleAttr) {
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

        centerPaint = new Paint();
        centerPaint.setColor(Color.argb((int)(brightness * 200), 44, 199, 194));
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setAntiAlias(true);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(2);

        palaceData = new String[9][4];
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 4; j++) {
                palaceData[i][j] = "";
            }
        }
        
        luckData = new String[9];
        for (int i = 0; i < 9; i++) {
            luckData[i] = "平";
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
        float padding = 12f;
        int cellSize = (int)((Math.min(width, height) - padding * 2) / 3.0);
        float offsetX = (width - cellSize * 3) / 2;
        float offsetY = (height - cellSize * 3) / 2;
        float innerPadding = 8f;
        float radius = 10f;

        textPaint.setTextSize(cellSize * 0.15f);

        for (int i = 0; i < 9; i++) {
            int row = PALACE_POSITIONS[i][0];
            int col = PALACE_POSITIONS[i][1];
            
            float left = offsetX + col * cellSize + innerPadding;
            float top = offsetY + row * cellSize + innerPadding;
            float right = offsetX + (col + 1) * cellSize - innerPadding;
            float bottom = offsetY + (row + 1) * cellSize - innerPadding;

            String luck = luckData != null && luckData[i] != null ? luckData[i] : "平";

            bgPaint.setShader(new android.graphics.LinearGradient(left, top, right, bottom, 
                COLOR_BG_CARD, COLOR_BG_PRIMARY, android.graphics.Shader.TileMode.CLAMP));
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, bgPaint);

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

            textPaint.setTextSize(cellSize * 0.15f);
            canvas.drawText(palaceData[i][0], x, y, textPaint);

            y += cellSize * 0.22f;
            textPaint.setTextSize(cellSize * 0.12f);
            canvas.drawText(palaceData[i][1], x, y, textPaint);

            y += cellSize * 0.22f;
            textPaint.setTextSize(cellSize * 0.12f);
            canvas.drawText(palaceData[i][2], x, y, textPaint);

            y += cellSize * 0.18f;
            textPaint.setTextSize(cellSize * 0.10f);
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
            canvas.drawText(palaceData[i][3], x, y, textPaint);
        }
    }

    public void setPalaceData(String[][] data) {
        if (data != null && data.length == 9) {
            for (int i = 0; i < 9; i++) {
                if (data[i].length >= 4) {
                    for (int j = 0; j < 4; j++) {
                        palaceData[i][j] = data[i][j];
                    }
                }
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
    
    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, brightness));
        gridPaint.setColor(Color.argb((int)(brightness * 120), 160, 174, 192));
        textPaint.setColor(Color.argb((int)(brightness * 255), 74, 144, 217));
        centerPaint.setColor(Color.argb((int)(brightness * 200), 44, 199, 194));
        invalidate();
    }
}