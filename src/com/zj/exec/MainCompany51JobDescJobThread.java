package com.zj.exec;

import java.util.Date;

import org.apache.log4j.Logger;

import com.util.DateFormat;
import com.zj.bean.QueueBean;
import com.zj.intoDb.IntoDb;
import com.zj.parkExec.Company51JobDescJobExecThread;
import com.zj.queue.Company51JobDescJobAntQueue;

/**
 * 用于解析job 中 公司 的url中公司信息
 * @author Administrator
 *
 */
public class MainCompany51JobDescJobThread implements Runnable{
	private static Logger log = Logger.getLogger(MainCompany51JobDescJobThread.class);
	private QueueBean queueBean=null;
	private QueueBean queueBeanFirst=null;
	private String time=DateFormat.parse(new Date());
	public MainCompany51JobDescJobThread(QueueBean queueBean,QueueBean queueBeanFirst)
	{
		this.queueBean=queueBean;
		this.queueBeanFirst=queueBeanFirst;
	}
	public void run()
	{
		//初始化队列信息
		//队列是否初始化
		Company51JobDescJobAntQueue queue=new Company51JobDescJobAntQueue();
		queue.init(queueBean,queueBean.getQueueInputName(0),queueBean.isInputQueueInit());
		//存储job队列信息
		//入库队列信息
		IntoDb intoDb=new IntoDb(queueBean,queueBean.getQueueOutputDatabase(0),queueBean.getQueueOutputName(0),1,12,null,queueBean.getOutQueueGetTime());
		Thread intoDbl=null;
//		System.out.println(queueBean.getQueueOutputName()+"\t"+queueBean.isAllWriteRun());
		if(queueBean.getQueueOutputName(0)==null|| queueBean.isAllWriteRun())
		{
			//如果为 不使用redis则执行线程 否则 不用管
			intoDbl=new Thread(intoDb,Integer.toString(queueBean.getThreadCount()));
			intoDbl.start();
		}

		Company51JobDescJobExecThread execThread=new Company51JobDescJobExecThread(queueBean,queueBeanFirst,queue,intoDb);
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
		log.info("执行结束");
		//当51job执行结束后需要执行增量更新程序
		//直接调用shell程序
		String str=queueBean.getShellContent();
		ShellExec.execShell(str,time,log);
		//全量插入
//		IntoMongoDB.readWriteToMongodJobCompany(fileName, fileName2,
//				outputFile, ip, port, database, collectionName, printCount);
		//增量更新
//		IntoMongoDB.readWriteToMongodJobCompanyUpdate(fileName, fileName2,
//				outputFile, ip, port, database, collectionName, printCount,
//				limitCount);
		queueBean.setMainRun(false);
	}
}
