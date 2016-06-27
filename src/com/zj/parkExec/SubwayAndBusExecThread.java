package com.zj.parkExec;

import org.apache.log4j.Logger;

import com.zj.bean.QueueBean;
import com.zj.bean.SubwayAndBusInputQueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.SubwayAndBusAntParse;
import com.zj.queue.SubwayAndBusAntQueue;

public class SubwayAndBusExecThread implements Runnable {

	private static Logger log = Logger.getLogger(SubwayAndBusExecThread.class);
	private IntoDb IntoDb = null;
	private QueueBean queueBean=null;
	private QueueBean queueFirstBean=null;
	private SubwayAndBusAntQueue queue=null;
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
	public SubwayAndBusExecThread(QueueBean queueBean,QueueBean queueFirstBean,SubwayAndBusAntQueue queue,IntoDb IntoDb) {
		this.queue=queue;
		this.queueFirstBean=queueFirstBean;
		this.IntoDb = IntoDb;
		this.queueBean=queueBean;
	}

	/**
	 * 线程执行方法
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		log.info("公交执行线程启动："+Thread.currentThread().getName());
		while (true) {
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("地铁公交 线程跳出:"+Thread.currentThread().getName());
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
			SubwayAndBusInputQueueBean bean = null;
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
			String url = "";
			try{
			url = AntGetUrl.doGet(bean.getUrl(), "gbk");
			}catch(Exception e)
			{
				log.error(e);
				e.printStackTrace();
				log.error("urlCode 被放入队列中:"+bean.getUrl());
				queue.add(bean);
				continue;
			}
			// 执行页面解析 相关操作
			// System.out.println(url);
			if(url.length()>0)
			{
				SubwayAndBusAntParse.runUrl(url, bean, IntoDb);
				count++;
				if(count%queueBean.getPrintCount()==0)
				{
					log.info("地铁公交爬取到:"+bean.getSubwayLine()+"\t"+bean.getSubwayStation()+"\t队列数量:"+queue.getSize(queue.info));
				}
			}else{
				log.error("地铁公交执行url错误"+bean.getUrl());
			}
		}
		log.info("公交执行线程结束："+Thread.currentThread().getName());
		threadCountEnd++;
		if(threadCountEnd>=queueBean.getThreadCount())
		{
			queueBean.setExecIsRun(false);
		}
	}
	
}
