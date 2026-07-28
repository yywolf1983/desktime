package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

public class DirectionNinePalaceView extends View {
    
    private Paint borderPaint = new Paint();
    private Paint textPaint = new Paint();
    
    private String[][] palaceData = new String[9][3];
    private String[] luckData = new String[9];
    private String[] directions = new String[9];
    private String[] wuxingData = new String[9];
    
    private static final int[][] PALACE_POSITIONS = {
        {0, 1}, {2, 0}, {1, 2}, {2, 2}, {1, 1}, {0, 0}, {1, 0}, {0, 2}, {2, 1}
    };
    
    private static final String[] PALACE_NAMES = {
        "坎一宫", "坤二宫", "震三宫", "巽四宫", "中五宫", "乾六宫", "兑七宫", "艮八宫", "离九宫"
    };
    
    private static final String[] BASE_DIRECTIONS = {
        "北", "西南", "东", "东南", "", "西北", "西", "东北", "南"
    };
    
    private static final int COLOR_BG_CARD = 0xFF191C26;
    private static final int COLOR_BG_PRIMARY = 0xFF0F1219;
    private static final int COLOR_GOLD = 0xFFE6C46A;
    private static final int COLOR_GREEN = 0xFF3FA34D;
    private static final int COLOR_RED = 0xFFE0593B;
    private static final int COLOR_BLUE = 0xFF3E87C2;
    private static final int COLOR_BORDER = 0xFF262A36;
    
    private float rotation = 0f;
    
    public DirectionNinePalaceView(Context context) {
        super(context);
        init();
    }
    
    public DirectionNinePalaceView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    private void init() {
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        
        for (int i = 0; i < 9; i++) {
            palaceData[i][0] = PALACE_NAMES[i];
            palaceData[i][1] = "";
            palaceData[i][2] = "";
            luckData[i] = "平";
            directions[i] = BASE_DIRECTIONS[i];
            wuxingData[i] = "";
        }
    }
    
    public void setRotation(float rotation) {
        this.rotation = rotation;
        invalidate();
    }
    
    public void setPalaceData(String[][] data) {
        if (data != null) {
            for (int i = 0; i < Math.min(data.length, 9); i++) {
                if (data[i] != null && data[i].length >= 3) {
                    palaceData[i][0] = data[i][0];
                    palaceData[i][1] = data[i][1];
                    palaceData[i][2] = data[i][2];
                } else if (data[i] != null && data[i].length >= 2) {
                    palaceData[i][0] = data[i][0];
                    palaceData[i][1] = data[i][1];
                }
            }
        }
        invalidate();
    }
    
    public void setWuxingData(String[] wuxing) {
        if (wuxing != null) {
            for (int i = 0; i < Math.min(wuxing.length, 9); i++) {
                wuxingData[i] = wuxing[i];
            }
        }
        invalidate();
    }
    
    public void setLuckData(String[] luck) {
        if (luck != null) {
            for (int i = 0; i < Math.min(luck.length, 9); i++) {
                luckData[i] = luck[i];
            }
        }
        invalidate();
    }
    
    public void setDirections(String[] dirs) {
        if (dirs != null) {
            for (int i = 0; i < Math.min(dirs.length, 9); i++) {
                directions[i] = dirs[i];
            }
        }
        invalidate();
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        
        int width = getWidth();
        int height = getHeight();
        int centerX = width / 2;
        int centerY = height / 2;
        
        canvas.save();
        canvas.rotate(rotation, centerX, centerY);
        
        float padding = 8f;
        int cellSize = (int)((Math.min(width, height) - padding * 2) / 3.0);
        float offsetX = (width - cellSize * 3) / 2;
        float offsetY = (height - cellSize * 3) / 2;
        float innerPadding = 4f;
        float radius = 6f;
        
        for (int i = 0; i < 9; i++) {
            int row = PALACE_POSITIONS[i][0];
            int col = PALACE_POSITIONS[i][1];
            
            float left = offsetX + col * cellSize + innerPadding;
            float top = offsetY + row * cellSize + innerPadding;
            float right = offsetX + (col + 1) * cellSize - innerPadding;
            float bottom = offsetY + (row + 1) * cellSize - innerPadding;
            
            String luck = luckData[i] != null ? luckData[i] : "平";
            if (i == 4) {
                borderPaint.setColor(COLOR_GOLD);
            } else if (luck.equals("吉")) {
                borderPaint.setColor(COLOR_GREEN);
            } else if (luck.equals("平吉")) {
                borderPaint.setColor(Color.argb(150, 122, 154, 96));
            } else if (luck.equals("凶")) {
                borderPaint.setColor(COLOR_RED);
            } else if (luck.equals("平凶")) {
                borderPaint.setColor(Color.argb(150, 196, 123, 94));
            } else {
                borderPaint.setColor(COLOR_BORDER);
            }
            borderPaint.setStrokeWidth(2f);
            borderPaint.setStyle(Paint.Style.STROKE);
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, borderPaint);
            
            float x = offsetX + (col + 0.5f) * cellSize;
            float y = offsetY + (row + 0.16f) * cellSize;
            
            if (luck.equals("吉")) {
                textPaint.setColor(Color.argb(255, 144, 238, 144));
            } else if (luck.equals("平吉")) {
                textPaint.setColor(Color.argb(200, 122, 154, 96));
            } else if (luck.equals("凶")) {
                textPaint.setColor(Color.argb(255, 255, 140, 140));
            } else if (luck.equals("平凶")) {
                textPaint.setColor(Color.argb(200, 196, 123, 94));
            } else {
                textPaint.setColor(COLOR_BLUE);
            }
            textPaint.setTextSize(cellSize * 0.15f);
            String dir = directions[i] != null ? directions[i] : "";
            canvas.drawText(dir, x, y, textPaint);
            
            y += cellSize * 0.20f;
            textPaint.setTextSize(cellSize * 0.12f);
            textPaint.setColor(Color.argb(160, 216, 212, 200));
            String palaceName = palaceData[i][0] != null ? palaceData[i][0] : "";
            canvas.drawText(palaceName, x, y, textPaint);
            
            y += cellSize * 0.20f;
            textPaint.setTextSize(cellSize * 0.14f);
            textPaint.setColor(COLOR_GOLD);
            String name1 = palaceData[i][1] != null ? palaceData[i][1] : "";
            canvas.drawText(name1, x, y, textPaint);
            
            y += cellSize * 0.20f;
            textPaint.setTextSize(cellSize * 0.11f);
            String name2 = palaceData[i][2] != null ? palaceData[i][2] : "";
            if (!name2.isEmpty()) {
                textPaint.setColor(COLOR_BLUE);
                canvas.drawText(name2, x, y, textPaint);
            } else {
                String wuxing = wuxingData[i] != null ? wuxingData[i] : "";
                if (!wuxing.isEmpty()) {
                    textPaint.setColor(COLOR_BLUE);
                    canvas.drawText(wuxing, x, y, textPaint);
                }
            }
            
            y += cellSize * 0.18f;
            textPaint.setTextSize(cellSize * 0.12f);
            if (luck.equals("吉")) {
                textPaint.setColor(Color.argb(255, 144, 238, 144));
            } else if (luck.equals("平吉")) {
                textPaint.setColor(Color.argb(200, 122, 154, 96));
            } else if (luck.equals("凶")) {
                textPaint.setColor(Color.argb(255, 255, 140, 140));
            } else if (luck.equals("平凶")) {
                textPaint.setColor(Color.argb(200, 196, 123, 94));
            } else {
                textPaint.setColor(Color.argb(180, 216, 212, 200));
            }
            canvas.drawText(luck, x, y, textPaint);
        }
        
        canvas.restore();
    }
}