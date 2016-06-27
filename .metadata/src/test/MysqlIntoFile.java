package test;

import java.io.File;
import java.sql.Date;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;

import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.etl.FileUtil2;
import com.util.FileUtil;

public class MysqlIntoFile {

	
	
	public static void main(String[] args) throws SQLException, TasteException {
		 MysqlConnection mysql = new MysqlConnection(
	 				"jdbc:mysql://192.168.85.11:3306/zjMysql", "root",
	 				"root");
		
		String sql="select a.*,b.CommentCount,b.CommentStart from MallAndShop as a "+
"left join MallAndCommentCount as b "+
"on a.MallName=b.MallName order by MallID,ShopID";
		MysqlSelect  myse=mysql.sqlSelect(sql);
		
		FileUtil2 fileUtil=new FileUtil2(System.getProperty("user.dir")+"/data/shopAndMallAndCategory.txt","utf-8");
		FastByIDMap<PreferenceArray> preferences =new FastByIDMap<PreferenceArray>();
		
		
		LinkedList<String> inputFile=new LinkedList<String>();
		//存储mall信息
		HashMap<Integer,String> mall=new HashMap<Integer,String>();
		HashMap<Integer,String> shop=new HashMap<Integer,String>();
		LinkedList<Integer> ma=new LinkedList<Integer>();
		ArrayList<Integer> shop_id=new ArrayList<Integer>();
		int mall_id=-1;
		while(myse.resultSet.next())
		{

			//System.out.println("存在");
			String shopName=myse.resultSet.getString(3);
			int category=myse.resultSet.getInt(4);
			String mallName=myse.resultSet.getString(5);
			int mallID=myse.resultSet.getInt(6);
			int shopID=myse.resultSet.getInt(7);
			int commentCount=myse.resultSet.getInt(8);
			//inputFile.add(mallID+","+category+","+shopID+",1");
			inputFile.add(shopID+","+category+","+mallID+",1");
			if(mall_id==-1)
			{
				mall.put(mallID, mallName);
				shop.put(shopID, shopName);
				ma.add(mallID);
			}
			if(mall_id!=-1 && mallID!=mall_id)
			{
				PreferenceArray prefsForUser=new GenericUserPreferenceArray(shop_id.size());
				prefsForUser.setUserID(0,mallID);
				int i=-1;
				for(Integer il:shop_id)
				{
					i++;
					prefsForUser.setItemID(i,il);
			        prefsForUser.setValue(i,1.0f); 
				}
				preferences.put(mallID, prefsForUser);
				mall_id=mallID;
				mall.put(mallID, mallName);
				shop.put(shopID, shopName);
				shop_id=new ArrayList<Integer>();
			}else{
				mall_id=mallID;
				shop.put(shopID, shopName);
				ma.add(mallID);
				shop_id.add(shopID);
			}
			//加入myhout中
		}
		if(shop_id.size()>0)
		{
			PreferenceArray prefsForUser=new GenericUserPreferenceArray(shop_id.size());
			prefsForUser.setUserID(0,mall_id);
			int i=-1;
			for(Integer il:shop_id)
			{
				i++;
				prefsForUser.setItemID(i,il);
		        prefsForUser.setValue(i,1.0f); 
			}
			preferences.put(mall_id, prefsForUser);
			shop_id=null;
		}
		fileUtil.wirte(inputFile,inputFile.size());
		DataModel  model=new  GenericDataModel(preferences);
		
	//	DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/hello.txt"));//文件名一定要是绝对路径  
	      
	       UserSimilarity similarity =new PearsonCorrelationSimilarity(model);  
	       UserNeighborhood neighborhood =new NearestNUserNeighborhood(2,similarity,model);  
	       Recommender recommender= new GenericUserBasedRecommender(model,neighborhood,similarity); 
	       for(int i:ma)
	       {
	       List<RecommendedItem> recommendations =recommender.recommend(i, 10);//为用户1推荐两个ItemID  
	       for(RecommendedItem recommendation :recommendations){  
	           System.out.println(recommendation);  
	       }
	       }
	}
}
