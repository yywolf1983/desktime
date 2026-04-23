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
    private boolean[] segments = new boolean[7];

    private static final boolean[][] DIGIT_PATTERNS = {
        {true, true, true, true, true, true, false},
        {false, true, true, false, false, false, false},
        {true, true, false, true, true, false, true},
        {true, true, true, true, false, false, true},
        {false, true, true, false, false, true, true},
        {true, false, true, true, false, true, true},
        {true, false, true, true, true, true, true},
        {true, true, true, false, false, false, false},
        {true, true, true, true, true, true, true},
        {true, true, true, true, false, true, true}
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
        segmentPaint = new Paint();
        segmentPaint.setColor(Color.rgb(180, 180, 180));
        segmentPaint.setStyle(Paint.Style.FILL);
        segmentPaint.setAntiAlias(true);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.TRANSPARENT);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);

        outlinePaint = new Paint();
        outlinePaint.setColor(Color.argb(20, 120, 120, 120));
        outlinePaint.setStyle(Paint.Style.STROKE);
        outlinePaint.setStrokeWidth(2);
        outlinePaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        
        if (heightMode == MeasureSpec.EXACTLY) {
            setMeasuredDimension(widthSize, heightSize);
        } else {
            int calculatedHeight = (int) (widthSize * 1.8f);
            setMeasuredDimension(widthSize, calculatedHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getWidth();
        int height = getHeight();

        canvas.drawRect(0, 0, width, height, backgroundPaint);

        float segmentWidth = width * 0.15f;
        float segmentMargin = width * 0.05f;
        float segmentRadius = segmentWidth * 0.2f;

        drawSegmentsOutline(canvas, width, height, segmentWidth, segmentMargin, segmentRadius);

        int segmentColor = Color.argb((int)(brightness * 255), 180, 180, 180);
        segmentPaint.setColor(segmentColor);

        if (segments[0]) {
            RectF topSegment = new RectF(
                    segmentMargin + segmentWidth,
                    segmentMargin,
                    width - segmentMargin - segmentWidth,
                    segmentMargin + segmentWidth
            );
            canvas.drawRoundRect(topSegment, segmentRadius, segmentRadius, segmentPaint);
        }

        if (segments[1]) {
            RectF topRightSegment = new RectF(
                    width - segmentMargin - segmentWidth,
                    segmentMargin + segmentWidth,
                    width - segmentMargin,
                    height / 2 - segmentMargin
            );
            canvas.drawRoundRect(topRightSegment, segmentRadius, segmentRadius, segmentPaint);
        }

        if (segments[2]) {
            RectF bottomRightSegment = new RectF(
                    width - segmentMargin - segmentWidth,
                    height / 2 + segmentMargin,
                    width - segmentMargin,
                    height - segmentMargin - segmentWidth
            );
            canvas.drawRoundRect(bottomRightSegment, segmentRadius, segmentRadius, segmentPaint);
        }

        if (segments[3]) {
            RectF bottomSegment = new RectF(
                    segmentMargin + segmentWidth,
                    height - segmentMargin - segmentWidth,
                    width - segmentMargin - segmentWidth,
                    height - segmentMargin
            );
            canvas.drawRoundRect(bottomSegment, segmentRadius, segmentRadius, segmentPaint);
        }

        if (segments[4]) {
            RectF bottomLeftSegment = new RectF(
                    segmentMargin,
                    height / 2 + segmentMargin,
                    segmentMargin + segmentWidth,
                    height - segmentMargin - segmentWidth
            );
            canvas.drawRoundRect(bottomLeftSegment, segmentRadius, segmentRadius, segmentPaint);
        }

        if (segments[5]) {
            RectF topLeftSegment = new RectF(
                    segmentMargin,
                    segmentMargin + segmentWidth,
                    segmentMargin + segmentWidth,
                    height / 2 - segmentMargin
            );
            canvas.drawRoundRect(topLeftSegment, segmentRadius, segmentRadius, segmentPaint);
        }

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

    private void drawSegmentsOutline(Canvas canvas, int width, int height, float segmentWidth, float segmentMargin, float segmentRadius) {
        RectF topSegment = new RectF(
                segmentMargin + segmentWidth,
                segmentMargin,
                width - segmentMargin - segmentWidth,
                segmentMargin + segmentWidth
        );
        canvas.drawRoundRect(topSegment, segmentRadius, segmentRadius, outlinePaint);

        RectF topRightSegment = new RectF(
                width - segmentMargin - segmentWidth,
                segmentMargin + segmentWidth,
                width - segmentMargin,
                height / 2 - segmentMargin
        );
        canvas.drawRoundRect(topRightSegment, segmentRadius, segmentRadius, outlinePaint);

        RectF bottomRightSegment = new RectF(
                width - segmentMargin - segmentWidth,
                height / 2 + segmentMargin,
                width - segmentMargin,
                height - segmentMargin - segmentWidth
        );
        canvas.drawRoundRect(bottomRightSegment, segmentRadius, segmentRadius, outlinePaint);

        RectF bottomSegment = new RectF(
                segmentMargin + segmentWidth,
                height - segmentMargin - segmentWidth,
                width - segmentMargin - segmentWidth,
                height - segmentMargin
        );
        canvas.drawRoundRect(bottomSegment, segmentRadius, segmentRadius, outlinePaint);

        RectF bottomLeftSegment = new RectF(
                segmentMargin,
                height / 2 + segmentMargin,
                segmentMargin + segmentWidth,
                height - segmentMargin - segmentWidth
        );
        canvas.drawRoundRect(bottomLeftSegment, segmentRadius, segmentRadius, outlinePaint);

        RectF topLeftSegment = new RectF(
                segmentMargin,
                segmentMargin + segmentWidth,
                segmentMargin + segmentWidth,
                height / 2 - segmentMargin
        );
        canvas.drawRoundRect(topLeftSegment, segmentRadius, segmentRadius, outlinePaint);

        RectF middleSegment = new RectF(
                segmentMargin + segmentWidth,
                height / 2 - segmentWidth / 2,
                width - segmentMargin - segmentWidth,
                height / 2 + segmentWidth / 2
        );
        canvas.drawRoundRect(middleSegment, segmentRadius, segmentRadius, outlinePaint);
    }

    public void setDigit(int digit) {
        if (digit >= 0 && digit <= 9) {
            this.digit = digit;
            updateSegments();
            invalidate();
        }
    }

    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, brightness));
        invalidate();
    }

    private void updateSegments() {
        if (digit >= 0 && digit <= 9) {
            segments = DIGIT_PATTERNS[digit].clone();
        }
    }
}
