package com.etl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;

import test.objbean.CategoryItemBean;
import test.objbean.CategoryMallBean;
import test.objbean.ItemRecommandBean;
import test.objbean.MallRecommandBean;
import test.objbean.SonBean;
import test.objbean.SonSimBean;
import test.otherBean.GroupNameAndIdBean;
import test.otherBean.HotBrandBean;

import com.etl.bean.DianpingBean;
import com.etl.bean.MallInfoBean;
import com.mysql.jdbc.PreparedStatement;
import com.util.DateFormat;
import com.util.JsonUtil;
import com.zj.bean.CompanyBean3;
import com.zj.bean.CompanyBean4;

/**
 * 文件读写操作类
 * 
 * @author Administrator
 *
 */
public class FileToMysql {
	Logger log = Logger.getLogger(FileToMysql.class);
	/**
	 * 文件指针
	 */
	private File file = null;
	/**
	 * 文件类型
	 */
	private String code = "utf-8";
	
	/**
	 * 数据库表名字
	 */
	private String name="";
	
	private long maxIndex=0;
	/**
	 * 文件
	 * @param filename
	 * @param code
	 */
	public FileToMysql(String filename,String code,String name)
	{
		System.out.println(filename+"\t"+code);
		//文件名需要重制定
		file=new File(filename);
		if(!file.exists())
		{
			log.info("文件不存在:"+filename);
			System.exit(1);
		}
		this.code=code;
		this.name=name;
		
	}
	
	public void run()
	{
		BufferedReader reader = null;
		try{
		 
		 InputStreamReader read = new InputStreamReader(new FileInputStream(file),this.code);  
         reader = new BufferedReader(read);
         String tempString = null;
         
         MysqlConnection mysql = new MysqlConnection(
 				"jdbc:mysql://192.168.1.4:3306/zjMysql", "root",
 				"root");
         System.out.println("开始读取文件");
         // 一次读入一行，直到读入null为文件结束
         if(name.equals("MallAndShop"))
         {
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 inputMallAndShop(mysql,tempString);
	        	 
	         }
         }else if(name.equals("MallAndCommentCount"))
         {
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 inputMallAndCommentCount(mysql,tempString);
	        	 
	         }
         }else if(name.equals("MallInfo"))
         {
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 System.out.println(tempString);
	        	 inputMallInfo(mysql,tempString);
	        	 
	         }
         }else if(name.equals("MallDianpingInfo"))
        {
        	 MysqlSelect select=mysql.sqlSelect("select max(ID) from DianpingRel");
        	 while(select.resultSet.next())
        	 {
        		 maxIndex=select.resultSet.getLong(1);
        	 }
        	 while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 inputMallDianpingInfo(mysql,tempString,true);
	         }
        }else if(name.equals("BrandDianpingInfo"))
        {
       	 MysqlSelect select=mysql.sqlSelect("select max(ID) from DianpingRel");
       	 while(select.resultSet.next())
       	 {
       		 maxIndex=select.resultSet.getLong(1);
       	 }
       	 while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 inputMallDianpingInfo(mysql,tempString,false);
	         }
       }
         else if(name.equals("BrandDianpingInfo"))
        {
        	 MysqlSelect select=mysql.sqlSelect("select max(ID) from DianpingRel");
           	 while(select.resultSet.next())
           	 {
           		 maxIndex=select.resultSet.getLong(1);
           	 }
       	 while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 inputBrandDianpingInfo(mysql,tempString);
	         }
       }
         else if(name.equals("ShopInfo"))
         {
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 inputShopInfo(mysql,tempString);
	        	 
	         }
         }else if(name.equals("MallAndShopRel"))
         {
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 inputMallAndShopRel(mysql,tempString);
	        	 
	         }
         }else if(name.equals("CompanySource"))
         {
        	  while ((tempString = reader.readLine()) != null)
 	         {
 	        	 if(tempString.length()==0)
 	        	 {
 	        		 continue;
 	        	 }
 	        	 inputCompanySource(mysql,tempString);
 	         }
         }else if(name.equals("Company51Job"))
         {
        	 while ((tempString = reader.readLine()) != null)
 	         {
 	        	 if(tempString.length()==0)
 	        	 {
 	        		 continue;
 	        	 }
 	        	inputCompany51Job(mysql,tempString);
 	         } 
         }else if(name.equals("CompanyZhaopinJob"))
         {
        	 while ((tempString = reader.readLine()) != null)
 	         {
 	        	 if(tempString.length()==0)
 	        	 {
 	        		 continue;
 	        	 }
 	        	inputCompanyZhaopinJob(mysql,tempString);
 	         } 
         }else if(name.equals("HotBrand"))
         {
        	 while ((tempString = reader.readLine()) != null)
 	         {
 	        	 if(tempString.length()==0)
 	        	 {
 	        		 continue;
 	        	 }
 	        	inputHotBrand(mysql,tempString);
 	         } 
         }
         else if(name.equals("GroupIdAndName"))
         {//点评上的组信息入库
        	 int i=0;
        	 PreparedStatement pst = mysql.setPreparedStatement("insert into ShopGroupIdAndName(ShopGroupID,ShopGroupName) values(?,?)");  
        	 
        	 while ((tempString = reader.readLine()) != null)
 	         {
        		 i++;
        		 if(i%10000==0)
        		 {
        			 System.out.println(i);
        			 mysql.runPreparedStatement(pst);
        			 pst.clearBatch();
        			// pst = mysql.setPreparedStatement("insert into ShopGroupIdAndName(ShopGroupID,ShopGroupName) values(?,?)");  
        		 }
 	        	 if(tempString.length()==0)
 	        	 {
 	        		 continue;
 	        	 }
 	        	inputGroupIdName(tempString,pst);
 	        	//System.out.println(str);
 	         }
        	 mysql.runPreparedStatement(pst);
			 pst.clearBatch();
         }else if(name.equals("RecommandInfo"))
         {//推荐数据入库
        	 HashMap<String,Object> map=new HashMap<String,Object>();
        	 map.put("categoryItem",CategoryItemBean.class);
        	 map.put("similaryMall",SonSimBean.class);
        	 map.put("recommandShop",SonBean.class);
        	 while ((tempString = reader.readLine()) != null)
 	         {
 	        	 if(tempString.length()==0)
 	        	 {
 	        		 continue;
 	        	 }
 	        	inputRecommandResult(mysql,tempString,map,true);
 	         } 
         }else if(name.equals("RecommandInfoBrand"))
         {//推荐数据入库
        	 HashMap<String,Object> map=new HashMap<String,Object>();
        	 map.put("categoryMall",CategoryMallBean.class);
        	 map.put("similaryBrand",SonSimBean.class);
        	 map.put("recommandMall",SonBean.class);
        	 while ((tempString = reader.readLine()) != null)
 	         {
 	        	 if(tempString.length()==0)
 	        	 {
 	        		 continue;
 	        	 }
 	        	inputRecommandResult(mysql,tempString,map,false);
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
	}
	/**
	 * mallAndShop文件
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputMallAndShop(MysqlConnection mysql,String tempString) throws SQLException
	{
	 	 String[] strTemp=tempString.split("\t");
	 	// System.out.println(strTemp.length);
    	 String temp="";
    	 boolean flag=false;
    	 int i=0;
    	 for(String st:strTemp)
    	 {
    		 i++;
    		 if(i==7)
    		 {
    			 break;
    		 }
    		 if(i==6)
    		 {
    			 temp+=","+0+"";
    			 continue;
    		 }
    		 if(!flag)
    		 {
    			 temp+=""+st.trim()+"";
    		 }else{
    			 if(i==3||i==5)
    			 {
    				 temp+=","+st.trim()+"";
    			 }else{
    				 temp+=",\""+st.trim()+"\"";
    			 }
    		 }
    		 flag=true;
    	 }
//    	 System.out.println(temp);
    	 String input="insert into MallAndShop(MallIDWeb,ShopName,Category,MallName,MallID,ShopID) values("+temp+")";
    	 System.out.println(input);
    	  mysql.sqlInsert(input);
	}
	
	/**
	 * mallAndCommentCount文件
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputMallAndCommentCount(MysqlConnection mysql,String tempString) throws SQLException
	{
		//System.out.println(tempString);
	 	 String[] strTemp=tempString.split("\t");
    	 String temp="";
    	 boolean flag=false;
    	 int i=0;
    	 for(String st:strTemp)
    	 {
    		 i++;
    		 if(!flag)
    		 {
    			 temp+="\""+st.trim()+"\"";
    		 }else{
    			 if(i==3)
    			 {
    				 temp+=","+st.trim()+"";
    			 }else{
    				 temp+=",\""+st.trim()+"\"";
    			 }
    		 }
    		 flag=true;
    	 }
//    	 System.out.println(temp);
    	 String input="insert into MallAndCommentCount(MallIDWeb,MallName,CommentCount,CommentStart) values("+temp+")";
    	 System.out.println(input);
    	  mysql.sqlInsert(input);
	}
	
	
	/**
	 * mall 对应的 poi数据
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputMallInfo(MysqlConnection mysql,String tempString) throws SQLException
	{
		//System.out.println(tempString);
		MallInfoBean mallInfo=(MallInfoBean) JsonUtil.getDtoFromJsonObjStr(tempString, MallInfoBean.class);
//    	 System.out.println(temp);
    	 String input="insert into MallInfo(MallName,MallID,Baidu_lng,Baidu_lat) values(\""+mallInfo.getName()+"\","+Long.parseLong(mallInfo.getDianpingid())+
    			 ","+mallInfo.getBaidu().getLng()+","+mallInfo.getBaidu().getLat()+")";
    	 System.out.println(input);
    	  mysql.sqlInsert(input);
	}
	/**
	 * mall 对应的点拼数据
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputMallDianpingInfo(MysqlConnection mysql,String tempString,boolean flag) throws SQLException
	{
		tempString =tempString.substring(tempString.indexOf("\"shopInfo\" : ")+13,tempString.indexOf("\"parkInfo\"")-2);
		DianpingBean mallInfo=(DianpingBean) JsonUtil.getDtoFromJsonObjStr(tempString, DianpingBean.class);
		StringBuffer input=null;
		if(flag)
			input=new StringBuffer("insert into MallInfoDianping");
		else
			input=new StringBuffer("insert into BrandInfoDianping");
		input.append("(SimilarShops,priceInfo,searchName,altName,"
				+ "phoneNo2,firstUserNickName,"
				+ "businessHours,firstUserFace,crossRoad,"
				+ "shopTags,publicTransit,"
				+ "nearByTags,nearbyShops,addUserName,defaultPic,"
				+ "address,primaryTag,userCanUpdate,webSite,searchKeyWord,shopPowerTitle,"
				+ "lastUserName,shopName,writeUp,branchName,phoneNo,lastDate,addDate,lastIp,");
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
		input.append("ShopType,wishTotal,shopGroupId,avgPrice,score4,score1"
				+ ",score3,weeklyHits,shopPower,district,promoId,minUserMana,todayHits"+
		",score,firstReviewId,voteTotal,clientType,picTotal,priceLevel,power,cityId,score2,hits,addUser,monthlyHits,"+
		"lastUser,popularity,prevWeeklyHits,shopId,oldChainId,cat_list,shops,");
		List<Long> longList=new LinkedList<Long>();
		longList.add(mallInfo.getShopType());
		longList.add(mallInfo.getWishTotal());
		longList.add(mallInfo.getShopGroupId());
		longList.add(mallInfo.getAvgPrice());
		longList.add(mallInfo.getScore4());
		longList.add(mallInfo.getScore1());
		longList.add(mallInfo.getScore3());
		longList.add(mallInfo.getWeeklyHits());
		longList.add(mallInfo.getShopPower());
		longList.add(mallInfo.getDistrict());
		longList.add(mallInfo.getPromoId());
		longList.add(mallInfo.getMinUserMana());
		longList.add(mallInfo.getTodayHits());
		longList.add(mallInfo.getScore());
		longList.add(mallInfo.getFirstReviewId());
		longList.add(mallInfo.getVoteTotal());
		longList.add(mallInfo.getClientType());
		longList.add(mallInfo.getPicTotal());
		longList.add(mallInfo.getPriceLevel());
		longList.add(mallInfo.getPower());
		longList.add(mallInfo.getCityId());
		longList.add(mallInfo.getScore2());
		longList.add(mallInfo.getHits());
		longList.add(mallInfo.getAddUser());
		longList.add(mallInfo.getMonthlyHits());

		longList.add(mallInfo.getLastUser());
		longList.add(mallInfo.getPopularity());
		longList.add(mallInfo.getPrevWeeklyHits());
		longList.add(mallInfo.getShopId());
		longList.add(mallInfo.getOldChainId());


		longList.add(++maxIndex);
		longList.add(++maxIndex);
		input.append("canSendSms,groupFlag,hasStaticMap,");
		List<Boolean> booleanList=new LinkedList<Boolean>();
		booleanList.add(mallInfo.getCanSendSms());
		booleanList.add(mallInfo.getGroupFlag());
		booleanList.add(mallInfo.getHasStaticMap());
		input.append("glng,glat");
		input.append(") values(");
		List<Double> doubleList=new LinkedList<Double>();
		doubleList.add(mallInfo.getGlng());
		doubleList.add(mallInfo.getGlat());
		input.append(getStringList(strList)).append(",");
		input.append(getLongList(longList)).append(",");
		input.append(getBooleanList(booleanList)).append(",");
		input.append(getDoubleList(doubleList)).append(")");
		System.out.println(input.toString());
		//System.out.println(mallInfo.getShopId());
		LinkedList<String> in=new LinkedList<String>();
		in.add(input.toString());
		int index=0;
		
		String sql2="insert into DianpingRel(relcode,sortIndex,category) values(";
		if(mallInfo.getCat_list()!=null)
		for(String st:mallInfo.getCat_list())
		{
			//添加 分类信息
			index++;
			String sql22=sql2+(maxIndex-1)+","+index+","+"\""+st+"\")";
			in.add(sql22);
		}
		index=0;
		String sql3="insert into DianpingRel(relcode,sortIndex,dianpingCode) values(";
		if(mallInfo.getShops()!=null)
		for(String st:mallInfo.getShops())
		{
			//对应的分店code
			index++;
			String sql33=sql3+(maxIndex)+","+index+","+Long.parseLong(st)+")";
			in.add(sql33);
		}
    	mysql.sqlInsertBatch(in);
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
				str.append("\"").append(vl.substring(0,vl.length()>300?300:vl.length()).replace("\"", "\\\"")).append("\"");
			}else{
				if(vl==null)
				{
					str.append(",null");
				}else
				str.append(",").append("\"").append(vl.substring(0,vl.length()>300?300:vl.length()).replace("\"", "\\\"")).append("\"");
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
	
	public String getBooleanCom(Boolean str)
	{
		if(str==null)
		{
			return ",";
		}else{
			if(str)
			{
				return ",1";
			}else{
				return ",0";
			}
		}
	}
	
	
	/**
	 * brand 点评数据
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputBrandDianpingInfo(MysqlConnection mysql,String tempString) throws SQLException
	{
		//System.out.println(tempString);
		DianpingBean mallInfo=(DianpingBean) JsonUtil.getDtoFromJsonObjStr(tempString, DianpingBean.class);
//    	 System.out.println(temp);
//    	 String input="insert into MallInfo(MallName,MallID,Baidu_lng,Baidu_lat) values(\""+mallInfo.getName()+"\","+Long.parseLong(mallInfo.getDianpingid())+
//    			 ","+mallInfo.getBaidu().getLng()+","+mallInfo.getBaidu().getLat()+")";
    	 //System.out.println(input);
    	 // mysql.sqlInsert(input);
	}
	
	/**
	 * mallAndCommentCount文件
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputShopInfo(MysqlConnection mysql,String tempString) throws SQLException
	{
		//System.out.println(tempString);
	 	 String[] strTemp=tempString.split("\t");
    	 String temp="";
    	 boolean flag=false;
    	 for(String st:strTemp)
    	 {
    		 if(!flag)
    		 {
    			 temp+=""+st.trim()+"";
    		 }else{
    			 temp+=",\""+st.trim()+"\"";
    		 }
    		 flag=true;
    	 }
//    	 System.out.println(temp);
    	 String input="insert into ShopInfo(ShopID,ShopName,CategoryCode) values("+temp+")";
    	 System.out.println(input);
    	 mysql.sqlInsert(input);
	}
	
	/**
	 * mallAndCommentCount文件
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputMallAndShopRel(MysqlConnection mysql,String tempString) throws SQLException
	{
		//System.out.println(tempString);
	 	 String[] strTemp=tempString.split("\t");
    	 String temp="";
    	 boolean flag=false;
    	 int i=0;
    	 for(String st:strTemp)
    	 {
    		 i++;
    		 if(!flag)
    		 {
    			 temp+=""+st.trim()+"";
    		 }else{
    			 if(i==3)
    			 {
    				 temp+=",\""+st.trim()+"\"";
    			 }
    			 else{
    				 temp+=","+st.trim()+""; 
    			 }
    		 }
    		 flag=true;
    	 }
//    	 System.out.println(temp);
    	 String input="insert into MallAndShopRel(MallID,ShopID,CategoryCode) values("+temp+")";
    	 System.out.println(input);
    	 mysql.sqlInsert(input);
	}
	
	
	/**
	 * 企业文件
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputCompanySource(MysqlConnection mysql,String tempString) throws SQLException
	{
		//System.out.println(tempString);
	 	// String[] strTemp=tempString.split("\t");
//    	 System.out.println(temp);
    	 String input="insert into CompanySource(CompanyName,Keyword) values(\""+tempString+"\",\""+getFilter(tempString)+"\")";
    	 System.out.println(input);
    	 mysql.sqlInsert(input);
	}
	/**
	 * 51job上公司信息入库
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputCompany51Job(MysqlConnection mysql,String tempString) throws SQLException
	{
		//System.out.println(tempString);
	 	// String[] strTemp=tempString.split("\t");
//    	 System.out.println(temp);
	 	CompanyBean3 bean=(CompanyBean3)JsonUtil.getDtoFromJsonObjStr(tempString, CompanyBean3.class);
    	 String input="insert into Company51JobSource(CompanyName,CompanyUrl,SubwayStation,Keyword,CompanyCategory"
    	 		+ ",CompanyMemberNum,FansCount,IndustryCategory) values(\""+bean.getCompanyName()+
    			 "\",\""+bean.getCompanyUrl()+"\",\""+bean.getSubWayStation()+"\",\""+getFilter(bean.getCompanyName())+
    			 "\",\""+bean.getCompanyCategory()+"\",\""+bean.getCompanyMemberNum()+"\","+
    			 bean.getFansCount()+",\""+bean.getIndustryCategory()+"\")";
    	 System.out.println(input);
    	 mysql.sqlInsert(input);
	}
	
	/**
	 * 将最终推荐结果写入mysql中
	 * @param mysql
	 * @param tempString
	 * @param map
	 * @throws SQLException
	 */
	public void inputRecommandResult(MysqlConnection mysql,String tempString,HashMap<String,Object> map,boolean flag) throws SQLException
	{
		if(flag)
		{
		MallRecommandBean bean=(MallRecommandBean)JsonUtil.getDtoFromJsonObjStr(tempString, MallRecommandBean.class, map);
		String sql1="insert into RecommandInfo(RecommandType,RecommandCode,RecommandName,RecommandId,SimilaryId) values(";
		sql1+="0,";
		maxIndex++;
		sql1+=bean.getMallId()+",\""+bean.getMallName()+"\","+maxIndex+",";
		String sql2="insert into RecommandCategoryInfo(RecommandId,CategoryCode,CategoryName,RecommandItemId) values(";
		sql2+=maxIndex+",";
		
		LinkedList<String> result1=inputRecommandCategory(mysql,bean.getCategoryItem(),sql2);
		String sql3="insert into RecommandSimDesc(RecommandSimId,ReId,ReScore,RecommandName) values(";
		sql3+=++maxIndex+",";
		sql1+=maxIndex+")";
		LinkedList<String> result2=inputRecommandSim(mysql,bean.getSimilaryMall(),sql3);
		System.out.println(sql1);
		result1.add(sql1);
		result1.addAll(result2);
		mysql.sqlInsertBatch(result1);	
		}else{
			ItemRecommandBean bean=(ItemRecommandBean)JsonUtil.getDtoFromJsonObjStr(tempString, ItemRecommandBean.class, map);
			String sql1="insert into RecommandInfo(RecommandType,RecommandCode,RecommandName,RecommandId,SimilaryId) values(";
			sql1+="1,";
			maxIndex++;
			sql1+=bean.getBrandId()+",\""+bean.getBrandName()+"\","+maxIndex+",";
			String sql2="insert into RecommandCategoryInfo(RecommandId,CategoryCode,CategoryName,RecommandItemId) values(";
			sql2+=maxIndex+",";
			
			LinkedList<String> result1=inputRecommandCategoryB(mysql,bean.getCategoryMall(),sql2);
			String sql3="insert into RecommandSimDesc(RecommandSimId,ReId,ReScore,RecommandName) values(";
			sql3+=++maxIndex+",";
			sql1+=maxIndex+")";
			LinkedList<String> result2=inputRecommandSim(mysql,bean.getSimilaryBrand(),sql3);
			System.out.println(sql1);
			result1.add(sql1);
			result1.addAll(result2);
			mysql.sqlInsertBatch(result1);	
		}
	}
	
	
	
	/**
	 * 获取推荐分类信息
	 * brand
	 * @param mysql
	 * @param bean
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public LinkedList<String> inputRecommandCategory(MysqlConnection mysql,LinkedList<CategoryItemBean> bean,String sql) throws SQLException
	{
		String sql2="insert into RecommandItemDesc(RecommandItemId,ReId,ReScore,RecommandName,ReCount) values(";
		LinkedList<String> result=new LinkedList<String>();
		for(CategoryItemBean cate:bean)
		{
//			cate.getCategory();
//			cate.getCategoryName();
//			cate.getRecommandShop();
			String str=sql+cate.getCategory()+",\""+cate.getCategoryName()+"\","+(++maxIndex)+")";
			result.add(str);
			String sql3=sql2+maxIndex+",";
			LinkedList<String> result2=inputRecommandItem(mysql,cate.getRecommandShop(),sql3);
			result.addAll(result2);
		}
		return result;
	}
	/**
	 * 获取推荐分类信息
	 * mall
	 * @param mysql
	 * @param bean
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public LinkedList<String> inputRecommandCategoryB(MysqlConnection mysql,LinkedList<CategoryMallBean> bean,String sql) throws SQLException
	{
		String sql2="insert into RecommandItemDesc(RecommandItemId,ReId,ReScore,RecommandName,ReCount) values(";
		LinkedList<String> result=new LinkedList<String>();
		for(CategoryMallBean cate:bean)
		{
//			cate.getCategory();
//			cate.getCategoryName();
//			cate.getRecommandShop();
			String str=sql+cate.getCategory()+",\""+cate.getCategoryName()+"\","+(++maxIndex)+")";
			result.add(str);
			String sql3=sql2+maxIndex+",";
			LinkedList<String> result2=inputRecommandItem(mysql,cate.getRecommandMall(),sql3);
			result.addAll(result2);
		}
		return result;
	}
	/**
	 * 获取推荐物品信息
	 * @param mysql
	 * @param sonBean
	 * @param sql
	 * @return
	 */
	public LinkedList<String> inputRecommandItem(MysqlConnection mysql,LinkedList<SonBean> sonBean,String sql)
	{
		LinkedList<String> result=new LinkedList<String>();
		for(SonBean son:sonBean)
		{
			result.add(sql+son.getId()+","+(int)(son.getValue()*100)+",\""+son.getName()+"\","+son.getCount()+")");
		}
		return result;
	}
	/**
	 * 获取相似度信息
	 * @param mysql
	 * @param bean
	 * @param sql
	 * @return
	 * @throws SQLException
	 */
	public LinkedList<String> inputRecommandSim(MysqlConnection mysql,LinkedList<SonSimBean> bean,String sql) throws SQLException
	{
		LinkedList<String> result=new LinkedList<String>();
		for(SonSimBean sonBean:bean)
		{
			result.add(sql+sonBean.getSimId()+","+(int)(sonBean.getValue()*100)+",\""+sonBean.getName()+"\")");
		}
		return result;
	}
	/**
	 * 过滤括号内部
	 * @param temp
	 * @return
	 */
	public String getStringClearn(String temp)
	{
		String address="";
		while(true)
	   	 {
	       	 if(temp.contains("("))
	       	 {
	       		 String temp2="";
	       		 temp2=temp.substring(0,temp.indexOf("("));
	       		 if(temp.contains(")"))
	       		 {
	       			 temp2+=temp.substring(temp.indexOf(")")+1);
	      			 }
	       		 temp=temp2;
	       		 //System.out.println("temp:"+temp);
	       		
	       	 }else if(temp.contains("（"))
	       	 {
	       		 String temp2="";
	       		 temp2=temp.substring(0,temp.indexOf("（"));
	       		 if(temp.contains("）"))
	       		 {
	       			 temp2+=temp.substring(temp.indexOf("）")+1);
	      			 }
	       		 temp=temp2;
	       		 //System.out.println("temp:"+temp);
	       		
	       	 }
	       	 {
	       		 address=temp;
	       		// tree.add(temp);
	       		 break;
	       	 }
	   	 }
		return address;
	}
	
	public String getFilter(String str)
	{
		str=getStringClearn(str);
		str=getStringFilterCity(str);
		str=getStringFilterCompany(str);
		return str;
	}
	public String getStringFilterCity(String str)
	{
		if(str.contains("中国电子")||str.contains("中国医药") || str.contains("中国化工")
				||str.contains("中国装饰") || str.contains("中国建筑")||str.contains("中国汽车")
				|| str.contains("中国联通") ||str.contains("中国电信"))
		{
			
		}else{
			str=str.replaceAll("中国", "");
		}
		if(str.contains("成都市第五建筑"))
		{
			
		}else{
			str=str.replaceAll("成都[市]*", "");
		}
		if(str.contains("北京环球"))
		{}else{
			str=str.replaceAll("北京[市]*", "");
		}
		return str.replaceAll("加拿大","")
				.replaceAll("美国", "").replaceAll("日本","").replaceAll("荷兰", "").replaceAll("法国","")
				.replaceAll("英国", "").replaceAll("德国","").replaceAll("韩国", "").replaceAll("瑞士","")
				.replace("上海[市]*","").replace("广州[省]*", "").replace("深圳[市]*", "").replaceAll("成都","")
				.replaceAll("合肥[市]*","").replaceAll("安徽[市]*", "").replaceAll("四川[省]*", "")
				
				.replaceAll("江苏[省]*", "").replaceAll("大兴", "").replaceAll("重庆[市]*", "");
				
				
	}
	public String getStringFilterCompany(String str)
	{
		if(str.contains("中国网络通信"))
		{
			
		}else{
			str=str.replaceAll("通信","").replaceAll("网络", "");
		}
//		if(str.contains("国际航空"))
//		{}else{
//			
//		}
		if(str.contains("技术投资贸易"))
		{
			
		}else{
			str=str.replaceAll("贸易", "").replaceAll("投资", "");
		}
		if(str.contains("成都市第五建筑"))
		{
			
		}else{
			str=str.replaceAll("第[十一二三四五六七八九]*+","").replaceAll("[十一二三四五六七八九]*+局", "");
		}
		if(str.contains("中国电子")||str.contains("中国医药") || str.contains("中国化工")
				||str.contains("中国装饰") || str.contains("中国建筑")||str.contains("中国汽车"))
		{
			
		}
		return str.replaceAll("有限","").replaceAll("总公司", "").replace("分公司","").replaceAll("服务公司", "").replaceAll("公司","").replaceAll("责任", "").replaceAll("股份", "")
				.replaceAll("开发","").replaceAll("农业科技", "").replace("实业", "")
				.replaceAll("信息","").replaceAll("技术", "").replaceAll("石化发展","").replaceAll("发展","").replaceAll("工程", "")
				.replaceAll("经销","").replaceAll("物资","").replaceAll("总承包部","").replaceAll("广告", "").replaceAll("开发","")
				.replaceAll("管理中心", "").replaceAll("中心", "").replaceAll("展示", "").replaceAll("项目部", "")
				.replaceAll("国际科技","").replaceAll("科技","").replaceAll("国际贸易","").replaceAll("代表处", "").replaceAll("贸易", "").replaceAll("经济合作", "")
				.replaceAll("商贸", "").replaceAll("财富·", "").replaceAll("第三处", "").replaceAll("安装", "")
				.replaceAll("办公室", "").replaceAll("工作室", "").replaceAll("总部", "").replaceAll("十六局集团第一", "").replaceAll("集团", "")
				.replaceAll("商标事务", "").replaceAll("资源","").replaceAll("第一处", "").replaceAll("西区", "")
				
				.replaceAll("贸易", "").replaceAll("委员会","").replaceAll("管理处", "").replaceAll("国际智能", "")
				.replaceAll("建路桥管理部", "").replaceAll("总代理", "").replaceAll("地区", "")
				.replaceAll("仓库", "").replaceAll("事业部", "").replaceAll("迈克罗·", "").replaceAll("办事处", "").replaceAll("管理所", "")
				.replaceAll("修配厂", "").replaceAll("输油气","");
		//.replaceAll("医药", "").replaceAll("制造", "")
				
	}
	
	/**
	 * 智联job上公司信息入库
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputCompanyZhaopinJob(MysqlConnection mysql,String tempString) throws SQLException
	{
		//System.out.println(tempString);
	 	// String[] strTemp=tempString.split("\t");
//    	 System.out.println(temp);
	 	CompanyBean4 bean=(CompanyBean4)JsonUtil.getDtoFromJsonObjStr(tempString, CompanyBean4.class);
    	 String input="insert into CompanyZhaopinJobSource(CompanyName,CompanyUrl,Keyword,CompanyCategory,CompanyMemberNum,IndustryCategory) values(\""+bean.getCompanyName()+
    			 "\",\""+bean.getCompanyUrl()+"\",\""+getFilter(bean.getCompanyName())+
    			  "\",\""+bean.getCompanyCategory()+"\",\""+bean.getCompanyMemberNum()+"\",\""+
    			 bean.getIndustryCategory()+"\")";
    	 System.out.println(input);
    	 mysql.sqlInsert(input);
	}
	
	/**
	 * 热门品牌的入库
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputHotBrand(MysqlConnection mysql,String tempString) throws SQLException
	{
		HotBrandBean bean=(HotBrandBean)JsonUtil.getDtoFromJsonObjStr(tempString, HotBrandBean.class);
		String input="insert into HotBrand(Shoptype,ShopGroupName,AverageHits,ShopGroupId,InsertDate) values("
		+bean.getShopType()+",\""+bean.getShopGroupName()+"\","+bean.getAverageHits()+","+
				bean.getShopGroupId()+","+"date(\""+DateFormat.parse(new Date())+"\"))";
		System.out.println(input);
		mysql.sqlInsert(input);
	}
	/**
	 * 	
	 * @param mysql
	 * @param tempString
	 * @throws SQLException
	 */
	public void inputGroupIdName(String tempString,PreparedStatement pst) throws SQLException
	{
		GroupNameAndIdBean bean=(GroupNameAndIdBean)JsonUtil.getDtoFromJsonObjStr(tempString, GroupNameAndIdBean.class);
		pst.setLong(1,bean.get_id());
		pst.setString(2, bean.getValue());
		pst.addBatch();
		//mysql.sqlInsert(input);
	}
	
	public static void main(String[] args) {
		//mallAndShop
	//	String path="C:\\Users\\Administrator\\Desktop\\"+"MallAndShop.txt";
//		FileToMysql file=new FileToMysql(path,"utf-8","MallAndShop");
//		file.run();
//		String path2="C:\\Users\\Administrator\\Desktop\\"+"MallAndCommentCount.txt";
//		FileToMysql file2=new FileToMysql(path2,"utf-8","MallAndCommentCount");
//		file2.run();
		//String path3=System.getProperty("user.dir")+"/data/mallgeo_baidu_128Mall.txt";
				//"C:\\Users\\Administrator\\Desktop\\"+"MallInfo.txt";
		//FileToMysql file3=new FileToMysql(path3,"utf-8","MallInfo");
		//file3.run();
//		String path4="C:\\Users\\Administrator\\Desktop\\"+"ShopInfo.txt";
//		FileToMysql file4=new FileToMysql(path4,"utf-8","ShopInfo");
//		file4.run();
//		String path5="C:\\Users\\Administrator\\Desktop\\"+"MallAndShopRel.txt";
//		FileToMysql file5=new FileToMysql(path5,"utf-8","MallAndShopRel");
//		file5.run();
//		String path=System.getProperty("user.dir")+"/data/企业.txt";
//		FileToMysql file=new FileToMysql(path,"utf-8","CompanySource");
//		file.run();
//		String path="f:\\zj"+"/companyEndDesc-2014-11-28.txt";
//		FileToMysql file=new FileToMysql(path,"utf-8","Company51Job");
//		file.run();
//		String path2="F:\\zj"+"/companySearchEndDesc-2014-11-28.txt";
//		FileToMysql file2=new FileToMysql(path2,"utf-8","CompanyZhaopinJob");
//		file2.run();
		
//		String path3=System.getProperty("user.dir")+"/data/recommend-raw-data-141216/128Mall.json";
		//"C:\\Users\\Administrator\\Desktop\\"+"MallInfo.txt";
//		FileToMysql file3=new FileToMysql(path3,"utf-8","MallDianpingInfo");
//		file3.run();
//		 path3=System.getProperty("user.dir")+"/data/recommend-raw-data-141216/brand-now.txt";
//		 file3=new FileToMysql(path3,"utf-8","BrandDianpingInfo");
//			file3.run();
	
//		String f=System.getProperty("user.dir")+"/data/hotBrand/";
//		File dir=new File(f);
//		if(dir.isDirectory())
//		{
//			File[] files=dir.listFiles();
//			for(File file:files)
//			{
//				 FileToMysql file3=new FileToMysql(file.getAbsolutePath(),"utf-8","HotBrand");
//				 file3.run();
//			}
//		}else{
//			System.out.println("输入不是目录");
//			System.exit(0);
//		}
		
		
//		String path2=System.getProperty("user.dir")+"/data/groupNameAndId/shopBrand.dat";
//		FileToMysql file2=new FileToMysql(path2,"utf-8","GroupIdAndName");
//		file2.run();
		
		//推荐mall推荐品牌入mysql
		String path2="D:/eclipse/workspaceML/ZJCrawler/data/mallCos10Test.txt";
		FileToMysql file2=new FileToMysql(path2,"utf-8","RecommandInfo");
		file2.run();
		
		
		//推荐 品牌推荐mall入mysql
		path2="D:/eclipse/workspaceML/ZJCrawler/data/mallCos10Itemcontent.txt";
		file2=new FileToMysql(path2,"utf-8","RecommandInfoBrand");
		file2.run();
		
	}

}
