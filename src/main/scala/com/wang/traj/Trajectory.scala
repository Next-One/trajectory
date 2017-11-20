package com.wang.traj

import org.apache.spark.HashPartitioner
import org.apache.spark.streaming.Minutes
import java.{ util => ju }
import com.fasterxml.jackson.databind.ObjectMapper
import com.wang.utils.SparkUtil
import scala.collection.JavaConversions.seqAsJavaList
import com.wang.utils.StringUtil
import com.wang.dp.Point
import com.wang.dp.DPClassic

object Trajectory {

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
				var statePoints = t._3.getOrElse(Nil);
				//				如果超过10个点就drop前10个,这里的10个点已经压缩储存过了，可以丢弃了
				if (statePoints.length >= 10) {
					statePoints = statePoints.drop(10)
				} else {
					if (!statePoints.isEmpty && isEnd(statePoints.last)) {
						statePoints = Nil
					}
				}
				//				也有可能和空值连接Nil
				val currentPoints = statePoints ::: newPoints
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
				.foreachPartition(trajToPostgis(_))
		}
		ssc.start()
		ssc.awaitTermination()
	}

	/**
	 * 判断该轨迹是否要处理
	 * @param line
	 * @return
	 */
	def getHandleTraj(line: List[Point]): Boolean = {
		if (line.size >= 10)
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
		val last = p.getIsEnd()
		if (last != null && last == true)
			return true
		return false
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
			val tid = keyLine._1
			val wkt = StringUtil.getLineString(keyLine._2)
			val uid = parseTID(tid)
			val sql = s"SELECT updateOrInsertLine('$tid',$uid,'$wkt')"
			stmt.addBatch(sql)
		})
		stmt.executeBatch()
		connect.commit()
		PostgisPool.close(connect, stmt)
	}

	/**
	 * 目前使用经典DP算法对轨迹进行压缩
	 * @param keyLine
	 * @param threshold
	 * @return
	 */
	def trajCompress(keyLine: (String, List[Point]), threshold: Double): (String, List[Point]) = {
		val key = keyLine._1
		val line = keyLine._2
		if (line.size >= 10) {
			val ps = line.take(10)
			val dp = new DPClassic(ps, threshold)
			val result = dp.startCompress() //压缩完成后会用剩下的轨迹替换原始轨迹
			return (key, javaListToScala(result))
		}
		return (key, line)
	}

	/**
	 * 将java的list转化为scala
	 * 的list
	 * @param line
	 * @return
	 */
	def javaListToScala(line: ju.List[Point]):List[Point] = {
		var l = List[Point]()
		val iter = line.iterator()
		while (iter.hasNext()) {
			val p = iter.next()
			l = l :+ p
		}
		l
	}
}


