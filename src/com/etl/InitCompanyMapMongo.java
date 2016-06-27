package com.etl;

import java.util.Date;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.db.MongoDb;
import com.db.Redis;
import com.ibm.icu.text.SimpleDateFormat;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.util.JsonUtil;
import com.zj.bean.Company51JobCompany2Bean;
import com.zj.bean.Company51PositionSonBean;
import com.zj.bean.Company51jobPosition2Bean;
import com.zj.exec.MainStatic;

public class InitCompanyMapMongo {

	private static Logger log = Logger.getLogger(InitCompanyMapMongo.class);
//	public static String mongoIp="192.168.1.11";
//	public static int mongoPort=27017;
//	public static String mongoDatabase="demo";
//	public static String redisString="192.168.1.11:51900";
	public static SimpleDateFormat df=new SimpleDateFormat("yyyy-MM-dd");
	public static void run(String mongoIP,int mongoPort,String mongoDatabase,String redisIp,int redisPort) throws Exception
	{
		MongoDb mongo = new MongoDb(mongoIP, mongoPort, mongoDatabase);
		Redis redis=new Redis(redisIp+":"+redisPort);
		redis.del(MainStatic.companyCodeMap);
		redis.del(MainStatic.jobCodeMap);
		DBCursor cursor= mongo.find("company51job");
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("jobs",Company51jobPosition2Bean.class);
		map.put("jobInfoList",Company51PositionSonBean.class);
		int companyCount=0;
		int jobCount=0;
		while(cursor.hasNext())
		{
			DBObject obj=cursor.next();
			obj.removeField("_id");
			Company51JobCompany2Bean bean=(Company51JobCompany2Bean) JsonUtil.getDtoFromJsonObjStr(obj.toString(),Company51JobCompany2Bean.class,map);
			companyCount++;
			log.info(companyCount+":公司编号:"+bean.getCompanyCode());
			redis.hset(MainStatic.companyCodeMap,Long.toString(bean.getCompanyCode()), bean.getCompanyUrl());
			for(Company51jobPosition2Bean son:bean.getJobs())
			{
				//System.out.println(son.getJobCode()+"\t"+son.getPositionUrl());
				String str="";
				Date da=null;
				for(Company51PositionSonBean s:son.getJobInfoList())
				{
					
					Date temp=df.parse(s.getPublishDate());
					//System.out.println(temp);
					if(da==null)
					{
						da=temp;
					}else{
						if(temp.compareTo(da)>0)
						{
							da=temp;
						}
					}
				}
				if(da!=null)
				str=df.format(da);
				jobCount++;
				//System.out.println(Long.toString(son.getJobCode())+"\t"+str);
				redis.hset(MainStatic.jobCodeMap,Long.toString(son.getJobCode()), str);
			}
		}
		log.info("公司数:"+companyCount+"\t职位数:"+jobCount);
		// redis.hset(MainStatic.companyCodeMap,Long.toString(company.getCompanyCode()), company.getCompanyUrl());
		 
		 
		// redis.hset(MainStatic.jobCodeMap,Long.toString(company.getJobCode()), company.getPublishDate());
	}
	public static void main(String[] args) {
		//InitCompanyMapMongo mongo=new InitCompanyMapMongo();
//		try {
//			mongo.run();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
}
