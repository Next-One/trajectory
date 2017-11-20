package com.wang.dp;

import java.util.List;

import com.wang.utils.DPUtil;

/**
 * @author root
 *一条轨迹
 */
public class Line {
	
	private final Point start; //开始点
	private final Point end;   //结束点
	private final double a;//将单条线段的长度存起来，避免在方法中重复计算
	private final List<Point> trail ;
	private int index;// 最大距离所对应曲线上的点的索引号
	private double distance;// 最大距离，与阈值比较

	public List<Point> getLinePoints() {
		return trail;
	}

	public Line(List<Point> trail) {
		this(trail,0,trail.size()-1);
	}

	public Line(List<Point> trail, int start, int end) {
		this.trail = trail;
		this.start = trail.get(start);
		this.end = trail.get(end);
		this.a = DPUtil.getDistance(this.start, this.end);
		computeLineToLine(start,end);
	}

	public Point getStart() {
		return start;
	}

	
	public Point getEnd() {
		return end;
	}

	public double getDistance() {
		return distance;
	}

	public void setDistance(double distance) {
		this.distance = distance;
	}
	
	
	/*
	 * 利用三角形面积相等的方法求得点到直线的距离
	 */
	public double distToSegment(Point pX){
		double b=Math.abs(DPUtil.getDistance(start, pX));
		double c=Math.abs(DPUtil.getDistance(end, pX));		
		double p=(a+b+c)/2.0;		
		double s=Math.sqrt(Math.abs(p*(p-a)*(p-b)*(p-c)));
		return 2.0*s/a;
	}
	
	
	public void computeLineToLine(int start, int end) {
		double maxDist = Double.MIN_VALUE;
		double dist = 0;
		index = 0;
//		[1,n-1]
		for (int i = start+1; i < end-1; i++) {
			dist = distToSegment(trail.get(i));
			if (dist > maxDist) {
				maxDist = dist;
				index = i;
			}
		} 
		this.distance = maxDist;
	}

	
	
	public int getIndex() {
		return index;
	}


	
}