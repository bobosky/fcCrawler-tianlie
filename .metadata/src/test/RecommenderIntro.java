package test;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.common.Weighting;
import org.apache.mahout.cf.taste.eval.DataModelBuilder;
import org.apache.mahout.cf.taste.eval.IRStatistics;
import org.apache.mahout.cf.taste.eval.RecommenderBuilder;
import org.apache.mahout.cf.taste.eval.RecommenderEvaluator;
import org.apache.mahout.cf.taste.eval.RecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.eval.AverageAbsoluteDifferenceRecommenderEvaluator;
import org.apache.mahout.cf.taste.impl.eval.GenericRecommenderIRStatsEvaluator;
import org.apache.mahout.cf.taste.impl.model.GenericBooleanPrefDataModel;
import org.apache.mahout.cf.taste.impl.model.file.*;  
import org.apache.mahout.cf.taste.impl.neighborhood.*;  
import org.apache.mahout.cf.taste.impl.recommender.*;
import org.apache.mahout.cf.taste.impl.recommender.knn.KnnItemBasedRecommender;
import org.apache.mahout.cf.taste.impl.recommender.knn.NonNegativeQuadraticOptimizer;
import org.apache.mahout.cf.taste.impl.recommender.knn.Optimizer;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.MemoryDiffStorage;
import org.apache.mahout.cf.taste.impl.recommender.slopeone.SlopeOneRecommender;
import org.apache.mahout.cf.taste.impl.recommender.svd.Factorizer;
import org.apache.mahout.cf.taste.impl.recommender.svd.SVDRecommender;
import org.apache.mahout.cf.taste.impl.similarity.*;  
import org.apache.mahout.cf.taste.model.*;  
import org.apache.mahout.cf.taste.neighborhood.*;  
import org.apache.mahout.cf.taste.recommender.*;  
import org.apache.mahout.cf.taste.recommender.slopeone.DiffStorage;
import org.apache.mahout.cf.taste.similarity.*;  
import org.apache.mahout.common.RandomUtils;

import java.io.*;  
import java.util.*;  
public class RecommenderIntro {  
    private RecommenderIntro(){};  
      
    
    public static void runPerson() throws Exception
    {
        // step:1 构建模型 2 计算相似度 3 查找k紧邻 4 构造推荐引擎  
   	
       DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/mallAndShop.txt"));//文件名一定要是绝对路径  
      
       UserSimilarity similarity =new PearsonCorrelationSimilarity(model);  
       UserNeighborhood neighborhood =new NearestNUserNeighborhood(2,similarity,model);  
       Recommender recommender= new GenericUserBasedRecommender(model,neighborhood,similarity); 
       for(int i=1;i<10;i++)
       {
    	   List<RecommendedItem> recommendations =recommender.recommend(i, 10);//为用户1推荐两个ItemID  
    	   for(RecommendedItem recommendation :recommendations){  
    	   
           System.out.println(recommendation);  
       }
       }
    }
    /**
     * boolean模型
     */
    public static void runBooleanModel() throws Exception
    {
    	DataModel model =new GenericBooleanPrefDataModel(
    			GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File(System.getProperty("user.dir")+"/data/ml-100k/ua.base"))));
    	RecommenderEvaluator evaluator=new AverageAbsoluteDifferenceRecommenderEvaluator();
    	RecommenderBuilder recommenderBuilder=new RecommenderBuilder()
    	{
    		public Recommender buildRecommender(DataModel model) throws TasteException
    		{
    			//UserSimilarity similarity=new PearsonCorrelationSimilarity(model);
    			//加权person 偏向
    			//UserSimilarity similarity=new PearsonCorrelationSimilarity(model,Weighting.WEIGHTED);
    			//UserNeighborhood neighborhood=new NearestNUserNeighborhood(10,similarity,model);
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
    			//物品相似度
    			ItemSimilarity similarity=new PearsonCorrelationSimilarity(model,Weighting.WEIGHTED);
    			return new GenericItemBasedRecommender(model,similarity);
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
    	double score=evaluator.evaluate(recommenderBuilder,modelBuilder,model,0.9,1.0);
    	System.out.println(score);
    }
    
    /**
     * boolean模型
     */
    public static void runBooleanModel2() throws Exception
    {
    	DataModel model =new GenericBooleanPrefDataModel(
    			GenericBooleanPrefDataModel.toDataMap(new FileDataModel(new File(System.getProperty("user.dir")+"/data/ml-100k/ua.base"))));
    	RecommenderIRStatsEvaluator evaluator=new GenericRecommenderIRStatsEvaluator();
    	RecommenderBuilder recommenderBuilder=new RecommenderBuilder()
    	{
    		public Recommender buildRecommender(DataModel model) throws TasteException
    		{
    			UserSimilarity similarity=new LogLikelihoodSimilarity(model);
    			UserNeighborhood neighborhood=new NearestNUserNeighborhood(5,similarity,model);
    			//return new GenericUserBasedRecommender(model,neighborhood,similarity);
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
    	IRStatistics stats=evaluator.evaluate(recommenderBuilder,modelBuilder,model,null,5,0.7,0.3);
    			//GenericRecommenderIRStatsEvaluator.CHOOSE_THRESHOLD,1.0);
    	System.out.println("boolean:"+stats.getPrecision());
    	System.out.println("boolean:"+stats.getRecall());
    }
    
    public static void run() throws Exception
    {
    	   RandomUtils.useTestSeed();
    	   DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/mallAndShop.txt"));//文件名一定要是绝对路径  
    	   RecommenderEvaluator evaluator=new AverageAbsoluteDifferenceRecommenderEvaluator();
    	   RecommenderBuilder builder=new RecommenderBuilder()
    	   {
    		   @Override
    		   public Recommender buildRecommender(DataModel model) throws TasteException
    		   {
        		   UserSimilarity similarity=new PearsonCorrelationSimilarity(model);
    			  UserNeighborhood neighborhood=new NearestNUserNeighborhood(2,similarity,model);
    			  return new GenericUserBasedRecommender(model,neighborhood,similarity);
    		   }
    	   };
    	   double score=evaluator.evaluate(builder,null,model,0.7,0.3);
    	   System.out.println("person:"+score);
    }
    
    
    //slope-one
    //用户对新物品的洗好成都的估算方法
    
    public static void runSlopeOne() throws Exception
    {
    	  DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/mallAndShop.txt"));//文件名一定要是绝对路径  
    	  //第3个字段为 内存使用上限 b
    	  RecommenderEvaluator evaluator=new AverageAbsoluteDifferenceRecommenderEvaluator();
    	  RecommenderBuilder builder=new RecommenderBuilder() {
              public Recommender buildRecommender(DataModel model) throws TasteException {
            	  DiffStorage diffStorage=new MemoryDiffStorage(model,Weighting.UNWEIGHTED,Long.MAX_VALUE);
            	  return new SlopeOneRecommender(model,Weighting.UNWEIGHTED,Weighting.UNWEIGHTED,diffStorage);
              }
             };
             double score=evaluator.evaluate(builder,null,model,0.7,0.3);
       	   System.out.println("slope:"+score);
    }
    
    
    public static void  svdRecommender(final Factorizer factorizer) throws TasteException, IOException {
    	DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/mallAndShop.txt"));//文件名一定要是绝对路径  
    	RecommenderEvaluator evaluator=new AverageAbsoluteDifferenceRecommenderEvaluator();
    	RecommenderBuilder builder=new RecommenderBuilder() {
            public Recommender buildRecommender(DataModel dataModel) throws TasteException {
                return new SVDRecommender(dataModel, factorizer);
            }
        };
        double score=evaluator.evaluate(builder,null,model,0.7,0.3);
 	   System.out.println("svd:"+score);
    }
        
    public static void  knnItem() throws TasteException, IOException {
    	DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/mallAndShop.txt"));//文件名一定要是绝对路径  
   
    	RecommenderEvaluator evaluator=new AverageAbsoluteDifferenceRecommenderEvaluator();
    	//最大似然毒 计算10个最近邻物品
    	RecommenderBuilder builder=new RecommenderBuilder() {
    		public Recommender buildRecommender(DataModel model) throws TasteException {
    			Optimizer optimizer=new NonNegativeQuadraticOptimizer();
    		 	ItemSimilarity similarity=new LogLikelihoodSimilarity(model);
    		 	return new KnnItemBasedRecommender(model,similarity,optimizer,5);
    		}
    	};
    	 double score=evaluator.evaluate(builder,null,model,0.7,0.3);
   	   System.out.println("knn:"+score);
    }
    
    public static void  runCluster() throws TasteException, IOException {
    	DataModel  model =new FileDataModel(new File(System.getProperty("user.dir")+"/data/mallAndShop.txt"));//文件名一定要是绝对路径  
    	RecommenderEvaluator evaluator=new AverageAbsoluteDifferenceRecommenderEvaluator();
    	//最大似然毒 计算10个最近邻物品
    	RecommenderBuilder builder=new RecommenderBuilder() {
    		public Recommender buildRecommender(DataModel model) throws TasteException {
    	UserSimilarity similarity=new LogLikelihoodSimilarity(model);
    	ClusterSimilarity clusterSimilarity=new FarthestNeighborClusterSimilarity(similarity);
    	return new TreeClusteringRecommender(model,clusterSimilarity,5);
    		}
    		};	
    		 double score=evaluator.evaluate(builder,null,model,0.7,0.3);
    	   	   System.out.println("cluster:"+score);
    }
        
    public static void main (String args[]) throws Exception{  
       
    	//RecommenderIntro.runTest1();
          
    	//RecommenderIntro.runTest2();
    	
    	//RecommenderIntro.runBooleanModel();
    	
    	//RecommenderIntro.runBooleanModel2();
    	RecommenderIntro.run();
    }  
}  