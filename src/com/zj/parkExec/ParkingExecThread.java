package com.zj.parkExec;

import org.apache.log4j.Logger;

import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.ParkingAntParse;
import com.zj.queue.ParkingAntQueue;

/**
 * 执行线程
 * @author Administrator
 *
 */
public class ParkingExecThread  implements Runnable{
	
	private static Logger log = Logger.getLogger(ParkingExecThread.class);
	private QueueBean queueBean=null;
	private IntoDb parkingIntoDb=null;
	private ParkingAntQueue queue=null;
	private QueueBean queueFirstBean=null;
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
	public ParkingExecThread(QueueBean queueBean,QueueBean queueFirstBean,ParkingAntQueue queue,IntoDb parkingIntoDb)
	{
		this.queueFirstBean=queueFirstBean;
		this.queue=queue;
		this.parkingIntoDb=parkingIntoDb;
		this.queueBean=queueBean;
	}
	/**
	 * 线程执行方法
	 */
	@SuppressWarnings("unchecked")
	public void run()
	{
		log.info("park执行线程启动："+Thread.currentThread().getName());
		while(true)
		{
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("parking 线程跳出:"+Thread.currentThread().getName());
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
			Integer bean=null;
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
			String url="";
			url=AntGetUrl.getUrl(bean);
			
			
			//执行页面解析 相关操作
			if(url.length()>0)
			{
				ParkingAntParse.runUrl(url,parkingIntoDb);
				count++;
				if(count%queueBean.getPrintCount()==0)
				{
					log.info("车场页面剩余:"+queue.getSize(queue.info)+"\t爬取到:"+bean+"页");
				}
			}else{
				log.error("车场页执行url错误:http://www.bjjtw.gov.cn/jtw_service/page/service/parking.jsp"+"\t第"+bean+"页");
			}
		}
		log.info("park执行线程结束："+Thread.currentThread().getName());
		threadCountEnd++;
		if(threadCountEnd>=queueBean.getThreadCount())
		{
			queueBean.setExecIsRun(false);
		}
	}
	
	
}
