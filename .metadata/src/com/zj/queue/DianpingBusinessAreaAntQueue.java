package com.zj.queue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.BussinessAreaPageBean;
import com.zj.bean.QueueBean;

/**
 * /**
 * 大众点评商圈信息
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class DianpingBusinessAreaAntQueue extends QueueFather{
	private Logger log = Logger.getLogger(DianpingBusinessAreaAntQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public LinkedBlockingQueue<BussinessAreaPageBean> info=null;
	/**
	 * 初始化队列信息
	 */
	@SuppressWarnings("unchecked")
	public void init(QueueBean queueBean,String listName2,boolean flag)
	{
		if(queueBean.getQueueInputName()!=null)
		{
			redis=new Redis(queueBean.getInputQueueUrl());
			listName=listName2;
			isRedis=true;
		}else
		info=new LinkedBlockingQueue<BussinessAreaPageBean>(queueBean.getInputQueueNum());
		if(!flag)
			return;
		//初始化
		for(int i=1;i<=3000;i++)
		{
		BussinessAreaPageBean bean=new BussinessAreaPageBean();
		bean.setPage(i);
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
		log.info("大众点评商圈信息初始化完成");

	}

	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  BussinessAreaPageBean get(QueueBean bean)
	{
		return (BussinessAreaPageBean)getQueueBean(bean,info,BussinessAreaPageBean.class,log,"点评商圈信息为空");
	}
	/**
	 * 添加内容
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(BussinessAreaPageBean bean)
	{
		addQueueBean(bean,info);
	}
} 
