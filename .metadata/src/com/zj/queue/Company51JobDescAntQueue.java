package com.zj.queue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.Company51JobBean;
import com.zj.bean.QueueBean;

/**
 * /**
 * 51 job 对应 公司信息 获取 公司信息
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class Company51JobDescAntQueue extends QueueFather{
	private  Logger log = Logger.getLogger(Company51JobDescAntQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public  LinkedBlockingQueue<Company51JobBean> info=null;
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
		info=new LinkedBlockingQueue<Company51JobBean>(queueBean.getInputQueueNum());
		if(!flag)
			return;
		//>([^<]*?)</option>([^<]*?)</select> 页 </span>
		
		//需要读取本地从 51job上获取的所有公司url信息 
		File file=new File(queueBean.getInputFileList().get(0));
		BufferedReader reader = null;
		try{
		 InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");  
         reader = new BufferedReader(read);
         String tempString = null;
         log.info("开始读取文件:"+listName2);
         // 一次读入一行，直到读入null为文件结束
         int i=0;
         HashSet<Long> map=new HashSet<Long>();
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 i++;
	        	 if(i%1000==0)
	        	 {
	        		 log.info("读取文件数量:"+i);
	        	 }
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	// System.out.println(tempString);
	        	 //判断是否重复
	        	 Company51JobBean com=(Company51JobBean)JsonUtil.getDtoFromJsonObjStr(tempString, Company51JobBean.class);
	        	 if(map.contains(com.getCompanyCode()))
	        	 {
	        		 //重复
	        		 continue;
	        	 }
	        	 map.add(com.getCompanyCode());
	        	 if(isRedis)
	        	 {
	        		 log.info("添加公司:"+tempString);
	        		 redis.rpush(listName, tempString);
	        	 }else{
	        	 //System.out.println(com.getCompanyName()+"\t"+com.getCompanyUrl());
	        		 info.add(com);
	        	 }
	         }
	         map=null;
         try {
	 			reader.close();
	 		} catch (IOException e1) {
	 			// TODO Auto-generated catch block
	 			e1.printStackTrace();
	 		}
	 		try {
	 			read.close();
	 		} catch (IOException e) {
	 			// TODO Auto-generated catch block
	 			e.printStackTrace();
	 		}
		}
         catch(Exception e)
         {
        	 e.printStackTrace();
         }
		finally{
	       
		}
		
		log.info("company 获取  信息初始化完成");

	}
	
	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  Company51JobBean get(QueueBean bean)
	{
		return (Company51JobBean)getQueueBean(bean,info,Company51JobBean.class,log,"51job 公司名 队列为空");
	}
	/**
	 * 添加内容
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(Company51JobBean bean)
	{
		addQueueBean(bean,info);
	}
	
	
	public static void main(String[] args) {
//		JobsAntQueue main=new JobsAntQueue();
		//main.init(10000);
		
	}
} 
