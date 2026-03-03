package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class SevenSegmentDisplay extends View {
    private Paint segmentPaint;
    private Paint backgroundPaint;
    private int digit = 0;
    private float brightness = 1.0f;
    private boolean[] segments = new boolean[7]; // 7个段的状态
    
    // 七段数码管的段定义
    // 0: 顶部, 1: 右上, 2: 右下, 3: 底部, 4: 左下, 5: 左上, 6: 中间
    private static final boolean[][] DIGIT_PATTERNS = {
        {true, true, true, true, true, true, false},  // 0
        {false, true, true, false, false, false, false}, // 1
        {true, true, false, true, true, false, true},   // 2
        {true, true, true, true, false, false, true},   // 3
        {false, true, true, false, false, true, true},  // 4
        {true, false, true, true, false, true, true},   // 5
        {true, false, true, true, true, true, true},    // 6
        {true, true, true, false, false, false, false}, // 7
        {true, true, true, true, true, true, true},     // 8
        {true, true, true, true, false, true, true}     // 9
    };

    public SevenSegmentDisplay(Context context) {
        super(context);
        init();
    }

    public SevenSegmentDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public SevenSegmentDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint outlinePaint;

    private void init() {
        // 初始化段的画笔，使用黄色模拟辉光管效果
        segmentPaint = new Paint();
        segmentPaint.setColor(Color.rgb(255, 215, 0)); // 金黄色，辉光管效果
        segmentPaint.setStyle(Paint.Style.FILL);
        segmentPaint.setAntiAlias(true);
        
        // 初始化背景画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(10, 10, 20)); // 深蓝色背景，更沉稳
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
        
        // 初始化轮廓画笔，用于绘制隐约的七段管轮廓
        outlinePaint = new Paint();
        outlinePaint.setColor(Color.argb(50, 200, 165, 0)); // 更暗淡的金黄色，保持沉稳风格
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(2);
        outlinePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取宽度测量规格
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        
        // 计算高度，增加到1.8:1的宽高比，使时间显示更高
        int heightSize = (int) (widthSize * 1.8f);
        
        // 设置测量结果
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        
        // 绘制背景
        canvas.drawRect(0, 0, width, height, backgroundPaint);
        
        // 计算段的尺寸
        float segmentWidth = width * 0.15f;
        float segmentHeight = height * 0.3f;
        float segmentMargin = width * 0.05f;
        float segmentRadius = segmentWidth * 0.2f;
        
        // 先绘制七段管的隐约轮廓
        drawSegmentsOutline(canvas, width, height, segmentWidth, segmentMargin, segmentRadius);
        
        // 根据当前亮度设置段的颜色，使用黄色模拟辉光管效果
        int segmentColor = Color.argb((int)(brightness * 255), 255, 215, 0); // 金黄色，辉光管效果
        segmentPaint.setColor(segmentColor);
        
        // 绘制各个段
        // 顶部段
        if (segments[0]) {
            RectF topSegment = new RectF(
                    segmentMargin + segmentWidth,
                    segmentMargin,
                    width - segmentMargin - segmentWidth,
                    segmentMargin + segmentWidth
            );
            canvas.drawRoundRect(topSegment, segmentRadius, segmentRadius, segmentPaint);
        }
        
        // 右上段
        if (segments[1]) {
            RectF topRightSegment = new RectF(
                    width - segmentMargin - segmentWidth,
                    segmentMargin + segmentWidth,
                    width - segmentMargin,
                    height / 2 - segmentMargin
            );
            canvas.drawRoundRect(topRightSegment, segmentRadius, segmentRadius, segmentPaint);
        }
        
        // 右下段
        if (segments[2]) {
            RectF bottomRightSegment = new RectF(
                    width - segmentMargin - segmentWidth,
                    height / 2 + segmentMargin,
                    width - segmentMargin,
                    height - segmentMargin - segmentWidth
            );
            canvas.drawRoundRect(bottomRightSegment, segmentRadius, segmentRadius, segmentPaint);
        }
        
        // 底部段
        if (segments[3]) {
            RectF bottomSegment = new RectF(
                    segmentMargin + segmentWidth,
                    height - segmentMargin - segmentWidth,
                    width - segmentMargin - segmentWidth,
                    height - segmentMargin
            );
            canvas.drawRoundRect(bottomSegment, segmentRadius, segmentRadius, segmentPaint);
        }
        
        // 左下段
        if (segments[4]) {
            RectF bottomLeftSegment = new RectF(
                    segmentMargin,
                    height / 2 + segmentMargin,
                    segmentMargin + segmentWidth,
                    height - segmentMargin - segmentWidth
            );
            canvas.drawRoundRect(bottomLeftSegment, segmentRadius, segmentRadius, segmentPaint);
        }
        
        // 左上段
        if (segments[5]) {
            RectF topLeftSegment = new RectF(
                    segmentMargin,
                    segmentMargin + segmentWidth,
                    segmentMargin + segmentWidth,
                    height / 2 - segmentMargin
            );
            canvas.drawRoundRect(topLeftSegment, segmentRadius, segmentRadius, segmentPaint);
        }
        
        // 中间段
        if (segments[6]) {
            RectF middleSegment = new RectF(
                    segmentMargin + segmentWidth,
                    height / 2 - segmentWidth / 2,
                    width - segmentMargin - segmentWidth,
                    height / 2 + segmentWidth / 2
            );
            canvas.drawRoundRect(middleSegment, segmentRadius, segmentRadius, segmentPaint);
        }
    }

    // 绘制七段管的隐约轮廓
    private void drawSegmentsOutline(Canvas canvas, int width, int height, float segmentWidth, float segmentMargin, float segmentRadius) {
        // 绘制顶部段轮廓
        RectF topSegment = new RectF(
                segmentMargin + segmentWidth,
                segmentMargin,
                width - segmentMargin - segmentWidth,
                segmentMargin + segmentWidth
        );
        canvas.drawRoundRect(topSegment, segmentRadius, segmentRadius, outlinePaint);
        
        // 绘制右上段轮廓
        RectF topRightSegment = new RectF(
                width - segmentMargin - segmentWidth,
                segmentMargin + segmentWidth,
                width - segmentMargin,
                height / 2 - segmentMargin
        );
        canvas.drawRoundRect(topRightSegment, segmentRadius, segmentRadius, outlinePaint);
        
        // 绘制右下段轮廓
        RectF bottomRightSegment = new RectF(
                width - segmentMargin - segmentWidth,
                height / 2 + segmentMargin,
                width - segmentMargin,
                height - segmentMargin - segmentWidth
        );
        canvas.drawRoundRect(bottomRightSegment, segmentRadius, segmentRadius, outlinePaint);
        
        // 绘制底部段轮廓
        RectF bottomSegment = new RectF(
                segmentMargin + segmentWidth,
                height - segmentMargin - segmentWidth,
                width - segmentMargin - segmentWidth,
                height - segmentMargin
        );
        canvas.drawRoundRect(bottomSegment, segmentRadius, segmentRadius, outlinePaint);
        
        // 绘制左下段轮廓
        RectF bottomLeftSegment = new RectF(
                segmentMargin,
                height / 2 + segmentMargin,
                segmentMargin + segmentWidth,
                height - segmentMargin - segmentWidth
        );
        canvas.drawRoundRect(bottomLeftSegment, segmentRadius, segmentRadius, outlinePaint);
        
        // 绘制左上段轮廓
        RectF topLeftSegment = new RectF(
                segmentMargin,
                segmentMargin + segmentWidth,
                segmentMargin + segmentWidth,
                height / 2 - segmentMargin
        );
        canvas.drawRoundRect(topLeftSegment, segmentRadius, segmentRadius, outlinePaint);
        
        // 绘制中间段轮廓
        RectF middleSegment = new RectF(
                segmentMargin + segmentWidth,
                height / 2 - segmentWidth / 2,
                width - segmentMargin - segmentWidth,
                height / 2 + segmentWidth / 2
        );
        canvas.drawRoundRect(middleSegment, segmentRadius, segmentRadius, outlinePaint);
    }

    // 设置显示的数字
    public void setDigit(int digit) {
        if (digit >= 0 && digit <= 9) {
            this.digit = digit;
            updateSegments();
            invalidate();
        }
    }

    // 设置亮度
    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, brightness));
        invalidate();
    }

    // 更新段的状态
    private void updateSegments() {
        if (digit >= 0 && digit <= 9) {
            segments = DIGIT_PATTERNS[digit].clone();
        }
    }
}