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
    private Paint ringPaint;
    private Paint symbolPaint;
    private Paint namePaint;
    private Paint baguaLinePaint;
    private Paint arcPaint;
    private Paint taijiPaint;
    private Paint overlayPaint;
    private Paint gradientOverlayPaint;
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
        
        ringPaint = new Paint();
        ringPaint.setStyle(Paint.Style.STROKE);
        ringPaint.setAntiAlias(true);
        
        symbolPaint = new Paint();
        symbolPaint.setColor(0x306A90B0);
        symbolPaint.setTextSize(80f);
        symbolPaint.setAntiAlias(true);
        symbolPaint.setTextAlign(Paint.Align.CENTER);
        
        namePaint = new Paint();
        namePaint.setColor(0x255A80A0);
        namePaint.setTextSize(64f);
        namePaint.setAntiAlias(true);
        namePaint.setTextAlign(Paint.Align.CENTER);
        
        baguaLinePaint = new Paint();
        baguaLinePaint.setColor(0x0A3A6080);
        baguaLinePaint.setStrokeWidth(1f);
        
        arcPaint = new Paint();
        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setColor(0x104A7090);
        arcPaint.setStrokeWidth(1f);
        
        taijiPaint = new Paint();
        taijiPaint.setStyle(Paint.Style.FILL);
        taijiPaint.setAntiAlias(true);
        
        overlayPaint = new Paint();
        overlayPaint.setColor(0x10FFFFFF);
        
        gradientOverlayPaint = new Paint();
        
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        setAlpha(0.9f);
    }
    
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        centerX = w / 2;
        centerY = h / 2;
        radius = Math.min(w, h) * 0.55f;
        
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
        glowPaint.setAlpha(100);
        
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
        float[] ringRadii = {radius * 0.3f, radius * 0.5f, radius * 0.7f, radius * 0.85f};
        int[] ringColors = {0x255A80C0, 0x1A4A70A0, 0x103A6080, 0x0A2A5060};
        int[] ringWidths = {2, 2, 1, 1};
        
        for (int i = 0; i < ringRadii.length; i++) {
            ringPaint.setColor(ringColors[i]);
            ringPaint.setStrokeWidth(ringWidths[i]);
            canvas.drawCircle(centerX, centerY, ringRadii[i], ringPaint);
        }
        
        drawTaiChi(canvas, radius * 0.25f);
        
        // 后天八卦排列：从北开始顺时针 - 坎、艮、震、巽、离、坤、兑、乾
        String[] baGuaSymbols = {"☵", "☶", "☳", "☴", "☲", "☷", "☱", "☰"};
        String[] baGuaNames = {"坎", "艮", "震", "巽", "离", "坤", "兑", "乾"};
        
        float symbolRadius = radius * 0.65f;
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8 - Math.PI / 2;
            float x = (float) (centerX + symbolRadius * Math.cos(angle));
            float y = (float) (centerY + symbolRadius * Math.sin(angle));
            
            canvas.drawText(baGuaSymbols[i], x, y - 20, symbolPaint);
            canvas.drawText(baGuaNames[i], x, y + 48, namePaint);
        }
        
        for (int i = 0; i < 8; i++) {
            double angle = Math.PI * 2 * i / 8 - Math.PI / 2;
            float x = (float) (centerX + radius * 0.8f * Math.cos(angle));
            float y = (float) (centerY + radius * 0.8f * Math.sin(angle));
            canvas.drawLine(centerX, centerY, x, y, baguaLinePaint);
        }
        
        float arcRadius = radius * 0.75f;
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
        float halfSize = size / 2;
        
        canvas.save();
        canvas.rotate(-90, centerX, centerY);
        
        taijiPaint.setColor(0xFF0A0A12);
        canvas.drawArc(
            centerX - size, centerY - size,
            centerX + size, centerY + size,
            0, 180, true, taijiPaint
        );
        
        taijiPaint.setColor(0x205A80B0);
        canvas.drawArc(
            centerX - size, centerY - size,
            centerX + size, centerY + size,
            180, 180, true, taijiPaint
        );
        
        taijiPaint.setColor(0xFF0A0A12);
        canvas.drawCircle(centerX, centerY - halfSize, halfSize, taijiPaint);
        
        taijiPaint.setColor(0x205A80B0);
        canvas.drawCircle(centerX, centerY + halfSize, halfSize, taijiPaint);
        
        taijiPaint.setColor(0x205A80B0);
        canvas.drawCircle(centerX, centerY - halfSize, halfSize * 0.3f, taijiPaint);
        
        taijiPaint.setColor(0xFF0A0A12);
        canvas.drawCircle(centerX, centerY + halfSize, halfSize * 0.3f, taijiPaint);
        
        canvas.restore();
    }
    
    private void drawFrostedOverlay(Canvas canvas, int w, int h) {
        canvas.drawRect(0, 0, w, h, overlayPaint);
        
        LinearGradient lg = new LinearGradient(
            0, 0, w, h,
            0x15FFFFFF, 0x00FFFFFF,
            Shader.TileMode.CLAMP
        );
        gradientOverlayPaint.setShader(lg);
        canvas.drawRect(0, 0, w, h, gradientOverlayPaint);
    }
}