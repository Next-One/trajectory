package com.wang.slidewindow

import com.wang.utils.GisUtil

class Line(list: List[Point]) {
	val a: Double = GisUtil.getDistance(list.head, list.last);

	def distToSegment(pX: Point): Double = {
		val b = Math.abs(GisUtil.getDistance(list.head, pX));
		val c = Math.abs(GisUtil.getDistance(list.last, pX));
		val p = (a + b + c) / 2.0;
		val s = Math.sqrt(Math.abs(p * (p - a) * (p - b) * (p - c)));
		2.0 * s / a;
	}

	def computeMaxD() = computeLineToLine(1, list.length)
	
	def computeLineToLine(start: Int, end: Int): (Int, Double) = {
		var maxDist = .0;
		var dist = .0;
		var index = 0;

		//		[1,n-1]
		for (i <- start until end) {
			dist = distToSegment(list(i));
			if (dist > maxDist) {
				maxDist = dist;
				index = i;
			}
		}
		(index, maxDist)
	}

}