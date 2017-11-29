package com.wang.test

import java.util.ArrayList

import com.wang.slidewindow.Point
import com.wang.utils.StringUtil
import com.wang.slidewindow.SlideWindow

object TestWKT extends App {
	var ps = List[Point]();

	for (i <- 1 to 10) {
		val p = new Point
		p.setLat(i * 2 + .1);
		p.setLon(i * 9 + .1);
		ps = ps :+ p
	}

	val wkt = StringUtil.getLineString(ps);
	println(wkt);
//	LINESTRING(9.1 2.1,18.1 4.1,27.1 6.1,36.1 8.1,45.1 10.1,54.1 12.1,63.1 14.1,72.1 16.1,81.1 18.1,90.1 20.1)
	val point = new Point()
	point.setLat(23)
	point.setLon(90)
	val line = List(point)
	val wkt2 = StringUtil.getLineString(line)
//	LINESTRING(90.0 23.0,90.0 23.0)
	println(wkt2)
	
	
}