package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColonDisplay extends View {
    private Paint colonPaint;
    private Paint backgroundPaint;
    private float brightness = 1.0f;

    public ColonDisplay(Context context) {
        super(context);
        init();
    }

    public ColonDisplay(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ColonDisplay(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        colonPaint = new Paint();
        colonPaint.setColor(Color.rgb(180, 180, 180));
        colonPaint.setStyle(Paint.Style.FILL);
        colonPaint.setAntiAlias(true);

        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.TRANSPARENT);
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
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

        float dotRadius = width * 0.22f;
        float topDotCenter = height / 3.0f;
        float bottomDotCenter = height * 2.0f / 3.0f;

        int colonColor = Color.argb((int)(brightness * 255), 180, 180, 180);
        colonPaint.setColor(colonColor);

        canvas.drawCircle(
                width / 2,
                topDotCenter,
                dotRadius,
                colonPaint
        );

        canvas.drawCircle(
                width / 2,
                bottomDotCenter,
                dotRadius,
                colonPaint
        );
    }

    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, brightness));
        invalidate();
    }
}
