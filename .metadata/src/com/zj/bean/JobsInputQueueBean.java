package com.zj.bean;

/**
 * job信息的读取队列bean
 * @author Administrator
 *
 */
public class JobsInputQueueBean {
	/**
	 * 使用的url
	 */
	private String url="";
	/**
	 * 对应的地铁线路
	 * 如果 subWayLine==null
	 * 则表示为热门地标
	 */
	private String subWayLine="";
	/**
	 * subWay 站
	 */
	private String subWayStation="";
	/**
	 * 地铁线路对应的编码
	 */
	private String subWayLineCode="";
	
	/**
	 * 战对应的编号
	 */
	private String subWayStationCode="";

	/**
	 * 当前页面
	 */
	private int currentPage=0;
	/**
	 * 搜索到的第几公里
	 */
	private int nearKm=1;
	/**
	 * 工作类型
	 */
	private int workCategory=-1;
	
	private LationLngLat location=null;
	
	
	public LationLngLat getLocation() {
		return location;
	}

	public void setLocation(LationLngLat location) {
		this.location = location;
	}

	public int getNearKm() {
		return nearKm;
	}

	public void setNearKm(int nearKm) {
		this.nearKm = nearKm;
	}

	public int getCurrentPage() {
		return currentPage;
	}

	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}

	public String getSubWayLine() {
		return subWayLine;
	}

	public void setSubWayLine(String subWayLine) {
		this.subWayLine = subWayLine;
	}

	public String getSubWayStation() {
		return subWayStation;
	}

	public void setSubWayStation(String subWayStation) {
		this.subWayStation = subWayStation;
	}

	public String getSubWayStationCode() {
		return subWayStationCode;
	}

	public void setSubWayStationCode(String subWayStationCode) {
		this.subWayStationCode = subWayStationCode;
	}

	public String getSubWayLineCode() {
		return subWayLineCode;
	}

	public void setSubWayLineCode(String subWayLineCode) {
		this.subWayLineCode = subWayLineCode;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public int getWorkCategory() {
		return workCategory;
	}

	public void setWorkCategory(int workCategory) {
		this.workCategory = workCategory;
	}
	
	
}
