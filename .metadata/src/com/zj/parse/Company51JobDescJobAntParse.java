package com.zj.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.util.JsonUtil;
import com.zj.bean.Company51JobDescBean;
import com.zj.bean.Company51JobPositionBean;
import com.zj.intoDb.IntoDb;

public class Company51JobDescJobAntParse {

	/**
	 * 返回是否 正确 如果为 false 则表示 到页面结束了
	 * @param url
	 * @param inputBean
	 * @param intoDb
	 * @return
	 */
	public static void runUrl(String url,Company51JobPositionBean inputBean,IntoDb intoDb)
	{
		
		
		//判断当前页是否有效
		Company51JobDescBean jobDesc=new Company51JobDescBean();
		Pattern p_1 = Pattern.compile("学[^<]*?历[^<]*?</td><td[^>]*?>([^<]*?)<");
		Matcher m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			jobDesc.setEducationBackground(m_1.group(1));
		}
		
		p_1 = Pattern.compile("工作年限[^<]*?</td><td[^>]*?>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			jobDesc.setYearsOfWorking(m_1.group(1));
			
		}
		p_1 = Pattern.compile("薪水范围[^<]*?</td><td[^>]*?>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			jobDesc.setMonthlyply(m_1.group(1));
			
		}
		//
		p_1 = Pattern.compile("position_label\">([^<]*?)</");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			jobDesc.setPositionTag(jobDesc.getPositionTag()+","+m_1.group(1));
			
		}
		p_1 = Pattern.compile("职位职能[^<]*?</strong>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			jobDesc.setPositionFunction(m_1.group(1).replace("(&nbsp;&nbsp;)"," ").trim());
			
		}
		p_1 = Pattern.compile("职位描述[^<]*?</strong><br/>[\\s]*?<div[^>]*?>([\\s\\S]*?)</div>");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			jobDesc.setPositionDesc(m_1.group(1));
			
		}
		jobDesc.setCompanyCode(inputBean.getCompanyCode());
		jobDesc.setJobCode(inputBean.getJobCode());
		jobDesc.setMemberCount(inputBean.getMemberCount());
		jobDesc.setPublishDate(inputBean.getPublishDate());
		jobDesc.setPositonName(inputBean.getPositonName());
		jobDesc.setTrunkDate(inputBean.getTrunkDate());
		jobDesc.setWorkAddress(inputBean.getWorkAddress());
		jobDesc.setPositionUrl(inputBean.getPositionUrl());
		String str=JsonUtil.getJsonStr(jobDesc);
		//System.out.println("str:"+str);
		intoDb.add(str);
		
	}
}
