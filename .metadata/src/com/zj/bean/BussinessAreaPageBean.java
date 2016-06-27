package com.zj.bean;

import java.io.Serializable;

/**
 * 大众点评商圈地址信息
 * @author Administrator
 *
 */
public class BussinessAreaPageBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2095442265261516238L;
	private String urlStart="http://www.dianping.com/shopall/";
	private int page=1;
	public String getUrl()
	{
		return urlStart+page+"/0";
	}
	public String getUrlStart() {
		return urlStart;
	}
	public void setUrlStart(String urlStart) {
		this.urlStart = urlStart;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	
	
}
