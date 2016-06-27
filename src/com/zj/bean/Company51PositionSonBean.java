package com.zj.bean;

import java.io.Serializable;

public class Company51PositionSonBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8574711226312376765L;

	/**
	 * 发不日期
	 */
	private String publishDate="";
	
	/**
	 * 招聘人数
	 */
	private String memberCount="0";

	public String getPublishDate() {
		return publishDate;
	}

	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}

	public String getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(String memberCount) {
		this.memberCount = memberCount;
	}
	
	
}
