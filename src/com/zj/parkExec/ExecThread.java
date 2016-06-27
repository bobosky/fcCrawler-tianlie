package com.zj.parkExec;

import com.zj.bean.QueueBean;
import com.zj.queue.QueueFather;

public class ExecThread<T> {

	
	public T isStop(QueueFather queue,QueueBean queueBean,QueueBean queueFirstBean)
	{
		boolean isBreak=false;
		int nullCount=0;
		T bean=null;
		while(true)
		{
			bean=(T)queue.get(queueBean);
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
		return bean;
	}
}
