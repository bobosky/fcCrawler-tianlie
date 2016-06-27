package com.zj.bean;

import java.io.Serializable;

public class CompanyBean3 implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 公司名
	 */
	private String companyName="";
	/**
	 * 分类
	 */
	private String industryCategory="";
	/**
	 * 公司人数
	 */
	private String companyMemberNum="";
	
	/**
	 * 公司分类
	 */
	private String companyCategory="";
	/**
	 * 公司url
	 */
	private String companyUrl="";
	/**
	 * 51job上公司id
	 */
	private String companyCode="";
	/**
	 * subWay 站
	 */
	private String subWayStation=null;
	
	
	private int fansCount=0;
	

	public int getFansCount() {
		return fansCount;
	}

	public void setFansCount(int fansCount) {
		this.fansCount = fansCount;
	}

	public String getSubWayStation() {
		return subWayStation;
	}

	public void setSubWayStation(String subWayStation) {
		this.subWayStation = subWayStation;
	}

	public String getCompanyUrl() {
		return companyUrl;
	}

	public void setCompanyUrl(String companyUrl) {
		this.companyUrl = companyUrl;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	public String getIndustryCategory() {
		return industryCategory;
	}

	public void setIndustryCategory(String industryCategory) {
		this.industryCategory = industryCategory;
	}

	public String getCompanyMemberNum() {
		return companyMemberNum;
	}

	public void setCompanyMemberNum(String companyMemberNum) {
		this.companyMemberNum = companyMemberNum;
	}

	public String getCompanyCategory() {
		return companyCategory;
	}

	public void setCompanyCategory(String companyCategory) {
		this.companyCategory = companyCategory;
	}
	
	
}
