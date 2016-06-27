package com.zj.bean;

import java.io.Serializable;
import java.util.LinkedList;

public class BussinessArea implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4472947967861240814L;
	private String city="";
	/**
	 * 城市id
	 */
	private int cityId=0;
	/**
	 * 商圈信息
	 */
	private LinkedList<BussinessAreaDesc> bussinessAreaAll=new LinkedList<BussinessAreaDesc>();
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public int getCityId() {
		return cityId;
	}
	public void setCityId(int cityId) {
		this.cityId = cityId;
	}
	public LinkedList<BussinessAreaDesc> getBussinessAreaAll() {
		return bussinessAreaAll;
	}
	public void setBussinessAreaAll(LinkedList<BussinessAreaDesc> bussinessAreaAll) {
		this.bussinessAreaAll = bussinessAreaAll;
	}
	public void addBussinessAreaAll(BussinessAreaDesc bussinessAreaAll) {
		this.bussinessAreaAll.add(bussinessAreaAll);
	}
	
	
	
}
