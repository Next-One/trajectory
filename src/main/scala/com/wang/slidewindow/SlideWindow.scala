package com.wang.slidewindow

object SlideWindow {
	/**
	 * 这条line上的点必须大于十个
	 * 在之前过滤会数据修改量
	 * @param line
	 * @param threshold
	 * 滑动只是为了找到开始点，然后做标记，不会修改原始轨迹
	 * 对轨迹打上标记后，就可以对其进行存储，和丢弃
	 * 如果没有被打上标记，则可以在下一轮更新时
	 * 丢弃至lineSize-1即可
	 */
	def slide(line: List[Point], lineSize: Int, threshold: Double): List[Point] = {
		val size = line.size
		var count = 0;
		while (size - count >= lineSize) {
			val right = size - count - lineSize
			//				斩头去尾，确保有十个点
			val lineNum = line.drop(count).dropRight(right)
			val l = new Line(lineNum)
			val indexMaxD = l.computeMaxD
			//				这里可能会找到多个符合条件的点
			if (indexMaxD._2 >= threshold) {
				val index = indexMaxD._1 + count
				line(index).setIsStart(true)
			}
			count += 1
		}
		line
	}
}