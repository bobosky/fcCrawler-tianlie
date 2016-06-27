package com.zj.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.util.JsonUtil;
import com.zj.bean.AreaBean;
import com.zj.bean.BussinessArea;
import com.zj.bean.BussinessAreaDesc;
import com.zj.bean.BussinessAreaPageBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;

public class DianpingBusinessAreaAntParse {

	/**
	 * 返回是否 正确 如果为 false 则表示 到页面结束了
	 * @param url
	 * @param inputBean
	 * @param intoDb
	 * @return
	 */
	public static boolean runUrl(String url,BussinessAreaPageBean inputBean,IntoDb intoDb)
	{
		BussinessArea bean=new BussinessArea();
		Pattern p_1= Pattern.compile("<h1 class=\"shopall\">[\\s]*?<strong>([^<]*?)生活指南地图<");
		Matcher m_1= p_1.matcher(url);
		//判断市并是否有效
		if(m_1.find())
		{
			bean.setCity(m_1.group(1));
			bean.setCityId(inputBean.getPage());
		}else{
			return false;
		}
		
		
		 p_1= Pattern.compile("<h2>商区<a[\\s\\S]*?</div");
		 m_1= p_1.matcher(url);
		if(m_1.find())
		{
			//截取商区信息
			url=url.substring(m_1.start(),m_1.end());
		}
		//获取区
		 p_1 = Pattern.compile("<dt><a class=\"Bravia\" href=\"([^\"]*?)\">([^<]*?)</a></dt>[\\s]*?<dd>[\\s]*?<ul>([\\s\\S]*?)</ul>");
		 m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			
			BussinessAreaDesc desc=new BussinessAreaDesc();
			AreaBean qu=new AreaBean();		
			String area=m_1.group(2);
			String areaUrl=m_1.group(1);
			qu.setName(area);
			qu.toUpdateUrl(areaUrl);
			desc.setArea(qu);
			String inner=m_1.group(3).trim();
			if(inner.equals(""))
			{
				
			}else{
				//获取商圈信息
				Pattern p_2= Pattern.compile("<li><a class=\"B\" href=\"([^\"]*?)\">([^<]*?)<");
				Matcher m_2= p_2.matcher(inner);
				while(m_2.find())
				{
					//设置商圈
					AreaBean son=new AreaBean();
					son.toUpdateUrl(m_2.group(1));
					son.setName(m_2.group(2));
					desc.addBussinessArea(son);
				}
			}
			bean.addBussinessAreaAll(desc);
		}
		//如果有效则转换json
		String result=JsonUtil.getJsonStr(bean);
		System.out.println("result:"+result);
		intoDb.add(result);
		return true;
	}
	public static void main(String[] args) {
		BussinessAreaPageBean bean=new BussinessAreaPageBean();
		bean.setPage(2);
		String url="";
		try{
			url=AntGetUrl.doGet(bean.getUrl(),"utf-8");
		}catch(Exception e)
		{
			
		}
		boolean flag=DianpingBusinessAreaAntParse.runUrl(url,bean,null);
	}
}
