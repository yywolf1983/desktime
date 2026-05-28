package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class LuoPanView extends View {
    
    private Paint circlePaint;
    private Paint textPaint;
    private Paint centerPaint;
    private Paint outerCirclePaint;
    private Paint linePaint;
    private Paint arrowPaint;
    private Paint borderPaint;
    private Paint bgPaint;
    private Paint taijiPaint;
    
    private static final String[] TWENTY_FOUR_MOUNTAINS = {
        "壬", "子", "癸", "丑", "艮", "寅",
        "甲", "卯", "乙", "辰", "巽", "巳",
        "丙", "午", "丁", "未", "坤", "申",
        "庚", "酉", "辛", "戌", "乾", "亥"
    };
    
    private static final String[] TWELVE_ZHI = {
        "子", "丑", "寅", "卯", "辰", "巳",
        "午", "未", "申", "酉", "戌", "亥"
    };
    
    private static final String[] EIGHT_TRIGRAMS = {
        "☵", "☶", "☳", "☴", "☲", "☷", "☱", "☰"
    };
    
    private static final String[] TEN_GAN = {
        "甲", "乙", "丙", "丁", "戊",
        "己", "庚", "辛", "壬", "癸"
    };
    
    private static final String[] EIGHT_DIRECTIONS = {
        "北", "东北", "东", "东南", "南", "西南", "西", "西北"
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

    private float scale = 1f;

    private void init() {
        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(Color.YELLOW);
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(4);
        outerCirclePaint.setAntiAlias(true);
        
        circlePaint = new Paint();
        circlePaint.setColor(Color.CYAN);
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.CYAN);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(36);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        centerPaint = new Paint();
        centerPaint.setColor(Color.rgb(44, 199, 194));
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setAntiAlias(true);
        
        linePaint = new Paint();
        linePaint.setColor(Color.argb(120, 255, 215, 0));
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1.2f);
        linePaint.setAntiAlias(true);
        
        arrowPaint = new Paint();
        arrowPaint.setColor(Color.RED);
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setAntiAlias(true);
        
        borderPaint = new Paint();
        borderPaint.setColor(Color.rgb(180, 0, 0));
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(0.8f);
        borderPaint.setAntiAlias(true);
        
        bgPaint = new Paint();
        bgPaint.setColor(Color.argb(40, 135, 206, 235));
        bgPaint.setStyle(Paint.Style.FILL);
        bgPaint.setAntiAlias(true);
        
        taijiPaint = new Paint();
        taijiPaint.setColor(Color.WHITE);
        taijiPaint.setStyle(Paint.Style.FILL);
        taijiPaint.setAntiAlias(true);
        
        setBackgroundColor(Color.TRANSPARENT);
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
        scale = Math.min(width, height) / 400f; // 基准尺寸400px
        int centerX = width / 2;
        int centerY = height / 2;
        float radius = Math.min(width, height) / 2 - 25 * scale;

        canvas.drawCircle(centerX, centerY, radius, bgPaint);

        canvas.save();
        canvas.rotate(rotation, centerX, centerY);

        drawConcentricCircles(canvas, centerX, centerY, radius);
        drawTwentyFourMountains(canvas, centerX, centerY, radius);
        drawTwelveZhi(canvas, centerX, centerY, radius);
        drawEightTrigrams(canvas, centerX, centerY, radius);
        drawTenGan(canvas, centerX, centerY, radius);
        drawEightDirections(canvas, centerX, centerY, radius);
        drawCenter(canvas, centerX, centerY, radius);

        canvas.restore();
        
        drawFixedPointer(canvas, centerX, centerY, radius);
    }

    private void drawConcentricCircles(Canvas canvas, int cx, int cy, float r) {
        canvas.drawCircle(cx, cy, r, outerCirclePaint);
        
        float[] radii = {r * 0.90f, r * 0.78f, r * 0.65f, r * 0.50f, r * 0.33f, r * 0.20f};
        for (float radius : radii) {
            canvas.drawCircle(cx, cy, radius, circlePaint);
        }
        

        
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 - 90);
            float startX = cx + (float) (r * 0.20f * Math.cos(angle));
            float startY = cy + (float) (r * 0.20f * Math.sin(angle));
            float midX = cx + (float) (r * 0.50f * Math.cos(angle));
            float midY = cy + (float) (r * 0.50f * Math.sin(angle));
            float mid2X = cx + (float) (r * 0.65f * Math.cos(angle));
            float mid2Y = cy + (float) (r * 0.65f * Math.sin(angle));
            float endX = cx + (float) (r * 0.90f * Math.cos(angle));
            float endY = cy + (float) (r * 0.90f * Math.sin(angle));
            
            canvas.drawLine(startX, startY, midX, midY, linePaint);
            canvas.drawLine(mid2X, mid2Y, endX, endY, linePaint);
        }
    }

    private void drawTwentyFourMountains(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.84f;
        for (int i = 0; i < 24; i++) {
            double angle = Math.toRadians(i * 15 - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * 15, x, y);
            
            if (i % 3 == 0) {
                textPaint.setColor(Color.YELLOW);
            } else if (i % 3 == 1) {
                textPaint.setColor(Color.GREEN);
            } else {
                textPaint.setColor(Color.CYAN);
            }
            
            textPaint.setTextSize(34 * scale);
            canvas.drawText(TWENTY_FOUR_MOUNTAINS[i], x, y + 12 * scale, textPaint);
            canvas.restore();
        }
    }
    
    private void drawTwelveZhi(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.71f;
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(i * 30 - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * 30, x, y);
            
            textPaint.setTextSize(30 * scale);
            textPaint.setColor(Color.rgb(255, 182, 193));
            canvas.drawText(TWELVE_ZHI[i], x, y + 11 * scale, textPaint);
            canvas.restore();
        }
    }
    
    private void drawEightTrigrams(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.57f;
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * 45, x, y);
            
            textPaint.setTextSize(44 * scale);
            textPaint.setColor(Color.rgb(74, 144, 217));
            canvas.drawText(EIGHT_TRIGRAMS[i], x, y + 16 * scale, textPaint);
            canvas.restore();
        }
    }
    
    private void drawTenGan(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.41f;
        for (int i = 0; i < 10; i++) {
            double angle = Math.toRadians(i * 36 - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * 36, x, y);
            
            textPaint.setTextSize(24 * scale);
            textPaint.setColor(Color.rgb(173, 255, 47));
            canvas.drawText(TEN_GAN[i], x, y + 9 * scale, textPaint);
            canvas.restore();
        }
    }
    
    private void drawEightDirections(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.26f;
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * 45, x, y);
            
            textPaint.setTextSize(20 * scale);
            textPaint.setColor(Color.WHITE);
            textPaint.setFakeBoldText(true);
            canvas.drawText(EIGHT_DIRECTIONS[i], x, y + 7 * scale, textPaint);
            canvas.restore();
        }
    }

    private void drawCenter(Canvas canvas, int cx, int cy, float r) {
        canvas.drawCircle(cx, cy, 18 * scale, centerPaint);
        
        taijiPaint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, 7 * scale, taijiPaint);
        
        taijiPaint.setColor(Color.BLACK);
        canvas.drawCircle(cx, cy, 3 * scale, taijiPaint);
    }
    
    private void drawFixedPointer(Canvas canvas, int cx, int cy, float r) {
        float baseRadius = r * 0.96f;
        float tipRadius = r * 1.01f;
        float shoulderRadius = r * 0.985f;
        
        double upAngle = Math.toRadians(-90);
        double leftAngle = Math.toRadians(-90 - 7);
        double rightAngle = Math.toRadians(-90 + 7);
        
        float tipX = cx + (float) (tipRadius * Math.cos(upAngle));
        float tipY = cy + (float) (tipRadius * Math.sin(upAngle));
        
        float baseX = cx + (float) (baseRadius * Math.cos(upAngle));
        float baseY = cy + (float) (baseRadius * Math.sin(upAngle));
        
        float shoulderLeftX = cx + (float) (shoulderRadius * Math.cos(leftAngle));
        float shoulderLeftY = cy + (float) (shoulderRadius * Math.sin(leftAngle));
        
        float shoulderRightX = cx + (float) (shoulderRadius * Math.cos(rightAngle));
        float shoulderRightY = cy + (float) (shoulderRadius * Math.sin(rightAngle));
        
        android.graphics.Path arrowPath = new android.graphics.Path();
        arrowPath.moveTo(tipX, tipY);
        arrowPath.lineTo(shoulderLeftX, shoulderLeftY);
        arrowPath.lineTo(baseX, baseY);
        arrowPath.lineTo(shoulderRightX, shoulderRightY);
        arrowPath.close();
        canvas.drawPath(arrowPath, arrowPaint);
        
        canvas.drawPath(arrowPath, borderPaint);
        
        textPaint.setTextSize(14 * scale);
        textPaint.setColor(Color.RED);
        textPaint.setFakeBoldText(true);
        float textY = cy - r * 1.1f;
        canvas.drawText("北", cx, textY, textPaint);
    }

    public void setRotation(float rotation) {
        this.rotation = rotation;
        invalidate();
    }

    public float getRotationValue() {
        return this.rotation;
    }
    
    public String getCurrentMountain() {
        float normalizedRotation = (-rotation % 360 + 360) % 360;
        int index = Math.round(normalizedRotation / 15f) % 24;
        if (index < 0) index += 24;
        return TWENTY_FOUR_MOUNTAINS[index];
    }
    
    public String getCurrentDirection() {
        float normalizedRotation = (-rotation % 360 + 360) % 360;
        int index = Math.round(normalizedRotation / 45f) % 8;
        if (index < 0) index += 8;
        String[] directions = {"坎(北)", "艮(东北)", "震(东)", "巽(东南)", "离(南)", "坤(西南)", "兑(西)", "乾(西北)"};
        return directions[index];
    }
}
