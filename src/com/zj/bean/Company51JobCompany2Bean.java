/**
 * 
 */
package com.zj.bean;

import java.io.Serializable;
import java.util.LinkedList;

/**
 * 
 * company入mongo最终结果
 * @author Administrator
 *
 */
public class Company51JobCompany2Bean extends Company51JobCompanyBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6942897559434121637L;
	
	private LinkedList<Company51jobPosition2Bean> jobs=new LinkedList<Company51jobPosition2Bean>();

	public LinkedList<Company51jobPosition2Bean> getJobs() {
		return jobs;
	}

	public void setJobs(LinkedList<Company51jobPosition2Bean> jobs) {
		this.jobs = jobs;
	}
	public void addJobs(Company51jobPosition2Bean job) {
		this.jobs.add(job);
	}
	
	

}
