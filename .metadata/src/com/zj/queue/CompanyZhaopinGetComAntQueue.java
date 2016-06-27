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
import com.zj.bean.CompanyBean4;
import com.zj.bean.QueueBean;

/**
 * /**
 * 51 job 对应 公司信息 获取 公司信息
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class CompanyZhaopinGetComAntQueue extends QueueFather{
	private static Logger log = Logger.getLogger(CompanyZhaopinGetComAntQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public static LinkedBlockingQueue<CompanyBean4> info=null;
	/**
	 * redis
	 */
	public static Redis redis=null;
	/**
	 * 队列名
	 */
	public static String listName="";
	
	public static boolean isRedis=false;
	/**
	 * 初始化队列信息
	 */
	public static void init(QueueBean queueBean,String listName2,boolean flag)
	{
		if(queueBean.getQueueInputName()!=null)
		{
			redis=new Redis(queueBean.getInputQueueUrl());
			listName=listName2;
			isRedis=true;
		}else
		info=new LinkedBlockingQueue<CompanyBean4>(queueBean.getInputQueueNum());
		if(!flag)
			return;
		//>([^<]*?)</option>([^<]*?)</select> 页 </span>
		File file=new File(System.getProperty("user.dir")+"/data/companySearchDesc-2014-11-28.txt");
		BufferedReader reader = null;
		try{
		 InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");  
         reader = new BufferedReader(read);
         String tempString = null;
         log.info("开始读取文件:"+file);
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
	        	 if(!isRedis)
	     		{
	        	 //System.out.println(tempString);
	        	 CompanyBean4 com=(CompanyBean4)JsonUtil.getDtoFromJsonObjStr(tempString, CompanyBean4.class);
	        	 //System.out.println(com.getCompanyName()+"\t"+com.getCompanyUrl());
	        	 System.out.println("company:"+com.getCompanyName());
	        	 info.add(com);
	     		}else{
	     			redis.rpush(listName, tempString);
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
	public  CompanyBean4 get(QueueBean bean)
	{
		return (CompanyBean4)getQueueBean(bean,info,CompanyBean4.class,log,"智联招聘 公司名队列信息为空");
	}
	/**
	 * 添加内容
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(CompanyBean4 bean)
	{
		addQueueBean(bean,info);
	}
	
} 
