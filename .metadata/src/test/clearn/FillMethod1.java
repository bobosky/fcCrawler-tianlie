package test.clearn;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.db.MysqlSelect;
import com.db.MysqlConnection;

/**
 * 定制方法
 * 
 * @author Administrator
 * 
 */
public class FillMethod1 extends DataFillUtil {

	public int clusterCount = 3;
	/**
	 * 判断对应的string的位置
	 */
	public HashMap<String, Integer> stringMap = new HashMap<String, Integer>();
	/**
	 * 从文件的index到数组中德index的对应
	 */
	public HashMap<Integer, Integer> stringMapMap = new HashMap<Integer, Integer>();

	/**
	 * 分位数数据信息
	 */
	public HashMap<Long,Integer> queue1Map=new HashMap<Long,Integer>();
	@Override
	public void run(HashSet<Integer> feather, HashMap<Integer, Integer> cluster) {
		// TODO Auto-generated method stub
		// 做的是品牌的推荐方法
		// 修改 mall对应的信息 其中价格需要 做中位数
		// 其他使用 均值
		try {
			System.out.println("获取分类信息");
			// MysqlConnection mysql = new MysqlConnection(
			// "jdbc:mysql://192.168.1.4:3306/zjMysql", "root",
			// "root");
			// String
			// str="select CategoryCode,Categoryname,'' from MallCategory";
			//
			// MysqlSelect select=mysql.sqlSelect(str);
			// ResultSet res=select.resultSet;
			// while(res.next())
			// {
			// Long categoryCode=res.getLong(1);
			// String categoryName=res.getString(2).split("\r\n")[0];
			// category.put(categoryCode,categoryName);
			// }
			// res.close();
			// 添加mall对应的标签
			LinkedList<String[]> sList = readFile(
					System.getProperty("user.dir")
							+ "/data/mallFileCategory.txt", "utf-8");
			for (String[] s : sList) {
				Long categoryCode = parseLong(s[0]);
				String categoryName = s[1];
				category.put(categoryCode, categoryName);
			}
			
			System.out.println("获取品牌信息");

			// str="select a.shopName,a.branchName,a.shopId,a.prevWeeklyHits,a.weeklyHits,a.monthlyHits,a.hits,a.avgPrice,"+
			// "a.shopPower,a.popularity,a.power,GROUP_CONCAT(c.shopGroupId2),a.score "+
			// "from MallInfoDianping as a left join DianpingRel as b on  a.shops=b.relcode "+
			// "left join BrandInfoDianping as  c on b.dianpingCode=c.shopId where a.shopId<>11298446 group by shopName,branchName ";
			// 
			//
			// select=mysql.sqlSelect(str);
			// res=select.resultSet;
			// 添加brand的标签
			sList = readFile(System.getProperty("user.dir")
					+ "/data/mallFile.txt", "utf-8");
			// 数据离散化方法 有序分类变量

			stander(sList, true, feather, mallStatistic, mall, "mall");
			// res.close();
			sList = readFile(System.getProperty("user.dir")
					+ "/data/mallFileBrand.txt", "utf-8");

			// str="select a.shopName2,"",a.shopGroupId2,convert(avg(a.prevWeeklyHits),signed), ";
//			 "convert(avg(a.weeklyHits),signed),convert(avg(a.monthlyHits),signed),convert(avg(a.hits),signed),convert(avg(a.avgPrice),signed), ";
//			 "convert(avg(a.shopPower),signed),convert(avg(a.popularity),signed),convert(avg(a.power),signed),"",convert(avg(a.score),signed) "; 
//			 "from(select a.shopName2,a.branchName,a.shopId,a.shopGroupId2,a.prevWeeklyHits,a.weeklyHits,a.monthlyHits,a.hits, ";
//			 "a.avgPrice,a.shopPower,a.popularity,a.power,a.score from BrandInfoDianping as a ";
//			 "order by a.shopGroupId2,a.score desc ) as a ";
//			 "group by a.shopName2,a.shopGroupId2 order by a.shopGroupId2";
			// System.out.println("获取品牌信息");
			// select=mysql.sqlSelect(str);
			// res=select.resultSet;
			System.out.println("获取分位数数据");	
			System.out.println("标准化");
			stander(sList, false, feather, brandStatistic, brand, "brand");
			//初始化需要分位数数据
			int scoreIndex=stringMap.get("score");
			initScore1Qure(scoreIndex);
			//填补score数据位1分位数数据
			updateScore1Qure(scoreIndex);
			// 离散化品牌数据
			// 聚类mall的信息
			clusterMall(feather, cluster);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

	}

	/**
	 * 对mall做canopy聚类算法//并修改对应的品牌的分类变量
	 */
	public void clusterMall(HashSet<Integer> feather,
			HashMap<Integer, Integer> cluster) {
		for (Entry<Long, String> mall2 : mall.entrySet()) {
			Long mallId = mall2.getKey();
			int[] lp = mapS.get(mallId);
			// 获取所有下面的 brand
			long[] brandId = relMap.get(mallId);
			for (int i = 0; i < lp.length; i++) {
				Integer clusterCount = 3;
				clusterCount = cluster.get(stringMapMap.get(new Integer(i)));
				// System.out.println(stringMapMap.size()+"\t"+clusterCount);
				if (clusterCount == null) {
					continue;
				}

				// 将前四个点击的做离散化处理
				LinkedList<float[]> val = getBrandByIndex(brandId, i);
				Canopy canopy = new Canopy(val, 4f, 1f);
				canopy.randomCentor(clusterCount);
				// canopy.run();
				// System.out.println("数量:"+canopy.centor.size());
				canopy.runKmeans(100);
				ArrayList<float[]> centor = canopy.centor;
				// 最近临分配类别
				changeBrandVal(centor, brandId, i);
				changeMallVal(centor, mallId, i);
			}

		}
	}

	/**
	 * 获取mall下的品牌的某一个index的float组
	 * 
	 * @param brandId
	 * @param index
	 * @return
	 */
	public LinkedList<float[]> getBrandByIndex(long[] brandId, int index) {
		LinkedList<float[]> result = new LinkedList<float[]>();
		for (long brand : brandId) {
			float[] temp = map.get(brand);
			if (temp == null) {
				System.out.println("不存在品牌:" + brand);
				continue;
			}
			float[] temp2 = new float[1];
			temp2[0] = temp[index];
			result.add(temp2);
		}
		return result;
	}

	/**
	 * 通过mall的聚类中心，修改brandId的类
	 * 
	 * @param centor
	 * @param brandId
	 * @param index
	 */
	public void changeBrandVal(ArrayList<float[]> centor, long[] brandId,
			int index) {
		// Collections.sort();
		ArrayList<Float> temp = new ArrayList<Float>();
		for (float[] f : centor) {
			temp.add(f[0]);
		}
		Collections.sort(temp);
		for (long brand : brandId) {
			float[] brandV = map.get(brand);
			if (brandV == null) {
				continue;
			}
			float min = Float.MAX_VALUE;
			int[] brandSV = mapS.get(brand);
			brandSV[index] = 0;
			for (int i = 0; i < centor.size(); i++) {
				float val = Math.abs(temp.get(i) - brandV[index]);
				if (min > val) {
					min = val;
					brandSV[index] = i + 1;
				}
			}
			// System.out.println(brandSV[index]+"\t"+index);
		}
	}

	/**
	 * 通过mall的聚类中心，修改mallId的类
	 * 
	 * @param centor
	 * @param brandId
	 * @param index
	 */
	public void changeMallVal(ArrayList<float[]> centor, long mallId, int index) {
		float[] mallV = map.get(mallId);
		int[] mallVS = mapS.get(mallId);
		float min = Float.MAX_VALUE;
		mallVS[index]=0;
		for (int i = 0; i < centor.size(); i++) {
			float temp = Math.abs(centor.get(i)[0] - mallV[index]);
			if (temp < min) {
				min = temp;
				mallVS[index] = i + 1;
			}
		}
	}

	/**
	 * 读取对应的输入数据 并做标准化处理
	 * 
	 * @param sList
	 *            输入的文件数据
	 * @param isMall
	 *            是否是mall
	 * @param count
	 *            feather数量
	 * @param statistics
	 *            对应的mall或者brand的统计类
	 * @param rel
	 *            mall或者brand的特征hashMap
	 */
	public void stander(LinkedList<String[]> sList, boolean isMall,
			HashSet<Integer> feather, Statistics statistics,
			HashMap<Long, String> rel, String name) {
		int indexll = 0;
		if (feather.contains(new Integer(3))) {
			stringMapMap.put(indexll, 3);
			stringMap.put("prevWeeklyHits", indexll);
			indexll++;
		}
		if (feather.contains(new Integer(4))) {
			stringMapMap.put(indexll, 4);
			stringMap.put("weeklyHits", indexll);
			indexll++;
		}
		if (feather.contains(new Integer(5))) {
			stringMapMap.put(indexll, 5);
			stringMap.put("monthlyHits", indexll);
			indexll++;
		}
		if (feather.contains(new Integer(6))) {
			stringMapMap.put(indexll, 6);
			stringMap.put("hits", indexll);
			indexll++;
		}
		if (feather.contains(new Integer(7))) {
			stringMapMap.put(indexll, 7);
			stringMap.put("avgPrice", indexll);
			indexll++;
		}
		if (feather.contains(new Integer(8))) {
			stringMapMap.put(indexll, 8);
			stringMap.put("shopPower", indexll);
			indexll++;
		}
		if (feather.contains(new Integer(9))) {
			stringMapMap.put(indexll, 9);
			stringMap.put("popularity", indexll);
			indexll++;
		}
		if (feather.contains(new Integer(10))) {
			stringMapMap.put(indexll, 10);
			stringMap.put("power", indexll);
			indexll++;
		}
		if (feather.contains(new Integer(12))) {
			stringMapMap.put(indexll, 12);
			stringMap.put("score", indexll);
		}

		ArrayList<ArrayList<Float>> valList = new ArrayList<ArrayList<Float>>();
		for (int i = 0; i < feather.size(); i++)
			valList.add(new ArrayList<Float>());
		statistics.mean = new float[feather.size()];
		for (String[] s : sList) {
			String shopName = s[0];
			String branchName = s[1];
			Long shopId = parseLong(s[2]);
			if(shopId==11298446)
			{
				//将华联商厦数据清除掉
				continue;
			}
			if (isMall) {
				if (branchName != null && !branchName.equals(""))
					rel.put(shopId, shopName + "(" + branchName + ")");
				else
					rel.put(shopId, shopName);
			} else {
				rel.put(shopId, shopName);
			}
			Long prevWeeklyHits = null;
			if (feather.contains(new Integer(3)))
				prevWeeklyHits = parseLong(s[3]);
			Long weeklyHits = null;
			if (feather.contains(new Integer(4)))
				weeklyHits = parseLong(s[4]);
			Long monthlyHits = null;
			if (feather.contains(new Integer(5)))
				monthlyHits = parseLong(s[5]);
			Long hits = null;
			if (feather.contains(new Integer(6)))
				hits = parseLong(s[6]);
			Long avgPrice = null;
			if (feather.contains(new Integer(7)))
				avgPrice = parseLong(s[7]);
			Long shopPower = null;
			if (feather.contains(new Integer(8)))
				shopPower = parseLong(s[8]);
			Long popularity = null;
			if (feather.contains(new Integer(9)))
				popularity = parseLong(s[9]);
			Long power = null;
			if (feather.contains(new Integer(10)))
				power = parseLong(s[10]);
			Long score = 0L;
			if (feather.contains(new Integer(12))) {
				if (s.length < 13) {

				} else {
					score = parseLong(s[12]);
				}
			}
			// 存储原始数据
			float[] temp = new float[feather.size()];
			// 存储离散化数据
			int[] temp2 = new int[feather.size()];
			int indexl = 0;
			// 添加对应的tag信息
			if (feather.contains(new Integer(3))) {
				set(temp, temp2, indexl, (float) Math.log(prevWeeklyHits + 1),
						shopId, valList, statistics.mean);
				indexl++;
			}
			if (feather.contains(new Integer(4))) {
				set(temp, temp2, indexl, (float) Math.log(weeklyHits + 1),
						shopId, valList, statistics.mean);
				indexl++;
			}
			if (feather.contains(new Integer(5))) {
				set(temp, temp2, indexl, (float) Math.log(monthlyHits + 1),
						shopId, valList, statistics.mean);
				indexl++;
			}
			if (feather.contains(new Integer(6))) {
				set(temp, temp2, indexl, (float) Math.log(hits + 1), shopId,
						valList, statistics.mean);
				indexl++;
			}
			if (feather.contains(new Integer(7))) {
				set(temp, temp2, indexl, avgPrice, shopId, valList,
						statistics.mean);
				indexl++;
			}
			if (feather.contains(new Integer(8))) {
				set(temp, temp2, indexl, shopPower, shopId, valList,
						statistics.mean);
				indexl++;
			}
			if (feather.contains(new Integer(9))) {
				set(temp, temp2, indexl, popularity, shopId, valList,
						statistics.mean);
				indexl++;
			}
			if (feather.contains(new Integer(10))) {
				set(temp, temp2, indexl, power, shopId, valList,
						statistics.mean);
				indexl++;
			}
			if (feather.contains(new Integer(12))) {
				set(temp, temp2, indexl, score, shopId, valList,
						statistics.mean);
				indexl++;
			}
			if (s.length < 12) {
				relMap.put(shopId, new long[0]);
			} else {
				String[] shops = s[11].split(",");

				long[] shopsId = new long[shops.length];
				for (int i = 0; i < shops.length; i++) {
					shopsId[i] = parseLong(shops[i]);
				}
				// 存储关联
				relMap.put(shopId, shopsId);
			}
			// 设置shopid值
			// System.out.println("添加："+shopId);
			map.put(shopId, temp);
			mapS.put(shopId, temp2);
		}
		System.out.println("数据清洗");
		clearn();
		// for (int i = 0; i < valList.size(); i++) {
		// statistics.mean[i] /= valList.get(i).size();
		// }
		// 获取标准差
		// statistics.std=GetStd.get(valList,statistics.mean);
		// 正规化处理
		// GetStd.change(map,statistics.mean, statistics.std);
		// GetStd.print(statistics.mean, statistics.std,name);
	}

	/**
	 * 清洗和填充缺失值填补
	 */
	public void clearn() {
		while (true) {
			Long remove = poll();
			if (remove == null) {
				return;
			}
			// 做清洗

			float[] shop = map.get(remove);
			// 更新 得分
			updateScore(remove, shop);
		}
	}

	/**
	 * 某一列 做均值 更新
	 * 
	 * @param remove
	 * @param shopVal
	 * @param index
	 */
	public void updateScoreMean(Long remove, float[] shopVal, int index,
			long[] shops) {
		if (shopVal[index] < 1E-10) {// 更新score
			float val = 0f;
			int count = 0;
			for (long shop : shops) {
				float[] in = map.get(shop);
				if (in == null) {
					continue;
				}
				if (in[index] > 1E-10) {
					val += in[index];
					count++;
				}
			}
			if (count > 0)
				shopVal[index] = (float) Math.floor(val / count);
		}
	}
	/**
	 * 通过分位数方法 获取1分位的数量
	 * 不按照业态做统计
	 * @param remove
	 * @param shopVal
	 * @param index
	 * @param shops
	 */
	public void initScore1Qure(int index)
	{
		for(Entry<Long,String> mall1:mall.entrySet())
		{
			Long id=mall1.getKey();
			//获取对应的shops
			long[] shops=relMap.get(id);
			//获取shop得分
			ArrayList<Integer> shopVal=new ArrayList<Integer>();
			for(long shop:shops)
			{
				int[] flv=mapS.get(shop);
				if(flv==null || flv[index]<=1E-4)
				{
					continue;
				}
				//获取对应的index
				shopVal.add((int)flv[index]);
			}
			Collections.sort(shopVal);
			queue1Map.put(id,shopVal.get(shopVal.size()*3/4));
		}
	}

	/**
	 * 通过分位数方法 获取1分位的数量
	 * 不按照业态做统计
	 * @param remove
	 * @param shopVal
	 * @param index
	 * @param shops
	 */
	public void updateScore1Qure(int index)
	{
		for(Entry<Long,String> mal:mall.entrySet())
		{
			Long id=mal.getKey();
			//获取shop得分
			int[] val=mapS.get(id);
			if(val[index]==0)
			{//如果为0则做填补
				val[index]=queue1Map.get(id);
			}
		}
	}
	
	/**
	 * 更新 得分
	 * 
	 * @param remove
	 * @param shopVal
	 * @param index
	 * @param shops
	 */
	public void updateScoreAvgScore(Long remove, float[] shopVal, int index,
			long[] shops) {
		if (shopVal[index] < 1E-10) {// 更新avgPrice
										// 使用1/4-3/4的数据集的均值
			LinkedList<Float> val = new LinkedList<Float>();
			for (long shop : shops) {
				float[] in = map.get(shop);
				if (in == null) {
					continue;
				}
				if (in[index] > 1E-10) {
					val.add(in[index]);
				}
			}
			Collections.sort(val);
			if (val.size() > 4) {
				int index2 = 0;
				float v = 0;
				for (Float f : val) {
					index2++;
					if (index2 >= val.size() * 3 / 4) {
						break;
					} else if (index2 >= val.size() / 4) {
						v += f;
					}
				}
				shopVal[index] = (float) Math.floor(v / (val.size() / 2));
				// shopVal[index]=1000000;
			} else if (val.size() > 0) {
				float v = 0;
				for (Float f : val) {
					v += f;
				}
				shopVal[index] = (float) Math.floor(v / val.size());
				// shopVal[index]=1000000;
			} else {
				// shopVal[index]=-111111;
			}
		}
	}

	/**
	 * 
	 * @param remove
	 *            更新的id
	 * @param index
	 *            更新的列
	 * @return
	 */
	public void updateScore(Long remove, float[] shopVal) {
		long[] shops = relMap.get(remove);
		if (shops == null) {
			return;
		}
		// System.out.println(stringMap.get("avgPrice"));
		if (stringMap.get("avgPrice") != null)
			updateScoreAvgScore(remove, shopVal, stringMap.get("avgPrice"),
					shops);
		if (stringMap.get("shopPower") != null)
			updateScoreMean(remove, shopVal, stringMap.get("shopPower"), shops);
		if (stringMap.get("popularity") != null)
			updateScoreMean(remove, shopVal, stringMap.get("popularity"), shops);
		if (stringMap.get("power") != null)
			updateScoreMean(remove, shopVal, stringMap.get("power"), shops);
		if (stringMap.get("score") != null)
			updateScoreMean(remove, shopVal, stringMap.get("score"), shops);

	}

	/**
	 * 初始化
	 * 
	 * @param temp
	 * @param index
	 * @param val
	 * @param shopId
	 */
	public void set(float[] temp, int index, Long val, Long shopId) {
		if (val == null) {
			temp[index] = 0f;
		} else {
			temp[index] = val;
		}
		if (temp[index] < 1E-10) {
			add(shopId);
		}
	}

	/**
	 * 初始化
	 * 
	 * @param temp
	 * @param index
	 * @param val
	 * @param shopId
	 */
	public void set(float[] temp, int[] temp2, int index, float val,
			Long shopId, ArrayList<ArrayList<Float>> valList, float[] mean) {
		if (val == 0f) {
			temp[index] = 0f;
		} else {
			float fl = Float.valueOf(val);
			valList.get(index).add(fl);
			mean[index] += fl;
			temp[index] = fl;
			temp2[index] = (int) val;
		}
		if (temp[index] < 1E-10) {
			add(shopId);
		}
	}

	/**
	 * 初始化
	 * 
	 * @param temp
	 * @param index
	 * @param val
	 * @param shopId
	 */
	public void set(float[] temp, int index, Long val, Long shopId,
			ArrayList<ArrayList<Float>> valList, float[] mean) {
		if (val == null) {
			temp[index] = 0f;
		} else {
			float fl = Float.parseFloat(Long.toString(val));
			valList.get(index).add(fl);
			mean[index] += fl;
			temp[index] = fl;
		}
		if (temp[index] < 1E-10) {
			add(shopId);
		}
	}

	public LinkedList<String[]> readFile(String file, String fileCode) {
		LinkedList<String[]> result = new LinkedList<String[]>();
		BufferedReader reader = null;
		try {

			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), fileCode);
			reader = new BufferedReader(read);
			String tempString = null;
			System.out.println("开始读取文件");
			// 一次读入一行，直到读入null为文件结束
			// 是否为注释
			while ((tempString = reader.readLine()) != null) {
				if (tempString.length() == 0) {
					continue;
				}
				result.add(tempString.trim().split("\t"));
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
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		return result;
	}

	

}
