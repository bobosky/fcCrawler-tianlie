package com.zj.parkExec;

import org.apache.log4j.Logger;

import com.zj.bean.CompanyBean4;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.CompanyZhaopinGetComAntParse;
import com.zj.queue.CompanyZhaopinGetComAntQueue;

public class CompanyZhaopinGetComExecThread implements Runnable{
	private static Logger log = Logger.getLogger(CompanyZhaopinGetComExecThread.class);
	private IntoDb intoDb=null;
	private QueueBean queueBean=null;
	private QueueBean queueFirstBean=null;
	private CompanyZhaopinGetComAntQueue queue=null;
	/**
	 * 有效执行次数
	 */
	private long count=0;
	/**
	 * 初始化程序
	 */
	public CompanyZhaopinGetComExecThread(QueueBean queueBean,QueueBean queueFirstBean,CompanyZhaopinGetComAntQueue queue,IntoDb intoDb)
	{
		this.queueFirstBean=queueFirstBean;
		this.queue=queue;
		this.intoDb=intoDb;
		this.queueBean=queueBean;
	}
	/**
	 * 结束的线程数
	 */
	private int threadCountEnd=0;
	/**
	 * 线程执行方法
	 */
	@SuppressWarnings({ "unchecked", "static-access" })
	public void run()
	{
		log.info("company 执行线程启动："+Thread.currentThread().getName());
		while(true)
		{
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("zhaopin 详细线程跳出:"+Thread.currentThread().getName());
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
			CompanyBean4 bean=null;
			boolean isBreak=false;
			int nullCount=0;
			while(true)
			{
				bean=queue.get(queueBean);
				if(bean==null)
				{
					nullCount++;
					try {
						Thread.sleep(queueBean.getUrlParseCycleTime()+1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if(nullCount>=3 &&(queueFirstBean==null || !queueFirstBean.isExecIsRun()))
					{
						queueBean.setEnd(true);
						isBreak=true;
						break;
					}
					continue;
				}else{
					//结束
					break;
				}
			}
			if(isBreak)
			{
				break;
			}
			if(bean==null)
			{
				continue;
			}
			//执行 页面获取
			String url="";
			try{
			url=AntGetUrl.doGet(bean.getCompanyUrl(),"utf-8");
			}catch(Exception e)
			{
				log.error(e);
				e.printStackTrace();
				log.error("urlCode 被放入队列中:"+bean.getCompanyUrl());
				queue.add(bean);
				continue;
			}
			//执行页面解析 相关操作
			//System.out.println(url);
			if(url.length()>0)
			{
				CompanyZhaopinGetComAntParse.runUrl(url,bean,intoDb);
				count++;
				if(count%queueBean.getPrintCount()==0)
				{
					log.info("招聘公司信息页剩余:"+queue.getSize(queue.info));
				}
				
			}else{
				log.error("招聘公司信息页执行url错误"+url);
			}
		}
		log.info("company执行线程结束："+Thread.currentThread().getName());
		threadCountEnd++;
		if(threadCountEnd>=queueBean.getThreadCount())
		{
			queueBean.setExecIsRun(false);
		}
	}
	
	
}
