package com.wang.test

import java.util.ArrayList
import com.wang.utils.StringUtil
import com.wang.dp.Point

object TestWKT extends App {
	val ps = new ArrayList[Point]();

	for (i <- 1 to 10) {
		val p = new Point();
		p.setLat(i * 2 + .1);
		p.setLon(i * 9 + .1);
		ps.add(p);
	}

	val wkt = StringUtil.getLineString(ps);
	println(wkt);
	
	val sql = "insert into public.trajectory(uid,geom) values (" + 
	1 + ",st_setsrid(st_geomfromewkt('" + wkt + "'),4326))"
					println(sql)
}