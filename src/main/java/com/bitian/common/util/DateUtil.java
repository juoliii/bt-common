package com.bitian.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 日期操作工具类，提供日期格式化、解析、计算等常用功能。
 * <p>
 * 内部使用 {@link ThreadLocal} 缓存 {@link SimpleDateFormat} 以保证线程安全。
 * </p>
 *
 * @author 何浩
 */
public class DateUtil {

    private static final Logger LOG = Logger.getLogger(DateUtil.class.getName());

    // ==================== 常量 ====================

    /** 默认的日期时间格式 */
    public static String defaultPattern = "yyyy-MM-dd HH:mm:ss";

    /** 常用解析格式数组（保持原有公共字段兼容） */
    public static String[] fs = {
            "yyyy-MM-dd HH:mm:ss",
            "yyyy-MM-dd HH:mm",
            "yyyy-MM-dd"
    };

    /** 纯日期格式 */
    public static final String PATTERN_DATE = "yyyy-MM-dd";

    /** 日期时间格式（不含秒） */
    public static final String PATTERN_DATETIME_SHORT = "yyyy-MM-dd HH:mm";

    /** 日期时间格式（含秒） */
    public static final String PATTERN_DATETIME = "yyyy-MM-dd HH:mm:ss";

    /** 紧凑日期格式 */
    public static final String PATTERN_DATE_COMPACT = "yyyyMMdd";

    /** 紧凑日期时间格式 */
    public static final String PATTERN_DATETIME_COMPACT = "yyyyMMddHHmmss";

    /** 一天的起始时刻 */
    private static final int DAY_START_HOUR = 0;
    private static final int DAY_START_MINUTE = 0;
    private static final int DAY_START_SECOND = 0;

    /** 一天的结束时刻 */
    private static final int DAY_END_HOUR = 23;
    private static final int DAY_END_MINUTE = 59;
    private static final int DAY_END_SECOND = 59;

    /** 每个季度的起始月份（0-based） */
    private static final int[] QUARTER_START_MONTHS = {0, 3, 6, 9};

    /** 每个季度的结束月份（0-based） */
    private static final int[] QUARTER_END_MONTHS = {2, 5, 8, 11};

    // ==================== ThreadLocal 缓存 ====================

    /**
     * 使用 ThreadLocal + ConcurrentHashMap 缓存 SimpleDateFormat，
     * 避免每次调用都创建新实例，同时保证线程安全。
     */
    private static final ThreadLocal<Map<String, SimpleDateFormat>> FORMAT_CACHE =
            ThreadLocal.withInitial(ConcurrentHashMap::new);

    private static SimpleDateFormat getFormat(String pattern) {
        return FORMAT_CACHE.get().computeIfAbsent(pattern, SimpleDateFormat::new);
    }

    // ==================== 私有构造 ====================

    private DateUtil() {
        // 工具类禁止实例化
    }

    // ==================== 格式化方法 ====================

    /**
     * 使用默认格式格式化当前时间
     *
     * @return 格式化后的日期字符串
     */
    public static String formatNow() {
        return format(System.currentTimeMillis(), defaultPattern);
    }

    /**
     * 使用指定格式格式化当前时间
     *
     * @param formatStr 日期格式
     * @return 格式化后的日期字符串
     */
    public static String formatNow(String formatStr) {
        return format(System.currentTimeMillis(), formatStr);
    }

    /**
     * 格式化 Calendar 对象
     *
     * @param calendar  日历对象
     * @param formatStr 日期格式
     * @return 格式化后的日期字符串
     */
    public static String format(Calendar calendar, String formatStr) {
        return getFormat(formatStr).format(calendar.getTime());
    }

    /**
     * 格式化时间戳（毫秒）
     *
     * @param millis    毫秒时间戳
     * @param formatStr 日期格式
     * @return 格式化后的日期字符串
     */
    public static String format(long millis, String formatStr) {
        return getFormat(formatStr).format(new Date(millis));
    }

    /**
     * 使用默认格式格式化时间戳
     *
     * @param millis 毫秒时间戳
     * @return 格式化后的日期字符串
     */
    public static String format(long millis) {
        return format(millis, defaultPattern);
    }

    /**
     * 使用默认格式格式化 Date 对象
     *
     * @param date 日期对象
     * @return 格式化后的日期字符串，date 为 null 时返回空字符串
     */
    public static String format(Date date) {
        return format(date, defaultPattern);
    }

    /**
     * 使用指定格式格式化 Date 对象
     *
     * @param date      日期对象
     * @param formatStr 日期格式
     * @return 格式化后的日期字符串，date 为 null 时返回空字符串
     */
    public static String format(Date date, String formatStr) {
        if (date == null) {
            return "";
        }
        return getFormat(formatStr).format(date);
    }

    // ==================== 时间字段获取 ====================

    /**
     * 获取当前小时（24 小时制）
     *
     * @return 当前小时 0-23
     */
    public static int getHour() {
        return Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取指定时间戳对应的日（月中第几天）
     *
     * @param millis 毫秒时间戳
     * @return 日（1-31）
     */
    public static int getDay(long millis) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(millis);
        return c.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取指定日期所在的季度（1-4）
     *
     * @param date 日期对象
     * @return 季度 1-4
     */
    public static int getQuarter(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.MONTH) / 3 + 1;
    }

    /**
     * 获取当前季度（1-4）
     *
     * @return 当前季度
     */
    public static int getCurrentQuarter() {
        return getQuarter(new Date());
    }

    /**
     * 获取指定日期是星期几（1=周一, 7=周日）
     *
     * @param date 日期对象
     * @return 星期几（ISO 标准，1-7）
     */
    public static int getDayOfWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int dow = c.get(Calendar.DAY_OF_WEEK) - 1;
        return dow == 0 ? 7 : dow;
    }

    // ==================== 日期构造 ====================

    /**
     * 构造指定日期时间的 Date 对象
     *
     * @param year  年
     * @param month 月（1-12，自然月）
     * @param day   日
     * @param hour  时
     * @param min   分
     * @param send  秒
     * @return Date 对象
     */
    public static Date date(int year, int month, int day, int hour, int min, int send) {
        Calendar c = Calendar.getInstance();
        c.set(year, month - 1, day, hour, min, send);
        c.set(Calendar.MILLISECOND, hour == 0 ? 0 : 999);
        return c.getTime();
    }

    // ==================== 年度边界 ====================

    /**
     * 获取当年第一天的起始时间
     *
     * @return 当年 1月1日 00:00:00
     */
    public static Date getCurrentYearFirstDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.MONTH, c.getActualMinimum(Calendar.MONTH));
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return setDayStart(c).getTime();
    }

    /**
     * 获取当年最后一天的结束时间
     *
     * @param notForward 若为 true，则使用当前日期而非年末
     * @return 当年 12月31日 23:59:59 或当前日期 23:59:59
     */
    public static Date getCurrentYearLastDay(boolean notForward) {
        Calendar c = Calendar.getInstance();
        if (!notForward) {
            c.set(Calendar.MONTH, c.getActualMaximum(Calendar.MONTH));
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        return setDayEnd(c).getTime();
    }

    // ==================== 季度边界 ====================

    /**
     * 获取当前季度第一天的起始时间
     *
     * @return 当前季度首日 00:00:00
     */
    public static Date getCurrentQuarterFirstDay() {
        Calendar c = Calendar.getInstance();
        int quarterIndex = c.get(Calendar.MONTH) / 3;
        c.set(Calendar.MONTH, QUARTER_START_MONTHS[quarterIndex]);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return setDayStart(c).getTime();
    }

    /**
     * 获取当前季度最后一天的结束时间
     *
     * @param notForward 若为 true，则使用当前日期而非季末
     * @return 当前季度末日 23:59:59 或当前日期 23:59:59
     */
    public static Date getCurrentQuarterLastDay(boolean notForward) {
        Calendar c = Calendar.getInstance();
        if (!notForward) {
            int quarterIndex = c.get(Calendar.MONTH) / 3;
            c.set(Calendar.MONTH, QUARTER_END_MONTHS[quarterIndex]);
            c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        }
        return setDayEnd(c).getTime();
    }

    // ==================== 月度边界 ====================

    /**
     * 获取当月第一天的起始时间
     *
     * @return 当月首日 00:00:00
     */
    public static Date getCurrentMonthFirstDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return setDayStart(c).getTime();
    }

    /**
     * 获取当月最后一天的结束时间
     *
     * @return 当月末日 23:59:59
     */
    public static Date getCurrentMonthLastDay() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return setDayEnd(c).getTime();
    }

    /**
     * 获取上月第一天的起始时间
     *
     * @return 上月首日 00:00:00
     */
    public static Date getLastMonthFirstDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return setDayStart(c).getTime();
    }

    /**
     * 获取上月最后一天的结束时间
     *
     * @return 上月末日 23:59:59
     */
    public static Date getLastMonthLastDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, -1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return setDayEnd(c).getTime();
    }

    /**
     * 获取下月第一天的起始时间
     *
     * @return 下月首日 00:00:00
     */
    public static Date getNextMonthFirstDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
        return setDayStart(c).getTime();
    }

    /**
     * 获取下月最后一天的结束时间
     *
     * @return 下月末日 23:59:59
     */
    public static Date getNextMonthLastDay() {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 1);
        c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
        return setDayEnd(c).getTime();
    }

    // ==================== 周边界 ====================

    /**
     * 获取当前周第一天（周一）的起始时间
     *
     * @return 本周一 00:00:00
     */
    public static Date getCurrentWeekFirstDay() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        c.add(Calendar.DATE, -dayOfWeek + 1);
        return setDayStart(c).getTime();
    }

    /**
     * 获取当前周最后一天（周日）的结束时间
     *
     * @return 本周日 23:59:59
     */
    public static Date getCurrentWeekLastDay() {
        Calendar c = Calendar.getInstance();
        int dayOfWeek = c.get(Calendar.DAY_OF_WEEK) - 1;
        if (dayOfWeek == 0) {
            dayOfWeek = 7;
        }
        c.add(Calendar.DATE, -dayOfWeek + 7);
        return setDayEnd(c).getTime();
    }

    // ==================== 日期时间设置 ====================

    /**
     * 根据时间戳设置指定的时、分、秒
     *
     * @param time   毫秒时间戳
     * @param hour   时
     * @param min    分
     * @param second 秒
     * @return 设置后的 Date
     */
    public static Date getDate(long time, int hour, int min, int second) {
        return getDate(new Date(time), hour, min, second);
    }

    /**
     * 为 Date 设置指定的时、分、秒
     *
     * @param date   日期对象
     * @param hour   时
     * @param min    分
     * @param second 秒
     * @return 设置后的 Date
     */
    public static Date getDate(Date date, int hour, int min, int second) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return getCalendar(calendar, hour, min, second).getTime();
    }

    /**
     * 为 Calendar 设置指定的时、分、秒及毫秒
     *
     * @param c      日历对象
     * @param hour   时
     * @param min    分
     * @param second 秒
     * @return 设置后的 Calendar
     */
    public static Calendar getCalendar(Calendar c, int hour, int min, int second) {
        c.set(Calendar.HOUR_OF_DAY, hour);
        c.set(Calendar.MINUTE, min);
        c.set(Calendar.SECOND, second);
        c.set(Calendar.MILLISECOND, second == 0 ? 0 : 999);
        return c;
    }

    /**
     * 获取某一天的起始时间戳（00:00:00.000）
     *
     * @param time 毫秒时间戳
     * @return 当天起始毫秒时间戳；若 time <= 0 原样返回
     */
    public static long getStartTimestamp(long time) {
        if (time <= 0) {
            return time;
        }
        return getDate(time, DAY_START_HOUR, DAY_START_MINUTE, DAY_START_SECOND).getTime();
    }

    /**
     * 获取某一天的结束时间戳（23:59:59.999）
     *
     * @param time 毫秒时间戳
     * @return 当天结束毫秒时间戳；若 time <= 0 原样返回
     */
    public static long getEndTimestamp(long time) {
        if (time <= 0) {
            return time;
        }
        return getDate(time, DAY_END_HOUR, DAY_END_MINUTE, DAY_END_SECOND).getTime();
    }

    // ==================== 日期偏移 ====================

    /**
     * 以当前日期为基准偏移指定天数，并设置时分秒
     *
     * @param days   偏移天数（正数向后，负数向前）
     * @param hour   时
     * @param min    分
     * @param second 秒
     * @return 偏移后的 Calendar
     */
    public static Calendar addDate(int days, int hour, int min, int second) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, days);
        return getCalendar(c, hour, min, second);
    }

    /**
     * 以当前日期为基准偏移指定月数
     *
     * @param monthds 偏移月数（正数向后，负数向前）
     * @return 偏移后的 Calendar
     */
    public static Calendar addMonth(int monthds) {
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, monthds);
        return c;
    }

    /**
     * 对指定日期增加/减少天数
     *
     * @param date 基准日期
     * @param days 偏移天数
     * @return 偏移后的新 Date 对象
     */
    public static Date addDays(Date date, int days) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, days);
        return c.getTime();
    }

    /**
     * 对指定日期增加/减少小时
     *
     * @param date  基准日期
     * @param hours 偏移小时数
     * @return 偏移后的新 Date 对象
     */
    public static Date addHours(Date date, int hours) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, hours);
        return c.getTime();
    }

    /**
     * 对指定日期增加/减少分钟
     *
     * @param date    基准日期
     * @param minutes 偏移分钟数
     * @return 偏移后的新 Date 对象
     */
    public static Date addMinutes(Date date, int minutes) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minutes);
        return c.getTime();
    }

    // ==================== 解析方法 ====================

    /**
     * 使用默认格式数组解析日期字符串为时间戳
     *
     * @param str 日期字符串
     * @return 毫秒时间戳；解析失败或为空返回 0
     */
    public static long parseDateStringToTimestamp(String str) {
        return parseDateStringToTimestamp(str, fs);
    }

    /**
     * 使用指定格式数组解析日期字符串为时间戳
     *
     * @param str     日期字符串
     * @param formats 尝试的日期格式
     * @return 毫秒时间戳；解析失败或为空返回 0
     */
    public static long parseDateStringToTimestamp(String str, String... formats) {
        if (StringUtils.isEmpty(str)) {
            return 0;
        }
        Date date = parseDate(str, formats);
        return date != null ? date.getTime() : 0;
    }

    /**
     * 使用指定格式数组解析日期字符串
     *
     * @param str     日期字符串
     * @param formats 尝试的日期格式
     * @return 解析后的 Date 对象；解析失败返回 null
     */
    public static Date parseDate(String str, String... formats) {
        try {
            return DateUtils.parseDate(str, formats);
        } catch (ParseException e) {
            LOG.log(Level.WARNING, "Failed to parse date string: " + str, e);
        }
        return null;
    }

    /**
     * 使用默认格式数组解析日期字符串
     *
     * @param str 日期字符串
     * @return 解析后的 Date 对象；解析失败返回 null
     */
    public static Date parseDate(String str) {
        return parseDate(str, fs);
    }

    // ==================== 日期判断 ====================

    /**
     * 判断两个日期是否为同一天（忽略时分秒）
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return 是否同一天
     */
    public static boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        Calendar c1 = Calendar.getInstance();
        c1.setTime(date1);
        Calendar c2 = Calendar.getInstance();
        c2.setTime(date2);
        return c1.get(Calendar.YEAR) == c2.get(Calendar.YEAR)
                && c1.get(Calendar.DAY_OF_YEAR) == c2.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * 判断指定日期是否为今天
     *
     * @param date 日期对象
     * @return 是否为今天
     */
    public static boolean isToday(Date date) {
        return isSameDay(date, new Date());
    }

    /**
     * 判断指定年份是否为闰年
     *
     * @param year 年份
     * @return 是否为闰年
     */
    public static boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * 判断 date1 是否在 date2 之前
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return date1 是否早于 date2
     */
    public static boolean isBefore(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.before(date2);
    }

    /**
     * 判断 date1 是否在 date2 之后
     *
     * @param date1 日期1
     * @param date2 日期2
     * @return date1 是否晚于 date2
     */
    public static boolean isAfter(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return false;
        }
        return date1.after(date2);
    }

    /**
     * 判断指定日期是否在给定的区间内（含边界）
     *
     * @param date  待检查的日期
     * @param start 区间起始
     * @param end   区间结束
     * @return 是否在区间内
     */
    public static boolean isBetween(Date date, Date start, Date end) {
        if (date == null || start == null || end == null) {
            return false;
        }
        return !date.before(start) && !date.after(end);
    }

    // ==================== 日期计算 ====================

    /**
     * 计算两个日期之间相差的天数（date2 - date1）
     *
     * @param date1 较早的日期
     * @param date2 较晚的日期
     * @return 相差天数（可以为负）
     */
    public static long daysBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        long diff = date2.getTime() - date1.getTime();
        return TimeUnit.MILLISECONDS.toDays(diff);
    }

    /**
     * 计算两个日期之间相差的小时数（date2 - date1）
     *
     * @param date1 较早的日期
     * @param date2 较晚的日期
     * @return 相差小时数（可以为负）
     */
    public static long hoursBetween(Date date1, Date date2) {
        if (date1 == null || date2 == null) {
            return 0;
        }
        long diff = date2.getTime() - date1.getTime();
        return TimeUnit.MILLISECONDS.toHours(diff);
    }

    /**
     * 根据出生日期计算年龄
     *
     * @param birthDate 出生日期
     * @return 周岁年龄；birthDate 为 null 返回 0
     */
    public static int getAge(Date birthDate) {
        if (birthDate == null) {
            return 0;
        }
        Calendar birth = Calendar.getInstance();
        birth.setTime(birthDate);
        Calendar now = Calendar.getInstance();

        int age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
        if (now.get(Calendar.DAY_OF_YEAR) < birth.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return Math.max(age, 0);
    }

    /**
     * 获取指定月份的天数
     *
     * @param year  年份
     * @param month 月份（1-12，自然月）
     * @return 该月的天数
     */
    public static int getDaysOfMonth(int year, int month) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month - 1);
        return c.getActualMaximum(Calendar.DAY_OF_MONTH);
    }

    // ==================== 内部辅助方法 ====================

    /**
     * 将 Calendar 设置为一天的起始时刻（00:00:00.000）
     */
    private static Calendar setDayStart(Calendar c) {
        return getCalendar(c, DAY_START_HOUR, DAY_START_MINUTE, DAY_START_SECOND);
    }

    /**
     * 将 Calendar 设置为一天的结束时刻（23:59:59.999）
     */
    private static Calendar setDayEnd(Calendar c) {
        return getCalendar(c, DAY_END_HOUR, DAY_END_MINUTE, DAY_END_SECOND);
    }
}
