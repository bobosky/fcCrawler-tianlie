package com.zj.parkExec;

import org.apache.log4j.Logger;

import com.zj.bean.Company51JobBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.Company51JobDescAntParse;
import com.zj.queue.Company51JobDescAntQueue;

public class Company51JobDescExecThread implements Runnable{
	private static Logger log = Logger.getLogger(Company51JobDescExecThread.class);
	//用于存储职位url
	private IntoDb intoDb=null;
	//用于存储 公司信息
	private IntoDb intoDb2=null;
	private QueueBean queueBean=null;
	//以来上一个任务 
	private QueueBean queueFirstBean=null; 
	/**
	 * 读取队列
	 */
	private Company51JobDescAntQueue queue=null;
	/**
	 * 有效执行次数
	 */
	private long count=0;
	/**
	 * 初始化程序
	 */
	public Company51JobDescExecThread(QueueBean queueBean,QueueBean queueFirstBean,Company51JobDescAntQueue queue,IntoDb intoDb,IntoDb intoDb2)
	{
		this.queue=queue;
		this.queueFirstBean=queueFirstBean;
		this.intoDb=intoDb;
		this.intoDb2=intoDb2;
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
		log.info("company 执行线程启动："+Thread.currentThread().getName());
		while(true)
		{
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("公司 job 详细线程跳出:"+Thread.currentThread().getName());
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
			Company51JobBean bean=null;
			boolean isBreak=false;
			int nullCount=0;
			while(true)
			{
				bean=queue.get(queueBean);
				if(bean==null)
				{
					nullCount++;
					if(nullCount>=3 &&(queueFirstBean==null || !queueFirstBean.isExecIsRun()))
					{
						//如果上一次执行结束则本次也执行结束
							queueBean.setEnd(true);
							isBreak=true;
						break;
					}
					try {
						Thread.sleep(queueBean.getUrlParseCycleTime()+1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
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
			if (!bean.getCompanyUrl().contains("search.51job.com"))
			{
				log.error("公司地址不是 51job地址"+bean.getCompanyName()+"\t"+bean.getCompanyUrl());
				continue;
			}
			//执行 页面获取
			String url="";
				try{
				int index=bean.getCompanyUrl().lastIndexOf(",",bean.getCompanyUrl().lastIndexOf(","))-3;
				if(index<=0)
				{
					
				}else{
					try{
					//爬取当前页 50行
						//System.out.println(bean.getCompanyUrl());
						//System.out.println(bean.getCompanyUrl().substring(0,index));
					url=AntGetUrl.doGet(bean.getCompanyUrl().substring(0,index)+",50,"+bean.getCurrentPage()+".html","gbk");
					//System.out.println(bean.getCompanyUrl().substring(0,index)+",50,"+bean.getCurrentPage()+".html");
					}catch(Exception e)
					{
						log.error(e);
						e.printStackTrace();
						log.error("urlCode 被放入队列中:"+bean.getCompanyUrl());
						queue.add(bean);
						continue;
					}
				}
				}catch(Exception e)
				{
					
				}
			
			//执行页面解析 相关操作
			//System.out.println(url);
			if(url.length()>0)
			{
				
				boolean flag=Company51JobDescAntParse.runUrl(url,bean,queue,intoDb,intoDb2);
				count++;
				if(flag)
				{
					if(count%queueBean.getPrintCount()==0)
					{
						log.info("51job公司信息页剩余:"+queue.getSize(queue.info)+"\t当前页"+bean.getCurrentPage());
					}
					bean.setCurrentPage(bean.getCurrentPage()+1);
					queue.add(bean);
				}else{
					log.info("51job公司页结束:"+bean.getCompanyName()+"\t"+bean.getCompanyUrl());
				}
			}else{
				log.error("51job公司信息页执行url错误"+url);
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
