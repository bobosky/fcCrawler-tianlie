package com.zj.bean;

import java.io.Serializable;

/**
 * 51job 职位信息
 * @author Administrator
 *
 */
public class Company51JobDescBean extends Company51JobPositionBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6367311373002935801L;
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
	
	
	
}
