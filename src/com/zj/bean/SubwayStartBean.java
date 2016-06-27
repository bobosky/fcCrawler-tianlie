package com.zj.bean;

import java.io.Serializable;

/**
 * 地铁信息的始发车信息
 * @author Administrator
 *
 */
public class SubwayStartBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 发车类型
	 */
	private String departCategory="";
	
	/**
	 * 发车时间时间
	 */
	private String time="";
	/**
	 * 包括全程 空 以及对应的站信息
	 */
	private String stationInfo="";
	public String getDepartCategory() {
		return departCategory;
	}
	public void setDepartCategory(String departCategory) {
		this.departCategory = departCategory;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	public String getStationInfo() {
		return stationInfo;
	}
	public void setStationInfo(String stationInfo) {
		this.stationInfo = stationInfo;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}
