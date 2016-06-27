package com.etl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.util.FileUtil2;
import com.util.JsonUtil;
import com.zj.bean.CompanyBean;
import com.zj.bean.CompanyBean2;
import com.zj.bean.CompanyBean3;

public class FileFilter {
	
	/**
	 * 将公司信息过滤 重复 整合 放入 文件种
	 */
	public static void runIntoCompanyFilter()
	{
		FileUtil2 fileUtil=new FileUtil2(System.getProperty("user.dir")+"/data/companyFilter.txt","utf8");
		File file=new File("F:\\zj"+"/companyDesc-2014-11-27.txt");
		BufferedReader reader = null;
		try{
		 InputStreamReader read = new InputStreamReader(new FileInputStream(file),"utf-8");  
         reader = new BufferedReader(read);
         String tempString = null;
         System.out.println("开始读取文件");
         // 一次读入一行，直到读入null为文件结束
         //是否为注释
         HashMap<String,CompanyBean2> comList=new HashMap<String,CompanyBean2>();
         int i=0;
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 i++;
	        	 if(i%1000==0)
	        	 {
	        		 System.out.println(i);
	        	 }
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 CompanyBean com=(CompanyBean)JsonUtil.getDtoFromJsonObjStr(tempString, CompanyBean.class);
	        	 //System.out.println(com.getCompanyName()+"\t"+com.getCompanyUrl());
	        	 if(comList.containsKey(com.getCompanyCode()))
	        	 {
	        		 CompanyBean2 com2=comList.get(com.getCompanyCode());
	        		 com2.addSubwayStation(com.getSubWayStation());
	        	 }else{
	        		 CompanyBean2 com2=new CompanyBean2();
	        		 com2.setCompanyCategory(com.getCompanyCategory());
	        		 com2.setCompanyUrl(com.getCompanyUrl());
	        		 com2.setCompanyName(com.getCompanyName());
	        		 com2.addSubwayStation(com.getSubWayStation());
	        		 comList.put(com.getCompanyCode(), com2);
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
	 		LinkedList<String> inputData=new LinkedList<String>();
	 		for(Entry<String, CompanyBean2> co:comList.entrySet())
	 		{
	 			CompanyBean2 c=co.getValue();
	 			CompanyBean3 cb=new CompanyBean3();
	 			cb.setCompanyCategory(c.getCompanyCategory());
	 			cb.setCompanyName(c.getCompanyName());
	 			cb.setCompanyUrl(c.getCompanyUrl());
	 			String tem="";
	 			int p=0;
	 			for(String st:c.getSubWayStation())
	 			{
	 				p++;
	 				if(p==1)
	 				{
	 					tem+=st;
	 				}else{
	 					tem+=","+st;
	 				}
	 			}
	 			cb.setSubWayStation(tem);
	 			inputData.add(JsonUtil.getJsonStr(cb));
	 		}
	 		fileUtil.wirte(inputData, inputData.size());
	 		fileUtil.close();
		}
         catch(Exception e)
         {
        	 e.printStackTrace();
         }
		finally{
	       
		}
	}

	public static void main(String[] args) {
		
		FileFilter.runIntoCompanyFilter();
	}
}
