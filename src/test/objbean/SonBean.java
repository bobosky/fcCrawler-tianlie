package test.objbean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

public class SonBean  implements Serializable,Comparable<SonBean>{

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
	private float value=0f;
	/**
	 * 数量
	 */
	private int  count=0;
	
	/**
	 * 描述信息
	 */
	private ArrayList<ExplainBean> explain=new ArrayList<ExplainBean>();
	
	public ArrayList<ExplainBean> getExplain() {
		return explain;
	}
	public void setExplain(ArrayList<ExplainBean> explain) {
		this.explain = explain;
	}
	public void addExplain(ExplainBean explain)
	{
		boolean flag=false;
		for(int i=0;i<this.explain.size();i++)
		{
			if(this.explain.get(i).getText().equals(explain.getText()))
			{
				flag=true;
			}
		}
		if(!flag)
		{
			this.explain.add(explain);
		}
	}
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
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public void sortExplain()
	{
		Collections.sort(explain);
	}
	@Override
	public int compareTo(SonBean o) {
		// TODO Auto-generated method stub
		SonBean in=(SonBean)o;
		return Double.compare(in.getValue(),this.value);
	}
	
}
