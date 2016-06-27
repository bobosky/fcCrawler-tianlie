package test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import org.apache.mahout.cf.taste.impl.common.FastByIDMap;
import org.apache.mahout.cf.taste.impl.model.GenericUserPreferenceArray;
import org.apache.mahout.cf.taste.model.PreferenceArray;
import org.mortbay.log.Log;

import sun.misc.Compare;
import test.bean.ItemNode;
import test.bean.UserInfo;
import test.bean.UserNode;
import test.clearn.DataFillUtil;
import test.clearn.FillMethod1;
import test.clearn.PreHitAndNowHitChange;
import test.clearn.ValueAddChange;
import test.filter.ItemGetCel;
import test.filter.ItemGetRurl;
import test.filter.RecommanderFilterUtil;
import test.filter.RecommanderHostItemCountFilter;
import test.filter.RecommanderHostItemRateFilter;
import test.similary.CityBlockSimilary;
import test.similary.CosinSimilary;
import test.similary.JarcardSimilary;
import test.similary.LogLikelihoodRatioSimilary;
import test.similary.MathchHard;
import test.similary.PearsonSimilary;
import test.similary.SimilaryUtil;
import test.similary.SpearmanSimilary;
import test.weight.UserHotItemLoss;
import test.weight.WeightUtil;

import com.db.MysqlConnection;
import com.db.MysqlSelect;
import com.etl.FileUtil2;
import com.sun.xml.internal.stream.Entity;
import com.util.FileUtil;

/**
 * cf 基于用户Cf
 * 
 * @author Administrator
 * 
 */
public class CF {

	public static String splitStr = "^@@^";
	public static String splitReg = "\\^@@\\^";
	/**
	 * 存储组 对应的用户
	 */
	private ArrayList<UserInfo> userGroup = new ArrayList<UserInfo>();
	/**
	 * 测试集 对应的用户
	 */
	private ArrayList<UserInfo> userGroupTest = new ArrayList<UserInfo>();
	/**
	 * 相似性函数
	 */
	private SimilaryUtil simiFunc = null;
	/**
	 * 基于内容推荐的相似性计算方法
	 */
	private SimilaryUtil contentSimiFunc = null;

	private HashMap<Long, long[]> contentSimiMatrix = null;
	/**
	 * 相似度加权方法
	 */
	private WeightUtil weight = null;
	
	/**
	 * 作为基于内容推荐的权重信息
	 */
	private HashMap<Long,float[]> weightPower=null;
	
	public void setWeightPower(HashMap<Long, float[]> weightPower) {
		this.weightPower = weightPower;
	}

	/**
	 * 最终推荐结果的过滤器方法
	 */
	private LinkedList<RecommanderFilterUtil> recommanderFilter = new LinkedList<RecommanderFilterUtil>();

	/**
	 * 数据清洗的的过滤器方法
	 */
	private LinkedList<RecommanderFilterUtil> dataClearnFilter = new LinkedList<RecommanderFilterUtil>();
	/**
	 * 物品的提取规则
	 */
	private ItemGetRurl itemRurl = null;
	/**
	 * 输出数量限制
	 */
	private int limitItem = 200;
	/**
	 * 相似度获取上限 比率
	 */
	private float simLimitRate = 0.1f;
	/**
	 * 
	 */
	private int simLimitRateCount = 20;
	/**
	 * 相似度上限制 是否为向上方向
	 */
	private boolean isGetUp = true;
	/**
	 * 距离限制 >simLimitvalue
	 */
	private float simLimitValue = 0f;
	/**
	 * mall id 对应名字
	 */
	private HashMap<Long, String> mall = null;
	/**
	 * shop id 对应 名字
	 */
	private HashMap<Long, String> shop = null;
	/**
	 * 分类划分
	 */
	private HashMap<Long, String> category = null;
	/**
	 * 训练集比率
	 */
	private float trainRate = 0.8f;
	/**
	 * 库中全部商品的总数量
	 */
	private int itemCount = 12595;
	/**
	 * 输出文件地址
	 */
	private String outIntoFile = "";
	/**
	 * 文件工具类 存储推荐数据
	 */
	private FileUtil2 fileUitl = null;
	/**
	 * 存储 用户相似度
	 */
	private FileUtil2 fileUser = null;

	/**
	 * 读取content文件
	 */
	private String contentFile = null;
	/**
	 * mall 是否为user
	 */
	private boolean isMall = true;
	/**
	 * 写文件的时候是否打印 过滤的物品
	 */
	private boolean isPrintFilterItem = false;

	/**
	 * 打印 用户相似度
	 */
	private boolean isPrintUserSimilary = true;

	private int printUserSimilaryLimit = 50;
	
	
	public ArrayList<UserInfo> getUserGroupTest() {
		return userGroupTest;
	}

	public void setUserGroupTest(ArrayList<UserInfo> userGroupTest) {
		this.userGroupTest = userGroupTest;
	}

	public ArrayList<UserInfo> getUserGroup() {
		return userGroup;
	}

	public void setUserGroup(ArrayList<UserInfo> userGroup) {
		this.userGroup = userGroup;
	}

	public String getContentFile() {
		return contentFile;
	}

	public void setContentFile(String contentFile) {
		this.contentFile = contentFile;
	}

	public boolean isPrintUserSimilary() {
		return isPrintUserSimilary;
	}

	public void setPrintUserSimilary(boolean isPrintUserSimilary) {
		this.isPrintUserSimilary = isPrintUserSimilary;
	}

	public int getPrintUserSimilaryLimit() {
		return printUserSimilaryLimit;
	}

	public void setPrintUserSimilaryLimit(int printUserSimilaryLimit) {
		this.printUserSimilaryLimit = printUserSimilaryLimit;
	}

	/**
	 * 是否按照业态划分
	 */
	private boolean isCategory = false;

	/**
	 * 
	 * @param limitItem
	 *            最终显示的物品数量
	 * @param simLimitRate
	 *            有效相似度 邻域比率值 默认为 up方向
	 * @param trainRate
	 *            训练比率
	 * @param mall
	 *            mall 对应 id 的名字
	 * @param shop
	 *            shop id对应名字
	 * @param outIntoFile
	 *            输出文件地址
	 * @param isMall
	 *            是mall推荐品牌，还是品牌推荐mall
	 */
	public CF(int limitItem, float simLimitRate, float trainRate,
			HashMap<Long, String> mall, HashMap<Long, String> shop,
			HashMap<Long, String> category, String outIntoFile, boolean isMall,
			boolean isCategory) {
		this.isMall = isMall;
		this.outIntoFile = outIntoFile;
		this.limitItem = limitItem;
		this.simLimitRate = simLimitRate;
		this.trainRate = trainRate;
		this.mall = mall;
		this.shop = shop;
		this.category = category;
		this.outIntoFile = outIntoFile;
		this.isCategory = isCategory;
		if (outIntoFile == null) {
		} else {
			fileUitl = new FileUtil2(System.getProperty("user.dir")
					+ "/data/mallToShop" + this.outIntoFile + ".txt", "utf-8");
		}
	}

	/**
	 * 设置物品总数量
	 * 
	 * @param itemCount
	 */
	public void setItemCount(int itemCount) {
		this.itemCount = itemCount;
	}

	/**
	 * 设置权重规则
	 * 
	 * @param weight
	 */
	public void setWeightUtil(WeightUtil weight) {
		this.weight = weight;
		if (weight == null) {
		} else {
		}
	}

	/**
	 * 设置最终推荐过滤器方法
	 * 
	 * @param recommanderFilter
	 */
	public void setRecommanderFilter(
			LinkedList<RecommanderFilterUtil> recommanderFilter) {
		this.recommanderFilter = recommanderFilter;
	}

	/**
	 * 设置最终推荐过滤器方法
	 * 
	 * @param recommanderFilter
	 */
	public void addRecommanderFilter(RecommanderFilterUtil recommanderFilter) {
		this.recommanderFilter.add(recommanderFilter);
	}

	/**
	 * 设置初始 数据清洗工具
	 * 
	 * @param recommanderFilter
	 */
	public void setDataClearnFilter(
			LinkedList<RecommanderFilterUtil> recommanderFilter) {
		this.dataClearnFilter = recommanderFilter;
	}

	/**
	 * 设置初始 数据清洗工具
	 * 
	 * @param recommanderFilter
	 */
	public void addDataClearnFilter(RecommanderFilterUtil recommanderFilter) {
		this.dataClearnFilter.add(recommanderFilter);
	}

	/**
	 * 设置训练集比率
	 * 
	 * @param trainRate
	 */
	public void setTrainRate(float trainRate) {
		this.trainRate = trainRate;
	}

	/**
	 * 设置 截断值
	 * 
	 * @param simLimitValue
	 */
	public void setSimLimitValue(float simLimitValue) {
		this.simLimitValue = simLimitValue;
	}

	/**
	 * 设置限制方向
	 * 
	 * @param isGetUp
	 */
	public void SetGetUp(boolean isGetUp) {
		this.isGetUp = isGetUp;
	}

	public WeightUtil getWeight() {
		return weight;
	}

	public void setWeight(WeightUtil weight) {
		this.weight = weight;
	}

	/**
	 * 设置相似性函数
	 */
	public void setSimilaryFunc(SimilaryUtil simiFunc) {
		this.simiFunc = simiFunc;
	}

	/**
	 * 设置 基于内容的推荐方法
	 * 
	 * @param simiFunc
	 */
	public void setContentSimiFunc(SimilaryUtil simiFunc) {
		this.contentSimiFunc = simiFunc;
	}

	/**
	 * 设置物品提取规则
	 * 
	 * @param itemRurl
	 */
	public void setItemGetRurl(ItemGetRurl itemRurl) {
		this.itemRurl = itemRurl;
	}

	/**
	 * 添加用户到对应的组中
	 * 
	 * @param userNode
	 * @param is
	 *            Category 是否 按照分类分隔
	 */
	public void addUser(long category, UserNode userNode, boolean isCategory) {
		if (isCategory) {
			boolean flag = false;
			for (int i = 0; i < userGroup.size(); i++) {
				UserInfo userInfo = userGroup.get(i);
				flag = userInfo.isThisGroup(category);
				if (flag) {
					userInfo.addUser(userNode);
					break;
				}
			}
			if (!flag) {
				UserInfo userInfo = new UserInfo();
				userInfo.setCatgory(category);
				userInfo.addUser(userNode);
				userGroup.add(userInfo);
			}
		} else {
			boolean flag = false;
			for (int i = 0; i < userGroup.size(); i++) {
				UserInfo userInfo = userGroup.get(i);
				userInfo.addUser(userNode);
				flag = true;
				break;
			}
			if (!flag) {
				UserInfo userInfo = new UserInfo();
				userInfo.setCatgory(1);
				userInfo.addUser(userNode);
				userGroup.add(userInfo);
			}
		}
	}

	/**
	 * 添加用户到对应的组中 测试集
	 * 
	 * @param userNode
	 */
	public void addUserTest(long category, UserNode userNode, boolean isCategory) {
		if (isCategory) {
			boolean flag = false;
			for (int i = 0; i < userGroupTest.size(); i++) {
				UserInfo userInfo = userGroupTest.get(i);
				flag = userInfo.isThisGroup(category);
				if (flag) {
					userInfo.addUser(userNode);
					break;
				}
			}
			if (!flag) {
				UserInfo userInfo = new UserInfo();
				userInfo.setCatgory(category);
				userInfo.addUser(userNode);
				userGroupTest.add(userInfo);
			}
		} else {
			boolean flag = false;
			for (int i = 0; i < userGroupTest.size(); i++) {
				UserInfo userInfo = userGroupTest.get(i);
				userInfo.addUser(userNode);
				flag = true;
				break;
			}
			if (!flag) {
				UserInfo userInfo = new UserInfo();
				userInfo.setCatgory(1);
				userInfo.addUser(userNode);
				userGroupTest.add(userInfo);
			}
		}
	}

	/**
	 * 读取文件
	 * 
	 * @param filePath
	 * @param code
	 * @param regex
	 */
	public void readFile(String filePath, String code, String regex) {
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("文件不存在:" + filePath);
			System.exit(1);
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(filePath), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String tempString = null;
		System.out.println("开始读取文件");
		// 一次读入一行，直到读入null为文件结束
		try {
			while ((tempString = reader.readLine()) != null) {
				// 用户，物品，喜好
				String[] split = tempString.split(regex);
				if(split[1].equals("45") || split[1].equals("60") ||split[1].equals("65")||split[1].equals("80"))
				{
					//剔除不使用的分类
					continue;
				}
				if (split.length >= 4) {
					UserNode user = new UserNode();
					user.setUser(Long.parseLong(split[0]));
					ItemNode item = new ItemNode(Long.parseLong(split[2]),
							Float.parseFloat(split[3]), 1L);
					user.addItem(item);
					// 应该 做成分层抽样算法
					if (Math.random() < this.trainRate) {
						addUser(Long.parseLong(split[1]), user, isCategory);
					} else {
						addUserTest(Long.parseLong(split[1]), user, isCategory);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void readContent() {
		readContent(contentFile, "utf-8", "\t");
	}

	/**
	 * 读取文件
	 * 
	 * @param filePath
	 * @param code
	 * @param regex
	 */
	public void readContent(String filePath, String code, String regex) {
		contentSimiMatrix = new HashMap<Long, long[]>();
		File file = new File(filePath);
		if (!file.exists()) {
			System.out.println("文件不存在:" + filePath);
			System.exit(1);
		}
		InputStreamReader read = null;
		try {
			read = new InputStreamReader(new FileInputStream(filePath), "utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		BufferedReader reader = new BufferedReader(read);
		String tempString = null;
		System.out.println("开始读取文件");
		// 一次读入一行，直到读入null为文件结束
		try {
			while ((tempString = reader.readLine()) != null) {
				// 用户，物品，喜好
				String[] split = tempString.split(regex);
				if (split.length <= 0) {
					continue;
				}
				long[] temp = new long[split.length - 1];
				// 读取id
				long idCode = Long.parseLong(split[0]);
				for (int i = 1; i < split.length; i++) {
					temp[i - 1] = Long.parseLong(split[i]);
				}
				contentSimiMatrix.put(idCode, temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 协同过滤执行程序
	 */
	public void run() {
		this.fileUser = new FileUtil2(System.getProperty("user.dir")
				+ "/data/userSimiary" + outIntoFile + ".txt", "utf-8");
		// Log.info("计算content相似度");
		// if(this.contentSimiFunc!=null)
		// {
		// readContent(contentFile,"utf-8","\t");
		// }
		// Log.info("结束");
		/**
		 * 
		 */
		for (int i = 0; i < userGroup.size(); i++) {
			// 添加过滤器
			Log.info("权重初始化");
			if (weight == null) {
				Log.info("没有设置权重");
			} else {
				weight.init(userGroup.get(i));
			}
			// 执行数据清洗
			userGroup.get(i).dataClearnFilter(dataClearnFilter);
			// 执行相似度计算及推荐
			// UserInfo userInfoTemp=userGroup.get(i);
			// System.out.println(userGroup.get(i).getCatgory()+"b:"+userGroup.get(i).size());
			run(userGroup.get(i));
			// System.out.println(userGroup.get(i).getCatgory()+"f:"+userGroup.get(i).size());
		}
		if (fileUitl != null) {
			fileUitl.close();
		}
	}

	/**
	 * 执行
	 * 
	 * @param userinfo
	 */
	public void run(UserInfo userInfo) {
		ArrayList<UserNode> users = userInfo.getUsers();
		// 计算用户之间物品相似度
		float sim = 0f;
		int i = 0;
		float simM = 0f;
		float meanM = 0f;
		// System.out.println(simM.length*this.simLimit);
		ArrayList<Float> simMatrixGroup = null;
		float[] weightPowerTemp=null;
		if(weightPower!=null)
			weightPower.get(userInfo.getCatgory());
		LinkedList<String> input = new LinkedList<String>();
		for (UserNode userNode : users) {
			int j = 0;
			simMatrixGroup = new ArrayList<Float>();
			//System.out.println("数量:" + users.size());
			ArrayList<Float> ftemp = new ArrayList<Float>();
			for (UserNode userNode2 : users) {

				if (i == j) {
					j++;
					continue;
				}
				// 添加用户相似度 信息
				if (contentSimiFunc != null) {
					if (this.isGetUp) {
						// 提供内容推荐的相似度只
						sim = contentSimiFunc.getSimilary(userNode, userNode2,
								this.contentSimiMatrix, weight,weightPowerTemp);
						// System.out.println(userNode.getUserId()+"\t"+userNode2.getUserId()+"\t"+fl);
					} else {
						// 其中不能为0;
						sim = 1 / contentSimiFunc.getSimilary(userNode,
								userNode2, this.contentSimiMatrix, weight,weightPowerTemp);
					}
				} else {
					sim = simiFunc.getSimilary(userNode, userNode2, weight);
				}
				ftemp.add(sim);
				//System.out.println(sim);
				if (this.isGetUp) {
					if (sim > this.simLimitValue) {
						meanM += sim;
						// meanM[j]+=sim;
						simMatrixGroup.add(sim);
						// userInfo.addUserSim(userNode.getUserId(),userNode2.getUserId(),sim);
						// simMatrixGroup.get(j).add(sim);
					}
				} else {
					if (sim < this.simLimitValue) {
						meanM += sim;
						// meanM[j]+=sim;
						simMatrixGroup.add(sim);
						// userInfo.addUserSim(userNode.getUserId(),userNode2.getUserId(),sim);
						// simMatrixGroup.get(j).add(sim);
					}
				}
				j++;
			}
			// 快速排序
			// Sort.quickSort(simMatrix,0,simMatrix.length-1, true);
			if (simMatrixGroup.size() > 0) {
				// System.out.println("排序:"+simMatrixGroup.get(i).size());
				meanM = meanM / simMatrixGroup.size();
				Iterator<Float> iter = simMatrixGroup.iterator();
			//	System.out.println("原:"+simMatrixGroup.size());
				if(simMatrixGroup.size()<this.simLimitRateCount)
				{
					
				}else{
				if (isGetUp) {
					while (iter.hasNext()) {
						if (iter.next() < meanM) {
							iter.remove();
						}
					}
				} else {
					while (iter.hasNext()) {
						if (iter.next() > meanM) {
							iter.remove();
						}
					}
				}
				}
				// 重写排序算法 使用冒泡排序获取前n个值算法
				// SortList.bubbleSort(list, limit);
				// /Collections.sort(simMatrixGroup.get(i));
				if (isGetUp) {
					int len = simMatrixGroup.size() - 1
							- (int) (simMatrixGroup.size() * this.simLimitRate);
					len = len > simLimitRateCount ? simLimitRateCount : len;
					SortList.bubbleSort(simMatrixGroup, len, true);
					try{
					simM = simMatrixGroup.get(len);
					}catch(Exception e)
					{
						//e.printStackTrace();
						//System.out.println(simMatrixGroup.size());
						//System.out.println("mean:"+meanM);
						simM=this.simLimitValue;
					}
				} else {
					int len = (int) (simMatrixGroup.size() * this.simLimitRate);
					len = len > simLimitRateCount ? simLimitRateCount : len;
					SortList.bubbleSort(simMatrixGroup, len, false);
					simM = simMatrixGroup.get(len);
				}
			//	System.out.println("过滤:" + simMatrixGroup.size() + "\t" + simM);
			}
			// 获取有效的邻域用户
			int jj = -1;
			int jjj=-1;
			for (UserNode userNode2 : users) {
				jj++;
				jjj++;
				if (jj == i) {
					jjj--;
					continue;
				}
			
				Float fll = ftemp.get(jjj);
				if (isGetUp) {
					if (fll > simM) {

					} else {
						continue;
					}
				} else {
					if (fll < simM) {

					} else {
						continue;
					}
				}
				userInfo.addUserSim(userNode.getUserId(),
						userNode2.getUserId(), fll);
				if (contentSimiFunc == null)
					itemRurl.getRecommendItems(userNode, userNode2, fll,this.contentSimiMatrix,null);
				else
				{
					//计算用户 和物品的匹配度
					itemRurl.getRecommendItems(userNode, userNode2, fll,this.contentSimiMatrix, weight);
				}

			}
			// 每次用户计算完成后 需要排序
			userNode.sortItem(this.isGetUp, recommanderFilter);
			// 打印
			// print(userNode);
			if (isCategory) {
				input.add("推荐" + "\t" + userInfo.getCatgory() + "\t"
						+ category.get(userInfo.getCatgory()) + "\t"
						+ printString(userNode));
			} else {
				input.add("推荐" + "\t" + printString(userNode));
				// System.out.println(printString(userNode));
			}
			if (isPrintFilterItem) {
				input.add("过滤" + "\t" + userInfo.getCatgory() + "\t"
						+ category.get(userInfo.getCatgory()) + "\t"
						+ printStringFilter(userNode));
			}
			i++;
			System.out.println(userInfo.getCatgory() + ":" + i);
			fileUitl.wirte(input, input.size());
		}
		if (isPrintUserSimilary) {
			LinkedList<String> str2 = null;
			if (isCategory) {
				str2 = printStringSim(userInfo);
			} else {
				str2 = printStringSimNOtCategory(userInfo);
			}
			fileUser.wirte(str2, str2.size());
		}
		System.out.println("计算结束");
	}

	public boolean isPrintFilterItem() {
		return isPrintFilterItem;
	}

	public void setPrintFilterItem(boolean isPrintFilterItem) {
		this.isPrintFilterItem = isPrintFilterItem;
	}

	/**
	 * 打印用户推荐的物品
	 * 
	 * @param usernode
	 */
	public void print(UserNode usernode) {
		// System.out.println("mall:"+usernode.getUserId()+"\t"+mall.get(usernode.getUserId()));
		// System.out.println("推荐shop为:");
		// int i=0;
		// for(ItemNode ite:usernode.getSortItem())
		// {
		// i++;
		// System.out.println("index:"+i+":shop："+ite.getItemId()+":"+shop.get(ite.getItemId())+"\tvalue:"+ite.getValue());
		// if(i==limitItem)
		// {
		// break;
		// }
		// }
		System.out.print(usernode.getUserId() + "\t"
				+ mall.get(usernode.getUserId()));
		int i = 0;
		for (ItemNode ite : usernode.getSortItem()) {
			i++;
			System.out.print(ite.getItemId() + CF.splitStr
					+ shop.get(ite.getItemId()) + CF.splitStr + ite.getValue()
					+ "\t");
			if (i == limitItem) {
				break;
			}
		}
	}

	/**
	 * 打印用户推荐的物品
	 * 
	 * @param usernode
	 */
	public String printString(UserNode usernode) {
		String str = "";
		if (isMall) {
			str += usernode.getUserId() + "\t" + mall.get(usernode.getUserId())
					+ "\t";
		} else {
			str += usernode.getUserId() + "\t" + shop.get(usernode.getUserId())
					+ "\t";
		}
		int i = 0;
		for (ItemNode ite : usernode.getSortItem()) {
			i++;
			if (i == 1) {
				if (isMall) {
					str += ite.getItemId() + CF.splitStr
							+ shop.get(ite.getItemId()) + CF.splitStr
							+ ite.getValue() + CF.splitStr + ite.getCount();
				} else {
					str += ite.getItemId() + CF.splitStr
							+ mall.get(ite.getItemId()) + CF.splitStr
							+ ite.getValue() + CF.splitStr + ite.getCount();
				}
			} else {
				if (isMall) {
					str += "\t" + ite.getItemId() + CF.splitStr
							+ shop.get(ite.getItemId()) + CF.splitStr
							+ ite.getValue() + CF.splitStr + ite.getCount();
				} else {
					str += "\t" + ite.getItemId() + CF.splitStr
							+ mall.get(ite.getItemId()) + CF.splitStr
							+ ite.getValue() + CF.splitStr + ite.getCount();
				}
			}
			if (i >= limitItem) {
				break;
			}
		}
		return str;
	}

	/**
	 * 打印用户推荐后的过滤物品
	 * 
	 * @param usernode
	 */
	public String printStringFilter(UserNode usernode) {
		// System.out.println("mall:"+usernode.getUserId()+"\t"+mall.get(usernode.getUserId()));
		// System.out.println("推荐shop为:");
		// int i=0;
		// for(ItemNode ite:usernode.getSortItem())
		// {
		// i++;
		// System.out.println("index:"+i+":shop："+ite.getItemId()+":"+shop.get(ite.getItemId())+"\tvalue:"+ite.getValue());
		// if(i==limitItem)
		// {
		// break;
		// }
		// }
		String str = "";
		if (isMall) {
			str += usernode.getUserId() + "\t" + mall.get(usernode.getUserId())
					+ "\t";
		} else {
			str += usernode.getUserId() + "\t" + shop.get(usernode.getUserId())
					+ "\t";
		}
		int i = 0;
		for (ItemNode ite : usernode.getFilterItem()) {
			i++;
			if (i == 1) {
				if (isMall) {
					str += ite.getItemId() + CF.splitStr
							+ shop.get(ite.getItemId()) + CF.splitStr
							+ ite.getValue() + CF.splitStr + ite.getCount();
				} else {
					// System.out.println(ite.getItemId()+":"+mall.get(ite.getItemId())+":"+ite.getValue()+":"+ite.getCount());
					str += ite.getItemId() + CF.splitStr
							+ mall.get(ite.getItemId()) + CF.splitStr
							+ ite.getValue() + CF.splitStr + ite.getCount();
				}
			} else {
				if (isMall) {
					str += "\t" + ite.getItemId() + CF.splitStr
							+ shop.get(ite.getItemId()) + CF.splitStr
							+ ite.getValue() + CF.splitStr + ite.getCount();
				} else {
					str += "\t" + ite.getItemId() + CF.splitStr
							+ mall.get(ite.getItemId()) + CF.splitStr
							+ ite.getValue() + CF.splitStr + ite.getCount();
				}
			}
			if (i >= limitItem) {
				break;
			}
		}
		return str;
	}

	public class UserVal implements Comparable<UserVal> {
		public long user = -1;
		public Float val = 0f;

		public UserVal(long user, Float val) {
			this.user = user;
			this.val = val;
		}

		public int compareTo(UserVal other) {
			return -Float.compare(val, other.val);
		}
	}

	/**
	 * 打印用户相似度
	 * 
	 * @param userInfo
	 * @return
	 */
	public LinkedList<String> printStringSim(UserInfo userInfo) {
		LinkedList<String> result = new LinkedList<String>();
		HashMap<Long, Integer> map = new HashMap<Long, Integer>();
		HashMap<Integer, Long> mapRev = new HashMap<Integer, Long>();
		int index = 0;
		ArrayList<ArrayList<UserVal>> mapVal = new ArrayList<ArrayList<UserVal>>();
		// 遍历用户相似度
		for (Entry<Long, Float> sim : userInfo.entrySetSimilary()) {
			long user1 = UserInfo.getU_U1(sim.getKey());
			long user2 = UserInfo.getU_U2(sim.getKey());
			Float val = sim.getValue();
			if (this.isGetUp) {
				if (val < 1E-10) {
					continue;
				}
			} else {

			}
			if (map.containsKey(user1)) {
				mapVal.get(map.get(user1)).add(new UserVal(user2, val));
			} else {
				map.put(user1, index);
				ArrayList<UserVal> temp = new ArrayList<UserVal>();
				temp.add(new UserVal(user2, val));
				mapVal.add(temp);
				mapRev.put(index, user1);
				index++;
			}
			if (map.containsKey(user2)) {
				mapVal.get(map.get(user2)).add(new UserVal(user1, val));
			} else {
				map.put(user2, index);
				ArrayList<UserVal> temp = new ArrayList<UserVal>();
				temp.add(new UserVal(user1, val));
				mapVal.add(temp);
				mapRev.put(index, user2);
				index++;
			}
		}
		// System.out.println(category.get(userInfo.getCatgory())+"\t"+mapVal.size());
		for (int i = 0; i < mapVal.size(); i++) {
			ArrayList<UserVal> temp2 = mapVal.get(i);
			int ll = 0;
			Collections.sort(temp2);
			String str = "";
			if (isMall) {
				str = category.get(userInfo.getCatgory()) + "\t"
						+ mall.get(mapRev.get(i)) + "\t";
				// System.out.println(mall.get(mapRev.get(i))+"\t");
				for (UserVal u : temp2) {
					ll++;
					if (ll == 1) {
						str += mall.get(u.user) + CF.splitStr + u.val;
						// System.out.print(mall.get(u.user)+":"+u.val);
					} else {
						str += "\t" + mall.get(u.user) + CF.splitStr + u.val;
						// System.out.print("\t"+mall.get(u.user)+":"+u.val);
					}
					if (ll >= printUserSimilaryLimit) {
						// System.out.println();
						break;
					}
				}
			} else {
				str = category.get(userInfo.getCatgory()) + "\t"
						+ shop.get(mapRev.get(i)) + "\t";
				// System.out.println(mall.get(mapRev.get(i))+"\t");
				for (UserVal u : temp2) {
					ll++;
					if (ll == 1) {
						str += u.user + CF.splitStr + shop.get(u.user)
								+ CF.splitStr + u.val;
						// System.out.println(u.user+"\t"+shop.get(u.user)+":"+u.val);
					} else {
						str += "\t" + u.user + CF.splitStr + shop.get(u.user)
								+ CF.splitStr + u.val;
						// System.out.print("\t"+mall.get(u.user)+":"+u.val);
					}
					if (ll >= printUserSimilaryLimit) {
						// System.out.println();
						break;
					}
				}
			}
			result.add(str);
		}
		return result;
	}

	/**
	 * 打印用户相似度 部分业态
	 * 
	 * @param userInfo
	 * @return
	 */
	public LinkedList<String> printStringSimNOtCategory(UserInfo userInfo) {
		LinkedList<String> result = new LinkedList<String>();
		HashMap<Long, Integer> map = new HashMap<Long, Integer>();
		HashMap<Integer, Long> mapRev = new HashMap<Integer, Long>();
		int index = 0;
		ArrayList<ArrayList<UserVal>> mapVal = new ArrayList<ArrayList<UserVal>>();
		// 遍历用户相似度
		for (Entry<Long, Float> sim : userInfo.entrySetSimilary()) {
			long user1 = UserInfo.getU_U1(sim.getKey());
			long user2 = UserInfo.getU_U2(sim.getKey());
			Float val = sim.getValue();
			if (this.isGetUp) {
				if (val < 1E-10) {
					continue;
				}
			} else {

			}
			if (map.containsKey(user1)) {
				mapVal.get(map.get(user1)).add(new UserVal(user2, val));
			} else {
				map.put(user1, index);
				ArrayList<UserVal> temp = new ArrayList<UserVal>();
				temp.add(new UserVal(user2, val));
				mapVal.add(temp);
				mapRev.put(index, user1);
				index++;
			}
			if (map.containsKey(user2)) {
				mapVal.get(map.get(user2)).add(new UserVal(user1, val));
			} else {
				map.put(user2, index);
				ArrayList<UserVal> temp = new ArrayList<UserVal>();
				temp.add(new UserVal(user1, val));
				mapVal.add(temp);
				mapRev.put(index, user2);
				index++;
			}
		}
		// System.out.println(category.get(userInfo.getCatgory())+"\t"+mapVal.size());
		for (int i = 0; i < mapVal.size(); i++) {

			ArrayList<UserVal> temp2 = mapVal.get(i);
			int ll = 0;
			Collections.sort(temp2);
			String str = "";
			if (isMall) {
				str = mapRev.get(i) + CF.splitStr + mall.get(mapRev.get(i))
						+ "\t";
				// System.out.println(mall.get(mapRev.get(i))+"\t");
				for (UserVal u : temp2) {
					ll++;
					if (ll == 1) {
						str += u.user + CF.splitStr + mall.get(u.user)
								+ CF.splitStr + u.val;
						// System.out.print(mall.get(u.user)+":"+u.val);
					} else {
						str += "\t" + u.user + CF.splitStr + mall.get(u.user)
								+ CF.splitStr + u.val;
						// System.out.print("\t"+mall.get(u.user)+":"+u.val);
					}

					if (ll >= printUserSimilaryLimit) {

						// System.out.println();
						break;
					}
				}

			} else {
				str = mapRev.get(i) + CF.splitStr + shop.get(mapRev.get(i))
						+ "\t";
				// System.out.println(mall.get(mapRev.get(i))+"\t");
				for (UserVal u : temp2) {
					ll++;
					if (ll == 1) {
						str += u.user + CF.splitStr + shop.get(u.user)
								+ CF.splitStr + u.val;
						// System.out.println(u.user+"\t"+shop.get(u.user)+":"+u.val);
					} else {
						str += "\t" + u.user + CF.splitStr + shop.get(u.user)
								+ CF.splitStr + u.val;
						// System.out.print("\t"+mall.get(u.user)+":"+u.val);
					}
					if (ll >= printUserSimilaryLimit) {
						// System.out.println();
						break;
					}
				}
			}
			result.add(str);
		}

		return result;
	}

	/**
	 * 估计当前数据集的评估效果
	 * 
	 * @return
	 */
	public void evalue() {
		// 计算覆盖率
		HashSet<Long> cover = new HashSet<Long>();
		for (int i = 0; i < this.userGroup.size(); i++) {
			// System.out.println("eval11:"+userGroup.get(i).size());
			for (UserNode user : userGroup.get(i).getUsers()) {
				int l = 0;
				for (ItemNode item : user.getSortItem()) {
					cover.add(item.getItemId());
					l++;
					if (l >= this.limitItem) {
						break;
					}
				}
			}
		}
		double coverValue = cover.size() * 1d / itemCount;

		for (int i = 0; i < this.userGroupTest.size(); i++) {
			UserInfo userInfo = userGroupTest.get(i);
			// System.out.println("eval:"+userInfo.size());
			// 获取对应分类
			int ii = -1;
			for (int j = 0; j < userGroup.size(); j++) {
				if (userGroupTest.get(i).getCatgory() == userGroup.get(j)
						.getCatgory()) {
					ii = j;
				}
			}
			if (ii == -1) {
				continue;
			}
			HashSet<Long> coverTemp = new HashSet<Long>();
			// 当前分类下所有商品数量
			for (UserNode userTest : userInfo.getUsers()) {
				for (ItemNode it : userTest.getItems()) {
					coverTemp.add(it.getItemId());
				}
			}
			for (UserNode userTrain : userGroup.get(ii).getUsers()) {
				for (ItemNode it : userTrain.getItems()) {
					coverTemp.add(it.getItemId());
				}
			}
			HashSet<Long> coverTemp2 = new HashSet<Long>();
			for (UserNode userTrain : userGroup.get(ii).getUsers()) {
				int ll = 0;
				for (ItemNode it : userTrain.getSortItem()) {
					ll++;
					coverTemp2.add(it.getItemId());
					if (ll >= this.limitItem) {
						break;
					}
				}
			}
			double pricesionSum = 0d;
			double recallSum = 0d;
			for (UserNode userTest : userInfo.getUsers()) {
				UserNode userTrain = userGroup.get(ii).getUserInfo()
						.get(userTest.getUserId());
				double pricision = getPrecision(userTest, userTrain);
				double recall = getRecall(userTest, userTrain);
				double fValue = getFValue(pricision, recall);
				if (pricision < 0 || recall < 0) {
					continue;
				}
				pricesionSum += pricision;
				recallSum += recall;
				// System.out.println("分类:"+userInfo.getCatgory()+" "+s(category.get(userInfo.getCatgory()),8,true)+":用户:"+s(userTest.getUserId(),5,true)+s(mall.get(userTest.getUserId()),20,true)+" 查准率:"+
				// s(pricision,6,true)+" 查全率:"+s(recall,6,true)+" fValue:"+s(recall,4,true)+" testCount:"+s(userTest.size(),4,true)+"trainCount:"+s(userTrain.size(),4,true));
			}
			pricesionSum /= userInfo.size() == 0 ? 1 : userInfo.size();
			recallSum /= userInfo.size() == 0 ? 1 : userInfo.size();
			double coverValueTemp = coverTemp2.size() * 1d / coverTemp.size();
			// System.out.println("分类:"+userInfo.getCatgory()+" "+s(category.get(userInfo.getCatgory()),8,true)+" 分类覆盖率:"+s(coverValueTemp,5,true)+
			// "平均查准率:"+s(pricesionSum,5,true)+"平均查全率:"+s(recallSum,5,true));
			if (isCategory) {
				System.out.println(s(category.get(userInfo.getCatgory()), 8,
						true)
						+ s(coverValueTemp, 5, true)
						+ s(pricesionSum, 5, true)
						+ s(recallSum, 5, true)
						+ s(userInfo.size(), 5, true));
			} else {
				System.out.println(s(coverValueTemp, 5, true)
						+ s(pricesionSum, 5, true) + s(recallSum, 5, true)
						+ s(userInfo.size(), 5, true));
			}
		}
		System.out.println("覆盖率：" + coverValue);
	}

	private String stLN = "                                  ";

	public String s(String str, int len, boolean flag) {
		if (flag) {
			String temp = str;
			if (temp.length() > len) {
				return temp.substring(0, len) + "\t";
			} else {
				return temp + stLN.substring(0, len - temp.length() + 1) + "\t";
			}
		} else {
			String temp = str;
			if (temp.length() > len) {
				return temp.substring(0, len);
			} else {
				return stLN.substring(0, len - temp.length()) + temp;
			}
		}
	}

	public String s(long s, int len, boolean flag) {
		return s(Long.toString(s), len, flag);
	}

	public String s(float s, int len, boolean flag) {
		return s(Float.toString(s), len, flag);
	}

	public String s(int s, int len, boolean flag) {
		return s(Integer.toString(s), len, flag);
	}

	public String s(double s, int len, boolean flag) {
		return s(Double.toString(s), len, flag);
	}

	/**
	 * 查准率
	 * 
	 * @param userTest
	 *            测试集
	 * @param userTrain
	 *            训练集
	 * @return 如果 <0 则表示 数据集中存在一个不存在的信息
	 */
	public double getPrecision(UserNode userTest, UserNode userTrain) {
		if (userTest.size() == 0 || userTrain == null
				|| userTrain.getSortItem().size() == 0) {
			return -1;
		}
		int i = 0;
		int okCount = 0;
		// System.out.println("mall:"+mall.get(userTest.getUserId())+"\t正确shop");
		for (ItemNode train : userTrain.getSortItem()) {
			i++;
			for (ItemNode test : userTest.getItems()) {
				if (train.getItemId() == test.getItemId()) {
					// System.out.print(shop.get(train.getItemId())+"\t");
					okCount++;
				}
			}
			if (i == limitItem) {
				break;
			}
		}
		// if(i>0)
		// System.out.println();
		// System.out.println(okCount+"\t"+i+"\t"+userTest.size());
		//标准统计
		//return okCount*1.0/(limitItem);
		//统计方法2
		return okCount * 1.0 / (i > userTest.size() ? userTest.size() : i);

	}

	/**
	 * 查全率
	 * 
	 * @param userTest
	 *            测试集
	 * @param userTrain
	 *            训练集
	 * @return 如果 <0 则表示 数据集中存在一个不存在的信息
	 */
	public double getRecall(UserNode userTest, UserNode userTrain) {
		if (userTest.size() == 0 || userTrain == null
				|| userTrain.getSortItem().size() == 0) {
			return -1;
		}
		int i = 0;
		int okCount = 0;
		// 打印 mall 对应的推荐正确的 shop
		// System.out.println("mall:"+mall.get(userTest.getUserId())+"\t正确shop");
		for (ItemNode train : userTrain.getSortItem()) {
			i++;
			for (ItemNode test : userTest.getItems()) {
				if (train.getItemId() == test.getItemId()) {
					// System.out.print(shop.get(train.getItemId())+"\t");
					okCount++;
				}
			}
			if (i == limitItem) {
				break;
			}
		}
		// if(i>0)
		// System.out.println();
		// System.out.println(okCount+"\t"+i+"\t"+userTest.size());
		return okCount * 1.0 / userTest.size();
	}

	/**
	 * 计算F值
	 * 
	 * @param precision
	 * @param recall
	 * @return
	 */
	public double getFValue(double precision, double recall) {
		return precision * recall * 2 / (precision + recall);
	}

	/**
	 * F得分 和计算F值类似 当a=1f值时为 f值
	 * 
	 * @return
	 */
	public double getFMeasure(double precision, double recall, float a) {
		// float a=1f;
		return (Math.pow(a, 2f) + 1) * precision * recall
				/ (precision * recall);
	}

	/**
	 * 计算查准率P和查全率R的加权平均值 当其中一个为0时，E值为1 b越大，表示查准率的权重越大
	 * 
	 * @param pricision
	 * @param recall
	 * @param b
	 * @return
	 */
	public double getE(double pricision, double recall, float b) {
		return 1 - (1 + Math.pow(b, 2f))
				/ (Math.pow(b, 2f) / pricision + 1 / recall);
	}
	
	/**
	 * 读取权重信息表
	 * @param file
	 * @param code
	 * @return
	 */
	public static HashMap<Long,float[]>  readFileWeight(String file,String code)
	{
		
		HashMap<Long,float[]> weight=new HashMap<Long,float[]>();
		FileUtil2 fileUtil=new FileUtil2(file,code);
		LinkedList<String> strs=fileUtil.readAndClose();
		for(String str:strs)
		{
			String[] temp=str.split("\t");
			float[] fTemp=new float[temp.length-1];
			for(int i=1;i<temp.length;i++)
			{
				fTemp[i]=Float.parseFloat(temp[i]);
			}
			weight.put(Long.parseLong(temp[0]),fTemp);
		}
		return weight;
	}
	

	public static void main2(String[] args) throws SQLException {
		MysqlConnection mysql = new MysqlConnection(
				"jdbc:mysql://192.168.1.4:3306/zjMysql", "root", "root");

		// String
		// sql="select a.*,b.CommentCount,b.CommentStart,c.CategoryName from MallAndShop as a "+
		// "left join MallAndCommentCount as b "+
		// "on a.MallName=b.MallName left join MallCategory as c "+
		// "on a.CategoryCode=c.CategoryCode order by MallID,ShopID";
		String sql = "select ShopGroupID,ShopGroupName from ShopGroupIdAndName2";
		// 存储mall信息
		HashMap<Long, String> mall = new HashMap<Long, String>();
		HashMap<Long, String> shop = new HashMap<Long, String>();
		HashMap<Long, String> category = new HashMap<Long, String>();
		MysqlSelect myse = mysql.sqlSelect(sql);
		ResultSet result = myse.resultSet;
		while (result.next()) {
			Long groupId = result.getLong(1);
			String groupName = result.getString(2);
			shop.put(groupId, groupName);
		}
		result.close();
		sql = "select shopId,case when branchName is null or branchName =\"\" then shopName else CONCAT(shopName,\"(\",branchName,\")\") end from MallInfoDianping ";
		myse = mysql.sqlSelect(sql);
		result = myse.resultSet;
		while (result.next()) {
			Long groupId = result.getLong(1);
			String groupName = result.getString(2);
			mall.put(groupId, groupName);
		}
		sql = "select CategoryCode,Categoryname from MallCategory";
		myse = mysql.sqlSelect(sql);
		result = myse.resultSet;
		while (result.next()) {
			Long groupId = result.getLong(1);
			String groupName = result.getString(2);
			category.put(groupId, groupName);
		}

		// FastByIDMap<PreferenceArray> preferences =new
		// FastByIDMap<PreferenceArray>();

		// LinkedList<Long> ma=new LinkedList<Long>();
		// ArrayList<Long> shop_id=new ArrayList<Long>();
		// long mall_id=-1;
		// try{
		// while(myse.resultSet.next())
		// {
		// //System.out.println("存在");
		// String shopName=myse.resultSet.getString(3).trim();
		// String mallName=myse.resultSet.getString(5).trim();
		// long mallID=myse.resultSet.getInt(6);
		// long shopID=myse.resultSet.getInt(7);
		// long commentCount=myse.resultSet.getInt(8);
		// long cate=myse.resultSet.getLong(4);
		// String
		// CategoryName=myse.resultSet.getString(10).trim().split("\r\n")[0];
		// category.put(cate, CategoryName);
		// if(mall_id==-1)
		// {
		// mall.put(mallID, mallName);
		// shop.put(shopID, shopName);
		// ma.add(mallID);
		// }
		// if(mall_id!=-1 && mallID!=mall_id)
		// {
		// PreferenceArray prefsForUser=new
		// GenericUserPreferenceArray(shop_id.size());
		// prefsForUser.setUserID(0,mallID);
		// int i=-1;
		// for(Long il:shop_id)
		// {
		// i++;
		// prefsForUser.setItemID(i,il);
		// prefsForUser.setValue(i,1.0f);
		// }
		// preferences.put(mallID, prefsForUser);
		// mall_id=mallID;
		// mall.put(mallID, mallName);
		// shop.put(shopID, shopName);
		// shop_id=new ArrayList<Long>();
		// }else{
		// mall_id=mallID;
		// shop.put(shopID, shopName);
		// ma.add(mallID);
		// shop_id.add(shopID);
		// }
		// //加入myhout中
		// }
		// }catch(Exception e)
		// {
		// e.printStackTrace();
		// System.exit(1);
		// }

		// 执行cos 相关系数
		//CF cf = new CF(400, 0.7f, 0.9f, mall, shop, category, "余弦10Test", true,false);
		 CF cf=new CF(50,0.3f,0.9f,mall,shop,category,"余弦10",true,true);//计算全局
		cf.setSimilaryFunc(new CosinSimilary());
		// cf.readFile(System.getProperty("user.dir")+"/data/mallAndShopAndCategory.txt","utf-8",",");

		// jarcard 相关系数
		// CF cf=new CF(50,0.1f,0.9f,mall,shop,category,"jarcard50",true);
		// cf.setSimilaryFunc(new JarcardSimilary());

		// cf.readFile(System.getProperty("user.dir")+"/data/mallAndShopAndCategory.txt","utf-8",",");
		// 执行pearson 相关系数
		// CF cf=new CF(50,0.1f,0.9f,mall,shop,category,"pearson50",true);
		// cf.setSimilaryFunc(new PearsonSimilary());
		// cf.readFile(System.getProperty("user.dir")+"/data/mallAndShopAndCategory.txt","utf-8",",");
		// 曼哈顿距离
		// CF cf=new CF(30,0.1f,0.9f,mall,shop,category,"spearman",false);
		// cf.setSimilaryFunc(new CityBlockSimilary());
		// cf.SetGetUp(false);
		// cf.setSimLimitValue(300);
		// //cf.readFile(System.getProperty("user.dir")+"/data/mallAndShopAndCategory.txt","utf-8",",");
		// cf.readFile(System.getProperty("user.dir")+"/data/shopAndMallAndCategory.txt","utf-8",",");
		// spearman
		// CF cf=new CF(30,0.1f,0.9f,mall,shop,category,"斯皮尔曼item",false);
		// cf.setSimilaryFunc(new SpearmanSimilary());
		// cf.setSimLimitValue(0.7f);
		// //cf.readFile(System.getProperty("user.dir")+"/data/mallAndShopAndCategory.txt","utf-8",",");
		// cf.readFile(System.getProperty("user.dir")+"/data/shopAndMallAndCategory.txt","utf-8",",");
		// 似然函数
		// 12595
		// CF cf=new CF(50,0.1f,0.9f,mall,shop,category,"似然50",true);
		// // cf.setSimilaryFunc(new LogLikelihoodRatioSimilary(12595));
		// cf.setSimilaryFunc(new LogLikelihoodRatioSimilary());

		cf.readFile(System.getProperty("user.dir")
				+ "/data/mallAndShopAndCategory2.txt", "utf-8", "\t");
		// cf.readFile(System.getProperty("user.dir")+"/data/shopAndMallAndCategory.txt","utf-8",",");
		// cf.readFile(System.getProperty("user.dir")+"/data/shopAndMallAndCategory.txt","utf-8",",");
		// cf.addDataClearnFilter(new
		// RecommanderHostItemCountFilter(cf,CFParamStatic.recommanderHotlteFilter,1));
		cf.setPrintUserSimilaryLimit(10);
		cf.setWeightUtil(new UserHotItemLoss());
		cf.setPrintFilterItem(false);
		cf.setItemGetRurl(new ItemGetCel());
		cf.setPrintUserSimilary(true);
		cf.addRecommanderFilter(new RecommanderHostItemRateFilter(cf,
				CFParamStatic.recommanderHotGteFilter, 0.5f));
		cf.addRecommanderFilter(new RecommanderHostItemCountFilter(cf,
				CFParamStatic.recommanderHotGteFilter, 20));
		cf.addRecommanderFilter(new RecommanderHostItemCountFilter(cf,
				CFParamStatic.recommanderHotlteFilter, 1));
		cf.run();
		cf.evalue();
	}
	


	public static void main(String[] args) {

		// 初始化数据
		DataFillUtil util = new FillMethod1();
		//添加获取的feather
		//使用的变量
		HashSet<Integer> feather=new HashSet<Integer>();
		feather.add(3);//prevWeeklyHits
		feather.add(4);//weeklyHits
		feather.add(5);//monthlyHits
		feather.add(6);//hits
		//feather.add(7);//avgPrice
		feather.add(8);//shoppower
		feather.add(9);//popularity
		//feather.add(10);//power
		//feather.add(12);//score
		//使用离散化的变量
		HashMap<Integer,Integer> cluster=new HashMap<Integer,Integer>();
		cluster.put(3,3);//prevWeeklyHits
		cluster.put(4,3);//weeklyHits
		cluster.put(5,3);//monthlyHits
		cluster.put(6,3);//hitsS
//		cluster.put(7,3);//avgPrice
//		cluster.put(8,3);//shoppower
//		cluster.put(9,3);//popularity
//		cluster.put(10,3);//power
//		cluster.put(12,3);//score
		
//		/**
//		 * 权重信息表
//		 *如果为不分业态 则long值为1
//		 */
//		HashMap<Long,float[]> weight=readFileWeight(System.getProperty("user.dir")+"/data/weight.txt","utf-8");
//		
		util.run(feather,cluster);
		//添加交叉数据集
		{
		ArrayList<int[]> mapIndex=new ArrayList<int[]>();
		ArrayList<int[]> mapIndex2=new ArrayList<int[]>();
		int index=0;
		int index2=1;
		ArrayList<Integer> rel=new ArrayList<Integer>();
		Integer otherRel=0;
		mapIndex.add(new int[]{2,3});
		mapIndex2.add(new int[]{3});
		rel.add(3);
		mapIndex.add(new int[]{2,1});
		mapIndex2.add(new int[]{2});
		rel.add(2);
		otherRel=1;
		util.runCoss(index, index2, mapIndex, mapIndex2, rel, otherRel);
		}
		//添加新字段点击字段
		util.setChange(new PreHitAndNowHitChange());
		util.addOne();
		util.setChange(new ValueAddChange(){
			@Override
			public int[] change(float[] fval,int[] fint) {
				// TODO Auto-generated method stub
				return null;//(int)(fval[0]*3);
			}
		});
		util.addOne();
		util.setChange(new ValueAddChange(){
			@Override
			public int[] change(float[] fval,int[] fint) {
				// TODO Auto-generated method stub
				return null;//(int)(fval[1]*3);
			}
		});
		util.addOne();
		util.setChange(new ValueAddChange(){
			@Override
			public int[] change(float[] fval,int[] fint) {
				// TODO Auto-generated method stub
				return null;//(int)(fval[2]*3);
			}
		});
		util.addOne();
		util.setChange(new ValueAddChange(){
			@Override
			public int[] change(float[] fval,int[] fint) {
				// TODO Auto-generated method stub
				return null;//(int)(fval[3]*3);
			}
		});
		util.addOne();
		//打印离散化后数据岛 centoentoFile中
		util.printS(System.getProperty("user.dir") + "/data/contentFile.txt");

		// 存储mall信息
		HashMap<Long, String> mall = util.mall;
		HashMap<Long, String> shop = util.brand;
		HashMap<Long, String> category = util.category;

		CF cf = new CF(20, 0.1f, 0.7f, mall, shop, category,
				"余弦10Itemcontent", false, true);// 计算全局
		cf.setSimilaryFunc(new CosinSimilary());
	//	cf.setWeightPower(weight);//设置内容的权重
		// cf.readFile(System.getProperty("user.dir")+"/data/mallAndShopAndCategory2.txt","utf-8","\t");
		cf.readFile(System.getProperty("user.dir")
				+ "/data/shopAndMallAndCategory2.txt", "utf-8", "\t");
		cf.setContentSimiFunc(new MathchHard());
		cf.setContentFile(System.getProperty("user.dir")
				+ "/data/contentFile.txt");
		cf.readContent();
		util = null;
		cf.setSimLimitValue(0.15f);
		cf.setPrintUserSimilaryLimit(10);
		cf.setWeightUtil(new UserHotItemLoss());
		cf.setPrintFilterItem(false);
		cf.setItemGetRurl(new ItemGetCel());
		cf.setPrintUserSimilary(true);
//		cf.addRecommanderFilter(new RecommanderHostItemRateFilter(cf,
//				CFParamStatic.recommanderHotGteFilter, 0.5f));
		cf.addRecommanderFilter(new RecommanderHostItemCountFilter(cf,
				CFParamStatic.recommanderHotGteFilter,50));
		cf.addRecommanderFilter(new RecommanderHostItemCountFilter(cf,
				CFParamStatic.recommanderHotlteFilter, 1));
		cf.run();
		cf.evalue();
	}
}
