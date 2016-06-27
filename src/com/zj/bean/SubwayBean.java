package com.zj.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 地铁站 对应的地铁线路信息
 * @author Administrator
 *
 */
public class SubwayBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String subwayLine="";
	/**
	 * 地铁线路信息
	 */
	private String subwayStation="";
	/**
	 * 发车信息
	 */
	private List<SubwayStartBean> departDesc=new LinkedList<SubwayStartBean>();
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
	public List<SubwayStartBean> getDepartDesc() {
		return departDesc;
	}
	public void setDepartDesc(List<SubwayStartBean> departDesc) {
		this.departDesc = departDesc;
	}
	public void addDepartDesc(SubwayStartBean departDesc) {
		this.departDesc.add(departDesc);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
