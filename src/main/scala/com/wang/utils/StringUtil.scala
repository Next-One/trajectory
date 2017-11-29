package com.wang.utils

import com.wang.slidewindow.Point

object StringUtil {
	def getLineString(line: List[Point]) = {
		val sb = new StringBuilder
		sb.append("LINESTRING(")
		var count = 0
		val total = line.size

		if (total > 1) {
			while (count < total) {
				val p = line(count)
				if (count != total - 1) {
					sb.append(s"${p.getLon} ${p.getLat},")
				} else {
					sb.append(s"${p.getLon} ${p.getLat})")
				}
				count += 1
			}
		}else if(total == 1){
			val p = line.head
			val ps = s"${p.getLon} ${p.getLat}"
			sb.append(ps+",")
			sb.append(ps+")")
		}
		sb.toString
	}
}