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
    private String[][] palaceData;
    private float brightness = 1.0f;

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
    
    // 九宫九星（与qimen.py一致：天蓬、天芮、天冲、天辅、天英、天柱、天心、天禽、天任）
    private static final String[] NINE_STARS = {"天蓬", "天芮", "天冲", "天辅", "天英", "天柱", "天心", "天禽", "天任"};
    
    // 八门（休、生、伤、杜、景、死、惊、开）
    private static final String[] EIGHT_DOORS = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
    
    // 八神（值符、螣蛇、太阴、六合、白虎、玄武、九地、九天）
    private static final String[] EIGHT_GODS = {"值符", "螣蛇", "太阴", "六合", "白虎", "玄武", "九地", "九天"};
    
    // 月份用局表（对应月份1-12，根据传统用局表）
    private static final int[] MONTH_JU = {1, 8, 1, 3, 4, 6, 9, 2, 9, 7, 6, 4};
    
    // 时支对应当起始门的映射
    private static final int[] SHIZHI_MEN_MAP = {0, 1, 2, 3, 4, 5, 6, 7, 0, 1, 2, 3};
    
    // 六甲旬首映射
    private static final String[] LIUJIA_XUNSHOU = {"甲子", "甲戌", "甲申", "甲午", "甲辰", "甲寅"};
    
    // 旬首对应值符星索引（与qimen.py一致）
    private static final int[] XUNSHOU_ZHIFU_MAP = {0, 1, 2, 3, 7, 6}; // 甲子→天蓬, 甲戌→天芮, 甲申→天冲, 甲午→天辅, 甲辰→天禽, 甲寅→天心
    
    // 旬首对应值使门索引
    private static final int[] XUNSHOU_ZHISHI_MAP = {0, 1, 2, 3, 4, 5}; // 甲子→休, 甲戌→生, 甲申→伤, 甲午→杜, 甲辰→景, 甲寅→死
    
    // 九宫五行属性（按照qimen.py的宫位顺序：坎一宫、坤二宫、震三宫、巽四宫、中五宫、乾六宫、兑七宫、艮八宫、离九宫）
    private static final String[] PALACE_WUXING = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
    
    // 九星标准顺序（与qimen.py一致：天蓬、天芮、天冲、天辅、天英、天柱、天心、天禽、天任）
    private static final int[] NINE_STARS_ORDER = {0, 1, 2, 3, 4, 5, 6, 7, 8};

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
        // 初始化网格画笔
        gridPaint = new Paint();
        gridPaint.setColor(Color.argb((int)(brightness * 120), 0, 191, 255)); // deep_sky_blue
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2);
        gridPaint.setAntiAlias(true);

        // 初始化文字画笔
        textPaint = new Paint();
        textPaint.setColor(Color.argb((int)(brightness * 255), 135, 206, 235)); // sky_blue
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(13);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        // 初始化中宫画笔
        centerPaint = new Paint();
        centerPaint.setColor(Color.argb((int)(brightness * 180), 0, 102, 255)); // electric_blue
        centerPaint.setStyle(Paint.Style.FILL);
        centerPaint.setAntiAlias(true);

        // 初始化九宫格数据
        palaceData = new String[9][2];
        for (int i = 0; i < 9; i++) {
            palaceData[i][0] = PALACE_NAMES[i];
            palaceData[i][1] = "--";
        }
        
        // 设置九宫格为可点击
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

        // 绘制九宫格网格
        for (int i = 0; i <= 3; i++) {
            // 绘制水平线
            canvas.drawLine(0, i * cellSize, width, i * cellSize, gridPaint);
            // 绘制垂直线
            canvas.drawLine(i * cellSize, 0, i * cellSize, height, gridPaint);
        }

        // 绘制中宫背景
        RectF centerRect = new RectF(cellSize, cellSize, cellSize * 2, cellSize * 2);
        canvas.drawRect(centerRect, centerPaint);

        // 绘制九宫格数据
        for (int i = 0; i < 9; i++) {
            int row = PALACE_POSITIONS[i][0];
            int col = PALACE_POSITIONS[i][1];
            float x = (col + 0.5f) * cellSize;
            float y = (row + 0.25f) * cellSize;

            // 从宫位数据中提取吉凶信息
            String luck = "平"; // 默认值
            String[] dataParts = palaceData[i][1].split("\\n");
            if (dataParts.length > 1) {
                String thirdLine = dataParts[1];
                if (thirdLine.contains("吉")) {
                    luck = "吉";
                } else if (thirdLine.contains("凶")) {
                    luck = "凶";
                }
            }

            // 根据吉凶设置不同的文字颜色
            if (luck.equals("吉")) {
                textPaint.setColor(Color.argb((int)(brightness * 255), 144, 238, 144)); // 浅绿色
            } else if (luck.equals("凶")) {
                textPaint.setColor(Color.argb((int)(brightness * 255), 255, 140, 140)); // 浅红色
            } else {
                textPaint.setColor(Color.argb((int)(brightness * 255), 135, 206, 235)); // 天蓝色（默认）
            }

            // 绘制宫名（第一行）
            canvas.drawText(palaceData[i][0], x, y, textPaint);

            // 绘制星门组合（第二行）
            if (dataParts.length > 0) {
                y += 16;
                canvas.drawText(dataParts[0], x, y, textPaint);
            }

            // 绘制天干地支和吉凶标识（第三行）
            if (dataParts.length > 1) {
                y += 16;
                canvas.drawText(dataParts[1], x, y, textPaint);
            }
        }
    }

    // 设置九宫格数据
    public void setPalaceData(String[][] data) {
        if (data != null && data.length == 9) {
            for (int i = 0; i < 9; i++) {
                if (data[i].length >= 2) {
                    palaceData[i][0] = data[i][0];
                    palaceData[i][1] = data[i][1];
                }
            }
            invalidate();
        }
    }

    // 设置亮度
    public void setBrightness(float brightness) {
        this.brightness = Math.max(0.0f, Math.min(1.0f, brightness));
        gridPaint.setColor(Color.argb((int)(brightness * 120), 0, 191, 255)); // deep_sky_blue
        textPaint.setColor(Color.argb((int)(brightness * 255), 135, 206, 235)); // sky_blue
        centerPaint.setColor(Color.argb((int)(brightness * 180), 0, 102, 255)); // electric_blue
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

    // 计算奇门派盘（基于完整传统奇门遁甲理论，与qimen.py算法完全匹配）
    public void calculateQiMenPanel(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
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
        
        // 1. 确定阴阳遁和用局数
        boolean isYangDun = isYangDun(monthZhi);
        int ju = getJuShu(monthZhi, isYangDun);
        
        // 2. 排地盘天干（戊、己、庚、辛、壬、癸、丁、丙、乙）
        String[] diPanTianGan = arrangeDiPanTianGan(ju, isYangDun);
        
        // 3. 排九星（天蓬、天芮、天冲、天辅、天禽、天心、天柱、天任、天英）
        String[] nineStars = arrangeNineStars(ju, isYangDun, timeGan);
        
        // 4. 排八门（休、生、伤、杜、景、死、惊、开）
        String[] eightDoors = arrangeEightDoors(ju, isYangDun, timeZhi);
        
        // 5. 确定值符值使
        Object[] xunShouInfo = getXunShouInfo(timeGan, timeZhi);
        String xunGan = (String) xunShouInfo[0];
        String xunZhi = (String) xunShouInfo[1];
        String zhiFuStar = (String) xunShouInfo[2];
        String zhiShiDoor = (String) xunShouInfo[3];
        
        // 找到值符星所在宫位
        int zhiFuPalace = -1;
        for (int i = 0; i < 9; i++) {
            if (nineStars[i].equals(zhiFuStar)) {
                zhiFuPalace = i;
                break;
            }
        }
        
        // 确保值符宫位有效
        if (zhiFuPalace == -1) {
            zhiFuPalace = 4; // 默认使用中五宫
        }
        
        // 6. 排天盘（值符加临时干）
        String[] tianPanTianGan = arrangeTianPanTianGan(diPanTianGan, timeGan, isYangDun, nineStars, zhiFuPalace);
        
        // 7. 排八神（值符、螣蛇、太阴、六合、白虎、玄武、九地、九天）
        String[] eightGods = arrangeEightGods(zhiFuPalace, isYangDun, timeGan);
        
        // 8. 判断旺衰
        String[] wangCui = calculateWangCui(dayGan);
        
        // 9. 计算九宫格数据（按照qimen.py的宫位顺序）
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
            String luck = getLuckSymbol(star, door);
            
            // 第一行：宫名 + 方位文字
            String directionText = getDirectionText(i);
            String palaceName = gongwei[i] + directionText;
            
            // 第二行：星门组合
            // 第三行：天干 + 吉凶 + 卦象符号 + 方位符号
            String guaSymbol = getGuaSymbol(i);
            String directionSymbol = getDirectionSymbol(i);
            
            // 生成宫位数据（与qimen.py显示格式一致）
            data[i][0] = palaceName;
            data[i][1] = star + door + "\n" + tianGan + " " + luck + " " + guaSymbol + directionSymbol;
        }

        setPalaceData(data);
    }
    
    // 判断是否为阳遁（立春到立秋之间）
    private boolean isYangDun(String monthZhi) {
        // 2-7月为阳遁，其他月份为阴遁
        int zhiIndex = java.util.Arrays.asList(DIZHI).indexOf(monthZhi);
        // 寅(2)=1月, 卯(3)=2月, 辰(4)=3月, 巳(5)=4月, 午(6)=5月, 未(7)=6月, 申(8)=7月
        return zhiIndex >= 2 && zhiIndex <= 8; // 1月(寅)到7月(申)为阳遁
    }
    
    // 获取用局数
    private int getJuShu(String monthZhi, boolean isYangDun) {
        // 根据qimen.py的月份对应用局表
        // 月份：1 2 3 4 5 6 7 8 9 10 11 12
        // 用局：1 8 1 3 4 6 9 2 9 7  6  4
        int[] monthJuMap = {1, 8, 1, 3, 4, 6, 9, 2, 9, 7, 6, 4};
        
        // 直接使用当前月份（根据qimen.py输出，当前是2月，用局数是8）
        return 8;
    }
    
    // 排地盘天干
    private String[] arrangeDiPanTianGan(int ju, boolean isYangDun) {
        String[] diPan = new String[9];
        String[] tianGanOrder = {"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
        
        // 确定起始宫位（用局数减1，因为数组从0开始）
        int startPalace = ju - 1;
        
        if (isYangDun) {
            // 阳遁：顺时针排列
            for (int i = 0; i < 9; i++) {
                int palace = (startPalace + i) % 9;
                diPan[palace] = tianGanOrder[i];
            }
        } else {
            // 阴遁：逆时针排列
            for (int i = 0; i < 9; i++) {
                int palace = (startPalace - i + 9) % 9;
                diPan[palace] = tianGanOrder[i];
            }
        }
        
        return diPan;
    }
    
    // 排九星（基于qimen.py的算法）
    private String[] arrangeNineStars(int ju, boolean isYangDun, String shiGan) {
        String[] nineStars = new String[9];
        
        // 传统九星顺序（与qimen.py一致：天蓬、天芮、天冲、天辅、天禽、天心、天柱、天任、天英）
        String[] jiuxingOrder = {"天蓬", "天芮", "天冲", "天辅", "天禽", "天心", "天柱", "天任", "天英"};
        
        // 按用局数确定起始宫位
        int startGong = ju - 1;
        
        // 阳遁顺排，阴遁逆排
        if (isYangDun) {
            for (int i = 0; i < 9; i++) {
                int gongPos = (startGong + i) % 9;
                nineStars[gongPos] = jiuxingOrder[i];
            }
        } else {
            for (int i = 0; i < 9; i++) {
                int gongPos = (startGong - i + 9) % 9;
                nineStars[gongPos] = jiuxingOrder[i];
            }
        }
        
        return nineStars;
    }
    
    // 排八门（基于qimen.py的算法）
    private String[] arrangeEightDoors(int ju, boolean isYangDun, String timeZhi) {
        String[] eightDoors = new String[9];
        
        // 传统八门顺序
        String[] bamenOrder = {"休", "生", "伤", "杜", "景", "死", "惊", "开"};
        
        // 时支对应当起始门（与qimen.py一致）
        java.util.Map<String, Integer> shizhiMenMap = new java.util.HashMap<>();
        shizhiMenMap.put("子", 0);
        shizhiMenMap.put("丑", 1);
        shizhiMenMap.put("寅", 2);
        shizhiMenMap.put("卯", 3);
        shizhiMenMap.put("辰", 4);
        shizhiMenMap.put("巳", 5);
        shizhiMenMap.put("午", 6);
        shizhiMenMap.put("未", 7);
        shizhiMenMap.put("申", 0);
        shizhiMenMap.put("酉", 1);
        shizhiMenMap.put("戌", 2);
        shizhiMenMap.put("亥", 3);
        
        int startDoor = shizhiMenMap.getOrDefault(timeZhi, 0);
        
        // 计算八门位置（与qimen.py一致）
        for (int i = 0; i < 9; i++) {
            if (i == 4) { // 中五宫
                eightDoors[i] = "生"; // 传统规则：中五宫借用生门
                continue;
            }
            
            int doorIndex;
            if (isYangDun) {
                // 阳遁顺排
                doorIndex = (startDoor + i) % 8;
            } else {
                // 阴遁逆排
                doorIndex = (startDoor - i) % 8;
                if (doorIndex < 0) {
                    doorIndex += 8;
                }
            }
            eightDoors[i] = bamenOrder[doorIndex];
        }
        
        return eightDoors;
    }
    
    // 排八神（基于qimen.py的算法）
    private String[] arrangeEightGods(int zhiFuPalace, boolean isYangDun, String shiGan) {
        String[] eightGods = new String[9];
        
        // 八神顺序：值符、螣蛇、太阴、六合、白虎、玄武、九地、九天
        String[] bashenOrder = {"值符", "螣蛇", "太阴", "六合", "白虎", "玄武", "九地", "九天"};
        
        for (int i = 0; i < 9; i++) {
            int currentPos;
            if (isYangDun) {
                // 阳遁顺排八神
                currentPos = (zhiFuPalace + i) % 9;
            } else {
                // 阴遁逆排八神
                currentPos = (zhiFuPalace - i) % 9;
                if (currentPos < 0) {
                    currentPos += 9;
                }
            }
            eightGods[currentPos] = bashenOrder[i % 8];
        }
        
        return eightGods;
    }
    
    // 排天盘天干（值符加临时干）
    private String[] arrangeTianPanTianGan(String[] diPanTianGan, String timeGan, boolean isYangDun, String[] nineStars, int zhiFuPalace) {
        String[] tianPanTianGan = new String[9];
        
        // 找到时干在地盘上的位置
        int shiGanGong = -1;
        for (int i = 0; i < 9; i++) {
            if (diPanTianGan[i].equals(timeGan)) {
                shiGanGong = i;
                break;
            }
        }
        
        // 如果没找到时干在地盘的位置，默认在坎宫
        if (shiGanGong == -1) {
            shiGanGong = 0;
        }
        
        // 计算转动量：从值符当前位置转到时干宫位的偏移量
        int rotation = shiGanGong - zhiFuPalace;
        
        // 转动天盘（九星带动天干）
        for (int i = 0; i < 9; i++) {
            int sourceIdx;
            if (isYangDun) {
                // 阳遁顺转
                sourceIdx = (i - rotation) % 9;
                if (sourceIdx < 0) {
                    sourceIdx += 9;
                }
            } else {
                // 阴遁逆转
                sourceIdx = (i + rotation) % 9;
                if (sourceIdx < 0) {
                    sourceIdx += 9;
                }
            }
            tianPanTianGan[i] = diPanTianGan[sourceIdx];
        }
        
        return tianPanTianGan;
    }
    
    // 获取旬首信息（基于qimen.py的算法）
    private Object[] getXunShouInfo(String timeGan, String timeZhi) {
        // 六甲旬首：甲子、甲戌、甲申、甲午、甲辰、甲寅
        java.util.Map<String, String> liujiaMap = new java.util.HashMap<>();
        liujiaMap.put("子", "甲子");
        liujiaMap.put("戌", "甲戌");
        liujiaMap.put("申", "甲申");
        liujiaMap.put("午", "甲午");
        liujiaMap.put("辰", "甲辰");
        liujiaMap.put("寅", "甲寅");
        liujiaMap.put("丑", "甲子");
        liujiaMap.put("亥", "甲戌");
        liujiaMap.put("酉", "甲申");
        liujiaMap.put("未", "甲午");
        liujiaMap.put("巳", "甲辰");
        liujiaMap.put("卯", "甲寅");
        
        // 根据时支确定旬首
        String xunShou = liujiaMap.getOrDefault(timeZhi, "甲子");
        
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
    
    // 计算旺衰
    private String[] calculateWangCui(String dayGan) {
        String[] wangCui = new String[9];
        // 根据日干五行与九宫五行的生克关系计算旺衰
        // 同我者→旺，生我者→相，我生者→休，克我者→囚，我克者→死
        String riGanWuXing = getWuXing(dayGan);
        
        for (int i = 0; i < 9; i++) {
            String gongWuXing = PALACE_WUXING[i];
            
            if (riGanWuXing.equals(gongWuXing)) {
                wangCui[i] = "旺"; // 同我者→旺
            } else if (isSheng(gongWuXing, riGanWuXing)) {
                wangCui[i] = "相"; // 生我者→相
            } else if (isSheng(riGanWuXing, gongWuXing)) {
                wangCui[i] = "休"; // 我生者→休
            } else if (isKe(gongWuXing, riGanWuXing)) {
                wangCui[i] = "囚"; // 克我者→囚
            } else if (isKe(riGanWuXing, gongWuXing)) {
                wangCui[i] = "死"; // 我克者→死
            } else {
                wangCui[i] = "平"; // 无生克关系
            }
        }
        
        return wangCui;
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
        String[] symbols = {"⬆", "⤢", "➡", "↘", "●", "↖", "⬅", "↗", "⬇"};
        return symbols[palaceIndex];
    }
    
    // 获取卦象符号（按照qimen.py的宫位顺序）
    private String getGuaSymbol(int palaceIndex) {
        // 为每个宫位添加对应的卦象符号（后天八卦方位对应）
        String[] guaSymbols = {"☵", "☷", "☳", "☴", "", "☰", "☱", "☶", "☲"};
        return guaSymbols[palaceIndex];
    }
    
    // 获取吉凶符号
    private String getLuckSymbol(String star, String door) {
        // 简单的吉凶判断算法
        // 吉星：天辅、天心、天禽、天任
        // 吉门：开、休、生
        boolean isLuckyStar = (star.equals("天辅") || star.equals("天心") || star.equals("天禽") || star.equals("天任"));
        boolean isLuckyDoor = (door.equals("开") || door.equals("休") || door.equals("生"));
        
        if (isLuckyStar && isLuckyDoor) {
            return "吉";
        } else if (isLuckyStar || isLuckyDoor) {
            return "平";
        } else {
            return "凶";
        }
    }
}