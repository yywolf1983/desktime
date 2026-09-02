package com.example.timedisplay;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

public class NinePalacePanel extends View {
    private View.OnClickListener onClickListener;
    private Paint gridPaint;
    private Paint textPaint;
    private Paint centerPaint;
    private Paint bgPaint;
    private Paint borderPaint;
    private Paint tintPaint;
    private String[][] palaceData;
    private float brightness = 1.0f;
    private float scale = 1f;

    // 绘制缓存：避免在 onDraw 中每帧分配
    private android.graphics.Shader cellShader;
    private Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
    private int lastCellSize = -1;
    private float h1, h2, h3;
    private String[][] displayLines = new String[9][];
    
    // 颜色常量（参照 web 页面样式）
    private static final int COLOR_BG_CARD = 0xFF191C26;
    private static final int COLOR_BG_PRIMARY = 0xFF0F1219;
    private static final int COLOR_BORDER = 0xFF262A36;
    private static final int COLOR_GOLD = 0xFFE6C46A;
    private static final int COLOR_GREEN = 0xFF3FA34D;
    private static final int COLOR_RED = 0xFFE0593B;


    // 九宫格布局位置（按照指南针顺序：上北下南，左西右东）
    // 索引对应qimen.py的宫位顺序：0=坎一宫, 1=坤二宫, 2=震三宫, 3=巽四宫, 4=中五宫, 5=乾六宫, 6=兑七宫, 7=艮八宫, 8=离九宫
    private static final int[][] PALACE_POSITIONS = {
            {0, 1}, // 坎一宫（北方）- 第一行第二列
            {2, 0}, // 坤二宫（西南）- 第三行第一列
            {1, 2}, // 震三宫（东方）- 第二行第三列
            {2, 2}, // 巽四宫（东南）- 第三行第三列
            {1, 1}, // 中五宫（中方）- 第二行第二列
            {0, 0}, // 乾六宫（西北）- 第一行第一列
            {1, 0}, // 兑七宫（西方）- 第二行第一列
            {0, 2}, // 艮八宫（东北）- 第一行第三列
            {2, 1}  // 离九宫（南方）- 第三行第二列
    };

    // 九宫格名称（按照qimen.py的宫位顺序：坎一宫、坤二宫、震三宫、巽四宫、中五宫、乾六宫、兑七宫、艮八宫、离九宫）
    private static final String[] PALACE_NAMES = {
            "坎", "坤", "震", "巽", "中", "乾", "兑", "艮", "离"
    };
    
    // 天干地支
    private static final String[] TIANGAN = {"甲", "乙", "丙", "丁", "戊", "己", "庚", "辛", "壬", "癸"};
    private static final String[] DIZHI = {"子", "丑", "寅", "卯", "辰", "巳", "午", "未", "申", "酉", "戌", "亥"};
    

    

    

    


    public NinePalacePanel(Context context) {
        super(context);
        init();
    }

    public NinePalacePanel(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NinePalacePanel(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        gridPaint = new Paint();
        gridPaint.setColor(Color.argb((int)(brightness * 120), 160, 174, 192));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2);
        gridPaint.setAntiAlias(true);

        textPaint = new Paint();
        textPaint.setColor(Color.argb((int)(brightness * 255), 74, 144, 217));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        centerPaint = new Paint();
        centerPaint.setColor(Color.argb((int)(brightness * 200), 44, 199, 194));
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setAntiAlias(true);

        bgPaint = new Paint();
        bgPaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(3);

        tintPaint = new Paint();
        tintPaint.setStyle(Paint.Style.FILL);
        tintPaint.setAntiAlias(true);

        palaceData = new String[9][2];
        for (int i = 0; i < 9; i++) {
            palaceData[i][0] = PALACE_NAMES[i];
            palaceData[i][1] = "--";
            displayLines[i] = new String[]{"--"};
        }

        setClickable(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // 确保九宫格是正方形
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
        int cellSize = Math.min(width, height) / 3;
        float padding = 8f;
        float radius = 10f;

        if (cellSize != lastCellSize) {
            computeLayout();
            lastCellSize = cellSize;
        }

        textPaint.setTextSize(cellSize * 0.15f);

        for (int i = 0; i < 9; i++) {
            int row = PALACE_POSITIONS[i][0];
            int col = PALACE_POSITIONS[i][1];
            
            float left = col * cellSize + padding;
            float top = row * cellSize + padding;
            float right = (col + 1) * cellSize - padding;
            float bottom = (row + 1) * cellSize - padding;

            String luck = "平";
            if (copyPalaceData != null && copyPalaceData[i][5] != null) {
                luck = copyPalaceData[i][5];
            }

            String[] dataParts = displayLines[i];

            canvas.drawRoundRect(left, top, right, bottom, radius, radius, bgPaint);

            // 吉凶淡底纹
            int[] luckStyle = getLuckStyle(luck);
            tintPaint.setColor(luckStyle[0]);
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, tintPaint);

            if (i == 4) {
                borderPaint.setColor(COLOR_GOLD);
                borderPaint.setStrokeWidth(4f);
            } else {
                borderPaint.setColor(luckStyle[1]);
                borderPaint.setStrokeWidth(3f);
            }
            canvas.drawRoundRect(left, top, right, bottom, radius, radius, borderPaint);

            float x = (col + 0.5f) * cellSize;
            float lineSpacing = cellSize * 0.02f;

            // h1/h2/h3 来自 computeLayout 缓存，避免每帧计算
            float totalHeight = h1 + lineSpacing + h2 + lineSpacing + h2 + lineSpacing + h3;
            float topPadding = Math.max((cellSize - totalHeight) / 2, cellSize * 0.05f);

            float y = (row + topPadding / cellSize) * cellSize - fontMetrics.ascent;

            textPaint.setTextSize(cellSize * 0.15f);
            textPaint.setColor(i == 4 ? COLOR_GOLD : 0xFFE6E6E6);
            canvas.drawText(palaceData[i][0], x, y, textPaint);

            if (dataParts.length > 0) {
                y += h1 + lineSpacing;
                textPaint.setTextSize(cellSize * 0.13f);
                canvas.drawText(dataParts[0], x, y, textPaint);
            }

            if (dataParts.length > 1) {
                y += h2 + lineSpacing;
                textPaint.setTextSize(cellSize * 0.13f);
                canvas.drawText(dataParts[1], x, y, textPaint);
            }

            if (dataParts.length > 2) {
                y += h2 + lineSpacing;
                textPaint.setTextSize(cellSize * 0.12f);
                textPaint.setColor(luckStyle[1]);
                canvas.drawText(dataParts[2], x, y, textPaint);
            }

        }
    }

    // 仅在宫格尺寸变化时计算一次：渐变、字体度量、行高
    private void computeLayout() {
        int w = getWidth();
        int h = getHeight();
        cellShader = new android.graphics.LinearGradient(0, 0, w, h,
                COLOR_BG_CARD, COLOR_BG_PRIMARY, android.graphics.Shader.TileMode.CLAMP);
        bgPaint.setShader(cellShader);
        textPaint.setTextSize(w / 3f * 0.15f);
        textPaint.getFontMetrics(fontMetrics);
        h1 = fontMetrics.descent - fontMetrics.ascent;
        textPaint.setTextSize(w / 3f * 0.13f);
        textPaint.getFontMetrics(fontMetrics);
        h2 = fontMetrics.descent - fontMetrics.ascent;
        textPaint.setTextSize(w / 3f * 0.12f);
        textPaint.getFontMetrics(fontMetrics);
        h3 = fontMetrics.descent - fontMetrics.ascent;
    }

    // 吉凶等级 -> 颜色（与底部解释文案同源，保证一致）
    public static int getLuckColorByLabel(String label) {
        switch (label) {
            case "大吉": return 0xFF2E9E5B;
            case "吉":   return 0xFF3FA34D;
            case "平吉": return 0xFF7CA86A;
            case "平":   return 0xFF7C8C9C;
            case "平凶": return 0xFFC9873F;
            case "凶":   return 0xFFE0593B;
            case "大凶": return 0xFFC0392B;
            default:     return 0xFF7C8C9C;
        }
    }

    // 吉凶等级 -> {底纹色, 边框色}
    private int[] getLuckStyle(String label) {
        int color = getLuckColorByLabel(label);
        int tint = (color & 0x00FFFFFF) | 0x1A000000; // 约 10% 透明度
        return new int[]{tint, color};
    }

    // 值符所在宫的吉凶等级（用于底部整体解读，与九宫配色同源）
    public String getZhiFuPalaceLuck() {
        if (copyZhiFu == null) return "平";
        for (int i = 0; i < 9; i++) {
            if (copyPalaceData[i][1] != null && copyPalaceData[i][1].equals(copyZhiFu)) {
                return copyPalaceData[i][5] != null ? copyPalaceData[i][5] : "平";
            }
        }
        return "平";
    }

    // 设置九宫格数据
    public void setPalaceData(String[][] data) {
        if (data != null && data.length == 9) {
            for (int i = 0; i < 9; i++) {
                if (data[i].length >= 2) {
                    palaceData[i][0] = data[i][0];
                    palaceData[i][1] = data[i][1];
                    // 同步拆分展示行，避免在 onDraw 中每帧 split
                    displayLines[i] = (data[i][1] != null)
                            ? data[i][1].split("\n")
                            : new String[]{"--"};
                }
            }
            invalidate();
        }
    }

    private String copyJieqi = "";
    private int copyJu = 1;
    private boolean copyIsYangDun = true;
    private String copyXunShou = "";
    private String copyZhiFu = "";
    private String copyZhiShi = "";
    // 存储每宫完整的复制数据: [0]八神 [1]九星 [2]八门 [3]天盘天干 [4]地盘天干 [5]吉凶 [6]旺衰
    private String[][] copyPalaceData = new String[9][7];

    // 获取当前排盘信息的文本（用于复制）- 标准排盘格式
    public String getCopyText() {
        StringBuilder sb = new StringBuilder();
        // 九宫顺序：按宫位数排列
        String[] gongwei = {"坎一宫", "坤二宫", "震三宫", "巽四宫", "中五宫", "乾六宫", "兑七宫", "艮八宫", "离九宫"};
        String[] directions = {"北", "西南", "东", "东南", "中", "西北", "西", "东北", "南"};
        
        for (int i = 0; i < 9; i++) {
            String god = copyPalaceData[i][0] != null ? copyPalaceData[i][0] : "";
            String star = copyPalaceData[i][1] != null ? copyPalaceData[i][1] : "";
            String door = copyPalaceData[i][2] != null ? copyPalaceData[i][2] : "";
            String tianGan = copyPalaceData[i][3] != null ? copyPalaceData[i][3] : "";
            String diGan = copyPalaceData[i][4] != null ? copyPalaceData[i][4] : "";
            String luck = copyPalaceData[i][5] != null ? copyPalaceData[i][5] : "";
            String wangCuiVal = copyPalaceData[i][6] != null ? copyPalaceData[i][6] : "";
            
            // 格式化一行：宫位(方位) 八神 九星 天盘/地盘 八门 吉凶 旺衰
            sb.append(gongwei[i]).append("(").append(directions[i]).append(")");
            if (!god.isEmpty()) sb.append(" ").append(god);
            if (!star.isEmpty()) sb.append(" ").append(star);
            if (!door.isEmpty()) sb.append(" ").append(door);
            if (!tianGan.isEmpty() && !diGan.isEmpty()) {
                sb.append(" ").append(tianGan).append("/").append(diGan);
            }
            if (!luck.isEmpty()) sb.append(" ").append(luck);
            if (!wangCuiVal.isEmpty()) sb.append(" ").append(wangCuiVal);
            sb.append("\n");
        }
        return sb.toString();
    }

    // 设置复制用的节气信息
    public void setCopyInfo(String jieqi, int ju, boolean isYangDun) {
        this.copyJieqi = jieqi;
        this.copyJu = ju;
        this.copyIsYangDun = isYangDun;
    }

    public String getCopyJieqi() { return copyJieqi; }
    public int getCopyJu() { return copyJu; }
    public boolean getCopyIsYangDun() { return copyIsYangDun; }
    public String getCopyXunShou() { return copyXunShou; }
    public String getCopyZhiFu() { return copyZhiFu; }
    public String getCopyZhiShi() { return copyZhiShi; }

    // 设置亮度
    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, brightness));
        gridPaint.setColor(Color.argb((int)(brightness * 120), 160, 174, 192));
        textPaint.setColor(Color.argb((int)(brightness * 255), 74, 144, 217));
        centerPaint.setColor(Color.argb((int)(brightness * 200), 44, 199, 194));
        invalidate();
    }
    
    // 设置点击监听器
    @Override
    public void setOnClickListener(View.OnClickListener l) {
        this.onClickListener = l;
    }
    
    // 处理触摸事件
    @Override
    public boolean onTouchEvent(android.view.MotionEvent event) {
        if (event.getAction() == android.view.MotionEvent.ACTION_UP && onClickListener != null) {
            onClickListener.onClick(this);
            return true;
        }
        return super.onTouchEvent(event);
    }

    // 计算奇门排盘（统一调用 QiMenCalculator，全 app 唯一的排盘算法实现）
    public void calculateQiMenPanel(String yearPillar, String monthPillar, String dayPillar, String timePillar, String jieqi, int dayInJieqi) {
        // 基于四柱信息计算九宫格数据
        String[][] data = new String[9][2];

        // 统一调用 QiMenCalculator（修复原实现中阴阳遁地盘、天盘、旬首、三元用局等不一致问题）
        QiMenCalculator.Result r = QiMenCalculator.calculate(yearPillar, monthPillar, dayPillar, timePillar, jieqi, dayInJieqi);
        setCopyInfo(jieqi, r.ju, r.isYangDun);

        // 保存旬首/值符/值使用于复制
        copyXunShou = r.xunShou;
        copyZhiFu = r.zhiFuStar;
        copyZhiShi = r.zhiShiDoor;

        // 计算九宫格数据（按照宫位顺序）
        String[] gongwei = {"坎", "坤", "震", "巽", "中", "乾", "兑", "艮", "离"};
        for (int i = 0; i < 9; i++) {
            String star = r.nineStars[i];
            String door = r.eightDoors[i];
            String god = r.eightGods[i];
            String tianGan = r.tianPanTianGan[i];
            String diGan = r.diPanTianGan[i];
            String luck = r.luck[i];
            String wangCuiValue = r.wangCui[i];

            String directionText = getDirectionText(i);
            String directionSymbol = getDirectionSymbol(i);
            String palaceName = gongwei[i] + " " + directionSymbol + " " + directionText;
            String guaSymbol = getGuaSymbol(i);

            data[i][0] = palaceName;
            data[i][1] = god + " " + star + "\n" + tianGan + "/" + diGan + "\n" + (door != null && !door.isEmpty() ? door : "") + " " + luck + " " + wangCuiValue;

            copyPalaceData[i][0] = god;
            copyPalaceData[i][1] = star;
            copyPalaceData[i][2] = door;
            copyPalaceData[i][3] = tianGan;
            copyPalaceData[i][4] = diGan;
            copyPalaceData[i][5] = luck;
            copyPalaceData[i][6] = wangCuiValue;
        }

        setPalaceData(data);
    }
    
    // 重载方法：兼容旧接口
    public void calculateQiMenPanel(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        // 默认使用春分作为节气（阳遁3局，上元）进行计算
        calculateQiMenPanel(yearPillar, monthPillar, dayPillar, timePillar, "春分", 1);
    }
    

    

    

    

    
    // 获取方位文字（按照qimen.py的宫位顺序）
    private String getDirectionText(int palaceIndex) {
        // 为每个宫位添加对应的方位文字
        String[] directionTexts = {"北方", "西南", "东方", "东南", "中心", "西北", "西方", "东北", "南方"};
        return directionTexts[palaceIndex];
    }
    
    // 获取方位符号（按照qimen.py的宫位顺序）
    private String getDirectionSymbol(int palaceIndex) {
        // 为每个宫位添加对应的方位符号
        String[] symbols = {"↑", "↙", "→", "↘", "●", "↖", "←", "↗", "↓"};
        return symbols[palaceIndex];
    }
    
    // 获取卦象符号（按照qimen.py的宫位顺序）
    private String getGuaSymbol(int palaceIndex) {
        // 为每个宫位添加对应的卦象符号（后天八卦方位对应）
        String[] guaSymbols = {"☵", "☷", "☳", "☴", "", "☰", "☱", "☶", "☲"};
        return guaSymbols[palaceIndex];
    }
    

    

    

}