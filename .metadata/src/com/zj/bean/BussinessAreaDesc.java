package com.zj.bean;

import java.io.Serializable;
import java.util.LinkedList;

public class BussinessAreaDesc implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3671510034058882560L;
	/**
	 * 区
	 */
	private AreaBean area=null;
	private LinkedList<AreaBean> bussinessArea=new LinkedList<AreaBean>();
	public AreaBean getArea() {
		return area;
	}
	public void setArea(AreaBean area) {
		this.area = area;
	}
	public LinkedList<AreaBean> getBussinessArea() {
		return bussinessArea;
	}
	public void setBussinessArea(LinkedList<AreaBean> bussinessArea) {
		this.bussinessArea = bussinessArea;
	}
	
	public void addBussinessArea(AreaBean bussinessArea) {
		this.bussinessArea.add(bussinessArea);
	}
	
}
