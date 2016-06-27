package com.etl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.util.JsonUtil;
import com.zj.bean.AreaBean;
import com.zj.bean.BussinessArea;
import com.zj.bean.BussinessAreaDesc;
import com.zj.bean.CompanyBean2;
import com.zj.bean.CompanyBean3;

/**
 * 获取城市 商圈信息
 * @author Administrator
 *
 */
public class BussinessAreaEtl {

	public static void runIntoCompanyFilter()
	{
		FileUtil2 fileUtil=new FileUtil2(System.getProperty("user.dir")+"/data/dianpingArea.txt","utf8");
		File file=new File("F:\\zj"+"/dianpingArea-2014-12-29.txt");
		BufferedReader reader = null;
		HashMap<String,Object> map=new HashMap<String,Object>();
		map.put("bussinessAreaAll",BussinessAreaDesc.class);
		map.put("bussinessArea",AreaBean.class);
		map.put("area",AreaBean.class);
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
	        		// System.out.println(i);
	        	 }
	        	 if(tempString.length()==0)
	        	 {
	        		 continue;
	        	 }
	        	 BussinessArea com=(BussinessArea)JsonUtil.getDtoFromJsonObjStr(tempString, BussinessArea.class,map);
	        	 //System.out.println(tempString);
	        	 //System.out.println(com.getCompanyName()+"\t"+com.getCompanyUrl());
	        	if(com.getCityId()==2)
	        	{
	        		for(BussinessAreaDesc desc:com.getBussinessAreaAll())
	        		{
	        			//System.out.println("'"+desc.getArea().getName()+"',");
	        			for(AreaBean bean:desc.getBussinessArea())
	        			{
	        				//System.out.println("'"+desc.getArea().getName()+"',");
	        				System.out.print("'"+bean.getName()+"',");
	        			}
	        			//System.out.println();
	        		}
	        		//break;
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
		BussinessAreaEtl.runIntoCompanyFilter();
	}
}
