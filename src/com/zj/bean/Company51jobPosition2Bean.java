package com.zj.bean;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * 51job 公司code 对应code实体
 * @author Administrator
 *
 */
public class Company51jobPosition2Bean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -837550386509783491L;
	/**
	 * 职位 code
	 */
	private long jobCode=0L;
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
	 * 截止日期
	 */
	private String trunkDate="";
	
	/**
	 * 学历
	 */
	private String educationBackground="";
	/**
	 * 月薪
	 */
	private String monthlyply="";
	/**
	 * 职位标签
	 */
	private String positionTag="";
	/**
	 * 职能
	 */
	private String positionFunction="";
	/**
	 * 职位描述
	 */
	private String positionDesc="";
	
	private String yearsOfWorking="";
	
	public String getYearsOfWorking() {
		return yearsOfWorking;
	}
	public void setYearsOfWorking(String yearsOfWorking) {
		this.yearsOfWorking = yearsOfWorking;
	}
	public String getEducationBackground() {
		return educationBackground;
	}
	public void setEducationBackground(String educationBackground) {
		this.educationBackground = educationBackground;
	}
	public String getMonthlyply() {
		return monthlyply;
	}
	public void setMonthlyply(String monthlyply) {
		this.monthlyply = monthlyply;
	}
	public String getPositionTag() {
		return positionTag;
	}
	public void setPositionTag(String positionTag) {
		this.positionTag = positionTag;
	}
	public String getPositionFunction() {
		return positionFunction;
	}
	public void setPositionFunction(String positionFunction) {
		this.positionFunction = positionFunction;
	}
	public String getPositionDesc() {
		return positionDesc;
	}
	public void setPositionDesc(String positionDesc) {
		this.positionDesc = positionDesc;
	}
	
	
	
	/**
	 * 职位发布日期以及 招聘是人数
	 */
	private List<Company51PositionSonBean> jobInfoList=new LinkedList<Company51PositionSonBean>();
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

	
	public List<Company51PositionSonBean> getJobInfoList() {
		return jobInfoList;
	}
	public void addJobInfoList(Company51PositionSonBean jobInfo) {
		this.jobInfoList.add(jobInfo);
	}
	public void setJobInfoList(List<Company51PositionSonBean> jobInfoList) {
		this.jobInfoList = jobInfoList;
	}
	public String getTrunkDate() {
		return trunkDate;
	}
	public void setTrunkDate(String trunkDate) {
		this.trunkDate = trunkDate;
	}
	public long getJobCode() {
		return jobCode;
	}
	public void setJobCode(long jobCode) {
		this.jobCode = jobCode;
	}
	
	
	
}
