package com.zj.queue;

/**
 * 使用来作为统计 n次从队列中取数据位空
 * 的次数
 * @author Administrator
 *
 */
public class StatisticStopStatus {

	/**
	 * 次数上限
	 */
	public  long countLimit=100;
	/**
	 *统计次数
	 */
	public  long count=0L;
	
	/**
	 * 判断是否停止
	 * @param val 为增量数据
	 * 如果val为0则需要重置coutn值为0
	 * 否则为累加
	 * @return 是否停止
	 */
	public synchronized  boolean isStop(long val)
	{
		if(val==0L)
		{
			count=0L;
		}else{
			count++;
		}
		if(count>=countLimit)
		{
			return true;
		}else{
			return false;
		}
	}
	
}
