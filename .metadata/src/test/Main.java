package test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericBooleanPrefUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.impl.similarity.PearsonCorrelationSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.apache.mahout.common.RandomUtils;

import com.db.MysqlConnection;
import com.db.MysqlSelect;

public class Main {

	  /**
     * boolean模型
     */
    public static void runBooleanModel2(HashMap<Long,String> mall,HashMap<Long,String> shop) throws Exception
    {
    	RandomUtils.useTestSeed();
    	  DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/mallAndShop.txt"));//文件名一定要是绝对路径  
    	//DataModel model =new GenericBooleanPrefDataModel(
    	//		GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File(System.getProperty("user.dir")+"/data/mallAndShop.txt"))));
    	RecommenderIRStatsEvaluator evaluator=new GenericRecommenderIRStatsEvaluator();
    	RecommenderBuilder recommenderBuilder=new RecommenderBuilder()
    	{
    		public Recommender buildRecommender(DataModel model) throws TasteException
    		{
    			UserSimilarity similarity=new LogLikelihoodSimilarity(model);
    			//10代码表临域大小
    			UserNeighborhood neighborhood=new NearestNUserNeighborhood(5,similarity,model);
    			//return new GenericUserBasedRecommender(model,neighborhood,similarity);
    			//阈值处理函数 高于0.7的保存
    			//new ThresholdUserNeighborhood(0.7,similarity,model);
    			
    			//UserSimilarity similarity=new PearsonCorrelationSimilarity(model,Weighting.WEIGHTED);
    			//UserNeighborhood neighborhood=new NearestNUserNeighborhood(5,similarity,model);
    			//return new GenericUserBasedRecommender(model,neighborhood,similarity);
    			//欧式距离
    			//EuclideanDistanceSimilarity
    			
    			//通过这种方式可以把计算复杂度过高的 内部缓存加快速度
    			//UserSimilarity similarity=new CachingUserSimilarity(new SpearmanCorrelationSimilarity(model),model);
    			//谷本相似度 交集比率 jaccard
    			//TanimotoCoefficientSimilarity
    			//对数似然值
    			//LogLikelihoodSimilarity
    			//设置对缺失物品的偏好值默认值
    			//similarity.setPreferenceInferrer();
    			return new GenericBooleanPrefUserBasedRecommender(model,neighborhood,similarity);
    		}
    	};
    	DataModelBuilder modelBuilder=new DataModelBuilder()
    	{
			@Override
			public DataModel buildDataModel(
					FastByIDMap<PreferenceArray> trainingData) {
				// TODO Auto-generated method stub
				return new GenericBooleanPrefDataModel(GenericBooleanPrefDataModel.toDataMap(trainingData));
			}
    	};
    	IRStatistics stats=evaluator.evaluate(recommenderBuilder,modelBuilder,model,null,5,0.6,0.4);
    			//GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,1.0);
    	
    	System.out.println(stats.getPrecision());
    	//System.out.println(stats.getRecall());
    	Recommender recommender= recommenderBuilder.buildRecommender(model); 
    	print(recommender,mall,shop);
    	
    	//double score=evaluator.evaluate(recommenderBuilder,modelBuilder,model,null,5, 0.9,1.0);
    	//System.out.println(score);
    }
	
    public static void test(HashMap<Long,String> mall,HashMap<Long,String> shop) throws IOException, TasteException
    {
//   	 MysqlConnection mysql = new MysqlConnection(
//				"jdbc:mysql://192.168.85.11:3306/zjMysql", "root",
//				"root");
//	
//	String sql="select a.*,b.CommentCount,b.CommentStart from MallAndShop as a "+
//"left join MallAndCommentCount as b "+
//"on a.MallName=b.MallName order by MallID,ShopID";
//	MysqlSelect  myse=mysql.sqlSelect(sql);
    	
	
//	FileUtil2 fileUtil=new FileUtil2(System.getProperty("user.dir")+"/data/hello.txt","utf-8");
//	
//	
//	FastByIDMap<PreferenceArray> preferences =new FastByIDMap<PreferenceArray>();
//	
//	//存储mall信息
//	HashMap<Integer,String> mall=new HashMap<Integer,String>();
//	HashMap<Integer,String> shop=new HashMap<Integer,String>();
//	LinkedList<Integer> ma=new LinkedList<Integer>();
//	ArrayList<Integer> shop_id=new ArrayList<Integer>();
//	int mall_id=-1;
//	LinkedList<String> intoFile=new LinkedList<String>();
//	while(myse.resultSet.next())
//	{
//
//		//System.out.println("存在");
//		String shopName=myse.resultSet.getString(3);
//		String mallName=myse.resultSet.getString(5);
//		int mallID=myse.resultSet.getInt(6);
//		int shopID=myse.resultSet.getInt(7);
//		int commentCount=myse.resultSet.getInt(8);
//		System.out.println(mallID+","+shopID+","+1);
//		intoFile.add(mallID+","+shopID+","+1);
//		//System.out.println(shopName+"\t"+shopID+"\t"+mallID+"\t"+mallName);
//		if(mall_id==-1)
//		{
//			mall.put(mallID, mallName);
//			shop.put(shopID, shopName);
//			ma.add(mallID);
//		}
//		if(mall_id!=-1 && mallID!=mall_id)
//		{
//			PreferenceArray prefsForUser=new GenericUserPreferenceArray(shop_id.size());
//			prefsForUser.setUserID(0,mallID);
//			int i=-1;
//			for(Integer il:shop_id)
//			{
//				i++;
//				prefsForUser.setItemID(i,il);
//		        prefsForUser.setValue(i,1.0f); 
//			}
//			preferences.put(mallID, prefsForUser);
//			mall_id=mallID;
//			mall.put(mallID, mallName);
//			shop.put(shopID, shopName);
//			shop_id=new ArrayList<Integer>();
//		}else{
//			mall_id=mallID;
//			shop.put(shopID, shopName);
//			ma.add(mallID);
//			shop_id.add(shopID);
//		}
//		//加入myhout中
//		
//	}
//	fileUtil.wirte(intoFile, intoFile.size());
//	if(shop_id.size()>0)
//	{
//		PreferenceArray prefsForUser=new GenericUserPreferenceArray(shop_id.size());
//		prefsForUser.setUserID(0,mall_id);
//		int i=-1;
//		for(Integer il:shop_id)
//		{
//			i++;
//			prefsForUser.setItemID(i,il);
//	        prefsForUser.setValue(i,1.0f); 
//		}
//		preferences.put(mall_id, prefsForUser);
//		shop_id=null;
//	}
//	System.out.println("preferences:"+preferences.size());
//	DataModel  model=new  GenericDataModel(preferences);
	
//	DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/hello1.txt"));//文件名一定要是绝对路径  
//   
//    UserSimilarity similarity =new PearsonCorrelationSimilarity(model);  
//    UserNeighborhood neighborhood =new NearestNUserNeighborhood(2,similarity,model);  
//    Recommender recommender= new GenericUserBasedRecommender(model,neighborhood,similarity); 
//    //for(int i:ma)
//    for(int i=1;i<100;i++)
//    {
//    List<RecommendedItem> recommendations =recommender.recommend(i, 10);//为用户1推荐两个ItemID  
//    for(RecommendedItem recommendation :recommendations){  
//        System.out.println(recommendation);  
//    }
//    }
	   DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/mallAndShop.txt"));//文件名一定要是绝对路径  
	      
    UserSimilarity similarity =new PearsonCorrelationSimilarity(model);  
    UserNeighborhood neighborhood =new NearestNUserNeighborhood(2,similarity,model);  
    Recommender recommender= new GenericUserBasedRecommender(model,neighborhood,similarity); 
    print(recommender,mall,shop);
    //FastByIDMap by=new FastByIDMap();
    }
    
    public static void print(Recommender recommender,HashMap<Long,String> mall,HashMap<Long,String> shop)
    {
    	  for(int i=1;i<153;i++)
    	    {
    	    List<RecommendedItem> recommendations=null;
			try {
				recommendations = recommender.recommend(i*1L, 10);
			} catch (TasteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}//为用户1推荐两个ItemID 
    	    System.out.println("mall:"+i+"\t"+mall.get(i*1L));
    	    for(RecommendedItem recommendation :recommendations){  
    	        System.out.println("shop:"+recommendation.getItemID()+":"+shop.get(recommendation.getItemID())+"\tvalue:"+recommendation.getValue());  
    	    }
    	    }
    }
	
	
	public static void main(String[] args) throws Exception {
		MysqlConnection mysql = new MysqlConnection(
 				"jdbc:mysql://192.168.85.11:3306/zjMysql", "root",
 				"root");
	
		String sql="select a.*,b.CommentCount,b.CommentStart from MallAndShop as a "+
	"left join MallAndCommentCount as b "+
	"on a.MallName=b.MallName order by MallID,ShopID";
		MysqlSelect  myse=mysql.sqlSelect(sql);
		FastByIDMap<PreferenceArray> preferences =new FastByIDMap<PreferenceArray>();
		
		//存储mall信息
		HashMap<Long,String> mall=new HashMap<Long,String>();
		HashMap<Long,String> shop=new HashMap<Long,String>();
		LinkedList<Long> ma=new LinkedList<Long>();
		ArrayList<Long> shop_id=new ArrayList<Long>();
		long mall_id=-1;
		try{
		while(myse.resultSet.next())
		{
			//System.out.println("存在");
			String shopName=myse.resultSet.getString(3);
			String mallName=myse.resultSet.getString(5);
			long mallID=myse.resultSet.getInt(6);
			long shopID=myse.resultSet.getInt(7);
			//long commentCount=myse.resultSet.getInt(8);
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
				for(Long il:shop_id)
				{
					i++;
					prefsForUser.setItemID(i,il);
			        prefsForUser.setValue(i,1.0f); 
				}
				preferences.put(mallID, prefsForUser);
				mall_id=mallID;
				mall.put(mallID, mallName);
				shop.put(shopID, shopName);
				shop_id=new ArrayList<Long>();
			}else{
				mall_id=mallID;
				shop.put(shopID, shopName);
				ma.add(mallID);
				shop_id.add(shopID);
			}
			//加入myhout中
		}
		}catch(Exception e)
		{
			e.printStackTrace();
			System.exit(1);
		}
		
		Main.runBooleanModel2(mall,shop);
	}
}
