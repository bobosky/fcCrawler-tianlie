package com.zj.queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.JobsInputQueueBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;

/**
 * 停车信息 队列容器
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class JobsAntQueue extends QueueFather{
	private  Logger log = Logger.getLogger(JobsAntQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public  LinkedBlockingQueue<JobsInputQueueBean> info=null;

	/**
	 * 初始化队列信息
	 */
	@SuppressWarnings("unchecked")
	public  void init(QueueBean queueBean,String listName2,boolean flag)
	{
		if(queueBean.getQueueInputName()!=null)
		{
			redis=new Redis(queueBean.getInputQueueUrl());
			listName=listName2;
			isRedis=true;
		}else
		info=new LinkedBlockingQueue<JobsInputQueueBean>(queueBean.getInputQueueNum());
		if(!flag)
			return;
		//>([^<]*?)</option>([^<]*?)</select> 页 </span>
		String url="";
		try{
		url = AntGetUrl.doGet("http://search.51job.com/jobsearch/map_search.php","gbk");
		}catch(Exception e)
		{
			log.error(e);
			e.printStackTrace();
			return;
		}
		Pattern p_1 = Pattern.compile("hotDibiaoSearch\\(([^,]*?), this\\.innerHTML ,([^\\)]*?)\\)[^>]*?>([^<]*?)</a>");
		//System.out.println(url);
		Matcher m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			//解析出每个地铁站信息
			//获取是否为有效数据
			if(m_1.group(2).contains("线"))
			{
				//加入队列中
				JobsInputQueueBean jobsInputQueueBean=new JobsInputQueueBean();
				jobsInputQueueBean.setSubWayLineCode(moveLeftAndRight(m_1.group(1)));
				jobsInputQueueBean.setSubWayLine(moveLeftAndRight(m_1.group(2)));
				jobsInputQueueBean.setSubWayStation(m_1.group(3));
				if(isRedis)
				{
					try {
						redis.rpush(listName, JsonUtil.getJsonStr(jobsInputQueueBean));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
				info.add(jobsInputQueueBean);
				}
				//System.out.println(jobsInputQueueBean.getSubWayLine()+"\t"+jobsInputQueueBean.getSubWayLineCode()+"\t"+jobsInputQueueBean.getSubWayStation());
			}else{
				continue;
			}
		}
		p_1 = Pattern.compile("hotDibiaoSearch\\(([^,]*?), this\\.innerHTML ,([^\\)]*?)\\)[^>]*?><b>([^<]*?)</b></a>");
		//System.out.println(url);
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			//解析出每个地铁站信息
			//获取是否为有效数据
			if(m_1.group(2).contains("线"))
			{
				//加入队列中
				JobsInputQueueBean jobsInputQueueBean=new JobsInputQueueBean();
				jobsInputQueueBean.setSubWayLineCode(moveLeftAndRight(m_1.group(1)));
				jobsInputQueueBean.setSubWayLine(moveLeftAndRight(m_1.group(2)));
				jobsInputQueueBean.setSubWayStation(m_1.group(3));
				//System.out.println(m_1.group(3));
				if(isRedis)
				{
					try {
						redis.rpush(listName, JsonUtil.getJsonStr(jobsInputQueueBean));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}else{
				info.add(jobsInputQueueBean);
				}
				//System.out.println(jobsInputQueueBean.getSubWayLine()+"\t"+jobsInputQueueBean.getSubWayLineCode()+"\t"+jobsInputQueueBean.getSubWayStation());
			}else{
				continue;
			}
		}
		
		
		log.info("job信息初始化完成");

	}
	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  JobsInputQueueBean get(QueueBean bean)
	{
		return (JobsInputQueueBean)getQueueBean(bean,info,JobsInputQueueBean.class,log,"51job 人群队列信息为空");
	}
	/**
	 * 添加内容
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(JobsInputQueueBean bean)
	{
		addQueueBean(bean,info);
	}
} 
