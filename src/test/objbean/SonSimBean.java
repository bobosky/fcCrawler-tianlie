package test.objbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class SonSimBean  implements Serializable,Comparable<SonSimBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private long simId=0L;
	/**
	 * 名
	 */
	private String name="";
	/**
	 * 值
	 */
	private float value=0F;
	
	/**
	 * 描述信息
	 */
	private ArrayList<ExplainBean> explain=new ArrayList<ExplainBean>();
	
	public long getSimId() {
		return simId;
	}
	public void setSimId(long simId) {
		this.simId = simId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public int compareTo(SonSimBean other)
	{
		return -Float.compare(value,other.value);
	}
	
	public void sortExplain()
	{
		Collections.sort(explain);
	}
	
	
}
