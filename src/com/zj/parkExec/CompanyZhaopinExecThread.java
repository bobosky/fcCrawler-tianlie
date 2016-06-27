package com.zj.parkExec;

import org.apache.log4j.Logger;

import com.zj.bean.CompanyBean4;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.CompanyZhaopinAntParse;
import com.zj.queue.CompanyZhaopinAntQueue;

public class CompanyZhaopinExecThread implements Runnable{
	private static Logger log = Logger.getLogger(CompanyZhaopinExecThread.class);
	private IntoDb intoDb=null;
	private QueueBean queueBean=null;
	private QueueBean queueFirstBean=null;
	private CompanyZhaopinAntQueue queue=null;
	/**
	 * 有效执行次数
	 */
	private long count=0;
	/**
	 * 初始化程序
	 */
	public CompanyZhaopinExecThread(QueueBean queueBean,QueueBean queueFirstBean,CompanyZhaopinAntQueue queue,IntoDb intoDb)
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
	@SuppressWarnings("unchecked")
	public void run()
	{
		log.info("company Name 执行线程启动："+Thread.currentThread().getName());
		while(true)
		{
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("zhaopin 线程跳出:"+Thread.currentThread().getName());
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
			//执行 页面获取
			//取5公里内的公司
			String urlCode="http://sou.zhaopin.com/jobs/advjobsearch?jl=%E5%8C%97%E4%BA%AC&kw="+bean.getKeyword().replaceAll("[ ]","")
					//+ "%E6%B4%BE%E7%91%9E%E5%A8%81%E8%A1%8C"
					+ "&sm=0&p=1&sf=0&st=99999";
			String url="";
			try{
			url=AntGetUrl.doGet(urlCode,"utf-8");
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
				CompanyZhaopinAntParse.runUrl(url,bean,intoDb);
				count++;
				if(count%queueBean.getPrintCount()==0)
				{
					log.info("招聘公司列表剩余:"+queue.getSize(queue.info));
				}
			}else{
				log.error("招聘公司列表执行url错误"+url);
			}

		}
		log.info("company Name执行线程结束："+Thread.currentThread().getName());
		threadCountEnd++;
		if(threadCountEnd>=queueBean.getThreadCount())
		{
			queueBean.setExecIsRun(false);
		}
	}
	
	
}
