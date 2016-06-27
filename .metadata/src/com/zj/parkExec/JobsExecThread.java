package com.zj.parkExec;

import java.net.URLEncoder;

import org.apache.log4j.Logger;

import com.zj.bean.JobsInputQueueBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.JobsAntParse;
import com.zj.queue.JobsAntQueue;

public class JobsExecThread implements Runnable{
	private static Logger log = Logger.getLogger(JobsExecThread.class);
	private IntoDb IntoDb=null;
	private QueueBean queueBean=null;
	private JobsAntQueue queue=null;
	/**
	 * 有效执行次数
	 */
	private long count=0;
	
	/**
	 * 结束的线程数
	 */
	private int threadCountEnd=0;
	/**
	 * 初始化程序
	 */
	public JobsExecThread(QueueBean queueBean,JobsAntQueue queue,IntoDb IntoDb)
	{
		this.queue=queue;
		this.IntoDb=IntoDb;
		this.queueBean=queueBean;
	}
	/**
	 * 线程执行方法
	 */
	@SuppressWarnings("unchecked")
	public void run()
	{
		log.info("job执行线程启动："+Thread.currentThread().getName());
		while(true)
		{
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("job线程跳出:"+Thread.currentThread().getName());
				break;
			}
			try {
				Thread.sleep(queueBean.getUrlParseCycleTime());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!queueBean.isStart())
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			JobsInputQueueBean bean=queue.get(queueBean);
			
			if(bean==null)
			{
				continue;
			}
			//执行 页面当前页信息
			////http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea=010000&funtype=0000&industrytype=00&issuedate=9&keywordtype=2&lang=c&stype=3&address=%E8%8B%B9%E6%9E%9C%E5%9B%AD&radius=0.03&fromType=20
			//String urlCode="http://search.51job.com/jobsearch/search_result.php?fromJs=1&funtype=0000&industrytype=00&issuedate=9&keywordtype=2&lang=c&stype=3&fromType=20&address=";
			//%E8%8B%B9%E6%9E%9C%E5%9B%AD&jobarea=010000&radius=0.03"
			//urlCode+=bean.getSubWayStation()+"&jobarea="+bean.getSubWayLineCode()+"&radius=0.0";
//			
//			while(true)
//			{
//				String url="";
//				if(bean.getNearKm()>5)
//				{
//					break;
//				}
//				try{
//					url=AntGetUrl.doGet(urlCode+bean.getNearKm(),"gbk");
//				}catch(Exception e2)
//				{
//					log.error("job urlCode 被放入队列中:"+bean.getSubWayLine()+"\t"+bean.getSubWayStation());
//					JobsAntQueue.add(bean);
//					continue;
//				}
//				//执行页面解析 相关操作
//				//System.out.println(url);
//				if(url.length()>0)
//				{
//					JobsAntParse.runUrl(url,bean,bean.getNearKm(),IntoDb);
//					count++;
//					if(count%queueBean.getPrintCount()==0)
//					{
//						log.info("招聘页面剩余:"+JobsAntQueue.getSize());
//					}
//					bean.setNearKm(bean.getNearKm()+2);
//				}else{
//					
//					log.error("招聘页执行url错误:"+urlCode+bean.getNearKm());
//				}
//			}
//
//		}
			boolean flag_l=false;
			for(int ll=2;ll<=8;ll++)
			{
			//获取 有条件页
			int index=0;
			if(ll==2)
			{}else if(ll<=5){
				index=ll-2;
			}else if(ll==6)
			{
				index=5;
			}else if(ll==7)
			{
				index=8;
			}else if(ll==8)
			{
				index=10;
			}
			if(!bean.getSubWayLineCode().equals("010000"))
			{
				log.info("不是北京:"+bean.getSubWayLineCode());
				continue;
			}
			String urlCode="http://search.51job.com/list/010000%252C00,000000,0000,00,4,99,%2B,2,1.html?lang=c&stype=3&postchannel=0000&workyear="
					+ ll
					+ "&cotype=99&degreefrom=99&jobterm=01&companysize=99&address=";
			
			try{
					urlCode+=URLEncoder.encode(bean.getSubWayStation(), "gbk");
			}catch(Exception e)
			{
				e.printStackTrace();
			}
			//只取最近一天 对应的不同工作年限的信息
			urlCode+="&radius=0.01&ord_field=0&list_type=0&confirmdate=9&fromType=6&fromType=6";
		bean.setWorkCategory(index);
		String url="";
		bean.setUrl(urlCode);
		try{
			url=AntGetUrl.doGet(urlCode,"gbk");
		//	System.out.println(url);
		}catch(Exception e2)
		{
			log.error("job urlCode 被放入队列中:"+bean.getSubWayLine()+"\t"+bean.getSubWayStation()+"\tworkCategory"+bean.getWorkCategory());
			log.error(bean.getUrl());
			queue.add(bean);
			flag_l=true;
			break;
		}

		//执行页面解析 相关操作
		//System.out.println(url);
		if(url.length()>0)
		{
			JobsAntParse.runUrl(url,bean,1,IntoDb);
			count++;
			if(count%queueBean.getPrintCount()==0)
			{
				log.info("招聘页面剩余:"+queue.getSize(queue.info));
			}
			bean.setNearKm(1);
		}else{
			
			log.error("招聘页执行url错误:"+urlCode+bean.getNearKm());
		}
			}
			if(flag_l)
			{
				break;
			}
	}
		
		log.info("job执行线程结束："+Thread.currentThread().getName());
		threadCountEnd++;
		if(threadCountEnd>=queueBean.getThreadCount())
		{
			queueBean.setExecIsRun(false);
		}
	}
	
	

}
