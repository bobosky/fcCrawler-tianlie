package com.zj.exec;

import java.util.HashSet;

/**
 * main静态文件 用户存储 一些全局动态变量
 * @author Administrator
 *
 */
public class MainStatic {

	/**
	 * redis中 公司code
	 */
	public static String companyCodeMap="CompanyCode";
	/**
	 * redis中 jobcode
	 */
	public static String jobCodeMap="JobCode";
	/**
	 * 存储公司的code
	 */
	public static HashSet<Long> companyCode=new HashSet<Long>();
	/**
	 * 存储 job code
	 */
	public static HashSet<Long> jobCode=new HashSet<Long>();
	/**
	 * 公司code
	 * @param o
	 * @return
	 */
	public static boolean companyCode(long o)
	{
		return companyCode.contains(o);
	}
	
	/**
	 *job code
	 * @param o
	 * @return
	 */
	public static synchronized boolean jobCode(long o)
	{
		return jobCode.contains(o);
	}
	
	public static synchronized void addCompanyCode(long o)
	{
		companyCode.add(o);
	}
	
	public static synchronized void addJobCode(long o)
	{
		jobCode.add(o);
	}
	
	public static synchronized void setCompanyCodeNull()
	{
		companyCode=new HashSet<Long>();
		
	}
	
	public static synchronized void setJobCodeNull()
	{
		jobCode=new HashSet<Long>();
		
	}
	
}
