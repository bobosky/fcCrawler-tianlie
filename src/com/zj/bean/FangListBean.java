package com.zj.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 搜房的改版后列表页对应的数据信息
 * @author Administrator
 *
 */
public class FangListBean  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 城市名
	 */
	private String cityName="";
	/**
	 * 写字楼对应的code
	 */
	private String buildingCode="";
	
	/**
	 * 租金
	 * 首个都是不限
	 */
	private ArrayList<String> hire=new ArrayList<String>();
	
	/**
	 * 面积
	 * 首个都是不限
	 */
	private ArrayList<String> space=new ArrayList<String>();
	
	/**
	 * 区域信息
	 * 首个都是不限
	 */
	private ArrayList<String> area=new ArrayList<String>();

	public String getBuildingCode() {
		return buildingCode;
	}

	public void setBuildingCode(String buildingCode) {
		this.buildingCode = buildingCode;
	}

	public ArrayList<String> getHire() {
		return hire;
	}

	public void setHire(ArrayList<String> hire) {
		this.hire = hire;
	}
	
	public void addHire(String hire)
	{
		this.hire.add(hire);
	}

	public ArrayList<String> getSpace() {
		return space;
	}
	
	public void addSpace(String space)
	{
		this.space.add(space);
	}

	public void setSpace(ArrayList<String> space) {
		this.space = space;
	}

	public ArrayList<String> getArea() {
		return area;
	}

	public void setArea(ArrayList<String> area) {
		this.area = area;
	}
	
	public void addArea(String area)
	{
		this.area.add(area);
	}

	public String getCityName() {
		return cityName;
	}

	public void setCityName(String cityName) {
		this.cityName = cityName;
	}
	
	
	
}
