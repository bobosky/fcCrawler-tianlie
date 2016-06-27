package com.zj.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.util.DateFormat;

/**
 * 51job相关信息实体类
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
public class JobsBean implements Serializable{
	private String url="";
	/**
	 * 地铁线
	 */
	private String subwayLine="";
	/**
	 * 地铁线站
	 */
	private String subwayStation="";
	/**
	 * 地铁线编码
	 */
	private String subWayLineCode="";
	
	/**
	 * 附近几公里
	 */
	private int nearbyKm=1;
	
	
	/**
	 * 工作类型
	 */
	private int workCategory=-1;
	/**
	 * 工作年限
	 */
	private List<JobsSonBean> workExperience=new LinkedList<JobsSonBean>();
	/**
	 * 发布日期
	 */
	private List<JobsSonBean> releaseDate=new LinkedList<JobsSonBean>();
	/**
	 * 学历背景
	 */
	private List<JobsSonBean> educationBackground=new LinkedList<JobsSonBean>();
	/**
	 * 公司性质
	 */
	private List<JobsSonBean> companyProperty=new LinkedList<JobsSonBean>();
	/**
	 * 月薪范围 不一定为连续
	 */
	private List<JobsSonBean> monthlyPay=new LinkedList<JobsSonBean>();
	/**
	 * 工作类型
	 */
	private List<JobsSonBean> jobCategory=new LinkedList<JobsSonBean>();
	
	/**
	 * 公司规模
	 */
	private List<JobsSonBean> companyScale=new LinkedList<JobsSonBean>();
	/**
	 * 工作信息
	 */
	private List<JobAndCompanyBean> jobDesc=new LinkedList<JobAndCompanyBean>();
	
	/**
	 * 爬取时间
	 */
	private String crawlerTime=DateFormat.parse(new Date());
	/**
	 * 获取当前信息是否有效
	 * @return
	 */
	public boolean isOk()
	{
		if(workExperience.size()>0)
		{
			return true;
		}else if(releaseDate.size()>0)
		{
			return true;
		}else if(educationBackground.size()>0)
		{
			return true;
		}else if(companyProperty.size()>0)
		{
			return true;
		}
		else if(monthlyPay.size()>0)
		{
			return true;
		}else if(jobCategory.size()>0)
		{
			return true;
		}else if(companyScale.size()>0)
		{
			return true;
		}
		return false;
	}
	
	
	public int getWorkCategory() {
		return workCategory;
	}


	public void setWorkCategory(int workCategory) {
		this.workCategory = workCategory;
	}


	public String getCrawlerTime() {
		return crawlerTime;
	}


	public void setCrawlerTime(String crawlerTime) {
		this.crawlerTime = crawlerTime;
	}


	public int getNearbyKm() {
		return nearbyKm;
	}


	public void setNearbyKm(int nearbyKm) {
		this.nearbyKm = nearbyKm;
	}


	public String getSubWayLineCode() {
		return subWayLineCode;
	}


	public void setSubWayLineCode(String subWayLineCode) {
		this.subWayLineCode = subWayLineCode;
	}


	public String getSubwayLine() {
		return subwayLine;
	}

	public void setSubwayLine(String subwayLine) {
		this.subwayLine = subwayLine;
	}

	public String getSubwayStation() {
		return subwayStation;
	}

	public void setSubwayStation(String subwayStation) {
		this.subwayStation = subwayStation;
	}

	public List<JobsSonBean> getWorkExperience() {
		return workExperience;
	}

	public void setWorkExperience(List<JobsSonBean> workExperience) {
		this.workExperience = workExperience;
	}
	
	
	public void addWorkExperience(JobsSonBean workExperience) {
		this.workExperience.add(workExperience);
	}

	public List<JobsSonBean> getReleaseDate() {
		return releaseDate;
	}

	public void setReleaseDate(List<JobsSonBean> releaseDate) {
		this.releaseDate = releaseDate;
	}
	
	public void addReleaseDate(JobsSonBean releaseDate) {
		this.releaseDate.add(releaseDate);
	}

	public List<JobsSonBean> getEducationBackground() {
		return educationBackground;
	}

	public void setEducationBackground(List<JobsSonBean> educationBackground) {
		this.educationBackground = educationBackground;
	}
	public void addEducationBackground(JobsSonBean educationBackground) {
		this.educationBackground.add(educationBackground);
	}

	public List<JobsSonBean> getCompanyProperty() {
		return companyProperty;
	}

	public void setCompanyProperty(List<JobsSonBean> companyProperty) {
		this.companyProperty = companyProperty;
	}
	public void addCompanyProperty(JobsSonBean companyProperty) {
		this.companyProperty.add(companyProperty);
	}


	public List<JobsSonBean> getMonthlyPay() {
		return monthlyPay;
	}

	public void setMonthlyPay(List<JobsSonBean> monthlyPay) {
		this.monthlyPay = monthlyPay;
	}
	
	public void addMonthlyPay(JobsSonBean monthlyPay) {
		this.monthlyPay.add(monthlyPay);
	}

	public List<JobsSonBean> getJobCategory() {
		return jobCategory;
	}

	public void setJobCategory(List<JobsSonBean> jobCategory) {
		this.jobCategory = jobCategory;
	}
	
	public void addJobCategory(JobsSonBean jobCategory) {
		this.jobCategory.add(jobCategory);
	}

	public List<JobsSonBean> getCompanyScale() {
		return companyScale;
	}

	public void setCompanyScale(List<JobsSonBean> companyScale) {
		this.companyScale = companyScale;
	}
	
	public void addCompanyScale(JobsSonBean companyScale) {
		this.companyScale.add(companyScale);
	}


	public List<JobAndCompanyBean> getJobDesc() {
		return jobDesc;
	}


	public void setJobDesc(List<JobAndCompanyBean> jobDesc) {
		this.jobDesc = jobDesc;
	}
	
	public void addJobDesc(JobAndCompanyBean jobDesc) {
		this.jobDesc.add(jobDesc);
	}


	public String getUrl() {
		return url;
	}


	public void setUrl(String url) {
		this.url = url;
	}
	
	
}
