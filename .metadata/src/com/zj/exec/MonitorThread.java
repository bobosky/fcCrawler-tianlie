package com.zj.exec;

import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import com.util.ParamStatic;
import com.zj.bean.QueueBean;

/**
 * 监听线程
 * @author Administrator
 *
 */
public class MonitorThread implements Runnable{

	private static Logger log = Logger.getLogger(MonitorThread.class);
	
	private List<QueueBean> taskList =null;
	
	private List<Runnable> taskThread=null;
	
	private List<Thread> taskListThread=null;
	
	public MonitorThread(List<QueueBean> taskList,List<Runnable> taskThread,List<Thread> taskListThread)
	{
		this.taskList=taskList;
		this.taskThread=taskThread;
		this.taskListThread=taskListThread;
	}
	/**
	 * 启动程序
	 */
	public void run()
	{
		log.info("监控程序启动");
		LinkedList<Integer> removeL=new LinkedList<Integer>();
		LinkedList<Runnable> addL=new LinkedList<Runnable>();
		LinkedList<QueueBean> addB=new LinkedList<QueueBean>();
		LinkedList<Thread> addT=new LinkedList<Thread>();
		while(true)
		{
			Date now=new Date();
			log.info("监控程序:"+now);
			if(removeL.size()!=0)
			{
				removeL=new LinkedList<Integer>();
				addL=new LinkedList<Runnable>();
				addB=new LinkedList<QueueBean>();
				addT=new LinkedList<Thread>();
			}
			int i=0;
			for(QueueBean queueBean:taskList)
			{
				//判断时间是否有效
				//System.out.println("监控id:"+queueBean.getId()+":"+queueBean.getStartTime()+"\t"+queueBean.getNextTime());
				if(queueBean.getStartTime().compareTo(now)<0)
				{
					if(!queueBean.isStart())
					{
						//启动程序
						queueBean.setStart(true);
						Runnable run1=Main.run(queueBean);
						addL.add(run1);
						Thread tr=new Thread(run1,"main"+queueBean.getIdIndex());
						//启动
						tr.start();
						log.info(tr.getName()+"\t直接开始启动");
						addT.add(tr);
						continue;
					}
					
				}
				//判断是否结束
				if(queueBean.getNextTime().compareTo(now)<0)
				{
					//System.out.println("");
					//判断是否可以kill掉
					if(queueBean.getCycleStrageory()==ParamStatic.kill)
					{
						queueBean.setEnd(true);
						log.info("关闭");
						removeL.add(i);
						QueueBean tempQ=queueBean.changeTime();
						while(tempQ.getNextTime().compareTo(now)<0)
						{
							tempQ=tempQ.changeTime();
						}
						tempQ.setStart(true);
						addB.add(tempQ);
						
						//System.out.println(queueBean.getNextTime());
						Runnable run1=Main.run(tempQ);
						//System.out.println(tempQ.getNextTime());
						addL.add(run1);
						Thread tr=new Thread(run1,"main"+tempQ.getIdIndex());
						//启动
						tr.start();
						log.info(tr.getName()+"\t断开后重新启动");
						addT.add(tr);
					}else{
						queueBean.setEnd(true);
						//需要重新建立新的线程
						removeL.add(i);
						QueueBean tempQ=queueBean.changeTime();
						while(tempQ.getNextTime().compareTo(now)<0)
						{
							tempQ=tempQ.changeTime();
						}
						tempQ.setStart(true);
						addB.add(tempQ);
						
						//System.out.println(queueBean.getNextTime());
						Runnable run1=Main.run(tempQ);
						//System.out.println(tempQ.getNextTime());
						addL.add(run1);
						Thread tr=new Thread(run1,"main"+tempQ.getIdIndex());
						//启动
						tr.start();
						log.info(tr.getName()+"\t断开后重新启动");
						addT.add(tr);
					}
				}
			}
			if(removeL.size()>0)
			{
				//移出
				Collections.sort(removeL);
				int lp=0;
				for(Integer l:removeL)
				{
					taskList.remove(l-lp);
					taskThread.remove(l-lp);
					taskListThread.remove(l-lp);
					lp++;
				}
			}
			if(addL.size()>0)
			{
				for(QueueBean queueBean:addB)
				{
					taskList.add(queueBean);
				}
				for(Runnable queueBean:addL)
				{
					taskThread.add(queueBean);
				}
				for(Thread queueBean:addT)
				{
					taskListThread.add(queueBean);
				}
			}

			try {
				Thread.sleep(Main.monitorCycle);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	
		}

	}
}
