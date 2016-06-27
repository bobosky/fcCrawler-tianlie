package com.etl;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.log4j.Logger;

import com.db.MongoDb;
import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.etl.bean.Cat;
import com.etl.bean.DianpingBean;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.util.DateFormat;
import com.util.FileUtil;
import com.util.FileUtil2;
import com.util.JsonUtil;
import com.zj.bean.Company51PositionSonBean;
import com.zj.bean.Company51jobPosition2Bean;
import com.zj.bean.JobsSonBean;
import com.zj.bean.SubwayAndBusBean;
import com.zj.bean.SubwayAndBusSonBean;
import com.zj.bean.SubwayBean;
import com.zj.bean.SubwayStartBean;

/**
 * 文件读写操作类
 * 
 * @author Administrator
 *
 */
public class MongoToMysql {
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
	private String mongoDatabase="demo";
	
	
	public String getMongoIp() {
		return mongoIp;
	}
	public void setMongoIp(String mongoIp) {
		this.mongoIp = mongoIp;
	}
	public int getMongoPort() {
		return mongoPort;
	}
	public void setMongoPort(int mongoPort) {
		this.mongoPort = mongoPort;
	}
	public String getMongoDatabase() {
		return mongoDatabase;
	}
	public void setMongoDatabase(String mongoDatabase) {
		this.mongoDatabase = mongoDatabase;
	}
	/**
	 * 文件
	 * 
	 * @param filename
	 * @param code
	 */
	public MongoToMysql(String name) {
		this.name = name;
	}
	/**
	 * 文件
	 * 
	 * @param filename
	 * @param code
	 */
	public MongoToMysql(String name,String nameColl) {
		this.name = name;
		this.nameColl=nameColl;
	}
	

	public void run() {
		MysqlConnection mysql = new MysqlConnection(
				"jdbc:mysql://192.168.1.4:3306/zjMysql", "root", "root");
//		MysqlConnection mysql=null;
//		MongoDb mongo=null;
		MongoDb mongo = new MongoDb(mongoIp, mongoPort, mongoDatabase);
		if (this.name.equals("busAndStation")) {
			intoBusAndStation(mongo, mysql);
		}else if (this.name.equals("parking")) {
			intoParkingStation(mongo, mysql);
		}else if (this.name.equals("subwayAndBus")) {
			intoSubwayAndBus(mongo, mysql);
		}else if (this.name.equals("subwayAndJob")) {
			intoSubwayAndJob(mongo, mysql);
		}else if (this.name.equals("test")) {
			intoSubwayAndBusTest(mongo, mysql);
		}else if(this.name.equals("poi"))
		{
			intoPoi(mongo,mysql,nameColl);
		}else if(this.name.equals("fang"))
		{
			intoFang(mongo,mysql);
		}else if(this.name.equals("busPoi"))
		{
			intoBusPoi(mongo,mysql,nameColl);
		}else if(this.name.equals("51company"))
		{
			into51Company(mongo,mysql);
		}else if(this.name.equals("dianping"))
		{
			intoDianping(mongo,mysql);
		}
			
			
		
			

	}
	/**
	 * busAndStation
	 * @param mongo
	 * @param mysql
	 */
	public void intoBusAndStation(MongoDb mongo, MysqlConnection mysql) {
		DBCursor ret = mongo.find("busStationInfo");
		//TreeSet<String> tree = new TreeSet<String>();
		while (ret.hasNext()) {
			try {
				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				if (bdbObj != null) {
					String busCompany = bdbObj.getString("busCompany");
					String busName = bdbObj.getString("busName");
					String downStationTime = bdbObj
							.getString("downStationTime");
					String upStationTime = bdbObj.getString("upStationTime");
					String intervalTime = bdbObj.getString("intervalTime");
					String payDisc = bdbObj.getString("payDisc");
					String[] stationName = bdbObj.getString("stationName")
							.replaceAll("[\\[\\]\"\\s]", "").split(",");
					String[] stationNameRever = bdbObj
							.getString("stationNameRever")
							.replaceAll("[\\[\\]\"\\s]", "").split(",");
					// 入mysql
					String sql = "insert into BusInfo(BusCompany,BusLine,UpStationTime,"
							+ "DownStationTime,PayDisc,IntervalTime) values("
							+ getString(busCompany)
							+ getStringAndCom(busName)
							+ getStringAndCom(upStationTime)
							+ getStringAndCom(downStationTime)
							+ getStringAndCom(payDisc)
							+ getStringAndCom(intervalTime) + ")";
					System.out.println(sql);
					mysql.sqlInsert(sql);
					int i = 0;
					LinkedList<String> sqlBath=new LinkedList<String>();
					for (String station : stationName) {
						i++;
						String sqlTemp = "insert into BusAndStation(BusLine,IsInver,BusStation,BusStationID) values("
								+ getString(busName)
								+ ",0"
								+ getStringAndCom(station) + "," + i + ")";
						sqlBath.add(sqlTemp);
					}
					mysql.sqlInsertBatch(sqlBath);
					sqlBath=new LinkedList<String>();
					i = 0;
					for (String station : stationNameRever) {
						i++;
						String sqlTemp = "insert into BusAndStation(BusLine,IsInver,BusStation,BusStationID) values("
								+ getString(busName)
								+ ",1"
								+ getStringAndCom(station) + "," + i + ")";
						sqlBath.add(sqlTemp);
					}
					mysql.sqlInsertBatch(sqlBath);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * Parking
	 * @param mongo
	 * @param mysql
	 */
	public void intoParkingStation(MongoDb mongo, MysqlConnection mysql) {
		DBCursor ret = mongo.find("parkingStation");
		//TreeSet<String> tree = new TreeSet<String>();
		while (ret.hasNext()) {
			try {
				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				if (bdbObj != null) {
					String areaCategory = bdbObj.getString("areaCatygory");
					String areaCode = bdbObj.getString("areaCode");
					String bakCode = bdbObj
							.getString("bakCode");
					String carNum = bdbObj.getString("carNum");
					String  machineryCarNum= bdbObj.getString("machineryCarNum");
					String mechanicalCarNum = bdbObj.getString("mechanicalCarNum");
					String parkingCategory = bdbObj.getString("parkingCategory");
					String parkingManageComp = bdbObj.getString("parkingManageComp");
					String parkingName = bdbObj.getString("parkingName");
					String crawlerTime=bdbObj.getString("crawlerTime");
					// 入mysql
					String sql = "insert into Parking(AreaCatygory,AreaCode,BakCode,"
							+ "CarNum,MachineryCarNum,MechanicalCarNum,"
							+ "ParkingCategory,ParkingManageComp,ParkingName,CrawlerTime) values("
							+ getString(areaCategory)
							+ getStringAndCom(areaCode)
							+ getStringAndCom(bakCode)
							+ getStringAndCom(carNum)
							+ getStringAndCom(machineryCarNum)
							+ getStringAndCom(mechanicalCarNum)
							+ getStringAndCom(parkingCategory)
							+ getStringAndCom(parkingManageComp)
							+ getStringAndCom(parkingName) 
							+ getStringAndCom(crawlerTime)+")";
					System.out.println(sql);
					mysql.sqlInsert(sql);
					//int i = 0;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public void intoSubwayAndBus(MongoDb mongo, MysqlConnection mysql) {
		DBCursor ret = mongo.find("subwayAndBus");
		//TreeSet<String> tree = new TreeSet<String>();
		while (ret.hasNext()) {
			try {
				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				if (bdbObj != null) {
					String subwayLine = bdbObj.getString("subwayLine");
					String subwayStation = bdbObj.getString("subwayStation");
					String portAndBus = bdbObj.getString("portAndBus");
					//System.out.println(portAndBus);
					//System.out.println(bdbObj.toString());
//					JSONArray ja = JSONArray.fromObject(bdbObj.toString());
//					SubwayAndBusSonBean son=(SubwayAndBusSonBean)bdbObj.get(portAndBus);
//					 Map<String, Object> classMap = new HashMap<String, Object>(); 
//					 classMap.put("portAndBus", SubwayAndBusSonBean.class);
//					 SubwayAndBusBean list = (SubwayAndBusBean) JSONArray.toArray(ja, SubwayAndBusBean.class, classMap);
//					 
//					System.out.println(list.getPortAndBus().size());
					JSONArray  js= JSONArray.fromObject(portAndBus);
					Object[] obj=js.toArray();
					String sql="insert into SubwayAndBus(SubwayLine,SubwayStation,BusLine,subwayPort) values(";
					sql+=getString(subwayLine)+getStringAndCom(subwayStation);
					LinkedList<String> sqlBath=new LinkedList<String>();
					for(int i=0;i<obj.length;i++)
					{

					JSONObject jsonObj= JSONObject.fromObject(obj[i]);
					SubwayAndBusSonBean son= (SubwayAndBusSonBean)JSONObject.toBean(jsonObj,SubwayAndBusSonBean.class); 
					String sqlTemp2=sql+getStringAndCom(son.getPort());
					for(int j=0;j<son.getBusStation().size();j++)
					{
						String bus=son.getBusStation().get(j);
						String sqlTemp3=sqlTemp2+getStringAndCom(bus)+")";
						System.out.println(sqlTemp3);
						sqlBath.add(sqlTemp3);
					}
					}
					mysql.sqlInsertBatch(sqlBath);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	
	public void intoSubwayAndBusTest(MongoDb mongo, MysqlConnection mysql) {
		DBCursor ret = mongo.find("subwayAndBus");
		//TreeSet<String> tree = new TreeSet<String>();
		while (ret.hasNext()) {
			try {
				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				if (bdbObj != null) {
//					String subwayLine = bdbObj.getString("subwayLine");
//					String subwayStation = bdbObj.getString("subwayStation");
//					String portAndBus = bdbObj.getString("portAndBus");
					//System.out.println(portAndBus);
					//System.out.println(bdbObj.toString());
//					JSONArray ja = JSONArray.fromObject(bdbObj.toString());
//					SubwayAndBusSonBean son=(SubwayAndBusSonBean)bdbObj.get(portAndBus);
//					 Map<String, Object> classMap = new HashMap<String, Object>(); 
//					 classMap.put("portAndBus", SubwayAndBusSonBean.class);
//					 SubwayAndBusBean list = (SubwayAndBusBean) JSONArray.toArray(ja, SubwayAndBusBean.class, classMap);
//					 
//					System.out.println(list.getPortAndBus().size());
					String str="[{"+bdbObj.toString().substring(bdbObj.toString().indexOf("} ,")+3)+"]";
					str=str.replaceFirst("(\"ok\" : true ,)","");
					System.out.println(str);
					//JSONArray ja = JSONArray.fromObject(str);
					 Map<String, Object> classMap = new HashMap<String, Object>(); 
					 classMap.put("portAndBus", SubwayAndBusSonBean.class);
					 classMap.put("subWayDesc", SubwayBean.class);
					 classMap.put("departDesc", SubwayStartBean.class);
					 SubwayAndBusBean bean =(SubwayAndBusBean) JsonUtil.getListFromJsonArrStr(str, SubwayAndBusBean.class, classMap);
					// SubwayAndBusBean bean = (SubwayAndBusBean) JSONArray.toArray(ja, SubwayAndBusBean.class, classMap);
					System.out.println(bean.getSubwayLine());
//					JSONArray  js= JSONArray.fromObject(portAndBus);
//					Object[] obj=js.toArray();
//					String sql="insert into SubwayAndBus(SubwayLine,SubwayStation,BusLine,subwayPort) values(";
//					sql+=getString(subwayLine)+getStringAndCom(subwayStation);
//					LinkedList<String> sqlBath=new LinkedList<String>();
//					for(int i=0;i<obj.length;i++)
//					{
//
//					JSONObject jsonObj= JSONObject.fromObject(obj[i]);
//					SubwayAndBusSonBean son= (SubwayAndBusSonBean)JSONObject.toBean(jsonObj,SubwayAndBusSonBean.class); 
//					String sqlTemp2=sql+getStringAndCom(son.getPort());
//					for(int j=0;j<son.getBusStation().size();j++)
//					{
//						String bus=son.getBusStation().get(j);
//						String sqlTemp3=sqlTemp2+getStringAndCom(bus)+")";
//						System.out.println(sqlTemp3);
//						sqlBath.add(sqlTemp3);
//					}
//					}
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void intoSubwayAndJob(MongoDb mongo, MysqlConnection mysql) {
		//DBCursor ret = mongo.findAndCursor("subwayAndJobSimple","crawlerTime",DateFormat.parse(new Date()));
		
		DBCursor ret = mongo.findAndCursor("subwayAndJobSimple","crawlerTime","2014-12-09");
		
		//TreeSet<String> tree = new TreeSet<String>();
		
		String sqlcate="insert into JobCategory(JobCategoryCode,JobCategoryName) values(";
		for(int i=0;i<7;i++)
		{
			String temp="";
			if(i==0)
			{
				temp+=sqlcate+i+",\"公司类型\")";
			}else if(i==1)
			{
				temp+=sqlcate+i+",\"公司规模\")";
			}else if(i==2)
			{
				temp+=sqlcate+i+",\"教育背景\")";
			}else if(i==3)
			{
				temp+=sqlcate+i+",\"工作类型\")";
			}else if(i==4)
			{
				temp+=sqlcate+i+",\"薪酬\")";
			}else if(i==5)
			{
				temp+=sqlcate+i+",\"最近时间\")";
			}else if(i==6)
			{
				temp+=sqlcate+i+",\"工作经历\")";
			}
				mysql.sqlInsert(temp);
		}
		int jobCode=0;
		MysqlSelect cn2= mysql.sqlSelect("select max(JobCode) from JobInfoAndCount");
		try{
		if(cn2.resultSet.next())
		{
			jobCode=cn2.resultSet.getInt(1);
		}
		}catch(Exception e)
		{}
		HashSet<String> tree=new HashSet<String>(); 
		while (ret.hasNext()) {
			try {
				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				LinkedList<String> sqlBath=new LinkedList<String>();
				if (bdbObj != null) {
					//System.out.println(bdbObj.toString());
					int[] code=new int[7];
					//System.out.println(bdbObj.getString("_id"));
					//BasicDBObject mongoIdObj=(BasicDBObject)bdbObj.get("_id");
					String mongoId=bdbObj.getString("_id");
					String nearbyKm = bdbObj.getString("nearbyKm");
					String subWayLineCode = bdbObj.getString("subWayLineCode");
					String subwayLine = bdbObj.getString("subwayLine");
					String subwayStation = bdbObj.getString("subwayStation");
					int workCategory=bdbObj.getInt("workCategory");
					String crawlerDate=bdbObj.getString("crawlerTime");
					
					if(tree.contains(subwayLine+"_"+subwayStation+"_"+workCategory+"_"+crawlerDate))
					{
						log.error("数据重复");
						continue;
					}else{
						tree.add(subwayLine+"_"+subwayStation+"_"+workCategory+"_"+crawlerDate);
					}
					//String crawlerTime = bdbObj.getString("crawlerTime");
					String[] strList=new String[7];
					String companyProperty = bdbObj.getString("companyProperty");
					strList[0]=companyProperty;
					String companyScale = bdbObj.getString("companyScale");
					strList[1]=companyScale;
					String educationBackground = bdbObj.getString("educationBackground");
					strList[2]=educationBackground;
					String jobCategory = bdbObj.getString("jobCategory");
					strList[3]=jobCategory;
					String monthlyPay = bdbObj.getString("monthlyPay");
					strList[4]=monthlyPay;
					String releaseDate = bdbObj.getString("releaseDate");
					strList[5]=releaseDate;
					String workExperience= bdbObj.getString("workExperience");
					strList[6]=workExperience;
					
					for(int l=0;l<strList.length;l++)
					{
						jobCode++;
					JSONArray  js= JSONArray.fromObject(strList[l]);
					Object[] obj=js.toArray();
					String sql="insert into JobInfoAndCount(JobCode,JobCategoryCode,CategoryName,CategoryCount,CategoryValue) values("+jobCode+","+(l+1);
					boolean flag=false;
					for(int i=0;i<obj.length;i++)
					{
						if(obj[i]==null||obj.length==0)
						{
							continue;
						}
						flag=true;
						JSONObject jsonObj= JSONObject.fromObject(obj[i]);
						JobsSonBean son= (JobsSonBean)JSONObject.toBean(jsonObj,JobsSonBean.class);
						String sqlTemp=sql+getStringAndCom(son.getName())+","+son.getCount()+",\"";
						String valll=son.getName().replaceAll("以上","").replaceAll("及", "").replaceAll("以下", "").replaceAll("年", "");
						sqlTemp+=valll.substring(0,valll.indexOf("-")<0?valll.length():valll.indexOf("-")).replaceAll("-", "")+"\")";
						//System.out.println(sqlTemp);
						sqlBath.add(sqlTemp);
					}
					if(flag)
					{
						code[l]=jobCode;
					}
					}
					String sqlMain="insert into SubwayAndJob(mongoId,SubwayLine,SubwayStation,NearbyKm,"
							+ "SubWayLineCode,WorkExperience,ReleaseDate,EducationBackground,"
							+ "CompanyProperty,MonthlyPay,JobCategory,CompanyScale,WorkExperienceCategory,InsertDate) values(";
					sqlMain+=getString(mongoId)+getStringAndCom(subwayLine)+getStringAndCom(subwayStation)+getIntCom(nearbyKm)+getStringAndCom(subWayLineCode);
					sqlMain+=getIntCom(code[6])+getIntCom(code[5])+getIntCom(code[2])+getIntCom(code[0]);
					sqlMain+=getIntCom(code[4])+getIntCom(code[3])+getIntCom(code[1])+getIntCom(workCategory)+getStringAndCom(crawlerDate)+")";
					sqlBath.add(sqlMain);
					System.out.println(sqlMain);
					mysql.sqlInsertBatch(sqlBath);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public String getString(String str) {
		if(str==null)
		{
			return "\"\"";
		}
		return "\"" + str.replaceAll("\"", "").trim() + "\"";
	}

	public String getStringAndCom(String str) {
		if(str==null)
		{
			return ",\"\"";
		}
		return ",\"" + str.replaceAll("\"", "").trim() + "\"";
	}
	public String getIntCom(String str)
	{
		return ","+str;
	}
	
	public String getIntCom(int str)
	{
		return ","+str;
	}
	public String getIntCom(long str)
	{
		return ","+str;
	}
	
	public String getDoubleCom(Double str)
	{
		return ","+str;
	}
	
	/**
	 * 插入poi数据
	 * @param mongo
	 * @param mysql
	 * 查询 name 为 name的所有内容
	 */
	public void intoPoi(MongoDb mongo, MysqlConnection mysql,String name2) {
		DBCursor  ret = mongo.findAndCursor("poi","keyword",name2);
		//TreeSet<String> tree = new TreeSet<String>();
		String sql="insert into Poi(Distance,Address,Name,Location_Lng,Location_Lat,Keyword,"
				+ "Baidu_name,Baidu_Lng,Baidu_Lat,Tencent_name,Tencent_Lng,Tencent_Lat) values(";
		while (ret.hasNext()) {
			try {
				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				if (bdbObj != null) {
					Double distance = bdbObj.getDouble("distance");
					String address = bdbObj.getString("address");
					String keyword = bdbObj.getString("keyword");
					String name=bdbObj.getString("name");
					BasicDBObject location=(BasicDBObject) bdbObj.get("location");
					Double location_lng=location.getDouble("lng");
					Double location_lat=location.getDouble("lat");
					//System.out.println(location_lng);
					BasicDBObject sourcePoi=(BasicDBObject) bdbObj.get("sourcePOI");
					BasicDBObject baidu=(BasicDBObject) sourcePoi.get("baidu");
					Double baidu_lng=baidu.getDouble("lng");
					Double baidu_lat=baidu.getDouble("lat");
					String baidu_name=sourcePoi.getString("name");
					BasicDBObject tencent=(BasicDBObject) sourcePoi.get("tencent");
					String tencent_name=tencent.getString("name");
					Double tencent_lng=tencent.getDouble("lng");
					Double tencent_lat=tencent.getDouble("lat");
					String sql2=sql+distance+getStringAndCom(address)+getStringAndCom(keyword)+
							getDoubleCom(location_lng)+getDoubleCom(location_lat)+getStringAndCom(name)+
							getStringAndCom(baidu_name)+getDoubleCom(baidu_lng)+getDoubleCom(baidu_lat)+
							getStringAndCom(tencent_name)+getDoubleCom(tencent_lng)+getDoubleCom(tencent_lat)+")";
					System.out.println(sql2);
					mysql.sqlInsert(sql2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 插入公交poi数据
	 * @param mongo
	 * @param mysql
	 * 查询 name 为 name的所有内容
	 */
	public void intoBusPoi(MongoDb mongo, MysqlConnection mysql,String name2) {
		DBCursor  ret = mongo.findAndCursor("poi","keyword",name2);
		//TreeSet<String> tree = new TreeSet<String>();
		String sql="insert into MallBusStationPoi(Distance,Address,Name,Location_Lng,Location_Lat,Keyword,"
				+ "Baidu_name,Baidu_Lng,Baidu_Lat,Tencent_name,Tencent_Lng,Tencent_Lat) values(";
		while (ret.hasNext()) {
			try {
				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				if (bdbObj != null) {
					Double distance = bdbObj.getDouble("distance");
					String address = bdbObj.getString("address");
					String keyword = bdbObj.getString("keyword");
					String name=bdbObj.getString("name");
					BasicDBObject location=(BasicDBObject) bdbObj.get("location");
					Double location_lng=location.getDouble("lng");
					Double location_lat=location.getDouble("lat");
					//System.out.println(location_lng);
					BasicDBObject sourcePoi=(BasicDBObject) bdbObj.get("sourcePOI");
					BasicDBObject baidu=(BasicDBObject) sourcePoi.get("baidu");
					Double baidu_lng=baidu.getDouble("lng");
					Double baidu_lat=baidu.getDouble("lat");
					String baidu_name=sourcePoi.getString("name");
					BasicDBObject tencent=(BasicDBObject) sourcePoi.get("tencent");
					String tencent_name=tencent.getString("name");
					Double tencent_lng=tencent.getDouble("lng");
					Double tencent_lat=tencent.getDouble("lat");
					String sql2=sql+distance+getStringAndCom(address)+getStringAndCom(keyword)+
							getDoubleCom(location_lng)+getDoubleCom(location_lat)+getStringAndCom(name)+
							getStringAndCom(baidu_name)+getDoubleCom(baidu_lng)+getDoubleCom(baidu_lat)+
							getStringAndCom(tencent_name)+getDoubleCom(tencent_lng)+getDoubleCom(tencent_lat)+")";
					System.out.println(sql2);
					mysql.sqlInsert(sql2);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 搜房信息
	 * @param mongo
	 * @param mysql
	 */
	public void intoFang(MongoDb mongo, MysqlConnection mysql) {
		DBCursor ret = mongo.find("fang");
		//TreeSet<String> tree = new TreeSet<String>();
		String sql="insert into FangPoi(BuildingName,Address,location_Lng,location_Lat,parkingCount,StationId) values(";
		String sql_son="insert into FangAndStation(StationId,StationDesc) values(";
		int index=0;
		while (ret.hasNext()) {
			try {
				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				
				if (bdbObj != null) {
					LinkedList<String> intoMysql=new LinkedList<String>();
					String name = bdbObj.getString("officeBuildingName");
					String address = bdbObj.getString("address");
					String busAndStation = bdbObj.getString("busAndStation");
					String parkingCount=bdbObj.getString("parkingCount");
					BasicDBObject location=(BasicDBObject)bdbObj.get("location");
				//	System.out.println(name+"\t"+address+"\t"+busAndStation+"\t"+location);				
					JSONArray  js= JSONArray.fromObject(busAndStation);
					Object[] obj=js.toArray();
					index++;
					Double location_lng=Double.parseDouble(location.getString("lng"));
					Double location_lat=Double.parseDouble(location.getString("lat"));
					String sql2=sql+getString(name)+getStringAndCom(address)+getDoubleCom(location_lng)+getDoubleCom(location_lat)+
							getStringAndCom(parkingCount)+getIntCom(index)+")";
					System.out.println(sql2);
					intoMysql.add(sql2);
					for(Object s:obj)
					{
						String[] split=((String)s).split("[,，]");
						for(String st:split)
						{
						String sql_son2=sql_son+index+getStringAndCom(st)+")";
						intoMysql.add(sql_son2);
						}
					//	System.out.println(sql_son2);
					}
					mysql.sqlInsertBatch(intoMysql);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * 51 公司信息
	 * @param mongo
	 * @param mysql
	 */
	public void into51Company(MongoDb mongo, MysqlConnection mysql) {
		DBCursor ret = mongo.find("company51job");
		//TreeSet<String> tree = new TreeSet<String>();
		String sqlCompany="insert into Company51JobSource2(CompanyCode,CompanyName,Address,CompanyUrl,"
				+ "CompanyCategory,CompanyMemberNum,FansCount,IndustryCategory,Location_Lng,Location_Lat) values(";
		String sqlJob="insert into Company51JobJobInfo(JobCode,monthlyply,positionFunction,"
				+ "positionTag,positionUrl,positonName,trunkDate,workAddress,yearsOfWorking) values(";
		String sqlCompanyAndJob="insert into Company51JobComInfo(CompanyCode,JobCode) values(";
		String sqlJobDesc="insert into Company51JobPosition(JobCode,PulishDate,MemberCount) values(";
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("jobInfoList",Company51PositionSonBean.class);
		int index=0;

		while (ret.hasNext()) {
			index++;
			System.out.println("执行公司数:"+index);
			try {
				LinkedList<String> intoMysql=new LinkedList<String>();
				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				if (bdbObj != null) {
					String address = bdbObj.getString("address");
					String companyCategory = bdbObj.getString("companyCategory");
					Long companyCode = bdbObj.getLong("companyCode");
					String companyMemberNum=bdbObj.getString("companyMemberNum");
					String companyName=bdbObj.getString("companyName");
					String companyUrl=bdbObj.getString("companyUrl");
					int fansCount=bdbObj.getInt("fansCount");
					String industryCategory=bdbObj.getString("industryCategory");
					BasicDBObject location=(BasicDBObject)bdbObj.get("location");
					if(location==null)
					{
						System.out.println(companyCode+"\t不存在poi");
						continue;
					}
					Double location_lng=Double.parseDouble(location.getString("lng"));
					Double location_lat=Double.parseDouble(location.getString("lat"));
					String jobs=bdbObj.getString("jobs");
					StringBuffer strb=new StringBuffer();
					strb.append(sqlCompany).append(companyCode).append(getStringAndCom(companyName)).append(getStringAndCom(address)).append(getStringAndCom(companyUrl)).
					append(getStringAndCom(companyCategory)).append(getStringAndCom(companyMemberNum)).append(getIntCom(fansCount)).
					append(getStringAndCom(industryCategory)).append(getDoubleCom(location_lng)).append(getDoubleCom(location_lat)).append(")");
					//添加公司信息
					//System.out.println(strb.toString());
					intoMysql.add(strb.toString());
				
					List<Company51jobPosition2Bean> jo=(List<Company51jobPosition2Bean>)JsonUtil.getListFromJsonArrStr(jobs, Company51jobPosition2Bean.class,map);
					for(Company51jobPosition2Bean bean:jo)
					{
						StringBuffer strb2=new StringBuffer();
						strb2.append(sqlJob).append(bean.getJobCode()).append(getStringAndCom(bean.getMonthlyply())).append(getStringAndCom(bean.getPositionFunction())).
						append(getStringAndCom(bean.getPositionTag())).append(getStringAndCom(bean.getPositionUrl())).
						append(getStringAndCom(bean.getPositonName().substring(0,bean.getPositonName().length()>50?50:bean.getPositonName().length()))).append(",date(\""+bean.getTrunkDate()+"\")").
						append(getStringAndCom(bean.getWorkAddress())).append(getStringAndCom(bean.getYearsOfWorking())).append(")");
						//添加职位
						//System.out.println(strb2.toString());
						intoMysql.add(strb2.toString());
						StringBuffer strb3=new StringBuffer();
						strb3.append(sqlCompanyAndJob).append(companyCode).append(getIntCom(bean.getJobCode())).append(")");
						//添加公司与职位对应
						//System.out.println(strb3.toString());
						intoMysql.add(strb3.toString());
						for(Company51PositionSonBean son:bean.getJobInfoList())
						{
							StringBuffer strb4=new StringBuffer();
							strb4.append(sqlJobDesc).append(bean.getJobCode()).append(",date(\""+son.getPublishDate()+"\")").append(getStringAndCom(son.getMemberCount())).
							append(")");
							//添加职位对应的 发布信息
							//System.out.println(strb4.toString());
							intoMysql.add(strb4.toString());
						}
					}
					mysql.sqlInsertBatch(intoMysql);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	

	/**
	 * 51 公司信息
	 * @param mongo
	 * @param mysql
	 */
	public void intoDianping(MongoDb mongo, MysqlConnection mysql) {
		
		FileUtil2 file=new FileUtil2(System.getProperty("user.dir")+"/data/groupNameAndId/cat_maping.txt","utf-8",true);
		LinkedList<String> list= file.readAndClose();
		HashMap<String,String> map2=new HashMap<String,String>();
		for(String str:list)
		{
			String[] strs=str.split("\t");
			String st1="";
			String str2="";
			if(strs.length>=3)
			{
				st1+=strs[0]+":"+strs[1]+":"+strs[2];
			}
			if(strs.length>=4)
			{
				str2+=strs[3]+":";
			}else{
				str2+=":";
			}
			if(strs.length>=5)
			{
				str2+=strs[4]+":";
			}else{
				str2+=":";
			}
			if(strs.length>=6)
			{
				str2+=strs[5]+":";
			}else{
				str2+=":";
			}
			if(strs.length>=7)
			{
				str2+=strs[6];
			}else{
				
			}
			map2.put(st1,str2);
		}
		FileUtil2 file2=new FileUtil2(System.getProperty("user.dir")+"/data/groupNameAndId/city_2_page_v2.dat","utf-8",true);
		LinkedList<String> list2= file2.readAndClose();
		HashMap<Long,Cat> map3=new HashMap<Long,Cat>();
		HashMap<Long,Long> map4=new HashMap<Long,Long>();
		for(String str:list2)
		{
			Pattern p_1 = Pattern.compile("cat_list\" : ([\\s\\S]*?), \"views");
			Matcher m_1 = p_1.matcher(str);
			Cat cat=null;
			long close=0;
			if(m_1.find())
			{
				cat=(Cat)JsonUtil.getDtoFromJsonObjStr(m_1.group(1), Cat.class);
			}
			if(cat==null)
			{
				continue;
			}
			 p_1 = Pattern.compile("closed\" : ([^,]*?),");
			 m_1 = p_1.matcher(str);
			 if(m_1.find())
			 {
				// System.out.println(m_1.group(1));
				 if(m_1.group(1).equals("false"))
					{
						close=0;
					}else{
						close=1;
					}
			 }
			 long shopId=0;
			 p_1 = Pattern.compile("shopId\" : ([^,]*?),");
			 m_1 = p_1.matcher(str);
			 if(m_1.find())
			 {
				 shopId=Long.parseLong(m_1.group(1));
			 }
			 map3.put(shopId,cat);
			 map4.put(shopId,close);
			
		}
		MongoDb mongo2=new MongoDb("192.168.1.11",27017,"demo");
		DBCursor ret = mongo.find("city_2");
		//TreeSet<String> tree = new TreeSet<String>();
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("jobInfoList",Company51PositionSonBean.class);
		int index=0;
		LinkedList<String> intoMysql=new LinkedList<String>();
		while (ret.hasNext()) {



			try {

				BasicDBObject bdbObj = (BasicDBObject) ret.next();
				System.out.println("执行点评数:"+index);
				index++;
//				if(index<92000)
//				{
//					continue;
//				}
				if (bdbObj != null) {
					if(intoMysql.size()>=3000)
					{
						mysql.sqlInsertBatchPoll(intoMysql);
					}
					bdbObj.removeField("_id");
					String tempString =bdbObj.toString();
					tempString =tempString.substring(tempString.indexOf("\"shopInfo\" : ")+13,tempString.indexOf("\"parkInfo\"")-2);
					DianpingBean mallInfo=(DianpingBean) JsonUtil.getDtoFromJsonObjStr(tempString, DianpingBean.class);
					StringBuffer input=null;
					
					Long shopId=mallInfo.getShopId();
					BasicDBObject db=new BasicDBObject();
					db.put("shopId",shopId);
				//	DBCursor ret2=mongo2.find("city_2_page_v2",db);
					Long close=map4.get(shopId);
					Cat cat=map3.get(shopId);
					if(cat==null)
					{
						System.out.println("---------shop为空:"+shopId);
						continue;
					}
//					if(ret2.hasNext())
//					{
//						BasicDBObject bdbObj2 = (BasicDBObject) ret2.next();
//						if (bdbObj != null) {
//							//System.out.println("cat_list:"+bdbObj2.getString("cat_list"));
//							cat=(Cat)JsonUtil.getDtoFromJsonObjStr(bdbObj2.getString("cat_list"), Cat.class);
//							Boolean clo=bdbObj2.getBoolean("closed");
//							if(clo==false)
//							{
//								close=0;
//							}else{
//								close=1;
//							}
//						}
//					}
					input=new StringBuffer("insert into BrandInfo");
					input.append("(similarShops,priceInfo,searchName,altName,"
							+ "phoneNo2,firstUserNickName,"
							+ "businessHours,firstUserFace,crossRoad,"
							+ "shopTags,publicTransit,"
							+ "nearByTags,nearbyShops,addUserName,defaultPic,"
							+ "address,primaryTag,userCanUpdate,webSite,searchKeyWord,shopPowerTitle,"
							+ "lastUserName,shopName,writeUp,branchName,phoneNo,lastDate,addDate,lastIp,cat1,"
							+ "cat2,tag1,tag2,district,businessArea,");
					//System.out.println(input.toString().split(",").length);
					List<String> strList=new LinkedList<String>();
					strList.add(mallInfo.getSimilarShops());
					strList.add(mallInfo.getPriceInfo());
					strList.add(mallInfo.getSearchName());
					strList.add(mallInfo.getAltName());
					strList.add(mallInfo.getPhoneNo2());
					strList.add(mallInfo.getFirstUserNickName());
					strList.add(mallInfo.getBusinessHours());
					strList.add(mallInfo.getFirstUserFace());
					strList.add(mallInfo.getCrossRoad());
					strList.add(mallInfo.getShopTags());
					strList.add(mallInfo.getPublicTransit());
					strList.add(mallInfo.getNearByTags());
					strList.add(mallInfo.getNearbyShops());
					strList.add(mallInfo.getAddUserName());
					strList.add(mallInfo.getDefaultPic());
					strList.add(mallInfo.getAddress());
					strList.add(mallInfo.getPrimaryTag());
					strList.add(mallInfo.getUserCanUpdate());
					strList.add(mallInfo.getWebSite());
					strList.add(mallInfo.getSearchKeyWord());
					strList.add(mallInfo.getShopPowerTitle());
					strList.add(mallInfo.getLastUserName());
					strList.add(mallInfo.getShopName());
					strList.add(mallInfo.getWriteUp());
					strList.add(mallInfo.getBranchName());
					strList.add(mallInfo.getPhoneNo());
					strList.add(mallInfo.getLastDate());
					strList.add(mallInfo.getAddDate());
					strList.add(mallInfo.getLastIp());
					
					String st=cat.getCat_1()+":"+cat.getCat_2()+":"+cat.getCat_3();
					String result=map2.get(st);
					if(result==null)
					{
						System.out.println("result:"+st);
						continue;
					}
					String[] stL=result.split("[:]");
					strList.add(stL[0]);
					if(stL.length>=2)
					{
						strList.add(stL[1]);
					}else{
						strList.add("");
					}
					if(stL.length>=3)
					{
						strList.add(stL[2]);
					}else{
						strList.add("");
					}
					if(stL.length>=4)
					{
						strList.add(stL[3]);
					}else{
						strList.add("");
					}
					strList.add(cat.getDistrict());
					strList.add(cat.getBusiness_area());
					//System.out.println(strList.size());
					input.append("ShopType,shopGroupId,avgPrice"
							+ ",shopPower,promoId"+
					",firstReviewId,voteTotal,clientType,picTotal,priceLevel,cityId,addUser,"+
					"lastUser,shopId,oldChainId,closed,");
					//System.out.println(input.toString().split(",").length);
					List<Long> longList=new LinkedList<Long>();
					longList.add(mallInfo.getShopType());
					longList.add(mallInfo.getShopGroupId());
					longList.add(mallInfo.getAvgPrice());
					longList.add(mallInfo.getShopPower());
					longList.add(mallInfo.getPromoId());
					longList.add(mallInfo.getFirstReviewId());
					longList.add(mallInfo.getVoteTotal());
					longList.add(mallInfo.getClientType());
					longList.add(mallInfo.getPicTotal());
					longList.add(mallInfo.getPriceLevel());
					longList.add(mallInfo.getCityId());
					longList.add(mallInfo.getAddUser());
					longList.add(mallInfo.getLastUser());
					longList.add(mallInfo.getShopId());
					longList.add(mallInfo.getOldChainId());
					
					longList.add(close);
					//System.out.println(longList.size());
					input.append("glng,glat");
					input.append(") values(");
					//System.out.println(input.toString().split(",").length);
					List<Double> doubleList=new LinkedList<Double>();
					doubleList.add(mallInfo.getGlng());
					doubleList.add(mallInfo.getGlat());
					input.append(getStringList(strList)).append(",");
					input.append(getLongList(longList)).append(",");
					input.append(getDoubleList(doubleList)).append(")");	
					//System.out.println(input);
				intoMysql.add(input.toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(intoMysql.size()>0)
		{
			mysql.sqlInsertBatchPoll(intoMysql);
		}
	}

	public String getLongList(List<Long> val)
	{
		StringBuffer str=new StringBuffer();
		for(Long vl:val)
		{

			if(str.length()==0)
			{
				if(vl==null)
				{
					str.append("null");
				}else
				str.append(Long.toString(vl));
			}else{
				if(vl==null)
				{
					str.append(",null");
				}else{
				str.append(",").append(Long.toString(vl));
				}
			}
		}
		return str.toString();
	}
	
	public String getDoubleList(List<Double> val)
	{
		StringBuffer str=new StringBuffer();
		for(Double vl:val)
		{
			if(str.length()==0)
			{
				if(vl==null)
				{
					str.append("null");
				}else
				str.append(Double.toString(vl));
			}else{
				if(vl==null)
				{
					str.append(",null");
				}else
				str.append(",").append(Double.toString(vl));
			}
		}
		return str.toString();
	}
	
	public String getStringList(List<String> val)
	{
		StringBuffer str=new StringBuffer();
		for(String vl:val)
		{
			if(str.length()==0)
			{
				
				if(vl==null)
				{
					str.append("null");
				}else
				str.append("\"").append(vl.substring(0,vl.length()>300?300:vl.length()).replaceAll("\\\\","|").replace("\"", "\\\"")).append("\"");
			}else{
				if(vl==null)
				{
					str.append(",null");
				}else
				str.append(",").append("\"").append(vl.substring(0,vl.length()>300?300:vl.length()).replaceAll("\\\\","|").replace("\"", "\\\"")).append("\"");
			}
		}
		return str.toString();
	}
	public String getBooleanList(List<Boolean> val)
	{
		StringBuffer str=new StringBuffer();
		for(Boolean vl:val)
		{
			if(str.length()==0)
			{
				if(vl==null)
				{
					str.append("null");
				}else{
					if(vl)
					{
						str.append("1");
					}else{
						str.append("0");
					}
				}
			}else{
				if(vl==null)
				{
					str.append(",null");
				}else{
					if(vl)
					{
						str.append(",1");
					}else{
						str.append(",0");
					}
				}
			}
		}
		return str.toString();
	}
	
	
	public static void main(String[] args) {
		// mallAndShop
//		MongoToMysql exec = new MongoToMysql("busAndStation");
//		exec.run();
//		MongoToMysql exec2 = new MongoToMysql("parking");
//		exec2.run();
//		MongoToMysql exec3 = new MongoToMysql("subwayAndBus");
//		exec3.run();
//		MongoToMysql exec4 = new MongoToMysql("subwayAndJob");
//		exec4.run();
//		MongoToMysql exec5 = new MongoToMysql("subwayAndJob");
//		exec5.run();
//		MongoToMysql exec6 = new MongoToMysql("test");
//		exec6.run();
//		MongoToMysql exec7 = new MongoToMysql("poi","地铁站");
//		exec7.setMongoDatabase("mydb");
//		exec7.setMongoIp("192.168.1.37");
//		exec7.setMongoPort(27017);
//		exec7.run();
//		MongoToMysql exec8 = new MongoToMysql("fang");
//		exec8.run();
		
//		MongoToMysql exec7 = new MongoToMysql("busPoi","公交站");
//		exec7.setMongoDatabase("mydb");
//		exec7.setMongoIp("192.168.1.37");
//		exec7.setMongoPort(27017);
//		exec7.run();
//		String str="sfad\\sdfasd\\sdf";
//		System.out.println(str.replaceAll("\\\\","|"));
		MongoToMysql exec4 = new MongoToMysql("dianping");
		exec4.run();

	}

}
