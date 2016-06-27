package com.zj.queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.BusAndStationInputQueueBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;

/**
 * 停车信息 队列容器
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class BusAndStationQueue extends QueueFather{
	private  Logger log = Logger.getLogger(BusAndStationQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public  LinkedBlockingQueue<BusAndStationInputQueueBean> info=null;
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
		}else{
		info=new LinkedBlockingQueue<BusAndStationInputQueueBean>(queueBean.getInputQueueNum());
		}
		if(!flag)
			return;
		//>([^<]*?)</option>([^<]*?)</select> 页 </span>
		String url="";
		try{
		url = AntGetUrl.doGet("http://bus.mapbar.com/beijing/xianlu/","utf-8");
		}catch(Exception e)
		{
			log.error(e);
			e.printStackTrace();
			return;
		}
		Pattern p_1 = Pattern.compile("<a href=\"([^\"]*?)\" title=[^>]*?target=\"_blank\">([^<]*?)</a>");
		//System.out.println(url);
		Matcher m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			BusAndStationInputQueueBean busAndMallBean=new BusAndStationInputQueueBean();
			busAndMallBean.setBusName(m_1.group(2).trim());
			busAndMallBean.setUrl(m_1.group(1));
			if(isRedis)
			{
				try {
					redis.rpush(listName, JsonUtil.getJsonStr(busAndMallBean));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
			info.add(busAndMallBean);
			}
		}
		System.out.println("地铁公交信息初始化完成");

	}
	
	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  BusAndStationInputQueueBean get(QueueBean bean)
	{
		return (BusAndStationInputQueueBean)getQueueBean(bean,info,BusAndStationInputQueueBean.class,log,"地铁公交队列为空");
	}
	/**
	 * 添加内容
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(BusAndStationInputQueueBean bean)
	{
		addQueueBean(bean,info);
	}

} 
