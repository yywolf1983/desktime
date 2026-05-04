package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class CircularNinePalacePanel extends View {
    private Paint circlePaint;
    private Paint textPaint;
    private Paint centerPaint;
    private Paint linePaint;
    private Paint sectorPaint;
    private String[][] palaceData;
    private float brightness = 1.0f;
    
    // 九宫格位置（按圆形排列：坎、艮、震、巽、离、坤、兑、乾 + 中宫）
    private static final int[] CIRCULAR_ORDER = {0, 7, 2, 3, 8, 1, 6, 5, 4};
    
    // 九宫格名称
    private static final String[] PALACE_NAMES = {
        "坎", "坤", "震", "巽", "中", "乾", "兑", "艮", "离"
    };
    
    // 方位名称
    private static final String[] DIRECTIONS = {
        "北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"
    };
    
    // 八卦符号
    private static final String[] GUA_SYMBOLS = {
        "☵", "☷", "☳", "☴", "", "☰", "☱", "☶", "☲"
    };

    public CircularNinePalacePanel(Context context) {
        super(context);
        init();
    }

    public CircularNinePalacePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircularNinePalacePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化圆形画笔
        circlePaint = new Paint();
        circlePaint.setColor(Color.argb((int)(brightness * 120), 160, 174, 192));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);
        circlePaint.setAntiAlias(true);
        
        // 初始化扇形画笔
        sectorPaint = new Paint();
        sectorPaint.setStyle(Paint.Style.FILL);
        sectorPaint.setAntiAlias(true);
        
        // 初始化线条画笔
        linePaint = new Paint();
        linePaint.setColor(Color.argb((int)(brightness * 150), 100, 149, 237));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1);
        linePaint.setAntiAlias(true);
        
        // 初始化文字画笔
        textPaint = new Paint();
        textPaint.setColor(Color.argb((int)(brightness * 255), 135, 206, 235));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(16);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);
        
        // 初始化中心画笔
        centerPaint = new Paint();
        centerPaint.setColor(Color.argb((int)(brightness * 200), 44, 199, 194));
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setAntiAlias(true);
        
        // 初始化九宫格数据
        palaceData = new String[9][2];
        for (int i = 0; i < 9; i++) {
            palaceData[i][0] = PALACE_NAMES[i];
            palaceData[i][1] = "--";
        }
        
        // 设置为可点击
        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 确保是正方形
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
        int centerX = width / 2;
        int centerY = height / 2;
        float radius = Math.min(width, height) / 2 - 10;
        
        // 绘制多层同心圆
        drawConcentricCircles(canvas, centerX, centerY, radius);
        
        // 绘制分割线
        drawDividingLines(canvas, centerX, centerY, radius);
        
        // 绘制八个扇形宫位
        drawEightSectors(canvas, centerX, centerY, radius);
        
        // 绘制中心宫位
        drawCenterPalace(canvas, centerX, centerY, radius);
    }
    
    private void drawConcentricCircles(Canvas canvas, int centerX, int centerY, float radius) {
        // 绘制外圆
        canvas.drawCircle(centerX, centerY, radius, circlePaint);
        
        // 绘制中间圆
        canvas.drawCircle(centerX, centerY, radius * 0.68f, circlePaint);
        
        // 绘制内圆
        canvas.drawCircle(centerX, centerY, radius * 0.35f, circlePaint);
    }
    
    private void drawDividingLines(Canvas canvas, int centerX, int centerY, float radius) {
        // 绘制8条分割线
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 - 90);
            float startX = centerX + (float)(radius * 0.35f * Math.cos(angle));
            float startY = centerY + (float)(radius * 0.35f * Math.sin(angle));
            float endX = centerX + (float)(radius * Math.cos(angle));
            float endY = centerY + (float)(radius * Math.sin(angle));
            canvas.drawLine(startX, startY, endX, endY, linePaint);
        }
    }
    
    private void drawEightSectors(Canvas canvas, int centerX, int centerY, float radius) {
        // 绘制8个扇形宫位（按圆形排列：坎、艮、震、巽、离、坤、兑、乾）
        for (int i = 0; i < 8; i++) {
            int palaceIndex = CIRCULAR_ORDER[i];
            float startAngle = i * 45 - 112.5f;
            
            // 绘制扇形背景
            drawSectorBackground(canvas, centerX, centerY, radius, startAngle, palaceIndex);
            
            // 绘制扇形内容
            drawSectorContent(canvas, centerX, centerY, radius, startAngle, palaceIndex);
        }
    }
    
    private void drawSectorBackground(Canvas canvas, int centerX, int centerY, float radius, float startAngle, int palaceIndex) {
        String[] dataParts = palaceData[palaceIndex][1].split("\n");
        String luck = "平";
        if (dataParts.length > 1) {
            String thirdLine = dataParts[1];
            if (thirdLine.contains("吉")) {
                luck = "吉";
            } else if (thirdLine.contains("凶")) {
                luck = "凶";
            }
        }
        
        // 设置扇形背景颜色
        if (luck.equals("吉")) {
            sectorPaint.setColor(Color.argb((int)(brightness * 50), 144, 238, 144));
        } else if (luck.equals("凶")) {
            sectorPaint.setColor(Color.argb((int)(brightness * 50), 255, 140, 140));
        } else {
            sectorPaint.setColor(Color.argb((int)(brightness * 30), 135, 206, 235));
        }
        
        // 绘制扇形
        RectF rectF = new RectF(
            centerX - radius,
            centerY - radius,
            centerX + radius,
            centerY + radius
        );
        canvas.drawArc(rectF, startAngle, 45, true, sectorPaint);
    }
    
    private void drawSectorContent(Canvas canvas, int centerX, int centerY, float radius, float startAngle, int palaceIndex) {
        double midAngle = Math.toRadians(startAngle + 22.5);
        float midRadius = radius * 0.55f;
        
        float x = centerX + (float)(midRadius * Math.cos(midAngle));
        float y = centerY + (float)(midRadius * Math.sin(midAngle));
        
        String[] dataParts = palaceData[palaceIndex][1].split("\n");
        String luck = "平";
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
        
        // 绘制宫位名称
        textPaint.setTextSize(18);
        canvas.drawText(palaceData[palaceIndex][0], x, y - 15, textPaint);
        
        // 绘制八卦符号
        if (palaceIndex < GUA_SYMBOLS.length && !GUA_SYMBOLS[palaceIndex].isEmpty()) {
            textPaint.setTextSize(22);
            canvas.drawText(GUA_SYMBOLS[palaceIndex], x, y + 8, textPaint);
        }
        
        // 绘制星门信息（第一行）
        if (dataParts.length > 0) {
            textPaint.setTextSize(12);
            canvas.drawText(dataParts[0], x, y + 25, textPaint);
        }
        
        // 绘制第二行信息（天干等）
        if (dataParts.length > 1) {
            textPaint.setTextSize(10);
            canvas.drawText(dataParts[1], x, y + 38, textPaint);
        }
        
        // 在外圈绘制方位名称
        float outerRadius = radius * 0.85f;
        float outerX = centerX + (float)(outerRadius * Math.cos(midAngle));
        float outerY = centerY + (float)(outerRadius * Math.sin(midAngle));
        textPaint.setTextSize(11);
        textPaint.setColor(Color.argb((int)(brightness * 255), 255, 215, 0));
        if (palaceIndex < DIRECTIONS.length) {
            canvas.drawText(DIRECTIONS[palaceIndex], outerX, outerY + 4, textPaint);
        }
    }
    
    private void drawCenterPalace(Canvas canvas, int centerX, int centerY, float radius) {
        // 绘制中心宫位背景
        canvas.drawCircle(centerX, centerY, radius * 0.3f, centerPaint);
        
        // 绘制中心宫位内容
        int palaceIndex = 4; // 中宫
        String[] dataParts = palaceData[palaceIndex][1].split("\n");
        
        String luck = "平";
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
            textPaint.setColor(Color.argb((int)(brightness * 255), 255, 255, 255));
        }
        
        // 绘制宫位名称
        textPaint.setTextSize(20);
        canvas.drawText(palaceData[palaceIndex][0], centerX, centerY - 10, textPaint);
        
        // 绘制星门信息
        if (dataParts.length > 0) {
            textPaint.setTextSize(13);
            canvas.drawText(dataParts[0], centerX, centerY + 8, textPaint);
        }
        
        if (dataParts.length > 1) {
            textPaint.setTextSize(11);
            canvas.drawText(dataParts[1], centerX, centerY + 22, textPaint);
        }
    }

    // 设置九宫格数据
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

    // 设置亮度
    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, brightness));
        circlePaint.setColor(Color.argb((int)(brightness * 120), 160, 174, 192));
        linePaint.setColor(Color.argb((int)(brightness * 150), 100, 149, 237));
        textPaint.setColor(Color.argb((int)(brightness * 255), 135, 206, 235));
        centerPaint.setColor(Color.argb((int)(brightness * 200), 44, 199, 194));
        invalidate();
    }
}
