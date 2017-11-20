package com.wang.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class LoadUtil {
	private static final Logger LOG = LoggerFactory.getLogger(LoadUtil.class);
	private static final String POSTFIX=".properties";
	/**
	 * 把第二个拷贝到第一个后面
	 * @param first
	 * @param second
	 * @return
	 */
	public static <T> T[] concat(T[] first, T[] second) {
		T[] result = Arrays.copyOf(first, first.length + second.length);
		System.arraycopy(second, 0, result, first.length, second.length);
		return result;
	}
	
	public static Properties createProps(String path) {
		Properties props = new Properties();
		if(!path.contains(".pro"))
			path += POSTFIX;
        try {
			props.load(Thread.currentThread().getContextClassLoader()
					.getResourceAsStream(path));
		} catch (IOException e) {
			LOG.error(path+" is miss!",e);
		}
		return props;
	}
}
