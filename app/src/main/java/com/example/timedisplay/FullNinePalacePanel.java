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
    private String[][] palaceData;
    private float brightness = 1.0f;

    // 九宫格布局位置（按照指南针顺序：上北下南，左西右东）
    private static final int[][] PALACE_POSITIONS = {
        {0, 1}, // 坎一宫（北方）- 第一行第二列
        {2, 0}, // 坤二宫（西南）- 第三行第一列
        {1, 2}, // 震三宫（东方）- 第二行第三列
        {2, 2}, // 巽四宫（东南）- 第三行第三列
        {1, 1}, // 中五宫（中方）- 第二行第二列
        {0, 0}, // 乾六宫（西北）- 第一行第一列
        {1, 0}, // 兑七宫（西方）- 第二行第一列
        {0, 2}, // 艮八宫（东北）- 第一行第三列
        {2, 1}  // 离九宫（南方）- 第三行第二列
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
        textPaint.setColor(Color.argb((int)(brightness * 255), 74, 144, 217));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        centerPaint = new Paint();
        centerPaint.setColor(Color.argb((int)(brightness * 200), 44, 199, 194));
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setAntiAlias(true);

        palaceData = new String[9][2];
        for (int i = 0; i < 9; i++) {
            palaceData[i][0] = "";
            palaceData[i][1] = "";
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
        int cellSize = (int)(Math.min(width, height) / 3.0 * 1.05);

        for (int i = 0; i <= 3; i++) {
            canvas.drawLine(0, i * cellSize, width, i * cellSize, gridPaint);
            canvas.drawLine(i * cellSize, 0, i * cellSize, height, gridPaint);
        }

        RectF centerRect = new RectF(cellSize, cellSize, cellSize * 2, cellSize * 2);
        canvas.drawRect(centerRect, centerPaint);

        for (int i = 0; i < 9; i++) {
            int row = PALACE_POSITIONS[i][0];
            int col = PALACE_POSITIONS[i][1];
            float x = (col + 0.5f) * cellSize;
            float y = (row + 0.25f) * cellSize;

            String luck = "平";
            String[] dataParts = palaceData[i][1].split("\\n");
            if (dataParts.length > 1) {
                String thirdLine = dataParts[1];
                if (thirdLine.contains("吉")) {
                    luck = "吉";
                } else if (thirdLine.contains("凶")) {
                    luck = "凶";
                }
            }

            if (luck.equals("吉")) {
                textPaint.setColor(Color.argb((int)(brightness * 255), 144, 238, 144));
            } else if (luck.equals("凶")) {
                textPaint.setColor(Color.argb((int)(brightness * 255), 255, 140, 140));
            } else {
                textPaint.setColor(Color.argb((int)(brightness * 255), 135, 206, 235));
            }

            canvas.drawText(palaceData[i][0], x, y, textPaint);

            if (dataParts.length > 0) {
                y += 45;
                canvas.drawText(dataParts[0], x, y, textPaint);
            }

            if (dataParts.length > 1) {
                y += 45;
                canvas.drawText(dataParts[1], x, y, textPaint);
            }
        }
    }

    public void setPalaceData(String[][] data) {
        if (data != null && data.length == 9) {
            for (int i = 0; i < 9; i++) {
                if (data[i].length >= 2) {
                    palaceData[i][0] = data[i][0];
                    palaceData[i][1] = data[i][1];
                }
            }
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
