package com.wang.utils

import com.wang.dp.Point

object StringUtil {
	def getLineString(line: java.util.List[Point]) = {
		val sb = new StringBuilder
		sb.append("LINESTRING(")
		var count = 0
		val total = line.size
		while (count < total) {
			val p = line.get(count)
			if (count != total - 1) {
				sb.append(p.toString + ",")
			} else {
				sb.append(p.toString + ")")
			}
			count += 1
		}
		sb.toString
	}
}