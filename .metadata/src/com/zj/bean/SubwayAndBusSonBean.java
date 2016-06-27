package com.zj.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
/**
 * 地铁口对应的公交车信息
 * @author Administrator
 *
 */
public class SubwayAndBusSonBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 地铁口信息
	 */
	private String port="a";
	/**
	 * 地铁口对应的公交车信息
	 */
	private List<String> busStation=new LinkedList<String>();
	public String getPort() {
		return port;
	}
	public void setPort(String port) {
		this.port = port;
	}
	public List<String> getBusStation() {
		return busStation;
	}
	public void setBusStation(List<String> busStation) {
		this.busStation = busStation;
	}
	
	public void addBusStation(String busStation) {
		this.busStation.add(busStation);
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
}
