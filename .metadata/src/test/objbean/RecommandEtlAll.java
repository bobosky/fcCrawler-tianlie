package test.objbean;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import test.CF;

import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.etl.FileUtil2;
import com.util.JsonUtil;


/**
 * 不按照业态分类的整合方法
 * @author Administrator
 *
 */
public class RecommandEtlAll {

	
	private FileUtil2 fileUtilWrite=null;
	/**
	 * 相似度文件
	 */
	private String filePathSim=null;
	/**
	 * 推荐文件
	 */
	private String filePathRe=null;
	/**
	 * 按照业态计算的推荐
	 */
	private String filePathSource=null;
	/**
	 * 是否为 mall文件
	 */
	private boolean isMall=false;
	/**
	 * 品牌对应 分类
	 */
	private HashMap<String,String> cateToShopName=null;
	
	private HashMap<String,Integer> category=null;
	/**
	 * 文件名
	 * @param filePath
	 * @param code
	 */
	public RecommandEtlAll(String filePathRe,String filePathSim,String filePathSource,String outPutPath,boolean isMall)
	{
		this.filePathRe=filePathRe;
		this.filePathSim=filePathSim;
		this.fileUtilWrite=new FileUtil2(outPutPath,"utf-8");
		this.isMall=isMall;
		this.filePathSource=filePathSource;
	}
	
	public void run()
	{
		//获取 分类信息
		MysqlConnection mysql = new MysqlConnection(
 				"jdbc:mysql://192.168.1.4:3306/zjMysql", "root",
 				"root");
	
		String sql="select a.ShopName,c.CategoryName from  MallAndShop as a, "+
		"MallCategory as c "+
		"where a.CategoryCode=c.CategoryCode "+
		"group by ShopName";
		MysqlSelect  myse=mysql.sqlSelect(sql);
		//用于存储 品牌名对应的分类
		cateToShopName=new HashMap<String,String>();
		category=new HashMap<String,Integer>();
		try{
		while(myse.resultSet.next())
		{
			String shopName=myse.resultSet.getString(1).toLowerCase().trim();
			String categoryName=myse.resultSet.getString(2).split("\r\n")[0];
			cateToShopName.put(shopName, categoryName);
		}}catch (Exception e)
		{
			e.printStackTrace();
			System.out.println("数据库连接异常");
			System.exit(1);
		}
		
		sql="select CategoryName,CategoryCode from  MallCategory";
		myse=mysql.sqlSelect(sql);
		try{
			while(myse.resultSet.next())
			{
				String shopName=myse.resultSet.getString(1).toLowerCase().trim();
				Integer categoryInt=myse.resultSet.getInt(2);
				category.put(shopName, categoryInt);
			}}catch (Exception e)
			{
				e.printStackTrace();
				System.out.println("数据库连接异常");
				System.exit(1);
			}
			mysql.close();
		
		if(isMall)
		{
			//执行mall程序
			runMall();
		}else{
			//执行brand程序
			runBrand();
		}
	}
	
	public void runMall()
	{
		System.out.println("读取推荐文件");
		//存储名字
		HashMap<String,Integer> indexName=new HashMap<String,Integer>();
		ArrayList<MallRecommandBean> recommandList=new ArrayList<MallRecommandBean>();
		HashMap<String,Integer> indexMap=new HashMap<String,Integer>();
		BufferedReader reader = null;
		int indexName_=0;
		try{
			 InputStreamReader read = new InputStreamReader(new FileInputStream(filePathRe),"utf-8");  
	         reader = new BufferedReader(read);
	         String tempString = null;
	         // 一次读入一行，直到读入null为文件结束
	         MallRecommandBean re=null;
		     while ((tempString = reader.readLine()) != null)
	       	{
		    	 String[] str=tempString.split("\t");
		    	//则存在则添加recommandlist中
		    	//System.out.println(intName);
		    	 MallRecommandBean bean=new MallRecommandBean();
		    	 bean.setMallId(Long.parseLong(str[1]));
		    	 bean.setMallName(str[2]);
		    	 indexName.put(bean.getMallName().trim(), indexName_);
		    	 indexName_++;
				for(int i=3;i<str.length;i++)
				{
					SonBean sonBean=new SonBean();
					String[] str2=str[i].split(CF.splitReg);
					//System.out.println("test:"+str[i]);
					sonBean.setId(Long.parseLong(str2[0]));
					sonBean.setCount(Integer.parseInt(str2[3]));
					sonBean.setName(str2[1]);
					sonBean.setValue(Double.parseDouble(str2[2]));
					
					//获取当前类对应的分类
			    	String category2=cateToShopName.get(sonBean.getName().toLowerCase());
			    	if(category2==null)
			    	{
			    		System.out.println("异常:"+sonBean.getName());
			    		continue;
			    	}
			    	if(category2.equals("生活服务") || category2.equals("爱车")||category2.equals("酒店"))
			    	{
			    		continue;
			    	}
			    	Integer iIndex=indexMap.get(category2+str[2]);
			    	System.out.println(category2+"\t"+str[2]+"\t"+iIndex);
			    	if(category2==null)
			    	{
			    		System.out.println("不存在该品牌分类"+"\t:"+sonBean.getName()+":");
			    		continue;
			    	}
					if(iIndex==null)
					{
						CategoryItemBean reBean=new CategoryItemBean();
						reBean.setCategoryName(category2);
						reBean.setCategory(category.get(category2));
						//判断位置
						indexMap.put(category2+bean.getMallName(),bean.getCategoryItem().size());
						reBean.addRecommandShopLimit(sonBean,10);
						bean.addCategoryItem(reBean);
					
					}else{
						CategoryItemBean reBean=bean.getCategoryItem().get(iIndex);
						//判断位置
						reBean.addRecommandShopLimit(sonBean,10);
					}
				}
				recommandList.add(bean);
				
	        }
	     reader.close();
		}catch(Exception e)
		{
			e.printStackTrace();
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(recommandList.size());
		System.out.println("读取相似度文件");
		//读取相似度文件
		try{
			 InputStreamReader read = new InputStreamReader(new FileInputStream(filePathSim),"utf-8");  
	         reader = new BufferedReader(read);
	         String tempString = null;
	         // 一次读入一行，直到读入null为文件结束
		     while ((tempString = reader.readLine()) != null)
	       	{
		    	 String[] str=tempString.split("\t");
		    	 //判断业态+mall是否存在
		    	 Integer int1=indexName.get(str[0].split(CF.splitReg)[1].trim());
		    	if(int1==null)
		    	{
		    		System.out.println("不存在:"+str[0]+str[1]);
		    		continue;
		    	}
		    	//则存在则添加recommandlist中
		    	MallRecommandBean reBean=recommandList.get(int1);
//		    	CategoryItemBean itemBean=reBean.getCategoryItem().get(int2);
		    	//System.out.println(int1+"\t"+int2+"\t"+itemBean.getSizeSim());
				for(int i=2;i<str.length;i++)
				{
					SonSimBean sonSimBean=new SonSimBean();
					String[] str2=str[i].split(CF.splitReg);
					sonSimBean.setSimId(Long.parseLong(str2[0]));
					sonSimBean.setName(str2[1]);
					sonSimBean.setValue(Float.parseFloat(str2[2]));
					//System.out.println(int1+"\t"+int2+"\t"+str2[0]);
					if(str2.length==0)
					{
						continue;
					}
					//itemBean.addSimilaryMall(sonSimBean);
					reBean.addSimilaryMall(sonSimBean);
				}
				//System.out.println(int1+"\t"+int2+"\t"+itemBean.getSizeSim());
	        }
	     reader.close();
		}catch(Exception e)
		{
			
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		//读取按业态分类的信息 替换
		//购物 美食 丽人
		System.out.println("读取业态文件");
		try{
			 InputStreamReader read = new InputStreamReader(new FileInputStream(filePathSource),"utf-8");  
	         reader = new BufferedReader(read);
	         String tempString = null;
	         // 一次读入一行，直到读入null为文件结束
		     while ((tempString = reader.readLine()) != null)
	       	{
		    	 String[] str=tempString.split("\t");
		    	 String cate=str[2].trim();
		    	 String mallN=str[4].trim();
		    	 //判断业态+mall是否存在
		    	 if(!cate.equals("购物")&&!cate.equals("美食")&&!cate.equals("丽人"))
		    	 {
		    		 continue;
		    	 }
		    	 Integer int1=indexName.get(mallN);
		    	 Integer int2=indexMap.get(cate+mallN);
		    	 
		    	if(int1==null)
		    	{
		    		System.out.println("不存在:"+str[2]+str[4]);
		    		continue;
		    	}
		    	//则存在则添加recommandlist中
		    	MallRecommandBean reBean=recommandList.get(int1);
		    	if(int2==null)
		    	{
		    		System.out.println("不存在该业态"+str[2]+"\t"+str[4]);
		    		CategoryItemBean items=new CategoryItemBean();
		    		items.setCategoryName(cate);
		    		items.setCategory(category.get(cate));
		    		for(int i=5;i<str.length;i++)
					{
						SonBean son=new SonBean();
						String[] str2=str[i].split(CF.splitReg);
						son.setId(Long.parseLong(str2[0]));
						son.setCount(Integer.parseInt(str2[3]));
						son.setName(str2[1]);
						son.setValue(Double.parseDouble(str[2]));
						items.addRecommandShopLimit(son,10);
					}
		    		reBean.addCategoryItem(items);
		    		continue;
		    	}
		    	//判断是否替换
		    	CategoryItemBean items=new CategoryItemBean();
	    		items.setCategoryName(cate);
	    		items.setCategory(category.get(cate));
	    		for(int i=5;i<str.length;i++)
				{
					SonBean son=new SonBean();
					String[] str2=str[i].split(CF.splitReg);
					son.setId(Long.parseLong(str2[0]));
					son.setCount(Integer.parseInt(str2[3]));
					son.setName(str2[1]);
					son.setValue(Double.parseDouble(str2[2]));
					items.addRecommandShopLimit(son,10);
				}
	    		CategoryItemBean itemsource=reBean.getCategoryItem().get(int2);
	    		if(items.getSizeShop()>1)
	    		{
	    			System.out.println(mallN+"\t"+cate+"\t被替换");
	    			//替换
	    			reBean.setCategoryItem(int2,items);
	    		}else{
	    			//不替换
	    		}
	        }
	     reader.close();
		}catch(Exception e)
		{
			
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("写文件");
		
		//最终写文件
		LinkedList<String> recommandListStr=new LinkedList<String>();
		while(recommandList.size()>0)
		{
			MallRecommandBean recom=recommandList.remove(0);
			recom.sortSim();
			recommandListStr.add(JsonUtil.getJsonStr(recom));
		}
		fileUtilWrite.wirte(recommandListStr, recommandListStr.size());
		fileUtilWrite.close();
	}
	
	
	public void runBrand()
	{
		System.out.println("读取推荐文件");
		//存储分类+名
		HashMap<String,Integer> indexMap=new HashMap<String,Integer>();
		//存储名字
		HashMap<String,Integer> indexName=new HashMap<String,Integer>();
		ArrayList<ItemRecommandBean> recommandList=new ArrayList<ItemRecommandBean>();
		BufferedReader reader = null;
		int indexName_=0;
		try{
			 InputStreamReader read = new InputStreamReader(new FileInputStream(filePathRe),"utf-8");  
	         reader = new BufferedReader(read);
	         String tempString = null;
	         // 一次读入一行，直到读入null为文件结束
	         ItemRecommandBean re=null;
		     while ((tempString = reader.readLine()) != null)
	       	{
		    	 String[] str=tempString.split("\t");
		    	 //判断mall是否存在
		    	Integer intName=indexName.get(str[4]);
		    	if(intName==null)
		    	{
		    		//添加
		    		re=new ItemRecommandBean();
		    		re.setBrandName(str[4]);
		    		addInfoBrand(re,str,0,indexName_,indexMap,indexName,recommandList);
		    		indexName_++;
		    		continue;
		    	}
		    	//则存在则添加recommandlist中
		    	//System.out.println(intName);
		    	ItemRecommandBean reBean=recommandList.get(intName);
		    	indexMap.put(str[2]+str[4],reBean.getCategoryMall().size());
		    	CategoryMallBean bean=new CategoryMallBean();
		    	bean.setCategoryName(str[2]);
				bean.setCategory(Long.parseLong(str[1]));
				for(int i=5;i<str.length;i++)
				{
					SonBean sonBean=new SonBean();
					String[] str2=str[i].split(CF.splitReg);
					sonBean.setId(Long.parseLong(str2[0]));
					sonBean.setCount(Integer.parseInt(str2[3]));
					sonBean.setName(str2[1]);
					sonBean.setValue(Double.parseDouble(str2[2]));
					if(str2.length==0)
					{
						continue;
					}
					bean.addRecommandMall(sonBean);
				}
				//添加入信息中
				reBean.addCategoryMall(bean);
	        }
	     reader.close();
		}catch(Exception e)
		{
			
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println(recommandList.size());
		System.out.println("读取相似度文件");
		//读取相似度文件
		try{
			 InputStreamReader read = new InputStreamReader(new FileInputStream(filePathSim),"utf-8");  
	         reader = new BufferedReader(read);
	         String tempString = null;
	         // 一次读入一行，直到读入null为文件结束
		     while ((tempString = reader.readLine()) != null)
	       	{
		    	 String[] str=tempString.split("\t");
		    	 //判断业态+mall是否存在
		    	 Integer int1=indexName.get(str[0]);
//		    	Integer int2=indexMap.get(str[0]+str[1]);
		 
		    	if(int1==null)
		    	{
		    		System.out.println("不存在:"+str[0]+str[1]);
		    		continue;
		    	}
		    	//则存在则添加recommandlist中
		    	ItemRecommandBean reBean=recommandList.get(int1);
		    	//CategoryMallBean itemBean=reBean.getCategoryMall().get(int2);
		    	//System.out.println(int1+"\t"+int2+"\t"+itemBean.getSizeSim());
				for(int i=2;i<str.length;i++)
				{
					SonSimBean sonSimBean=new SonSimBean();
					String[] str2=str[i].split(CF.splitReg);
					sonSimBean.setSimId(Long.parseLong(str2[0]));
					sonSimBean.setName(str2[1]);
					sonSimBean.setValue(Float.parseFloat(str2[2]));
					//System.out.println(int1+"\t"+int2+"\t"+str2[0]);
					if(str2.length==0)
					{
						continue;
					}
					//itemBean.addSimilaryMall(sonSimBean);
					reBean.addSimilaryBrand(sonSimBean);
				}
				//System.out.println(int1+"\t"+int2+"\t"+itemBean.getSizeSim());
	        }
	     reader.close();
		}catch(Exception e)
		{
			
		}finally{
			try {
				reader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("写文件");
		//最终写文件
		LinkedList<String> recommandListStr=new LinkedList<String>();
		while(recommandList.size()>0)
		{
			ItemRecommandBean recom=recommandList.remove(0);
			recom.sortSim();
			recommandListStr.add(JsonUtil.getJsonStr(recom));
		}
		fileUtilWrite.wirte(recommandListStr, recommandListStr.size());
		fileUtilWrite.close();
	}
	/**
	 * 添加 mall
	 * @param re
	 * @param str
	 * @param indexMap_
	 * @param indexName_
	 * @param indexMap
	 * @param indexName
	 * @param recommandList
	 */
	public void addInfoMall(MallRecommandBean re,String[] str,int indexMap_,int indexName_,HashMap<String,Integer> indexName,ArrayList<MallRecommandBean> recommandList)
	{
		CategoryItemBean bean=new CategoryItemBean();
		bean.setCategory(Integer.parseInt(str[1]));
		bean.setCategoryName(str[2]);
		for(int i=5;i<str.length;i++)
		{
			SonBean sonBean=new SonBean();
			String[] str2=str[i].split(CF.splitReg);
			sonBean.setId(Long.parseLong(str2[0]));
			sonBean.setCount(Integer.parseInt(str2[3]));
			sonBean.setName(str2[1]);
			sonBean.setValue(Double.parseDouble(str2[2]));
			if(str2.length==0)
			{
				continue;
			}
			bean.addRecommandShop(sonBean);
		}
		re.addCategoryItem(bean);
		indexName.put(str[4],indexName_);
		recommandList.add(re);
	}
	
	/**
	 * 添加 brand
	 * @param re
	 * @param str
	 * @param indexMap_
	 * @param indexName_
	 * @param indexMap
	 * @param indexName
	 * @param recommandList
	 */
	public void addInfoBrand(ItemRecommandBean re,String[] str,int indexMap_,int indexName_,HashMap<String,Integer> indexMap,HashMap<String,Integer> indexName,ArrayList<ItemRecommandBean> recommandList)
	{
		CategoryMallBean bean=new CategoryMallBean();
		bean.setCategoryName(str[2]);
		bean.setCategory(Long.parseLong(str[1]));
		for(int i=5;i<str.length;i++)
		{
			SonBean sonBean=new SonBean();
			String[] str2=str[i].split(CF.splitReg);
			sonBean.setId(Long.parseLong(str2[0]));
			sonBean.setCount(Integer.parseInt(str2[3]));
			sonBean.setName(str2[1]);
			sonBean.setValue(Double.parseDouble(str2[2]));
			if(str2.length==0)
			{
				continue;
			}
			bean.addRecommandMall(sonBean);
		}
		re.addCategoryMall(bean);
		indexName.put(str[4],indexName_);
		indexMap.put(str[2]+str[4], indexMap_);
		recommandList.add(re);
	}
	
	
	public static void main(String[] args) {
		String rePath=System.getProperty("user.dir")+"/data/mallToShop余弦10Test.txt";
		String reSim=System.getProperty("user.dir")+"/data/userSimiary余弦10Test.txt";
		String reSource=System.getProperty("user.dir")+"/data/mallToShop余弦10.txt";
		String output=System.getProperty("user.dir")+"/data/mallCos10Test.txt";
		
		
//		String rePath=System.getProperty("user.dir")+"/data/mallToShop余弦10Item.txt";
//		String reSim=System.getProperty("user.dir")+"/data/userSimiary余弦10Item.txt";
//		String output=System.getProperty("user.dir")+"/data/mallCos10Item.txt";
		RecommandEtlAll etl=new RecommandEtlAll(rePath,reSim,reSource,output,true);
		etl.run();
	}
}
