package com.zj.queue;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.Company51JobBean;
import com.zj.bean.JobsInputQueueBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.exec.MainStatic;
import com.zj.intoDb.IntoDb;

/**
 * /**
 * 51 job 对应 公司信息
 *
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class Company51JobAntQueue extends QueueFather{
	private Logger log = Logger.getLogger(Company51JobAntQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public  LinkedBlockingQueue<JobsInputQueueBean> info=null;
	/**
	 * 初始化队列信息
	 * @param flag 为 是否初始化
	 * @param intodb 为 入库类
	 */
	@SuppressWarnings("unchecked")
	public  void init(QueueBean queueBean,String listName2,boolean flag,IntoDb intodb)
	{
		log = Logger.getLogger(Company51JobAntQueue.class);
		if(queueBean.getQueueInputName()!=null)
		{
			redis=new Redis(queueBean.getInputQueueUrl());
			listName=listName2;
			isRedis=true;
		}else
		info=new LinkedBlockingQueue<JobsInputQueueBean>(queueBean.getInputQueueNum());
		//需要初始化公司code
		log.info("公司url初始化");
		Map<String,String> mm=null;
		try {
			mm=redis.hgetAll(MainStatic.companyCodeMap);
			log.info("公司url初始化大小:"+mm.size());
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if(mm==null)
		{}else{
			//初始化公司及公司 url
			for(Entry<String, String> entry:mm.entrySet())
			{
				Company51JobBean company=new Company51JobBean();
				company.setCompanyCode(Long.parseLong(entry.getKey()));
				company.setCompanyUrl(entry.getValue());
				//System.out.println(company.getCompanyUrl());
				intodb.add(JsonUtil.getJsonStr(company));
			}
		}
		mm=null;
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
		Matcher m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			if(!m_1.group(1).contains("010000"))
			{
				continue;
			}
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
				info.add(jobsInputQueueBean);
				}
			}else{
				continue;
			}
		}
		p_1 = Pattern.compile("hotDibiaoSearch\\(([^,]*?), this\\.innerHTML ,([^\\)]*?)\\)[^>]*?><b>([^<]*?)</b></a>");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			if(!m_1.group(1).contains("010000"))
			{
				continue;
			}
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
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
				info.add(jobsInputQueueBean);
				}
			}else{
				continue;
			}
		}
		//获取热门地标
		p_1 = Pattern.compile("hotDibiaoSearch\\(([^,]*?),[\\s]*?this\\.innerHTML[^>]*?>([^<]*?)</a>");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			//解析出每个地铁站信息
			//获取是否为有效数据
			if(m_1.group(1).contains("010000"))
			{
				//加入队列中
				JobsInputQueueBean jobsInputQueueBean=new JobsInputQueueBean();
				jobsInputQueueBean.setSubWayLineCode(moveLeftAndRight(m_1.group(1)));
				jobsInputQueueBean.setSubWayLine(null);
				jobsInputQueueBean.setSubWayStation(m_1.group(2));
				if(isRedis)
				{
					try {
						redis.rpush(listName, JsonUtil.getJsonStr(jobsInputQueueBean));
					} catch (Exception e) {
						// TODO Auto-generated catch block
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

		log.info("company 信息初始化完成");

	}
	
	
	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  JobsInputQueueBean get(QueueBean bean)
	{
		return (JobsInputQueueBean)getQueueBean(bean,info,JobsInputQueueBean.class,log,"51job 页面队列为空");
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
	
	
	
	public  void main(String[] args) {
//		JobsAntQueue main=new JobsAntQueue();
		//main.init(10000);
		
	}
} 
