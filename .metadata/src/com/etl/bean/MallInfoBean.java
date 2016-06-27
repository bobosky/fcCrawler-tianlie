package com.etl.bean;

import java.io.Serializable;

import com.zj.bean.LationLngLat;

/**
 * mall 实体
 * @author Administrator
 *
 */
public class MallInfoBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1873266929013383902L;
	private String name="";
	
	private LationLngLat baidu=null;
	
	private LationLngLat tencent=null;
	
	private String dianpingid="";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LationLngLat getBaidu() {
		return baidu;
	}

	public void setBaidu(LationLngLat baidu) {
		this.baidu = baidu;
	}

	public LationLngLat getTencent() {
		return tencent;
	}

	public void setTencent(LationLngLat tencent) {
		this.tencent = tencent;
	}

	public String getDianpingid() {
		return dianpingid;
	}

	public void setDianpingid(String dianpingid) {
		this.dianpingid = dianpingid;
	}
	
	
}
