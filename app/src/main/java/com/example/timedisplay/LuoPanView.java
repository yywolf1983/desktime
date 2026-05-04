package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class LuoPanView extends View {
    private Paint circlePaint;
    private Paint textPaint;
    private Paint centerPaint;
    private Paint linePaint;
    private Paint outerCirclePaint;
    
    // 二十四山向
    private static final String[] TWENTY_FOUR_MOUNTAINS = {
        "壬", "子", "癸",
        "丑", "艮", "寅",
        "甲", "卯", "乙",
        "辰", "巽", "巳",
        "丙", "午", "丁",
        "未", "坤", "申",
        "庚", "酉", "辛",
        "戌", "乾", "亥"
    };
    
    // 八宫方位
    private static final String[] EIGHT_DIRECTIONS = {
        "坎", "艮", "震", "巽", "离", "坤", "兑", "乾"
    };
    
    // 八卦符号
    private static final String[] EIGHT_TRIGRAMS = {
        "☵", "☶", "☳", "☴", "☲", "☷", "☱", "☰"
    };
    
    private float rotation = 0;

    public LuoPanView(Context context) {
        super(context);
        init();
    }

    public LuoPanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LuoPanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // 初始化外圆画笔
        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(Color.argb(200, 255, 215, 0));
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(4);
        outerCirclePaint.setAntiAlias(true);
        
        // 初始化圆形画笔
        circlePaint = new Paint();
        circlePaint.setColor(Color.argb(180, 180, 200, 220));
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(3);
        circlePaint.setAntiAlias(true);

        // 初始化文字画笔
        textPaint = new Paint();
        textPaint.setColor(Color.argb(255, 135, 206, 235));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(36);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        // 初始化中心画笔
        centerPaint = new Paint();
        centerPaint.setColor(Color.argb(220, 44, 199, 194));
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setAntiAlias(true);
        
        // 初始化线条画笔
        linePaint = new Paint();
        linePaint.setColor(Color.argb(200, 120, 169, 255));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(2);
        linePaint.setAntiAlias(true);
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
        int centerX = width / 2;
        int centerY = height / 2;
        float radius = Math.min(width, height) / 2 - 30;

        canvas.save();
        canvas.rotate(rotation, centerX, centerY);

        // 绘制多层圆形
        drawConcentricCircles(canvas, centerX, centerY, radius);
        
        // 绘制二十四山
        drawTwentyFourMountains(canvas, centerX, centerY, radius);
        
        // 绘制八卦
        drawEightTrigrams(canvas, centerX, centerY, radius);
        
        // 绘制中心
        drawCenter(canvas, centerX, centerY);
        
        // 绘制指针（固定在南方）
        drawPointer(canvas, centerX, centerY, radius);

        canvas.restore();
    }

    private void drawConcentricCircles(Canvas canvas, int centerX, int centerY, float radius) {
        // 绘制外圆
        canvas.drawCircle(centerX, centerY, radius, outerCirclePaint);
        
        // 绘制多层同心圆
        float[] radii = {radius * 0.92f, radius * 0.78f, radius * 0.62f, radius * 0.48f, radius * 0.25f};
        for (float r : radii) {
            canvas.drawCircle(centerX, centerY, r, circlePaint);
        }
    }

    private void drawTwentyFourMountains(Canvas canvas, int centerX, int centerY, float radius) {
        float angleStep = 360 / 24;
        float textRadius = radius * 0.86f;

        for (int i = 0; i < 24; i++) {
            float angle = (float) Math.toRadians(i * angleStep - 90);
            float x = centerX + (float) (textRadius * Math.cos(angle));
            float y = centerY + (float) (textRadius * Math.sin(angle));
            
            // 调整文字方向
            canvas.save();
            canvas.rotate(i * angleStep, x, y);
            
            // 根据位置设置颜色
            if (i % 3 == 0) {
                textPaint.setColor(Color.argb(255, 255, 215, 0)); // 金色
            } else if (i % 3 == 1) {
                textPaint.setColor(Color.argb(255, 144, 238, 144)); // 绿色
            } else {
                textPaint.setColor(Color.argb(255, 135, 206, 235)); // 蓝色
            }
            
            textPaint.setTextSize(40);
            canvas.drawText(TWENTY_FOUR_MOUNTAINS[i], x, y + 14, textPaint);
            canvas.restore();
            
            // 绘制分隔线
            float lineAngle = (float) Math.toRadians(i * angleStep - 90);
            float startX = centerX + (float) (radius * 0.78f * Math.cos(lineAngle));
            float startY = centerY + (float) (radius * 0.78f * Math.sin(lineAngle));
            float endX = centerX + (float) (radius * 0.94f * Math.cos(lineAngle));
            float endY = centerY + (float) (radius * 0.94f * Math.sin(lineAngle));
            canvas.drawLine(startX, startY, endX, endY, linePaint);
        }
    }

    private void drawEightTrigrams(Canvas canvas, int centerX, int centerY, float radius) {
        float angleStep = 360 / 8;
        float trigramRadius = radius * 0.70f;
        float directionRadius = radius * 0.54f;

        for (int i = 0; i < 8; i++) {
            float angle = (float) Math.toRadians(i * angleStep - 90);
            
            // 绘制八卦符号
            float trigramX = centerX + (float) (trigramRadius * Math.cos(angle));
            float trigramY = centerY + (float) (trigramRadius * Math.sin(angle));
            
            textPaint.setTextSize(48);
            textPaint.setColor(Color.argb(255, 74, 144, 217));
            canvas.drawText(EIGHT_TRIGRAMS[i], trigramX, trigramY + 16, textPaint);
            
            // 绘制方位文字
            float directionX = centerX + (float) (directionRadius * Math.cos(angle));
            float directionY = centerY + (float) (directionRadius * Math.sin(angle));
            
            textPaint.setTextSize(32);
            textPaint.setColor(Color.argb(230, 255, 215, 0));
            canvas.drawText(EIGHT_DIRECTIONS[i], directionX, directionY + 12, textPaint);
        }
    }

    private void drawCenter(Canvas canvas, int centerX, int centerY) {
        float centerRadius = 60;
        
        // 绘制中心圆
        canvas.drawCircle(centerX, centerY, centerRadius, centerPaint);
        
        // 绘制中心太极点
        Paint taijiPaint = new Paint();
        taijiPaint.setColor(Color.argb(255, 255, 255, 255));
        taijiPaint.setStyle(Paint.Style.FILL);
        taijiPaint.setAntiAlias(true);
        canvas.drawCircle(centerX, centerY, 20, taijiPaint);
        
        taijiPaint.setColor(Color.argb(255, 0, 0, 0));
        canvas.drawCircle(centerX, centerY, 10, taijiPaint);
    }

    private void drawPointer(Canvas canvas, int centerX, int centerY, float radius) {
        // 绘制固定的指针（指向正南）
        Paint pointerPaint = new Paint();
        pointerPaint.setColor(Color.argb(255, 255, 70, 70));
        pointerPaint.setStyle(Paint.Style.FILL);
        pointerPaint.setAntiAlias(true);
        
        // 绘制指针形状
        float pointerLength = radius * 0.85f;
        float pointerWidth = 20;
        
        // 保存画布状态并旋转指针到正南方向
        canvas.save();
        canvas.rotate(180, centerX, centerY);
        
        // 绘制指针三角形头部
        android.graphics.Path path = new android.graphics.Path();
        path.moveTo(centerX, centerY - pointerLength - 30);
        path.lineTo(centerX - pointerWidth, centerY - pointerLength);
        path.lineTo(centerX + pointerWidth, centerY - pointerLength);
        path.close();
        canvas.drawPath(path, pointerPaint);
        
        // 绘制指针尾部
        RectF pointerRect = new RectF(
            centerX - pointerWidth / 2,
            centerY - pointerLength,
            centerX + pointerWidth / 2,
            centerY
        );
        canvas.drawRect(pointerRect, pointerPaint);
        
        canvas.restore();
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        invalidate();
    }

    public float getRotationValue() {
        return this.rotation;
    }
}
