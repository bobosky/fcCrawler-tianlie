package test.objbean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import test.CF;

import com.etl.FileUtil2;
import com.etl.MysqlConnection;
import com.util.JsonUtil;

/**
 * 值整合2个文件的
 * 将文件整合成文件
 * @author Administrator
 *
 */
public class RecommandEtl {

	
	
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
	 * 是否为 mall文件
	 */
	private boolean isMall=false;
	/**
	 * 文件名
	 * @param filePath
	 * @param code
	 */
	public RecommandEtl(String filePathRe,String filePathSim,String outPutPath,boolean isMall)
	{
		this.filePathRe=filePathRe;
		this.filePathSim=filePathSim;
		this.fileUtilWrite=new FileUtil2(outPutPath,"utf-8");
		this.isMall=isMall;
		//获取热门品牌热门品牌的信息
		
	}
	
	public void run()
	{
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
		//存储分类+名
		HashMap<String,Integer> indexMap=new HashMap<String,Integer>();
		//存储名字
		HashMap<String,Integer> indexName=new HashMap<String,Integer>();
		ArrayList<MallRecommandBean> recommandList=new ArrayList<MallRecommandBean>();
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
		    	 //判断mall是否存在
		    	Integer intName=indexName.get(str[4]);
		    	if(intName==null)
		    	{
		    		//添加
		    		re=new MallRecommandBean();
		    		re.setMallName(str[4]);
		    		addInfoMall(re,str,0,indexName_,indexMap,indexName,recommandList);
		    		indexName_++;
		    		continue;
		    	}
		    	//则存在则添加recommandlist中
		    	//System.out.println(intName);
		    	MallRecommandBean reBean=recommandList.get(intName);
		    	indexMap.put(str[2]+str[4],reBean.getCategoryItem().size());
		    	CategoryItemBean bean=new CategoryItemBean();
		    	 if(!str[2].equals("购物")&&!str[2].equals("美食")&&!str[2].equals("丽人"))
		    	 {
		    		 continue;
		    	 }
				bean.setCategoryName(str[2]);
				bean.setCategory(Integer.parseInt(str[1]));
				for(int i=5;i<str.length;i++)
				{
					SonBean sonBean=new SonBean();
					String[] str2=str[i].split(CF.splitReg);
					sonBean.setId(Long.parseLong(str[0]));
					sonBean.setCount(Integer.parseInt(str2[3]));
					sonBean.setName(str2[1]);
					sonBean.setValue(Double.parseDouble(str2[2]));
					if(str2.length==0)
					{
						continue;
					}
					bean.addRecommandShop(sonBean);
				}
				//添加入信息中
				reBean.addCategoryItem(bean);
	        }
	     reader.close();
		}catch(Exception e)
		{
			
		}finally{
			try {
				if(reader!=null)
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
		    	 Integer int1=indexName.get(str[1]);
		    	Integer int2=indexMap.get(str[0]+str[1]);
		 
		    	if(int1==null)
		    	{
		    		System.out.println("不存在:"+str[0]+str[1]);
		    		continue;
		    	}
		    	//则存在则添加recommandlist中
		    	MallRecommandBean reBean=recommandList.get(int1);
		    	CategoryItemBean itemBean=reBean.getCategoryItem().get(int2);
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
		    		re.setBrandId(Long.parseLong(str[3]));
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
				bean.setCategory(Long.parseLong(str[1]));
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
				if(reader!=null)
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
		    	 Integer int1=indexName.get(str[1]);
		    	Integer int2=indexMap.get(str[0]+str[1]);
		 
		    	if(int1==null)
		    	{
		    		System.out.println("不存在:"+str[0]+str[1]);
		    		continue;
		    	}
		    	//则存在则添加recommandlist中
		    	ItemRecommandBean reBean=recommandList.get(int1);
		    	CategoryMallBean itemBean=reBean.getCategoryMall().get(int2);
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
	public void addInfoMall(MallRecommandBean re,String[] str,int indexMap_,int indexName_,HashMap<String,Integer> indexMap,HashMap<String,Integer> indexName,ArrayList<MallRecommandBean> recommandList)
	{
		CategoryItemBean bean=new CategoryItemBean();
		bean.setCategoryName(str[2]);
		bean.setCategory(Integer.parseInt(str[1]));
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
		indexMap.put(str[2]+str[4], indexMap_);
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
		bean.setCategory(Long.parseLong(str[1]));
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
			bean.addRecommandMall(sonBean);
		}
		re.addCategoryMall(bean);
		indexName.put(str[4],indexName_);
		indexMap.put(str[2]+str[4], indexMap_);
		recommandList.add(re);
	}
	
	
	public static void main(String[] args) {
//		String rePath=System.getProperty("user.dir")+"/data/mallToShop余弦10.txt";
//		String reSim=System.getProperty("user.dir")+"/data/userSimiary余弦10.txt";
//		String output=System.getProperty("user.dir")+"/data/mallCos10.txt";
		
		
//		String rePath=System.getProperty("user.dir")+"/data/mallToShop余弦10Item.txt";
//		String reSim=System.getProperty("user.dir")+"/data/userSimiary余弦10Item.txt";
//		String output=System.getProperty("user.dir")+"/data/mallCos10Item.txt";
		
		String rePath=System.getProperty("user.dir")+"/data/mallToShop余弦10Itemcontent.txt";
		String reSim=System.getProperty("user.dir")+"/data/userSimiary余弦10Itemcontent.txt";
		String output=System.getProperty("user.dir")+"/data/mallCos10Itemcontent.txt";
		RecommandEtl etl=new RecommandEtl(rePath,reSim,output,false);
		etl.run();
	}
}
