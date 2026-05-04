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

        // 绘制多层圆圈
        drawConcentricCircles(canvas, centerX, centerY, radius);
        
        // 绘制二十四山
        drawTwentyFourMountains(canvas, centerX, centerY, radius);
        
        // 绘制八卦
        drawEightTrigrams(canvas, centerX, centerY, radius);
        
        // 绘制十二地支
        drawTwelveZhi(canvas, centerX, centerY, radius);
        
        // 绘制十天干
        drawTenGan(canvas, centerX, centerY, radius);
        
        // 绘制中心
        drawCenter(canvas, centerX, centerY);

        canvas.restore();
        
        // 绘制固定在外面的指针（不随罗盘旋转）
        drawFixedPointer(canvas, centerX, centerY, radius);
    }

    private void drawConcentricCircles(Canvas canvas, int centerX, int centerY, float radius) {
        // 绘制外圆
        canvas.drawCircle(centerX, centerY, radius, outerCirclePaint);
        
        // 绘制多层同心圆
        float[] radii = {
            radius * 0.95f,  // 最外层装饰圈
            radius * 0.88f,  // 二十四山圈
            radius * 0.78f,  // 分界线
            radius * 0.68f,  // 八卦圈
            radius * 0.58f,  // 十二地支圈
            radius * 0.45f,  // 十天干圈
            radius * 0.30f,  // 内圈
            radius * 0.20f   // 中心圈
        };
        for (float r : radii) {
            canvas.drawCircle(centerX, centerY, r, circlePaint);
        }
        
        // 绘制六十四卦的分隔线
        drawDividingLines(canvas, centerX, centerY, radius);
    }
    
    private void drawDividingLines(Canvas canvas, int centerX, int centerY, float radius) {
        // 绘制8个主方向线
        float angleStep = 360 / 8;
        for (int i = 0; i < 8; i++) {
            float angle = (float) Math.toRadians(i * angleStep - 90);
            float startX = centerX + (float) (radius * 0.20f * Math.cos(angle));
            float startY = centerY + (float) (radius * 0.20f * Math.sin(angle));
            float endX = centerX + (float) (radius * 0.95f * Math.cos(angle));
            float endY = centerY + (float) (radius * 0.95f * Math.sin(angle));
            
            Paint linePaint = new Paint();
            linePaint.setColor(Color.argb(150, 255, 215, 0));
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(2);
            linePaint.setAntiAlias(true);
            
            canvas.drawLine(startX, startY, endX, endY, linePaint);
        }
        
        // 绘制16个次方向线
        angleStep = 360 / 16;
        for (int i = 0; i < 16; i++) {
            if (i % 2 == 0) continue; // 跳过已绘制的主方向
            float angle = (float) Math.toRadians(i * angleStep - 90);
            float startX = centerX + (float) (radius * 0.30f * Math.cos(angle));
            float startY = centerY + (float) (radius * 0.30f * Math.sin(angle));
            float endX = centerX + (float) (radius * 0.88f * Math.cos(angle));
            float endY = centerY + (float) (radius * 0.88f * Math.sin(angle));
            
            Paint linePaint = new Paint();
            linePaint.setColor(Color.argb(80, 135, 206, 235));
            linePaint.setStyle(Paint.Style.STROKE);
            linePaint.setStrokeWidth(1);
            linePaint.setAntiAlias(true);
            
            canvas.drawLine(startX, startY, endX, endY, linePaint);
        }
    }
    
    private void drawTwelveZhi(Canvas canvas, int centerX, int centerY, float radius) {
        String[] TWELVE_ZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
        float angleStep = 360 / 12;
        float textRadius = radius * 0.62f;
        
        for (int i = 0; i < 12; i++) {
            float angle = (float) Math.toRadians(i * angleStep - 90);
            float x = centerX + (float) (textRadius * Math.cos(angle));
            float y = centerY + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * angleStep, x, y);
            
            textPaint.setTextSize(26);
            textPaint.setColor(Color.argb(255, 255, 182, 193)); // 粉红色
            canvas.drawText(TWELVE_ZHI[i], x, y + 9, textPaint);
            canvas.restore();
        }
    }
    
    private void drawTenGan(Canvas canvas, int centerX, int centerY, float radius) {
        String[] TEN_GAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
        float angleStep = 360 / 10;
        float textRadius = radius * 0.50f;
        
        for (int i = 0; i < 10; i++) {
            float angle = (float) Math.toRadians(i * angleStep - 90);
            float x = centerX + (float) (textRadius * Math.cos(angle));
            float y = centerY + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * angleStep, x, y);
            
            textPaint.setTextSize(22);
            textPaint.setColor(Color.argb(255, 173, 255, 47)); // 绿黄色
            canvas.drawText(TEN_GAN[i], x, y + 8, textPaint);
            canvas.restore();
        }
    }
    
    private void drawFixedPointer(Canvas canvas, int centerX, int centerY, float radius) {
        // 绘制固定在外面的指针（指向正南）
        Paint pointerPaint = new Paint();
        pointerPaint.setColor(Color.argb(255, 255, 70, 70));
        pointerPaint.setStyle(Paint.Style.FILL);
        pointerPaint.setAntiAlias(true);
        
        // 指针在罗盘外面
        float pointerStart = radius * 1.02f;
        float pointerEnd = radius * 1.15f;
        
        // 绘制指针三角形（指向南方）
        float angle = (float) Math.toRadians(90);
        float tipX = centerX + (float) (pointerEnd * Math.cos(angle));
        float tipY = centerY + (float) (pointerEnd * Math.sin(angle));
        
        float baseAngle1 = (float) Math.toRadians(90 - 15);
        float baseAngle2 = (float) Math.toRadians(90 + 15);
        float base1X = centerX + (float) (pointerStart * Math.cos(baseAngle1));
        float base1Y = centerY + (float) (pointerStart * Math.sin(baseAngle1));
        float base2X = centerX + (float) (pointerStart * Math.cos(baseAngle2));
        float base2Y = centerY + (float) (pointerStart * Math.sin(baseAngle2));
        
        android.graphics.Path path = new android.graphics.Path();
        path.moveTo(tipX, tipY);
        path.lineTo(base1X, base1Y);
        path.lineTo(base2X, base2Y);
        path.close();
        canvas.drawPath(path, pointerPaint);
        
        // 绘制"南"字标记
        textPaint.setTextSize(28);
        textPaint.setColor(Color.argb(255, 255, 70, 70));
        canvas.drawText("南", tipX, tipY + 40, textPaint);
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

    public void setRotation(float rotation) {
        this.rotation = rotation;
        invalidate();
    }

    public float getRotationValue() {
        return this.rotation;
    }
}
