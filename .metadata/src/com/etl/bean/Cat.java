package com.etl.bean;

import java.io.Serializable;

public class Cat implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -923979227178390827L;
	private String cat_1="";
	private String cat_2="";
	private String cat_3="";
	private String business_area="";
	private String district="";
	
	
	public String getCat_1() {
		return cat_1;
	}
	public void setCat_1(String cat_1) {
		this.cat_1 = cat_1;
	}

	public String getCat_2() {
		return cat_2;
	}
	public void setCat_2(String cat_2) {
		this.cat_2 = cat_2;
	}
	public String getCat_3() {
		return cat_3;
	}
	public void setCat_3(String cat_3) {
		this.cat_3 = cat_3;
	}
	public String getBusiness_area() {
		return business_area;
	}
	public void setBusiness_area(String business_area) {
		this.business_area = business_area;
	}
	public String getDistrict() {
		return district;
	}
	public void setDistrict(String district) {
		this.district = district;
	}
	
	
}
