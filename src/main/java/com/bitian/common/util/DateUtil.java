package com.bitian.common.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 日期操作类，主要是用于获得当前日期的字符串和日期格式的转换
 * 
 * @author 何浩
 * 
 */
public class DateUtil {

	public static String[] fs = {
			"yyyy-MM-dd HH:mm:ss",
			"yyyy-MM-dd HH:mm",
			"yyyy-MM-dd"
	};

	public static String defaultPattern="yyyy-MM-dd HH:mm:ss";

	/**
	 * 格式化当前日期
	 * @return
	 */
	public static String formatNow(){
		return format(System.currentTimeMillis(),defaultPattern);
	}

	public static String format(Calendar calendar, String formatStr) {
		DateFormat format = new SimpleDateFormat(formatStr);
		return format.format(calendar.getTime());
	}

	public static String format(long millis, String formatStr) {
		DateFormat format = new SimpleDateFormat(formatStr);
		return format.format(millis);
	}

	public static String format(long millis) {
		return format(millis, defaultPattern);
	}

	public static int getHour() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.HOUR_OF_DAY);
	}

	public static Date date(int year, int month, int day, int hour, int min, int send) {
		Calendar c = Calendar.getInstance();
		c.set(year, month - 1, day, hour, min, send);
		c.set(Calendar.MILLISECOND, hour == 0 ? 0 : 999);
		return c.getTime();
	}

	public static Date getCurrentYearFirstDay() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.MONTH, c.getActualMinimum(Calendar.MONTH));
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getCalendar(c, 0, 0, 0).getTime();
	}

	public static Date getCurrentYearLastDay(boolean notForward) {
		Calendar c = Calendar.getInstance();
		if (!notForward) {
			c.set(Calendar.MONTH, c.getActualMaximum(Calendar.MONTH));
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		return getCalendar(c, 23, 59, 59).getTime();
	}

	public static Date getCurrentQuarterFirstDay() {
		Calendar c = Calendar.getInstance();
		int currentMonth = c.get(Calendar.MONTH) + 1;
		if (currentMonth >= 1 && currentMonth <= 3) {
			c.set(Calendar.MONTH, 0);
		} else if (currentMonth >= 4 && currentMonth <= 6) {
			c.set(Calendar.MONTH, 3);
		} else if (currentMonth >= 7 && currentMonth <= 9) {
			c.set(Calendar.MONTH, 6);
		} else if (currentMonth >= 10 && currentMonth <= 12) {
			c.set(Calendar.MONTH, 9);
		}
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getCalendar(c, 0, 0, 0).getTime();
	}

	public static Date getCurrentQuarterLastDay(boolean notForward) {
		Calendar c = Calendar.getInstance();
		if (!notForward) {
			int currentMonth = c.get(Calendar.MONTH) + 1;
			if (currentMonth >= 1 && currentMonth <= 3) {
				c.set(Calendar.MONTH, 2);
			} else if (currentMonth >= 4 && currentMonth <= 6) {
				c.set(Calendar.MONTH, 5);
			} else if (currentMonth >= 7 && currentMonth <= 9) {
				c.set(Calendar.MONTH, 8);
			} else if (currentMonth >= 10 && currentMonth <= 12) {
				c.set(Calendar.MONTH, 11);
			}
			c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		}
		return getCalendar(c, 23, 59, 59).getTime();
	}

	public static Date getCurrentMonthFirstDay() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getCalendar(c, 0, 0, 0).getTime();
	}

	public static Date getCurrentMonthLastDay() {
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getCalendar(c, 23, 59, 59).getTime();
	}

	public static Date getLastMonthFirstDay() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getCalendar(c, 0, 0, 0).getTime();
	}

	public static Date getLastMonthLastDay() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, -1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getCalendar(c, 23, 59, 59).getTime();
	}

	public static Date getNextMonthFirstDay() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMinimum(Calendar.DAY_OF_MONTH));
		return getCalendar(c, 0, 0, 0).getTime();
	}

	public static Date getNextMonthLastDay() {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
		return getCalendar(c, 23, 59, 59).getTime();
	}

	public static Date getCurrentWeekFirstDay() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0) {
			day_of_week = 7;
		}
		c.add(Calendar.DATE, -day_of_week + 1);
		return getCalendar(c, 0, 0, 0).getTime();
	}

	public static Date getCurrentWeekLastDay() {
		Calendar c = Calendar.getInstance();
		int day_of_week = c.get(Calendar.DAY_OF_WEEK) - 1;
		if (day_of_week == 0) {
			day_of_week = 7;
		}
		c.add(Calendar.DATE, -day_of_week + 7);
		return getCalendar(c, 23, 59, 59).getTime();
	}

	public static Date getDate(long time, int hour, int min, int second) {
		return getDate(new Date(time), hour, min, second);
	}

	public static Date getDate(Date date, int hour, int min, int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return getCalendar(calendar, hour, min, second).getTime();
	}

	public static Calendar getCalendar(Calendar c, int hour, int min, int second) {
		c.set(Calendar.HOUR_OF_DAY, hour);
		c.set(Calendar.MINUTE, min);
		c.set(Calendar.SECOND, second);
		c.set(Calendar.MILLISECOND, second == 0 ? 0 : 999);
		return c;
	}

	public static long getStartTimestamp(long time) {
		if (time <= 0) {
			return time;
		}
		return getDate(time, 0, 0, 0).getTime();
	}

	public static long getEndTimestamp(long time) {
		if (time <= 0) {
			return time;
		}
		return getDate(time, 23, 59, 59).getTime();
	}

	public static Calendar addDate(int days, int hour, int min, int second) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, days);
		return getCalendar(c, hour, min, second);
	}

	public static Calendar addMonth(int monthds) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, monthds);
		return c;
	}

	public static int getDay(long millis) {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(millis);
		return c.get(Calendar.DAY_OF_MONTH);
	}

	public static long parseDateStringToTimestamp(String str) {
		return parseDateStringToTimestamp(str,fs);
	}

	public static long parseDateStringToTimestamp(String str, String... formats) {
		if (StringUtils.isEmpty(str)) {
			return 0;
		}
		Date date = parseDate(str, formats);
		return date != null ? date.getTime() : 0;
	}

	public static Date parseDate(String str, String... formats) {
		try {
			return DateUtils.parseDate(str, formats);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}

}
