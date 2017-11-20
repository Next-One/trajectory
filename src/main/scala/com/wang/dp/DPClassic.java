package com.wang.dp;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class DPClassic {
	
	private Double threshold;// 距离阈值
	
	private List<Point> trajectory;// 原始轨迹
	
	public double getThreshold() {
		return threshold;
	}


	public void setThreshold(Double threshold) {
		this.threshold = threshold;
	}


	public List<Point> getTrajectory() {
		return trajectory;
	}


	public void setTrajectory(List<Point> trajectory) {
		this.trajectory = trajectory;
	}


	

	public DPClassic(List<Point> trajectory, double threshold) {
		this.threshold = threshold;
		this.trajectory = trajectory;
	}
	
	public DPClassic() {
	}

	
	/**
	 * 开始压缩轨迹
	 * @return 
	 */
	public List<Point> startCompress() {
		int len = trajectory.size() - 1;
		List<Point> resultTrail = new ArrayList<>();
		resultTrail.add(trajectory.get(0));
		resultTrail.add(trajectory.get(len));
		compressLine(0, len,trajectory,resultTrail);
		// 对结果进行排序
		Collections.sort(resultTrail);
		return resultTrail;
	}

	/**
	 * 对线段进行压缩
	 * 
	 * @param start
	 *            开始下标
	 * @param end
	 *            结束下标
	 * @param resultTrail 
	 * @param trail 
	 */
	private void compressLine(int start, int end, List<Point> trail, List<Point> resultTrail) {
		// 至少有3个点才可以压缩,这里的首尾点已存储
		if (end - start < 1)
			return;
		Line line = new Line(trail, start, end);
		double dist = line.getDistance();
		if (dist > threshold) {
			int index = line.getIndex();
			resultTrail.add(trail.get(index));
			compressLine(start, index,trail,resultTrail);// 分两段进行压缩，分段的位置极为距离最大的点
			compressLine(index, end,trail,resultTrail);
		}
	}
	

	

	

}
