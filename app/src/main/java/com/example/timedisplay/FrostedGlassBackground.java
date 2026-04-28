package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class FrostedGlassBackground extends View {
    
    private Paint gradientPaint;
    private Paint glowPaint;
    private Paint gridPaint;
    private int centerX, centerY;
    private float radius;
    
    public FrostedGlassBackground(Context context) {
        super(context);
        init();
    }
    
    public FrostedGlassBackground(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public FrostedGlassBackground(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }
    
    private void init() {
        gradientPaint = new Paint();
        glowPaint = new Paint();
        gridPaint = new Paint();
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setAlpha(0.9f);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(w, h) * 0.4f;
        
        setupPaints(w, h);
    }
    
    private void setupPaints(int w, int h) {
        LinearGradient mainGradient = new LinearGradient(
            0, 0, w, h,
            new int[] {0xFF050508, 0xFF0A0A12, 0xFF050508},
            new float[] {0f, 0.5f, 1f},
            Shader.TileMode.CLAMP
        );
        gradientPaint.setShader(mainGradient);
        
        RadialGradient glowGradient = new RadialGradient(
            centerX, centerY, radius * 2f,
            new int[] {0x204A70A0, 0x106080A0, 0x08305060, 0x00000000},
            new float[] {0f, 0.4f, 0.7f, 1f},
            Shader.TileMode.CLAMP
        );
        glowPaint.setShader(glowGradient);
        glowPaint.setAlpha(150);
        
        gridPaint.setColor(0x104A70A0);
        gridPaint.setStrokeWidth(1f);
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int w = getWidth();
        int h = getHeight();
        
        canvas.drawRect(0, 0, w, h, gradientPaint);
        
        drawGrid(canvas, w, h);
        
        canvas.drawCircle(centerX, centerY, radius * 2f, glowPaint);
        
        drawBaGuaArray(canvas);
        
        drawFrostedOverlay(canvas, w, h);
    }
    
    private void drawGrid(Canvas canvas, int w, int h) {
        float gridSize = 50f;
        
        for (float x = 0; x < w; x += gridSize) {
            canvas.drawLine(x, 0, x, h, gridPaint);
        }
        for (float y = 0; y < h; y += gridSize) {
            canvas.drawLine(0, y, w, y, gridPaint);
        }
    }
    
    private void drawBaGuaArray(Canvas canvas) {
        Paint ringPaint = new Paint();
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setAntiAlias(true);
        
        float[] ringRadii = {radius * 0.35f, radius * 0.55f, radius * 0.75f, radius * 0.95f};
        int[] ringColors = {0x255A80C0, 0x1A4A70A0, 0x103A6080, 0x0A2A5060};
        int[] ringWidths = {2, 2, 1, 1};
        
        for (int i = 0; i < ringRadii.length; i++) {
            ringPaint.setColor(ringColors[i]);
            ringPaint.setStrokeWidth(ringWidths[i]);
            canvas.drawCircle(centerX, centerY, ringRadii[i], ringPaint);
        }
        
        drawTaiChi(canvas, radius * 0.25f);
        
        String[] baGuaSymbols = {"☰", "☱", "☲", "☳", "☴", "☵", "☶", "☷"};
        String[] baGuaNames = {"乾", "兑", "离", "震", "巽", "坎", "艮", "坤"};
        
        Paint symbolPaint = new Paint();
        symbolPaint.setColor(0x306A90B0);
        symbolPaint.setTextSize(20f);
        symbolPaint.setAntiAlias(true);
        symbolPaint.setTextAlign(Paint.Align.CENTER);
        
        Paint namePaint = new Paint();
        namePaint.setColor(0x255A80A0);
        namePaint.setTextSize(16f);
        namePaint.setAntiAlias(true);
        namePaint.setTextAlign(Paint.Align.CENTER);
        
        float symbolRadius = radius * 0.6f;
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8 - Math.PI / 2;
            float x = (float) (centerX + symbolRadius * Math.cos(angle));
            float y = (float) (centerY + symbolRadius * Math.sin(angle));
            
            canvas.drawText(baGuaSymbols[i], x, y - 8, symbolPaint);
            canvas.drawText(baGuaNames[i], x, y + 20, namePaint);
        }
        
        Paint linePaint = new Paint();
        linePaint.setColor(0x0A3A6080);
        linePaint.setStrokeWidth(1f);
        
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8 - Math.PI / 2;
            float x = (float) (centerX + radius * 0.9f * Math.cos(angle));
            float y = (float) (centerY + radius * 0.9f * Math.sin(angle));
            canvas.drawLine(centerX, centerY, x, y, linePaint);
        }
        
        Paint arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(0x104A7090);
        arcPaint.setStrokeWidth(1f);
        
        float arcRadius = radius * 0.85f;
        for (int i = 0; i < 8; i++) {
            double startAngle = Math.PI * 2 * i / 8 - Math.PI / 2 - 0.1f;
            double sweepAngle = Math.PI * 2 / 8 + 0.2f;
            canvas.drawArc(
                centerX - arcRadius, centerY - arcRadius,
                centerX + arcRadius, centerY + arcRadius,
                (float) Math.toDegrees(startAngle),
                (float) Math.toDegrees(sweepAngle),
                false, arcPaint
            );
        }
    }
    
    private void drawTaiChi(Canvas canvas, float size) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        
        float halfSize = size / 2;
        
        canvas.save();
        canvas.rotate(-90, centerX, centerY);
        
        canvas.drawArc(
            centerX - size, centerY - size,
            centerX + size, centerY + size,
            0, 180, true, paint
        );
        
        paint.setColor(0x205A80B0);
        canvas.drawArc(
            centerX - size, centerY - size,
            centerX + size, centerY + size,
            180, 180, true, paint
        );
        
        paint.setColor(0xFF0A0A12);
        canvas.drawCircle(centerX, centerY - halfSize, halfSize, paint);
        
        paint.setColor(0x205A80B0);
        canvas.drawCircle(centerX, centerY + halfSize, halfSize, paint);
        
        paint.setColor(0x205A80B0);
        canvas.drawCircle(centerX, centerY - halfSize, halfSize * 0.3f, paint);
        
        paint.setColor(0xFF0A0A12);
        canvas.drawCircle(centerX, centerY + halfSize, halfSize * 0.3f, paint);
        
        canvas.restore();
    }
    
    private void drawFrostedOverlay(Canvas canvas, int w, int h) {
        Paint overlayPaint = new Paint();
        overlayPaint.setColor(0x10FFFFFF);
        canvas.drawRect(0, 0, w, h, overlayPaint);
        
        Paint gradientOverlay = new Paint();
        LinearGradient lg = new LinearGradient(
            0, 0, w, h,
            0x15FFFFFF, 0x00FFFFFF,
            Shader.TileMode.CLAMP
        );
        gradientOverlay.setShader(lg);
        canvas.drawRect(0, 0, w, h, gradientOverlay);
    }
}