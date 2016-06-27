package com.analysis;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import com.db.MysqlConnection;
import com.db.MysqlSelect;

/**
 * 薪酬分析模块
 * @author Administrator
 *
 */
public class MoneyAnalysis {

	MysqlConnection mysqlConnection=null;
	/**
	 * mysql连接
	 * @param ip
	 * @param name
	 * @param password
	 */
	public MoneyAnalysis(String ip,String name,String password)
	{
		mysqlConnection= new MysqlConnection(
				"jdbc:mysql://192.168.85.11:3306/zjMysql", "root", "root");
	}
	/**
	 * 计算 按照不同业态 (shop数量)对应的地铁站的权重值
	 * @param weight
	 * @throws SQLException
	 */
	public void run(double weight) throws SQLException
	{
		String sql="select a.Address,a.Keyword,a.Baidu_name,CategoryName,WorkSum,CategoryName,a.Location_Lng,a.Location_Lat,a.Baidu_lng,a.Baidu_Lat "+
"from PoiTemp2 as a "+
"left JOIN "+
"( "+
"select replace(replace(`x1`.`SubwayLine`,'8通线','八通线'),'地铁','')  "+
" AS `SubwayLine`,`x1`.`SubwayStation` AS `SubwayStation`,x2.CategoryName, "+
"`x2`.`CategoryCount` AS `WorkSum` "+
"from ((SubwayAndJob as x1 "+
"left join `JobInfoAndCount` `x2`  "+
"on((`x1`.`MonthlyPay` = `x2`.`JobCode`)) "+
"and x2.CategoryName<>'面议' "+
")  "+
"left join `JobCategory` `x3` on((`x2`.`JobCategoryCode` = `x3`.`JobCategoryCode`)))  "+
"where (x1.NearbyKm = 1)  "+
"order by `x1`.`SubwayLine`,`x1`.`SubwayStation` "+
")as b "+
"on a.Keyword=b.SubwayStation "+
"and  replace(replace(a.Address,'海淀区知春里','地铁10号线'),'机场线','机场快线') like CONCAT('%',b.SubwayLine,'%') "+
"where a.Distance<=1000 "+
"order by Baidu_name,Keyword "+
"and b.WorkSum is not null";
		
		String sql2="select replace(replace(`x1`.`SubwayLine`,'8通线','八通线'),'地铁','') "+
 "AS `SubwayLine`,`x1`.`SubwayStation` AS `SubwayStation`,x2.CategoryName, "+
"`x2`.`CategoryCount` AS `WorkSum` "+
"from ((SubwayAndJob as x1 "+
"left join `JobInfoAndCount` `x2`  "+
"on((`x1`.`MonthlyPay` = `x2`.`JobCode`)) "+
"and x2.CategoryName<>'面议')  "+
"left join `JobCategory` `x3` on((`x2`.`JobCategoryCode` = `x3`.`JobCategoryCode`)))  "+
"where (x1.NearbyKm = 1)  "+
"group by SubwayStation,categoryName "+
"order by SubwayStation,SubwayLine";
		
		String sql3="select a.Address,a.Keyword,a.Baidu_name,CategoryName,WorkSum,a.Location_Lng,a.Location_Lat,a.Baidu_lng,a.Baidu_Lat "+ 
"from PoiTemp2 as a  "+
"left JOIN(select replace(replace(`x1`.`SubwayLine`,'8通线','八通线'),'地铁','') "+
" AS `SubwayLine`,`x1`.`SubwayStation` AS `SubwayStation`,x2.CategoryName, "+
"sum(`x2`.`CategoryCount`) AS `WorkSum`  "+
"from ((`SubwayAndJob` `x1`  "+
"left join `JobInfoAndCount` `x2`  "+
"on((`x1`.`MonthlyPay` = `x2`.`JobCode`)))  "+
"left join `JobCategory` `x3` on((`x2`.`JobCategoryCode` = `x3`.`JobCategoryCode`)))  "+
"where (`x1`.`NearbyKm` = 1)  "+
"group by `x1`.`SubwayLine`,`x1`.`SubwayStation`,`x1`.`WorkExperience`  "+
"order by `x1`.`SubwayLine`,`x1`.`SubwayStation`,`x1`.`WorkExperience` "+
") as b "+
"on a.Keyword=b.SubwayStation "+
"and  replace(replace(a.Address,'海淀区知春里','地铁10号线'),'机场线','机场快线') like CONCAT('%',b.SubwayLine,'%') "+
"where a.Distance<=1000 "+
"and b.WorkSum is not null "+
"group by Address,keyword,Baidu_name "+
"order by keyword";
		//System.out.println(sql3);
		
		String sql4="select  c1.Address,c1.Keyword,c1.Baidu_name,c1.CategoryName,c1.WorkSum, "+
"c1.Location_Lng,c1.Location_Lat,c1.Baidu_lng,c1.Baidu_Lat,c2.CategoryCode,count(1) as shopCount "+
"from( "+
"select a.Address,a.Keyword,a.Baidu_name,CategoryName,WorkSum,a.Location_Lng,a.Location_Lat,a.Baidu_lng,a.Baidu_Lat "+
"from PoiTemp2 as a  "+
"left JOIN(select replace(replace(`x1`.`SubwayLine`,'8通线','八通线'),'地铁','') "+
" AS `SubwayLine`,`x1`.`SubwayStation` AS `SubwayStation`,x2.CategoryName, "+
"sum(`x2`.`CategoryCount`) AS `WorkSum`  "+
"from ((`SubwayAndJob` `x1`  "+
"left join `JobInfoAndCount` `x2`  "+
"on((`x1`.`MonthlyPay` = `x2`.`JobCode`)))  "+
"left join `JobCategory` `x3` on((`x2`.`JobCategoryCode` = `x3`.`JobCategoryCode`)))  "+
"where (`x1`.`NearbyKm` = 1)  "+
"group by `x1`.`SubwayLine`,`x1`.`SubwayStation`,`x1`.`WorkExperience`  "+
"order by `x1`.`SubwayLine`,`x1`.`SubwayStation`,`x1`.`WorkExperience` "+
") as b "+
"on a.Keyword=b.SubwayStation "+
"and  replace(replace(a.Address,'海淀区知春里','地铁10号线'),'机场线','机场快线') like CONCAT('%',b.SubwayLine,'%') "+
"where a.Distance<=1000 "+
"and b.WorkSum is not null "+
"group by Address,keyword,Baidu_name "+
") as c1 "+
"left join MallAndShop as c2 "+
"on c1.Baidu_name=c2.MallName "+
"group by Address,keyword,Baidu_name,CategoryCode "+
"order by keyword,Baidu_name";
		//System.exit(1);
		MysqlSelect mysqlSelect=mysqlConnection.sqlSelect(sql4);
		ResultSet resultSet=mysqlSelect.resultSet;
		String address_="";
		String keyword_="";
		String baidu_name_="";
		double location_lng_=0D;
		double location_lat_=0D;
		double baidu_lng_=0D;
		double baidu_lat_=0D;
		String categoryCode_="";
		int shopCount_=0;
		LinkedList<Poi> mo=null;
		Poi station=null;
		Poi mall=null;
		LinkedList<String> intoMysql=new LinkedList<String>();
		while(resultSet.next())
		{
			String address=resultSet.getString(1);
			String keyword=resultSet.getString(2);
			String baidu_name=resultSet.getString(3);
			String categoryName=resultSet.getString(4);
			int workSum=resultSet.getInt(5);
			double location_lng=resultSet.getDouble(6);
			double location_lat=resultSet.getDouble(7);
			double baidu_lng=resultSet.getDouble(8);
			double baidu_lat=resultSet.getDouble(9);
			String categoryCode=resultSet.getString(10);
			int shopCount=resultSet.getInt(11);
			if(!keyword_.equals(keyword))//||!baidu_name_.equals(baidu_name)||!categoryCode_.equals(categoryCode)
			{//按照地铁站和业态划分
				if(mo==null)
				{
					//不分析
				}else{
					//分析 mall分布比率
					LinkedList<Poi> mallPoi=analisisy(station,mo,weight);
					addList(intoMysql,print(mallPoi,keyword_));
					addList(intoMysql,printShop(mo,keyword_));
					
				}
				mo=new LinkedList<Poi>();
				station=new Poi(baidu_name,location_lng,location_lat,null);
				address_=address;
				keyword_=keyword;
				baidu_name_=baidu_name;
				location_lng_=location_lng;
				location_lat_=location_lat;
				baidu_lng_=baidu_lng;
				baidu_lat_=baidu_lat;
				categoryCode_=categoryCode;
				shopCount_=shopCount;
			}
			mall=new Poi(baidu_name,baidu_lng,baidu_lat,categoryCode);
			mall.shopCount=shopCount;
			mo.add(mall);
			
//			int index=categoryName.indexOf("-");
//			Money money=null;
//			if(index<0)
//			{
//				index=categoryName.indexOf("及");
//				if(index<0)
//				{
//					index=categoryName.indexOf("以");
//				}
//			}
//			if(index<=0)
//			{
//				money=new Money(Double.parseDouble(categoryName),workSum,categoryName);
//			}else{
//				money=new Money(Double.parseDouble(categoryName.substring(0,index)),workSum,categoryName);
//			}
//			mo.add(money);
		}
		if(mo!=null)
		{
			LinkedList<Poi> mallPoi=analisisy(station,mo,weight);
			addList(intoMysql,print(mallPoi,keyword_));
			addList(intoMysql,printShop(mo,keyword_));
			
		}
		mysqlConnection.sqlInsertBatch(intoMysql);
		System.out.println("结束");
	}
	/**
	 * 通过聚类算法计算相似性的地铁站
	 * @throws SQLException 
	 */
	public void run2() throws SQLException
	{
		
		String sql="select replace(replace(`x1`.`SubwayLine`,'8通线','八通线'),'地铁','') "+
 "AS `SubwayLine`,`x1`.`SubwayStation` AS `SubwayStation`,x2.CategoryName, "+
"`x2`.`CategoryCount` AS `WorkSum` "+
"from ((SubwayAndJob as x1 "+
"left join `JobInfoAndCount` `x2` "+ 
"on((`x1`.`MonthlyPay` = `x2`.`JobCode`)) "+
"and x2.CategoryName<>'面议')  "+
"left join `JobCategory` `x3` on((`x2`.`JobCategoryCode` = `x3`.`JobCategoryCode`))) "+ 
"where (x1.NearbyKm = 1)  "+
"group by SubwayStation,categoryName "+
"order by SubwayStation,SubwayLine";
		
		MysqlSelect mysqlSelect=mysqlConnection.sqlSelect(sql);
		ResultSet resultSet=mysqlSelect.resultSet;
		ArrayList<Station> dataP=new ArrayList<Station>();
		HashMap<String,Integer> map=new HashMap<String,Integer>();
		int index=-1;
		String subwayStationL="";
		Station station=null;
		while(resultSet.next())
		{
			String subwayLine=resultSet.getString(1);
			String	subwayStation=resultSet.getString(2);
			String categoryName=resultSet.getString(3);
			int workSum=resultSet.getInt(4);
			if(map.containsKey(categoryName))
			{}else{
				index++;
				map.put(categoryName,index);
			}
			if(subwayStationL.equals(subwayStation))
			{
				station.categoryName.add(categoryName);
				station.workSum.add(workSum);
			}else{
				subwayStationL=subwayStation;
				if(station!=null)
				{
					dataP.add(station);
					station=new Station(subwayLine,subwayStation,categoryName,workSum);
				}else{
					station=new Station(subwayLine,subwayStation,categoryName,workSum);
				}
			}
		}
		if(station!=null)
		{
			dataP.add(station);
		}
		double[][] data=new double[dataP.size()][map.size()];
		index=0;
		for(Station sta:dataP)
		{
			for(int j=0;j<sta.categoryName.size();j++)
			{
				data[index][map.get(sta.categoryName.get(j))]=sta.workSum.get(j);
			}
			index++;
		}
		Kmeans kmeans=new Kmeans(data,0.07,0.15);
		kmeans.run();
		kmeans.print();
		ArrayList<double[]> center=kmeans.clusterCenter;
		int[] cluster=kmeans.cluster;
		index=0;
		LinkedList<String> intoMysql=new LinkedList<String>();
		String sql2="insert into StationMoneyCluster(SubwayLine,Station,Cluster) values(";
		for(Station sta:dataP)
		{
			String sql3=sql2+"\""+sta.subwayLine+"\",\""+sta.subwayStation+"\","+cluster[index]+")";
			intoMysql.add(sql3);
			//System.out.println(sql3);
			index++;
		}
		mysqlConnection.sqlInsertBatch(intoMysql);
		System.out.println("结束");
	}
	
	public class Station
	{
		private String subwayLine="";
		private String subwayStation="";
		private ArrayList<String> categoryName=new ArrayList<String>();
		private ArrayList<Integer> workSum=new ArrayList<Integer>();
		public Station(String subwayLine,String subwayStation,String categoryName,int workSum)
		{
			this.subwayLine=subwayLine;
			this.subwayStation=subwayStation;
			this.categoryName.add(categoryName);
			this.workSum.add(workSum);
		}
	}
	
	/**
	 * 分析程序
	 * 计算每个商铺 所占有的成分
	 * @return 返回为mall的权重信息
	 * 并且会修改 业态信息
	 */
	public LinkedList<Poi> analisisy(Poi station,LinkedList<Poi> mallAndShop,double weight)
	{
		if(mallAndShop.size()==0)
		{
			return mallAndShop;
		}
		LinkedList<Poi> mall=new LinkedList<Poi>();
		
		
		//首先不按照业态进行统计
		int mallCount=0;
		String name="";
		double lng=0D;
		double lat=0D;
		for(Poi poi:mallAndShop)
		{
			if(name.equals(""))
			{
				name=poi.name;
				lng=poi.lng;
				lat=poi.lat;
			}else if(!name.equals(poi.name))
			{
				mall.add(new Poi(name,lng,lat,null));
				name=poi.name;
				lng=poi.lng;
				lat=poi.lat;
			}else{
				//以店铺为基础
				mallCount+=poi.lp;
			}
		}
		if(!name.equals(""))
		{
			mall.add(new Poi(name,lng,lat,null));
		}
		
		double[] count=new double[4];
		for(Poi poi:mall)
		{
			//计算 坐标
			//分布到4个象限中
			poi.lp=station.getSpoi(poi);
			count[poi.lp]++;
		}
		double con=0;
		for(double c:count)
		{
			con+=c;
		}
		HashMap<String,Double> m=new HashMap<String,Double>();
		for(Poi poi:mall)
		{
			poi.weight=(1+(count[poi.lp])/con)/2;
			m.put(poi.name,poi.weight);
		}
		
		//分析 shop信息
		double[] countShop=new double[4];
		HashMap<String,ArrayList<Double>> m2=new HashMap<String,ArrayList<Double>>();
		for(Poi poi:mallAndShop)
		{
			//计算 坐标
			//分布到4个象限中
			poi.lp=station.getSpoi(poi);
			//System.out.println(poi.lp);
			ArrayList<Double> poi_l4=m2.get(poi.category);
			if(poi_l4==null)
			{
				ArrayList<Double> temp=new ArrayList<Double>();
				temp.add(0D);temp.add(0D);temp.add(0D);temp.add(0D);
				temp.set(poi.lp,poi.shopCount*1.0);
				m2.put(poi.category,temp);
			}else{
				poi_l4.set(poi.lp,poi_l4.get(poi.lp)+poi.shopCount);
			}
		}
		double conShop=0d;
		for(double c:countShop)
		{
			conShop+=c;
		}
		for(Poi poi:mallAndShop)
		{
			Double dl=m.get(poi.name);
			ArrayList<Double> shopDl=m2.get(poi.category);
			//System.out.println(poi.category+"\t"+dl+"\t"+poi.shopCount+"\t"+shopDl.get(poi.lp));
			poi.weight=(weight)*(1+(poi.shopCount)/shopDl.get(poi.lp))/2+(1-weight)*(dl);
		}
		
		
		return mall;
	}
	public LinkedList<String> print(LinkedList<Poi> mall,String station)
	{
		LinkedList<String> str=new LinkedList<String>();
		String sql="insert into MallAndShopMoney_S(Station,CategoryCode,ShopCount,MallName,Weight) values(";
		for(Poi poi:mall)
		{
			System.out.println("station:"+station+"\t"+poi.name+"\tweight:"+poi.weight);
			String sql2=sql+"\""+station+"\","+"\"\",0,\""+poi.name+"\","+poi.weight+")";
			str.add(sql2);
		}
		return str;
	}
	public LinkedList<String> printShop(LinkedList<Poi> mall,String station)
	{
		LinkedList<String> str=new LinkedList<String>();
		String sql="insert into MallAndShopMoney_S(Station,CategoryCode,ShopCount,MallName,Weight) values(";
		for(Poi poi:mall)
		{
			System.out.println("station:"+station+"\tcategory:"+poi.category+"\t"+poi.shopCount+"\t"+poi.name+"\tweight:"+poi.weight);
			String sql2=sql+"\""+station+"\","+"\""+poi.category+"\","+poi.shopCount+",\""+poi.name+"\","+poi.weight+")";
			str.add(sql2);
		}
		return str;
	}
	
	public class Poi
	{
		private String name="";
		private double lng=0D;
		private double lat=0D;
		//象限
		private int lp=0;
		//权重
		private double weight=0D;
		/**
		 * 业态
		 */
		private String category="";
		/**
		 * 商铺数
		 */
		private int shopCount=0;
		public Poi(String name,double lng,double lat,String category)
		{
			this.name=name;
			this.lng=lng;
			this.lat=lat;
			this.category=category;
		}
		/**
		 * 返回属于的象限
		 * @param poi
		 * @return
		 */
		public int getSpoi(Poi poi)
		{
			double dlng=lng-poi.lng;
			double dlat=lat-poi.lat;
			if(dlng<0)
			{
				if(dlat<0)
				{
					return 2;
				}else{
					return 1;
				}
			}else{
				if(dlat<0)
				{
					return 3;
				}else{
					return 0;
				}
			}
		}
	}
	
	public void addList(LinkedList<String> str1,LinkedList<String> str2)
	{
		while(str2.size()>0)
		{
			str1.add(str2.pollFirst());
		}
	}
	
	public class Money
	{
		private double value=0D;
		private int count=0;
		private double saleAbility=0D;
		private String name="";
		
		public Money(double value,int count,String name)
		{
			this.value=value;
			this.count=count;
			this.name=name;
			this.saleAbility=value*count;
			
		}
		public double getValue() {
			return value;
		}
		public void setValue(double value) {
			this.value = value;
		}
		public int getCount() {
			return count;
		}
		public void setCount(int count) {
			this.count = count;
		}
		public double getSaleAbility() {
			return saleAbility;
		}
		public void setSaleAbility(double saleAbility) {
			this.saleAbility = saleAbility;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		
		
	}
	
	public static void main(String[] args) throws SQLException {
		MoneyAnalysis main=new MoneyAnalysis(null,null,null);
		//按照不同业态计算
		//	main.run(0.8);
		//计算相似性地铁站
		main.run2();
	}
}
