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
    

    

    
    // 九宫五行属性（按照qimen.py的宫位顺序：坎一宫、坤二宫、震三宫、巽四宫、中五宫、乾六宫、兑七宫、艮八宫、离九宫）
    private static final String[] PALACE_WUXING = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
    


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

    // 计算奇门排盘（基于正统拆补法时家转盘奇门遁甲标准算法）
    public void calculateQiMenPanel(String yearPillar, String monthPillar, String dayPillar, String timePillar, String jieqi) {
        // 基于四柱信息计算九宫格数据
        String[][] data = new String[9][2];
        
        // 提取四柱的天干地支
        String yearGan = yearPillar.substring(0, 1);
        String yearZhi = yearPillar.substring(1, 2);
        String monthGan = monthPillar.substring(0, 1);
        String monthZhi = monthPillar.substring(1, 2);
        String dayGan = dayPillar.substring(0, 1);
        String dayZhi = dayPillar.substring(1, 2);
        String timeGan = timePillar.substring(0, 1);
        String timeZhi = timePillar.substring(1, 2);
        
        // 1. 确定阴阳遁（根据节气判断）
        boolean isYangDun = isYangDunByJieqi(jieqi);
        
        // 2. 确定用局数（根据节气和三元）
        int ju = getJuShuByJieqi(jieqi);
        
        // 保存节气信息用于复制
        setCopyInfo(jieqi, ju, isYangDun);
        
        // 3. 排地盘天干（戊、己、庚、辛、壬、癸、丁、丙、乙）
        // 规则：几局 = 戊落第几宫，阳遁为正，阴遁为负
        int diPanJu = isYangDun ? ju : -ju;
        String[] diPanTianGan = arrangeDiPanTianGanStandard(diPanJu);
        
        // 4. 确定旬首、值符、值使
        Object[] xunShouInfo = getXunShouInfo(timeGan, timeZhi);
        String xunShou = (String) xunShouInfo[0] + (String) xunShouInfo[1];
        String zhiFuStar = (String) xunShouInfo[2];
        String zhiShiDoor = (String) xunShouInfo[3];
        
        // 保存旬首/值符/值使用于复制
        copyXunShou = xunShou;
        copyZhiFu = zhiFuStar;
        copyZhiShi = zhiShiDoor;
        
        // 5. 值符落宫：时干在地盘的位置
        int zhiFuPalace = getShiGanPosition(diPanTianGan, timeGan);
        
        // 6. 值使落宫：从旬首宫位顺/逆数时支步数
        int xunShouPalace = getXunShouPalace(xunShou);
        int zhiShiPalace = getZhiShiPalace(xunShouPalace, timeZhi, isYangDun);
        
        // 7. 排九星（值符随时干）
        String[] nineStars = arrangeNineStarsStandard(zhiFuStar, zhiFuPalace, isYangDun);
        
        // 8. 排八门（值使随时支）
        String[] eightDoors = arrangeEightDoorsStandard(zhiShiDoor, zhiShiPalace, isYangDun);
        
        // 9. 排天盘天干（星动仪随）
        String[] tianPanTianGan = arrangeTianPanTianGanStandard(diPanTianGan, timeGan, zhiFuPalace, isYangDun);
        
        // 10. 排八神（从值符落宫开始）
        String[] eightGods = arrangeEightGodsStandard(zhiFuPalace, isYangDun);
        
        // 11. 判断旺衰（以月令地支五行为基准）
        String[] wangCui = calculateWangCui(monthZhi);
        
        // 12. 计算九宫格数据（按照qimen.py的宫位顺序）
        String[] gongwei = {"坎", "坤", "震", "巽", "中", "乾", "兑", "艮", "离"};
        for (int i = 0; i < 9; i++) {
            // 获取当前宫位的星、门、神
            String star = nineStars[i];
            String door = eightDoors[i];
            String god = eightGods[i];
            
            // 获取地盘和天盘天干
            String diGan = diPanTianGan[i];
            String tianGan = tianPanTianGan[i];
            
            // 获取旺衰
            String wangCuiValue = wangCui[i];
            
            // 计算吉凶标识（基于星门组合）
            String luck = getLuckSymbol(star, door, god, wangCuiValue);
            
            // 第一行：宫名 + 方位符号 + 方位文字
            String directionText = getDirectionText(i);
            String directionSymbol = getDirectionSymbol(i);
            String palaceName = gongwei[i] + " " + directionSymbol + " " + directionText;
            
            // 第二行：八神 + 九星 + 八门
            // 第三行：天盘天干/地盘天干 + 吉凶 + 旺衰
            String guaSymbol = getGuaSymbol(i);
            
            // 生成宫位数据（完整奇门排盘，四行显示）
            // 第一行：宫名 + 方位符号 + 方位文字
            // 第二行：八神 + 九星
            // 第三行：天盘天干/地盘天干
            // 第四行：八门 + 吉凶 + 旺衰
            data[i][0] = palaceName;
            data[i][1] = god + " " + star + "\n" + tianGan + "/" + diGan + "\n" + (door != null && !door.isEmpty() ? door : "") + " " + luck + " " + wangCuiValue;
            
            // 存储完整数据用于复制
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
        // 默认使用春分作为节气（阳遁3局）进行计算
        calculateQiMenPanel(yearPillar, monthPillar, dayPillar, timePillar, "春分");
    }
    

    

    
    // 获取旬首信息（基于qimen.py的算法）
    private Object[] getXunShouInfo(String timeGan, String timeZhi) {
        String[] liujiazi = {
            "甲子", "乙丑", "丙寅", "丁卯", "戊辰", "己巳", "庚午", "辛未", "壬申", "癸酉",
            "甲戌", "乙亥", "丙子", "丁丑", "戊寅", "己卯", "庚辰", "辛巳", "壬午", "癸未",
            "甲申", "乙酉", "丙戌", "丁亥", "戊子", "己丑", "庚寅", "辛卯", "壬辰", "癸巳",
            "甲午", "乙未", "丙申", "丁酉", "戊戌", "己亥", "庚子", "辛丑", "壬寅", "癸卯",
            "甲辰", "乙巳", "丙午", "丁未", "戊申", "己酉", "庚戌", "辛亥", "壬子", "癸丑",
            "甲寅", "乙卯", "丙辰", "丁巳", "戊午", "己未", "庚申", "辛酉", "壬戌", "癸亥"
        };
        
        String shiGanzhi = timeGan + timeZhi;
        int shiIdx = -1;
        for (int i = 0; i < liujiazi.length; i++) {
            if (liujiazi[i].equals(shiGanzhi)) {
                shiIdx = i;
                break;
            }
        }
        
        // 计算属于哪一旬（0-5）
        String[] xunshouList = {"甲子", "甲戌", "甲申", "甲午", "甲辰", "甲寅"};
        String xunShou = "甲子";
        if (shiIdx >= 0) {
            int xunIndex = shiIdx / 10;
            xunShou = xunshouList[xunIndex];
        }
        
        String xunGan = xunShou.substring(0, 1);
        String xunZhi = xunShou.substring(1, 2);
        
        // 根据旬首确定值符星和值使门
        java.util.Map<String, String> zhiFuMap = new java.util.HashMap<>();
        zhiFuMap.put("甲子", "天蓬");
        zhiFuMap.put("甲戌", "天芮");
        zhiFuMap.put("甲申", "天冲");
        zhiFuMap.put("甲午", "天辅");
        zhiFuMap.put("甲辰", "天禽");
        zhiFuMap.put("甲寅", "天心");
        
        java.util.Map<String, String> zhiShiMap = new java.util.HashMap<>();
        zhiShiMap.put("甲子", "休");
        zhiShiMap.put("甲戌", "生");
        zhiShiMap.put("甲申", "伤");
        zhiShiMap.put("甲午", "杜");
        zhiShiMap.put("甲辰", "景");
        zhiShiMap.put("甲寅", "死");
        
        String zhiFuStar = zhiFuMap.getOrDefault(xunShou, "天蓬");
        String zhiShiDoor = zhiShiMap.getOrDefault(xunShou, "休");
        
        return new Object[]{xunGan, xunZhi, zhiFuStar, zhiShiDoor};
    }
    
    // 计算旺衰（以「月令」即节月地支五行为基准）
    private String[] calculateWangCui(String monthZhi) {
        String[] wangCui = new String[9];
        // 奇门旺衰由月令定：同我者→旺，生我者→相，我生者→休，克我者→囚，我克者→死
        // 「我」= 当前节月地支五行，而非日干
        String yueLingWuXing = getZhiWuXing(monthZhi);
        
        for (int i = 0; i < 9; i++) {
            String gongWuXing = PALACE_WUXING[i];

            // 「我」= 月令(yueLingWuXing)。五行旺相休囚死标准：
            // 同我→旺；我生者→相；生我者→休；克我者→囚；我克者→死
            // （验证：春木月 → 木旺、火相、土死、金囚、水休）
            if (yueLingWuXing.equals(gongWuXing)) {
                wangCui[i] = "旺"; // 同我者→旺（当令）
            } else if (isSheng(yueLingWuXing, gongWuXing)) {
                wangCui[i] = "相"; // 我（月令）生者→相
            } else if (isSheng(gongWuXing, yueLingWuXing)) {
                wangCui[i] = "休"; // 生我（月令）者→休
            } else if (isKe(yueLingWuXing, gongWuXing)) {
                wangCui[i] = "囚"; // 克我（月令）者→囚
            } else if (isKe(gongWuXing, yueLingWuXing)) {
                wangCui[i] = "死"; // 我（月令）克者→死
            } else {
                wangCui[i] = "平"; // 无生克关系（五行完备时不会触发）
            }
        }
        
        return wangCui;
    }
    
    // 获取地支五行（用于奇门月令旺衰）
    private String getZhiWuXing(String zhi) {
        switch (zhi) {
            case "寅": case "卯": return "木";
            case "巳": case "午": return "火";
            case "辰": case "戌": case "丑": case "未": return "土";
            case "申": case "酉": return "金";
            case "亥": case "子": return "水";
            default: return "土";
        }
    }
    
    // 获取天干五行
    private String getWuXing(String gan) {
        switch (gan) {
            case "甲":
            case "乙":
                return "木";
            case "丙":
            case "丁":
                return "火";
            case "戊":
            case "己":
                return "土";
            case "庚":
            case "辛":
                return "金";
            case "壬":
            case "癸":
                return "水";
            default:
                return "土";
        }
    }
    
    // 判断五行生克关系（a生b）
    private boolean isSheng(String a, String b) {
        return (a.equals("木") && b.equals("火")) ||
               (a.equals("火") && b.equals("土")) ||
               (a.equals("土") && b.equals("金")) ||
               (a.equals("金") && b.equals("水")) ||
               (a.equals("水") && b.equals("木"));
    }
    
    // 判断五行生克关系（a克b）
    private boolean isKe(String a, String b) {
        return (a.equals("木") && b.equals("土")) ||
               (a.equals("火") && b.equals("金")) ||
               (a.equals("土") && b.equals("水")) ||
               (a.equals("金") && b.equals("木")) ||
               (a.equals("水") && b.equals("火"));
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
    
    private String getLuckSymbol(String star, String door) {
        return getLuckSymbol(star, door, null, null);
    }
    
    private String getLuckSymbol(String star, String door, String god, String wangCui) {
        int score = 0;
        
        if (star != null) {
            if (star.equals("天辅") || star.equals("天心") || star.equals("天禽")) {
                score += 3;
            } else if (star.equals("天任")) {
                score += 2;
            } else if (star.equals("天冲")) {
                score += 1;
            } else if (star.equals("天英")) {
                score += 0;
            } else if (star.equals("天蓬") || star.equals("天芮") || star.equals("天柱")) {
                score -= 2;
            }
        }
        
        if (door != null && !door.isEmpty()) {
            if (door.equals("开") || door.equals("生")) {
                score += 3;
            } else if (door.equals("休")) {
                score += 2;
            } else if (door.equals("景")) {
                score += 1;
            } else if (door.equals("杜")) {
                score += 0;
            } else if (door.equals("惊")) {
                score -= 1;
            } else if (door.equals("伤") || door.equals("死")) {
                score -= 3;
            }
        }
        
        if (god != null && !god.isEmpty()) {
            if (god.equals("值符")) {
                score += 3;
            } else if (god.equals("九天") || god.equals("太阴") || god.equals("六合")) {
                score += 2;
            } else if (god.equals("九地")) {
                score += 0;
            } else if (god.equals("螣蛇")) {
                score -= 1;
            } else if (god.equals("白虎") || god.equals("玄武")) {
                score -= 3;
            }
        }
        
        if (wangCui != null) {
            if (wangCui.equals("旺")) {
                score += 3;
            } else if (wangCui.equals("相")) {
                score += 2;
            } else if (wangCui.equals("休")) {
                score += 0;
            } else if (wangCui.equals("囚")) {
                score -= 2;
            } else if (wangCui.equals("死")) {
                score -= 3;
            }
        }
        
        if (score >= 5) {
            return "大吉";
        } else if (score >= 3) {
            return "吉";
        } else if (score >= 1) {
            return "平吉";
        } else if (score >= -1) {
            return "平";
        } else if (score >= -3) {
            return "平凶";
        } else if (score >= -5) {
            return "凶";
        } else {
            return "大凶";
        }
    }
    
    // ==================== 标准算法方法 ====================
    
    // 根据节气判断阴阳遁
    private boolean isYangDunByJieqi(String jieqi) {
        String[] yangDunJieqi = {"冬至", "小寒", "大寒", "立春", "雨水", "惊蛰", 
                                "春分", "清明", "谷雨", "立夏", "小满", "芒种"};
        for (String jq : yangDunJieqi) {
            if (jq.equals(jieqi)) {
                return true;
            }
        }
        return false;
    }
    
    // 根据节气确定用局数
    private int getJuShuByJieqi(String jieqi) {
        java.util.Map<String, Integer> jieqiJuMap = new java.util.HashMap<>();
        // 阳遁用局表
        jieqiJuMap.put("冬至", 1);
        jieqiJuMap.put("小寒", 2);
        jieqiJuMap.put("大寒", 3);
        jieqiJuMap.put("立春", 8);
        jieqiJuMap.put("雨水", 9);
        jieqiJuMap.put("惊蛰", 1);
        jieqiJuMap.put("春分", 3);
        jieqiJuMap.put("清明", 4);
        jieqiJuMap.put("谷雨", 5);
        jieqiJuMap.put("立夏", 4);
        jieqiJuMap.put("小满", 5);
        jieqiJuMap.put("芒种", 6);
        // 阴遁用局表
        jieqiJuMap.put("夏至", 9);
        jieqiJuMap.put("小暑", 8);
        jieqiJuMap.put("大暑", 7);
        jieqiJuMap.put("立秋", 2);
        jieqiJuMap.put("处暑", 1);
        jieqiJuMap.put("白露", 9);
        jieqiJuMap.put("秋分", 7);
        jieqiJuMap.put("寒露", 6);
        jieqiJuMap.put("霜降", 5);
        jieqiJuMap.put("立冬", 6);
        jieqiJuMap.put("小雪", 5);
        jieqiJuMap.put("大雪", 4);
        
        return jieqiJuMap.getOrDefault(jieqi, 1);
    }
    
    // 排地盘天干（标准算法）
    // 传统规则：阳遁n局，戊落第n宫，顺时针排布；阴遁n局，戊落第(10-n)宫，逆时针排布
    private String[] arrangeDiPanTianGanStandard(int ju) {
        String[] result = new String[9];
        String[] tianGanOrder = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
        
        // 确定戊的起始位置
        // 阳遁：1局→坎一宫(0), 2局→坤二宫(1), ..., 9局→离九宫(8)
        // 阴遁：1局→离九宫(8), 2局→艮八宫(7), ..., 9局→坎一宫(0)
        int startPos = ju - 1;
        
        // 阳遁顺时针排布，阴遁逆时针排布（通过调用者传入的ju已经考虑了阴阳遁差异）
        // ju为正时是阳遁，ju为负时是阴遁
        if (ju > 0) {
            // 阳遁：顺时针
            for (int i = 0; i < 9; i++) {
                int pos = (startPos + i) % 9;
                result[pos] = tianGanOrder[i];
            }
        } else {
            // 阴遁：逆时针
            int yinJu = -ju;
            startPos = 9 - yinJu;
            for (int i = 0; i < 9; i++) {
                int pos = (startPos - i + 9) % 9;
                result[pos] = tianGanOrder[i];
            }
        }
        
        return result;
    }
    
    // 获取时干在地盘的位置
    private int getShiGanPosition(String[] diPan, String shiGan) {
        for (int i = 0; i < 9; i++) {
            if (diPan[i].equals(shiGan)) {
                return i;
            }
        }
        return 0; // 默认坎一宫
    }
    
    // 获取旬首所在宫位（六甲遁）
    private int getXunShouPalace(String xunShou) {
        java.util.Map<String, Integer> xunShouPalaceMap = new java.util.HashMap<>();
        // 六甲遁：甲子→戊(坎一)、甲戌→己(坤二)、甲申→庚(震三)
        //        甲午→辛(巽四)、甲辰→壬(中五)、甲寅→癸(乾六)
        xunShouPalaceMap.put("甲子", 0);
        xunShouPalaceMap.put("甲戌", 1);
        xunShouPalaceMap.put("甲申", 2);
        xunShouPalaceMap.put("甲午", 3);
        xunShouPalaceMap.put("甲辰", 4);
        xunShouPalaceMap.put("甲寅", 5);
        
        return xunShouPalaceMap.getOrDefault(xunShou, 0);
    }
    
    // 计算值使落宫
    private int getZhiShiPalace(int xunShouPalace, String timeZhi, boolean isYangDun) {
        // 计算时辰数（子=1, 丑=2, ..., 亥=12）
        int zhiIndex = java.util.Arrays.asList(DIZHI).indexOf(timeZhi);
        int shiCheng = (zhiIndex + 1) % 12;
        if (shiCheng == 0) {
            shiCheng = 12;
        }
        
        // 值使落宫：阳遁顺时针数，阴遁逆时针数
        if (isYangDun) {
            return (xunShouPalace + shiCheng - 1) % 9;
        } else {
            return (xunShouPalace - shiCheng + 1 + 9) % 9;
        }
    }
    
    // 排九星（标准算法）
    private String[] arrangeNineStarsStandard(String zhiFuStar, int zhiFuPalace, boolean isYangDun) {
        String[] nineStars = new String[9];
        String[] jiuxingOrder = {"天蓬", "天芮", "天冲", "天辅", "天禽", "天心", "天柱", "天任", "天英"};
        
        // 找到值符星在九星顺序中的位置
        int zhiFuIndex = -1;
        for (int i = 0; i < jiuxingOrder.length; i++) {
            if (jiuxingOrder[i].equals(zhiFuStar)) {
                zhiFuIndex = i;
                break;
            }
        }
        if (zhiFuIndex == -1) {
            zhiFuIndex = 0;
        }
        
        // 从值符落宫开始排布九星
        if (isYangDun) {
            // 阳遁顺时针
            for (int i = 0; i < 9; i++) {
                int pos = (zhiFuPalace + i) % 9;
                nineStars[pos] = jiuxingOrder[(zhiFuIndex + i) % 9];
            }
        } else {
            // 阴遁逆时针
            for (int i = 0; i < 9; i++) {
                int pos = (zhiFuPalace - i + 9) % 9;
                nineStars[pos] = jiuxingOrder[(zhiFuIndex + i) % 9];
            }
        }
        
        return nineStars;
    }
    
    // 排八门（标准算法）
    private String[] arrangeEightDoorsStandard(String zhiShiDoor, int zhiShiPalace, boolean isYangDun) {
        String[] eightDoors = new String[9];
        String[] bamenOrder = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
        
        // 找到值使门在八门顺序中的位置
        int zhiShiIndex = -1;
        for (int i = 0; i < bamenOrder.length; i++) {
            if (bamenOrder[i].equals(zhiShiDoor)) {
                zhiShiIndex = i;
                break;
            }
        }
        if (zhiShiIndex == -1) {
            zhiShiIndex = 0;
        }
        
        // 从值使落宫开始排布八门
        int currentDoorIndex = zhiShiIndex;
        for (int i = 0; i < 9; i++) {
            int pos;
            if (isYangDun) {
                pos = (zhiShiPalace + i) % 9;
            } else {
                pos = (zhiShiPalace - i + 9) % 9;
            }
            
            if (pos == 4) {
                eightDoors[pos] = ""; // 中五宫无门
            } else {
                eightDoors[pos] = bamenOrder[currentDoorIndex];
                currentDoorIndex = (currentDoorIndex + 1) % 8;
            }
        }
        
        return eightDoors;
    }
    
    // 排天盘天干（标准算法）
    private String[] arrangeTianPanTianGanStandard(String[] diPan, String timeGan, int zhiFuPalace, boolean isYangDun) {
        String[] tianPan = new String[9];
        String[] tianGanOrder = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
        
        // 找到时干在天盘顺序中的位置
        int shiGanIndex = -1;
        for (int i = 0; i < tianGanOrder.length; i++) {
            if (tianGanOrder[i].equals(timeGan)) {
                shiGanIndex = i;
                break;
            }
        }
        if (shiGanIndex == -1) {
            shiGanIndex = 0;
        }
        
        // 从值符落宫开始排布天盘
        if (isYangDun) {
            for (int i = 0; i < 9; i++) {
                int pos = (zhiFuPalace + i) % 9;
                tianPan[pos] = tianGanOrder[(shiGanIndex + i) % 9];
            }
        } else {
            for (int i = 0; i < 9; i++) {
                int pos = (zhiFuPalace - i + 9) % 9;
                tianPan[pos] = tianGanOrder[(shiGanIndex + i) % 9];
            }
        }
        
        return tianPan;
    }
    
    // 排八神（标准算法）
    private String[] arrangeEightGodsStandard(int zhiFuPalace, boolean isYangDun) {
        String[] eightGods = new String[9];
        String[] yangShenOrder = {"值符", "螣蛇", "太阴", "六合", "白虎", "玄武", "九地", "九天"};
        String[] yinShenOrder = {"值符", "九天", "九地", "玄武", "白虎", "六合", "太阴", "螣蛇"};
        
        String[] bashenOrder = isYangDun ? yangShenOrder : yinShenOrder;
        
        if (isYangDun) {
            int currentGodIndex = 0;
            int pos = zhiFuPalace;
            while (currentGodIndex < 8) {
                if (pos != 4) {
                    eightGods[pos] = bashenOrder[currentGodIndex];
                    currentGodIndex++;
                }
                pos = (pos + 1) % 9;
            }
        } else {
            int currentGodIndex = 0;
            int pos = zhiFuPalace;
            while (currentGodIndex < 8) {
                if (pos != 4) {
                    eightGods[pos] = bashenOrder[currentGodIndex];
                    currentGodIndex++;
                }
                pos = (pos - 1 + 9) % 9;
            }
        }
        eightGods[4] = "";
        return eightGods;
    }
}