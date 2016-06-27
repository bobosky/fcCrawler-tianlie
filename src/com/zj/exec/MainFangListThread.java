package com.zj.exec;

import java.util.Date;

import org.apache.log4j.Logger;

import com.util.DateFormat;
import com.zj.bean.QueueBean;
import com.zj.intoDb.IntoDb;
import com.zj.parkExec.FangListExecThread;
import com.zj.queue.FangListAntQueue;

/**
 * 搜房线程
 * @author Administrator
 *
 */
public class MainFangListThread implements Runnable{

	private static Logger log = Logger.getLogger(MainFangListThread.class);
	private QueueBean queueBean=null;
	private QueueBean queueFirstBean=null;
	private String time=DateFormat.parse(new Date());
	public MainFangListThread(QueueBean queueBean,QueueBean queueFirstBean)
	{
		this.queueBean=queueBean;
		this.queueFirstBean=queueFirstBean;
	}
	public void run()
	{
		//入库队列信息
		//System.out.println(queueBean.getOutQueueUrl());
		log.info("搜房程序启动"+Thread.currentThread().getName());
		
		IntoDb intoDb=new IntoDb(queueBean,queueBean.getQueueOutputDatabase(0),queueBean.getQueueOutputName(0),1,5,null,queueBean.getOutQueueGetTime());
		Thread intoDbl=null;
		if(queueBean.getQueueOutputName(0)==null|| queueBean.isAllWriteRun())
		{
			//如果为 不使用redis则执行线程 否则 不用管
			intoDbl=new Thread(intoDb,Integer.toString(queueBean.getThreadCount()));
			intoDbl.start();
		}
		//初始化队列信息
		//队列是否初始化
		FangListAntQueue queue=new FangListAntQueue();
		queue.init(queueBean,queueBean.getQueueInputName(0),queueBean.isInputQueueInit(),queueBean.getQueueOutputName(0),intoDb);


		FangListExecThread execThread=new FangListExecThread(queueBean,queueFirstBean,queue,intoDb);
		Thread[] execThreadl=new Thread[queueBean.getThreadCount()];
		for(int i=0;i<queueBean.getThreadCount();i++)
		{
			execThreadl[i]=new Thread(execThread,queueBean.getName()+Integer.toString(i));
			execThreadl[i].start();
		}
		for(int i=0;i<queueBean.getThreadCount();i++)
		{
			try {
				execThreadl[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			if(queueBean.getQueueOutputName()==null)
			{
				intoDbl.join();
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		log.info("搜房程序结束"+Thread.currentThread().getName());
		String str=queueBean.getShellContent();
		ShellExec.execShell(str,time,log);
		queueBean.setMainRun(false);
	}
}
