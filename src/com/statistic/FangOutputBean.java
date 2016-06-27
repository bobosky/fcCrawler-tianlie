package com.statistic;

import java.io.Serializable;

public class FangOutputBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long fangCode=0l;
	private String buildName="";
	private Long personNum=0l;
	public Long getFangCode() {
		return fangCode;
	}
	public void setFangCode(Long fangCode) {
		this.fangCode = fangCode;
	}
	public String getBuildName() {
		return buildName;
	}
	public void setBuildName(String buildName) {
		this.buildName = buildName;
	}
	public Long getPersonNum() {
		return personNum;
	}
	public void setPersonNum(Long personNum) {
		this.personNum = personNum;
	}
	
}
