package com.zj.bean;

import java.io.Serializable;

/**
 * 新闻实体
 * @author Administrator
 *
 */
public class NewsBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3296090795422833987L;
	/**
	 * url
	 */
	private String url="";
	/**
	 * 简介
	 */
	private String briefIntroduction="";
	/**
	 * 发布时间
	 */
	private String publishDate="";
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getBriefIntroduction() {
		return briefIntroduction;
	}
	public void setBriefIntroduction(String briefIntroduction) {
		this.briefIntroduction = briefIntroduction;
	}
	public String getPublishDate() {
		return publishDate;
	}
	public void setPublishDate(String publishDate) {
		this.publishDate = publishDate;
	}
	
	
	
}
