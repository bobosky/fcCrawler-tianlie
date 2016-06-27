package com.zj.parse;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.util.JsonUtil;
import com.zj.bean.SubwayAndBusBean;
import com.zj.bean.SubwayAndBusInputQueueBean;
import com.zj.bean.SubwayAndBusSonBean;
import com.zj.bean.SubwayBean;
import com.zj.bean.SubwayStartBean;
import com.zj.intoDb.IntoDb;

/**
 * 地铁口对应的公交信息
 * @author Administrator
 *
 */
public class SubwayAndBusAntParse {
	private static Logger log = Logger.getLogger(SubwayAndBusAntParse.class);
	/**
	 * 
	 * @param url
	 * @param IntoDb 输出队列
	 * @param inputBean为输入的地铁线路信息
	 * @param km 附近几公里 
	 */
	public static void runUrl(String url,SubwayAndBusInputQueueBean inputBean,IntoDb intoDb)
	{
		SubwayAndBusBean reustl=new SubwayAndBusBean();
		reustl.setSubwayLine(inputBean.getSubwayLine());
		reustl.setSubwayStation(inputBean.getSubwayStation());
		//获取首末车相关信息
		Pattern p_1 = Pattern.compile("<span class=\"timeinfo_diff\">([^<]*?)</span>([^<]*?)</div");
		Matcher m_1 = p_1.matcher(url);
		int start=0;
		int end=0;
		SubwayBean subwayBean=null;
		boolean flag=false;
		while(m_1.find())
		{
			end=m_1.end();
			if(flag)
			{
				//获取首末车相关信息
				getLineSon(url,start,m_1.start(),subwayBean,reustl);
				start=m_1.end();
				subwayBean=new SubwayBean();
				subwayBean.setSubwayLine(m_1.group(1));
				subwayBean.setSubwayStation(m_1.group(2).replace("（", "").replace("）", ""));
			}else{
				start=m_1.end();
				subwayBean=new SubwayBean();
				subwayBean.setSubwayLine(m_1.group(1));
				subwayBean.setSubwayStation(m_1.group(2).replace("（", "").replace("）", ""));
				flag=true;
			}
		}
		if(flag)
		{
			//获取首末车相关信息
			getLineSon(url,start,url.length(),subwayBean,reustl);
		}
		//获取发布日期
		url=url.substring(end<0?0:end);
		end=0;
		//获取公交换乘
		SubwayAndBusSonBean portBusBean=null;
		p_1 = Pattern.compile("<span class=\"ch_in_bus\">([^<]*?)</span[\\s\\S]*?<span class=\"timeinfo_time\">([^<]*?)</span");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
				//获取首末车相关信息
				portBusBean=new SubwayAndBusSonBean();
				portBusBean.setPort(m_1.group(1));
				//System.out.println("group:"+m_1.group(2));
				//String[] str=m_1.group(2).split("(\\&#160;&#160;)");
				String[] str=m_1.group(2).split("(\\&nbsp;\\&nbsp;)");
				if(str.length==0)
				{
					continue;
				}
				List<String> strTemp=new LinkedList<String>();
				for(String st:str)
				{
					strTemp.add(st);
				}
				portBusBean.setBusStation(strTemp);
				reustl.addPortAndBus(portBusBean);
		}

		if(reustl.isOk())
		{
			//加入入库队列中
			intoDb.add(JsonUtil.getJsonStr(reustl));
		}
		
	}
	
	/**
	 * 获取首末车时刻
	 * @param url
	 * @param start 开始
	 * @param end 结束
	 * @param subwayBean 存储的 线路bean
	 * @param reustl 总bean
	 */
	public static void getLineSon(String url,int start,int end,SubwayBean subwayBean,SubwayAndBusBean reustl)
	{
		String tempUrl=url.substring(start,end);
		tempUrl=tempUrl.replaceAll("\\&nbsp;","");

		Pattern p_2 = Pattern.compile("<div class=\"timeinfo_list2\">([^<]*?)<span class=\"timeinfo_time\">([^<]*?)</span>");
		Matcher m_2 = p_2.matcher(tempUrl);
		boolean flag2=false;
		while(m_2.find())
		{
			flag2=true;
			SubwayStartBean startBean=new SubwayStartBean();
			//判断时间是否有效 直接判断是否存在冒号 即可
			if(m_2.group(2).contains(":"))
			{
				if(m_2.group(1).contains("（"))
				{
					startBean.setDepartCategory(m_2.group(1).substring(0,m_2.group(1).indexOf("（")).trim());
					startBean.setStationInfo(m_2.group(1).substring(m_2.group(1).indexOf("（")+1,m_2.group(1).indexOf("）")).trim());
				}else{
					startBean.setDepartCategory(m_2.group(1).trim());
				}
				startBean.setTime(m_2.group(2));
				subwayBean.addDepartDesc(startBean);
			}else{
				continue;
			}
		}
		if(flag2)
		{
			reustl.addSubWayDesc(subwayBean);
		}
	}
	

}
