package com.wang.traj

import java.sql.Connection
import java.sql.ResultSet

import org.apache.commons.dbcp2.BasicDataSource
import org.apache.commons.dbcp2.BasicDataSourceFactory

import com.wang.utils.LoadUtil
import java.sql.Statement

object PostgisPool {
	var ds: BasicDataSource = null
	def getDataSource = {
		if (ds == null) {
			val prop = LoadUtil.createProps("database")
			ds = BasicDataSourceFactory.createDataSource(prop)
		}
		ds
	}

	def getConnection: Connection = {
		val connect = if (ds != null)
			ds.getConnection
		else
			getDataSource.getConnection
		connect
	}

	def shutDownDataSource: Unit = if (ds != null) { ds.close() }

	def close(connect: Connection): Unit = {
		if (connect != null) { connect.close() }
	}

	def close(connect: Connection, ps: Statement): Unit = {
		if (ps != null) { ps.close }
		close(connect)
	}

	def close(connect: Connection, ps: Statement, rs: ResultSet): Unit = {
		if (rs != null) { rs.close }
		close(connect, ps)
	}

}  