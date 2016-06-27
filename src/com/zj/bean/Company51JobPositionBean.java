package com.zj.bean;

import java.io.Serializable;

/**
 * 51job 公司code 对应code实体
 * @author Administrator
 *
 */
public class Company51JobPositionBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -837550386509783491L;
	/**
	 * 职位 code
	 */
	private long jobCode=0L;
	/**
	 * 公司code
	 */
	private long companyCode=0L;
	/**
	 * 职位url
	 */
	private String positionUrl="";
	/**
	 * 职位名称
	 */
	private String positonName="";
	/**
	 * 工作地点
	 */
	private String workAddress="";
	/**
	 * 发不日期
	 */
	private String publishDate="";
	/**
	 * 截止日期
	 */
	private String trunkDate="";
	/**
	 * 招聘人数
	 */
	private String memberCount="0";
	public long getCompanyCode() {
		return companyCode;
	}
	public void setCompanyCode(long companyCode) {
		this.companyCode = companyCode;
	}
	public String getPositionUrl() {
		return positionUrl;
	}
	public void setPositionUrl(String positionUrl) {
		this.positionUrl = positionUrl;
	}
	public String getPositonName() {
		return positonName;
	}
	public void setPositonName(String positonName) {
		this.positonName = positonName;
	}
	public String getWorkAddress() {
		return workAddress;
	}
	public void setWorkAddress(String workAddress) {
		this.workAddress = workAddress;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	public String getTrunkDate() {
		return trunkDate;
	}
	public void setTrunkDate(String trunkDate) {
		this.trunkDate = trunkDate;
	}
	
	public String getMemberCount() {
		return memberCount;
	}
	public void setMemberCount(String memberCount) {
		this.memberCount = memberCount;
	}
	public long getJobCode() {
		return jobCode;
	}
	public void setJobCode(long jobCode) {
		this.jobCode = jobCode;
	}
	
	
	
}
