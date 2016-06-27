package com.zj.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.sun.swing.internal.plaf.synth.resources.synth;
import com.util.DateFormat;
import com.util.JsonUtil;
import com.zj.bean.FangBean;
import com.zj.bean.FangInput2QueueBean;
import com.zj.bean.FangInputQueueBean;
import com.zj.bean.FangListBean;
import com.zj.bean.FangMonthCountBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.queue.FangListAntQueue;

/**
 * 地铁口对应的公交信息
 * @author Administrator
 *
 */
public class FangListAntParse {
	private static Logger log = Logger.getLogger(FangListAntParse.class);
	
	/**
	 * 唯一搜房code
	 */
	public static HashSet<String> uniCode=new HashSet<String>();
	
	public static HashSet<String> getUniCode() {
		return uniCode;
	}

	public static void setUniCode(HashSet<String> uniCode) {
		FangListAntParse.uniCode = uniCode;
	}

	/**
	 * 从字符串中接写出fangcode
	 * @param str
	 * @return
	 */
	public static Long clearCode(String str)
	{
		try{
			
			return Long.parseLong(str.substring(str.indexOf("house-xm")+8,str.length()-1));
		}catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 
	 * @param url
	 * @param IntoDb 输出队列
	 * @param inputBean为输入的地铁线路信息
	 */
	public static void runUrl(String url,FangListBean inputBean,IntoDb intoDb)
	{
		HashMap<Long,FangInput2QueueBean> map=new HashMap<Long,FangInput2QueueBean>();
		//将房源的相关信息放入 result中
		//获取首末车相关信息
		Pattern p_1 = Pattern.compile("class=\"title\">[^>]*?href='([^\"]*?)'[^>]*?[\\s\\S]*?href='([^\"]*?)'[^>]*?><span class=\"spName\">");
		Matcher m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			//System.out.println(m_1.group(1).trim()+"\t"+m_1.group(2));
			FangInput2QueueBean fangBean=new FangInput2QueueBean();
			fangBean.setCity(inputBean.getCityName());
			//fangBean.setFangSonSourceUrl("http://office.fang.com"+m_1.group(1));
			fangBean.setFangSonSourceUrl(FangListAntQueue.getCityUrl(inputBean.getCityName())+m_1.group(1));
			Long fangCode=clearCode(m_1.group(2).trim());
			if(fangCode==null)
			{
				log.error("字符串解析code错误:"+m_1.group(2));
				continue;
			}
			if(map.containsKey(fangCode))
			{
				continue;
			}else{
				//System.out.println("fangCode:"+fangCode);
				fangBean.setFangCode(fangCode);
				map.put(fangCode,fangBean);
			}
		}
		//遍历所有有效的数据添加如队列中
		for(Entry<Long,FangInput2QueueBean> bean:map.entrySet())
		{
			//过滤重复
			synchronized (uniCode) {
				if(uniCode.contains(bean.getValue().getUrl()))
				{
					//System.out.println("包含:"+bean.getKey());
					continue;
				}
				uniCode.add(bean.getValue().getUrl());
			}
			//否则添加如内存中
			intoDb.add(JsonUtil.getJsonStr(bean.getValue()));
		}
			//获取百度poi数据
//			String mapUrl="http://esf.fang.com/map/newhouse/ShequMap.aspx?newcode="+result.getFangCode();
//			while(true)
//			{
//				try {
//					url = AntGetUrl.doGetGzip(mapUrl,"gbk");
//					//System.out.println("租金:"+url);
//					break;
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					try {
//						Thread.sleep(2000);
//					} catch (InterruptedException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//			}
//			//获取对应信息
//			p_1 = Pattern.compile(",[\\s]*?px:\"([^\"]*?)\"[\\s]*?,[\\s]*?py:\"([^\"]*?)\"");
//			m_1 = p_1.matcher(url);
//			if(m_1.find())
//			{
//				LationLngLat location=new LationLngLat();
//				location.setLng(Double.parseDouble(m_1.group(1)));
//				location.setLat(Double.parseDouble(m_1.group(2)));
//				result.setLocation(location);
//			}
			// 加入入库队列中
			//intoDb.add(JsonUtil.getJsonStr(result));
		
	}
	
	public static void main(String[] args) throws Exception {
//		System.out.println("00sdf".replaceAll("(^[0]*+)",""));
//		String str="('http://www.fang.com/ask/Ask_StepTwo.aspx?asktitle=%BD%A8%CD%E2SOHO%D0%B4%D7%D6%C2%A5&newcode=1010087100')";
//		System.out.println(str.substring(str.indexOf("newcode=")+8,str.indexOf(')')-1));
//		String url="";
//		String mapUrl="http://esf.fang.com/map/newhouse/ShequMap.aspx?newcode=1010132225";
//		while(true)
//		{
//			try {
//				url = AntGetUrl.doGetGzip(mapUrl,"gbk");
//				//System.out.println("租金:"+url);
//				break;
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		} 
//		System.out.println(url);
//		//获取对应信息
//		Pattern p_1 = Pattern.compile(",[\\s]*?px:\"([^\"]*?)\"[\\s]*?,[\\s]*?py:\"([^\"]*?)\"");
//		Matcher m_1 = p_1.matcher(url);
//		if(m_1.find())
//		{
//			System.out.println(m_1.group(1));
//			System.out.println(m_1.group(2));
//		}
//		
//	}
//		String url="http://guoyingdasha.fang.com/office/";
//		String urlCode=AntGetUrl.doGetGzip(url, "gbk");
//		FangAntParse.runUrl(urlCode,null,null);
		String str="元/平米·天，环比上月上涨";
		
		System.out.println(str.substring(0,str.indexOf("，")).trim());
		
		
		str="4923.htm\", \"width\": 2, \"dot-size\": 3, \"halo-size\": 1, \"loop\": false, \"colour\": \"#F24D00\", \"values\": [ { \"value\": 30012, \"tip\": \"2014\u5E7406\u6708\u003Cbr>#val#\u5143/\u5E73\u65B9\u7C73\" }, { \"value\": 30008, \"tip\": \"2014\u5E7407\u6708\u003Cbr>#val#\u5143/\u5E73\u65B9\u7C73\" }, { \"value\": 2930";
		Pattern p_1 = Pattern.compile("\"value\":[\\s]*?([0-9\\.]*?),[\\s]*?\"tip\":[\\s]*?\"([\\d]*?)年");
		Matcher m_1 = p_1.matcher(str);
		System.out.println(str);
		while(m_1.find())
		{
			System.out.println(m_1.group(1)+"\t"+m_1.group(2));
		}
	}

}
