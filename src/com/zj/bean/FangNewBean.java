package com.zj.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.util.DateFormat;

/**
 * 新房源的数据
 * @author Administrator
 *
 */
public class FangNewBean implements Serializable{

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5185632774456567407L;
	/**
	 * url地址
	 */
	private String url="";
	/**
	 * 搜房code
	 */
	private long fangCode=0l;
	/**
	 * 当月最低价格
	 */
	private String currentLowPrice="";
	/**
	 * 最低房价对应的时间
	 */
	private String currentDate="";
	/**
	 * 租售状态
	 */
	private String hireSaleSitation="";
	/**
	 * 开盘时间
	 */
	private String openingDate="";
	/**
	 * 写字楼类型
	 */
	private String buildingCategory="";
	/**
	 * 写字楼名字
	 */
	private String buildingName="";
	/**
	 * 写字楼别名
	 */
	private String buildingNameAlias="";
	/**
	 * 建筑面积
	 */
	private String buildSpace="";
	/**
	 * 开间面积
	 */
	private String openSpace="";
	/**
	 * 电梯配置
	 */
	private String elevatorPlat="";
	/**
	 * 停车位数量
	 */
	private String parkingNum="";
	/**
	 * 物业费
	 */
	private String propertyFee="";
	/**
	 * 物业公司
	 */
	private String propertyCompany="";
	/**
	 * 开发商
	 */
	private String developers="";
	/**
	 * 物业地址
	 */
	private String propertyAddress="";
	
	/**
	 * 销售电话信息
	 */
	private String salePhoneInfo="";
	
	/**
	 * 描述信息
	 */
	private String desc="";
	/**
	 * 详情页地址
	 */
	private String descUrl="";
	/**
	 * 新闻信息
	 */
	private ArrayList<NewsBean> news=new ArrayList<NewsBean>();
	
	
	//详细信息
	/**
	 * 商圈
	 */
	private String tradingArea="";
	
	/**
	 * 项目特色
	 */
	private String programFeature="";
	/**
	 * 产权年限
	 */
	private String equityAgeLimit="";
	
	/**
	 * 标准层面积
	 */
	private String standerLayerSpace="";
	
	/**
	 * 标准层数
	 */
	private String layerNum="";
	/**
	 * 装修状况
	 */
	private String fitmentSituation="";
	
	/**
	 * 标准层高
	 */
	private String standerLayerHight="";
	/**
	 * 周边配套
	 */
	private String couplingPeriphery="";
	/**
	 * 目标业态
	 */
	private String targetBusiness="";
	
	/**
	 * 交通状况
	 */
	private String trifficSituation="";
	/**
	 * 楼栋状况
	 */
	private String buildingCondition="";
	
	/**
	 * poi 数据
	 */
	private LationLngLat location=null;
	
	/**
	 * 效果图
	 */
	private String effectImg="";
	/**
	 * 交通图
	 */
	private String trifficImg="";
	/**
	 * 实景图
	 */
	private String realImg="";
	/**
	 * 外景图
	 */
	private String outerImg="";
	/**
	 * 周边配套图
	 */
	private String aroundImg="";
	/**
	 * 户型图
	 */
	private String houseTypeImg="";
	
	/**
	 * 建筑的详情页
	 */
	private FangNewDescBean buildingDescPage=null;
		
	/**
	 * 月份
	 */
	private int month=Calendar.getInstance().get(Calendar.MONTH);
	/**
	 * 年
	 */
	private int year=Calendar.getInstance().get(Calendar.YEAR);
	/**
	 * 爬取时间
	 */
	private String crawlerTime=DateFormat.parse(new Date());
	
	public int getMonth() {
		return month;
	}
	public void setMonth(int month) {
		this.month = month;
	}
	public int getYear() {
		return year;
	}
	public void setYear(int year) {
		this.year = year;
	}
	public FangNewDescBean getBuildingDescPage() {
		return buildingDescPage;
	}
	public void setBuildingDescPage(FangNewDescBean buildingDescPage) {
		this.buildingDescPage = buildingDescPage;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public long getFangCode() {
		return fangCode;
	}
	public void setFangCode(long fangCode) {
		this.fangCode = fangCode;
	}
	public String getCurrentLowPrice() {
		return currentLowPrice;
	}
	public void setCurrentLowPrice(String currentLowPrice) {
		this.currentLowPrice = currentLowPrice;
	}
	public String getCurrentDate() {
		return currentDate;
	}
	public void setCurrentDate(String currentDate) {
		this.currentDate = currentDate;
	}
	public String getOpeningDate() {
		return openingDate;
	}
	public void setOpeningDate(String openingDate) {
		this.openingDate = openingDate;
	}
	public String getBuildingCategory() {
		return buildingCategory;
	}
	public void setBuildingCategory(String buildingCategory) {
		this.buildingCategory = buildingCategory;
	}
	public String getBuildingName() {
		return buildingName;
	}
	public void setBuildingName(String buildingName) {
		this.buildingName = buildingName;
	}
	public String getBuildingNameAlias() {
		return buildingNameAlias;
	}
	public void setBuildingNameAlias(String buildingNameAlias) {
		this.buildingNameAlias = buildingNameAlias;
	}
	public String getBuildSpace() {
		return buildSpace;
	}
	public void setBuildSpace(String buildSpace) {
		this.buildSpace = buildSpace;
	}
	public String getOpenSpace() {
		return openSpace;
	}
	public void setOpenSpace(String openSpace) {
		this.openSpace = openSpace;
	}
	public String getElevatorPlat() {
		return elevatorPlat;
	}
	public void setElevatorPlat(String elevatorPlat) {
		this.elevatorPlat = elevatorPlat;
	}
	public String getParkingNum() {
		return parkingNum;
	}
	public void setParkingNum(String parkingNum) {
		this.parkingNum = parkingNum;
	}
	public String getPropertyFee() {
		return propertyFee;
	}
	public void setPropertyFee(String propertyFee) {
		this.propertyFee = propertyFee;
	}
	public String getPropertyCompany() {
		return propertyCompany;
	}
	public void setPropertyCompany(String propertyCompany) {
		this.propertyCompany = propertyCompany;
	}
	public String getDevelopers() {
		return developers;
	}
	public void setDevelopers(String developers) {
		this.developers = developers;
	}
	public String getPropertyAddress() {
		return propertyAddress;
	}
	public void setPropertyAddress(String propertyAddress) {
		this.propertyAddress = propertyAddress;
	}
	public String getSalePhoneInfo() {
		return salePhoneInfo;
	}
	public void setSalePhoneInfo(String salePhoneInfo) {
		this.salePhoneInfo = salePhoneInfo;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
	public String getDescUrl() {
		return descUrl;
	}
	public void setDescUrl(String descUrl) {
		this.descUrl = descUrl;
	}
	public ArrayList<NewsBean> getNews() {
		return news;
	}
	public void setNews(ArrayList<NewsBean> news) {
		this.news = news;
	}
	
	public void addNews(NewsBean news)
	{
		this.news.add(news);
	}
	public String getTradingArea() {
		return tradingArea;
	}
	public void setTradingArea(String tradingArea) {
		this.tradingArea = tradingArea;
	}
	public String getProgramFeature() {
		return programFeature;
	}
	public void setProgramFeature(String programFeature) {
		this.programFeature = programFeature;
	}
	public String getEquityAgeLimit() {
		return equityAgeLimit;
	}
	public void setEquityAgeLimit(String equityAgeLimit) {
		this.equityAgeLimit = equityAgeLimit;
	}
	public String getStanderLayerSpace() {
		return standerLayerSpace;
	}
	public void setStanderLayerSpace(String standerLayerSpace) {
		this.standerLayerSpace = standerLayerSpace;
	}
	public String getLayerNum() {
		return layerNum;
	}
	public void setLayerNum(String layerNum) {
		this.layerNum = layerNum;
	}
	public String getFitmentSituation() {
		return fitmentSituation;
	}
	public void setFitmentSituation(String fitmentSituation) {
		this.fitmentSituation = fitmentSituation;
	}
	public String getStanderLayerHight() {
		return standerLayerHight;
	}
	public void setStanderLayerHight(String standerLayerHight) {
		this.standerLayerHight = standerLayerHight;
	}
	public String getCouplingPeriphery() {
		return couplingPeriphery;
	}
	public void setCouplingPeriphery(String couplingPeriphery) {
		this.couplingPeriphery = couplingPeriphery;
	}
	public String getTargetBusiness() {
		return targetBusiness;
	}
	public void setTargetBusiness(String targetBusiness) {
		this.targetBusiness = targetBusiness;
	}
	public String getTrifficSituation() {
		return trifficSituation;
	}
	public void setTrifficSituation(String trifficSituation) {
		this.trifficSituation = trifficSituation;
	}
	public String getBuildingCondition() {
		return buildingCondition;
	}
	public void setBuildingCondition(String buildingCondition) {
		this.buildingCondition = buildingCondition;
	}
	public LationLngLat getLocation() {
		return location;
	}
	public void setLocation(LationLngLat location) {
		this.location = location;
	}
	public String getEffectImg() {
		return effectImg;
	}
	public void setEffectImg(String effectImg) {
		this.effectImg = effectImg;
	}
	public String getTrifficImg() {
		return trifficImg;
	}
	public void setTrifficImg(String trifficImg) {
		this.trifficImg = trifficImg;
	}
	public String getRealImg() {
		return realImg;
	}
	public void setRealImg(String realImg) {
		this.realImg = realImg;
	}
	public String getOuterImg() {
		return outerImg;
	}
	public void setOuterImg(String outerImg) {
		this.outerImg = outerImg;
	}
	public String getAroundImg() {
		return aroundImg;
	}
	public void setAroundImg(String aroundImg) {
		this.aroundImg = aroundImg;
	}
	public String getHouseTypeImg() {
		return houseTypeImg;
	}
	public void setHouseTypeImg(String houseTypeImg) {
		this.houseTypeImg = houseTypeImg;
	}
	public String getHireSaleSitation() {
		return hireSaleSitation;
	}
	public void setHireSaleSitation(String hireSaleSitation) {
		this.hireSaleSitation = hireSaleSitation;
	}
	public String getCrawlerTime() {
		return crawlerTime;
	}
	public void setCrawlerTime(String crawlerTime) {
		this.crawlerTime = crawlerTime;
	}
	
}
