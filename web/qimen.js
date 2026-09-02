/*
 * qimen.js —— 奇门遁甲时家转盘排盘计算引擎（唯一权威实现）
 *
 * 算法移植自 Java 端 QiMenCalculator / JieqiData（已与标准奇门规则核对）：
 *  - 用局：节气 + 节气内第几天（三元：上/中/下元）查表
 *  - 地盘：阳遁自局数宫顺排戊己庚辛壬癸丁丙乙；阴遁自局数宫逆排戊乙丙丁癸壬辛庚己
 *  - 旬首：时辰干支落于六十甲子哪一旬，值符星/值使门 = 地盘该旬首宫固定九星/八门（中宫寄坤）
 *  - 天盘：随值符旋转，使「旬首六仪」落值符宫（值符随时干）
 *  - 旺衰：以宫五行为用神、节气月令五行为时令，定旺相休囚死
 *  - 吉凶：综合星、门、神、旺衰打分（大吉/吉/平吉/平/平凶/凶/大凶）
 *
 * 对外暴露 window.QiMenCalc.calculate(date) -> 完整排盘结果。
 */
(function (global) {
    'use strict';

    /* ===================== 基础常量 ===================== */
    const TIANGAN = ['甲', '乙', '丙', '丁', '戊', '己', '庚', '辛', '壬', '癸'];
    const DIZHI = ['子', '丑', '寅', '卯', '辰', '巳', '午', '未', '申', '酉', '戌', '亥'];
    const LIUJIAZI = [
        '甲子', '乙丑', '丙寅', '丁卯', '戊辰', '己巳', '庚午', '辛未', '壬申', '癸酉',
        '甲戌', '乙亥', '丙子', '丁丑', '戊寅', '己卯', '庚辰', '辛巳', '壬午', '癸未',
        '甲申', '乙酉', '丙戌', '丁亥', '戊子', '己丑', '庚寅', '辛卯', '壬辰', '癸巳',
        '甲午', '乙未', '丙申', '丁酉', '戊戌', '己亥', '庚子', '辛丑', '壬寅', '癸卯',
        '甲辰', '乙巳', '丙午', '丁未', '戊申', '己酉', '庚戌', '辛亥', '壬子', '癸丑',
        '甲寅', '乙卯', '丙辰', '丁巳', '戊午', '己未', '庚申', '辛酉', '壬戌', '癸亥'
    ];

    // 节气顺序（与 JieqiData.SOLAR_TERMS 一致）
    const SOLAR_TERMS = [
        '立春', '雨水', '惊蛰', '春分', '清明', '谷雨',
        '立夏', '小满', '芒种', '夏至', '小暑', '大暑',
        '立秋', '处暑', '白露', '秋分', '寒露', '霜降',
        '立冬', '小雪', '大雪', '冬至', '小寒', '大寒'
    ];

    // 地盘九星（宫序：坎坤震巽中乾兑艮离）
    const DI_PAN_STARS = ['天蓬', '天芮', '天冲', '天辅', '天禽', '天心', '天柱', '天任', '天英'];
    // 地盘八门（中宫空，寄坤；甲辰旬落中宫时值使门寄坤之死门）
    const DI_PAN_DOORS = ['休', '死', '伤', '杜', '', '开', '惊', '生', '景'];
    const EIGHT_DOORS = ['休', '生', '伤', '杜', '景', '死', '惊', '开'];

    // 阳遁/阴遁节气序
    const YANG_DUN_JIEQI = ['冬至', '小寒', '大寒', '立春', '雨水', '惊蛰', '春分', '清明', '谷雨', '立夏', '小满', '芒种'];
    const VIN_DUN_JIEQI = ['夏至', '小暑', '大暑', '立秋', '处暑', '白露', '秋分', '寒露', '霜降', '立冬', '小雪', '大雪'];

    // 三元局数表：[节气索引][上元/中元/下元]
    const YANG_DUN_JU = [
        [1, 7, 4], [2, 8, 5], [3, 9, 6], [8, 5, 2], [9, 6, 3], [1, 7, 4],
        [3, 9, 6], [4, 1, 7], [5, 2, 8], [4, 1, 7], [5, 2, 8], [6, 3, 9]
    ];
    const VIN_DUN_JU = [
        [9, 3, 6], [8, 2, 5], [7, 1, 4], [2, 5, 8], [1, 4, 7], [9, 3, 6],
        [7, 1, 4], [6, 9, 3], [5, 8, 2], [6, 9, 3], [5, 8, 2], [4, 7, 1]
    ];

    // 六甲旬首遁于六仪
    const XUNSHOU_GAN = { '甲子': '戊', '甲戌': '己', '甲申': '庚', '甲午': '辛', '甲辰': '壬', '甲寅': '癸' };

    // 宫五行（宫序：坎坤震巽中乾兑艮离）
    const PALACE_WUXING = ['水', '土', '木', '木', '土', '金', '金', '土', '火'];

    // 排盘展示用（与 UI 对应）
    const PALACE_NAMES = ['坎', '坤', '震', '巽', '中', '乾', '兑', '艮', '离'];
    const DIRECTIONS = ['北方', '西南', '东方', '东南', '中心', '西北', '西方', '东北', '南方'];
    const DIRECTION_SYMBOLS = ['↑', '↙', '→', '↘', '●', '↖', '←', '↗', '↓'];
    const GUA_SYMBOLS = ['☵', '☷', '☳', '☴', '', '☰', '☱', '☶', '☲'];

    /* ===================== 五行生克 ===================== */
    function isSheng(a, b) {
        const m = { '木': '火', '火': '土', '土': '金', '金': '水', '水': '木' };
        return m[a] === b;
    }
    function isKe(a, b) {
        const m = { '木': '土', '土': '水', '水': '火', '火': '金', '金': '木' };
        return m[a] === b;
    }

    /* ===================== 节气（寿星公式，移植 JieqiData） ===================== */
    function toMillis(y, m, d, h) {
        return new Date(y, m - 1, d, h || 0, 0, 0, 0).getTime();
    }

    function getJieqiDate(year, jieqiIndex) {
        // 寿星公式节气常数（year20=20世纪, year21=21世纪）；立秋常数 8.35（旧误 28.35）
        const year20 = [4.6295, 19.4599, 6.3826, 21.4155, 5.59, 20.88, 6.318, 21.86, 6.5, 22.2, 7.28, 23.65,
            8.35, 23.95, 8.44, 23.822, 9.098, 24.218, 8.218, 23.08, 7.9, 22.6, 6.11, 20.84];
        const year21 = [3.87, 18.73, 5.63, 20.646, 4.81, 20.1, 5.52, 21.04, 5.678, 21.37, 7.108, 22.83,
            7.5, 23.13, 7.646, 23.042, 8.318, 23.438, 7.438, 22.36, 7.18, 21.94, 5.4055, 20.12];
        const months = [2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 1, 1];

        let calcYear = year;
        if (jieqiIndex > 21) calcYear = year + 1;

        const ydNum = calcYear % 100;
        const D = 0.2422;
        const solarTerms = (calcYear >= 2000) ? year21 : year20;

        let day = Math.floor(ydNum * D + solarTerms[jieqiIndex]) - Math.floor((ydNum - 1) / 4);
        const month = months[jieqiIndex];

        if (month === 2) {
            if ((calcYear % 4 === 0 && calcYear % 100 !== 0) || calcYear % 400 === 0) {
                if (day > 29) day = 29;
            } else {
                if (day > 28) day = 28;
            }
        } else if ((month === 4 || month === 6 || month === 9 || month === 11) && day > 30) {
            day = 30;
        }
        return [calcYear, month, day];
    }

    function getJieqiDateByContext(year, month, day, jieqiIndex) {
        if (jieqiIndex === 22 || jieqiIndex === 23) {
            const lichun = getJieqiDate(year, 0);
            const beforeLichun = (month < lichun[1]) || (month === lichun[1] && day < lichun[2]);
            return beforeLichun ? getJieqiDate(year - 1, jieqiIndex) : getJieqiDate(year, jieqiIndex);
        }
        return getJieqiDate(year, jieqiIndex);
    }

    function compareYmd(y1, m1, d1, y2, m2, d2) {
        if (y1 !== y2) return y1 - y2;
        if (m1 !== m2) return m1 - m2;
        return d1 - d2;
    }

    // 取得当前节气名（以正午 12:00 为界细化临界日归属）
    function getCurrentJieqi(year, month, day, hour) {
        const now = toMillis(year, month, day, hour);
        const xiaohan = getJieqiDate(year - 1, 22);
        const dahan = getJieqiDate(year - 1, 23);
        const lichun = getJieqiDate(year, 0);

        if (now < toMillis(xiaohan[0], xiaohan[1], xiaohan[2], 12)) return '冬至';
        if (now < toMillis(dahan[0], dahan[1], dahan[2], 12)) return '小寒';
        if (now < toMillis(lichun[0], lichun[1], lichun[2], 12)) return '大寒';

        for (let i = 0; i <= 21; i++) {
            const jd = getJieqiDate(year, i);
            const next = (i < 21) ? getJieqiDate(year, i + 1) : getJieqiDate(year, 22);
            const jt = toMillis(jd[0], jd[1], jd[2], 12);
            const nt = toMillis(next[0], next[1], next[2], 12);
            if (now >= jt && now < nt) return SOLAR_TERMS[i];
        }
        return '冬至';
    }

    // 该日处于当前节气内的第几天（0 基，0..14；未到返回 -1）
    function getDaysIntoJieqi(year, month, day, jieqi) {
        const index = SOLAR_TERMS.indexOf(jieqi);
        if (index < 0) return -1;
        const date = getJieqiDateByContext(year, month, day, index);
        const jy = date[0], jm = date[1], jd = date[2];
        if (compareYmd(jy, jm, jd, year, month, day) > 0) return -1;
        const diff = toMillis(year, month, day, 0) - toMillis(jy, jm, jd, 0);
        const days = Math.floor(diff / 86400000);
        return Math.max(0, Math.min(days, 14));
    }

    /* ===================== 四柱（年/月/日/时干支） ===================== */
    // 年柱以「小寒/大寒」为界（与 App 一致）：处于小寒、大寒节气内（冬至后、立春前）属上一年
    function calculateYearPillar(year, month, day) {
        const jieqi = getCurrentJieqi(year, month, day, 12);
        let y = year;
        if (jieqi === '小寒' || jieqi === '大寒') y = year - 1;
        const baseYear = 1900;
        const baseIndex = 36;
        const yearIndex = (baseIndex + (y - baseYear)) % 60;
        return LIUJIAZI[((yearIndex % 60) + 60) % 60];
    }

    // 月支按节气（节月）定，与 App 的 getMonthZhi 完全一致
    function getMonthZhi(year, month, day) {
        const jieqi = getCurrentJieqi(year, month, day, 12);
        switch (jieqi) {
            case '立春': case '雨水': return '寅';
            case '惊蛰': case '春分': return '卯';
            case '清明': case '谷雨': return '辰';
            case '立夏': case '小满': return '巳';
            case '芒种': case '夏至': return '午';
            case '小暑': case '大暑': return '未';
            case '立秋': case '处暑': return '申';
            case '白露': case '秋分': return '酉';
            case '寒露': case '霜降': return '戌';
            case '立冬': case '小雪': return '亥';
            case '大雪': case '冬至': return '子';
            case '小寒': case '大寒': return '丑';
            default: return '寅';
        }
    }

    function calculateMonthPillar(year, month, day, yearGan) {
        const monthZhi = getMonthZhi(year, month, day);
        const wuhudun = { '甲': '丙', '己': '丙', '乙': '戊', '庚': '戊', '丙': '庚', '辛': '庚', '丁': '壬', '壬': '壬', '戊': '甲', '癸': '甲' };
        const monthZhiList = ['寅', '卯', '辰', '巳', '午', '未', '申', '酉', '戌', '亥', '子', '丑'];
        const yinMonthGan = wuhudun[yearGan] || '丙';
        const yinGanIndex = TIANGAN.indexOf(yinMonthGan);
        const monthZhiIndex = monthZhiList.indexOf(monthZhi);
        const monthGanIndex = (yinGanIndex + monthZhiIndex) % 10;
        return TIANGAN[monthGanIndex] + monthZhi;
    }

    // 儒略日（整数部分）：与 App 同源，避免 DST 引起的日历天数误差
    function julianDay(y, m, d) {
        if (m <= 2) { y -= 1; m += 12; }
        const a = Math.floor(y / 100);
        const b = 2 - a + Math.floor(a / 4);
        return Math.floor(365.25 * (y + 4716)) + Math.floor(30.6001 * (m + 1)) + d + b - 1524;
    }

    // 日柱：1900-01-01 为甲戌日（baseGanzhiIndex=10），用儒略日整数差求干支
    function calculateDayPillar(year, month, day) {
        const daysDiff = julianDay(year, month, day) - julianDay(1900, 1, 1);
        const baseGanzhiIndex = 10;
        let ganzhiIndex = (baseGanzhiIndex + daysDiff) % 60;
        if (ganzhiIndex < 0) ganzhiIndex += 60;
        return LIUJIAZI[ganzhiIndex];
    }

    // 时柱：2 小时一个时辰（子=23:00~01:00，其余左闭右开）；不做 45 分进位（与 App 一致）
    function calculateTimePillar(hour, minute, dayGan) {
        let hourZhi = '子';
        let hourZhiIndex = 0;
        if (hour >= 23 || hour < 1) { hourZhi = '子'; hourZhiIndex = 0; }
        else if (hour >= 1 && hour < 3) { hourZhi = '丑'; hourZhiIndex = 1; }
        else if (hour >= 3 && hour < 5) { hourZhi = '寅'; hourZhiIndex = 2; }
        else if (hour >= 5 && hour < 7) { hourZhi = '卯'; hourZhiIndex = 3; }
        else if (hour >= 7 && hour < 9) { hourZhi = '辰'; hourZhiIndex = 4; }
        else if (hour >= 9 && hour < 11) { hourZhi = '巳'; hourZhiIndex = 5; }
        else if (hour >= 11 && hour < 13) { hourZhi = '午'; hourZhiIndex = 6; }
        else if (hour >= 13 && hour < 15) { hourZhi = '未'; hourZhiIndex = 7; }
        else if (hour >= 15 && hour < 17) { hourZhi = '申'; hourZhiIndex = 8; }
        else if (hour >= 17 && hour < 19) { hourZhi = '酉'; hourZhiIndex = 9; }
        else if (hour >= 19 && hour < 21) { hourZhi = '戌'; hourZhiIndex = 10; }
        else { hourZhi = '亥'; hourZhiIndex = 11; }
        const wushudun = { '甲': '甲', '己': '甲', '乙': '丙', '庚': '丙', '丙': '戊', '辛': '戊', '丁': '庚', '壬': '庚', '戊': '壬', '癸': '壬' };
        const startGan = wushudun[dayGan] || '甲';
        const startGanIndex = TIANGAN.indexOf(startGan);
        const hourGanIndex = (startGanIndex + hourZhiIndex) % 10;
        return TIANGAN[hourGanIndex] + hourZhi;
    }

    function daysInMonth(y, m) {
        return new Date(y, m, 0).getDate();
    }

    function calcSiZhu(date) {
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
        const hour = date.getHours();
        const minute = date.getMinutes();

        // 子时（hour>=23）日柱、时柱按次日计算（与 App 一致）
        let calcYear = year, calcMonth = month, calcDay = day;
        if (hour >= 23) {
            calcDay += 1;
            if (calcDay > daysInMonth(calcYear, calcMonth)) {
                calcDay = 1; calcMonth += 1;
                if (calcMonth > 12) { calcMonth = 1; calcYear += 1; }
            }
        }

        const yearPillar = calculateYearPillar(year, month, day);
        const monthPillar = calculateMonthPillar(year, month, day, yearPillar.charAt(0));
        const dayPillar = calculateDayPillar(calcYear, calcMonth, calcDay);
        const timePillar = calculateTimePillar(hour, minute, dayPillar.charAt(0));
        return { yearPillar, monthPillar, dayPillar, timePillar };
    }

    /* ===================== 阴阳遁 / 用局（三元） ===================== */
    function isYangDunByJieqi(jieqi) {
        return YANG_DUN_JIEQI.indexOf(jieqi) >= 0;
    }

    // 三元序号：0上元(1-5日)、1中元(6-10日)、2下元(11-15日)，dayInJieqi 为 1 基
    function getYuanIndex(dayInJieqi) {
        if (dayInJieqi <= 5) return 0;
        if (dayInJieqi <= 10) return 1;
        return 2;
    }

    function getJuShuByJieqi(jieqi, dayInJieqi) {
        const yuan = getYuanIndex(dayInJieqi);
        for (let i = 0; i < YANG_DUN_JIEQI.length; i++) {
            if (YANG_DUN_JIEQI[i] === jieqi) return YANG_DUN_JU[i][yuan];
        }
        for (let i = 0; i < VIN_DUN_JIEQI.length; i++) {
            if (VIN_DUN_JIEQI[i] === jieqi) return VIN_DUN_JU[i][yuan];
        }
        return 1;
    }

    /* ===================== 地盘 / 天盘 ===================== */
    function arrangeDiPanTianGanStandard(ju) {
        const result = new Array(9);
        const yangOrder = ['戊', '己', '庚', '辛', '壬', '癸', '丁', '丙', '乙'];
        const yinOrder = ['戊', '乙', '丙', '丁', '癸', '壬', '辛', '庚', '己'];
        if (ju > 0) {
            const startPos = ju - 1;
            for (let i = 0; i < 9; i++) result[(startPos + i) % 9] = yangOrder[i];
        } else {
            const startPos = (-ju) - 1;
            for (let i = 0; i < 9; i++) result[(startPos - i + 9) % 9] = yinOrder[i];
        }
        return result;
    }

    function getShiGanPosition(diPan, shiGan) {
        for (let i = 0; i < 9; i++) if (diPan[i] === shiGan) return i;
        return 0;
    }

    // 旬首所在宫：六甲遁于六仪，旬首所在宫 = 对应六仪在「地盘」的宫位（随局数变化，不可写死）
    function getXunShouPalace(xunShou, diPan) {
        const gan = XUNSHOU_GAN[xunShou];
        if (gan) {
            for (let i = 0; i < 9; i++) if (gan === diPan[i]) return i;
        }
        return 0;
    }

    /**
     * 值使门落宫：从旬首本宫（地盘旬首六仪宫）起，按「时支与旬首地支的差值」顺(阳)/逆(阴)移动。
     * 步数必须用「时支序数 − 旬首地支序数」(0..11)，不能用「时支绝对序数 − 1」，
     * 否则只对甲子旬正确，甲戌/甲申/甲午/甲辰/甲寅旬（含其甲X时）会整体错位。
     * 例：甲戌时（甲戌旬，旬首地支=戌）→ 时支戌与旬首戌差 0 → 值使仍落旬首宫。
     */
    function getZhiShiPalace(xunShouPalace, xunShou, timeZhi, isYangDun) {
        const zhiIndex = DIZHI.indexOf(timeZhi);
        const shouZhiIndex = DIZHI.indexOf(xunShou.charAt(1));
        const steps = (zhiIndex - shouZhiIndex + 12) % 12;
        if (isYangDun) return (xunShouPalace + steps) % 9;
        return ((xunShouPalace - steps) % 9 + 9) % 9;
    }

    /**
     * 获取旬首信息。
     * 值符星 = 旬首所在宫的「地盘九星」；值使门 = 旬首所在宫的「地盘八门」（中宫寄坤）。
     * 返回 {xunShou, xunShouGan, xunShouPalace, zhiFuStar, zhiShiDoor}
     */
    function getXunShouInfoStandard(timeGan, timeZhi, diPan) {
        const shiGanzhi = timeGan + timeZhi;
        let shiIdx = LIUJIAZI.indexOf(shiGanzhi);
        if (shiIdx < 0) shiIdx = 0;
        const xunshouList = ['甲子', '甲戌', '甲申', '甲午', '甲辰', '甲寅'];
        const xunShou = xunshouList[Math.floor(shiIdx / 10)] || '甲子';
        const xunShouGan = XUNSHOU_GAN[xunShou] || '戊';
        const xunShouPalace = getXunShouPalace(xunShou, diPan);
        const zhiFuStar = DI_PAN_STARS[xunShouPalace];
        let zhiShiDoor = DI_PAN_DOORS[xunShouPalace];
        if (!zhiShiDoor) zhiShiDoor = DI_PAN_DOORS[1]; // 中宫寄坤二宫（死门）
        return { xunShou, xunShouGan, xunShouPalace, zhiFuStar, zhiShiDoor };
    }

    /* ===================== 九星 / 八门 / 八神 ===================== */
    function arrangeNineStarsStandard(zhiFuStar, zhiFuPalace, isYangDun) {
        const result = new Array(9);
        const jiuxingOrder = ['天蓬', '天芮', '天冲', '天辅', '天禽', '天心', '天柱', '天任', '天英'];
        let zhiFuIndex = jiuxingOrder.indexOf(zhiFuStar);
        if (zhiFuIndex < 0) zhiFuIndex = 0;
        for (let i = 0; i < 9; i++) {
            const pos = isYangDun ? (zhiFuPalace + i) % 9 : (zhiFuPalace - i + 9) % 9;
            result[pos] = jiuxingOrder[(zhiFuIndex + i) % 9];
        }
        return result;
    }

    function arrangeEightDoorsStandard(zhiShiDoor, zhiShiPalace, isYangDun) {
        const result = new Array(9);
        const bamenOrder = ['休', '生', '伤', '杜', '景', '死', '惊', '开'];
        let zhiShiIndex = bamenOrder.indexOf(zhiShiDoor);
        if (zhiShiIndex < 0) zhiShiIndex = 0;
        let currentDoorIndex = zhiShiIndex;
        for (let i = 0; i < 9; i++) {
            const pos = isYangDun ? (zhiShiPalace + i) % 9 : (zhiShiPalace - i + 9) % 9;
            if (pos === 4) {
                result[pos] = '';
            } else {
                result[pos] = bamenOrder[currentDoorIndex];
                currentDoorIndex = (currentDoorIndex + 1) % 8;
            }
        }
        return result;
    }

    /**
     * 排天盘三奇六仪：天盘随地盘整体旋转，使「旬首六仪」（值符星原本携带之干）落值符宫。
     * 例：阳遁一局己巳时，旬首甲子(戊)原在坎，值符落坤 → 天盘坤宫为戊，而非时干己。
     */
    function arrangeTianPanTianGanStandard(diPan, xunShouGan, zhiFuPalace, isYangDun) {
        const result = new Array(9);
        const order = ['戊', '己', '庚', '辛', '壬', '癸', '丁', '丙', '乙'];
        let startIdx = order.indexOf(xunShouGan);
        if (startIdx < 0) startIdx = 0;
        for (let i = 0; i < 9; i++) {
            const pos = isYangDun ? (zhiFuPalace + i) % 9 : (zhiFuPalace - i + 9) % 9;
            result[pos] = order[(startIdx + i) % 9];
        }
        return result;
    }

    function arrangeEightGodsStandard(zhiFuPalace, isYangDun) {
        const result = new Array(9);
        const yangShenOrder = ['值符', '螣蛇', '太阴', '六合', '白虎', '玄武', '九地', '九天'];
        const yinShenOrder = ['值符', '九天', '九地', '玄武', '白虎', '六合', '太阴', '螣蛇'];
        const bashenOrder = isYangDun ? yangShenOrder : yinShenOrder;
        if (isYangDun) {
            let cur = 0, pos = zhiFuPalace;
            while (cur < 8) {
                if (pos !== 4) { result[pos] = bashenOrder[cur]; cur++; }
                pos = (pos + 1) % 9;
            }
        } else {
            let cur = 0, pos = zhiFuPalace;
            while (cur < 8) {
                if (pos !== 4) { result[pos] = bashenOrder[cur]; cur++; }
                pos = (pos - 1 + 9) % 9;
            }
        }
        result[4] = '';
        return result;
    }

    /* ===================== 旺衰 / 吉凶 ===================== */
    function getYueLingWuXing(jieqi) {
        if (!jieqi) return '木';
        switch (jieqi) {
            case '立春': case '雨水': case '惊蛰': case '春分': return '木';   // 寅/卯月
            case '清明': case '谷雨': return '土';   // 辰月
            case '立夏': case '小满': case '芒种': case '夏至': return '火';   // 巳/午月
            case '小暑': case '大暑': return '土';   // 未月
            case '立秋': case '处暑': case '白露': case '秋分': return '金';   // 申/酉月
            case '寒露': case '霜降': return '土';   // 戌月
            case '立冬': case '小雪': case '大雪': case '冬至': return '水';   // 亥/子月
            case '小寒': case '大寒': return '土';   // 丑月
            default: return '木';
        }
    }

    // 各宫旺相休囚死：用神=宫五行，时令=月令五行
    function calculateWangCui(jieqi) {
        const ling = getYueLingWuXing(jieqi);
        const wc = new Array(9);
        for (let i = 0; i < 9; i++) {
            const yong = PALACE_WUXING[i];
            if (yong === ling) wc[i] = '旺';
            else if (isSheng(ling, yong)) wc[i] = '相';
            else if (isSheng(yong, ling)) wc[i] = '休';
            else if (isKe(ling, yong)) wc[i] = '囚';
            else wc[i] = '死';
        }
        return wc;
    }

    function getLuckSymbol(star, door, god, wangCui) {
        let score = 0;
        if (star) {
            if (star === '天辅' || star === '天心' || star === '天禽') score += 3;
            else if (star === '天任') score += 2;
            else if (star === '天冲') score += 1;
            else if (star === '天英') score += 0;
            else if (star === '天蓬' || star === '天芮' || star === '天柱') score -= 2;
        }
        if (door && door !== '') {
            if (door === '开' || door === '生') score += 3;
            else if (door === '休') score += 2;
            else if (door === '景') score += 1;
            else if (door === '杜') score += 0;
            else if (door === '惊') score -= 1;
            else if (door === '伤' || door === '死') score -= 3;
        }
        if (god && god !== '') {
            if (god === '值符') score += 3;
            else if (god === '九天' || god === '太阴' || god === '六合') score += 2;
            else if (god === '九地') score += 0;
            else if (god === '螣蛇') score -= 1;
            else if (god === '白虎' || god === '玄武') score -= 3;
        }
        if (wangCui) {
            if (wangCui === '旺') score += 3;
            else if (wangCui === '相') score += 2;
            else if (wangCui === '休') score += 0;
            else if (wangCui === '囚') score -= 2;
            else if (wangCui === '死') score -= 3;
        }
        if (score >= 5) return '大吉';
        if (score >= 3) return '吉';
        if (score >= 1) return '平吉';
        if (score >= -1) return '平';
        if (score >= -3) return '平凶';
        if (score >= -5) return '凶';
        return '大凶';
    }

    /* ===================== 主入口 ===================== */
    function calculate(date) {
        const year = date.getFullYear();
        const month = date.getMonth() + 1;
        const day = date.getDate();
        const hour = date.getHours();
        const minute = date.getMinutes();

        const sizhu = calcSiZhu(date);
        const jieqi = getCurrentJieqi(year, month, day, 12); // 节气名取正午 12:00 为界（与 App 一致）
        const isYangDun = isYangDunByJieqi(jieqi);
        const daysInto = getDaysIntoJieqi(year, month, day, jieqi); // 0..14
        const dayInJieqi = daysInto + 1;                            // 1..15（1 基）
        const ju = getJuShuByJieqi(jieqi, dayInJieqi);

        const timeGan = sizhu.timePillar.charAt(0);
        const timeZhi = sizhu.timePillar.charAt(1);

        const diPanJu = isYangDun ? ju : -ju;
        const diPan = arrangeDiPanTianGanStandard(diPanJu);

        const xs = getXunShouInfoStandard(timeGan, timeZhi, diPan);
        const xunShou = xs.xunShou;
        const xunShouGan = xs.xunShouGan;
        const xunShouPalace = xs.xunShouPalace;
        const zhiFuStar = xs.zhiFuStar;
        const zhiShiDoor = xs.zhiShiDoor;

        // 值符随时干：时干为六甲(甲)时不入地盘，取其遁首六仪(旬首六仪)所在宫
        const zhiFuGan = (timeGan === '甲') ? xunShouGan : timeGan;
        const zhiFuPalace = getShiGanPosition(diPan, zhiFuGan);
        const zhiShiPalace = getZhiShiPalace(xunShouPalace, xunShou, timeZhi, isYangDun);

        const nineStars = arrangeNineStarsStandard(zhiFuStar, zhiFuPalace, isYangDun);
        const eightDoors = arrangeEightDoorsStandard(zhiShiDoor, zhiShiPalace, isYangDun);
        const tianPan = arrangeTianPanTianGanStandard(diPan, xunShouGan, zhiFuPalace, isYangDun);
        const eightGods = arrangeEightGodsStandard(zhiFuPalace, isYangDun);

        const wangCui = calculateWangCui(jieqi);

        const luck = new Array(9);
        for (let i = 0; i < 9; i++) {
            luck[i] = getLuckSymbol(nineStars[i], eightDoors[i], eightGods[i], wangCui[i]);
        }

        const palaceData = new Array(9);
        for (let i = 0; i < 9; i++) {
            palaceData[i] = {
                palaceName: PALACE_NAMES[i],
                direction: DIRECTIONS[i],
                directionSymbol: DIRECTION_SYMBOLS[i],
                gua: GUA_SYMBOLS[i],
                god: eightGods[i],
                star: nineStars[i],
                door: eightDoors[i],
                tianGan: tianPan[i],
                diGan: diPan[i],
                luck: luck[i],
                wangCui: wangCui[i],
                palaceIndex: i
            };
        }

        return {
            yearPillar: sizhu.yearPillar,
            monthPillar: sizhu.monthPillar,
            dayPillar: sizhu.dayPillar,
            timePillar: sizhu.timePillar,
            jieqi, isYangDun, ju,
            dayInJieqi, daysIntoJieqi: daysInto,
            xunShou, xunShouGan, zhiFuStar, zhiShiDoor,
            zhiFuPalace, zhiShiPalace,
            diPanTianGan: diPan, tianPanTianGan: tianPan,
            nineStars, eightDoors, eightGods, wangCui, luck, palaceData
        };
    }

    global.QiMenCalc = {
        calculate,
        getCurrentJieqi,
        getJieqiDate,
        getDaysIntoJieqi
    };
})(window);
