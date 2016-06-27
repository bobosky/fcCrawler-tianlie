package com.zj.bean;
/**
 * 地铁公交入队列对应的实体信息
 * @author Administrator
 *
 */
public class SubwayAndBusInputQueueBean {

	/**
	 * 地铁线信息
	 */
	private String subwayLine="";
	/**
	 * 地铁站
	 */
	private String subwayStation="";
	/**
	 * 对应的url
	 */
	private String url="";
	public String getSubwayLine() {
		return subwayLine;
	}
	public void setSubwayLine(String subwayLine) {
		this.subwayLine = subwayLine;
	}
	public String getSubwayStation() {
		return subwayStation;
	}
	public void setSubwayStation(String subwayStation) {
		this.subwayStation = subwayStation;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
