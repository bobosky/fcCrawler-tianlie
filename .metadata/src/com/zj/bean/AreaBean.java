package com.zj.bean;

import java.io.Serializable;

public class AreaBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7750347388053543368L;
	private String name="";
	private String url="";
	private String code="";
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	public void toUpdateUrl(String url) {
		this.url = "http://www.dianping.com"+url;
		if(url.contains("/"))
		{
			code=url.substring(url.lastIndexOf("/")+1);
		}
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	
	
	
}
