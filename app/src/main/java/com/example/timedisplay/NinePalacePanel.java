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
    private boolean isSimpleMode = false;

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
        gridPaint.setColor(Color.argb((int)(brightness * 120), 160, 174, 192));
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeWidth(2);
        gridPaint.setAntiAlias(true);

        // 初始化文字画笔
        textPaint = new Paint();
        textPaint.setColor(Color.argb((int)(brightness * 255), 74, 144, 217));
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTextSize(40);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setAntiAlias(true);
        textPaint.setFakeBoldText(true);

        // 初始化中宫画笔
        centerPaint = new Paint();
        centerPaint.setColor(Color.argb((int)(brightness * 200), 44, 199, 194));
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
            canvas.drawLine(0, i * cellSize, width, i * cellSize, gridPaint);
            canvas.drawLine(i * cellSize, 0, i * cellSize, height, gridPaint);
        }

        RectF centerRect = new RectF(cellSize, cellSize, cellSize * 2, cellSize * 2);
        canvas.drawRect(centerRect, centerPaint);

        for (int i = 0; i < 9; i++) {
            int row = PALACE_POSITIONS[i][0];
            int col = PALACE_POSITIONS[i][1];
            float x = (col + 0.5f) * cellSize;
            float y = (row + 0.35f) * cellSize;

            String luck = "平";
            String[] dataParts = palaceData[i][1].split("\\n");
            if (dataParts.length > 1) {
                String thirdLine = dataParts[1];
                if (thirdLine.contains("吉")) {
                    luck = "吉";
                } else if (thirdLine.contains("凶")) {
                    luck = "凶";
                }
            }

            if (luck.equals("吉")) {
                textPaint.setColor(Color.argb((int)(brightness * 255), 144, 238, 144));
            } else if (luck.equals("凶")) {
                textPaint.setColor(Color.argb((int)(brightness * 255), 255, 140, 140));
            } else {
                textPaint.setColor(Color.argb((int)(brightness * 255), 135, 206, 235));
            }

            canvas.drawText(palaceData[i][0], x, y, textPaint);

            if (dataParts.length > 0) {
                y += 45;
                canvas.drawText(dataParts[0], x, y, textPaint);
            }

            if (dataParts.length > 1) {
                y += 45;
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

    // 计算奇门派盘（基于正统拆补法时家转盘奇门遁甲标准算法）
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
        
        // 3. 排地盘天干（戊、己、庚、辛、壬、癸、丁、丙、乙）
        // 规则：几局 = 戊落第几宫
        String[] diPanTianGan = arrangeDiPanTianGanStandard(ju);
        
        // 4. 确定旬首、值符、值使
        Object[] xunShouInfo = getXunShouInfo(timeGan, timeZhi);
        String xunShou = (String) xunShouInfo[0] + (String) xunShouInfo[1];
        String zhiFuStar = (String) xunShouInfo[2];
        String zhiShiDoor = (String) xunShouInfo[3];
        
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
        
        // 11. 判断旺衰
        String[] wangCui = calculateWangCui(dayGan);
        
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
    
    // 重载方法：兼容旧接口
    public void calculateQiMenPanel(String yearPillar, String monthPillar, String dayPillar, String timePillar) {
        // 默认使用春分作为节气（阳遁3局）进行计算
        calculateQiMenPanel(yearPillar, monthPillar, dayPillar, timePillar, "春分");
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
        if (monthZhi == null) {
            return 1;
        }
        int zhiIndex = java.util.Arrays.asList(DIZHI).indexOf(monthZhi);
        // 直接使用月份对应传统用局表
        int[] MONTH_JU = {1, 8, 1, 3, 4, 6, 9, 2, 9, 7, 6, 4};
        return MONTH_JU[zhiIndex];
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
    // 传统规则：戊一宫、己二宫、庚三宫、辛四宫、壬五宫、癸六宫、丁七宫、丙八宫、乙九宫
    // 无论阳遁阴遁，地盘天干顺序固定不变
    private String[] arrangeDiPanTianGanStandard(int ju) {
        // 地盘天干固定顺序（宫位0-8对应坎一到离九宫）
        return new String[]{"戊", "己", "庚", "辛", "壬", "癸", "丁", "丙", "乙"};
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
        String[] bashenOrder = {"值符", "螣蛇", "太阴", "六合", "白虎", "玄武", "九地", "九天"};
        
        if (isYangDun) {
            // 阳遁：从值符落宫开始顺时针排布八神，跳过中五宫
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
            // 阴遁：从值符落宫开始逆时针排布八神，跳过中五宫
            String[] bashenOrderYin = {"值符", "九天", "九地", "玄武", "白虎", "六合", "太阴", "螣蛇"};
            int currentGodIndex = 0;
            int pos = zhiFuPalace;
            while (currentGodIndex < 8) {
                if (pos != 4) {
                    eightGods[pos] = bashenOrderYin[currentGodIndex];
                    currentGodIndex++;
                }
                pos = (pos - 1 + 9) % 9;
            }
        }
        // 中五宫无神
        eightGods[4] = "";
        return eightGods;
    }
}