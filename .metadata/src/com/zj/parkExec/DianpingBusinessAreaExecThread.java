package com.zj.parkExec;

import org.apache.log4j.Logger;

import com.zj.bean.BussinessAreaPageBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.DianpingBusinessAreaAntParse;
import com.zj.queue.DianpingBusinessAreaAntQueue;

public class DianpingBusinessAreaExecThread implements Runnable{
	private static Logger log = Logger.getLogger(DianpingBusinessAreaExecThread.class);
	private IntoDb intoDb=null;
	private QueueBean queueBean=null;
	private DianpingBusinessAreaAntQueue queue=null;
	/**
	 * 有效执行次数
	 */
	private long count=0;
	/**
	 * 初始化程序
	 */
	public DianpingBusinessAreaExecThread(QueueBean queueBean,DianpingBusinessAreaAntQueue queue,IntoDb intoDb)
	{
		this.intoDb=intoDb;
		this.queue=queue;
		this.queueBean=queueBean;
	}
	/**
	 * 结束的线程数
	 */
	private int threadCountEnd=0;
	/**
	 * 线程执行方法
	 */
	public void run()
	{
		log.info("大众点评商圈 执行线程启动："+Thread.currentThread().getName());
		while(true)
		{
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("大众点评商圈 线程跳出:"+Thread.currentThread().getName());
				break;
			}
			try {
				Thread.sleep(queueBean.getUrlParseCycleTime());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			if(!queueBean.isStart())
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				continue;
			}
			BussinessAreaPageBean bean=queue.get(queueBean);
			if(bean==null)
			{
				continue;
			}
			//执行 页面获取
			//取5公里内的公司
			String urlCode=bean.getUrl();
			String url="";
			try{
			url=AntGetUrl.doGet(urlCode,"utf-8");
			}catch(Exception e)
			{
				log.error(e);
				e.printStackTrace();
				log.error("urlCode 被放入队列中:"+bean.getUrl());
				queue.add(bean);
				continue;
			}
			//执行页面解析 相关操作
			//System.out.println(url);
			if(url.length()>0)
			{
				boolean flag=DianpingBusinessAreaAntParse.runUrl(url,bean,intoDb);
//				if(bean.getPage()>500)
//				{
//					log.info("500以内结束:"+bean.getUrl());
//					continue;
//				}
				if(flag==false)
				{
					log.info("商圈不存在:"+bean.getUrl());
					continue;
				}
				count++;
				if(count%queueBean.getPrintCount()==0)
				{
					log.info("商圈当前页面:"+bean.getUrl());
				}
				//bean.setPage(bean.getPage()+1);
				//DianpingBusinessAreaAntQueue.add(bean);
			}else{
				log.error("大众点评商圈执行url错误"+url);
			}

		}
		log.info("大众点评商圈 行线程结束："+Thread.currentThread().getName());
		threadCountEnd++;
		if(threadCountEnd>=queueBean.getThreadCount())
		{
			queueBean.setExecIsRun(false);
		}
	}
	
	
}
