package com.zj.bean;

import java.io.Serializable;

public class FangOldBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3200303354930445955L;
	private Long fangCode=0L;
	private String url="";
	private LationLngLat location=null;
	
	private String city="";
	
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public Long getFangCode() {
		return fangCode;
	}
	public void setFangCode(Long fangCode) {
		this.fangCode = fangCode;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public LationLngLat getLocation() {
		return location;
	}
	public void setLocation(LationLngLat location) {
		this.location = location;
	}
	
	
	
}
