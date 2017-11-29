package com.wang.utils;

import java.io.Closeable;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.wang.slidewindow.Point;

/**
 * @author root
 */
public class GisUtil {
	public static final SimpleDateFormat TT_FORMAT = new SimpleDateFormat("ddHHmmss");
	public static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final long EARTH_RADIUS = 6378137L;	//赤道半径(单位m) 这里必须用long类型
	
	
	/**
	 * x 为经度y为纬度
	 * x 在180W - 180S
	 * y 在90S - 90N
	 * @param long1
	 *            经度1
	 * @param lat1
	 *            维度1
	 * @param long2
	 *            经度2
	 * @param lat2
	 *            纬度2
	 * @return
	 */
	public static double getDistance(double long1, double lat1, double long2, double lat2) {
		double a, b;
		lat1 = lat1 * Math.PI / 180.0;
		lat2 = lat2 * Math.PI / 180.0;
		a = lat1 - lat2;
		b = (long1 - long2) * Math.PI / 180.0;
		double d;
		double sa2, sb2;
		sa2 = Math.sin(a / 2.0);
		sb2 = Math.sin(b / 2.0);
		d = 2 * EARTH_RADIUS * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * 
				Math.cos(lat2) * sb2 * sb2));
		return  d;
	}
	
	public static double getDistance(Point p1, Point p2) {
		return getDistance(p1.getLon(), p1.getLat(), p2.getLon(), p2.getLat());
	}

	/**
	 * 
	 * @param lat_a
	 *            经度1
	 * @param lng_a
	 *            纬度1
	 * @param lat_b
	 *            经度2
	 * @param lng_b
	 *            纬度2
	 * @return
	 */
	public static double getAngle(double lng_a,double lat_a, double lng_b,  double lat_b) {

		double y = Math.sin(lng_b - lng_a) * Math.cos(lat_b);
		double x = Math.cos(lat_a) * Math.sin(lat_b) - Math.sin(lat_a) * 
				Math.cos(lat_b) * Math.cos(lng_b - lng_a);
		double brng = Math.atan2(y, x);

		brng = Math.toDegrees(brng);
		if (brng < 0)
			brng = brng + 360;
		return brng;
	}
	
	/*
	 * 利用三角形面积相等的方法求得点到直线的距离
	 */
	public static double distToSegment(Point pA, Point pB, Point pX) {
		double a = Math.abs(getDistance(pA, pB));
		double b = Math.abs(getDistance(pA, pX));
		double c = Math.abs(getDistance(pB, pX));
		double p = (a + b + c) / 2.0;
		double s = Math.sqrt(p * (p - a) * (p - b) * (p - c));
		return 2.0 * s / a;
	}
	/*
	 * 将角度转化为弧度
	 */
	public static double Rad(double d){
		return d*Math.PI/180.0;
	}

	/*
	 * 计算两点之间的距离
	 */
	public static double distance(Point pA,Point pB){
		double radLatA=Rad(pA.getLat());		//pA的纬度转换成度
		double radLonA=Rad(pA.getLon());		//pA的经度转换成度
		double radLatB=Rad(pB.getLat());		//pB的纬度转换成度
		double radLonB=Rad(pB.getLon());		//pB的纬度转换成度
		
		if (radLatA < 0)
			radLatA = Math.PI / 2 + Math.abs(radLatA);// south  
        if (radLatA > 0)
        	radLatA = Math.PI / 2 - Math.abs(radLatA);// north  
        if (radLonA < 0)
        	radLonA = Math.PI * 2 - Math.abs(radLonA);// west  
        if (radLatB < 0)
        	radLatB = Math.PI / 2 + Math.abs(radLatB);// south  
        if (radLatB > 0)
        	radLatB = Math.PI / 2 - Math.abs(radLatB);// north  
        if (radLonB < 0)
        	radLonB = Math.PI * 2 - Math.abs(radLonB);// west  
        
        double x1 = EARTH_RADIUS * Math.cos(radLonA) * Math.sin(radLatA);
        double y1 = EARTH_RADIUS * Math.sin(radLonA) * Math.sin(radLatA);
        double z1 = EARTH_RADIUS * Math.cos(radLatA);

        double x2 = EARTH_RADIUS * Math.cos(radLonB) * Math.sin(radLatB);
        double y2 = EARTH_RADIUS * Math.sin(radLonB) * Math.sin(radLatB);
        double z2 = EARTH_RADIUS * Math.cos(radLatB);

        double d = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
        //余弦定理求夹角  
        double theta = Math.acos(1-(d * d) / (2 * EARTH_RADIUS*EARTH_RADIUS));
        double dist = theta * EARTH_RADIUS;
        return dist; 
	}

	
	public static void close(Closeable ...io){
		for (Closeable temp : io) {
			try {
				if(temp != null){
					temp.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
}
