package com.zj.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.util.DateFormat;
/**
 * 地铁信息及 出口对应的公交车信息 实体
 * @author Administrator
 *
 */
public class SubwayAndBusBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * 地铁线
	 */
	private String subwayLine="";
	/**
	 * 对应的地铁站
	 */
	private String subwayStation="";
	
	/**
	 * 地铁对应的相关信息
	 */
	private List<SubwayBean> subWayDesc=new LinkedList<SubwayBean>();
	/**
	 * 地铁口对应的公交信息
	 */
	private List<SubwayAndBusSonBean> portAndBus=new LinkedList<SubwayAndBusSonBean>();
	
	
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
		if(subWayDesc.size()==0 &&portAndBus.size()==0)
		{
			return false;
		}
		return true;
	}
	
	
	
	public String getCrawlerTime() {
		return crawlerTime;
	}



	public void setCrawlerTime(String crawlerTime) {
		this.crawlerTime = crawlerTime;
	}



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

	public List<SubwayBean> getSubWayDesc() {
		return subWayDesc;
	}
	public void setSubWayDesc(List<SubwayBean> subWayDesc) {
		this.subWayDesc = subWayDesc;
	}
	public void addSubWayDesc(SubwayBean subWayDesc) {
		this.subWayDesc.add(subWayDesc);
	}
	public List<SubwayAndBusSonBean> getPortAndBus() {
		return portAndBus;
	}
	public void setPortAndBus(List<SubwayAndBusSonBean> portAndBus) {
		this.portAndBus = portAndBus;
	}
	public void addPortAndBus(SubwayAndBusSonBean portAndBus) {
		this.portAndBus.add(portAndBus);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
	
}
