package com.zj.queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.QueueBean;
import com.zj.bean.SubwayAndBusInputQueueBean;
import com.zj.crawler.AntGetUrl;

/**
 * 停车信息 队列容器
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class SubwayAndBusAntQueue extends QueueFather{
	private  Logger log = Logger.getLogger(SubwayAndBusAntQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public  LinkedBlockingQueue<SubwayAndBusInputQueueBean> info=null;

	/**
	 * 初始化队列信息
	 */
	@SuppressWarnings("unchecked")
	public  void init(QueueBean queueBean,String listName2,boolean flag)
	{
		if(queueBean.getQueueInputName()!=null)
		{
			redis=new Redis(queueBean.getInputQueueUrl());
			listName=listName2;
			isRedis=true;
		}else
		info=new LinkedBlockingQueue<SubwayAndBusInputQueueBean>(queueBean.getInputQueueNum());
		if(!flag)
			return;
		//>([^<]*?)</option>([^<]*?)</select> 页 </span>
		String url="";
		try{
		url = AntGetUrl.doGet("http://www.bjsubway.com/station/xltcx/","gbk");
		}catch(Exception e)
		{
			log.error(e);
			e.printStackTrace();
			return;
		}
		Pattern p_1 = Pattern.compile("div class=\"line_name\">[^>]*?>([^<]*?)</div");
		//System.out.println(url);
		Matcher m_1 = p_1.matcher(url);
		int start=0;
		int end=0;
		int i=0;
		String subwayName="";
		boolean flag1=false;
		while(m_1.find())
		{
			flag1=true;
			//
			//解析出地铁对应的站点url
			i++;
			if(i==1)
			{
				subwayName=m_1.group(1);
				start=m_1.end();
			}else{
				end=m_1.start();
				//并执行解析
				String strTemp=url.substring(start,end);
				//获取地铁信息
				regex(strTemp,subwayName);
				subwayName=m_1.group(1);
				start=m_1.end();
			}
		}
		if(flag1)
		{
			String strTemp=url.substring(start);
			//获取地铁信息
			regex(strTemp,subwayName);
		}
		log.info("地铁公交信息初始化完成");

	}
	/**
	 * 解析出地铁url对应正则
	 * @param url 
	 * @param subwayName地铁名
	 */
	public  void regex(String url,String subwayName)
	{
		Pattern p_1 = Pattern.compile("<div class=\"station\"><a href=\"([^\"]*?)\">([^<]*?)</a");
		//System.out.println(url);
		Matcher m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			SubwayAndBusInputQueueBean bean=new SubwayAndBusInputQueueBean();
			bean.setSubwayLine(subwayName);
			if(!m_1.group().startsWith("http"))
			{
				bean.setUrl("http://www.bjsubway.com"+m_1.group(1));
			}else{
				bean.setUrl(m_1.group(1));
			}
			bean.setSubwayStation(m_1.group(2));
			System.out.println(bean.getSubwayLine()+"\t"+bean.getSubwayStation()+"\t"+
			bean.getUrl());
			if(isRedis)
			{
				try {
					redis.rpush(listName,JsonUtil.getJsonStr(bean));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
			info.add(bean);
			}
		}
	}
	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  SubwayAndBusInputQueueBean get(QueueBean bean)
	{
		return (SubwayAndBusInputQueueBean)getQueueBean(bean,info,SubwayAndBusInputQueueBean.class,log,"地铁口 对公交队列信息为空");
	}
	/**
	 * 添加内容
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(SubwayAndBusInputQueueBean bean)
	{
		addQueueBean(bean,info);
	}
} 
