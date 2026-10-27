package com.example.timedisplay;

import java.util.ArrayList;

/**
 * 奇门遁甲格局克应库。
 *
 * 典籍依据：
 * - 《奇门遁甲秘笈大全》卷二十二（九星吉凶诗诀、八门吉凶诗诀）、卷六（九神主论章）、卷十五（吉凶格）
 * - 《烟波钓叟歌》（三奇得使、玉女守门、天网四张、五不遇时）
 * - 《三元经》（九星品级、伏宫飞宫、门迫宫迫）
 *
 * 本类为全 app 格局判定的唯一数据源，排盘页与首页共用，避免两处口径不一致。
 */
public final class QiMenGeJu {

    private QiMenGeJu() {}

    // ==================== 十干克应八十一格 ====================
    /**
     * 十干克应：天盘干加临地盘干所成之格，凡九乘九共八十一格。
     * {天盘干, 地盘干, 格名, 吉凶(吉/凶/平), 断语}
     */
    public static final String[][] KE_YING_GE = {
        // —— 天盘乙 ——
        {"乙", "乙", "日奇伏吟", "平", "宜静宜守，不利谒贵求名"},
        {"乙", "丙", "奇仪顺遂", "吉", "吉星加临主升迁，凶星相逢主离别"},
        {"乙", "丁", "奇仪相佐", "吉", "文书事吉，利于合作，百事可为"},
        {"乙", "戊", "利阴害阳", "凶", "门凶墓迫，破财伤人，不利公开"},
        {"乙", "己", "日奇入墓", "凶", "被土暗昧，门凶事凶，门吉有救"},
        {"乙", "庚", "日奇被刑", "凶", "争讼财产，夫妻怀私"},
        {"乙", "辛", "青龙逃走", "凶", "六乙加辛龙逃走，主客俱伤，百事凶"},
        {"乙", "壬", "日奇天罗", "凶", "尊卑悖乱，官灾是非，有人谋害"},
        {"乙", "癸", "日奇地网", "平", "宜遁迹隐形、躲灾避难，退吉进凶"},
        // —— 天盘丙 ——
        {"丙", "乙", "日月并行", "吉", "阴阳得位，刚柔相合，谋为皆吉"},
        {"丙", "丙", "月奇悖师", "凶", "文书逼迫，票据文章，破耗遗失"},
        {"丙", "丁", "三奇顺遂", "吉", "文书吉利，常人宜静"},
        {"丙", "戊", "飞鸟跌穴", "吉", "丙加甲兮鸟跌穴，举动皆宜，百事俱吉"},
        {"丙", "己", "火悖入刑", "凶", "囚人刑杖，文书不行，吉凶看门"},
        {"丙", "庚", "荧入太白", "凶", "丙火入金乡，门户破财，盗贼遗失"},
        {"丙", "辛", "谋事成就", "吉", "月奇相合，凶象被合，谋事可成"},
        {"丙", "壬", "火入天罗", "凶", "为客不利，是非颇多"},
        {"丙", "癸", "月奇地网", "凶", "暗昧不明，阴人害事，灾祸颇多"},
        // —— 天盘丁 ——
        {"丁", "乙", "玉女奇生", "吉", "玉女奇生，加官进禄，婚姻财喜"},
        {"丁", "丙", "星随月转", "平", "贵人越级高升，常人乐中生悲"},
        {"丁", "丁", "奇入太阴", "吉", "文书即至，喜事遂心，万事如意"},
        {"丁", "戊", "青龙转光", "吉", "官人升迁，常人威昌，利婚求学"},
        {"丁", "己", "火入勾陈", "凶", "奸私仇冤，事因女人，谋为不利"},
        {"丁", "庚", "星奇受阻", "凶", "文书阻隔，消息不通，行人必归"},
        {"丁", "辛", "朱雀入狱", "凶", "罪人释囚，官人失位，牢狱之灾"},
        {"丁", "壬", "五神互合", "吉", "贵人恩诏，讼狱公平"},
        {"丁", "癸", "朱雀投江", "凶", "六丁加癸雀入江，文书口舌，音信沉溺"},
        // —— 天盘戊（甲子） ——
        {"戊", "乙", "青龙合灵", "平", "门吉事吉，门凶事凶"},
        {"戊", "丙", "青龙回首", "吉", "甲加丙兮龙回首，不问阴阳二遁，百事皆吉"},
        {"戊", "丁", "青龙耀明", "吉", "谒贵求名吉利；若值墓迫，惹是招非"},
        {"戊", "戊", "青龙伏吟", "平", "凡事闭塞阻滞，静守为吉"},
        {"戊", "己", "贵人入狱", "凶", "公私皆不利，冲出墓库方有转机"},
        {"戊", "庚", "值符飞宫", "凶", "直符加庚天乙飞，吉事不吉，凶事更凶"},
        {"戊", "辛", "青龙折足", "凶", "吉门尚可谋为，凶门招灾失财、有足疾"},
        {"戊", "壬", "青龙入牢", "凶", "凡阴阳皆不吉利，投资亏本"},
        {"戊", "癸", "青龙华盖", "吉", "为利而合，事有合绊，吉凶看门"},
        // —— 天盘己（甲戌） ——
        {"己", "乙", "墓神不明", "平", "地户蓬星，宜遁迹隐形"},
        {"己", "丙", "火悖地户", "凶", "男被冤害，女被淫污"},
        {"己", "丁", "朱雀入墓", "平", "文状词讼，先曲后直，拨云见日"},
        {"己", "戊", "犬遇青龙", "吉", "门吉可谋，门凶徒劳"},
        {"己", "己", "地户逢鬼", "凶", "病者必死，百事不遂，不可谋为"},
        {"己", "庚", "刑格返名", "凶", "谋为徒劳，先动不利，谨防谋害"},
        {"己", "辛", "游魂入墓", "凶", "人鬼相侵，鬼魅作祟，凡事谨慎"},
        {"己", "壬", "地网高张", "凶", "狡童佚女，奸情伤杀"},
        {"己", "癸", "地刑玄武", "凶", "男女病危，词讼有囚狱之灾"},
        // —— 天盘庚（甲申） ——
        {"庚", "乙", "太白逢星", "平", "退吉进凶，不宜强进"},
        {"庚", "丙", "太白入荧", "凶", "六庚加丙白入荧，贼即来，为主破财"},
        {"庚", "丁", "亭亭之格", "平", "因私匿起官司，门吉有救"},
        {"庚", "戊", "值符伏宫", "凶", "庚加直符天乙伏，百事不可谋"},
        {"庚", "己", "官符刑格", "凶", "官司被重刑，忌官事诉讼"},
        {"庚", "庚", "太白同宫", "凶", "又名战格，官灾横祸，兄弟雷攻"},
        {"庚", "辛", "白虎干格", "凶", "远行必凶，车祸血光，意外之灾"},
        {"庚", "壬", "小格", "凶", "加壬之时为小格，远行防失路"},
        {"庚", "癸", "大格", "凶", "庚加癸兮为大格，图谋未可通，百事凶"},
        // —— 天盘辛（甲午） ——
        {"辛", "乙", "白虎猖狂", "凶", "六辛加乙虎猖狂，主客两伤，婚姻修造大凶"},
        {"辛", "丙", "干合悖师", "凶", "官非财引，随门就事"},
        {"辛", "丁", "狱神得奇", "吉", "囚逢天赦，其利倍盈"},
        {"辛", "戊", "困龙被伤", "凶", "官司破财，宜防灾静处"},
        {"辛", "己", "入狱自刑", "凶", "奴仆背主，诉讼难理"},
        {"辛", "庚", "白虎出力", "凶", "主客相残，血溅其身"},
        {"辛", "辛", "伏吟天庭", "平", "公废私就，讼狱自招其刑"},
        {"辛", "壬", "凶蛇入狱", "凶", "两男争女，狱讼不息"},
        {"辛", "癸", "天牢华盖", "凶", "误入天网，动止皆亏"},
        // —— 天盘壬（甲辰） ——
        {"壬", "乙", "小蛇得势", "吉", "女柔男健，禄马光华"},
        {"壬", "丙", "水蛇入火", "凶", "官灾刑禁，其时难停"},
        {"壬", "丁", "干合蛇刑", "平", "文书牵连，贵人行，男吉女凶"},
        {"壬", "戊", "小蛇化龙", "吉", "男人发达，女为产妇"},
        {"壬", "己", "反吟蛇刑", "凶", "讼败祸将罹，顺守或可吉"},
        {"壬", "庚", "太白擒蛇", "平", "刑狱公平，可剖邪正"},
        {"壬", "辛", "螣蛇相缠", "凶", "谋望被人欺瞒，吉门亦不安宁"},
        {"壬", "壬", "蛇入地罗", "平", "外人缠绕，内事萦绕"},
        {"壬", "癸", "幼女奸淫", "凶", "家丑外扬，门吉星凶福反悔"},
        // —— 天盘癸（甲寅） ——
        {"癸", "乙", "华盖逢星", "平", "凡平贵禄，以门断吉"},
        {"癸", "丙", "华盖悖师", "凶", "贵贱不利，上人行"},
        {"癸", "丁", "螣蛇夭矫", "凶", "六癸加丁蛇夭矫，忧惶之事必至"},
        {"癸", "戊", "天乙会合", "吉", "吉门财婚喜，凶迫祸非顾"},
        {"癸", "己", "华盖地户", "凶", "偏宜避厄，音信隔男女"},
        {"癸", "庚", "太白入网", "凶", "暴力争讼，其罪自承"},
        {"癸", "辛", "网盖天牢", "凶", "官司败诉，死罪之刑"},
        {"癸", "壬", "复见螣蛇", "凶", "外人缠绕，内事萦绕"},
        {"癸", "癸", "天网四张", "凶", "天网四张无路走，百物自伤"}
    };

    /** 查十干克应格；无格则返回 null。 */
    public static String[] findKeYingGe(String tianGan, String diGan) {
        if (tianGan == null || diGan == null) return null;
        for (String[] g : KE_YING_GE) {
            if (g[0].equals(tianGan) && g[1].equals(diGan)) return g;
        }
        return null;
    }

    // ==================== 六仪击刑 ====================
    /**
     * 六仪击刑：天盘六仪加临所刑之宫。
     * 歌曰：「六仪击刑何太凶，甲子直符愁向东。戌刑在未申在寅，寅巳辰辰午刑午。」
     * 宫序：0坎 1坤 2震 3巽 4中 5乾 6兑 7艮 8离
     */
    public static final String[][] JI_XING = {
        {"戊", "震三", "子刑卯"}, {"己", "坤二", "戌刑未"}, {"庚", "艮八", "申刑寅"},
        {"辛", "离九", "午自刑"}, {"壬", "巽四", "辰自刑"}, {"癸", "巽四", "寅刑巳"}
    };
    public static final int[] JI_XING_PALACE = {2, 1, 7, 8, 3, 3};

    /** 三奇入墓：乙奇临坤二（未）、丙丁奇临乾六（戌）。歌曰「乙日哪堪见未时，丙丁入火火墓戌」。 */
    public static boolean isQiRuMu(String tianGan, int palace) {
        if (tianGan == null) return false;
        if ("乙".equals(tianGan) && palace == 1) return true;
        if (("丙".equals(tianGan) || "丁".equals(tianGan)) && palace == 5) return true;
        return false;
    }

    /** 门迫：门五行克宫五行，谓之迫。宫五行序：水土木木土金金土火。 */
    public static boolean isMenPo(String door, int palace) {
        if (door == null || door.isEmpty()) return false;
        String[] PALACE_WX = {"水", "土", "木", "木", "土", "金", "金", "土", "火"};
        String doorWx;
        switch (door) {
            case "休": doorWx = "水"; break;
            case "生": case "死": doorWx = "土"; break;
            case "伤": case "杜": doorWx = "木"; break;
            case "景": doorWx = "火"; break;
            case "惊": case "开": doorWx = "金"; break;
            default: return false;
        }
        return QiMenCalculator.isKe(doorWx, PALACE_WX[palace]);
    }

    /** 五不遇时：时干克日干。歌曰「五不遇时龙不精，号为日月损光明」。 */
    public static boolean isWuBuYuShi(String dayGan, String timeGan) {
        if (dayGan == null || timeGan == null) return false;
        String dw = QiMenCalculator.getWuXing(dayGan);
        String tw = QiMenCalculator.getWuXing(timeGan);
        return dw != null && tw != null && QiMenCalculator.isKe(tw, dw) && !tw.equals(dw);
    }

    // ==================== 经典格局 ====================
    /** 地盘九星本位：坎天蓬 坤天芮 震天冲 巽天辅 中天禽 乾天心 兑天柱 艮天任 离天英。 */
    public static final String[] BEN_WEI_STAR =
        {"天蓬", "天芮", "天冲", "天辅", "天禽", "天心", "天柱", "天任", "天英"};
    /** 九宫对冲：坎↔离 坤↔艮 震↔兑 巽↔乾 中↔中。 */
    public static final int[] DUI_CHONG = {8, 7, 6, 5, 4, 3, 2, 1, 0};
    /** 六甲遁仪：甲子戊 甲戌己 甲申庚 甲午辛 甲辰壬 甲寅癸。 */
    public static final String[][] LIU_JIA_YI = {
        {"甲子", "戊"}, {"甲戌", "己"}, {"甲申", "庚"}, {"甲午", "辛"}, {"甲辰", "壬"}, {"甲寅", "癸"}
    };
    /** 时干入墓：歌曰「又有时干入墓宫，课中时下忌相逢。戊戌壬辰兼丙戌，癸未丁丑己同凶。」 */
    public static final String[] SHI_GAN_RU_MU = {"戊戌", "壬辰", "丙戌", "癸未", "丁丑", "己丑"};

    /**
     * 三奇得使：《烟波钓叟歌》「乙逢犬马丙鼠猴，六丁玉女骑龙虎」。
     * 甲戌甲午乙为使，甲子甲申丙为使，甲辰甲寅丁为使。{天盘奇, 地盘仪, 格名, 六甲}
     */
    public static final String[][] SAN_QI_DE_SHI = {
        {"乙", "己", "乙奇得使", "甲戌"}, {"乙", "辛", "乙奇得使", "甲午"},
        {"丙", "戊", "丙奇得使", "甲子"}, {"丙", "庚", "丙奇得使", "甲申"},
        {"丁", "壬", "丁奇得使", "甲辰"}, {"丁", "癸", "丁奇得使", "甲寅"}
    };

    /** 得使而带微疵者。原文「乙奇加甲午辛乃青龙逃走，丙奇加甲申庚乃荧入太白，丁奇加甲寅癸乃朱雀投江，凡此三者尚有微疵不吉。」 */
    public static final String[][] DE_SHI_WEI_CI = {
        {"乙", "辛", "青龙逃走"}, {"丙", "庚", "荧入太白"}, {"丁", "癸", "朱雀投江"}
    };

    /**
     * 三奇贵人升殿 / 三奇受制。{奇, 宫序, 格名, 吉凶, 断语}
     * 升殿：乙临震（日出扶桑）、丙临离（月照端门）、丁临兑（丁见西方）。
     * 受制：乙临乾（木入金乡）、丙临坎（火入水乡）。王璋曰「此谓三奇受制，万事不可举也。」
     */
    public static final String[][] SAN_QI_LIN_GONG = {
        {"乙", "2", "日出扶桑", "吉", "乙奇临震，有禄之乡，贵人升于乙卯正殿"},
        {"丙", "8", "月照端门", "吉", "丙奇到离，火旺之地，贵人升于丙午正殿"},
        {"丁", "6", "丁见西方", "吉", "丁奇临兑，天之神位，贵人升于丁酉本殿"},
        {"乙", "5", "三奇受制", "凶", "乙奇临乾，木入金乡，受制而万事不可举"},
        {"丙", "0", "三奇受制", "凶", "丙奇临坎，火入水乡，受制而万事不可举"}
    };

    /**
     * 九遁：天遁、地遁、人遁、神遁、鬼遁、风遁、云遁、龙遁、虎遁。皆为大吉之格。
     * 歌曰：「天上六丙合生门，下临地丁天遁成；开门乙奇临六己，地遁如斯是；
     *       休门丁奇合太阴，人遁自此陈；生门丙奇合九天，神遁是当然；
     *       杜门乙奇合九地，鬼遁从斯至；开门乙奇临巽宫，风遁在此中；
     *       乙奇加辛逢吉门，云遁须当记；休门乙奇临坎水，龙遁能潜水；
     *       生门乙奇临艮山，虎遁占山间。」
     */
    public static final String[][] JIU_DUN = {
        {"天遁", "生门丙奇，下临地盘丁奇", "宜求仙修道、上书献策、见贵求名"},
        {"地遁", "开门乙奇，下临地盘六己", "宜安营扎寨、设伏掩袭、修筑营垒"},
        {"人遁", "休门丁奇，上合太阴之神", "宜求贤访士、和合婚姻、密谋机密"},
        {"神遁", "生门丙奇，上合九天之神", "宜祭祀神祇、攻虚设伏、建庙塑像"},
        {"鬼遁", "杜门乙奇，上合九地之神", "宜探听贼机、偷营劫寨、设伏攻虚"},
        {"风遁", "开门乙奇，加临巽四之宫", "宜顺风而动、出使交涉、行兵迅速"},
        {"云遁", "乙奇加辛，更逢开休生门", "宜隐匿潜藏、求仙学道、云游远行"},
        {"龙遁", "休门乙奇，加临坎一之宫", "宜渡水行舟、祈雨兴云、水战立功"},
        {"虎遁", "生门乙奇，加临艮八之宫", "宜据险守隘、畋猎捕捉、安营固守"}
    };

    /**
     * 判定某宫是否成九遁之一。
     * @return 九遁名（如「天遁」），未成则返回 null。
     * 宫序：0坎 1坤 2震 3巽 4中 5乾 6兑 7艮 8离
     */
    public static String detectJiuDun(String door, String tianGan, String diGan, String god, int palace) {
        if (door == null || tianGan == null) return null;
        if ("生".equals(door) && "丙".equals(tianGan) && "丁".equals(diGan)) return "天遁";
        if ("开".equals(door) && "乙".equals(tianGan) && "己".equals(diGan)) return "地遁";
        if ("休".equals(door) && "丁".equals(tianGan) && "太阴".equals(god)) return "人遁";
        if ("生".equals(door) && "丙".equals(tianGan) && "九天".equals(god)) return "神遁";
        if ("杜".equals(door) && "乙".equals(tianGan) && "九地".equals(god)) return "鬼遁";
        if ("开".equals(door) && "乙".equals(tianGan) && palace == 3) return "风遁";
        if ("乙".equals(tianGan) && "辛".equals(diGan)
                && ("开".equals(door) || "休".equals(door) || "生".equals(door))) return "云遁";
        if ("休".equals(door) && "乙".equals(tianGan) && palace == 0) return "龙遁";
        if ("生".equals(door) && "乙".equals(tianGan) && palace == 7) return "虎遁";
        return null;
    }

    /** 取九遁之释义。 */
    public static String[] jiuDunDetail(String name) {
        if (name == null) return null;
        for (String[] d : JIU_DUN) {
            if (d[0].equals(name)) return d;
        }
        return null;
    }

    /** 由旬首（如「甲子」）取其遁仪（如「戊」）。 */
    public static String liuJiaYi(String xunShou) {
        if (xunShou == null) return null;
        for (String[] p : LIU_JIA_YI) {
            if (p[0].equals(xunShou)) return p[1];
        }
        return null;
    }

    /** 九宫方位名（宫序 0-8：北 西南 东 东南 中 西北 西 东北 南）。 */
    private static final String[] PALACE_DIR = {"北", "西南", "东", "东南", "中", "西北", "西", "东北", "南"};

    /** 吉凶配色：吉绿、凶红、平黄。 */
    public static String luckColor(String luck) {
        if ("吉".equals(luck)) return "#3FA34D";
        if ("凶".equals(luck)) return "#E0593B";
        return "#E6C46A";
    }

    /**
     * 汇总一盘之吉格与凶格（去重，带方位）。
     * 返回 [0]=吉格列表，[1]=凶格列表；每个元素为 {格局名, 方位名}，方位名为空字符串表示无固定方位。
     */
    public static ArrayList<String[]>[] collectGeJu(String[] tianPan, String[] diPan, String[] doors,
                                                  String[] eightGods, String[] nineStars, String dayGan,
                                                  String timeGan, String timePillar, int zhiShiPalace,
                                                  String xunShou) {
        @SuppressWarnings("unchecked")
        ArrayList<String[]>[] result = new ArrayList[]{new ArrayList<String[]>(), new ArrayList<String[]>()};
        ArrayList<String[]> ji = result[0];
        ArrayList<String[]> xiong = result[1];
        if (tianPan == null || diPan == null) return result;

        String yi = liuJiaYi(xunShou);

        for (int i = 0; i < 9; i++) {
            String tp = tianPan[i], dp = diPan[i];
            if (tp == null || dp == null) continue;
            String dir = PALACE_DIR[i];

            // 十干克应
            String[] ge = findKeYingGe(tp, dp);
            if (ge != null) {
                if ("吉".equals(ge[3]) && !containsName(ji, ge[2])) ji.add(new String[]{ge[2], dir});
                else if ("凶".equals(ge[3]) && !containsName(xiong, ge[2])) xiong.add(new String[]{ge[2], dir});
            }

            // 三奇得使
            for (String[] s : SAN_QI_DE_SHI) {
                if (s[0].equals(tp) && s[1].equals(dp) && !containsName(ji, s[2])) ji.add(new String[]{s[2], dir});
            }

            // 伏宫格、飞宫格
            if (yi != null) {
                if ("庚".equals(tp) && yi.equals(dp) && !containsName(xiong, "伏宫格"))
                    xiong.add(new String[]{"伏宫格", dir});
                if (yi.equals(tp) && "庚".equals(dp) && !containsName(xiong, "飞宫格"))
                    xiong.add(new String[]{"飞宫格", dir});
            }

            // 悖格
            if ("丙".equals(tp) && ("庚".equals(dp) || "丙".equals(dp) || (yi != null && yi.equals(dp)))) {
                if (!containsName(xiong, "悖格")) xiong.add(new String[]{"悖格", dir});
            }

            // 三奇升殿 / 受制
            for (String[] s : SAN_QI_LIN_GONG) {
                if (s[0].equals(tp) && Integer.parseInt(s[1]) == i) {
                    if ("吉".equals(s[3]) && !containsName(ji, s[2])) ji.add(new String[]{s[2], dir});
                    else if ("凶".equals(s[3]) && !containsName(xiong, s[2]))
                        xiong.add(new String[]{s[2], dir});
                }
            }

            // 九遁
            String dun = detectJiuDun(doors != null ? doors[i] : null, tp, dp,
                                      eightGods != null ? eightGods[i] : null, i);
            if (dun != null && !containsName(ji, dun)) ji.add(new String[]{dun, dir});

            // 天网四张
            if ("癸".equals(tp) && !containsName(xiong, "天网四张"))
                xiong.add(new String[]{"天网四张", dir});

            // 三奇入墓、门迫
            if (isQiRuMu(tp, i) && !containsName(xiong, "三奇入墓"))
                xiong.add(new String[]{"三奇入墓", dir});
            if (doors != null && isMenPo(doors[i], i) && !containsName(xiong, "门迫"))
                xiong.add(new String[]{"门迫", dir});
        }

        // 玉女守门：地盘六丁守直使之门
        if (zhiShiPalace >= 0 && zhiShiPalace < 9 && "丁".equals(diPan[zhiShiPalace])
                && !containsName(ji, "玉女守门")) {
            ji.add(new String[]{"玉女守门", PALACE_DIR[zhiShiPalace]});
        }

        // 九星伏吟、反吟
        if (nineStars != null) {
            for (int i = 0; i < 9; i++) {
                if (nineStars[i] == null || nineStars[i].isEmpty()) continue;
                if (nineStars[i].equals(BEN_WEI_STAR[i]) && !containsName(xiong, "九星伏吟"))
                    xiong.add(new String[]{"九星伏吟", PALACE_DIR[i]});
                if (DUI_CHONG[i] != i && nineStars[i].equals(BEN_WEI_STAR[DUI_CHONG[i]])
                        && !containsName(xiong, "九星反吟"))
                    xiong.add(new String[]{"九星反吟", PALACE_DIR[i]});
            }
        }

        // 六仪击刑
        for (int k = 0; k < JI_XING.length; k++) {
            int p = JI_XING_PALACE[k];
            if (JI_XING[k][0].equals(tianPan[p]) && !containsName(xiong, "六仪击刑"))
                xiong.add(new String[]{"六仪击刑", PALACE_DIR[p]});
        }

        // 五不遇时、时干入墓 —— 无固定方位
        if (isWuBuYuShi(dayGan, timeGan) && !containsName(xiong, "五不遇时"))
            xiong.add(new String[]{"五不遇时", ""});
        if (timePillar != null) {
            for (String s : SHI_GAN_RU_MU) {
                if (s.equals(timePillar) && !containsName(xiong, "时干入墓"))
                    xiong.add(new String[]{"时干入墓", ""});
            }
        }

        return result;
    }

    /** 辅助：判断列表中是否已有同名格局。 */
    private static boolean containsName(ArrayList<String[]> list, String name) {
        for (String[] e : list) if (e[0].equals(name)) return true;
        return false;
    }

    /** 构造格局条目 HTML：格局名(方位)，若无方位则省略括号。 */
    private static String formatGeJuItem(String[] item, String color) {
        StringBuilder sb = new StringBuilder();
        sb.append("<font color='").append(color).append("'>").append(item[0]);
        if (item[1] != null && !item[1].isEmpty()) {
            sb.append("<font color='#7C8C9C'>(").append(item[1]).append(")</font>");
        }
        sb.append("</font>");
        return sb.toString();
    }

    /**
     * 首页用：吉一行、凶一行（HTML）。
     * 每段至多列二格，无则省略；两端皆空则言「诸格平和」。
     */
    public static String buildBriefGeJu(String[] tianPan, String[] diPan, String[] doors,
                                        String[] eightGods, String[] nineStars, String dayGan, String timeGan,
                                        String timePillar, int zhiShiPalace, String xunShou) {
        ArrayList<String[]>[] res = collectGeJu(tianPan, diPan, doors, eightGods, nineStars,
                                              dayGan, timeGan, timePillar, zhiShiPalace, xunShou);
        ArrayList<String[]> ji = res[0];
        ArrayList<String[]> xiong = res[1];

        StringBuilder sb = new StringBuilder();
        if (ji.isEmpty() && xiong.isEmpty()) {
            sb.append("<font color='#7C8C9C'>诸格平和，可依星门之常而行</font>");
            return sb.toString();
        }
        // 吉行
        if (!ji.isEmpty()) {
            sb.append("<font color='#3FA34D'>吉</font><font color='#7C8C9C'>｜</font>");
            int n = Math.min(ji.size(), 2);
            for (int i = 0; i < n; i++) {
                if (i > 0) sb.append("<font color='#7C8C9C'>、</font>");
                sb.append(formatGeJuItem(ji.get(i), "#3FA34D"));
            }
        }
        // 凶行（单独一行）
        if (!xiong.isEmpty()) {
            if (sb.length() > 0) sb.append("<br/>");
            sb.append("<font color='#E0593B'>凶</font><font color='#7C8C9C'>｜</font>");
            int n = Math.min(xiong.size(), 2);
            for (int i = 0; i < n; i++) {
                if (i > 0) sb.append("<font color='#7C8C9C'>、</font>");
                sb.append(formatGeJuItem(xiong.get(i), "#E0593B"));
            }
        }
        return sb.toString();
    }
}
