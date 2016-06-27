package com.zj.intoDb;

import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.FileUtil;
import com.util.MongoDb;
import com.zj.bean.QueueBean;
import com.zj.config.FileConfig;

/**
 * 队列信息入库公共类 线程
 * @author Administrator
 *
 */
public class IntoDb implements Runnable{
	private static Logger log = Logger.getLogger(IntoDb.class);

	private boolean flag=false;
	/**
	 * 队列入库容器
	 */
	private LinkedBlockingQueue<String> intoDb=null;
	/**
	 * 每次执行的时间
	 */
	private int time=10000;
	/**
	 * 文件操作类
	 */
	private FileUtil fileUtil=null;
	
	/**
	 * redis
	 */
	public Redis redis=null;
	
	public boolean isRedis=false;
	/**
	 * 写文件和redis 是否一起进行
	 */
	public boolean isAllWriteRun=false;
	/**
	 * 队列名
	 */
	public String listName="";
	
	public QueueBean queueBean=null;
	
	public MongoDb mongo = null;
	public boolean ismongo=false;
	/**
	 * 初始化队列信息
	 * @param maxQueue 队列大小
	 * @param time 每次执行的周期 毫秒
	 * @param type 如果type为2 则不管是否是redis 只写入文件中
	 * @param category 1为停车场信息 2为job信息 3为地铁口对应公交信息 4位公交信息文件 
	 * 5为搜房信息文件 6公司对应job 7对应公司 job的最终信息 8对应智联招聘公司信息 9 对应智联招聘公司最终信息
	 * @param fileName 如果为文件 则 以^@@^为分隔符第二个字符为文件的字符集格式
	 */
	public IntoDb(QueueBean queueBean,String database,String listName,int type,int category,String fileName,int time)
	{
		this.queueBean=queueBean;
		this.isAllWriteRun=queueBean.isAllWriteRun();
		this.listName=listName;
		if(type==2)
		{
			isAllWriteRun=false;
			isRedis=false;
			ismongo=false;
			this.listName=null;
		}
		if(isAllWriteRun)
		{
			//只允许队列信息入redis
			isRedis=true;
			redis=new Redis(queueBean.getOutQueueUrl());
			this.listName=listName;
			intoDb=new LinkedBlockingQueue<String>(queueBean.getOutQueueNum());
		}else{
			if(this.listName!=null)
			{
				if(database.equals("Redis"))
				{
					isRedis=true;
					redis=new Redis(queueBean.getOutQueueUrl());
				}else if(database.equals("Mongo")){
					ismongo=true;
					mongo=new MongoDb(queueBean.getOutQueueUrl(),this.listName.split(",")[0]);
					this.listName=this.listName.split(",")[1];
				}
			}else
			{
				intoDb=new LinkedBlockingQueue<String>(queueBean.getOutQueueNum());
			}
		}
		this.time=time;
		if(!isRedis ||isAllWriteRun)
		{
			if(true)
			{
				if(fileName!=null)
				{
					fileUtil=new FileUtil(fileName,"utf-8");
				}
				else if(category==1)
				{
					fileUtil=new FileUtil(FileConfig.parkingFile,FileConfig.parkingFileCode);
				}else if(category==2){
					fileUtil=new FileUtil(FileConfig.jobsFile,FileConfig.jobsFileCode);
				}else if(category==3)
				{
					fileUtil=new FileUtil(FileConfig.subwayAndBusFile,FileConfig.subwayAndBusFileCode);
				}else if(category==4)
				{
					fileUtil=new FileUtil(FileConfig.busAndStationFile,FileConfig.busAndStationFileCode);
				}else if(category==5)
				{
					fileUtil=new FileUtil(FileConfig.fangFile,FileConfig.fangFileCode);
				}else if(category==6)
				{
					fileUtil=new FileUtil(FileConfig.companyFile,FileConfig.companyFileCode);
				}else if(category==7)
				{
					fileUtil=new FileUtil(FileConfig.companyEndFile,FileConfig.companyEndFileCode);
				}else if(category==8)
				{
					fileUtil=new FileUtil(FileConfig.companySearchFile,FileConfig.companySearchFileCode);
				}else if(category==9)
				{
					fileUtil=new FileUtil(FileConfig.companySearchEndFile,FileConfig.companySearchEndFileCode);
				}else if(category==10)
				{
					fileUtil=new FileUtil(FileConfig.companyDescFile,FileConfig.companyDescFileCode);
				}else if(category==11)
				{
					fileUtil=new FileUtil(FileConfig.companyDescJobFile,FileConfig.companyDescJobFileCode);
				}else if(category==12)
				{
					fileUtil=new FileUtil(FileConfig.companyDescJobDescFile,FileConfig.companyDescJobDescFileCode);
				}else if(category==13)
				{
					fileUtil=new FileUtil(FileConfig.bussinessAreaFile,FileConfig.bussinessAreaFileCode);
				}
			}else{
				log.error("未设置相关的入库类型");
			}
		}else{//配置redis
			
		}
		log.info("初始化完成"+"\t"+Thread.currentThread());
	}
	

	/**
	 * 执行方法
	 */
	public void run()
	{
		synchronized (this) {
			if(flag)
			{
				log.error("入库线程已启动，并只能为1个 关闭当前线程:"+Thread.currentThread().getName());
				return;
			}
			log.info(queueBean.getName()+":入库程序开始执行");
			boolean flagL=false;
			while(true)
			{
				if(!queueBean.isExecIsRun())
				{
					flagL=true;
				}
				if(!isRedis||isAllWriteRun)
				{
					int size=intoDb.size();
					fileUtil.wirte(intoDb, size);
				}
				try {
					if(flagL)
					{
						log.info(queueBean.getName()+"入库程序结束");
						break;
					}
					log.info("入库队列等待:线程号:"+Thread.currentThread().getName());
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	
	}
	
	/**
	 * 队列中添加
	 * @param str
	 */
	public void add(String str)
	{
		//System.out.println(str);
		while(true)
		{
			try{
				if(isAllWriteRun)
				{
					redis.rpush(listName, str);
					intoDb.add(str);
				}
				else if(isRedis)
				{
					redis.rpush(listName, str);
					//System.out.println("成功");
				}else if(ismongo)
				{
					mongo.insert(listName, str);
				}else
				{
					intoDb.add(str);
				}
			break;
			}
			catch(Exception e)
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * 队列中添加
	 * 只对isAllWriteRun有效
	 * @param flag =true则表示只添加入redis中
	 * @param flag 如果为 false则表示只添加如intoDb中
	 * @param str
	 */
	public void add(String str,boolean flag)
	{
		//System.out.println(str);
		while(true)
		{
			try{
				if(isAllWriteRun)
				{
					if(flag)
					{
						redis.rpush(listName, str);
					}else{
						intoDb.add(str);
					}
				}
				else if(isRedis)
				{
					redis.rpush(listName, str);
					//System.out.println("成功");
				}else if(ismongo)
				{
					mongo.insert(listName,str);
				}
				else {
					intoDb.add(str);
				}
			break;
			}
			catch(Exception e)
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
}
