package com.wang.test

import scala.io.Source
import org.slf4j.LoggerFactory
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import com.wang.slidewindow.Point
import com.wang.slidewindow.SlideWindow

/**
 * 滑动窗口测试
 * @author root
 * @datetime 2017年11月22日 下午3:39:16
 */
object TestSlideWindow extends App {
	val LOG = LoggerFactory.getLogger(TestSlideWindow.getClass);
	val format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	val DF = new DecimalFormat("0.000000");
	val ADF = new DecimalFormat("0.00");
	def getLine(path: String) = {
		val lines = Source.fromFile(path).getLines()
		lines.filter(filterLine).flatMap { line =>
			try {
				val info = line.split(",")
				val x = info(1).toDouble
				val y = info(0).toDouble
				val z = info(3).toDouble
				val time = info(5) + " " + info(6)
				val date = Util.parse(format, time)
				val ts = date.getTime();
				val point = new Point();
				point.setLon(DF.format(x).toDouble);
				point.setLat(DF.format(y).toDouble);
				point.setTs(ts);
				point.setAlt(ADF.format(z).toDouble);
				Some(point)
			} catch {
				case e: Exception => None
			}
		}
	}

	def filterLine(line: String) = {
		line.length() > 50 || line.indexOf(",") != -1
	}

	def isStart(p: Point): Boolean = {
		val isStart = p.getIsStart
		isStart != null && isStart == true
	}


	def testSlide(src: String, lineSize:Int,threshold: Double) = {
		val lines = getLine(src).toList
		val time = new TimeEscape
		val result = SlideWindow.slide(lines, lineSize, threshold)
		val starts = for (p <- result if isStart(p)) yield p
		val sourceSize = lines.size
		val resultSize = starts.size
		val rate = (1 - resultSize * 1.0 / sourceSize) * 100
		(sourceSize, resultSize, rate, time.elapsedTime() + "s")
	}

	
	val src = "g:/testdata/001/20081113234943.plt"
	val threshold = 5
	val lineSize = 10
//	(5568,682,87.75143678160919,0.67s)
	println(testSlide(src,lineSize,threshold))

}