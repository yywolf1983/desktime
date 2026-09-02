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
    // 数据行的默认文字色（随亮度变化），与首页九宫同一口径
    private int defaultTextColor = 0xFF4A90D9;
    
    private static final int COLOR_BG_CARD = 0xFF191C26;
    private static final int COLOR_BG_PRIMARY = 0xFF0F1219;
    private static final int COLOR_BORDER = 0xFF262A36;
    private static final int COLOR_GOLD = 0xFFE6C46A;
    private static final int COLOR_GREEN = 0xFF3FA34D;
    private static final int COLOR_RED = 0xFFE0593B;
    private Paint tintPaint;

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

        tintPaint = new Paint();
        tintPaint.setAntiAlias(true);

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

        // 与首页九宫一致：整块九宫使用一次对角渐变（而非每格重复），保证整体明暗统一
        bgPaint.setShader(new android.graphics.LinearGradient(0, 0, width, height,
            COLOR_BG_CARD, COLOR_BG_PRIMARY, android.graphics.Shader.TileMode.CLAMP));

        for (int i = 0; i < 9; i++) {
            int row = PALACE_POSITIONS[i][0];
            int col = PALACE_POSITIONS[i][1];
            
            float left = offsetX + col * cellSize + innerPadding;
            float top = offsetY + row * cellSize + innerPadding;
            float right = offsetX + (col + 1) * cellSize - innerPadding;
            float bottom = offsetY + (row + 1) * cellSize - innerPadding;

            String luck = luckData != null && luckData[i] != null ? luckData[i] : "平";

            canvas.drawRoundRect(left, top, right, bottom, radius, radius, bgPaint);

            // 与首页九宫一致：按吉凶等级铺一层约 10% 透明的底色，区分大吉/凶/平凶等
            int luckColor = NinePalacePanel.getLuckColorByLabel(luck);
            tintPaint.setColor((luckColor & 0x00FFFFFF) | 0x1A000000);
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, tintPaint);

            if (i == 4) {
                borderPaint.setColor(COLOR_GOLD);
                borderPaint.setStrokeWidth(4f);
            } else {
                // 吉凶配色统一取自首页九宫的公共色源，避免两处吉凶颜色不一致
                borderPaint.setColor(NinePalacePanel.getLuckColorByLabel(luck));
                borderPaint.setStrokeWidth(3f);
            }
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, borderPaint);

            float x = offsetX + (col + 0.5f) * cellSize;
            float y = offsetY + (row + 0.22f) * cellSize;

            // 宫名：与首页九宫一致（中宫金色，其余浅灰）
            textPaint.setColor(i == 4 ? COLOR_GOLD : 0xFFE6E6E6);
            textPaint.setTextSize(cellSize * 0.15f);
            canvas.drawText(palaceData[i][0], x, y, textPaint);

            // 数据行：与首页九宫一致，用默认文字色，不随吉凶染色
            y += cellSize * 0.22f;
            textPaint.setTextSize(cellSize * 0.12f);
            textPaint.setColor(defaultTextColor);
            canvas.drawText(palaceData[i][1], x, y, textPaint);

            y += cellSize * 0.22f;
            textPaint.setTextSize(cellSize * 0.12f);
            canvas.drawText(palaceData[i][2], x, y, textPaint);

            y += cellSize * 0.18f;
            textPaint.setTextSize(cellSize * 0.10f);
            // 吉凶行：与首页九宫同源
            textPaint.setColor(NinePalacePanel.getLuckColorByLabel(luck));
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
        defaultTextColor = Color.argb((int)(brightness * 255), 74, 144, 217);
        invalidate();
    }
}