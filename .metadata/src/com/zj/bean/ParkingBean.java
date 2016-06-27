package com.zj.bean;

import java.io.Serializable;
import java.util.Date;

import com.util.DateFormat;

/**
 * 停车信息的bean信息
 * @author Administrator
 *
 */
public class ParkingBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4556443335913646883L;
	/**
	 *类型描述信息
	 */
	private String desc="停车信息";
	/**
	 * 停车信息主键
	 */
	private int type=1;
	/**
	 * 辖区 对应 入东城西城等
	 */
	private String areaCode="";
	/**
	 * 备案号
	 */
	private String bakCode="";
	/**
	 * 停车场名称
	 */
	private String parkingName="";
	/**
	 * 停车场类型
	 */
	private String parkingCategory="";
	/**
	 * 停车场经营企业
	 */
	private String parkingManageComp="";
	/**
	 * 停车位数量
	 */
	private int carNum=0;
	/**
	 * 非机械车数量
	 */
	private int mechanicalCarNum=0;
	/**
	 * 机械车数量
	 */
	private int machineryCarNum=0;
	/**
	 * 地区类型
	 */
	private String areaCatygory="";
	
	/**
	 * 爬取时间
	 */
	private String crawlerTime=DateFormat.parse(new Date());
	
	
	public String getCrawlerTime() {
		return crawlerTime;
	}
	public void setCrawlerTime(String crawlerTime) {
		this.crawlerTime = crawlerTime;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getAreaCode() {
		return areaCode;
	}
	public void setAreaCode(String areaCode) {
		this.areaCode = areaCode;
	}
	public String getBakCode() {
		return bakCode;
	}
	public void setBakCode(String bakCode) {
		this.bakCode = bakCode;
	}
	public String getParkingName() {
		return parkingName;
	}
	public void setParkingName(String parkingName) {
		this.parkingName = parkingName;
	}
	public String getParkingCategory() {
		return parkingCategory;
	}
	public void setParkingCategory(String parkingCategory) {
		this.parkingCategory = parkingCategory;
	}
	public String getParkingManageComp() {
		return parkingManageComp;
	}
	public void setParkingManageComp(String parkingManageComp) {
		this.parkingManageComp = parkingManageComp;
	}
	public int getCarNum() {
		return carNum;
	}
	public void setCarNum(int carNum) {
		this.carNum = carNum;
	}
	public int getMechanicalCarNum() {
		return mechanicalCarNum;
	}
	public void setMechanicalCarNum(int mechanicalCarNum) {
		this.mechanicalCarNum = mechanicalCarNum;
	}
	public int getMachineryCarNum() {
		return machineryCarNum;
	}
	public void setMachineryCarNum(int machineryCarNum) {
		this.machineryCarNum = machineryCarNum;
	}
	public String getAreaCatygory() {
		return areaCatygory;
	}
	public void setAreaCatygory(String areaCatygory) {
		this.areaCatygory = areaCatygory;
	}
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
	
	
}
