package com.zj.queue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.CompanyBean3;
import com.zj.bean.QueueBean;

/**
 * /**
 * 51 job 对应 公司信息 获取 公司信息
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class Company51JobGetComAntQueue extends QueueFather{
	private  Logger log = Logger.getLogger(Company51JobGetComAntQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public  LinkedBlockingQueue<CompanyBean3> info=null;
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
		info=new LinkedBlockingQueue<CompanyBean3>(queueBean.getInputQueueNum());
		if(!flag)
			return;
		//>([^<]*?)</option>([^<]*?)</select> 页 </span>
		File file=new File(System.getProperty("user.dir")+"/data/companyFilter.txt");
		BufferedReader reader = null;
		try{
		 InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");  
         reader = new BufferedReader(read);
         String tempString = null;
         System.out.println("开始读取文件");
         // 一次读入一行，直到读入null为文件结束
         int i=0;
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 i++;
	        	 if(i%10000==0)
	        	 {
	        		 log.info("读取文件数量:"+i);
	        	 }
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 if(isRedis)
	        	 {
	        		 redis.rpush(listName, tempString);
	        	 }else{
	        	 CompanyBean3 com=(CompanyBean3)JsonUtil.getDtoFromJsonObjStr(tempString, CompanyBean3.class);
	        	 //System.out.println(com.getCompanyName()+"\t"+com.getCompanyUrl());
	        	
	        	 info.add(com);
	        	 }
	         }
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
		System.out.println("company 获取  信息初始化完成");

	}
	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  CompanyBean3 get(QueueBean bean)
	{
		return (CompanyBean3)getQueueBean(bean,info,CompanyBean3.class,log,"51job 职位匹配名 队列为空");
	}
	/**
	 * 添加内容
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(CompanyBean3 bean)
	{
		addQueueBean(bean,info);
	}
	
	
	public static void main(String[] args) {
//		JobsAntQueue main=new JobsAntQueue();
		//main.init(10000);
		
	}
} 
