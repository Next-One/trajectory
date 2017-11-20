package com.wang.dp;

/**
 * @author root
 *字段适用对象可以在序列化的时候
 *决定要不要
 *基本类型一定会被初始化
 */
public class Point implements Comparable<Point> {
	private Integer id; // 该点的索引,id只能在完整轨迹中使用
	private Long ts;    // 时间ms
	private Double lat; // 纬度
	private Double alt; // 海拔
	private Double lon; // 经度
	private String d;   // 该点的描述
	private Boolean isEnd;//是不是结束点

	public String getD() {
		return d;
	}

	public Boolean getIsEnd() {
		return isEnd;
	}

	public void setIsEnd(Boolean isEnd) {
		this.isEnd = isEnd;
	}
	
	public void setD(String d) {
		this.d = d;
	}

	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}

	public Double getLat() {
		return lat;
	}

	public void setLat(Double lat) {
		this.lat = lat;
	}

	public Double getAlt() {
		return alt;
	}

	public void setAlt(Double alt) {
		this.alt = alt;
	}

	public Double getLon() {
		return lon;
	}

	public void setLon(Double lon) {
		this.lon = lon;
	}

	/* 
	 * 方便生成空间点
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return lon + " " + lat;
	}

	@Override
	public int compareTo(Point o) {
		return ts.compareTo(o.ts);
	}
}