package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import java.util.Calendar;

public class WuyunLiuqiView extends View {
    private Paint paint;
    private Paint textPaint;
    private Paint highlightPaint;
    private Paint glowPaint;
    
    private int currentShichenIndex = 0;
    private String[] shichenNames = {
        "子", "丑", "寅", "卯", "辰", "巳",
        "午", "未", "申", "酉", "戌", "亥"
    };
    
    private String[] shichenTimes = {
        "23:00-01:00", "01:00-03:00", "03:00-05:00",
        "05:00-07:00", "07:00-09:00", "09:00-11:00",
        "11:00-13:00", "13:00-15:00", "15:00-17:00",
        "17:00-19:00", "19:00-21:00", "21:00-23:00"
    };
    
    private String[] shichenQuotes = {
        "夜半", "鸡鸣", "平旦", "日出", "食时", "隅中",
        "日中", "日昳", "晡时", "日入", "黄昏", "人定"
    };
    
    private String[] wuxingLabels = {
        "水·鼠", "土·牛", "木·虎", "木·兔", "土·龙", "火·蛇",
        "火·马", "土·羊", "金·猴", "金·鸡", "土·狗", "水·猪"
    };
    
    private String[] meridianLabels = {
        "胆经", "肝经", "肺经", "大肠经", "胃经", "脾经",
        "心经", "小肠经", "膀胱经", "肾经", "心包经", "三焦经"
    };

    public WuyunLiuqiView(Context context) {
        super(context);
        init();
    }

    public WuyunLiuqiView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WuyunLiuqiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        highlightPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        glowPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        
        textPaint.setTextAlign(Paint.Align.CENTER);
        
        highlightPaint.setColor(Color.parseColor("#FFD700"));
        highlightPaint.setStyle(Paint.Style.STROKE);
        highlightPaint.setStrokeWidth(3);
        
        glowPaint.setColor(Color.parseColor("#FFD700"));
        glowPaint.setStyle(Paint.Style.FILL);
        glowPaint.setAlpha(30);
    }

    public void setCurrentShichen(int index) {
        this.currentShichenIndex = index;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        int padding = 12;
        int gridPadding = 30;
        
        paint.setColor(Color.parseColor("#0A0A14"));
        paint.setAlpha(60);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRoundRect(0, 0, width, height, 14, 14, paint);
        
        String currentShichen = shichenNames[currentShichenIndex] + "时 · " + shichenQuotes[currentShichenIndex];
        
        int gridWidth = width - gridPadding * 2;
        
        float shichenTextSize = Math.max(gridWidth * 0.06f, 18f);
        textPaint.setTextSize(shichenTextSize);
        textPaint.setColor(Color.parseColor("#FFD700"));
        textPaint.setFakeBoldText(true);
        float shichenY = shichenTextSize * 0.9f;
        canvas.drawText(currentShichen, width / 2f, shichenY, textPaint);
        
        int gridTop = (int) (shichenY + shichenTextSize * 0.5f + 8);
        int bottomPadding = padding + 15;
        int gridHeight = height - gridTop - bottomPadding;
        
        float cellWidth = gridWidth / 4.0f;
        float cellHeight = gridHeight / 3.0f;
        float minCell = Math.min(cellWidth, cellHeight);
        
        float cornerRadius = 14;
        float cellPadding = 6;
        
        for (int i = 0; i < 12; i++) {
            int row = i / 4;
            int col = i % 4;
            
            float left = gridPadding + col * cellWidth;
            float top = gridTop + row * cellHeight;
            float right = left + cellWidth;
            float bottom = top + cellHeight;
            
            boolean isCurrent = i == currentShichenIndex;
            
            paint.setColor(Color.parseColor("#1a1a2e"));
            paint.setAlpha(100);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRoundRect(left + cellPadding, top + cellPadding, right - cellPadding, bottom - cellPadding, cornerRadius, cornerRadius, paint);
            
            // subtle border
            paint.setColor(Color.parseColor("#304a4a6a"));
            paint.setAlpha(80);
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(1);
            canvas.drawRoundRect(left + cellPadding, top + cellPadding, right - cellPadding, bottom - cellPadding, cornerRadius, cornerRadius, paint);
            
            if (isCurrent) {
                highlightPaint.setStrokeWidth(3);
                highlightPaint.setColor(Color.parseColor("#FFD700"));
                canvas.drawRoundRect(left + cellPadding, top + cellPadding, right - cellPadding, bottom - cellPadding, cornerRadius, cornerRadius, highlightPaint);
            }
            
            float centerX = (left + right) / 2;
            float centerY = (top + bottom) / 2;
            float availableHeight = bottom - top - cellPadding * 2;
            
            float nameSize = Math.max(minCell * 0.32f, 14f);
            textPaint.setTextSize(nameSize);
            textPaint.setColor(isCurrent ? Color.parseColor("#FFD700") : Color.parseColor("#FFD700"));
            textPaint.setFakeBoldText(true);
            canvas.drawText(shichenNames[i], centerX, centerY - availableHeight * 0.15f, textPaint);
            
            float timeSize = Math.max(minCell * 0.14f, 8f);
            textPaint.setTextSize(timeSize);
            textPaint.setColor(isCurrent ? Color.parseColor("#87CEEB") : Color.parseColor("#87CEEB"));
            textPaint.setFakeBoldText(false);
            canvas.drawText(shichenTimes[i], centerX, centerY + availableHeight * 0.03f, textPaint);
            
            float labelSize = Math.max(minCell * 0.12f, 7f);
            textPaint.setTextSize(labelSize);
            textPaint.setColor(isCurrent ? Color.parseColor("#ADD8E6") : Color.parseColor("#ADD8E6"));
            canvas.drawText(wuxingLabels[i], centerX, centerY + availableHeight * 0.20f, textPaint);
            
            textPaint.setTextSize(labelSize);
            textPaint.setColor(isCurrent ? Color.parseColor("#90EE90") : Color.parseColor("#90EE90"));
            canvas.drawText(meridianLabels[i], centerX, centerY + availableHeight * 0.35f, textPaint);
        }
    }
}