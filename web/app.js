// 二十四节气数据
const SOLAR_TERMS = [
    '立春', '雨水', '惊蛰', '春分', '清明', '谷雨',
    '立夏', '小满', '芒种', '夏至', '小暑', '大暑',
    '立秋', '处暑', '白露', '秋分', '寒露', '霜降',
    '立冬', '小雪', '大雪', '冬至', '小寒', '大寒'
];

// 天干地支
const TIANGAN = ['甲', '乙', '丙', '丁', '戊', '己', '庚', '辛', '壬', '癸'];
const DIZHI = ['子', '丑', '寅', '卯', '辰', '巳', '午', '未', '申', '酉', '戌', '亥'];

// 60甲子
const LIUJIAZI = [
    '甲子', '乙丑', '丙寅', '丁卯', '戊辰', '己巳', '庚午', '辛未', '壬申', '癸酉',
    '甲戌', '乙亥', '丙子', '丁丑', '戊寅', '己卯', '庚辰', '辛巳', '壬午', '癸未',
    '甲申', '乙酉', '丙戌', '丁亥', '戊子', '己丑', '庚寅', '辛卯', '壬辰', '癸巳',
    '甲午', '乙未', '丙申', '丁酉', '戊戌', '己亥', '庚子', '辛丑', '壬寅', '癸卯',
    '甲辰', '乙巳', '丙午', '丁未', '戊申', '己酉', '庚戌', '辛亥', '壬子', '癸丑',
    '甲寅', '乙卯', '丙辰', '丁巳', '戊午', '己未', '庚申', '辛酉', '壬戌', '癸亥'
];

// 奇门遁甲数据
const NINE_STARS = ['天蓬', '天芮', '天冲', '天辅', '天禽', '天心', '天柱', '天任', '天英'];
const EIGHT_DOORS = ['休', '生', '伤', '杜', '景', '死', '惊', '开'];
const EIGHT_GODS = ['值符', '螣蛇', '太阴', '六合', '白虎', '玄武', '九地', '九天'];
const PALACE_NAMES = ['坎', '坤', '震', '巽', '中', '乾', '兑', '艮', '离'];
const DIRECTIONS = ['北方', '西南', '东方', '东南', '中心', '西北', '西方', '东北', '南方'];
const DIRECTION_SYMBOLS = ['↑', '↙', '→', '↘', '●', '↖', '←', '↗', '↓'];

// 阴阳遁判断
const YANG_DUN_JIEQI = ['冬至', '小寒', '大寒', '立春', '雨水', '惊蛰', '春分', '清明', '谷雨', '立夏', '小满', '芒种'];

// 节气局数
const JIEQI_JU_MAP = {
    '冬至': 1, '小寒': 2, '大寒': 3,
    '立春': 8, '雨水': 9, '惊蛰': 1,
    '春分': 3, '清明': 4, '谷雨': 5,
    '立夏': 4, '小满': 5, '芒种': 6,
    '夏至': 9, '小暑': 8, '大暑': 7,
    '立秋': 2, '处暑': 1, '白露': 9,
    '秋分': 7, '寒露': 6, '霜降': 5,
    '立冬': 6, '小雪': 5, '大雪': 4
};

// 旬首对应值符值使
const XUNSHOU_MAP = {
    '甲子': { star: '天蓬', door: '休' },
    '甲戌': { star: '天芮', door: '生' },
    '甲申': { star: '天冲', door: '伤' },
    '甲午': { star: '天辅', door: '杜' },
    '甲辰': { star: '天禽', door: '景' },
    '甲寅': { star: '天心', door: '死' }
};

// 全局状态
let currentDate = new Date();
let isCustomTime = false;
let customDate = null;
let currentJieqiData = null;

// 初始化
document.addEventListener('DOMContentLoaded', function() {
    initTimeDisplay();
    initMainTabs();
    initQimenExplanation();
    updateTimeDisplay();
    window.timeInterval = setInterval(updateTimeDisplay, 1000);
    viewPaiPan(new Date());
});

// 初始化主标签页
function initMainTabs() {
    const tabs = document.querySelectorAll('.main-tabs .main-tab-btn');
    tabs.forEach(tab => {
        tab.addEventListener('click', () => {
            tabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            const tabId = tab.getAttribute('data-tab');
            document.querySelectorAll('.tab-content').forEach(content => {
                content.classList.remove('active');
            });
            document.getElementById(tabId).classList.add('active');
        });
    });
}

// 当前选择的日期时间
let selectedYear, selectedMonth, selectedDay, selectedHour, selectedMinute;

// 初始化时间显示
function initTimeDisplay() {
    const confirmBtn = document.getElementById('confirmTime');
    const clickableTime = document.getElementById('clickableTime');
    const timeModal = document.getElementById('timeModal');
    const closeModal = document.getElementById('closeModal');

    // 初始化选择器值
    updatePickerWithDate(new Date());
    // 生成年份选项
    generateYearOptions();
    // 生成日期选项
    generateDayOptions();

    // 点击时间显示区域打开弹窗
    clickableTime.addEventListener('click', () => {
        timeModal.classList.add('active');
        if (!isCustomTime || !customDate) {
            updatePickerWithDate(new Date());
            generateDayOptions();
        }
    });

    // 关闭弹窗
    closeModal.addEventListener('click', () => {
        timeModal.classList.remove('active');
        closeAllPickers();
    });

    // 点击遮罩关闭弹窗
    timeModal.addEventListener('click', (e) => {
        if (e.target === timeModal) {
            timeModal.classList.remove('active');
            closeAllPickers();
        }
    });

    // 点击选择器显示/隐藏选项
    document.getElementById('yearPicker').addEventListener('click', toggleYearPicker);
    document.getElementById('monthPicker').addEventListener('click', toggleMonthPicker);
    document.getElementById('dayPicker').addEventListener('click', toggleDayPicker);
    document.getElementById('hourPicker').addEventListener('click', toggleHourPicker);
    document.getElementById('minutePicker').addEventListener('click', toggleMinutePicker);

    // 月份选择
    document.querySelectorAll('#monthOptions .option-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            selectedMonth = parseInt(btn.textContent);
            document.getElementById('monthPicker').textContent = String(selectedMonth).padStart(2, '0');
            toggleMonthPicker();
            generateDayOptions();
        });
    });

    // 小时选择
    document.querySelectorAll('#hourOptions .option-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            selectedHour = parseInt(btn.textContent);
            document.getElementById('hourPicker').textContent = String(selectedHour).padStart(2, '0');
            toggleHourPicker();
        });
    });

    // 分钟选择
    document.querySelectorAll('#minuteOptions .option-btn').forEach(btn => {
        btn.addEventListener('click', () => {
            selectedMinute = parseInt(btn.textContent);
            document.getElementById('minutePicker').textContent = String(selectedMinute).padStart(2, '0');
            toggleMinutePicker();
        });
    });

    // 快速选择
    document.getElementById('quickNow').addEventListener('click', () => {
        setToNow();
    });

    // 确定按钮 - 点击后更新排盘
    confirmBtn.addEventListener('click', () => {
        const targetDate = new Date(selectedYear, selectedMonth - 1, selectedDay, selectedHour, selectedMinute);
        isCustomTime = true;
        customDate = targetDate;
        timeModal.classList.remove('active');
        closeAllPickers();
        stopAutoUpdate();
        viewPaiPan(targetDate);
    });
}

// 停止时间自动更新
function stopAutoUpdate() {
    if (window.timeInterval) {
        clearInterval(window.timeInterval);
        window.timeInterval = null;
    }
}

// 更新选择器显示
function updatePickerWithDate(date) {
    selectedYear = date.getFullYear();
    selectedMonth = date.getMonth() + 1;
    selectedDay = date.getDate();
    selectedHour = date.getHours();
    selectedMinute = date.getMinutes();
    
    document.getElementById('yearPicker').textContent = String(selectedYear);
    document.getElementById('monthPicker').textContent = String(selectedMonth).padStart(2, '0');
    document.getElementById('dayPicker').textContent = String(selectedDay).padStart(2, '0');
    document.getElementById('hourPicker').textContent = String(selectedHour).padStart(2, '0');
    document.getElementById('minutePicker').textContent = String(selectedMinute).padStart(2, '0');
}

// 生成年份选项
function generateYearOptions() {
    const yearOptions = document.getElementById('yearOptions');
    yearOptions.innerHTML = '';
    const currentYear = new Date().getFullYear();
    for (let year = currentYear - 50; year <= currentYear + 50; year++) {
        const btn = document.createElement('button');
        btn.className = 'option-btn';
        btn.textContent = String(year);
        btn.addEventListener('click', () => {
            selectedYear = year;
            document.getElementById('yearPicker').textContent = String(year);
            toggleYearPicker();
            generateDayOptions();
        });
        yearOptions.appendChild(btn);
    }
}

// 生成日期选项
function generateDayOptions() {
    const dayOptions = document.getElementById('dayOptions');
    dayOptions.innerHTML = '';
    const daysInMonth = new Date(selectedYear, selectedMonth, 0).getDate();
    for (let day = 1; day <= daysInMonth; day++) {
        const btn = document.createElement('button');
        btn.className = 'option-btn';
        btn.textContent = String(day).padStart(2, '0');
        btn.addEventListener('click', () => {
            selectedDay = day;
            document.getElementById('dayPicker').textContent = String(day).padStart(2, '0');
            toggleDayPicker();
        });
        dayOptions.appendChild(btn);
    }
}

// 切换年份选择器
function toggleYearPicker() {
    togglePicker('yearOptions');
}

// 切换月份选择器
function toggleMonthPicker() {
    togglePicker('monthOptions');
}

// 切换日期选择器
function toggleDayPicker() {
    togglePicker('dayOptions');
}

// 切换小时选择器
function toggleHourPicker() {
    togglePicker('hourOptions');
}

// 切换分钟选择器
function toggleMinutePicker() {
    togglePicker('minuteOptions');
}

// 切换选择器显示/隐藏
function togglePicker(optionsId) {
    const options = document.getElementById(optionsId);
    // 关闭其他选择器
    document.querySelectorAll('.click-picker-options').forEach(opt => {
        if (opt.id !== optionsId) {
            opt.classList.remove('active');
        }
    });
    // 切换当前选择器
    options.classList.toggle('active');
}

// 关闭所有选择器
function closeAllPickers() {
    document.querySelectorAll('.click-picker-options').forEach(opt => {
        opt.classList.remove('active');
    });
}

// 设置为现在
function setToNow() {
    updatePickerWithDate(new Date());
    generateDayOptions();
}

// 设置为今天（保持时间不变）
function setToToday() {
    const now = new Date();
    selectedYear = now.getFullYear();
    selectedMonth = now.getMonth() + 1;
    selectedDay = now.getDate();
    document.getElementById('yearPicker').textContent = String(selectedYear);
    document.getElementById('monthPicker').textContent = String(selectedMonth).padStart(2, '0');
    document.getElementById('dayPicker').textContent = String(selectedDay).padStart(2, '0');
    generateDayOptions();
}

// 设置为明天（保持时间不变）
function setToTomorrow() {
    const now = new Date();
    now.setDate(now.getDate() + 1);
    selectedYear = now.getFullYear();
    selectedMonth = now.getMonth() + 1;
    selectedDay = now.getDate();
    document.getElementById('yearPicker').textContent = String(selectedYear);
    document.getElementById('monthPicker').textContent = String(selectedMonth).padStart(2, '0');
    document.getElementById('dayPicker').textContent = String(selectedDay).padStart(2, '0');
    generateDayOptions();
}

// 更新时间显示
function updateTimeDisplay() {
    const now = new Date();
    const hours = now.getHours();
    const minutes = now.getMinutes();

    document.getElementById('hour1').textContent = Math.floor(hours / 10);
    document.getElementById('hour2').textContent = hours % 10;
    document.getElementById('minute1').textContent = Math.floor(minutes / 10);
    document.getElementById('minute2').textContent = minutes % 10;

    const displayDate = isCustomTime ? customDate : now;
    const weekDays = ['星期日', '星期一', '星期二', '星期三', '星期四', '星期五', '星期六'];
    const dateStr = displayDate.getFullYear() + '年' + (displayDate.getMonth() + 1) + '月' + displayDate.getDate() + '日 ' + weekDays[displayDate.getDay()];
    document.getElementById('dateDisplay').textContent = dateStr;
}

// 获取节气日期
function getJieqiDate(year, jieqiIndex) {
    // 寿星公式20世纪节气常数;立秋常数原误为28.35,实际为8.35
    const year20 = [4.6295, 19.4599, 6.3826, 21.4155, 5.59, 20.88, 6.318, 21.86, 6.5, 22.2, 7.28, 23.65, 8.35, 23.95, 8.44, 23.822, 9.098, 24.218, 8.218, 23.08, 7.9, 22.6, 6.11, 20.84];
    const year21 = [3.87, 18.73, 5.63, 20.646, 4.81, 20.1, 5.52, 21.04, 5.678, 21.37, 7.108, 22.83, 7.5, 23.13, 7.646, 23.042, 8.318, 23.438, 7.438, 22.36, 7.18, 21.94, 5.4055, 20.12];
    const months = [2, 2, 3, 3, 4, 4, 5, 5, 6, 6, 7, 7, 8, 8, 9, 9, 10, 10, 11, 11, 12, 12, 1, 1];

    let calcYear = year;
    if (jieqiIndex > 21) {
        calcYear = year + 1;
    }

    const ydNum = calcYear % 100;
    const solarTerms = calcYear >= 2000 ? year21 : year20;

    let day = Math.floor(ydNum * 0.2422 + solarTerms[jieqiIndex]) - Math.floor((ydNum - 1) / 4);

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

    return { year: calcYear, month, day };
}

// 将(year, month, day, hour)转换为时间戳,用于节气日期比较
function toJieqiMillis(year, month, day, hour) {
    return new Date(year, month - 1, day, hour, 0, 0, 0).getTime();
}

// 根据日历日期上下文,获取小寒、大寒在日历年中的具体日期。
// 小寒、大寒在1月:若当前在立春之前(1月、2月初),取当年1月日期;否则取下一年1月日期。
function getJieqiDateByContext(year, month, day, jieqiIndex) {
    if (jieqiIndex === 22 || jieqiIndex === 23) {
        const lichun = getJieqiDate(year, 0);
        const beforeLichun = (month < lichun.month) || (month === lichun.month && day < lichun.day);
        if (beforeLichun) {
            return getJieqiDate(year - 1, jieqiIndex);
        } else {
            return getJieqiDate(year, jieqiIndex);
        }
    }
    return getJieqiDate(year, jieqiIndex);
}

// 获取当前节气
// 注意:小寒、大寒在每年1月,跨日历年。getJieqiDate(year, 22/23) 返回 year+1 年1月的日期,
// 因此当年1月的小寒、大寒需用 getJieqiDate(year-1, 22/23) 获取。
// 旧实现“早于立春直接返回大寒”忽略了1月初的小寒期,且 i=23 循环用当年立春作为下一节气,
// 导致1月初到立春前这段日期返回的节气错误。
function getCurrentJieqi(date) {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();

    const now = toJieqiMillis(year, month, day, hour);

    // 当年1月的小寒、大寒:用 getJieqiDate(year-1, 22/23) 获取
    const xiaohan = getJieqiDate(year - 1, 22);
    const dahan = getJieqiDate(year - 1, 23);
    const lichun = getJieqiDate(year, 0);

    // 在小寒之前,实际属于上一年的冬至期
    if (now < toJieqiMillis(xiaohan.year, xiaohan.month, xiaohan.day, 12)) {
        return '冬至';
    }
    // 小寒 <= now < 大寒
    if (now < toJieqiMillis(dahan.year, dahan.month, dahan.day, 12)) {
        return '小寒';
    }
    // 大寒 <= now < 立春
    if (now < toJieqiMillis(lichun.year, lichun.month, lichun.day, 12)) {
        return '大寒';
    }

    // 立春及以后:从立春(i=0)到冬至(i=21)循环
    for (let i = 0; i <= 21; i++) {
        const jieqiDate = getJieqiDate(year, i);
        const nextJieqiDate = (i < 21) ? getJieqiDate(year, i + 1) : getJieqiDate(year, 22);
        const jieqiTime = toJieqiMillis(jieqiDate.year, jieqiDate.month, jieqiDate.day, 12);
        const nextJieqiTime = toJieqiMillis(nextJieqiDate.year, nextJieqiDate.month, nextJieqiDate.day, 12);
        if (now >= jieqiTime && now < nextJieqiTime) {
            return SOLAR_TERMS[i];
        }
    }
    return '冬至';
}

// 计算年柱
function calculateYearPillar(year, month, day) {
    const lichunDate = getJieqiDate(year, 0);
    let calcYear = year;
    if (month < lichunDate.month || (month === lichunDate.month && day < lichunDate.day)) {
        calcYear = year - 1;
    }

    const baseYear = 1900;
    const baseIndex = 36;
    const yearDiff = calcYear - baseYear;
    const yearIndex = (baseIndex + yearDiff) % 60;
    return LIUJIAZI[yearIndex];
}

// 计算月柱
function calculateMonthPillar(year, month, day, yearGan) {
    const monthZhiMap = { 1: '寅', 2: '卯', 3: '辰', 4: '巳', 5: '午', 6: '未', 7: '申', 8: '酉', 9: '戌', 10: '亥', 11: '子', 12: '丑' };
    const wuhudun = { '甲': '丙', '己': '丙', '乙': '戊', '庚': '戊', '丙': '庚', '辛': '庚', '丁': '壬', '壬': '壬', '戊': '甲', '癸': '甲' };
    const monthZhiList = ['寅', '卯', '辰', '巳', '午', '未', '申', '酉', '戌', '亥', '子', '丑'];

    let monthZhi = monthZhiMap[month];
    if (month === 2) {
        const lichun = getJieqiDate(year, 0);
        monthZhi = day >= lichun.day ? '寅' : '丑';
    }

    const yinMonthGan = wuhudun[yearGan] || '丙';
    const yinGanIndex = TIANGAN.indexOf(yinMonthGan);
    const monthZhiIndex = monthZhiList.indexOf(monthZhi);
    const monthGanIndex = (yinGanIndex + monthZhiIndex) % 10;
    return TIANGAN[monthGanIndex] + monthZhi;
}

// 计算日柱
function calculateDayPillar(year, month, day) {
    const baseDate = new Date(1900, 0, 1);
    const targetDate = new Date(year, month - 1, day);
    const daysDiff = Math.floor((targetDate - baseDate) / (1000 * 60 * 60 * 24));
    const baseIndex = 10;
    const ganzhiIndex = (baseIndex + daysDiff) % 60;
    return LIUJIAZI[(ganzhiIndex + 60) % 60];
}

// 计算时柱
function calculateTimePillar(hour, minute, dayGan) {
    let hourZhi = '子';
    let hourZhiIndex = 0;

    if (hour >= 23 || hour < 1) {
        hourZhi = '子';
        hourZhiIndex = 0;
    } else if (hour >= 1 && hour < 3) {
        hourZhi = '丑';
        hourZhiIndex = 1;
    } else if (hour >= 3 && hour < 5) {
        hourZhi = '寅';
        hourZhiIndex = 2;
    } else if (hour >= 5 && hour < 7) {
        hourZhi = '卯';
        hourZhiIndex = 3;
    } else if (hour >= 7 && hour < 9) {
        hourZhi = '辰';
        hourZhiIndex = 4;
    } else if (hour >= 9 && hour < 11) {
        hourZhi = '巳';
        hourZhiIndex = 5;
    } else if (hour >= 11 && hour < 13) {
        hourZhi = '午';
        hourZhiIndex = 6;
    } else if (hour >= 13 && hour < 15) {
        hourZhi = '未';
        hourZhiIndex = 7;
    } else if (hour >= 15 && hour < 17) {
        hourZhi = '申';
        hourZhiIndex = 8;
    } else if (hour >= 17 && hour < 19) {
        hourZhi = '酉';
        hourZhiIndex = 9;
    } else if (hour >= 19 && hour < 21) {
        hourZhi = '戌';
        hourZhiIndex = 10;
    } else {
        hourZhi = '亥';
        hourZhiIndex = 11;
    }

    const wushudun = { '甲': '甲', '己': '甲', '乙': '丙', '庚': '丙', '丙': '戊', '辛': '戊', '丁': '庚', '壬': '庚', '戊': '壬', '癸': '壬' };
    const startGan = wushudun[dayGan] || '甲';
    const startGanIndex = TIANGAN.indexOf(startGan);
    const hourGanIndex = (startGanIndex + hourZhiIndex) % 10;
    return TIANGAN[hourGanIndex] + hourZhi;
}

function getDaysInMonth(year, month) {
    const daysInMonth = [31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];
    let days = daysInMonth[month - 1];
    if (month === 2 && ((year % 4 === 0 && year % 100 !== 0) || year % 400 === 0)) {
        days = 29;
    }
    return days;
}

// 计算四柱
function calculateSiZhu(date) {
    const year = date.getFullYear();
    const month = date.getMonth() + 1;
    const day = date.getDate();
    const hour = date.getHours();
    const minute = date.getMinutes();

    const yearPillar = calculateYearPillar(year, month, day);
    const monthPillar = calculateMonthPillar(year, month, day, yearPillar[0]);
    
    let calcYear = year;
    let calcMonth = month;
    let calcDay = day;
    if (hour >= 23) {
        calcDay++;
        if (calcDay > getDaysInMonth(calcYear, calcMonth)) {
            calcDay = 1;
            calcMonth++;
            if (calcMonth > 12) {
                calcMonth = 1;
                calcYear++;
            }
        }
    }
    const dayPillar = calculateDayPillar(calcYear, calcMonth, calcDay);
    const timePillar = calculateTimePillar(hour, minute, dayPillar[0]);

    return {
        yearPillar,
        monthPillar,
        dayPillar,
        timePillar,
        jieqi: getCurrentJieqi(date)
    };
}

// 查看排盘
function viewPaiPan(date) {
    currentJieqiData = calculateSiZhu(date);
    updateQiMen(date);
}

// 获取时辰名称
function getShiChen(hour, minute) {
    const shichen = ['子时', '丑时', '寅时', '卯时', '辰时', '巳时', '午时', '未时', '申时', '酉时', '戌时', '亥时'];
    let index;
    if (hour >= 23 || hour < 1) index = 0;
    else if (hour >= 1 && hour < 3) index = 1;
    else if (hour >= 3 && hour < 5) index = 2;
    else if (hour >= 5 && hour < 7) index = 3;
    else if (hour >= 7 && hour < 9) index = 4;
    else if (hour >= 9 && hour < 11) index = 5;
    else if (hour >= 11 && hour < 13) index = 6;
    else if (hour >= 13 && hour < 15) index = 7;
    else if (hour >= 15 && hour < 17) index = 8;
    else if (hour >= 17 && hour < 19) index = 9;
    else if (hour >= 19 && hour < 21) index = 10;
    else index = 11;
    return shichen[index];
}

// 更新奇门遁甲
function updateQiMen(date) {
    if (!currentJieqiData) return;

    // 显示排盘时间
    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const day = String(date.getDate()).padStart(2, '0');
    const hours = String(date.getHours()).padStart(2, '0');
    const minutes = String(date.getMinutes()).padStart(2, '0');
    const shiChen = getShiChen(date.getHours(), date.getMinutes());
    
    document.getElementById('paiPanDate').textContent = `${year}-${month}-${day}`;
    document.getElementById('paiPanTimeDisplay').textContent = `${hours}:${minutes}`;
    document.getElementById('paiPanShiChen').textContent = shiChen;
    document.getElementById('paiPanTime').textContent = `${year}-${month}-${day} ${hours}:${minutes} ${shiChen}`;

    const jieqi = currentJieqiData.jieqi;
    const isYangDun = YANG_DUN_JIEQI.includes(jieqi);
    const ju = JIEQI_JU_MAP[jieqi] || 1;

    document.getElementById('qimenJieqi').textContent = jieqi;
    document.getElementById('qimenDun').textContent = isYangDun ? '阳遁' : '阴遁';
    document.getElementById('qimenJu').textContent = ju + '局';

    const timePillar = currentJieqiData.timePillar;
    const timeGan = timePillar[0];
    const timeZhi = timePillar[1];

    const xunshou = getXunShou(timeGan, timeZhi);
    const xunshouInfo = XUNSHOU_MAP[xunshou];

    document.getElementById('xunshou').textContent = xunshou;
    document.getElementById('zhifu').textContent = xunshouInfo.star;
    document.getElementById('zhishi').textContent = xunshouInfo.door + '门';

    const sizhuStr = currentJieqiData.yearPillar + ' ' + currentJieqiData.monthPillar + ' ' + currentJieqiData.dayPillar + ' ' + currentJieqiData.timePillar;
    document.getElementById('sizhu').textContent = sizhuStr;

    // 计算空亡
    const kongwang = getKongWang(currentJieqiData.dayPillar);
    document.getElementById('kongwang').textContent = kongwang;

    // 计算马星
    const maxing = getMaXing(currentJieqiData.dayPillar);
    document.getElementById('maxing').textContent = maxing;

    // 获取日干和时干
    const dayPillar = currentJieqiData.dayPillar;
    const riGan = dayPillar[0];
    const riZhi = dayPillar[1];
    document.getElementById('riGan').textContent = riGan + riZhi;

    const shiGan = timePillar[0];
    document.getElementById('shiGan').textContent = shiGan + timeZhi;

    const palaceData = calculateQiMenPanel(currentJieqiData.yearPillar, currentJieqiData.monthPillar, currentJieqiData.dayPillar, timePillar, jieqi);
    
    // 获取落宫信息
    const gongwei = ['坎一宫', '坤二宫', '震三宫', '巽四宫', '中五宫', '乾六宫', '兑七宫', '艮八宫', '离九宫'];
    let zhifuPalace = '';
    let zhishiPalace = '';
    let riGanPalace = '';
    let shiGanPalace = '';

    palaceData.forEach((data, index) => {
        if (data.star === xunshouInfo.star) {
            zhifuPalace = gongwei[index];
        }
        if (data.door === xunshouInfo.door) {
            zhishiPalace = gongwei[index];
        }
        if (data.tianGan === riGan) {
            riGanPalace = gongwei[index];
        }
        if (data.tianGan === shiGan) {
            shiGanPalace = gongwei[index];
        }
    });

    document.getElementById('zhifuPalace').textContent = zhifuPalace;
    document.getElementById('zhishiPalace').textContent = zhishiPalace;
    document.getElementById('riGanPalace').textContent = riGanPalace;
    document.getElementById('shiGanPalace').textContent = shiGanPalace;

    renderNinePalace(palaceData);
}

// 获取空亡
function getKongWang(dayPillar) {
    const xunshouList = ['甲子', '甲戌', '甲申', '甲午', '甲辰', '甲寅'];
    const kongwangMap = {
        '甲子': '戌亥',
        '甲戌': '申酉',
        '甲申': '午未',
        '甲午': '辰巳',
        '甲辰': '寅卯',
        '甲寅': '子丑'
    };
    
    const dayGan = dayPillar[0];
    const dayZhi = dayPillar[1];
    const shiGanzhi = dayGan + dayZhi;
    let shiIndex = LIUJIAZI.indexOf(shiGanzhi);
    if (shiIndex === -1) shiIndex = 0;
    const xunIndex = Math.floor(shiIndex / 10);
    const xunshou = xunshouList[xunIndex] || '甲子';
    
    return kongwangMap[xunshou] || '--';
}

// 获取马星
function getMaXing(dayPillar) {
    const maXingMap = {
        '申': '寅', '子': '午', '辰': '申',
        '寅': '午', '午': '申', '戌': '子',
        '巳': '亥', '酉': '巳', '丑': '酉',
        '亥': '巳', '卯': '酉', '未': '亥'
    };
    const dayZhi = dayPillar[1];
    return maXingMap[dayZhi] || '--';
}

// 获取旬首
function getXunShou(timeGan, timeZhi) {
    const shiGanzhi = timeGan + timeZhi;
    let shiIndex = LIUJIAZI.indexOf(shiGanzhi);
    if (shiIndex === -1) shiIndex = 0;

    const xunshouList = ['甲子', '甲戌', '甲申', '甲午', '甲辰', '甲寅'];
    const xunIndex = Math.floor(shiIndex / 10);
    return xunshouList[xunIndex] || '甲子';
}

// 计算奇门遁甲排盘
function calculateQiMenPanel(yearPillar, monthPillar, dayPillar, timePillar, jieqi) {
    const isYangDun = YANG_DUN_JIEQI.includes(jieqi);
    const ju = JIEQI_JU_MAP[jieqi] || 1;

    const timeGan = timePillar[0];
    const timeZhi = timePillar[1];

    const xunshou = getXunShou(timeGan, timeZhi);
    const xunshouInfo = XUNSHOU_MAP[xunshou];

    const zhiFuStar = xunshouInfo.star;
    const zhiShiDoor = xunshouInfo.door;

    const diPanTianGan = ['戊', '己', '庚', '辛', '壬', '癸', '丁', '丙', '乙'];

    const shiGanPosition = diPanTianGan.indexOf(timeGan);
    const zhiFuPalace = shiGanPosition !== -1 ? shiGanPosition : 0;

    const xunshouPalaceMap = { '甲子': 0, '甲戌': 1, '甲申': 2, '甲午': 3, '甲辰': 4, '甲寅': 5 };
    const xunshouPalace = xunshouPalaceMap[xunshou] || 0;

    const zhiIndex = DIZHI.indexOf(timeZhi);
    let zhiShiPalace = isYangDun ? (xunshouPalace + zhiIndex) % 9 : (xunshouPalace - zhiIndex + 9) % 9;

    const nineStars = arrangeNineStars(zhiFuStar, zhiFuPalace, isYangDun);
    const eightDoors = arrangeEightDoors(zhiShiDoor, zhiShiPalace, isYangDun);
    const tianPanTianGan = arrangeTianPanTianGan(diPanTianGan, timeGan, zhiFuPalace, isYangDun);
    const eightGods = arrangeEightGods(zhiFuPalace, isYangDun);

    const dayGan = dayPillar[0];
    const wangCui = calculateWangCui(dayGan);

    const palaceData = [];
    for (let i = 0; i < 9; i++) {
        const star = nineStars[i];
        const door = eightDoors[i];
        const god = eightGods[i];
        const tianGan = tianPanTianGan[i];
        const diGan = diPanTianGan[i];
        const luck = getLuckSymbol(star, door);

        palaceData.push({
            palaceName: PALACE_NAMES[i],
            direction: DIRECTIONS[i],
            directionSymbol: DIRECTION_SYMBOLS[i],
            god,
            star,
            door,
            tianGan,
            diGan,
            luck,
            wangCui: wangCui[i],
            palaceIndex: i
        });
    }

    return palaceData;
}

// 排九星
function arrangeNineStars(zhiFuStar, zhiFuPalace, isYangDun) {
    const result = new Array(9);
    const starIndex = NINE_STARS.indexOf(zhiFuStar);

    if (isYangDun) {
        for (let i = 0; i < 9; i++) {
            const pos = (zhiFuPalace + i) % 9;
            result[pos] = NINE_STARS[(starIndex + i) % 9];
        }
    } else {
        for (let i = 0; i < 9; i++) {
            const pos = (zhiFuPalace - i + 9) % 9;
            result[pos] = NINE_STARS[(starIndex + i) % 9];
        }
    }

    return result;
}

// 排八门
function arrangeEightDoors(zhiShiDoor, zhiShiPalace, isYangDun) {
    const result = new Array(9);
    const doorIndex = EIGHT_DOORS.indexOf(zhiShiDoor);
    let currentDoorIndex = doorIndex;

    for (let i = 0; i < 9; i++) {
        const pos = isYangDun ? (zhiShiPalace + i) % 9 : (zhiShiPalace - i + 9) % 9;

        if (pos === 4) {
            result[pos] = '';
        } else {
            result[pos] = EIGHT_DOORS[currentDoorIndex % 8];
            currentDoorIndex++;
        }
    }

    return result;
}

// 排天盘天干
function arrangeTianPanTianGan(diPan, timeGan, zhiFuPalace, isYangDun) {
    const result = new Array(9);
    const tianGanOrder = ['戊', '己', '庚', '辛', '壬', '癸', '丁', '丙', '乙'];
    const shiGanIndex = tianGanOrder.indexOf(timeGan);

    if (isYangDun) {
        for (let i = 0; i < 9; i++) {
            const pos = (zhiFuPalace + i) % 9;
            result[pos] = tianGanOrder[(shiGanIndex + i) % 9];
        }
    } else {
        for (let i = 0; i < 9; i++) {
            const pos = (zhiFuPalace - i + 9) % 9;
            result[pos] = tianGanOrder[(shiGanIndex + i) % 9];
        }
    }

    return result;
}

// 排八神
function arrangeEightGods(zhiFuPalace, isYangDun) {
    const result = new Array(9);
    let currentGodIndex = 0;
    let pos = zhiFuPalace;

    if (isYangDun) {
        while (currentGodIndex < 8) {
            if (pos !== 4) {
                result[pos] = EIGHT_GODS[currentGodIndex];
                currentGodIndex++;
            }
            pos = (pos + 1) % 9;
        }
    } else {
        while (currentGodIndex < 8) {
            if (pos !== 4) {
                result[pos] = EIGHT_GODS[currentGodIndex];
                currentGodIndex++;
            }
            pos = (pos - 1 + 9) % 9;
        }
    }

    result[4] = '';
    return result;
}

// 计算旺衰
function calculateWangCui(dayGan) {
    const palaceWuxing = ['水', '土', '木', '木', '土', '金', '金', '土', '火'];
    const ganWuxing = {
        '甲': '木', '乙': '木',
        '丙': '火', '丁': '火',
        '戊': '土', '己': '土',
        '庚': '金', '辛': '金',
        '壬': '水', '癸': '水'
    };

    const riGanWuxing = ganWuxing[dayGan] || '土';
    const wangCui = [];

    for (let i = 0; i < 9; i++) {
        const gongWuxing = palaceWuxing[i];

        if (riGanWuxing === gongWuxing) {
            wangCui.push('旺');
        } else if (isSheng(gongWuxing, riGanWuxing)) {
            wangCui.push('相');
        } else if (isSheng(riGanWuxing, gongWuxing)) {
            wangCui.push('休');
        } else if (isKe(gongWuxing, riGanWuxing)) {
            wangCui.push('囚');
        } else if (isKe(riGanWuxing, gongWuxing)) {
            wangCui.push('死');
        } else {
            wangCui.push('平');
        }
    }

    return wangCui;
}

// 五行相生
function isSheng(a, b) {
    const shengMap = {
        '木': '火',
        '火': '土',
        '土': '金',
        '金': '水',
        '水': '木'
    };
    return shengMap[a] === b;
}

// 五行相克
function isKe(a, b) {
    const keMap = {
        '木': '土',
        '土': '火',
        '火': '金',
        '金': '木',
        '水': '火'
    };
    return keMap[a] === b;
}

// 获取吉凶符号
function getLuckSymbol(star, door) {
    const luckyStars = ['天辅', '天心', '天禽', '天任'];
    const luckyDoors = ['开', '休', '生'];
    const isLuckyStar = luckyStars.includes(star);
    const isLuckyDoor = luckyDoors.includes(door);

    if (isLuckyStar && isLuckyDoor) return '吉';
    if (isLuckyStar || isLuckyDoor) return '平';
    return '凶';
}

// 渲染九宫格
function renderNinePalace(palaceData) {
    const container = document.getElementById('ninePalace');
    container.innerHTML = '';

    const displayOrder = [5, 0, 7, 2, 4, 6, 1, 8, 3];

    displayOrder.forEach((index) => {
        const data = palaceData[index];
        const div = document.createElement('div');
        let classes = ['palace'];
        if (index === 4) classes.push('center');
        if (data.luck === '吉') classes.push('lucky');
        if (data.luck === '凶') classes.push('unlucky');

        div.className = classes.join(' ');
        div.innerHTML = `
            <div class="palace-header">
                <span class="palace-name">${data.palaceName}</span>
                <span class="palace-number">${getPalaceNumber(index)}</span>
                <span class="palace-direction">${data.directionSymbol} ${data.direction}</span>
            </div>
            <div class="palace-body">
                <div class="palace-god-row">${data.god || ''}</div>
                <div class="palace-star-row">${data.star}</div>
                <div class="palace-door-row">${data.door || '—'}门</div>
                <div class="palace-gan-row">
                    <span class="tian-gan">${data.tianGan}</span>
                    <span class="gan-divider">/</span>
                    <span class="di-gan">${data.diGan}</span>
                </div>
                <div class="palace-footer">
                    <span class="palace-wangcui">${data.wangCui}</span>
                    <span class="palace-luck ${data.luck.toLowerCase()}">${data.luck}</span>
                </div>
            </div>
        `;
        container.appendChild(div);
    });

    document.getElementById('copyPaiPan').onclick = () => copyPaiPan(palaceData);

    // 更新当前排盘解析
    renderAnalysisGrid(palaceData);
}

// 获取宫位编号
function getPalaceNumber(index) {
    const numbers = ['一', '二', '三', '四', '五', '六', '七', '八', '九'];
    return numbers[index];
}

// 复制排盘信息
function copyPaiPan(palaceData) {
    const displayDate = isCustomTime ? customDate : new Date();
    const jieqi = getCurrentJieqi(displayDate);
    const isYangDun = YANG_DUN_JIEQI.includes(jieqi);
    const ju = JIEQI_JU_MAP[jieqi] || 1;
    const timePillar = currentJieqiData.timePillar;
    const timeGan = timePillar[0];
    const timeZhi = timePillar[1];
    const xunshou = getXunShou(timeGan, timeZhi);
    const xunshouInfo = XUNSHOU_MAP[xunshou];

    let text = '[' + '奇门遁甲排盘' + ']\n';
    text += '━━━━━━━━━━━━━━━\n';
    text += '时间：' + displayDate.getFullYear() + '年' + (displayDate.getMonth() + 1) + '月' + displayDate.getDate() + '日 ' + displayDate.getHours() + ':' + String(displayDate.getMinutes()).padStart(2, '0') + '\n';
    text += '四柱：' + currentJieqiData.yearPillar + ' ' + currentJieqiData.monthPillar + ' ' + currentJieqiData.dayPillar + ' ' + currentJieqiData.timePillar + '\n';
    text += '节气：' + jieqi + ' ' + (isYangDun ? '阳遁' : '阴遁') + ju + '局\n';
    text += '旬首：' + xunshou + ' 值符：' + xunshouInfo.star + ' 值使：' + xunshouInfo.door + '门\n\n';
    text += '[' + '九宫排盘' + ']\n';
    text += '─────────────\n';

    const gongwei = ['坎一宫', '坤二宫', '震三宫', '巽四宫', '中五宫', '乾六宫', '兑七宫', '艮八宫', '离九宫'];
    const directions = ['北', '西南', '东', '东南', '中', '西北', '西', '东北', '南'];

    for (let i = 0; i < 9; i++) {
        const data = palaceData[i];
        text += gongwei[i] + '(' + directions[i] + ')';
        if (data.god) text += ' ' + data.god;
        text += ' ' + data.star;
        if (data.door) text += ' ' + data.door;
        text += ' ' + data.tianGan + '/' + data.diGan;
        text += ' ' + data.luck + ' ' + data.wangCui + '\n';
    }

    navigator.clipboard.writeText(text).then(() => {
        alert('排盘信息已复制到剪贴板！');
    }).catch(() => {
        const textarea = document.createElement('textarea');
        textarea.value = text;
        document.body.appendChild(textarea);
        textarea.select();
        document.execCommand('copy');
        document.body.removeChild(textarea);
        alert('排盘信息已复制到剪贴板！');
    });
}

// 九星详解
const NINE_STARS_INFO = {
    '天蓬': { category: '凶星', desc: '又名贪狼星，属水，主盗匪、抢劫、水灾、牢狱之事。临宫主有意外之灾、破财、失物、争斗等。' },
    '天芮': { category: '凶星', desc: '又名巨门星，属土，主疾病、瘟疫、孝服、牢狱。临宫主有疾病缠身、家人不和、口舌是非。' },
    '天冲': { category: '平星', desc: '又名禄存星，属木，主动乱、冲击、竞争。临宫主有动中得利、外出吉、宜于行动。' },
    '天辅': { category: '吉星', desc: '又名文曲星，属木，主贵人、文书、学业、考试。临宫主有贵人相助、考试顺利、晋升机会。' },
    '天禽': { category: '吉星', desc: '又名廉贞星，属土，主中央、调和、安宁。临宫主百事吉昌、安宁稳定、万事如意。' },
    '天心': { category: '吉星', desc: '又名武曲星，属金，主谋略、决策、医卜。临宫主有智谋、善于策划、遇事能解。' },
    '天柱': { category: '凶星', desc: '又名破军星，属金，主破败、口舌、风波。临宫主有口舌是非、官非诉讼、事业破败。' },
    '天任': { category: '吉星', desc: '又名左辅星，属土，主田宅、农业、信任。临宫主有田宅之喜、信用卓著、安居乐业。' },
    '天英': { category: '平星', desc: '又名右弼星，属火，主文明、礼仪、文书。临宫主有文书之喜、声名远播、宜于文化事业。' }
};

// 八门详解
const EIGHT_DOORS_INFO = {
    '休': { category: '吉门', desc: '主休息、休养生息、安宁、喜庆。宜于休养、谈判、嫁娶、求财。' },
    '生': { category: '吉门', desc: '主生长、生气、财富、健康。宜于经商、求财、出行、求医。' },
    '伤': { category: '凶门', desc: '主伤害、损失、争斗、变动。宜于打猎、捕捉、讨债，不宜经商、出行。' },
    '杜': { category: '平门', desc: '主阻塞、闭塞、隐藏、保密。宜于躲藏、避祸、防守，不宜公开办事。' },
    '景': { category: '平门', desc: '主文书、消息、景况、光明。宜于上书、诉讼、献策、考试。' },
    '死': { category: '凶门', desc: '主死亡、丧事、停滞、不动。宜于吊丧、行刑、收敛，不宜吉事。' },
    '惊': { category: '凶门', desc: '主惊恐、口舌、官非、变动。宜于捕捉、诉讼、诈伪，不宜出行、谋事。' },
    '开': { category: '吉门', desc: '主开启、公开、通达、顺利。宜于开业、出行、嫁娶、求财、见贵。' }
};

// 八神详解
const EIGHT_GODS_INFO = {
    '值符': { category: '吉神', desc: '天盘主宰，诸吉之首。主尊贵、权威、贵人相助。凡临之宫，百事大吉。' },
    '螣蛇': { category: '凶神', desc: '主虚惊、怪异、缠绕、虚假。临宫主有虚惊之事、口舌是非、缠绕不清。' },
    '太阴': { category: '吉神', desc: '主阴私、隐秘、暗助、静守。临宫主宜于隐秘行事、暗中相助、静以待时。' },
    '六合': { category: '吉神', desc: '主和合、婚姻、交易、合作。临宫主宜于合作、交易、嫁娶、聚会。' },
    '白虎': { category: '凶神', desc: '主凶暴、血光、丧服、杀伐。临宫主有血光之灾、疾病、丧事、官非。' },
    '玄武': { category: '凶神', desc: '主盗贼、遗失、暧昧、蒙蔽。临宫主有失物、被盗、暧昧之事、小人作祟。' },
    '九地': { category: '吉神', desc: '主地、静止、稳定、蓄藏。临宫主宜于守静、积蓄、隐藏、防守。' },
    '九天': { category: '吉神', desc: '主天、动、高远、升腾。临宫主宜于远行、出征、进取、升迁。' }
};

// 九宫详解
const PALACES_INFO = {
    '坎': { direction: '北方', element: '水', desc: '主中男、水患、险陷。对应壬癸水，宜于谋略、北方利。' },
    '坤': { direction: '西南', element: '土', desc: '主老母、包容、厚德。对应戊己土，宜于静守、西南利。' },
    '震': { direction: '东方', element: '木', desc: '主长男、震动、奋起。对应甲乙木，宜于行动、东方利。' },
    '巽': { direction: '东南', element: '木', desc: '主长女、柔顺、进退。对应甲乙木，宜于进退、东南利。' },
    '中': { direction: '中央', element: '土', desc: '主太极、中和、稳定。对应戊己土，宜于调和、居中吉。' },
    '乾': { direction: '西北', element: '金', desc: '主老父、刚健、尊贵。对应庚辛金，宜于决断、西北利。' },
    '兑': { direction: '西方', element: '金', desc: '主少女、喜悦、口舌。对应庚辛金，宜于言词、西方利。' },
    '艮': { direction: '东北', element: '土', desc: '主少男、静止、阻挡。对应戊己土，宜于守静、东北利。' },
    '离': { direction: '南方', element: '火', desc: '主中女、光明、文采。对应丙丁火，宜于文明、南方利。' }
};

// 初始化奇门遁甲详解
function initQimenExplanation() {
    // 基础概念子标签页切换
    const subTabs = document.querySelectorAll('.exp-tab-btn');
    subTabs.forEach(tab => {
        tab.addEventListener('click', () => {
            subTabs.forEach(t => t.classList.remove('active'));
            tab.classList.add('active');
            const tabId = tab.getAttribute('data-exp-tab');
            document.querySelectorAll('.exp-content').forEach(content => {
                content.classList.remove('active');
            });
            document.getElementById('exp-' + tabId).classList.add('active');
        });
    });

    renderStarsGrid();
    renderDoorsGrid();
    renderGodsGrid();
    renderPalacesGrid();
}

// 渲染九星详解
function renderStarsGrid() {
    const grid = document.getElementById('starsGrid');
    grid.innerHTML = '';
    NINE_STARS.forEach(star => {
        const info = NINE_STARS_INFO[star];
        const div = document.createElement('div');
        div.className = 'exp-item';
        div.innerHTML = `
            <div class="exp-item-title">${star}</div>
            <div class="exp-item-category">${info.category}</div>
            <div class="exp-item-desc">${info.desc}</div>
        `;
        grid.appendChild(div);
    });
}

// 渲染八门详解
function renderDoorsGrid() {
    const grid = document.getElementById('doorsGrid');
    grid.innerHTML = '';
    EIGHT_DOORS.forEach(door => {
        const info = EIGHT_DOORS_INFO[door];
        const div = document.createElement('div');
        div.className = 'exp-item';
        div.innerHTML = `
            <div class="exp-item-title">${door}门</div>
            <div class="exp-item-category">${info.category}</div>
            <div class="exp-item-desc">${info.desc}</div>
        `;
        grid.appendChild(div);
    });
}

// 渲染八神详解
function renderGodsGrid() {
    const grid = document.getElementById('godsGrid');
    grid.innerHTML = '';
    const uniqueGods = ['值符', '螣蛇', '太阴', '六合', '白虎', '玄武', '九地', '九天'];
    uniqueGods.forEach(god => {
        const info = EIGHT_GODS_INFO[god];
        const div = document.createElement('div');
        div.className = 'exp-item';
        div.innerHTML = `
            <div class="exp-item-title">${god}</div>
            <div class="exp-item-category">${info.category}</div>
            <div class="exp-item-desc">${info.desc}</div>
        `;
        grid.appendChild(div);
    });
}

// 渲染九宫详解
function renderPalacesGrid() {
    const grid = document.getElementById('palacesGrid');
    grid.innerHTML = '';
    PALACE_NAMES.forEach(palace => {
        const info = PALACES_INFO[palace];
        const div = document.createElement('div');
        div.className = 'exp-item';
        div.innerHTML = `
            <div class="exp-item-title">${palace}宫</div>
            <div class="exp-item-category">${info.direction} · ${info.element}</div>
            <div class="exp-item-desc">${info.desc}</div>
        `;
        grid.appendChild(div);
    });
}

// 渲染当前排盘解析（整体信息汇总）
function renderAnalysisGrid(palaceData) {
    const grid = document.getElementById('analysisGrid');
    if (!grid) {
        console.error('analysisGrid element not found');
        return;
    }
    grid.innerHTML = '';

    // 获取排盘关键信息
    const jieqi = document.getElementById('qimenJieqi')?.textContent || '--';
    const dun = document.getElementById('qimenDun')?.textContent || '--';
    const ju = document.getElementById('qimenJu')?.textContent || '--';
    const xunshou = document.getElementById('xunshou')?.textContent || '--';
    const zhifu = document.getElementById('zhifu')?.textContent || '--';
    const zhishi = document.getElementById('zhishi')?.textContent || '--';
    const sizhu = document.getElementById('sizhu')?.textContent || '--';
    const paiPanTime = document.getElementById('paiPanTime')?.textContent || '--';
    const kongwang = document.getElementById('kongwang')?.textContent || '--';
    const maxing = document.getElementById('maxing')?.textContent || '--';
    const riGan = document.getElementById('riGan')?.textContent || '--';
    const shiGan = document.getElementById('shiGan')?.textContent || '--';
    const riGanPalace = document.getElementById('riGanPalace')?.textContent || '--';
    const shiGanPalace = document.getElementById('shiGanPalace')?.textContent || '--';
    const zhifuPalace = document.getElementById('zhifuPalace')?.textContent || '--';
    const zhishiPalace = document.getElementById('zhishiPalace')?.textContent || '--';

    // 分析各宫吉凶
    let luckyPalaces = [];
    let unluckyPalaces = [];
    let luckyDirections = [];
    let unluckyDirections = [];
    const gongwei = ['坎一宫', '坤二宫', '震三宫', '巽四宫', '中五宫', '乾六宫', '兑七宫', '艮八宫', '离九宫'];
    
    palaceData.forEach((data, index) => {
        const gongName = gongwei[index];
        if (data.luck === '吉') {
            luckyPalaces.push(gongName);
            luckyDirections.push(`${data.direction} ${gongName}`);
        } else if (data.luck === '凶') {
            unluckyPalaces.push(gongName);
            unluckyDirections.push(`${data.direction} ${gongName}`);
        }
    });

    // 生成详细运势指导
    const dailyGuide = generateDailyGuide(palaceData, zhifu, zhishi, dun, ju, jieqi);

    // 生成各宫详细分析
    const palaceAnalysis = generatePalaceAnalysis(palaceData, gongwei);

    // 生成整体分析报告（简洁高密度版）
    const fuStar = zhifu.replace('星', '');
    const shiDoor = zhishi.replace('门', '');
    let analysisHtml = `
        <div class="analysis-summary compact">
            <h4>当前排盘</h4>
            <div class="summary-grid">
                <div class="summary-cell"><span>排盘时间</span><strong>${paiPanTime}</strong></div>
                <div class="summary-cell"><span>节气</span><strong>${jieqi}</strong><em>${getJieqiAdvice(jieqi)}</em></div>
                <div class="summary-cell"><span>遁局</span><strong>${dun}${ju}</strong><em>${getDunJuGuide(dun, ju)}</em></div>
                <div class="summary-cell"><span>四柱</span><strong>${sizhu}</strong></div>
                <div class="summary-cell"><span>空亡</span><strong style="color:var(--red)">${kongwang}</strong><em>${getKongWangAdvice(kongwang)}</em></div>
                <div class="summary-cell"><span>马星</span><strong style="color:var(--blue)">${maxing}</strong><em>${getMaXingAdvice(maxing)}</em></div>
            </div>
        </div>

        <div class="analysis-summary compact">
            <h4>核心要素</h4>
            <div class="core-grid">
                <div class="core-card">
                    <div class="core-head">值符 <strong>${zhifu}</strong> <em>落${zhifuPalace}</em></div>
                    <div class="core-line">${getStarMeaning(fuStar)}</div>
                    <div class="core-rev">▸ ${getZhifuAdvice(zhifu)}</div>
                </div>
                <div class="core-card">
                    <div class="core-head">值使 <strong>${zhishi}</strong> <em>落${zhishiPalace}</em></div>
                    <div class="core-line">${getDoorMeaning(shiDoor)}</div>
                    <div class="core-rev">▸ ${getZhishiAdvice(zhishi)}</div>
                </div>
                <div class="core-card">
                    <div class="core-head">旬首 <strong>${xunshou}</strong></div>
                    <div class="core-line">${getXunshouInfo(xunshou)}</div>
                    <div class="core-rev">▸ 空亡${kongwang} · 马星${maxing}</div>
                </div>
            </div>
        </div>

        <div class="analysis-summary compact">
            <h4>日干 · 时干</h4>
            <div class="core-grid">
                <div class="core-card">
                    <div class="core-head">日干 <strong>${riGan}</strong> <em>落${riGanPalace}（自身）</em></div>
                    <div class="core-rev">▸ ${getRiGanAdvice(riGan[0], palaceData)}</div>
                </div>
                <div class="core-card">
                    <div class="core-head">时干 <strong>${shiGan}</strong> <em>落${shiGanPalace}（所谋之事）</em></div>
                    <div class="core-rev">▸ ${getShiGanAdvice(shiGan[0], palaceData)}</div>
                </div>
                <div class="core-card">
                    <div class="core-head">日时关系</div>
                    <div class="core-line">${getRiShiRelationship(riGan[0], shiGan[0], riGanPalace, shiGanPalace)}</div>
                </div>
            </div>
        </div>

        <div class="analysis-summary compact">
            <h4>九宫速览</h4>
            <div class="palace-analysis-grid">
                ${palaceAnalysis}
            </div>
        </div>

        <div class="analysis-summary compact">
            <h4>当前运势指导</h4>
            <div class="daily-guide compact">
                <div class="guide-section"><h5>事业</h5><p>${dailyGuide.career}</p></div>
                <div class="guide-section"><h5>财运</h5><p>${dailyGuide.wealth}</p></div>
                <div class="guide-section"><h5>感情</h5><p>${dailyGuide.relationships}</p></div>
                <div class="guide-section"><h5>健康</h5><p>${dailyGuide.health}</p></div>
                <div class="guide-section"><h5>出行</h5><p>${dailyGuide.travel}</p></div>
                <div class="guide-section"><h5>学习</h5><p>${dailyGuide.study}</p></div>
            </div>
        </div>

        <div class="analysis-summary compact">
            <h4>吉凶方位</h4>
            <div class="direction-guide">
                <div class="direction-group">
                    <h5>吉方（宜往）</h5>
                    <p class="dir-list">${luckyDirections.length > 0 ? luckyDirections.join('、') : '当前无明显吉方'}</p>
                </div>
                <div class="direction-group caution">
                    <h5>凶方（慎往）</h5>
                    <p class="dir-list">${unluckyDirections.length > 0 ? unluckyDirections.join('、') : '当前无明显凶方'}</p>
                </div>
            </div>
        </div>

        <div class="analysis-summary compact">
            <h4>今日宜忌</h4>
            <div class="daily-guide compact two-col">
                <div class="guide-section yi"><h5>宜</h5><p>${generateYi(shiDoor, dun)}</p></div>
                <div class="guide-section ji"><h5>忌</h5><p>${generateJi(shiDoor, dun)}</p></div>
            </div>
        </div>

        <div class="analysis-summary compact">
            <h4>当前寄语</h4>
            <div class="daily-message"><p>${dailyGuide.message}</p></div>
        </div>
    `;

    grid.innerHTML = analysisHtml;
}

// 生成各宫详细分析（紧凑版：要素标签行 + 一句旺衰启示）
function generatePalaceAnalysis(palaceData, gongwei) {
    let html = '';
    const displayOrder = [5, 0, 7, 2, 4, 6, 1, 8, 3];

    displayOrder.forEach((index) => {
        const data = palaceData[index];
        const gongName = gongwei[index];
        const luckColor = data.luck === '吉' ? 'var(--green)' : data.luck === '凶' ? 'var(--red)' : 'var(--gold)';
        const wangColor = data.wangCui === '旺' || data.wangCui === '相' ? 'var(--green)'
                        : data.wangCui === '囚' || data.wangCui === '死' ? 'var(--red)' : 'var(--gold)';

        html += `
            <div class="palace-analysis-card">
                <div class="palace-analysis-header">
                    <span class="palace-analysis-name">${gongName}</span>
                    <span class="palace-analysis-luck" style="color:${luckColor}">${data.luck}</span>
                </div>
                <div class="palace-tags">
                    <span class="ptag"><i>神</i>${data.god || '—'}</span>
                    <span class="ptag"><i>星</i>${data.star}</span>
                    <span class="ptag"><i>门</i>${data.door || '—'}</span>
                    <span class="ptag"><i>干</i>${data.tianGan}/${data.diGan}</span>
                    <span class="ptag" style="color:${wangColor}"><i>旺</i>${data.wangCui}</span>
                </div>
                <div class="palace-analysis-footer">
                    ${getPalaceAdvice(data)}
                </div>
            </div>
        `;
    });

    return html;
}

// 获取宫位建议（一句精简）
function getPalaceAdvice(data) {
    const advice = [];

    if (data.luck === '吉') {
        advice.push('吉庆顺遂');
    } else if (data.luck === '凶') {
        advice.push('凶险谨慎');
    } else {
        advice.push('平稳守成');
    }

    if (data.wangCui === '旺') {
        advice.push('得令而旺，气势充盈');
    } else if (data.wangCui === '相') {
        advice.push('得生而相，有助益');
    } else if (data.wangCui === '休') {
        advice.push('气退休废，宜潜养');
    } else if (data.wangCui === '囚') {
        advice.push('受制被囚，多有滞碍');
    } else if (data.wangCui === '死') {
        advice.push('气临死地，事多不成');
    }

    return `<p>${advice.join(' · ')}</p>`;
}

// 获取空亡建议
function getKongWangAdvice(kongwang) {
    if (kongwang === '--') return '';
    return `空亡之地，主事虚耗、信息不实，宜谨慎决策。`;
}

// 获取马星建议
function getMaXingAdvice(maxing) {
    if (maxing === '--') return '';
    return `马星动，主变动、出行、信息传递，宜把握时机行动。`;
}

// 获取日干建议
function getRiGanAdvice(riGan, palaceData) {
    const advice = {
        '甲': '甲木为参天大树，主贵人、领袖。当前宜积极进取，发挥领导力。',
        '乙': '乙木为花草之木，主柔顺、仁慈。当前宜以柔克刚，耐心处事。',
        '丙': '丙火为太阳之火，主光明、热情。当前宜展现才华，积极向上。',
        '丁': '丁火为灯烛之火，主文明、细致。当前宜注重细节，精益求精。',
        '戊': '戊土为大地之土，主稳重、诚信。当前宜脚踏实地，诚实守信。',
        '己': '己土为田园之土，主包容、厚德。当前宜宽厚待人，积累福报。',
        '庚': '庚金为刀剑之金，主果断、刚毅。当前宜当机立断，勇往直前。',
        '辛': '辛金为首饰之金，主精致、细腻。当前宜注重品质，精益求精。',
        '壬': '壬水为江海之水，主智慧、流动。当前宜灵活变通，顺势而为。',
        '癸': '癸水为雨露之水，主聪明、神秘。当前宜低调行事，暗中谋划。'
    };
    return advice[riGan] || '日干为自身，当前宜审视自身状态，做出合适调整。';
}

// 获取时干建议
function getShiGanAdvice(shiGan, palaceData) {
    const advice = {
        '甲': '甲木主事，主贵人相助，事情有望得到有力支持。',
        '乙': '乙木主事，主事情柔顺发展，需要耐心等待。',
        '丙': '丙火主事，主事情明朗，进展迅速，机遇显现。',
        '丁': '丁火主事，主事情需要细致处理，注重细节方能成功。',
        '戊': '戊土主事，主事情稳重推进，根基稳固，不易动摇。',
        '己': '己土主事，主事情需要包容忍耐，以柔克刚。',
        '庚': '庚金主事，主事情需要果断决策，勇往直前。',
        '辛': '辛金主事，主事情需要精益求精，注重品质。',
        '壬': '壬水主事，主事情变化多端，需要灵活应对。',
        '癸': '癸水主事，主事情暗藏玄机，需要谨慎分析。'
    };
    return advice[shiGan] || '时干主事，当前宜关注事情发展动向。';
}

// 获取日时关系
function getRiShiRelationship(riGan, shiGan, riGanPalace, shiGanPalace) {
    if (riGan === shiGan) {
        return '<strong>日时比和：</strong>事情容易达成，自身与事情协调一致，成功率高。';
    }
    
    const shengMap = {
        '木': '火', '火': '土', '土': '金', '金': '水', '水': '木'
    };
    const ganWuxing = {
        '甲': '木', '乙': '木', '丙': '火', '丁': '火',
        '戊': '土', '己': '土', '庚': '金', '辛': '金',
        '壬': '水', '癸': '水'
    };
    
    const riWuxing = ganWuxing[riGan];
    const shiWuxing = ganWuxing[shiGan];
    
    if (shengMap[riWuxing] === shiWuxing) {
        return '<strong>日生时：</strong>自身生助事情，需要付出努力方能成事，虽有消耗但终能成功。';
    }
    
    if (shengMap[shiWuxing] === riWuxing) {
        return '<strong>时生日：</strong>事情生助自身，事半功倍，易得他人帮助，事情顺利。';
    }
    
    return '<strong>日时关系一般：</strong>需要努力争取，保持耐心，顺其自然。';
}

// 生成宜做事项
function generateYi(door, dun) {
    const yiMap = {
        '开': '开业、求职、出行、嫁娶、求财、见贵、签约、谈判',
        '生': '经商、投资、置业、求财、出行、求医、合作、谈判',
        '休': '休息、休养、谈判、嫁娶、聚会、访友、规划、思考',
        '伤': '捕捉、讨债、竞技、运动、搬迁、修理、短途出行',
        '杜': '隐藏、保密、防守、学习、研究、策划、内部整顿',
        '景': '上书、诉讼、献策、考试、宣传、演讲、文化交流',
        '死': '吊丧、行刑、收敛、清理、结算、整理、反思',
        '惊': '捕捉、诉讼、辩论、演讲、讨债、防患、应对'
    };
    
    const additionalYi = dun === '阳遁' ? '主动出击、公开行事、开拓创新' : '守静待时、暗中谋划、积蓄力量';
    
    return `${yiMap[door] || '谨慎行事'}。${additionalYi}。`;
}

// 生成忌做事项
function generateJi(door, dun) {
    const jiMap = {
        '开': '不宜闭门不出、拖延犹豫',
        '生': '不宜保守退缩、错失良机',
        '休': '不宜过度劳累、急躁冒进',
        '伤': '不宜经商、签约、嫁娶、远行',
        '杜': '不宜公开办事、暴露行踪',
        '景': '不宜隐蔽行事、沉默不语',
        '死': '不宜开业、嫁娶、投资、远行',
        '惊': '不宜签订合同、重要谈判、信任他人'
    };
    
    const additionalJi = dun === '阳遁' ? '不宜消极等待、固步自封' : '不宜强行出头、鲁莽行事';
    
    return `${jiMap[door] || '不宜冲动行事'}。${additionalJi}。`;
}

// 获取节气建议
function getJieqiAdvice(jieqi) {
    const advice = {
        '立春': '万物复苏，宜开始新计划，多运动',
        '雨水': '湿气渐重，注意保暖祛湿，少吃生冷',
        '惊蛰': '春雷初响，宜振作精神，早睡早起',
        '春分': '昼夜平分，宜调理身心，心情舒畅',
        '清明': '天气晴朗，宜踏青祭祖，清淡饮食',
        '谷雨': '雨水充足，宜播种施肥，健脾养胃',
        '立夏': '夏季开始，宜静心防暑，清淡饮食',
        '小满': '麦类饱满，宜防湿防病，适度午休',
        '芒种': '麦类成熟，宜抓紧时机，劳逸结合',
        '夏至': '白天最长，宜养生静心，防暑降温',
        '小暑': '天气炎热，宜防暑降温，多喝水',
        '大暑': '一年最热，宜静心避暑，清淡饮食',
        '立秋': '秋季开始，宜收敛神气，早睡早起',
        '处暑': '暑气消退，宜调理脾胃，适当锻炼',
        '白露': '露水变白，宜添衣保暖，养阴润燥',
        '秋分': '昼夜平分，宜养阴润燥，饮食均衡',
        '寒露': '露水变寒，宜温补身体，少吃生冷',
        '霜降': '开始降霜，宜防寒保暖，温补养生',
        '立冬': '冬季开始，宜养藏阳气，进补养生',
        '小雪': '开始下雪，宜进补养生，适当锻炼',
        '大雪': '雪量增多，宜保暖防寒，静养为主',
        '冬至': '白天最短，宜进补收藏，早睡晚起',
        '小寒': '天气寒冷，宜温补身体，适度锻炼',
        '大寒': '一年最冷，宜防寒保暖，静养养生'
    };
    return advice[jieqi] || '注意节气变化，调整作息';
}

// 获取遁局指导
function getDunJuGuide(dun, ju) {
    if (dun === '阳遁') {
        return '阳气上升，适合主动进取、开拓创新、公开行事';
    } else {
        return '阴气内敛，适合守静待时、谋划布局、暗中行事';
    }
}

// 获取九星含义
function getStarMeaning(star) {
    const meanings = {
        '天蓬': '大盗之星，主冒险、开拓、变动',
        '天芮': '病符之星，主疾病、困扰、医药',
        '天冲': '冲动之星，主行动、竞争、冲突',
        '天辅': '文曲之星，主文化、教育、智慧',
        '天禽': '大吉之星，主吉祥、安宁、和解',
        '天心': '谋士之星，主谋略、决策、医道',
        '天柱': '破军之星，主破坏、口舌、诉讼',
        '天任': '承载之星，主任劳、稳重、诚信',
        '天英': '威猛之星，主名声、礼仪、火光'
    };
    return meanings[star] || '九星之一';
}

// 获取八门含义
function getDoorMeaning(door) {
    const meanings = {
        '休': '休息之门，主休息、休养、安宁',
        '生': '生机之门，主生长、发展、财富',
        '伤': '伤害之门，主伤害、竞争、出行',
        '杜': '杜塞之门，主堵塞、保密、隐藏',
        '景': '景明之门，主文书、文化、传播',
        '死': '死亡之门，主结束、停滞、丧事',
        '惊': '惊恐之门，主惊恐、口舌、诉讼',
        '开': '开启之门，主开启、成功、贵人'
    };
    return meanings[door] || '八门之一';
}

// 获取值符建议
function getZhifuAdvice(zhifu) {
    const star = zhifu.replace('星', '');
    const advice = {
        '天蓬': '值符为天蓬星，当前有冒险开拓之象，适合尝试新事物，但需谨慎行事',
        '天芮': '值符为天芮星，当前宜注意健康，避免与人争执，多做善事化解',
        '天冲': '值符为天冲星，当前行动力强，适合开展计划，但需避免冲动行事',
        '天辅': '值符为天辅星，当前智慧开启，适合学习思考，决策明智',
        '天禽': '值符为天禽星，当前吉祥如意，贵人相助，诸事顺遂',
        '天心': '值符为天心星，当前谋略深远，适合策划规划，决策英明',
        '天柱': '值符为天柱星，当前宜谨言慎行，避免口舌是非，低调行事',
        '天任': '值符为天任星，当前稳重踏实，适合积累耕耘，厚积薄发',
        '天英': '值符为天英星，当前声名远播，适合展现才华，获得认可'
    };
    return advice[star] || '值符照临，贵人相助';
}

// 获取值使建议
function getZhishiAdvice(zhishi) {
    const door = zhishi.replace('门', '');
    const advice = {
        '休': '值使为休门，当前宜休息调养，养精蓄锐，不宜过度劳累',
        '生': '值使为生门，当前财运亨通，适合投资理财、商务合作、开拓事业',
        '伤': '值使为伤门，当前宜谨慎行事，避免冲突伤害，注意交通安全',
        '杜': '值使为杜门，当前适合保密工作、技术研发，不宜公开行事',
        '景': '值使为景门，当前适合文案策划、宣传推广、信息传播',
        '死': '值使为死门，当前不宜开展新业务，适合处理收尾工作，保持低调',
        '惊': '值使为惊门，当前宜谨言慎行，避免与人争执，注意沟通方式',
        '开': '值使为开门，当前事业顺利，适合求职面试、晋升谈判、项目启动'
    };
    return advice[door] || '值使主事，行动有果';
}

// 生成每日指导
function generateDailyGuide(palaceData, zhifu, zhishi, dun, ju, jieqi) {
    const door = zhishi.replace('门', '');
    const star = zhifu.replace('星', '');
    const isYang = dun === '阳遁';
    
    // 事业运
    let career = '';
    if (['开', '生', '休'].includes(door)) {
        career = '<strong>运势极佳</strong>：当前值使为吉门，事业运势如日中天。适合求职面试、晋升谈判、项目启动、团队管理、商务拓展等重要事务。把握良机，主动出击，展现才华，必有所成。上级赏识，同事相助，正是大展宏图之时。';
    } else if (['景', '杜'].includes(door)) {
        career = '<strong>运势平稳</strong>：' + (door === '景' ? '适合文案策划、宣传推广、信息传播、教育培训、文化交流等事务。思路清晰，文思泉涌，利于创造性工作。' : '适合保密工作、内部整顿、技术研发、策略规划等事务。宜韬光养晦，积蓄力量，不宜公开行事。') + '稳扎稳打，厚积薄发。';
    } else {
        career = '<strong>运势谨慎</strong>：' + (door === '伤' ? '注意避免口舌是非，团队协作要多加留意，不宜与人争执。适合处理内部事务，避免对外交涉。' : door === '死' ? '不宜开展新业务，适合处理收尾工作、整理档案、总结反思。保持低调，不宜冒进。' : door === '惊' ? '注意沟通方式，避免争执冲突，不宜进行谈判签约。谨言慎行，三思而后行。' : '宜静不宜动，适合休养生息，不宜开展新计划。') + '低调行事，守正辟邪。';
    }
    
    // 财运
    let wealth = '';
    if (['生', '开'].includes(door)) {
        wealth = '<strong>财运亨通</strong>：当前财运旺盛，财源广进。适合投资理财、商务洽谈、签订合同、收款结账、拓展财源。把握机会，但也要谨慎决策，不宜盲目投资。财星高照，正财偏财皆有斩获。';
    } else if (['休', '景'].includes(door)) {
        wealth = '<strong>财运平稳</strong>：当前财运中等，稳中有升。适合规划理财方案，学习投资知识，整理财务账目。守成有余，进取不足。稳守财库，不宜冒险投机，静待良机。';
    } else {
        wealth = '<strong>财运谨慎</strong>：当前财运欠佳，不宜进行大额投资或冒险求财。守成为上，量入为出，避免借贷担保。宜紧缩开支，积谷防饥。';
    }
    
    // 感情运
    let relationships = '';
    if (['生', '休', '开'].includes(door)) {
        relationships = '<strong>感情顺畅</strong>：当前感情运势良好！适合约会表白、求婚订婚、亲友聚会、家庭团聚。敞开心扉，真诚沟通，增进感情。单身者有望邂逅良缘，已婚者感情和睦。';
    } else if (door === '景') {
        relationships = '<strong>感情平稳</strong>：当前适合表达情感，写信沟通，线上交流。多倾听对方心声，理解彼此需求。适合沟通感情，化解误会。';
    } else {
        relationships = '<strong>感情谨慎</strong>：当前感情方面宜保持冷静，避免冲动争吵。退一步海阔天空，多包容理解。不宜进行重要的感情决策，保持低调。';
    }
    
    // 健康运
    let health = '<strong>养生建议</strong>：' + getJieqiAdvice(jieqi) + ' 注意作息规律，饮食均衡，保持适度运动。心态平和，顺其自然。';
    
    // 出行运
    let travel = '';
    if (['天冲', '天辅'].includes(star) || ['休', '开'].includes(door)) {
        travel = '<strong>出行顺利</strong>：当前适合出行旅游、出差办事、探亲访友。旅途顺利，平安吉祥。提前规划路线，注意交通安全。外出可得贵人相助。';
    } else {
        travel = '<strong>出行谨慎</strong>：当前出行宜谨慎，' + (isYang ? '适合短途出行。' : '不宜长途跋涉。') + '如需出行，务必注意安全，小心驾驶。不宜远行，静守为宜。';
    }
    
    // 学习运
    let study = '';
    if (['天辅', '天心'].includes(star)) {
        study = '<strong>学习吉</strong>：' + star + '星照临，智慧开启，记忆力增强，理解力提升。适合学习新知识、备考复习、阅读思考、研究创作。思维敏捷，事半功倍。';
    } else {
        study = '<strong>学习平</strong>：当前适合温故知新，巩固已有知识。保持专注，循序渐进。不宜学习新的复杂知识，适合复习旧知识。';
    }
    
    // 当前寄语
    const luckyCount = palaceData.filter(p => p.luck === '吉').length;
    const unluckyCount = palaceData.filter(p => p.luck === '凶').length;
    let message = '';
    
    if (luckyCount >= 6) {
        message = '🌟 <strong>当前大吉</strong>！吉气旺盛，诸事顺遂。天时地利人和，放手去做，必有收获！把握良机，乘势而上，当前是成就大事的好时机。心存善念，广结善缘，功德无量。';
    } else if (luckyCount >= 4) {
        message = '✨ <strong>当前小吉</strong>！整体运势尚可，有吉有凶。趋吉避凶，把握良机，谨慎行事。保持平常心，稳扎稳打，自有收获。积德行善，福运自来。';
    } else {
        message = '⚠️ <strong>当前宜慎</strong>！凶方较多，需格外谨慎。低调行事，守正辟邪，静待转机。多行善事，积累福报，自有转机。修身养性，静待天时。';
    }
    
    return { career, wealth, relationships, health, travel, study, message };
}

// 获取节气信息
function getSolarTermInfo(jieqi) {
    const info = {
        '立春': '万物复苏，阳气上升，宜开始新计划',
        '雨水': '降雨增多，湿气渐重，注意保暖祛湿',
        '惊蛰': '春雷初响，蛰虫惊醒，宜振作精神',
        '春分': '昼夜平分，阴阳平衡，宜调理身心',
        '清明': '天气晴朗，万物生长，宜踏青祭祖',
        '谷雨': '雨水充足，谷物生长，宜播种施肥',
        '立夏': '夏季开始，阳气鼎盛，宜静心防暑',
        '小满': '麦类饱满，雨水增多，宜防湿防病',
        '芒种': '麦类成熟，忙于收割，宜抓紧时机',
        '夏至': '白天最长，阳气最盛，宜养生静心',
        '小暑': '天气炎热，注意防暑，宜清淡饮食',
        '大暑': '一年最热，防暑降温，宜静心避暑',
        '立秋': '秋季开始，阳气转衰，宜收敛神气',
        '处暑': '暑气消退，天气转凉，宜调理脾胃',
        '白露': '露水变白，天气转凉，宜添衣保暖',
        '秋分': '昼夜平分，阴阳平衡，宜养阴润燥',
        '寒露': '露水变寒，天气转冷，宜温补身体',
        '霜降': '开始降霜，天气寒冷，宜防寒保暖',
        '立冬': '冬季开始，阴气鼎盛，宜养藏阳气',
        '小雪': '开始下雪，天气寒冷，宜进补养生',
        '大雪': '雪量增多，天寒地冻，宜保暖防寒',
        '冬至': '白天最短，阴气最盛，宜进补收藏',
        '小寒': '天气寒冷，注意保暖，宜温补身体',
        '大寒': '一年最冷，防寒保暖，宜养生静养'
    };
    return info[jieqi] || '节气转换时期';
}

// 获取遁局解释
function getDunJuExplain(dun, ju) {
    const isYang = dun === '阳遁';
    const explanation = isYang 
        ? '阳遁九局，阳气上升，适合主动出击、开拓进取。'
        : '阴遁九局，阴气下降，适合守静待时、谋划布局。';
    return `${ju}${explanation}`;
}

// 获取旬首信息
function getXunshouInfo(xunshou) {
    const info = {
        '甲子': '六甲之首，万物之始，主根基稳固',
        '甲戌': '甲木得戌土，主蓄势待发',
        '甲申': '甲木得申金，主变动之机',
        '甲午': '甲木得午火，主光明通达',
        '甲辰': '甲木得辰土，主潜藏待时',
        '甲寅': '甲木得寅木，主生发旺盛'
    };
    return info[xunshou] || '旬首为一旬之主';
}

// 获取九星详情
function getStarDetail(star) {
    const info = NINE_STARS_INFO[star];
    return info ? info.desc : '';
}

// 获取八门详情
function getDoorDetail(door) {
    const info = EIGHT_DOORS_INFO[door];
    return info ? info.desc : '';
}

// 生成综合指导
function generateGuidance(palaceData, zhifuData, zhishiData, dun, ju) {
    const isYang = dun === '阳遁';
    let yi = '';
    let ji = '';
    let comprehensive = '';
    
    // 根据值符值使生成建议
    if (zhifuData && zhishiData) {
        const zhifuStar = zhifuData.star;
        const zhishiDoor = zhishiData.door;
        
        // 吉门组合
        const luckyDoors = ['生', '开', '休'];
        const luckyStars = ['天辅', '天禽', '天心', '天任'];
        
        if (luckyDoors.includes(zhishiDoor)) {
            yi += `值使${zhishiDoor}门为吉门，当前行事顺利，适合开展新事业、洽谈合作、求财交易。`;
        } else {
            ji += `值使${zhishiDoor}门为${EIGHT_DOORS_INFO[zhishiDoor]?.category || '平门'}，当前行事宜谨慎，不宜冒进。`;
        }
        
        if (luckyStars.includes(zhifuStar)) {
            yi += `${zhifuStar}星为吉星照临，贵人相助，机遇良好。`;
        }
    }
    
    // 根据遁局给出建议
    if (isYang) {
        yi += '阳遁之局，阳气旺盛，适合主动进取、开拓创新、公开行事。';
        comprehensive += '<p>【阳遁时期】阳气上升，万物生长，天时有利。宜把握时机，积极行动，大胆决策。适合开展新项目、拓展业务、求职面试、公开演讲等。</p>';
    } else {
        yi += '阴遁之局，阴气内敛，适合守静待时、谋划布局、暗中行事。';
        comprehensive += '<p>【阴遁时期】阴气下降，万物收藏，宜养精蓄锐。适合制定计划、学习研究、内部整顿、秘密策划等。不宜强行出头，宜静待时机。</p>';
    }
    
    // 默认建议
    if (!yi) yi = '当前宜静思慎行，稳扎稳打，不宜轻举妄动。';
    if (!ji) ji = '注意言行谨慎，避免与人争执，凡事三思而后行。';
    
    comprehensive += `
        <p>【值符启示】值符为天盘之主，代表贵人与权威。值符所在之宫为当前最吉方位，可向该方向行事以获助力。</p>
        <p>【值使启示】值使为八门之主，代表行动与执行。值使所在之宫为当前事务之关键，应重点关注。</p>
        <p>【行事建议】奇门遁甲讲究"趋吉避凶"，当前可根据各宫吉凶分布，选择吉方行事，避开凶方。遇到困难时可静心思考，待时而动。</p>
        <p>【养生建议】根据节气变化调整作息，饮食宜清淡均衡，保持身心和谐。</p>
    `;
    
    return { yi, ji, comprehensive };
}

// 生成详细的人生指导建议
function generateDetailedGuidance(palaceData, zhifuData, zhishiData, dun, ju, jieqi) {
    const isYang = dun === '阳遁';
    const guidance = {
        career: '',
        wealth: '',
        relationships: '',
        health: '',
        travel: '',
        study: '',
        business: '',
        overall: ''
    };
    
    // 根据值符值使判断
    if (zhifuData && zhishiData) {
        const zhifuStar = zhifuData.star;
        const zhishiDoor = zhishiData.door;
        
        // 事业运
        if (['开', '生', '休'].includes(zhishiDoor)) {
            guidance.career = `<p>🌟 <strong>事业运：大吉</strong></p>
                <p>当前值使为${zhishiDoor}门，事业运势极佳。适合求职面试、晋升谈判、项目推进、团队管理。${['天辅', '天心'].includes(zhifuStar) ? '天辅/天心星照临，智慧与谋略俱佳，决策明智。' : ''}</p>
                <p><strong>建议：</strong>把握良机，主动出击，展现才华，必有所成。</p>`;
        } else if (['景', '杜'].includes(zhishiDoor)) {
            guidance.career = `<p>🌟 <strong>事业运：平</strong></p>
                <p>当前值使为${zhishiDoor}门，事业运势平稳。${zhishiDoor === '景' ? '适合文案策划、宣传推广、信息传播。' : '适合保密工作、内部整顿、技术研发。'}</p>
                <p><strong>建议：</strong>稳扎稳打，不宜冒进，做好准备工作。</p>`;
        } else {
            guidance.career = `<p>🌟 <strong>事业运：慎</strong></p>
                <p>当前值使为${zhishiDoor}门，事业方面宜谨慎行事。${zhishiDoor === '伤' ? '注意避免口舌是非，团队协作要多加留意。' : zhishiDoor === '死' ? '不宜开展新业务，适合处理收尾工作。' : zhishiDoor === '惊' ? '注意沟通方式，避免争执冲突。' : '宜静不宜动。'}</p>
                <p><strong>建议：</strong>低调行事，谨言慎行，避免与人争执。</p>`;
        }
        
        // 财运
        if (['生', '开'].includes(zhishiDoor)) {
            guidance.wealth = `<p>💰 <strong>财运：吉</strong></p>
                <p>当前值使为${zhishiDoor}门，财运亨通。适合投资理财、商务洽谈、签订合同、收款结账。</p>
                <p><strong>建议：</strong>把握机会，但也要谨慎决策，不宜盲目投资。</p>`;
        } else if (['休', '景'].includes(zhishiDoor)) {
            guidance.wealth = `<p>💰 <strong>财运：平</strong></p>
                <p>当前财运平稳，适合规划理财方案，学习投资知识。</p>
                <p><strong>建议：</strong>稳守财库，不宜冒险投机。</p>`;
        } else {
            guidance.wealth = `<p>💰 <strong>财运：慎</strong></p>
                <p>当前财运欠佳，不宜进行大额投资或冒险求财。</p>
                <p><strong>建议：</strong>守成为上，量入为出，避免借贷担保。</p>`;
        }
        
        // 感情运
        if (['生', '休', '开'].includes(zhishiDoor)) {
            guidance.relationships = `<p>💕 <strong>感情运：吉</strong></p>
                <p>当前感情运势良好，适合约会表白、求婚订婚、亲友聚会。${['天辅', '天禽'].includes(zhifuStar) ? '天辅/天禽星照临，人际关系和谐。' : ''}</p>
                <p><strong>建议：</strong>敞开心扉，真诚沟通，增进感情。</p>`;
        } else if (['景'].includes(zhishiDoor)) {
            guidance.relationships = `<p>💕 <strong>感情运：平</strong></p>
                <p>当前适合表达情感，写信沟通，线上交流。</p>
                <p><strong>建议：</strong>多倾听对方心声，理解彼此需求。</p>`;
        } else {
            guidance.relationships = `<p>💕 <strong>感情运：慎</strong></p>
                <p>当前感情方面宜保持冷静，避免冲动争吵。</p>
                <p><strong>建议：</strong>退一步海阔天空，多包容理解。</p>`;
        }
    }
    
    // 健康运
    guidance.health = `<p>💪 <strong>健康运</strong></p>
        <p>${getHealthAdvice(jieqi)}</p>`;
    
    // 出行运
    const travelStars = ['天冲', '天辅'];
    const travelDoors = ['休', '开'];
    if (zhifuData && travelStars.includes(zhifuData.star) || (zhishiData && travelDoors.includes(zhishiData.door))) {
        guidance.travel = `<p>🚗 <strong>出行运：吉</strong></p>
            <p>当前适合出行旅游、出差办事、探亲访友。旅途顺利，平安吉祥。</p>
            <p><strong>建议：</strong>提前规划路线，注意交通安全。</p>`;
    } else {
        guidance.travel = `<p>🚗 <strong>出行运：慎</strong></p>
            <p>当前出行宜谨慎，${isYang ? '适合短途出行。' : '不宜长途跋涉。'}</p>
            <p><strong>建议：</strong>如需出行，务必注意安全，小心驾驶。</p>`;
    }
    
    // 学习运
    if (zhifuData && ['天辅', '天心'].includes(zhifuData.star)) {
        guidance.study = `<p>📚 <strong>学习运：吉</strong></p>
            <p>${zhifuData.star}星照临，智慧开启，记忆力增强，理解力提升。</p>
            <p><strong>建议：</strong>适合学习新知识、备考复习、阅读思考。</p>`;
    } else {
        guidance.study = `<p>📚 <strong>学习运：平</strong></p>
            <p>当前适合温故知新，巩固已有知识。</p>
            <p><strong>建议：</strong>保持专注，循序渐进。</p>`;
    }
    
    // 商务运
    if (zhishiData && ['开', '生'].includes(zhishiData.door)) {
        guidance.business = `<p>💼 <strong>商务运：吉</strong></p>
            <p>当前适合商务洽谈、签订合同、拓展业务、招商引资。</p>
            <p><strong>建议：</strong>展现专业能力，把握商业机会。</p>`;
    } else {
        guidance.business = `<p>💼 <strong>商务运：平</strong></p>
            <p>当前适合内部管理、整理资料、规划战略。</p>
            <p><strong>建议：</strong>做好准备工作，等待良机。</p>`;
    }
    
    // 综合运势
    const luckyPalaces = palaceData.filter(p => p.luck === '吉').length;
    const unluckyPalaces = palaceData.filter(p => p.luck === '凶').length;
    
    if (luckyPalaces >= 6) {
        guidance.overall = `<p>✨ <strong>当前综合运势：大吉</strong></p>
            <p>当前吉宫(${luckyPalaces}宫)多于凶宫(${unluckyPalaces}宫)，整体运势极佳，诸事顺遂。</p>
            <p><strong>寄语：</strong>天时地利人和，放手去做，必有收获！</p>`;
    } else if (luckyPalaces >= 4) {
        guidance.overall = `<p>✨ <strong>当前综合运势：吉</strong></p>
            <p>当前吉宫(${luckyPalaces}宫)与凶宫(${unluckyPalaces}宫)相当，整体运势尚可。</p>
            <p><strong>寄语：</strong>趋吉避凶，把握良机，谨慎行事。</p>`;
    } else {
        guidance.overall = `<p>✨ <strong>当前综合运势：慎</strong></p>
            <p>当前凶宫(${unluckyPalaces}宫)较多，整体运势需谨慎。</p>
            <p><strong>寄语：</strong>低调行事，守正辟邪，静待转机。</p>`;
    }
    
    return guidance;
}

// 获取健康建议
function getHealthAdvice(jieqi) {
    const advice = {
        '立春': '肝气当令，宜疏肝理气，多吃绿色蔬菜，适度运动。',
        '雨水': '湿气渐重，宜健脾祛湿，少吃生冷，注意保暖。',
        '惊蛰': '阳气上升，宜晚睡早起，散步踏青，舒畅情志。',
        '春分': '阴阳平衡，宜调和阴阳，饮食均衡，心情舒畅。',
        '清明': '天气转暖，宜踏青郊游，清淡饮食，预防过敏。',
        '谷雨': '雨水充足，宜健脾养胃，多吃豆类，适当进补。',
        '立夏': '心火当令，宜静心养心，清淡饮食，防暑降温。',
        '小满': '湿热交蒸，宜清热利湿，少吃油腻，适度午休。',
        '芒种': '天气炎热，宜清淡饮食，静心防暑，劳逸结合。',
        '夏至': '阳气最盛，宜晚睡早起，防暑降温，养心安神。',
        '小暑': '暑气渐盛，宜清热解暑，多喝水，忌贪凉。',
        '大暑': '一年最热，宜防暑降温，清淡饮食，静心养生。',
        '立秋': '肺气当令，宜滋阴润燥，早睡早起，收敛神气。',
        '处暑': '暑气消退，宜调理脾胃，清淡饮食，适当锻炼。',
        '白露': '天气转凉，宜养阴润燥，添衣保暖，早睡早起。',
        '秋分': '阴阳平衡，宜滋阴润肺，饮食均衡，适度运动。',
        '寒露': '露水变寒，宜温补脾肾，少吃生冷，适当进补。',
        '霜降': '开始降霜，宜防寒保暖，温补身体，早睡晚起。',
        '立冬': '肾气当令，宜养藏阳气，进补养生，注意保暖。',
        '小雪': '天气寒冷，宜温补脾肾，多吃温热食物，适当锻炼。',
        '大雪': '雪量增多，宜防寒保暖，温补身体，静养为主。',
        '冬至': '阴气最盛，宜进补收藏，早睡晚起，保暖防寒。',
        '小寒': '天气寒冷，宜温补身体，多吃温热食物，适度锻炼。',
        '大寒': '一年最冷，宜防寒保暖，静养养生，注意休息。'
    };
    return advice[jieqi] || '根据节气变化调整作息，保持身心健康。';
}