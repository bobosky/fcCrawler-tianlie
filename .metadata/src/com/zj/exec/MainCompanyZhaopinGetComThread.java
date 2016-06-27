package com.zj.exec;

import java.util.Date;

import org.apache.log4j.Logger;

import com.util.DateFormat;
import com.zj.bean.QueueBean;
import com.zj.intoDb.IntoDb;
import com.zj.parkExec.CompanyZhaopinGetComExecThread;
import com.zj.queue.CompanyZhaopinGetComAntQueue;

/**
 * 用于解析 招聘网上 公司url中 公司的信息
 * @author Administrator
 *
 */
public class MainCompanyZhaopinGetComThread implements Runnable{
	private static Logger log = Logger.getLogger(MainCompanyZhaopinGetComThread.class);
	private QueueBean queueBean=null;
	private String time=DateFormat.parse(new Date());
	public MainCompanyZhaopinGetComThread(QueueBean queueBean)
	{
		this.queueBean=queueBean;
	}
	@SuppressWarnings("static-access")
	public void run()
	{
		//初始化队列信息
		//队列是否初始化
		CompanyZhaopinGetComAntQueue queue=new CompanyZhaopinGetComAntQueue();
		queue.init(queueBean,queueBean.getQueueInputName(0),queueBean.isInputQueueInit());
		//入库队列信息
		IntoDb intoDb=new IntoDb(queueBean,queueBean.getQueueOutputDatabase(0),queueBean.getQueueOutputName(0),1,9,null,queueBean.getOutQueueGetTime());
		Thread intoDbl=null;
		if(queueBean.getQueueOutputName(0)==null|| queueBean.isAllWriteRun())
		{
			//如果为 不使用redis则执行线程 否则 不用管
			intoDbl=new Thread(intoDb,Integer.toString(queueBean.getThreadCount()));
			intoDbl.start();
		}

		CompanyZhaopinGetComExecThread execThread=new CompanyZhaopinGetComExecThread(queueBean,queue,intoDb);
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
		String str=queueBean.getShellContent();
		ShellExec.execShell(str,time,log);
	}
}
