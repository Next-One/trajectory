package com.wang.utils

import org.apache.spark.SparkConf
import org.apache.spark.streaming.Seconds
import org.apache.spark.streaming.StreamingContext
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.streaming.dstream.InputDStream
import org.apache.kafka.clients.consumer.ConsumerRecord

object SparkUtil {
	/**
	 * @param path 配置文件路径
	 * @param batchDur 时间间隔
	 */
	def getSSC(path: String,checkPoint:String, batchDur: Int): StreamingContext = {
		val props = LoadUtil.createProps(path)
		val conf = new SparkConf()
		val e = props.propertyNames
		//  		利用java资源文件设置spark APP的属性
		while (e.hasMoreElements()) {
			val key = e.nextElement().toString();
			val value = props.getProperty(key)
			conf.set(key, value)
		}
		val ssc = new StreamingContext(conf, Seconds(batchDur))
		ssc.checkpoint(checkPoint)	
		ssc
	}

	/**
	 * @param path
	 * @param ssc
	 * @return
	 */
	def getDStream(path: String, ssc: StreamingContext): InputDStream[ConsumerRecord[String, String]] = {
		val props = LoadUtil.createProps(path)
		val topic = props.getProperty("topics");
		val topics = topic.split(',')
		import scala.collection.mutable.HashMap
		val kafkaParam = new HashMap[String, String]
		val e = props.propertyNames
		//  		利用java资源文件设置spark APP的属性
		while (e.hasMoreElements()) {
			val key = e.nextElement().toString();
			if (!key.equals("topics")) {
				val value = props.getProperty(key)
				kafkaParam.put(key, value)
			}
		}
		//创建DStream，返回接收到的输入数据
		KafkaUtils.createDirectStream[String, String](ssc,
			PreferConsistent,
			Subscribe[String, String](topics, kafkaParam))
	}

}