package com.example.timedisplay;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class LuoPanView extends View {
    
    private OnRotationChangeListener rotationChangeListener;
    
    public interface OnRotationChangeListener {
        void onRotationChanged(float rotation);
    }
    
    public void setOnRotationChangeListener(OnRotationChangeListener listener) {
        this.rotationChangeListener = listener;
    }
    
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
    
    private static final String[] EIGHT_TRIGRAMS_NAMES = {
        "坎", "艮", "震", "巽", "离", "坤", "兑", "乾"
    };
    
    private static final String[] TEN_GAN = {
        "甲", "乙", "丙", "丁", "戊",
        "己", "庚", "辛", "壬", "癸"
    };
    
    private static final String[] EIGHT_DIRECTIONS = {
        "北", "东北", "东", "东南", "南", "西南", "西", "西北"
    };
    
    private static final String[] NINE_STARS = {
        "蓬", "芮", "冲", "辅", "禽", "心", "柱", "任", "英"
    };
    
    private static final String[] EIGHT_DOORS = {
        "休", "生", "伤", "杜", "景", "死", "惊", "开"
    };
    
    private float rotation = 0;
    
    private boolean showTwentyFourMountains = true;
    private boolean showTwelveZhi = true;
    private boolean showEightTrigrams = true;
    private boolean showTenGan = true;
    private boolean showEightDirections = true;
    private boolean showNineStars = false;
    private boolean showEightDoors = false;

    public LuoPanView(Context context) {
        super(context);
        init();
    }

    public LuoPanView(Context context, AttributeSet attrs) {
        super(context, attrs);
        resolveMaxSize(context, attrs);
        init();
    }

    public LuoPanView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        resolveMaxSize(context, attrs);
        init();
    }

    private float scale = 1f;
    private int maxSizePx = 0;   // 布局声明的 maxWidth/maxHeight（dp 已转 px），0 表示不限

    /** 读取布局中声明的 android:maxWidth / android:maxHeight（单位已转为 px） */
    private void resolveMaxSize(Context context, AttributeSet attrs) {
        if (attrs == null) return;
        try {
            TypedArray a = context.obtainStyledAttributes(
                    attrs, new int[]{android.R.attr.maxWidth, android.R.attr.maxHeight});
            int maxW = a.getDimensionPixelSize(0, Integer.MAX_VALUE);
            int maxH = a.getDimensionPixelSize(1, Integer.MAX_VALUE);
            a.recycle();
            int maxPx = Math.min(maxW, maxH);
            maxSizePx = (maxPx > 0 && maxPx != Integer.MAX_VALUE) ? maxPx : 0;
        } catch (Exception ignored) {
            maxSizePx = 0;
        }
    }

    private void init() {
        outerCirclePaint = new Paint();
        outerCirclePaint.setColor(0xFFE6C46A);   // 主题鎏金
        outerCirclePaint.setStyle(Paint.Style.STROKE);
        outerCirclePaint.setStrokeWidth(4);
        outerCirclePaint.setAntiAlias(true);
        
        circlePaint = new Paint();
        circlePaint.setColor(0x59E6C46A);        // 淡金同心圆
        circlePaint.setStyle(Paint.Style.STROKE);
        circlePaint.setStrokeWidth(2);
        circlePaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(0xFFBFE0EA);          // 淡青（text_accent）
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(36);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        centerPaint = new Paint();
        centerPaint.setColor(0xFFC99A3E);        // 暗金
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setAntiAlias(true);
        
        linePaint = new Paint();
        linePaint.setColor(0x5AE6C46A);          // 淡金放射线
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setStrokeWidth(1.2f);
        linePaint.setAntiAlias(true);
        
        arrowPaint = new Paint();
        arrowPaint.setColor(0xE6FF6347);         // 主题 danger 红
        arrowPaint.setStyle(Paint.Style.FILL);
        arrowPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setColor(0xFFE6C46A);        // 鎏金描边
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2.5f);
        borderPaint.setAntiAlias(true);
        
        bgPaint = new Paint();
        bgPaint.setColor(0x24181226);            // 暗底（与卡片同色系）
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
        // 尊重父容器约束与布局声明的 maxWidth/maxHeight，避免横屏下过大被裁剪
        if (MeasureSpec.getMode(widthMeasureSpec) == MeasureSpec.AT_MOST) {
            size = Math.min(size, width);
        }
        if (MeasureSpec.getMode(heightMeasureSpec) == MeasureSpec.AT_MOST) {
            size = Math.min(size, height);
        }
        if (maxSizePx > 0) {
            size = Math.min(size, maxSizePx);
        }
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
        // 预留边距：指针尖端为 1.03r、"北"字在 1.12r，需保证 1.12r <= min(w,h)/2
        float radius = Math.min(width, height) * 0.43f;

        canvas.drawCircle(centerX, centerY, radius, bgPaint);

        canvas.save();
        canvas.rotate(rotation, centerX, centerY);

        drawConcentricCircles(canvas, centerX, centerY, radius);
        
        if (showTwentyFourMountains) {
            drawTwentyFourMountains(canvas, centerX, centerY, radius);
        }
        if (showTwelveZhi) {
            drawTwelveZhi(canvas, centerX, centerY, radius);
        }
        if (showEightTrigrams) {
            drawEightTrigrams(canvas, centerX, centerY, radius);
        }
        if (showTenGan) {
            drawTenGan(canvas, centerX, centerY, radius);
        }
        if (showEightDirections) {
            drawEightDirections(canvas, centerX, centerY, radius);
        }
        if (showNineStars) {
            drawNineStars(canvas, centerX, centerY, radius);
        }
        if (showEightDoors) {
            drawEightDoors(canvas, centerX, centerY, radius);
        }
        
        drawCenter(canvas, centerX, centerY, radius);

        canvas.restore();
        
        drawFixedPointer(canvas, centerX, centerY, radius);
    }

    private void drawConcentricCircles(Canvas canvas, int cx, int cy, float r) {
        canvas.drawCircle(cx, cy, r, outerCirclePaint);
        
        float[] radii = {r * 0.92f, r * 0.80f, r * 0.68f, r * 0.52f, r * 0.38f, r * 0.22f};
        for (float radius : radii) {
            canvas.drawCircle(cx, cy, radius, circlePaint);
        }
        
        // 外圈每5度刻度标记（以北为准）
        Paint tickPaint = new Paint();
        tickPaint.setColor(0xFFE6C46A);
        tickPaint.setStrokeWidth(2);
        tickPaint.setAntiAlias(true);
        
        for (int deg = 0; deg < 360; deg += 5) {
            double angle = Math.toRadians(deg - 90); // 以北为0度
            float innerR, outerR;
            if (deg % 15 == 0) {
                // 每15度（二十四山位置）长刻度
                innerR = r * 0.95f;
                outerR = r * 1.0f;
                tickPaint.setStrokeWidth(3);
            } else if (deg % 5 == 0) {
                // 每5度短刻度
                innerR = r * 0.97f;
                outerR = r * 1.0f;
                tickPaint.setStrokeWidth(1.5f);
            } else {
                continue;
            }
            float startX = cx + (float) (innerR * Math.cos(angle));
            float startY = cy + (float) (innerR * Math.sin(angle));
            float endX = cx + (float) (outerR * Math.cos(angle));
            float endY = cy + (float) (outerR * Math.sin(angle));
            canvas.drawLine(startX, startY, endX, endY, tickPaint);
        }
        
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 - 90);
            float startX = cx + (float) (r * 0.22f * Math.cos(angle));
            float startY = cy + (float) (r * 0.22f * Math.sin(angle));
            float midX = cx + (float) (r * 0.52f * Math.cos(angle));
            float midY = cy + (float) (r * 0.52f * Math.sin(angle));
            float mid2X = cx + (float) (r * 0.68f * Math.cos(angle));
            float mid2Y = cy + (float) (r * 0.68f * Math.sin(angle));
            float endX = cx + (float) (r * 0.92f * Math.cos(angle));
            float endY = cy + (float) (r * 0.92f * Math.sin(angle));
            
            canvas.drawLine(startX, startY, midX, midY, linePaint);
            canvas.drawLine(mid2X, mid2Y, endX, endY, linePaint);
        }
    }

    private void drawTwentyFourMountains(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.86f;
        for (int i = 0; i < 24; i++) {
            double angle = Math.toRadians(i * 15 + 345 - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * 15 + 345, x, y);
            
            // 天元/人元/地元三元配色（金·青·暖灰，与主题协调）
            if (i % 3 == 0) {
                textPaint.setColor(0xFFE6C46A);
            } else if (i % 3 == 1) {
                textPaint.setColor(0xFFBFE0EA);
            } else {
                textPaint.setColor(0xFF9AA7B8);
            }
            
            textPaint.setTextSize(r * 0.09f);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float centerY = y - (fm.ascent + fm.descent) / 2;
            drawTextO(canvas, TWENTY_FOUR_MOUNTAINS[i], x, centerY, textPaint);
            canvas.restore();
        }
    }
    
    private void drawTwelveZhi(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.74f;
        int[] zhiAngles = {0, 30, 60, 90, 120, 150, 180, 210, 240, 270, 300, 330};
        for (int i = 0; i < 12; i++) {
            double angle = Math.toRadians(zhiAngles[i] - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(zhiAngles[i], x, y);
            
            textPaint.setTextSize(r * 0.085f);
            textPaint.setColor(0xFFBFE0EA);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float centerY = y - (fm.ascent + fm.descent) / 2;
            drawTextO(canvas, TWELVE_ZHI[i], x, centerY, textPaint);
            canvas.restore();
        }
    }
    
    private void drawEightTrigrams(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.60f;
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * 45, x, y);
            
            textPaint.setTextSize(r * 0.12f);
            textPaint.setColor(0xFF8AA6E4);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float centerY = y - (fm.ascent + fm.descent) / 2;
            drawTextO(canvas, EIGHT_TRIGRAMS[i], x - r * 0.05f, centerY, textPaint);
            
            textPaint.setTextSize(r * 0.08f);
            textPaint.setColor(0xFFC99A3E);
            fm = textPaint.getFontMetrics();
            centerY = y - (fm.ascent + fm.descent) / 2;
            drawTextO(canvas, EIGHT_TRIGRAMS_NAMES[i], x + r * 0.05f, centerY, textPaint);
            canvas.restore();
        }
    }
    
    private void drawTenGan(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.45f;
        int[] ganAngles = {75, 105, 165, 195, 0, 0, 255, 285, 345, 15};
        
        for (int i = 0; i < 10; i++) {
            if (i == 4 || i == 5) continue;
            double angle = Math.toRadians(ganAngles[i] - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(ganAngles[i], x, y);
            
            textPaint.setTextSize(r * 0.075f);
            textPaint.setColor(0xFF9CCB9C);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float centerY = y - (fm.ascent + fm.descent) / 2;
            drawTextO(canvas, TEN_GAN[i], x, centerY, textPaint);
            canvas.restore();
        }
    }
    
    private void drawEightDirections(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.28f;
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * 45, x, y);
            
            textPaint.setTextSize(r * 0.085f);
            textPaint.setColor(0xFFE0FFFF);
            textPaint.setFakeBoldText(true);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float centerY = y - (fm.ascent + fm.descent) / 2;
            drawTextO(canvas, EIGHT_DIRECTIONS[i], x, centerY, textPaint);
            canvas.restore();
        }
    }
    
    private void drawNineStars(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.15f;
        int[] starAngles = {0, 225, 90, 135, 0, 315, 270, 45, 180};
        
        for (int i = 0; i < 9; i++) {
            if (i == 4) {
                textPaint.setTextSize(r * 0.07f);
                textPaint.setColor(0xFFF3BA66);
                textPaint.setFakeBoldText(true);
                Paint.FontMetrics fm = textPaint.getFontMetrics();
                float centerY = cy - (fm.ascent + fm.descent) / 2;
                drawTextO(canvas, "天" + NINE_STARS[i], cx, centerY, textPaint);
                continue;
            }
            
            double angle = Math.toRadians(starAngles[i] - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(starAngles[i], x, y);
            
            textPaint.setTextSize(r * 0.07f);
            textPaint.setColor(0xFFF3BA66);
            textPaint.setFakeBoldText(true);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float centerY = y - (fm.ascent + fm.descent) / 2;
            drawTextO(canvas, "天" + NINE_STARS[i], x, centerY, textPaint);
            canvas.restore();
        }
    }
    
    private void drawEightDoors(Canvas canvas, int cx, int cy, float r) {
        float textRadius = r * 0.07f;
        for (int i = 0; i < 8; i++) {
            double angle = Math.toRadians(i * 45 - 90);
            float x = cx + (float) (textRadius * Math.cos(angle));
            float y = cy + (float) (textRadius * Math.sin(angle));
            
            canvas.save();
            canvas.rotate(i * 45, x, y);
            
            textPaint.setTextSize(r * 0.06f);
            textPaint.setColor(0xFFE6C46A);
            textPaint.setFakeBoldText(true);
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            float centerY = y - (fm.ascent + fm.descent) / 2;
            drawTextO(canvas, EIGHT_DOORS[i] + "门", x, centerY, textPaint);
            canvas.restore();
        }
    }

    private void drawTextO(Canvas canvas, String text, float x, float y, Paint paint) {
        if (text == null || paint == null) return;
        Paint.Style prevStyle = paint.getStyle();
        int prevColor = paint.getColor();
        float prevWidth = paint.getStrokeWidth();
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(Math.max(1f, paint.getTextSize() * 0.10f));
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setColor(0xDD10243B);
        canvas.drawText(text, x, y, paint);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(prevColor);
        canvas.drawText(text, x, y, paint);
        paint.setStyle(prevStyle);
        paint.setStrokeWidth(prevWidth);
    }

    private void drawCenter(Canvas canvas, int cx, int cy, float r) {
        float tr = r * 0.06f;
        taijiPaint.setStyle(Paint.Style.FILL);
        // 白底
        taijiPaint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy, tr, taijiPaint);
        // 阴阳鱼：右半黑 + 上下鱼头
        canvas.save();
        Path clip = new Path();
        clip.addCircle(cx, cy, tr, Path.Direction.CW);
        canvas.clipPath(clip);
        taijiPaint.setColor(Color.BLACK);
        canvas.drawRect(cx, cy - tr, cx + tr, cy + tr, taijiPaint);
        canvas.drawCircle(cx, cy - tr / 2, tr / 2, taijiPaint);
        taijiPaint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy + tr / 2, tr / 2, taijiPaint);
        canvas.restore();
        // 鱼眼
        taijiPaint.setColor(Color.WHITE);
        canvas.drawCircle(cx, cy - tr / 2, tr / 6, taijiPaint);
        taijiPaint.setColor(Color.BLACK);
        canvas.drawCircle(cx, cy + tr / 2, tr / 6, taijiPaint);
        // 金色外圈
        taijiPaint.setStyle(Paint.Style.STROKE);
        taijiPaint.setColor(0xFFE6C46A);
        taijiPaint.setStrokeWidth(tr * 0.10f);
        canvas.drawCircle(cx, cy, tr, taijiPaint);
        taijiPaint.setStyle(Paint.Style.FILL);
    }
    
    private void drawFixedPointer(Canvas canvas, int cx, int cy, float r) {
        float baseRadius = r * 0.93f;
        float tipRadius = r * 1.03f;
        float shoulderRadius = r * 0.965f;

        double upAngle = Math.toRadians(-90);
        double leftAngle = Math.toRadians(-90 - 6);
        double rightAngle = Math.toRadians(-90 + 6);

        float tipX = cx + (float) (tipRadius * Math.cos(upAngle));
        float tipY = cy + (float) (tipRadius * Math.sin(upAngle));
        float baseX = cx + (float) (baseRadius * Math.cos(upAngle));
        float baseY = cy + (float) (baseRadius * Math.sin(upAngle));
        float shoulderLeftX = cx + (float) (shoulderRadius * Math.cos(leftAngle));
        float shoulderLeftY = cy + (float) (shoulderRadius * Math.sin(leftAngle));
        float shoulderRightX = cx + (float) (shoulderRadius * Math.cos(rightAngle));
        float shoulderRightY = cy + (float) (shoulderRadius * Math.sin(rightAngle));

        Path arrowPath = new Path();
        arrowPath.moveTo(tipX, tipY);
        arrowPath.lineTo(shoulderLeftX, shoulderLeftY);
        arrowPath.lineTo(baseX, baseY);
        arrowPath.lineTo(shoulderRightX, shoulderRightY);
        arrowPath.close();

        canvas.drawPath(arrowPath, arrowPaint);
        canvas.drawPath(arrowPath, borderPaint);

        // 指针根部金色圆点
        taijiPaint.setStyle(Paint.Style.FILL);
        taijiPaint.setColor(0xFFE6C46A);
        canvas.drawCircle(cx, cy + r * 0.965f, r * 0.013f, taijiPaint);

        textPaint.setTextSize(r * 0.04f);
        textPaint.setColor(0xFFFF6347);
        textPaint.setFakeBoldText(true);
        float textY = cy - r * 1.12f;
        drawTextO(canvas, "北", cx, textY, textPaint);
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
    
    public void setShowTwentyFourMountains(boolean show) {
        showTwentyFourMountains = show;
        invalidate();
    }
    
    public void setShowTwelveZhi(boolean show) {
        showTwelveZhi = show;
        invalidate();
    }
    
    public void setShowEightTrigrams(boolean show) {
        showEightTrigrams = show;
        invalidate();
    }
    
    public void setShowTenGan(boolean show) {
        showTenGan = show;
        invalidate();
    }
    
    public void setShowEightDirections(boolean show) {
        showEightDirections = show;
        invalidate();
    }
    
    public void setShowNineStars(boolean show) {
        showNineStars = show;
        invalidate();
    }
    
    public void setShowEightDoors(boolean show) {
        showEightDoors = show;
        invalidate();
    }
    
    public boolean isShowTwentyFourMountains() {
        return showTwentyFourMountains;
    }
    
    public boolean isShowTwelveZhi() {
        return showTwelveZhi;
    }
    
    public boolean isShowEightTrigrams() {
        return showEightTrigrams;
    }
    
    public boolean isShowTenGan() {
        return showTenGan;
    }
    
    public boolean isShowEightDirections() {
        return showEightDirections;
    }
    
    public boolean isShowNineStars() {
        return showNineStars;
    }
    
    public boolean isShowEightDoors() {
        return showEightDoors;
    }
    
    public void resetLayers() {
        showTwentyFourMountains = true;
        showTwelveZhi = true;
        showEightTrigrams = true;
        showTenGan = true;
        showEightDirections = true;
        showNineStars = false;
        showEightDoors = false;
        invalidate();
    }
    
    private float startAngle = 0;
    private float startRotation = 0;
    private boolean isDragging = false;
    
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (getParent() != null) {
                getParent().requestDisallowInterceptTouchEvent(true);
            }
        }
        return super.dispatchTouchEvent(event);
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int centerX = getWidth() / 2;
        int centerY = getHeight() / 2;
        
        float x = event.getX();
        float y = event.getY();
        
        float dx = x - centerX;
        float dy = y - centerY;
        
        float distance = (float) Math.sqrt(dx * dx + dy * dy);
        if (distance < 10) {
            return true;
        }
        
        float currentAngle = (float) Math.toDegrees(Math.atan2(dy, dx));
        
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                isDragging = true;
                startAngle = currentAngle;
                startRotation = rotation;
                break;
                
            case MotionEvent.ACTION_MOVE:
                if (isDragging) {
                    float deltaAngle = currentAngle - startAngle;
                    
                    rotation = startRotation + deltaAngle;
                    rotation = ((rotation % 360) + 360) % 360;
                    
                    invalidate();
                    
                    if (rotationChangeListener != null) {
                        rotationChangeListener.onRotationChanged(rotation);
                    }
                }
                break;
                
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isDragging = false;
                if (getParent() != null) {
                    getParent().requestDisallowInterceptTouchEvent(false);
                }
                break;
        }
        
        return true;
    }
}
