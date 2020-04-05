package com.bitian.common.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期操作类，主要是用于获得当前日期的字符串和日期格式的转换
 * 
 * @author 何浩
 * 
 */
public class DateUtil {

	/**
	 * 只有日期的格式
	 */
	public static String ONLY_DATE = "yyyy-MM-dd";
	public static String ONLY_DATE_N = "yyyyMMdd";

	/**
	 * 只有时间的格式
	 */
	public static String ONLY_TIME = "HH:mm:ss";
	public static String ONLY_TIME_N = "HHmmss";

	/**
	 * 有时间和日期的格式
	 */
	public static String DATE_TIME = "yyyy-MM-dd HH:mm:ss";
	public static String DATE_TIME_N = "yyyyMMddHHmmss";

	private static ThreadLocal<SimpleDateFormat> formatPool = new ThreadLocal<SimpleDateFormat>() {
		@Override
		protected synchronized SimpleDateFormat initialValue() {
			return new SimpleDateFormat();
		}
	};

	/**
	 * 获取当前日期的字符串
	 * 
	 * @return 当前日期的字符串 例如：1999-09-09
	 */
	public static String getCurrentDate() {
		String date = "";
		SimpleDateFormat sf=formatPool.get();
		sf.applyPattern(ONLY_DATE);
		date = sf.format(new Date());
		return date;
	}

	/**
	 * 获取当前日期的字符串
	 * 
	 * @return 当前日期的字符串 例如：1999-09-09
	 */
	public static String getCurrentDate(Date d) {
		if (d == null)
			return "";
		String date = "";
		SimpleDateFormat sf=formatPool.get();
		sf.applyPattern(ONLY_DATE);
		date = sf.format(d);
		return date;
	}

	/**
	 * 获取当前时间的字符串
	 * 
	 * @return 当前时间的字符串 例如：10:20:10
	 */
	public static String getCurrentTime() {
		String date = "";
		SimpleDateFormat sf=formatPool.get();
		sf.applyPattern(ONLY_TIME);
		date = sf.format(new Date());
		return date;
	}

	/**
	 * 获取当前时间和日期的字符串
	 * 
	 * @return 当前日期和时间的字符串 例如：1999-09-09 10:20:10
	 */
	public static String getCurrentDateTime() {
		String date = "";
		SimpleDateFormat sf=formatPool.get();
		sf.applyPattern(DATE_TIME);
		date = sf.format(new Date());
		return date;
	}

	/**
	 * 获取当前时间和日期的无格式的字符串 例如：19990909102010
	 * 
	 * @return 当前时间和日期的无格式的字符串
	 */
	public static String getCurrentDateTimeNoFormat() {
		String date = "";
		SimpleDateFormat sf=formatPool.get();
		sf.applyPattern(DATE_TIME_N);
		date = sf.format(new Date());
		return date;
	}

	/**
	 * 将日期字符串转换为要显示的格式的字符串
	 * 
	 * @param str
	 *            日期字符串
	 * @param str
	 *            原来的日期字符串格式
	 * @param str
	 *            要转换成为的日期字符串格式
	 * @return 要显示的格式的字符串
	 * @throws ParseException 
	 */
	public static String formatDateTime(String str, String srcPattern,
			String destPattern) throws ParseException  {
		String date = "";
		SimpleDateFormat sf=formatPool.get();
		sf.applyPattern(srcPattern);
		Date d=sf.parse(str);
		sf.applyPattern(destPattern);
		date = sf.format(d);
		return date;
	}

	public static String formatDate(String str) throws ParseException {
		return formatDateTime(str, ONLY_DATE_N, ONLY_DATE);
	}

	public static Date getDateObjectFromString(String date) throws ParseException {
		Date ret = null;
		SimpleDateFormat sf=formatPool.get();
		sf.applyPattern(DATE_TIME);
		ret = sf.parse(date);
		return ret;
	}
	
	public static long getTimeMillisFromDateString(String time,String pattern){
		Date date = null;
		SimpleDateFormat sf=formatPool.get();
		sf.applyPattern(pattern);
		try {
			date = sf.parse(time);
		} catch (ParseException e) {
			return 0;
		}
		return date.getTime();
	}
	
	/**
	 * 获取当前毫秒数
	 * @return
	 */
	public static long getCurrentDateTimeAsLong() {
		return System.currentTimeMillis();
	}

	public static void main(String[] args) {
		long time=getTimeMillisFromDateString(getCurrentDate(), ONLY_DATE);
		System.out.println(time);
	}
}
