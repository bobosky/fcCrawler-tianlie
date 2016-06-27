package com.zj.exec;

import java.util.HashSet;

import org.bson.types.ObjectId;

import com.db.MongoDb;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
/**
 * 去除重复
 * @author Administrator
 *
 */
public class Test {

	HashSet<String> fangCode=new HashSet<String>();
	public void run()
	{
		MongoDb mongo=new MongoDb("192.168.1.4:27017","demo");
		DBCollection collection=mongo.getCollection("fang");
		DBCollection collection2=mongo.getCollection("fang");
		DBObject keys=new BasicDBObject();
		keys.put("_id",1);
		keys.put("fangCode",1);
		DBCursor cursor=collection.find(new BasicDBObject(), keys);
		while(cursor.hasNext())
		{
			BasicDBObject obj=(BasicDBObject)cursor.next();
			String fang=obj.getString("fangCode");
			if(fangCode.contains(fang))
			{
				DBObject key=new BasicDBObject();
				key.put("_id",new ObjectId(obj.getString("_id")));
				collection2.remove(key);
				System.out.println("删除:"+obj.getString("_id")+"\t"+fang);
			}else{
				fangCode.add(fang);
			}
			//System.out.println(obj.getString("_id")+"\t"+obj.getString("fangCode"));
		}
	}
	
	public static void main(String[] args) {
		Test test=new Test();
		test.run();
	}
}
