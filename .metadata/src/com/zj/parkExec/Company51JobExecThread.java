package com.zj.parkExec;

import org.apache.log4j.Logger;

import com.zj.bean.JobsInputQueueBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.Company51JobAntParse;
import com.zj.queue.Company51JobAntQueue;

public class Company51JobExecThread implements Runnable{
	private static Logger log = Logger.getLogger(Company51JobExecThread.class);
	private IntoDb intoDb=null;
	private QueueBean queueBean=null;
	private Company51JobAntQueue queue=null;
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
	public Company51JobExecThread(QueueBean queueBean,Company51JobAntQueue queue,IntoDb intoDb)
	{
		this.queue=queue;
		this.intoDb=intoDb;
		this.queueBean=queueBean;
	}
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
				log.info("公司 job线程跳出:"+Thread.currentThread().getName());
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
			JobsInputQueueBean bean=queue.get(queueBean);
			if(bean==null)
			{
				continue;
			}
			//执行 页面获取
			//取5公里内的公司
			
			//String urlCode="http://search.51job.com/jobsearch/search_result.php?fromJs=1%2C00&district=000000&funtype=0000&industrytype=00&issuedate=9&providesalary=99&keywordtype=2&curr_page="+(bean.getCurrentPage()+1);
			//urlCode+="&lang=c&stype=3&postchannel=0000&workyear=99&cotype=99&degreefrom=99&jobterm=01&companysize=99&address="+bean.getSubWayStation()+"&jobarea="+bean.getSubWayLineCode();
			//urlCode+="&radius=0.03&ord_field=0&list_type=0&fromType=14";
			String urlCode="";
			if(bean.getCurrentPage()==0)
			{
				urlCode="http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea="+bean.getSubWayLineCode()+"&funtype=0000&industrytype=00&keywordtype=2&lang=c&stype=3&postchannel=0000&address="+bean.getSubWayStation()+"&radius=0.03&fromType=1";
			}else{
			 urlCode="http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea="+bean.getSubWayLineCode()+"&curr_page="+(bean.getCurrentPage());
			 urlCode+="&lonlat="+bean.getLocation().getLng()+","+bean.getLocation().getLat();
			 urlCode+="&funtype=0000&industrytype=00&keywordtype=2&lang=c&stype=3&address="+bean.getSubWayStation()+ "&radius=0.03&fromType=1&fromType=5";
			}
			//System.out.println(urlCode);
			String url="";
			try{
			url=AntGetUrl.doGet(urlCode,"gbk");
			}catch(Exception e)
			{
				log.error(e);
				e.printStackTrace();
				log.error("urlCode 被放入队列中:"+bean.getSubWayLine()+"\t"+bean.getSubWayStation());
				queue.add(bean);
				continue;
			}
			//执行页面解析 相关操作
			//System.out.println(url);
			if(url.length()>0)
			{
				boolean flag=Company51JobAntParse.runUrl(url,bean,queue,intoDb);
				//System.out.println(flag);
				if(flag)
				{
					bean.setCurrentPage(bean.getCurrentPage()+1);
					queue.add(bean);
					count++;
					if(count%queueBean.getPrintCount()==0)
					{
						log.info("队列剩余:"+queue.getSize(queue.info)+"\t地铁线:"+bean.getSubWayLine()+"\t站:"+bean.getSubWayStation()+"\t页数:"+(bean.getCurrentPage()-1));		
					}
				}else{
					log.info("url不存在公司:"+bean.getSubWayStation()+":"+urlCode);
					log.info("队列剩余:"+queue.getSize(queue.info)+"\t地铁线:"+bean.getSubWayLine()+"\t站:"+bean.getSubWayStation()+"\t页数:"+bean.getCurrentPage()+"\t结束");
				}
				
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
