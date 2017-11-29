package com.wang.traj

import org.apache.spark.HashPartitioner
import org.apache.spark.streaming.Minutes
import java.{ util => ju }
import com.fasterxml.jackson.databind.ObjectMapper
import com.wang.utils.SparkUtil
import scala.collection.JavaConversions.seqAsJavaList
import com.wang.utils.StringUtil
import com.wang.slidewindow.Point
import com.wang.slidewindow.SlideWindow

object Trajectory {
	val LineSize = 10;
	def start(threshold: String) = {
		val ssc = SparkUtil.getSSC("sparkconf", "/spark/checkpoints", 5)
		val kafkaStream = SparkUtil.getDStream("kafka", ssc)

		//		将值提取出来在进行，序列化，设置检查点
		val keyPoints = kafkaStream.map(keyPoint => (keyPoint.key, keyPoint.value))
		keyPoints.checkpoint(Minutes(10))
		val mapper = new ObjectMapper
		//		key,point
		val keyPointsStream = keyPoints.flatMap { keyPoint =>
			try {
				val key = keyPoint._1
				val point = mapper.readValue(keyPoint._2, classOf[Point])
				Some((key, point))
			} catch {
				case e: Exception => None
			}
		}
		//		更新用户点 函数
		//入参是三元组遍历器，三个元组分别表示Key、当前时间间隔内产生的对应于Key的Value集合、上一个时间点的状态 
		val updateKeyPoints = (iterator: Iterator[(String, Seq[Point], Option[List[Point]])]) => {
			iterator.flatMap(t => {
				val newPoints = t._2.toList
				var prevPoints = t._3.getOrElse(Nil);
				prevPoints = drop(prevPoints)
				//				也有可能和空值连接Nil
				val currentPoints = prevPoints ::: newPoints
				Some(currentPoints)
			}.filter(!_.isEmpty).map(line => (t._1, line)))
		}
		//		更新状态
		val stateStream = keyPointsStream.updateStateByKey[List[Point]](
			updateKeyPoints,
			new HashPartitioner(ssc.sparkContext.defaultParallelism),
			true)

		//		filter  过滤
		//		map		压缩
		//		foreach 存储
		stateStream.foreachRDD { rdd =>
			rdd.filter(keyLine => getHandleTraj(keyLine._2))
				.map(trajCompress(_, threshold.toDouble))
				.foreachPartition(trajToPostgis)
		}
		ssc.start()
		ssc.awaitTermination()
	}

	/**
	 * 测试代码
	 * @param keyLine
	 * @return
	 */
	def showData(keyLine: (String, List[Point])) = {
		val key = keyLine._1
		val line = keyLine._2
		val size = line.size
		var sql = ""
		if (size < LineSize && !line.isEmpty && isEnd(line.last)) {
			val wkt = StringUtil.getLineString(keyLine._2)
			val uid = parseTID(key)
			sql = s"end($uid,$wkt)"
		} else if (size >= LineSize && line.exists(isStart)) {
			val starts = for (p <- line if isStart(p)) yield p
			val wkt = StringUtil.getLineString(starts)
			val uid = parseTID(key)
			sql = s"start($uid,$wkt)"
		}
		s"${size} => ${sql}"
	}

	/**
	 * 根据该丢弃规则，找出下一轮的应该保留的点
	 * @param line
	 */
	def drop(line: List[Point]): List[Point] = {
		var result = line
		val len = line.length
		if (len >= LineSize) {
			result = line.find(isStart) match {
				case Some(_) => line.dropWhile(isNull)
				case None => line.drop(len - LineSize + 1)
			}
		} else if (!line.isEmpty && isEnd(line.last)) {
			result = Nil
		}
		result
	}

	/**
	 * 表示无状态的中间点
	 * @param p
	 * @return
	 */
	def isNull(p: Point): Boolean = {
		val isStart = p.getIsStart
		isStart == null
	}

	/**
	 * 检查是不是开始点
	 * @param p
	 * @return
	 */
	def isStart(p: Point): Boolean = {
		val isStart = p.getIsStart
		isStart != null && isStart == true
	}

	/**
	 * 判断该轨迹是否要处理
	 * @param line
	 * @return
	 */
	def getHandleTraj(line: List[Point]): Boolean = {
		if (line.size >= LineSize)
			true
		else
			!line.isEmpty && isEnd(line.last)
	}

	/**
	 * 判断轨迹点是否结束
	 * @param p
	 * @return
	 */
	def isEnd(p: Point): Boolean = {
		val last = p.getIsEnd
		last != null && last == true
	}

	/**
	 * 获取主键前面的userID
	 * @param tid
	 * @return
	 */
	def parseTID(tid: String): Int = {
		val index = tid.indexOf('_')
		tid.substring(0, index).toInt
	}

	/**
	 * 将用户的轨迹压缩之后的结果存入postgis
	 * @param records 压缩后的用户记录
	 */
	def trajToPostgis(keyLines: Iterator[(String, List[Point])]) {
		val connect = PostgisPool.getConnection
		connect.setAutoCommit(false)
		val stmt = connect.createStatement()
		keyLines.foreach(keyLine => {
			val line = keyLine._2
			val tid = keyLine._1
			val size = line.size
			if (size < LineSize && !line.isEmpty && isEnd(line.last)) {
				val wkt = StringUtil.getLineString(keyLine._2)
				val uid = parseTID(tid)
				val sql = s"SELECT updateOrInsertLine('$tid',$uid,'$wkt')"
				stmt.addBatch(sql)
			} else if (size >= LineSize && line.exists(isStart)) {
				val starts = for (p <- line if isStart(p)) yield p
				val wkt = StringUtil.getLineString(starts)
				val uid = parseTID(tid)
				val sql = s"SELECT updateOrInsertLine('$tid',$uid,'$wkt')"
				stmt.addBatch(sql)
			}

		})
		stmt.executeBatch()
		connect.commit()
		PostgisPool.close(connect, stmt)
	}

	/**
	 * 目前使用滑动窗口算法对轨迹进行压缩
	 * @param keyLine
	 * @param threshold
	 * @return
	 */
	def trajCompress(keyLine: (String, List[Point]), threshold: Double): (String, List[Point]) = {
		val key = keyLine._1
		val line = keyLine._2
		if (line.size >= LineSize) {
			val result = SlideWindow.slide(line, LineSize, threshold)
			return (key, result)
		}
		return (key, line)
	}

}


