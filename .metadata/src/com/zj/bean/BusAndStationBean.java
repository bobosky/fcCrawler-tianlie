package com.zj.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.util.DateFormat;

public class BusAndStationBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 公交名
	 */
	private String busName="";
	
	/**
	 * 单位 为分钟
	 */
	private String intervalTime="";
	/**
	 * 起点站首末时间
	 */
	private String upStationTime="";
	/**
	 * 终点站首末时间
	 */
	private String downStationTime="";
	/**
	 * 票价信息
	 */
	private String payDisc="";
	
	/**
	 * 运营公司
	 */
	private String busCompany="";

	/**
	 * 对应的公交车站
	 */
	private List<String> stationName=new LinkedList<String>();
	/**
	 * 返乘路线
	 */
	private List<String> stationNameRever=new LinkedList<String>();
	/**
	 * 爬取时间
	 */
	private String crawlerTime=DateFormat.parse(new Date());
	/**
	 * 判断是否有效
	 * @return
	 */
	public boolean isOk()
	{
		if(stationName.size()==0)
		{
			return false;
		}
		return true;
	}
	
	public List<String> getStationName() {
		return stationName;
	}

	public void setStationName(List<String> stationName) {
		this.stationName = stationName;
	}

	public void addStationName(String stationName) {
		this.stationName.add(stationName);
	}
	
	public List<String> getStationNameRever() {
		return stationNameRever;
	}

	public void setStationNameRever(List<String> stationNameRever) {
		this.stationNameRever = stationNameRever;
	}

	public void addStationNameRever(String stationNameRever) {
		this.stationNameRever.add(stationNameRever);
	}
	
	public String getBusName() {
		return busName;
	}

	public void setBusName(String busName) {
		this.busName = busName;
	}

	

	public String getIntervalTime() {
		return intervalTime;
	}

	public void setIntervalTime(String intervalTime) {
		this.intervalTime = intervalTime;
	}


	public String getUpStationTime() {
		return upStationTime;
	}

	public void setUpStationTime(String upStationTime) {
		this.upStationTime = upStationTime;
	}

	public String getDownStationTime() {
		return downStationTime;
	}

	public void setDownStationTime(String downStationTime) {
		this.downStationTime = downStationTime;
	}

	public String getPayDisc() {
		return payDisc;
	}

	public void setPayDisc(String payDisc) {
		this.payDisc = payDisc;
	}

	public String getBusCompany() {
		return busCompany;
	}

	public void setBusCompany(String busCompany) {
		this.busCompany = busCompany;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getCrawlerTime() {
		return crawlerTime;
	}

	public void setCrawlerTime(String crawlerTime) {
		this.crawlerTime = crawlerTime;
	}
	
	
	
}
