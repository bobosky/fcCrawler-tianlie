package com.statistic;

public class FangBuildAreaBean implements Comparable {

	
	private float buildArea=0f;
	private float value=0f;
	
	public FangBuildAreaBean(float buildArea,float value)
	{
		this.buildArea=buildArea;
		this.value=value;
	}
	@Override
	public int compareTo(Object o) {
		// TODO Auto-generated method stub
		FangBuildAreaBean bean=(FangBuildAreaBean)o;
		return -Float.compare(value,bean.value);
	}
	public float getValue() {
		return value;
	}
	public void setValue(float value) {
		this.value = value;
	}
	public float getBuildArea() {
		return buildArea;
	}
	public void setBuildArea(float buildArea) {
		this.buildArea = buildArea;
	}
	
	
	
	
}
