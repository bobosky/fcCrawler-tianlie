package com.zj.parkExec;

import org.apache.log4j.Logger;

import com.zj.bean.FangInputQueueBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.FangAntParse;
import com.zj.queue.FangAntQueue;

public class FangExecThread implements Runnable{
private static Logger log = Logger.getLogger(FangExecThread.class);
	private IntoDb IntoDb=null;
	private QueueBean queueBean=null;
	private FangAntQueue queue=null;
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
	public FangExecThread(QueueBean queueBean,FangAntQueue queue,IntoDb IntoDb)
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
		log.info("搜房执行线程启动："+Thread.currentThread().getName());
		while(true)
		{
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("fang 线程跳出:"+Thread.currentThread().getName());
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
			FangInputQueueBean bean=queue.get(queueBean);
			if(bean==null)
			{
				continue;
			}
			//执行 页面获取
			////http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea=010000&funtype=0000&industrytype=00&issuedate=9&keywordtype=2&lang=c&stype=3&address=%E8%8B%B9%E6%9E%9C%E5%9B%AD&radius=0.03&fromType=20
			String urlCode=bean.getUrl();
			String url="";
//			if(urlCode.contains("/office/"))
//			{}else{
//				if(urlCode.endsWith("/"))
//				{
//					urlCode=urlCode+"office/";
//				}else{
//				urlCode=urlCode+"/office/";
//				}
//			}
			try{
				url=AntGetUrl.doGetGzip(urlCode,"gbk");
			}catch(Exception e)
			{
				log.error(e);
				e.printStackTrace();
				log.error("urlCode 被放入队列中:"+urlCode);
				queue.add(bean);
				continue;
			}
			//执行页面解析 相关操作
			//System.out.println(url);
			if(url.length()>0)
			{
				FangAntParse.runUrl(url,bean,IntoDb);
				count++;
				if(count%queueBean.getPrintCount()==0)
				{
					log.info("搜房页面剩余:"+queue.getSize(queue.info)+"\t爬取到:"+bean.getUrl());
				}
			}else{
				log.error("搜房页面执行url错误:"+urlCode);
			}

		}
		log.info("搜房执行线程结束："+Thread.currentThread().getName());
		threadCountEnd++;
		if(threadCountEnd>=queueBean.getThreadCount())
		{
			queueBean.setExecIsRun(false);
		}
	}
	
	
	

}
