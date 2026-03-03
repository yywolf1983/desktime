package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
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
        // 初始化冒号的画笔，使用黄色模拟辉光管效果
        colonPaint = new Paint();
        colonPaint.setColor(Color.rgb(255, 215, 0)); // 金黄色，辉光管效果
        colonPaint.setStyle(Paint.Style.FILL);
        colonPaint.setAntiAlias(true);
        
        // 初始化背景画笔
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.rgb(10, 10, 20)); // 深蓝色背景
        backgroundPaint.setStyle(Paint.Style.FILL);
        backgroundPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 获取宽度测量规格
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        
        // 计算高度，与七段数码管保持一致的宽高比
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
        
        // 计算冒号的尺寸
        float dotRadius = width * 0.15f;
        float dotMargin = width * 0.35f;
        
        // 根据当前亮度设置冒号的颜色
        int colonColor = Color.argb((int)(brightness * 255), 255, 215, 0); // 金黄色，辉光管效果
        colonPaint.setColor(colonColor);
        
        // 绘制上方的点
        canvas.drawCircle(
                width / 2,
                height / 2 - dotMargin,
                dotRadius,
                colonPaint
        );
        
        // 绘制下方的点
        canvas.drawCircle(
                width / 2,
                height / 2 + dotMargin,
                dotRadius,
                colonPaint
        );
    }

    // 设置亮度
    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, brightness));
        invalidate();
    }
}