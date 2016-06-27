package test;

import org.apache.mahout.clustering.kmeans.KMeansDriver;
import org.apache.mahout.clustering.kmeans.RandomSeedGenerator;
import org.apache.mahout.common.HadoopUtil;
import org.apache.mahout.common.RandomUtils;
import org.apache.mahout.common.distance.DistanceMeasure;
import org.apache.mahout.common.distance.EuclideanDistanceMeasure;
import org.apache.mahout.utils.clustering.ClusterDumper;

public class Kmeans {
//
//	
//	public static void kMeansClusterUsingMapReduce () throws Exception{
//	    Configuration conf = new Configuration();
//
//	    // 声明一个计算距离的方法，这里选择了欧几里德距离
//	    DistanceMeasure measure = new EuclideanDistanceMeasure();
//
//	    // 指定输入路径，基于 Hadoop 的实现是通过指定输入输出的文件路径来指定数据源的。
//	    Path testpoints = new Path("testpoints");
//	    Path output = new Path("output");
//
//	    // 清空输入输出路径下的数据
//	    HadoopUtil.delete(conf, testpoints);
//	    HadoopUtil.delete(conf, output);
//
//	    RandomUtils.useTestSeed();
//
//	    // 在输入路径下生成点集，与内存的方法不同，这里需要把所有的向量写进文件
//	    writePointsToFile(testpoints);
//
//	    // 指定需要聚类的个数，这里选择 2 类
//	    int k = 2;
//
//	    // 指定 K 均值聚类算法的最大迭代次数
//	    int maxIter = 3;
//
//	    // 指定 K 均值聚类算法的最大距离阈值
//	    double distanceThreshold = 0.01;
//
//	    // 随机的选择k个作为簇的中心
//	    Path clusters = RandomSeedGenerator.buildRandom(conf, testpoints, new Path(output, "clusters-0"), k, measure);
//
//	    // 调用 KMeansDriver.runJob 方法执行 K 均值聚类算法
//	    KMeansDriver.run(testpoints, clusters, output, measure, distanceThreshold, maxIter, true, true);
//
//	    // 调用 ClusterDumper 的 printClusters 方法将聚类结果打印出来。
//	    ClusterDumper clusterDumper = new ClusterDumper(new Path(output, "clusters-" + (maxIter - 1)), new Path(output, "clusteredPoints"));
//	    clusterDumper.printClusters(null);
//	}
//	
//	public static void kMeansClusterUsingMapReduce() throws IOException, InterruptedException,
//    ClassNotFoundException {
//Configuration conf = new Configuration();
//
//// 声明一个计算距离的方法，这里选择了欧几里德距离
//DistanceMeasure measure = new EuclideanDistanceMeasure();
//File testData = new File("input");
//if (!testData.exists()) {
//testData.mkdir();
//}
//
//// 指定输入路径，基于 Hadoop 的实现是通过指定输入输出的文件路径来指定数据源的。
//Path samples = new Path("input/file1");
//
//// 在输入路径下生成点集，这里需要把所有的向量写进文件
//List<Vector> sampleData = new ArrayList<Vector>();
//
//RandomPointsUtil.generateSamples(sampleData, 400, 1, 1, 3);
//RandomPointsUtil.generateSamples(sampleData, 300, 1, 0, 0.5);
//RandomPointsUtil.generateSamples(sampleData, 300, 0, 2, 0.1);
//ClusterHelper.writePointsToFile(sampleData, conf, samples);
//
//// 指定输出路径
//Path output = new Path("output");
//HadoopUtil.delete(conf, output);
//
//// 指定需要聚类的个数，这里选择3
//int k = 3;
//
//// 指定 K 均值聚类算法的最大迭代次数
//int maxIter = 10;
//
//// 指定 K 均值聚类算法的最大距离阈值
//double distanceThreshold = 0.01;
//
//// 随机的选择k个作为簇的中心
//Path clustersIn = new Path(output, "random-seeds");
//RandomSeedGenerator.buildRandom(conf, samples, clustersIn, k, measure);
//
//// 调用 KMeansDriver.run 方法执行 K 均值聚类算法
//KMeansDriver.run(samples, clustersIn, output, measure, distanceThreshold, maxIter, true, 0.0, true);
//
//// 输出结果
//List<List<Cluster>> Clusters = ClusterHelper.readClusters(conf, output);
//for (Cluster cluster : Clusters.get(Clusters.size() - 1)) {
//System.out.println("Cluster id: " + cluster.getId() + " center: " + cluster.getCenter().asFormatString());
//}
//}
//	
//	// 创建苹果信息数据的向量组
//	public static List<Vector> generateAppleData() {
//	    List<Vector> apples = new ArrayList<Vector>();
//	    // 这里创建的是 NamedVector，其实就是在上面几种 Vector 的基础上，
//	    // 为每个 Vector 提供一个可读的名字
//	    NamedVector apple = new NamedVector(new DenseVector(new double[] {0.11, 510, 1}), "Small round green apple");
//	    apples.add(apple);
//
//	    apple = new NamedVector(new DenseVector(new double[] {0.2, 650, 3}), "Large oval red apple");
//	    apples.add(apple);
//
//	    apple = new NamedVector(new DenseVector(new double[] {0.09, 630, 1}), "Small elongated red apple");
//	    apples.add(apple);
//
//	    apple = new NamedVector(new DenseVector(new double[] {0.18, 520, 2}), "Medium oval green apple");
//	    apples.add(apple);
//
//	    return apples;
//	}
//	
//	// 创建一个二维点集的向量组
//	public static final double[][] points = { { 1, 1 }, { 2, 1 }, { 1, 2 },
//	 { 2, 2 }, { 3, 3 },  { 8, 8 }, { 9, 8 }, { 8, 9 }, { 9, 9 }, { 5, 5 },
//	 { 5, 6 }, { 6, 6 }};
//	public static List<Vector> getPointVectors(double[][] raw) {
//	    List<Vector> points = new ArrayList<Vector>();
//	    for (int i = 0; i < raw.length; i++) {
//	        double[] fr = raw[i];
//	        // 这里选择创建 RandomAccessSparseVector
//	        Vector vec = new RandomAccessSparseVector(fr.length);
//	    // 将数据存放在创建的 Vector 中
//	        vec.assign(fr);
//	        points.add(vec);
//	    }
//	    return points;
//	}
//	
//	public static void run()
//	{
//		DataModel model = new FileDataModel(new File("/Users/matrix/Documents/plan/test/ratings.txt"));
//
//		// 2. 实现相似度算法
//		// 使用PearsonCorrelationSimilarity实现UserSimilarity接口, 计算用户的相似度
//		// 其中PearsonCorrelationSimilarity是基于皮尔逊相关系数计算相似度的实现类
//		// 其它的还包括
//		// EuclideanDistanceSimilarity：基于欧几里德距离计算相似度
//		// TanimotoCoefficientSimilarity：基于 Tanimoto 系数计算相似度
//		// UncerteredCosineSimilarity：计算 Cosine 相似度
//		UserSimilarity similarity = new PearsonCorrelationSimilarity(model);
//		// 可选项
//		similarity.setPreferenceInferrer(new AveragingPreferenceInferrer(model));
//
//		// 3. 选择邻居用户
//		// 使用NearestNUserNeighborhood实现UserNeighborhood接口, 选择最相似的三个用户
//		// 选择邻居用户可以基于'对每个用户取固定数量N个最近邻居'和'对每个用户基于一定的限制，取落在相似度限制以内的所有用户为邻居'
//		// 其中NearestNUserNeighborhood即基于固定数量求最近邻居的实现类
//		// 基于相似度限制的实现是ThresholdUserNeighborhood
//		UserNeighborhood neighborhood = new NearestNUserNeighborhood(3, similarity, model);
//
//		// 4. 实现推荐引擎
//		// 使用GenericUserBasedRecommender实现Recommender接口, 基于用户相似度进行推荐
//		Recommender recommender = new GenericUserBasedRecommender(model, neighborhood, similarity);
//		Recommender cachingRecommender = new CachingRecommender(recommender);
//		List<RecommendedItem> recommendations = cachingRecommender.recommend(1234, 10);
//
//		// 输出推荐结果
//		for (RecommendedItem item : recommendations) {
//		    System.out.println(item.getItemID() + "\t" + item.getValue());
//		}
//	}
}
