package com.zj.bean;

import java.io.Serializable;


/**
 * 存储job对应的name及对应的值
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class JobsSonBean implements Serializable{

	/**
	 * 对应的类型
	 */
	private String name="String";
	/**
	 * 对应的数量
	 */
	private int count=0;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
