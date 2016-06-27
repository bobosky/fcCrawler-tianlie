package com.zj.queue;
import java.sql.ResultSet;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.log4j.Logger;

import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.CompanyBean4;
import com.zj.bean.QueueBean;

/**
 * /**
 * 51 job 对应 公司信息
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class CompanyZhaopinAntQueue extends QueueFather{
	private  Logger log = Logger.getLogger(CompanyZhaopinAntQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public  LinkedBlockingQueue<CompanyBean4> info=null;
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
		info=new LinkedBlockingQueue<CompanyBean4>(queueBean.getInputQueueNum());
		if(!flag)
			return;
		//>([^<]*?)</option>([^<]*?)</select> 页 </span>
		//通过数据库 中获取 没有被 命中的信息
		   MysqlConnection mysql = new MysqlConnection(
	 				"jdbc:mysql://192.168.85.11:3306/zjMysql", "root",
	 				"root");
		   String sql="select a.CompanyName,a.Keyword,b.CompanyName,b.Keyword from CompanySource as a "+
				   	"left join Company51JobSource as b on a.Keyword=b.Keyword "+
				    " where length(a.Keyword)>1 and b.Keyword is null "+
				    " order by b.CompanyName";
		  MysqlSelect select= mysql.sqlSelect(sql);
		 ResultSet result= select.resultSet;
		 try{
		  while(result.next())
		  {
			  //String companyName=result.getString(1);
			  String keyword=result.getString(2);
			  CompanyBean4 bean=new CompanyBean4();
			  bean.setKeyword(keyword);
			  if(isRedis)
	        	 {
	        		 redis.rpush(listName, JsonUtil.getJsonStr(bean));
	        	 }else{
			  info.add(bean);
	        	 }
		  }
		 }catch(Exception e)
		 {
			 
		 }
		  
		System.out.println("company Name 信息初始化完成");

	}
	
	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  CompanyBean4 get(QueueBean bean)
	{
		return (CompanyBean4)getQueueBean(bean,info,CompanyBean4.class,log,"智联招聘队列信息为空");
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
