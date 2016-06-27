package com.zj.queue;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.ParkingAntParse;

/**
 * 停车信息 队列容器
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class ParkingAntQueue extends QueueFather{
	private static Logger log = Logger.getLogger(ParkingAntQueue.class);
	/**
	 * 队列容器
	 */
	public  LinkedBlockingQueue<Integer> info=null;
	/**
	 * redis
	 */
	public  Redis redis=null;
	/**
	 * 队列名
	 */
	public  String listName="";
	
	public  boolean isRedis=false;
	/**
	 * 初始化队列信息
	 */
	public  void init(QueueBean queueBean,String listName2,IntoDb parkingIntoDb,boolean flag)
	{
		if(queueBean.getQueueInputName()!=null)
		{
			redis=new Redis(queueBean.getInputQueueUrl());
			listName=listName2;
			isRedis=true;
		}else
		info=new LinkedBlockingQueue<Integer>(queueBean.getInputQueueNum());
		if(!flag)
			return;
		//>([^<]*?)</option>([^<]*?)</select> 页 </span>
		String url=AntGetUrl.getUrl(1);
		//
		//解析当前页面并放入文件中
		ParkingAntParse.runUrl(url,parkingIntoDb);
		//获取最大编号
		Pattern p_1 = Pattern.compile(">([^<]*?)</option>([^<]*?)</select> 页 </span>");
		Matcher m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			int pageSum=Integer.parseInt(m_1.group(1));
			for(int i=2;i<=pageSum;i++)
			{
				if(isRedis)
				{
					try {
						redis.rpush(listName, Integer.toString(i));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
				info.add(i);
				}
			}
		}

	}
	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  Integer get(QueueBean bean)
	{
		return (Integer)getQueueBean(bean,info,Integer.class,log,"停车场队列信息为空");
	}
	/**
	 * 添加内容
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(Integer bean)
	{
		addQueueBean(bean,info);
	}
} 
