package com.zj.exec;

import java.util.Date;

import org.apache.log4j.Logger;

import com.util.DateFormat;
import com.zj.bean.QueueBean;
import com.zj.intoDb.IntoDb;
import com.zj.parkExec.DianpingBusinessAreaExecThread;
import com.zj.queue.DianpingBusinessAreaAntQueue;

/**
 * 用于解析job 中 公司 的url中公司信息
 * @author Administrator
 *
 */
public class MainBussinessAreaThread implements Runnable{
	private static Logger log = Logger.getLogger(MainBussinessAreaThread.class);
	private QueueBean queueBean=null;
	private QueueBean queueFirstBean=null;
	private String time=DateFormat.parse(new Date());
	
	public MainBussinessAreaThread(QueueBean queueBean,QueueBean queueFirstBean)
	{
		this.queueFirstBean=queueFirstBean;
		this.queueBean=queueBean;
	}
	public void run()
	{
		//队列是否初始化
		DianpingBusinessAreaAntQueue queue=new DianpingBusinessAreaAntQueue();
		queue.init(queueBean,queueBean.getQueueInputName(0),queueBean.isInputQueueInit());
		//存储job队列信息
		//入库队列信息
		IntoDb intoDb=new IntoDb(queueBean,queueBean.getQueueOutputDatabase(0),queueBean.getQueueOutputName(0),1,13,null,queueBean.getOutQueueGetTime());
		Thread intoDbl=null;
//		System.out.println(queueBean.getQueueOutputName()+"\t"+queueBean.isAllWriteRun());
		if(queueBean.getQueueOutputName(0)==null|| queueBean.isAllWriteRun())
		{
			//如果为 不使用redis则执行线程 否则 不用管
			intoDbl=new Thread(intoDb,Integer.toString(queueBean.getThreadCount()));
			intoDbl.start();
		}
		//初始化队列信息
	
		DianpingBusinessAreaExecThread execThread=new DianpingBusinessAreaExecThread(queueBean,queueFirstBean,queue,intoDb);
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
				e.printStackTrace();
			}
		}
		try {
			if(queueBean.getQueueOutputName()==null)
			{
				intoDbl.join();
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.info("执行结束");
//		String str=queueBean.getShellContent();
//		ShellExec.execShellAndPrint(str,time,log);
		queueBean.setMainRun(false);
	}
}
