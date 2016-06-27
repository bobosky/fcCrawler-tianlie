package com.zj.queue;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.QueueBean;
/**
 * 队列公共方法类
 * @author Administrator
 *
 * @param <T>
 */
public class QueueFather<T> {
	/**
	 * redis
	 */
	public Redis redis = null;
	/**
	 * 队列名
	 */
	public String listName = "";
	/**
	 * 是否是redis
	 */
	public Boolean isRedis = false;

	public StatisticStopStatus status = new StatisticStopStatus();

	/**
	 * 将左右两侧特殊符号去除
	 * 
	 * @param str
	 * @return
	 */
	public String moveLeftAndRight(String str) {
		str = str.trim();
		if (str.length() > 1) {
			return str.substring(1, str.length() - 1);
		}
		return "";
	}

	/**
	 * 调整状态
	 * 
	 * @param bean
	 */
	public void changeStatus(QueueBean bean) {
		boolean flag2 = status.isStop(1l);
		if (flag2 == true) {
			// 则停止
			bean.setEnd(true);
		}
	}

	/**
	 * 获取队列内容
	 * 
	 * @param bean
	 *            bean信息
	 * @param info队列希虚拟
	 * @param 打印内容
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings({ "unchecked" })
	public T getQueueBean(QueueBean bean, LinkedBlockingQueue<T> info,
			@SuppressWarnings("rawtypes") Class obj, Logger log, String content) {
		T result = null;
		while (true) {
			if (!isRedis) {
				result = info.poll();
				if (result == null) {
					changeStatus(bean);
				}
			} else {
				String com = null;
				try {
					if(redis.llen(listName)>0)
					{
						com = redis.lpop(listName);
					}else{
						return null;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				if (com == null) {
					changeStatus(bean);
				} else {
					result = (T) JsonUtil.getDtoFromJsonObjStr(com, obj);
				}
			}
			if (result == null) {
				sleep(bean, log, content);
				continue;
			} else {
				break;
			}
		}
		return result;
	}

	/**
	 * 等待时间
	 * 
	 * @param bean
	 * @param content
	 */
	public void sleep(QueueBean bean, Logger log, String content) {
		try {
			Thread.sleep(bean.getInputQueueGetTime());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		log.warn(content);
	}

	/**
	 * 新添加内容
	 * 
	 * @param bean
	 */
	public void addQueueBean(T bean, LinkedBlockingQueue<T> info) {

		while (true) {
			try {
				if (isRedis) {
					redis.rpush(listName, JsonUtil.getJsonStr(bean));
				} else
					info.add(bean);
				break;
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	/**
	 * 获取队列大小
	 * @param info
	 * @return
	 */
	public long getSize(LinkedBlockingQueue<T> info) {
		while (true) {
			try {
				if (isRedis) {
					return redis.llen(listName);
				} else {
					return info.size();
				}
			} catch (Exception e) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	public T get(QueueBean bean)
	{
		return null;
	}
	
}
