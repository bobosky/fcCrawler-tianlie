package com.zj.bean;

import java.io.Serializable;


public class Company51JobBean implements Serializable,Comparable<Company51JobBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 公司code
	 */
	private long companyCode=0L;
	/**
	 * 公司poi
	 */
	private LationLngLat location=null;
	/**
	 * 公司名
	 */
	private String companyName="";
	/**
	 * 当前页
	 */
	private int currentPage=1;
	
	
	/**
	 * 公司地址
	 */
	private String companyUrl="";
	
	
	public int getCurrentPage() {
		return currentPage;
	}
	public void setCurrentPage(int currentPage) {
		this.currentPage = currentPage;
	}
	public long getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(long companyCode) {
		this.companyCode = companyCode;
	}
	public LationLngLat getLocation() {
		return location;
	}
	public void setLocation(LationLngLat location) {
		this.location = location;
	}
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	public String getCompanyUrl() {
		return companyUrl;
	}
	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}
	@Override
	public int compareTo(Company51JobBean o) {
		// TODO Auto-generated method stub
		return Long.compare(companyCode,o.companyCode);
	}
	public boolean equals(Object in)
	{
		Company51JobBean st=(Company51JobBean)in;
		return this.companyCode==st.companyCode;
	}
	public int hashCode()
	{
	//	System.out.println(this.word+":hashCode:"+this.word.hashCode());
		return (int) (this.companyCode%Integer.MAX_VALUE);
	}

	
	
}
