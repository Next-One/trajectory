package com.wang.test;

import java.io.Closeable;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author root 公用工具类
 */
public class Util {
	private final static Logger LOG = LoggerFactory.getLogger(Util.class);

	public static void close(Closeable... io) {
		for (Closeable temp : io) {
			try {
				if (temp != null) {
					temp.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 获取文件路径的前缀
	 * 
	 * @param filePath
	 * @return
	 */
	public static String getPrefix(String filePath) {
		int index = filePath.lastIndexOf('.');
		if (index == -1)
			return filePath;
		return filePath.substring(0, index);
	}

	/**
	 * 根据一个日期格式和日期字符串解析对应日期
	 * 
	 * @param sdf
	 * @param dateString
	 * @return
	 */
	public static Date parse(SimpleDateFormat sdf, String dateString) {
		Date date = null;
		try {
			date = sdf.parse(dateString);
		} catch (ParseException e) {
			LOG.error("dateString is error!", e);
		}
		return date;
	}

	/**
	 * 获取两个时间段
	 * 
	 * @param ts
	 * @return "ddHHmmss"返回这样的字符串
	 */
	public static String getDuration(long startTime, long endTime) {
		int ts = (int) (endTime / 1000 - startTime / 1000);
		int day = ts / (3600 * 24);
		int sub = ts % (3600 * 24);
		int hour = sub / 3600;
		sub = sub % 3600;
		int min = sub / 60;
		int sec = sub % 60;
		return add0(day, 2) + add0(hour, 2) + add0(min, 2) + add0(sec, 2);

	}

	/**
	 * 让一个线程睡眠一段时间
	 * 
	 * @param time
	 */
	public static void sleep(long time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			LOG.error(Thread.currentThread().getName(), e);
		}
	}

	/**
	 * 在字符串前面加0使其满足固定长度
	 * 
	 * @param num
	 * @param count
	 * @return
	 */
	public static String add0(String num, int count) {
		while (num.length() < count)
			num = "0" + num;
		return num;
	}

	public static String add0(int num, int count) {
		return add0(num + "", count);
	}

	public static String add0(long num, int count) {
		return add0(num + "", count);
	}

}
