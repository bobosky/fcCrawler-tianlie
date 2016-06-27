package com.zj.parkExec;

import org.apache.log4j.Logger;

import com.zj.bean.BusAndStationBean;
import com.zj.bean.BusAndStationInputQueueBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.BusAntStationAntParse;
import com.zj.queue.BusAndStationQueue;

public class BusAndStationExecThread implements Runnable {

	private static Logger log = Logger.getLogger(BusAndStationExecThread.class);
	private IntoDb IntoDb = null;
	private QueueBean queueBean=null;
	/**
	 * 有效执行次数
	 */
	private long count=0;
	private BusAndStationQueue queue=null;
	private QueueBean queueFirstBean=null;
	/**
	 * 初始化程序
	 */
	public BusAndStationExecThread(QueueBean queueBean,QueueBean queueFirstBean,BusAndStationQueue queue,IntoDb IntoDb) {
		this.queueFirstBean=queueFirstBean;
		this.queue=queue;
		this.IntoDb = IntoDb;
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
	public void run() {
		log.info("地铁公交执行线程启动："+Thread.currentThread().getName());
		while (true) {
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("公交线程跳出:"+Thread.currentThread().getName());
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
			BusAndStationInputQueueBean bean = null;
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
					if(queueFirstBean!=null)
					{
					if(nullCount>=3  &&(queueFirstBean==null || !queueFirstBean.isExecIsRun()))
					{
						queueBean.setEnd(true);
						isBreak=true;
						break;
					}
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
			String url = "";
			// 执行 页面获取
			while (true) {
				try {
					url = AntGetUrl.doGet(bean.getUrl()+"1", "utf-8");
					break;
				} catch (Exception e) {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
			//首先获取返程的公交车站
			BusAndStationBean result=new BusAndStationBean();
			BusAntStationAntParse.runUrlRever(url, bean, result);
			//获取当前公交车站
			try{
			url = AntGetUrl.doGet(bean.getUrl(), "utf-8");
			}catch(Exception e)
			{
				log.error(e);
				e.printStackTrace();
				log.error("urlCode 被放入队列中:"+bean.getUrl());
				queue.add(bean);
				continue;
			}
			
			if(url.length()>0)
			{
				BusAntStationAntParse.runUrl(url, bean, IntoDb,result);
				count++;
				if(count%queueBean.getPrintCount()==0)
				{
					log.info("地铁公交爬取到:"+bean.getBusName()+"\t队列数量:"+queue.getSize(queue.info));
				}
			}else{
				log.error("执行url错误:"+bean.getUrl());
			}

		}
		log.info("地铁公交执行线程结束："+Thread.currentThread().getName());
		threadCountEnd++;
		if(threadCountEnd>=queueBean.getThreadCount())
		{
			queueBean.setExecIsRun(false);
		}
	}

}
