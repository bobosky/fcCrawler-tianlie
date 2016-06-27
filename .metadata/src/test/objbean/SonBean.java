package test.objbean;

import java.io.Serializable;

public class SonBean  implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long id=0L;
	/**
	 * 名
	 */
	private String name="";
	/**
	 * 值
	 */
	private double value=0D;
	/**
	 * 数量
	 */
	private int  count=0;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public double getValue() {
		return value;
	}
	public void setValue(double value) {
		this.value = value;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	
	
}
