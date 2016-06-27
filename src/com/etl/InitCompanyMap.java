package com.etl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import com.db.Redis;
import com.util.JsonUtil;
import com.zj.bean.Company51JobCompanyBean;
import com.zj.bean.Company51JobDescBean;
import com.zj.exec.MainStatic;

/**
 * 用于初始化company信息
 * @author Administrator
 *
 */
public class InitCompanyMap {

		private File file = null;
		/**
		 * 文件类型
		 */
		private String code = "utf-8";
		
		private Redis redis=null;
		
		/**
		 * 文件
		 * @param filename
		 * @param code
		 */
		public InitCompanyMap(String filename,String code)
		{
			System.out.println(filename+"\t"+code);
			redis=new Redis("192.168.1.11:51900");
			//文件名需要重制定
			file=new File(filename);
			if(!file.exists())
			{
				System.out.println("文件不存在");
				System.exit(1);
			}
			this.code=code;
			
		}
		public void runCompany()
		{
			BufferedReader reader = null;
			try{
			 
			 InputStreamReader read = new InputStreamReader(new FileInputStream(file),this.code);  
	         reader = new BufferedReader(read);
	         String tempString = null;
	         
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()<10)
	        	 {
	        		 continue;
	        	 }
	        	 //Company51JobDescBean
	        	 Company51JobCompanyBean company=(Company51JobCompanyBean)JsonUtil.getDtoFromJsonObjStr(tempString, Company51JobCompanyBean.class);
	        	 redis.hset(MainStatic.companyCodeMap,Long.toString(company.getCompanyCode()), company.getCompanyUrl());
	        	// System.out.println(Long.toString(company.getCompanyCode())+"\t"+company.getCompanyUrl());
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
			}catch(Exception e)
			{
				e.printStackTrace();
				}
			  
		}
		
		public void runJob()
		{
			BufferedReader reader = null;
			try{
			 
			 InputStreamReader read = new InputStreamReader(new FileInputStream(file),this.code);  
	         reader = new BufferedReader(read);
	         String tempString = null;
	         
	         while ((tempString = reader.readLine()) != null)
	         {
	        	 if(tempString.length()<10)
	        	 {
	        		 continue;
	        	 }
	        	 //Company51JobDescBean
	        	 Company51JobDescBean company=(Company51JobDescBean)JsonUtil.getDtoFromJsonObjStr(tempString, Company51JobDescBean.class);
	        	 redis.hset(MainStatic.jobCodeMap,Long.toString(company.getJobCode()), company.getPublishDate());
	        	// System.out.println(Long.toString(company.getJobCode())+"\t"+company.getPublishDate());
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
			}catch(Exception e)
			{
				e.printStackTrace();
				}
			  
		}
		public static void main(String[] args) {
			InitCompanyMap map=new InitCompanyMap("f:\\zjCompany\\companyDesc-2014-12-12.txt","utf-8");
			map.runCompany();
			InitCompanyMap map2=new InitCompanyMap("f:\\zjCompany\\companyDesc-2014-12-13.txt","utf-8");
			map2.runCompany();
			 map=new InitCompanyMap("f:\\zjCompany\\companyDescJob-2014-12-12.txt","utf-8");
			map.runJob();
			 map2=new InitCompanyMap("f:\\zjCompany\\companyDescJob-2014-12-13.txt","utf-8");
			map2.runJob();
			
		}
}
