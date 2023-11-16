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

	public static Date now() {
		return new Date();
	}

	public static String format(Calendar calendar, String formatStr) {
		if (StringUtils.isEmpty(formatStr)) {
			formatStr = "yyyy-MM-dd HH:mm";
		}
		DateFormat format = new SimpleDateFormat(formatStr);
		return format.format(calendar.getTime());
	}

	public static String format(long millis, String formatStr) {
		if (StringUtils.isEmpty(formatStr)) {
			formatStr = "yyyy-MM-dd HH:mm";
		}
		DateFormat format = new SimpleDateFormat(formatStr);
		return format.format(millis);
	}

	public static String format(long millis) {
		return format(millis, "yyyy-MM-dd HH:mm:ss");
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

	public static Date now(int hour, int min, int send) {
		return getCalendar(Calendar.getInstance(), hour, min, send).getTime();
	}

	public static Date yesterday(int hour, int min, int send) {
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DAY_OF_MONTH, -1);
		return getCalendar(c, hour, min, send).getTime();
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

	public static long parseTimestamp(String str, String... formats) {
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

	public static long parseDateStrToLong(String str) {
		try {
			return DateUtils.parseDate(str, fs).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;
	}

	public static long getStartTimestamp(String date) {
		int length = StringUtils.length(date);
		if (length == 7) {
			Date d = parseDate(date, "yyyy-MM");
			if (d != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				c.set(Calendar.DAY_OF_MONTH, 1);
				return getCalendar(c, 0, 0, 0).getTimeInMillis();
			}
		} else if (length == 10) {
			Date d = parseDate(date, "yyyy-MM-dd");
			if (d != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				return getCalendar(c, 0, 0, 0).getTimeInMillis();
			}
		}
		return 0;
	}

	public static long getEndTimestamp(String date) {
		int length = StringUtils.length(date);
		if (length == 7) {
			Date d = parseDate(date, "yyyy-MM");
			if (d != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
				return getCalendar(c, 23, 59, 59).getTimeInMillis();
			}
		} else if (length == 10) {
			Date d = parseDate(date, "yyyy-MM-dd");
			if (d != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				return getCalendar(c, 23, 59, 59).getTimeInMillis();
			}
		} else if (length > 7 && length < 10) {
			Date d = parseDate(date, "yyyy-MM-dd");
			if (d != null) {
				Calendar c = Calendar.getInstance();
				c.setTime(d);
				return getCalendar(c, 23, 59, 59).getTimeInMillis();
			}
		}
		return 0;
	}

	public static String loadCurrentDateTime(int type) {
		SimpleDateFormat sdf = null;
		if (type == 0) {
			sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		} else if (type == 1) {
			sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		} else if (type == 2) {
			sdf = new SimpleDateFormat("yyyy-MM-dd");
		} else if (type == 3) {
			sdf = new SimpleDateFormat("yyyyMMdd");
		} else if (type == 4) {
			sdf = new SimpleDateFormat("yyyy");
		} else if (type == 5) {
			sdf = new SimpleDateFormat("yyyy-MM");
		} else if (type == 6) {
			sdf = new SimpleDateFormat("yyyyMM");
		} else if (type == 7) {
			sdf = new SimpleDateFormat("MM");
		} else if (type == 8) {
			sdf = new SimpleDateFormat("yyyyMMddHHmmssS");
		}
		return sdf.format(new Date());
	}

	public static String getSuggstionShowTimeStr(long showTime) {
		if (showTime > 0) {
			Calendar now = Calendar.getInstance();
			Calendar c = Calendar.getInstance();
			c.setTimeInMillis(showTime);
			if (now.get(Calendar.YEAR) == c.get(Calendar.YEAR)) {
				if (now.get(Calendar.MONTH) == c.get(Calendar.MONTH)) {
					int nowday = now.get(Calendar.DAY_OF_MONTH);
					int cday = c.get(Calendar.DAY_OF_MONTH);
					if (nowday == cday) {
						return format(c, "HH:mm");
					} else if (nowday == cday + 1) {
						return format(c, "昨天 HH:mm");
					}
				}
				return format(c, "MM月dd日 HH:mm");
			}
			return format(c, "yyyy年MM月dd日 HH:mm");
		}
		return "";
	}


	public static String getNeedTimeSubtractSystemTime(String needTime) {
		long time = DateUtil.getEndTimestamp(needTime);
		long sysTime = System.currentTimeMillis();
		long milliseconds;
		boolean isGo = false;
		if (time > sysTime) {
			milliseconds = time - sysTime;
			isGo = true;
		} else {
			milliseconds = sysTime - time;
		}
		long seconds = milliseconds / 1000; // 转换为秒
		long minutes = seconds / 60; // 转换为分钟
		long hours = minutes / 60; // 转换为小时
		long days = hours / 24; // 转换为天数
		if (!isGo) {
			return String.valueOf("-" + days);
		}
		return String.valueOf(days);

	}
}
