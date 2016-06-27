package com.intoMongo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import com.etl.FileUtil2;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.util.JsonUtil;
import com.util.MongoDb;
import com.zj.bean.Company51JobBean;
import com.zj.bean.Company51JobCompany2Bean;
import com.zj.bean.Company51JobCompanyBean;
import com.zj.bean.Company51JobDescBean;
import com.zj.bean.Company51JobPositionBean;
import com.zj.bean.Company51PositionSonBean;
import com.zj.bean.Company51jobPosition2Bean;
import com.zj.bean.FangBean;
import com.zj.bean.FangMonthCountBean;

/**
 * 从文件放入mongodb中
 * 
 * @author Administrator
 * 
 */
public class IntoMongoDB {

	private static Logger log = Logger.getLogger(IntoMongoDB.class);
	

	/**
	 * 从文件写入mongodb中
	 * 
	 * @param fileName
	 * @param ip
	 * @param port
	 * @param database
	 * @param collection
	 * @param printCount
	 *            文件打印行/每
	 */
	public static void readWriteToMongod(String fileName, String ip, int port,
			String database, String collection, int printCount, String regex) {
		MongoDb mongo = new MongoDb(ip, port, database);
		TreeSet<String> tree = new TreeSet<String>();
		File file = new File(fileName);
		if (!file.exists()) {
			log.error("文件不存在:" + file.getAbsolutePath());
			System.exit(1);
		}
		log.info("开始读取文件:" + file.getAbsolutePath());
		BufferedReader reader = null;
		int i = 0;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			reader = new BufferedReader(read);
			String tempString = "";
			while ((tempString = reader.readLine()) != null) {
				i++;
				if (i % printCount == 0) {
					log.info("读取文件行:" + i);
				}
				if (tempString.length() < 10) {
					continue;
				}
				if (regex.equals("fangCode")) {
					Pattern p_1 = Pattern.compile("\"" + regex
							+ "\"[^\"]*?\"([^\"]*?)\"");
					Matcher m_1 = p_1.matcher(tempString);
					if (m_1.find()) {
						// log.info(m_1.group(1));
						if (tree.contains(m_1.group(1))) {

						} else {
							tree.add(m_1.group(1));
							mongo.insert(collection, tempString);
						}
					}
				} else {
					mongo.insert(collection, tempString);
				}
				// tree.add();
				// mongo.insert(collection, tempString);
			}
			read.close();
		} catch (Exception e) {

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 搜房数据 从文件写入mongodb中 不存在则插入存在则更新
	 * 
	 * @param fileName
	 * @param ip
	 * @param port
	 * @param database
	 * @param collection
	 * @param printCount
	 *            文件打印行/每
	 */
	public static void readWriteToMongodUpdateFang(String fileName, String ip,
			int port, String database, String collection, int printCount,
			String regex) {
		MongoDb mongo = new MongoDb(ip, port, database);
		TreeSet<String> tree = new TreeSet<String>();
		File file = new File(fileName);
		if (!file.exists()) {
			log.error("文件不存在:" + file.getAbsolutePath());
			System.exit(1);
		}
		log.info("开始读取文件:" + file.getAbsolutePath());
		BufferedReader reader = null;
		regex = "fangCode";
		int i = 0;
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("priceTrendValue", FangMonthCountBean.class);
		map.put("hireTrendValue", FangMonthCountBean.class);
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			reader = new BufferedReader(read);
			String tempString = "";
			while ((tempString = reader.readLine()) != null) {
				i++;
				if (i % printCount == 0) {
					log.info("读取文件行:" + i);
				}
				if (tempString.length() < 10) {
					continue;
				}
				if (regex.equals("fangCode")) {
					Pattern p_1 = Pattern
							.compile("\"fangCode\"[^\"]*?\"([^\"]*?)\"");
					Matcher m_1 = p_1.matcher(tempString);
					if (m_1.find()) {
						log.info(m_1.group(1));
						if (tree.contains(m_1.group(1))) {

						} else {
							tree.add(m_1.group(1));
							FangBean fang = (FangBean) JsonUtil
									.getDtoFromJsonObjStr(tempString,
											FangBean.class, map);
							// 查询mongo数据
							BasicDBObject doc = new BasicDBObject();
							doc.put("fangCode", fang.getFangCode());
							DBCursor cursor = mongo.find(collection, doc);
							boolean flag = false;
							while (cursor.hasNext()) {
								flag = true;
								try {
									BasicDBObject bdbObj = (BasicDBObject) cursor
											.next();
									if (bdbObj != null) {
										List<FangMonthCountBean> priceTrendValue = (LinkedList<FangMonthCountBean>) JsonUtil
												.getListFromJsonArrStrLinked(
														bdbObj.getString("priceTrendValue"),
														FangMonthCountBean.class);
										List<FangMonthCountBean> hireTrendValue = (LinkedList<FangMonthCountBean>) JsonUtil
												.getListFromJsonArrStrLinked(
														bdbObj.getString("hireTrendValue"),
														FangMonthCountBean.class);

										BasicDBObject doc2 = new BasicDBObject();
										doc2.put("hirePrice", fang
												.getFangListc().getHirePrice());
										doc2.put("hirePriceCategory", fang
												.getFangListc()
												.getHirePriceCategory());
										doc2.put("photoAlbum", fang
												.getFangListc().getPhotoAlbum());
										doc2.put("phoneAlbumUrl", fang
												.getFangListc()
												.getPhotoAlbumUrl());
										doc2.put("saleHouseSource", fang
												.getFangListc()
												.getSaleHouseSource());
										doc2.put("salePrice", fang
												.getFangListc().getSalePrice());
										doc2.put("salePriceCategory", fang
												.getFangListc()
												.getSalePriceCategory());
										doc2.put("tenementFee", fang
												.getFangListc()
												.getTenementFee());
										doc2.put("tenementFeeCategory", fang
												.getFangListc()
												.getTenementFeeCategory());
										BasicDBObject doc3 = new BasicDBObject();
										doc3.put("fangListc", doc2);
										doc3.put("approveOwner",
												fang.getApproveOwner());
										doc3.put("currentMonthHirePrice",
												fang.getCurrentMonthHirePrice());
										doc3.put(
												"currentMonthHirePriceCategory",
												fang.getCurrentMonthHirePriceCategory());
										doc3.put("currentMonthSalePrice",
												fang.getCurrentMonthSalePrice());
										doc3.put(
												"currentMonthSalePriceCategory",
												fang.getCurrentMonthSalePriceCategory());
										doc3.put("saleCount",
												fang.getSaleCount());
										doc3.put("scanCount",
												fang.getScanCount());
										doc3.put("receiveRate",
												fang.getReceiveRate());
										doc3.put("tenementFee",
												fang.getTenementFee());
										doc3.put("tenementFeeCategory",
												fang.getTenementFeeCategory());
										// 修改 价格趋势
										List<FangMonthCountBean> price = fang
												.getPriceTrendValue();
										List<FangMonthCountBean> hire = fang
												.getHireTrendValue();
										for (FangMonthCountBean be : price) {
											boolean flag2 = false;
											for (FangMonthCountBean beS : priceTrendValue) {
												if (be.getMonth().equals(
														beS.getMonth())) {
													flag2 = true;
												}
											}
											if (!flag2) {
												priceTrendValue.add(be);
											}
										}
										for (FangMonthCountBean be : hire) {
											boolean flag2 = false;
											for (FangMonthCountBean beS : hireTrendValue) {
												if (be.getMonth().equals(
														beS.getMonth())) {
													flag2 = true;
												}
											}
											if (!flag2) {
												hireTrendValue.add(be);
											}
										}
										List<BasicDBObject> pri = new LinkedList<BasicDBObject>();
										for (FangMonthCountBean beS : priceTrendValue) {
											BasicDBObject ob = new BasicDBObject();
											ob.put("month", beS.getMonth());
											ob.put("money", beS.getMoney());
											pri.add(ob);
										}
										doc3.put("priceTrendValue", pri);
										List<BasicDBObject> hir = new LinkedList<BasicDBObject>();
										for (FangMonthCountBean beS : hireTrendValue) {
											BasicDBObject ob = new BasicDBObject();
											ob.put("month", beS.getMonth());
											ob.put("money", beS.getMoney());
											hir.add(ob);
										}
										doc3.put("hireTrendValue", hir);
										// BasicDBObject doc4=new
										// BasicDBObject("$set",doc3);
										mongo.update(
												collection,
												doc,
												new BasicDBObject("$set", doc3),
												true, false);
									}
								} catch (Exception e) {
									log.error(e);
									e.printStackTrace();
								}
							}
							if (!flag) {// 如果不存在则直接插入
								mongo.insert(collection, tempString);
							}
						}
					}
				}
				// tree.add();
				// mongo.insert(collection, tempString);
			}
			read.close();
		} catch (Exception e) {

		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
	}

	/**
	 * 增量更新 51job 公司指纹信息入mongo中 从文件写入mongodb中
	 * 
	 * @param fileName
	 *            公司文件
	 * @param fileName2
	 *            职位详情文件
	 * @param fileName3
	 *            职位文件
	 * @param ip
	 * @param port
	 * @param database
	 * @param collection
	 * @param printCount
	 *            文件打印行/每
	 */
	public static void readWriteToMongodJobCompanyUpdate(String fileName,
			String fileName2, String fileName3, String ip, int port,
			String database, String collection, int printCount,int limit) {
		HashMap<String, Object> m = new HashMap<String, Object>();
		m.put("jobInfoList", Company51PositionSonBean.class);
		MongoDb mongo = new MongoDb(ip, port, database);
		File file = new File(fileName);
		File file2 = new File(fileName2);
		File file3 = new File(fileName3);
		if (!file.exists()) {
			log.error("文件不存在:" + file.getAbsolutePath());
		}
		if (!file2.exists()) {
			log.error("文件不存在:" + file2.getAbsolutePath());
		}
		if (!file3.exists()) {
			log.error("文件不存在:" + file3.getAbsolutePath());
		}
		log.info("开始读取公司文件:" + file.getAbsolutePath());
		BufferedReader reader = null;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			reader = new BufferedReader(read);
			String tempString = null;
			log.info("开始读取文件");
			// 一次读入一行，直到读入null为文件结束
			int i = 0;
			while ((tempString = reader.readLine()) != null) {
				i++;
				if (i % printCount == 0) {
					log.info("读取文件数量:" + i);
				}
				if (tempString.length() == 0) {
					continue;
				}
				// 判断是否重复
				Company51JobCompanyBean com = (Company51JobCompanyBean) JsonUtil
						.getDtoFromJsonObjStr(tempString,
								Company51JobCompanyBean.class);
				Company51JobCompany2Bean company = new Company51JobCompany2Bean();
				company.setAddress(com.getAddress());
				company.setCompanyCategory(com.getCompanyCategory());
				company.setCompanyCode(com.getCompanyCode());
				company.setCompanyMemberNum(com.getCompanyMemberNum());
				company.setCompanyName(com.getCompanyName());
				company.setCompanyUrl(com.getCompanyUrl());
				company.setDesc(com.getDesc());
				company.setFansCount(com.getFansCount());
				company.setIndustryCategory(com.getIndustryCategory());
				company.setLocation(com.getLocation());
				company.setPostcode(com.getPostcode());
				// 获取公司信息
				BasicDBObject doc = new BasicDBObject();
				doc.put("companyCode", company.getCompanyCode());
				boolean flag = false;
				try {
					DBCursor cursor = mongo.find(collection, doc);
					while (cursor.hasNext()) {
						flag = true;
						break;
					}
				} catch (Exception e) {
					e.printStackTrace();
					log.error(e);
					System.exit(1);
				}
				if (!flag) {
					mongo.insert(collection, JsonUtil.getJsonStr(company));
				} else {
					// log.info("增量数据更新");
					BasicDBObject doc3 = new BasicDBObject();
					doc3.put("address", company.getAddress());
					doc3.put("companyCategory", company.getCompanyCategory());
					doc3.put("companyMemberNum", company.getCompanyMemberNum());
					doc3.put("companyName", company.getCompanyName());
					doc3.put("desc", company.getDesc());
					doc3.put("fansCount", company.getFansCount());
					doc3.put("industryCategory", company.getIndustryCategory());
					doc3.put("postcode", company.getPostcode());
					BasicDBObject location = new BasicDBObject();
					if (company.getLocation() == null) {
						log.error("不存在poi数据:" + company.getCompanyCode());
						continue;
					} else if (company.getLocation().getLat().equals("")) {
						log.error("不存在poi数据:" + company.getCompanyCode());
						continue;
					} else if (company.getLocation().getLng().equals("")) {
						log.error("不存在poi数据" + company.getCompanyCode());
						continue;
					} else {
						location.put("lat", company.getLocation().getLat());
						location.put("lng", company.getLocation().getLng());
						doc3.put("location", location);
					}
					try {
						mongo.update(collection, doc, new BasicDBObject("$set",
								doc3), true, false);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		// 读取 job详情更新文件
		reader = null;
		try {
			// 用于存储已经使用过的公司code
			HashSet<Long> companySet = new HashSet<Long>();
			int size_new = 0;
			int size_old = -1;
			while (size_new - size_old != 0) {
				log.info("size_old:"+size_old+"\t"+size_new);
				Thread.sleep(1000);
				// 当存在新增时则不跳出
				// 否则跳出
				HashSet<Long> companyTempSet=new HashSet<Long>();
				HashSet<Long> notCompanySet=new HashSet<Long>();
				HashMap<Long, List<Company51jobPosition2Bean>> returnMap = new HashMap<Long, List<Company51jobPosition2Bean>>();
				size_old = size_new;
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file2), "utf-8");
				reader = new BufferedReader(read);
				String tempString = null;
				log.info("开始读取职位详情文件" + file2.getAbsolutePath());
				// 一次读入一行，直到读入null为文件结束
				int i = 0;
				while ((tempString = reader.readLine()) != null) {
					i++;
					if (i % printCount == 0) {
						log.info("读取文件数量:" + i);
					}
					if (tempString.length() == 0) {
						continue;
					}
					// 判断是否重复
					Company51JobDescBean jo = (Company51JobDescBean) JsonUtil
							.getDtoFromJsonObjStr(tempString,
									Company51JobDescBean.class);
					if (companySet.contains(jo.getCompanyCode())) {
					} else {
						// 需要判断是否超过最大值
						if (companySet.size() - size_old > limit) {
							continue;
						} else {
							// 添加新内容
							companyTempSet.add(jo.getCompanyCode());
							companySet.add(jo.getCompanyCode());
						}
					}
					if(!companyTempSet.contains(jo.getCompanyCode()))
					{
						//如果不属于该次循环的code内容则跳过
						continue;
					}
					if(notCompanySet.contains(jo.getCompanyCode()))
					{
						//如果是不存在code的
						continue;
					}
					Company51jobPosition2Bean job = new Company51jobPosition2Bean();
					job.setJobCode(jo.getJobCode());
					job.setPositionUrl(jo.getPositionUrl());
					job.setPositonName(jo.getPositonName());
					job.setEducationBackground(jo.getEducationBackground());
					job.setMonthlyply(jo.getMonthlyply());
					job.setPositionDesc(jo.getPositionDesc());
					job.setPositionFunction(jo.getPositionFunction());
					job.setPositionTag(jo.getPositionTag());
					job.setTrunkDate(jo.getTrunkDate());
					job.setWorkAddress(jo.getWorkAddress());
					job.setYearsOfWorking(jo.getYearsOfWorking());
					Company51PositionSonBean son = new Company51PositionSonBean();
					son.setMemberCount(jo.getMemberCount());
					son.setPublishDate(jo.getPublishDate());
					job.addJobInfoList(son);
					// 获取公司信息
					String jobs = "";
					List<Company51jobPosition2Bean> jobsInfo = returnMap.get(jo
							.getCompanyCode());
					if (jobsInfo == null) {
						// 如果为空则 从monggo中获取内容
						BasicDBObject doc = new BasicDBObject();
						doc.put("companyCode", jo.getCompanyCode());
						DBCursor cursor = mongo.find(collection, doc);
						boolean flag = false;
						jobsInfo = new LinkedList<Company51jobPosition2Bean>();
						while (cursor.hasNext()) {
							flag = true;
							BasicDBObject so = (BasicDBObject) cursor.next();
							jobs = so.getString("jobs");
						}
						if (!flag) {
							log.info("不存在职位详细信息:" + jo.getCompanyCode() + "\t"
									+ jo.getJobCode());
							notCompanySet.add(jo.getCompanyCode());
							continue;
						}
						if (jobs == null || jobs.equals("")) {
							
						} else {
							jobsInfo = (List<Company51jobPosition2Bean>) JsonUtil
									.getListFromJsonArrStr(jobs,
											Company51jobPosition2Bean.class, m);
						}
						returnMap.put(jo.getCompanyCode(), jobsInfo);
					}
					// 判断是否存在新内容;
					// 并添加
					boolean flag2 = false;
					for (Company51jobPosition2Bean beanson : jobsInfo) {
						if (beanson.getJobCode() == job.getJobCode()) {
							beanson.setTrunkDate(job.getTrunkDate());
							beanson.setEducationBackground(job
									.getEducationBackground());
							job.getJobCode();
							beanson.setMonthlyply(job.getMonthlyply());
							beanson.setPositionDesc(job.getPositionDesc());
							beanson.setPositionFunction(job
									.getPositionFunction());
							beanson.setPositionTag(job.getPositionTag());
							beanson.setPositionUrl(job.getPositionUrl());
							beanson.setPositonName(job.getPositonName());
							beanson.setWorkAddress(job.getWorkAddress());
							beanson.setYearsOfWorking(job.getYearsOfWorking());
							flag2 = true;
							boolean flag3 = false;
							for (Company51PositionSonBean son2 : job
									.getJobInfoList()) {
								List<Company51PositionSonBean> ll = beanson
										.getJobInfoList();
								for (Company51PositionSonBean son1 : ll) {
									if (son2.getPublishDate().equals(
											son1.getPublishDate())) {
										flag3 = true;
										break;
									}
								}
								if (flag3) {
									break;
								} else {
									beanson.addJobInfoList(son2);
								}
							}
						}
					}
					if (!flag2) {
						jobsInfo.add(job);
					}
				}
				// 重新赋值
				size_new = companySet.size();
				// 重新更新相关字段
				for (Entry<Long, List<Company51jobPosition2Bean>> jobsInfoEntry : returnMap
						.entrySet()) {
					List<Company51jobPosition2Bean> jobsInfo = jobsInfoEntry
							.getValue();
					Long companyCode = jobsInfoEntry.getKey();
					List<BasicDBObject> allJob = new LinkedList<BasicDBObject>();
					for (Company51jobPosition2Bean beS : jobsInfo) {
						BasicDBObject obj1 = new BasicDBObject();
						obj1.put("educationBackground",
								beS.getEducationBackground());
						obj1.put("jobCode", beS.getJobCode());
						obj1.put("monthlyply", beS.getMonthlyply());
						obj1.put("positionDesc", beS.getPositionDesc());
						obj1.put("positionFunction", beS.getPositionFunction());
						obj1.put("positionTag", beS.getPositionTag());
						obj1.put("positionUrl", beS.getPositionUrl());
						obj1.put("positonName", beS.getPositonName());
						obj1.put("trunkDate", beS.getTrunkDate());
						obj1.put("workAddress", beS.getWorkAddress());
						obj1.put("yearsOfWorking", beS.getYearsOfWorking());
						List<BasicDBObject> jobInfoList = new LinkedList<BasicDBObject>();
						List<Company51PositionSonBean> ll = beS
								.getJobInfoList();
						for (Company51PositionSonBean s : ll) {
							BasicDBObject obj2 = new BasicDBObject();
							obj2.put("memberCount", s.getMemberCount());
							obj2.put("publishDate", s.getPublishDate());
							jobInfoList.add(obj2);
						}
						obj1.put("jobInfoList", jobInfoList);
						allJob.add(obj1);
					}
					BasicDBObject doc3 = new BasicDBObject();
					doc3.put("jobs", allJob);
					BasicDBObject doc = new BasicDBObject();
					doc.put("companyCode", companyCode);
					mongo.update(collection, doc, new BasicDBObject("$set",
							doc3), true, false);

				}
				returnMap = null;
				reader.close();
				read.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

		// 读取 job更新文件
		reader = null;
		try {
			// 用于存储已经使用过的公司code
			HashSet<Long> companySet = new HashSet<Long>();
			int size_new = 0;
			int size_old = -1;
			while (size_new - size_old != 0) {
				log.info("size_old:"+size_old+"\t"+size_new);
				Thread.sleep(1000);
				// 当存在新增时则不跳出
				// 否则跳出
				HashSet<Long> companyTempSet=new HashSet<Long>();
				HashSet<Long> notCompanySet=new HashSet<Long>();
				HashMap<Long, List<Company51jobPosition2Bean>> returnMap = new HashMap<Long, List<Company51jobPosition2Bean>>();
				size_old = size_new;
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file3), "utf-8");
				reader = new BufferedReader(read);
				String tempString = null;
				log.info("开始读取 职位文件" + file3.getAbsolutePath());
				// 一次读入一行，直到读入null为文件结束
				int i = 0;
				// 获取 对应的索引
				while ((tempString = reader.readLine()) != null) {
					i++;
					if (i % printCount == 0) {
						log.info("读取文件数量:" + i);
						// log.info("读取文件数量:"+i);
					}
					if (tempString.length() == 0) {
						continue;
					}
					// 判断是否重复
					Company51JobPositionBean job = (Company51JobPositionBean) JsonUtil
							.getDtoFromJsonObjStr(tempString,
									Company51JobPositionBean.class);
					if (companySet.contains(job.getCompanyCode())) {
					} else {
						// 需要判断是否超过最大值
						if (companySet.size() - size_old > limit) {
							continue;
						}  else {
							// 添加新内容
							companyTempSet.add(job.getCompanyCode());
							companySet.add(job.getCompanyCode());
						}
					}
					if(!companyTempSet.contains(job.getCompanyCode()))
					{
						//如果不属于该次循环的code内容则跳过
						continue;
					}// 获取公司信息
					if(notCompanySet.contains(job.getCompanyCode()))
					{
						continue;
					}
					String jobs = "";
					List<Company51jobPosition2Bean> jobsInfo = returnMap
							.get(job.getCompanyCode());
					if (jobsInfo == null) {
						// 获取公司信息
						BasicDBObject doc = new BasicDBObject();
						doc.put("companyCode", job.getCompanyCode());
						jobsInfo = new LinkedList<Company51jobPosition2Bean>();
						DBCursor cursor = mongo.find(collection, doc);
						boolean flag = false;
						while (cursor.hasNext()) {
							flag = true;
							BasicDBObject so = (BasicDBObject) cursor.next();
							//System.out.println(so.toString());
							jobs = so.getString("jobs");
						}
						if(!flag)
						{
							log.info("不存在职位信息:" + job.getCompanyCode() + "\t"
									+ job.getJobCode());
							notCompanySet.add(job.getCompanyCode());
							continue;
						}
						if (jobs == null || jobs.equals("")) {
						} else {
							jobsInfo = (List<Company51jobPosition2Bean>) JsonUtil
									.getListFromJsonArrStr(jobs,
											Company51jobPosition2Bean.class, m);
						}
						returnMap.put(job.getCompanyCode(), jobsInfo);

						boolean flag2 = false;
						for (Company51jobPosition2Bean beanson : jobsInfo) {
							if (beanson.getJobCode() == job.getJobCode()) {
								beanson.setTrunkDate(job.getTrunkDate());
								job.getJobCode();
								beanson.setPositionUrl(job.getPositionUrl());
								beanson.setPositonName(job.getPositonName());
								beanson.setWorkAddress(job.getWorkAddress());
								flag2 = true;
								boolean flag3 = false;
								List<Company51PositionSonBean> ll = beanson
										.getJobInfoList();
								for (Company51PositionSonBean son1 : ll) {
									if (job.getPublishDate().equals(
											son1.getPublishDate())) {
										flag3 = true;
										break;
									}
								}
								if (flag3) {
									break;
								} else {
									Company51PositionSonBean c = new Company51PositionSonBean();
									c.setMemberCount(job.getMemberCount());
									c.setPublishDate(job.getPublishDate());
									beanson.addJobInfoList(c);
								}
							}
						}
						if (!flag2) {
							continue;
						}
					}
				}
				// 重新赋值
				size_new = companySet.size();
				// 重新更新相关字段
				for (Entry<Long, List<Company51jobPosition2Bean>> jobsInfoEntry : returnMap
						.entrySet()) {
					List<Company51jobPosition2Bean> jobsInfo = jobsInfoEntry
							.getValue();
					Long companyCode = jobsInfoEntry.getKey();
					List<BasicDBObject> allJob = new LinkedList<BasicDBObject>();
					for (Company51jobPosition2Bean beS : jobsInfo) {
						BasicDBObject obj1 = new BasicDBObject();
						obj1.put("educationBackground",
								beS.getEducationBackground());
						obj1.put("jobCode", beS.getJobCode());
						obj1.put("monthlyply", beS.getMonthlyply());
						obj1.put("positionDesc", beS.getPositionDesc());
						obj1.put("positionFunction", beS.getPositionFunction());
						obj1.put("positionTag", beS.getPositionTag());
						obj1.put("positionUrl", beS.getPositionUrl());
						obj1.put("positonName", beS.getPositonName());
						obj1.put("trunkDate", beS.getTrunkDate());
						obj1.put("workAddress", beS.getWorkAddress());
						obj1.put("yearsOfWorking", beS.getYearsOfWorking());
						List<BasicDBObject> jobInfoList = new LinkedList<BasicDBObject>();
						List<Company51PositionSonBean> ll = beS
								.getJobInfoList();
						for (Company51PositionSonBean s : ll) {
							BasicDBObject obj2 = new BasicDBObject();
							obj2.put("memberCount", s.getMemberCount());
							obj2.put("publishDate", s.getPublishDate());
							jobInfoList.add(obj2);
						}
						obj1.put("jobInfoList", jobInfoList);
						allJob.add(obj1);
					}
					BasicDBObject doc3 = new BasicDBObject();
					doc3.put("jobs", allJob);
					BasicDBObject doc = new BasicDBObject();
					doc.put("companyCode", companyCode);
					mongo.update(collection, doc, new BasicDBObject("$set",
							doc3), true, false);

				}
				reader.close();
				read.close();
				returnMap = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
	}

	/**
	 * 全量更新 51job 公司指纹信息入mongo中 从文件写入mongodb中
	 * 
	 * @param fileName
	 *            公司文件
	 * @param fileName2
	 *            职位文件
	 * @param ip
	 * @param port
	 * @param database
	 * @param collection
	 * @param printCount
	 *            文件打印行/每
	 */
	public static void readWriteToMongodJobCompany(String fileName,
			String fileName2, String outputFile, String ip, int port,
			String database, String collection, int printCount) {
		MongoDb mongo = new MongoDb(ip, port, database);
		File file = new File(fileName);
		File file2 = new File(fileName2);

		if (!file.exists()) {
			log.error("文件不存在:" + file.getAbsolutePath());
			System.exit(1);
		}
		if (!file2.exists()) {
			log.error("文件不存在:" + file2.getAbsolutePath());
			System.exit(1);
		}
		FileUtil2 out = new FileUtil2(outputFile, "utf-8");
		log.info("开始读取公司文件:" + file.getAbsolutePath());
		BufferedReader reader = null;
		HashMap<Long, Company51JobCompany2Bean> map = new HashMap<Long, Company51JobCompany2Bean>();
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file), "utf-8");
			reader = new BufferedReader(read);
			String tempString = null;
			log.info("开始读取文件");
			// 一次读入一行，直到读入null为文件结束
			int i = 0;

			while ((tempString = reader.readLine()) != null) {
				i++;
				if (i % printCount == 0) {
					log.info("读取文件数量:" + i);
				}
				if (tempString.length() == 0) {
					continue;
				}
				// 判断是否重复
				Company51JobCompanyBean com = (Company51JobCompanyBean) JsonUtil
						.getDtoFromJsonObjStr(tempString,
								Company51JobCompanyBean.class);
				if (map.get(com.getCompanyCode()) != null) {
					// 重复
					continue;
				}
				Company51JobCompany2Bean company = new Company51JobCompany2Bean();
				company.setAddress(com.getAddress());
				company.setCompanyCategory(com.getCompanyCategory());
				company.setCompanyCode(com.getCompanyCode());
				company.setCompanyMemberNum(com.getCompanyMemberNum());
				company.setCompanyName(com.getCompanyName());
				company.setCompanyUrl(com.getCompanyUrl());
				company.setDesc(com.getDesc());
				company.setFansCount(com.getFansCount());
				company.setIndustryCategory(com.getIndustryCategory());
				company.setLocation(com.getLocation());
				if (company.getLocation() == null) {
					log.error("公司不存在 poi数据:" + company.getCompanyCode());
					continue;
				}
				company.setPostcode(com.getPostcode());
				map.put(com.getCompanyCode(), company);
			}
			try {
				reader.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			try {
				read.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}
		// 读取 job文件
		reader = null;
		try {
			InputStreamReader read = new InputStreamReader(new FileInputStream(
					file2), "utf-8");
			reader = new BufferedReader(read);
			String tempString = null;
			log.info("开始读取职位详情文件" + file2.getAbsolutePath());
			// 一次读入一行，直到读入null为文件结束
			int i = 0;
			HashSet<Long> jobMap = new HashSet<Long>();
			// 获取 对应的索引
			while ((tempString = reader.readLine()) != null) {
				i++;
				if (i % printCount == 0) {
					log.info("读取文件数量:" + i);
				}
				if (tempString.length() == 0) {
					continue;
				}
				// 判断是否重复
				Company51JobDescBean jo = (Company51JobDescBean) JsonUtil
						.getDtoFromJsonObjStr(tempString,
								Company51JobDescBean.class);
				Company51JobCompany2Bean re = map.get(jo.getCompanyCode());
				if (re == null) {
					// 不存在公司
					// 重复
					log.info("不存在公司:" + jo.getCompanyCode());
					continue;
				}
				if (jobMap.contains(jo.getJobCode())) {
					continue;
				}
				jobMap.add(jo.getJobCode());
				Company51jobPosition2Bean job = new Company51jobPosition2Bean();
				job.setJobCode(jo.getJobCode());
				job.setPositionUrl(jo.getPositionUrl());
				job.setPositonName(jo.getPositonName());
				job.setEducationBackground(jo.getEducationBackground());
				job.setMonthlyply(jo.getMonthlyply());
				job.setPositionDesc(jo.getPositionDesc());
				job.setPositionFunction(jo.getPositionFunction());
				job.setPositionTag(jo.getPositionTag());
				job.setTrunkDate(jo.getTrunkDate());
				job.setWorkAddress(jo.getWorkAddress());
				job.setYearsOfWorking(jo.getYearsOfWorking());
				Company51PositionSonBean son = new Company51PositionSonBean();
				son.setMemberCount(jo.getMemberCount());
				son.setPublishDate(jo.getPublishDate());
				job.addJobInfoList(son);
				re.addJobs(job);
			}
			int ll = 0;
			int size = map.size();
			for (Entry<Long, Company51JobCompany2Bean> bean : map.entrySet()) {
				ll++;
				log.info("公司数:" + size + "\t当前位置:" + ll);
				Company51JobCompany2Bean be = bean.getValue();
				// 从mongo中查询相关字段是否存在
				String str = JsonUtil.getJsonStr(be);
				mongo.insert(collection, str);
				out.write(str);
			}
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {

		}

	}

	public static void main(String[] args) {
		PropertyConfigurator.configure("./log4j.properties");
		String fileName = "f://zjCompany//companyDesc-2014-12-13.txt";
		String fileName2 = "f://zjCompany//companyDescJobDesc-2014-12-13.txt";
		String outputFile = "f://zjCompany//out.txt";
		String ip = "192.168.1.11";
		int port = 27017;
		String database = "demo";
		String collectionName = "company51job";
		// IntoMongoDB.readWriteToMongodJobCompany(fileName, fileName2,
		// outputFile, ip, port, database, collectionName, 1000);
		fileName = "f://zjCompany//companyDesc-2014-12-15.txt";
		fileName2 = "f://zjCompany//companyDescJobDesc-2014-12-15.txt";
		outputFile = "f://zjCompany//companyDescJob-2014-12-15.txt";
		IntoMongoDB.readWriteToMongodJobCompanyUpdate(fileName, fileName2,
				outputFile, ip, port, database, collectionName, 3,20);
	}
}
