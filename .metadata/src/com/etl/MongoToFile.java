package com.etl;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.util.MongoDb;

public class MongoToFile {
	 public static void main(String[] args) {
  	   String ip="192.168.85.11";
 		int port=10001;
 		String database="demo";
  	   MongoDb mongo=new MongoDb(ip,port,database);
  	   DBCursor ret=mongo.find("fang");

      //   System.out.println("从数据集中读取数据："); 
//         TreeSet<String> tree=new TreeSet<String>();
//         while(ret.hasNext()){  
//             BasicDBObject bdbObj = (BasicDBObject) ret.next();  
//             if(bdbObj != null){   
//          	   String[] strTemp=bdbObj.getString("stationName").replaceAll("[\\[\\]\"\\s]","").split(",");
//          	   for(String temp:strTemp)
//          	   {
//          		   while(true)
//                    	 {
//        	            	 if(temp.contains("("))
//        	            	 {
//        	            		 String temp2="";
//        	            		 temp2=temp.substring(0,temp.indexOf("("));
//        	            		 if(temp.contains(")"))
//        	            		 {
//        	            			 temp2+=temp.substring(temp.indexOf(")")+1);
//        	           			 }
//        	            		 temp=temp2;
//        	            		 //System.out.println("temp:"+temp);
//        	            		
//        	            	 }else if(temp.contains("（"))
//        	            	 {
//        	            		 String temp2="";
//        	            		 temp2=temp.substring(0,temp.indexOf("（"));
//        	            		 if(temp.contains("）"))
//        	            		 {
//        	            			 temp2+=temp.substring(temp.indexOf("）")+1);
//        	           			 }
//        	            		 temp=temp2;
//        	            		 //System.out.println("temp:"+temp);
//        	            		
//        	            	 }
//        	            	 {
//        	            		 tree.add(temp);
//        	            		 break;
//        	            	 }
//                    	 } 
//          	   }
//          	   String[] strTemp2=bdbObj.getString("stationNameRever").replaceAll("[\\[\\]\"\\s]","").split(",");
//          	   for(String temp:strTemp2)
//          	   {
//          		   while(true)
//                    	 {
//        	            	 if(temp.contains("("))
//        	            	 {
//        	            		 String temp2="";
//        	            		 temp2=temp.substring(0,temp.indexOf("("));
//        	            		 if(temp.contains(")"))
//        	            		 {
//        	            			 temp2+=temp.substring(temp.indexOf(")")+1);
//        	           			 }
//        	            		 temp=temp2;
//        	            		 //System.out.println("temp:"+temp);
//        	            		
//        	            	 }else if(temp.contains("（"))
//        	            	 {
//        	            		 String temp2="";
//        	            		 temp2=temp.substring(0,temp.indexOf("（"));
//        	            		 if(temp.contains("）"))
//        	            		 {
//        	            			 temp2+=temp.substring(temp.indexOf("）")+1);
//        	           			 }
//        	            		 temp=temp2;
//        	            		 //System.out.println("temp:"+temp);
//        	            		
//        	            	 }
//        	            	 {
//        	            		 tree.add(temp);
//        	            		 break;
//        	            	 }
//                    	 } 
//          	   }
//             }  
//         }
//         for(String st:tree)
//         {
//      	   System.out.println(st);
//         }
        // TreeSet<String> tree=new TreeSet<String>();
         while(ret.hasNext()){  
           BasicDBObject bdbObj = (BasicDBObject) ret.next();  
           if(bdbObj != null){ 
          	 String temp=bdbObj.getString("address");
          	// System.out.println(temp);
          //	 String address="";
          	 String name=bdbObj.getString("officeBuildingName");
          	 String mapPx=bdbObj.getString("mapPx");
          	String mapPy=bdbObj.getString("mapPy");
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
	            		// address=temp;
	            		// tree.add(temp);
	            		 break;
	            	 }
          	 }
          	 System.out.println(name+"\t"+temp+"\t"+mapPx+"\t"+mapPy);
           }
         }
//         for(String st:tree)
//           {
//        	   System.out.println(st);
//           }
	}
}
