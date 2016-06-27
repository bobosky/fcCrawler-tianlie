package com.zj.bean;
/**
 * 公交信息
 * @author Administrator
 *
 */
public class BusAndStationInputQueueBean {

	/**
	 * 公交车
	 */
	private String busName="";
	/**
	 * 对应的url
	 */
	private String url="";
	public String getBusName() {
		return busName;
	}
	public void setBusName(String busName) {
		this.busName = busName;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
}
