package com.zj.etlFilter;

import java.text.DecimalFormat;
import java.util.Map.Entry;

import org.bson.types.ObjectId;

import com.db.MongoDb;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;

/**
 * 搜房的数据添加字段
 * 临时程序 对搜房添加 相关的 etl字段
 * @author Administrator
 *
 */
public class FangNewObject {

	
	public static MongoDb mongo=new MongoDb("192.168.1.4",27017,"demo");
	
	public final static String  FANG="fang";
	
	public final  static String FANG_UPDATE="fangUpdate";
	
	public  final static  DecimalFormat df   = new DecimalFormat("#.00");   
	public static void run(String collection)
	{
		DBCursor cursor=mongo.find(collection);
		while(cursor.hasNext())
		{
			BasicDBObject obj=(BasicDBObject)cursor.next();
//			System.out.println(obj.toString());
//			System.out.println(obj.getString("_id"));
			ObjectId key=new ObjectId(obj.getString("_id"));
			BasicDBObject doc=new BasicDBObject();
//			System.out.println(obj.getString("currentMonthHirePrice"));
			String value=obj.getString("currentMonthHirePrice");
			BasicDBList valueList=(BasicDBList)obj.get("hireTrendValue");
			//System.out.println(obj.getString("currentMonthHirePriceCategory"));
			String cate=obj.getString("currentMonthHirePriceCategory");
			String city=((BasicDBObject)(obj.get("fangListc"))).getString("city");
			BasicDBList valueListEtl=null;
			String valueEtl="";
			if(cate.contains("天"))
			{
				valueEtl=value;
				valueListEtl=valueList;
			}else if(cate.equals("")||!cate.contains("米"))
			{
				valueEtl="";
				valueListEtl=valueList;
			}
			else{
				valueEtl=Double.toString(Math.round(Double.parseDouble(value)/30*100)*1d/100);
				//System.out.println(city+":"+value+":"+cate+":"+valueEtl);
				for(int i=0;i<valueList.size();i++)
				{	
					for(Entry<String,Object> map:((BasicDBObject)valueList.get(i)).entrySet())
					{
						if(map.getKey().equals("money"))
						{
							if(map.getValue() instanceof Double)
							{
								map.setValue(Math.round((double)(map.getValue())/30*100)*1d/100);
							}else{
								map.setValue(Math.round(((int)(map.getValue()))*1d/30*100)*1d/100);
							}
							//System.out.println("format:"+map.getValue());
						}
					}
				}
				valueListEtl=valueList;
				//System.out.println(valueListEtl.toString());
			}
			doc.put("_id", key);
			BasicDBObject doc2=new BasicDBObject();
			BasicDBObject currentMonthHirePriceEtl=new BasicDBObject();
			currentMonthHirePriceEtl.put("currentMonthHirePriceEtl",valueEtl);
			currentMonthHirePriceEtl.put("hireTrendValueEtl",valueListEtl);
			doc2.put("$set",currentMonthHirePriceEtl);
			mongo.update(collection, doc, doc2);
		}
	}	
	public static void main(String[] args) {
		FangNewObject.run(FangNewObject.FANG);
	}
}
