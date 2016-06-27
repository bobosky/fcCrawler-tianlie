package com.etl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.db.MongoDb;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.util.FileUtil2;
import com.util.JsonUtil;
import com.zj.bean.FangBean;
import com.zj.bean.FangMonthCountBean;
import com.zj.bean.LationLngLat;

/**
 * 从文件写入mongo中
 * @author Administrator
 *
 */
public class FileToMongo {
	Logger log = Logger.getLogger(MongoToMysql.class);
	/**
	 * 文件指针
	 */
	//private File file = null;
	/**
	 * 文件类型
	 */
	//private String code = "utf-8";

	/**
	 * 数据库表名字
	 */
	private String name = "";

	/**
	 * 字段名
	 */
	private String nameColl="";
	
	private String mongoIp="192.168.1.11";
	
	private int mongoPort=27017;
	/**
	 * database
	 */
	private String mongoDatabase="demo";
	/**
	 * 方法名
	 */
	private String methodName="";
	
	private String fangStatic="fang";
	private String fangUpdate="fangUpdate";
	private String filePath="";
	
	private MongoDb mongo =null;

	
	public FileToMongo(String mongoIp,int mongoPort,String mongoDatabase,String fangSource,String fangUpdate,String methodName,String filePath)
	{
		// 初始化log4j
		PropertyConfigurator.configure("./log4j.properties");
		this.mongoIp=mongoIp;
		this.mongoPort=mongoPort;
		this.mongoDatabase=mongoDatabase;
		this.methodName=methodName;
		this.fangStatic=fangSource;
		this.fangUpdate=fangUpdate;
		this.filePath=filePath;
	}
	
	public void run()
	{
		if(filePath.equals(""))
		{
			log.info("输入的文件错误");
			System.exit(1);
		}
		FileUtil2 file=new FileUtil2(filePath,"utf-8",false);
		mongo = new MongoDb(mongoIp, mongoPort, mongoDatabase);
		LinkedList<String> list=file.readAndClose();
		LinkedList<String> list2=new LinkedList<String>();
		if(methodName.equals("fang"))
		{
			//执行搜房数据更新 及新增
			for(String str:list)
			{
				list2.add(str.replaceAll("\"_id\" : [^,]*?,",""));
				//System.out.println(str);
			}
			intoFang(list2);
		}else if(methodName.equals("modifyFang"))
		{
			for(String str:list)
			{
				list2.add(str.replaceAll("\"_id\" : [^,]*?,",""));
				//System.out.println(str);
			}
			modifyFang(list2);
		}
		//做一次系统性纠正
		FangNewObject.run(FangNewObject.FANG);
	}
	
	/**
	 * 修复2014-12月的数据
	 * @param list
	 */
	public void modifyFang(LinkedList<String> list)
	{
		FileUtil2 file3=new FileUtil2("f:\\zj\\fangBak2.dat","utf-8");
		LinkedList<String> input=new LinkedList<String>();
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("hireTrendValue",FangMonthCountBean.class);
		map.put("priceTrendValue",FangMonthCountBean.class);
		map.put("location",LationLngLat.class);
	
		for(String str:list)
		{
			FangBean bean2=(FangBean)JsonUtil.getDtoFromJsonObjStr(str,FangBean.class,map);
			bean2.setYear(2014);
			bean2.setMonth(12);
			List<FangMonthCountBean> hireBean=bean2.getHireTrendValue();
			List<FangMonthCountBean> priceBean=bean2.getPriceTrendValue();
			for(FangMonthCountBean bean:hireBean)
			{
				//System.out.println(fangcode+"\t"+bean.getMonth());
				if(bean.getMonth().contains("-"))
				{
					//添加时间
					
				}else{
					if(bean.getMonth().length()==1)
					{
						bean.setMonth("2014-0"+bean.getMonth());
					}else{
						bean.setMonth("2014-"+bean.getMonth());
					}
				}
			
			}
			for(FangMonthCountBean bean:priceBean)
			{
				if(bean.getMonth().contains("-"))
				{
				
				}else{
					if(bean.getMonth().length()==1)
					{
						bean.setMonth("2014-0"+bean.getMonth());
					}else{
						bean.setMonth("2014-"+bean.getMonth());
					}
				}
			
			}
			input.add(JsonUtil.getJsonStr(bean2));
			
		}
		file3.wirte(input,input.size());
	}
	
	/**
	 * 搜房数据更新及新增
	 * @param list
	 */
	public void intoFang(LinkedList<String> list)
	{
		//获取全部的fangcode
		HashSet<Long> fangUniCode=new HashSet<Long>();
		HashSet<String> fangHire=new HashSet<String>();
		HashSet<String> fangPrice=new HashSet<String>();
		BasicDBObject doc=new BasicDBObject();
		doc.append("fangCode",1);
		doc.append("hireTrendValue", 1);
		doc.append("priceTrendValue",1);
		DBCursor cursor=mongo.find(fangStatic,new BasicDBObject(),doc);
		while(cursor.hasNext())
		{
			BasicDBObject obj=(BasicDBObject)cursor.next();
			if(obj==null)
			{
				continue;
			}
			Long fangcode=obj.getLong("fangCode");
			List<FangMonthCountBean> hireBean=(List<FangMonthCountBean>)JsonUtil.getListFromJsonArrStr(obj.getString("hireTrendValue"),FangMonthCountBean.class); 
			List<FangMonthCountBean> priceBean=(List<FangMonthCountBean>)JsonUtil.getListFromJsonArrStr(obj.getString("priceTrendValue"),FangMonthCountBean.class);
//			if(fangcode==1010079300L)
//			{
//				System.out.println("存在");
//			}
			fangUniCode.add(fangcode);
			for(FangMonthCountBean bean:hireBean)
			{
				//System.out.println(fangcode+"\t"+bean.getMonth());
				if(bean.getMonth().contains("-"))
				{
					//添加时间
					fangHire.add(fangcode+bean.getMonth());
				}else{
					if(bean.getMonth().length()==1)
					{
						fangHire.add(fangcode+"2014-0"+bean.getMonth());
					}else{
						fangHire.add(fangcode+"2014-"+bean.getMonth());
					}
				}
			
			}
			for(FangMonthCountBean bean:priceBean)
			{
				if(bean.getMonth().contains("-"))
				{
					//添加时间
					fangPrice.add(fangcode+bean.getMonth());
				}else{
					if(bean.getMonth().length()==1)
					{
						fangPrice.add(fangcode+"2014-0"+bean.getMonth());
					}else{
						fangPrice.add(fangcode+"2014-"+bean.getMonth());
					}
				}
			
			}
		//	System.out.println(fangcode);
		}
		//获取对应的时间节点
		//获取update数据集
		doc=new BasicDBObject();
		doc.append("fangCode",1);
		doc.append("month", 1);
		doc.append("year",1);
		cursor=mongo.find(fangUpdate,new BasicDBObject(),doc);
		while(cursor.hasNext())
		{
			BasicDBObject obj=(BasicDBObject)cursor.next();
			if(obj==null)
			{
				continue;
			}
			//添加增量的数据集
			Long fangcode=obj.getLong("fangCode");
			int month=obj.getInt("month");
			int year=obj.getInt("year");
			String temp=fangcode+""+year+"-"+month;
			//System.out.println(temp);
			fangHire.add(temp);
			fangPrice.add(temp);
		}
		
		
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("hireTrendValue",FangMonthCountBean.class);
		map.put("hireTrendValueEtl",FangMonthCountBean.class);
		map.put("priceTrendValue",FangMonthCountBean.class);
		map.put("location",LationLngLat.class);
		int iCount=0;
		for(String str:list)
		{
			iCount++;
			log.info(iCount);
			FangBean bean=(FangBean)JsonUtil.getDtoFromJsonObjStr(str,FangBean.class,map);
			long fangcode=bean.getFangCode();
			if(fangcode==0L)
			{
				continue;
			}
			if(bean.getMonth()==0)
			{//如果为空则为当前月的时间
				bean.setMonth(Calendar.getInstance().get(Calendar.MONTH));
			}
			if(bean.getYear()==0)
			{
				bean.setYear(Calendar.getInstance().get(Calendar.YEAR));
			}
			//判断是否存在
			if(!fangUniCode.contains(fangcode))
			{
				//如果不包含则为新的房源
				mongo.insert(fangStatic,JsonUtil.getJsonStr(bean));
				log.info("新增:"+bean.getFangCode());
				continue;
			}
			List<FangMonthCountBean> hireList=bean.getHireTrendValueEtl();
			List<FangMonthCountBean> priceList=bean.getPriceTrendValue();
			String date="";
			HashSet<String> dateList=new HashSet<String>();
			for(FangMonthCountBean bean2:hireList)
			{
				String date1=fangcode+bean2.getMonth();
				if(fangHire.contains(date1))
				{
					continue;
				}
				dateList.add(bean2.getMonth());
				fangHire.add(date1);
			}
			for(FangMonthCountBean bean2:priceList)
			{
				String date1=fangcode+bean2.getMonth();
				if(fangPrice.contains(date1))
				{
					continue;
				}
				dateList.add(bean2.getMonth());
				fangPrice.add(date1);
			}
			//获取有效的时间
			for(String monthStr:dateList)
			{
				String[] strList=monthStr.split("-");
				bean.setYear(Integer.parseInt(strList[0]));
				bean.setMonth(Integer.parseInt(strList[1]));
				for(FangMonthCountBean bean2:hireList)
				{
					if(monthStr.equals(bean2.getMonth()))
					{
					//为新的数据源
						bean.setHireValue(bean2.getMoney());
						break;
					}
				}
				for(FangMonthCountBean bean2:priceList)
				{
					if(monthStr.equals(bean2.getMonth()))
					{
					//为新的数据源
						bean.setPriceValue(bean2.getMoney());
						break;
					}
				}
				mongo.insert(fangUpdate,JsonUtil.getJsonStr(bean));
				log.info("新增Update:"+bean.getFangCode()+"\t"+bean.getYear()+"-"+bean.getMonth());
			}
		}
	}
	public static void main(String[] args) {
		FileToMongo exec=new FileToMongo("192.168.1.4",27017,"demo","fang","fangUpdate","fang","E:\\fangDesc-2015-02-25.txt");
	//	FileToMongo exec=new FileToMongo("192.168.1.4",27017,"demo","modifyFang","F:\\zj\\fangBak.dat");
		exec.run();
	}
}
