package com.zj.bean;

import java.io.Serializable;

/**
 * 51上公司信息实体
 * @author Administrator
 *
 */
public class Company51JobCompanyBean implements Serializable,Comparable<Company51JobCompanyBean>{

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
	 * 公司地址
	 */
	private String companyUrl="";
	
	/**
	 * 公司名
	 */
	private String companyName="";
	/**
	 * 行业
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
	 * 粉丝数
	 */
	private int fansCount=0;
	/**
	 * 公司地址
	 */
	private String address="";
	/**
	 * 公司简介
	 */
	private String desc="";
	/**
	 * 邮政编码
	 */
	private String postcode="";
	
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
	public int compareTo(Company51JobCompanyBean o) {
		// TODO Auto-generated method stub
		return Long.compare(companyCode,o.companyCode);
	}
	public boolean equals(Object in)
	{
		Company51JobCompanyBean st=(Company51JobCompanyBean)in;
		return this.companyCode==st.companyCode;
	}
	public int hashCode()
	{
	//	System.out.println(this.word+":hashCode:"+this.word.hashCode());
		return (int) (this.companyCode%Integer.MAX_VALUE);
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
	public int getFansCount() {
		return fansCount;
	}
	public void setFansCount(int fansCount) {
		this.fansCount = fansCount;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getPostcode() {
		return postcode;
	}
	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}
	
	
}
