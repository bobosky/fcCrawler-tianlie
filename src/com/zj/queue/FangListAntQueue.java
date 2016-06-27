package com.zj.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import sun.util.logging.resources.logging;

import com.db.MongoDb;
import com.db.Redis;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.util.DateFormat;
import com.util.JsonUtil;
import com.zj.bean.FangInput2QueueBean;
import com.zj.bean.FangInputQueueBean;
import com.zj.bean.FangListBean;
import com.zj.bean.FangOldBean;
import com.zj.bean.LationLngLat;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.FangListAntParse;

/**
 * 停车信息 队列容器
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("rawtypes")
public class FangListAntQueue extends QueueFather {
	private Logger log = Logger.getLogger(FangListAntQueue.class);
	/**
	 * 队列容器 其中存储的为
	 */
	public LinkedBlockingQueue<FangInputQueueBean> info = null;
	/**
	 * key为北京 value为url地址
	 */
	public static HashMap<String, String> cityUrlMap = new HashMap<String, String>();

	public String mongoString = "192.168.1.4:27017";

	/**
	 * 初始化城市对应的url列表
	 */
	public void initCityUrlMapping() {
		cityUrlMap.put("北京", "http://office.fang.com");
		// cityUrlMap.put("南京", "http://office.nanjing.fang.com");
		// cityUrlMap.put("上海", "http://office.sh.fang.com");
		// cityUrlMap.put("广州", "http://office.gz.fang.com");
		// cityUrlMap.put("深圳", "http://office.gz.fang.com");
		// 从页面获取对应的信息
		String url = "";
		while (true) {
			try {
				// url =
				// AntGetUrl.doGetGzip("http://office.fang.com/zu/house/w37/","gbk",false);
				url = AntGetUrl.doGetGzip("http://office.fang.com",
						"gbk", false);
				break;
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
		Pattern p_ = Pattern
				.compile("<a[\\s]*?href=\"([^\"]*?)\"[\\s]*?target=\"_blank\"[\\s]*?>([^<]*?)</a>");
		Matcher m_ = p_.matcher(url);
		while (m_.find()) {
			String cityUrlTemp=m_.group(1).trim();
			String cityUrl = cityUrlTemp.substring(0,cityUrlTemp.length()-1);
			String cityName = m_.group(2).replaceAll("写字楼","").trim();
			log.info("初始化城市列表:" + cityName + "\t" + cityUrl);
			cityUrlMap.put(cityName, cityUrl);
		}

	}

	public static String getCityUrl(String str) {
		return cityUrlMap.get(str);
	}

	/**
	 * 初始化队列信息
	 */
	@SuppressWarnings("unchecked")
	public void init(QueueBean queueBean, String listName2, boolean flag,
			String listNameOutput, IntoDb intoDb) {
		if (queueBean.getQueueInputName() != null) {
			redis = new Redis(queueBean.getInputQueueUrl());
			listName = listName2;
			isRedis = true;
		} else
			info = new LinkedBlockingQueue<FangInputQueueBean>(
					queueBean.getInputQueueNum());
		// >([^<]*?)</option>([^<]*?)</select> 页 </span>
		if (!flag)
			return;
		initCityUrlMapping();
		String url = "";

		// 初始化唯一标记数据
		FangListAntParse.setUniCode(new HashSet<String>());

		String initString = queueBean.getInputQueueInitString();
		if (initString.equals("")) {
			log.info("搜房网信息初始化完成");
			return;
		}
		String[] initStringList = initString.split(",");
		for (String cityName : initStringList) {
			String cityUrl = cityUrlMap.get(cityName);
			if (cityUrl == null) {
				continue;
			}
			// 从数据库中获取老的code到uniCode中
			ArrayList<FangOldBean> fangCodeOld = getMongoDbFangCode(cityName);
			
			for (FangOldBean bean : fangCodeOld) {
				FangListAntParse.uniCode.add(bean.getUrl());
				FangInput2QueueBean input = new FangInput2QueueBean();
				input.setFangCode(bean.getFangCode());
				input.setLocation(bean.getLocation());
				input.setCity(bean.getCity());
				input.setUrl(bean.getUrl());
				// listNameOutput 只有这个存在才可用 以及使用的是redis存储方式

				if (queueBean.getQueueInputName() != null
						&& listNameOutput != null) {
					try {
						String temp= JsonUtil.getJsonStr(input);
						System.out.println("添加老数据:"+temp);
						redis.rpush(listNameOutput,temp);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						System.exit(1);
					}
				} else {
					log.error("使用的code的时候没有正确配置请重新配置");
					System.exit(1);
				}
			}

			// 使用热门写字楼获取信息

			readHostOfficeBuilding(cityUrl + "/loupan/house/", cityName, intoDb);

			// 从租房信息中获取最新的 搜房信息
			
			//搜房已经修改完毕
//			while (true) {
//				try {
//					// url =
//					// AntGetUrl.doGetGzip("http://office.fang.com/zu/house/w37/","gbk",false);
//					url = AntGetUrl.doGetGzip(cityUrl, "gbk", false);
//					break;
//				} catch (Exception e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//			FangListBean fanglistBean = new FangListBean();
//			fanglistBean.setCityName(cityName);
//			// 获取区域信息
//			// 首先获取 纯写字楼code
//			Pattern p_1 = Pattern
//					.compile("<a href=\"([^\"]*?)\">[\\s]*?纯写字楼[\\s]*?</a>");
//			// 获取最大页面数
//			Matcher m_1 = p_1.matcher(url);
//			if (m_1.find()) {
//				fanglistBean.setBuildingCode(getCode(m_1.group(1).trim()));
//			}
//			// 获取租金信息
//			// i3
//			p_1 = Pattern.compile("租金：([\\s\\S]*?)<div class=\"top\">");
//			// 获取最大页面数
//			m_1 = p_1.matcher(url);
//			if (m_1.find()) {
//				// 获取租金信息
//				String strTemp = m_1.group(1);
//				Pattern p_2 = Pattern.compile("href=\'([^\"]*?)\'");
//				// 获取最大页面数
//				Matcher m_2 = p_2.matcher(strTemp);
//				while (m_2.find()) {
//					fanglistBean.addHire(getCode(m_2.group(1).trim()));
//				}
//			}
//
//			// 获取面积
//			p_1 = Pattern.compile("面积：([\\s\\S]*?)<div class=\"top\">");
//			// 获取最大页面数
//			m_1 = p_1.matcher(url);
//			if (m_1.find()) {
//				// 获取租金信息
//				String strTemp = m_1.group(1);
//				Pattern p_2 = Pattern.compile("href=\'([^\"]*?)\'");
//				// 获取最大页面数
//				Matcher m_2 = p_2.matcher(strTemp);
//				while (m_2.find()) {
//					fanglistBean.addSpace(getCode(m_2.group(1).trim()));
//				}
//			}
//			// 全部区域的信息
//			LinkedList<String> allArea = new LinkedList<String>();
//			// 获取面积
//			p_1 = Pattern.compile("区域：([\\s\\S]*?)class=\"shangQuan");
//			// 获取最大页面数
//			m_1 = p_1.matcher(url);
//			if (m_1.find()) {
//				// 获取租金信息
//				String strTemp = m_1.group(1);
//				Pattern p_2 = Pattern.compile("href=([^\"]*?)[\\s]");
//				// 获取最大页面数
//				Matcher m_2 = p_2.matcher(strTemp);
//				int i = 0;
//				while (m_2.find()) {
//					i++;
//					if (i == 1) {
//						continue;
//					}
//
//					allArea.add(cityUrl + m_2.group(1).trim());
//					// System.out.println(allArea.get(allArea.size()-1));
//				}
//			}
//			// 从每一个区获取对应的商圈
//			while (allArea.size() > 0) {
//				String webUrl = allArea.pollFirst();
//				while (true) {
//					try {
//						url = AntGetUrl.doGetGzip(webUrl, "gbk", false);
//						break;
//					} catch (Exception e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//				// 从url中获取对应的商区
//
//				p_1 = Pattern
//						.compile("<p[\\s]*?class=\"contain[^>]*?>[\\s]*?<[^>]*?>不限([\\s\\S]*?)<p class=\"bottom\">");
//				// 获取最大页面数
//				m_1 = p_1.matcher(url);
//				if (m_1.find()) {
//					// 获取租金信息
//					String strTemp = m_1.group(1);
//					Pattern p_2 = Pattern.compile("href=\"([^\"]*?)\"");
//					// 获取最大页面数
//					Matcher m_2 = p_2.matcher(strTemp);
//					while (m_2.find()) {
//						// fanglistBean.addArea("http://office.fang.com"+m_2.group(1).trim());
//						fanglistBean.addArea(cityUrl + m_2.group(1).trim());
//					}
//				}
//			}
//
//			// 初始化所有的队列信息
//			for (String ur : fanglistBean.getArea()) {
//				FangListBean fang = new FangListBean();
//				fang.setCityName(fanglistBean.getCityName());
//				fang.addArea(ur);
//				fang.setHire(fanglistBean.getHire());
//				fang.setBuildingCode(fanglistBean.getBuildingCode());
//				fang.setSpace(fanglistBean.getSpace());
//				add(fang);
//			}
		}
		log.info("搜房网信息初始化完成");
	}

	/**
	 * 从热门写字楼页面获取code
	 */
	public void readHostOfficeBuilding(String url, String cityName,
			IntoDb intoDb) {
		String url2 = "";
		int index = 0;
		int count=100;
		while (true) {
			index++;
			url2 = url + "i3" + index + "/";
			String urlCode = "";
			int count2=0;
			while (true) {
				count2++;
				try {
					// url =
					// AntGetUrl.doGetGzip("http://office.fang.com/zu/house/w37/","gbk",false);
					log.info("列表页执行:" + url2);
					urlCode = AntGetUrl.doGetGzip(url2, "gbk", false);
					break;
				} catch (Exception e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					if(count2>5){
						break;
					}
				}
			}
			if (urlCode.length() <= 0) {
				if(index==1)
				{
					break;
				}
				continue;
			}
			if(index==1)
			{
			Pattern p_1 = Pattern
					.compile("\"fy_text\">[\\s]*?([\\d]*)/([\\d]*)[\\s]*?</span");
			// 获取每一个页面对应的写字楼信息
			Matcher m_1 = p_1.matcher(urlCode);
			if(m_1.find())
			{
				String val=m_1.group(2);
				if(val==null||val.equals(""))
				{
					break;
				}
				count=Integer.parseInt(val);
			}
			}
			boolean isContinue = Regex(urlCode, cityName, intoDb);
			if (!isContinue) {
				break;
			}
			if(index>=count)
			{
				break;
			}
		}
	}

	/**
	 * 从mongo中获取搜房code
	 * 
	 * @param cityName
	 * @return
	 */
	public ArrayList<FangOldBean> getMongoDbFangCode(String cityName) {
		MongoDb mongo = new MongoDb(mongoString, "demo");
		BasicDBObject doc = new BasicDBObject();
		doc.put("fangCode", 1);
		doc.put("location", 1);
		doc.put("fangListc.url", 1);
		BasicDBObject doc2 = new BasicDBObject();
		doc2.put("fangListc.city", cityName);
		DBCollection collection = mongo.getCollection("fang");
		DBCursor cursor = collection.find(doc2, doc);
		ArrayList<FangOldBean> fangCode = new ArrayList<FangOldBean>();
		while (cursor.hasNext()) {
			BasicDBObject dbObj = (BasicDBObject) cursor.next();
			if (dbObj == null)
				continue;
			// System.out.println(dbObj.toString());
			FangOldBean oldBean = new FangOldBean();
			oldBean.setFangCode(Long.parseLong(dbObj.getString("fangCode")));
			oldBean.setLocation((LationLngLat) JsonUtil.getDtoFromJsonObjStr(
					dbObj.getString("location"), LationLngLat.class));
			oldBean.setUrl(((BasicDBObject) dbObj.get("fangListc"))
					.getString("url"));
			oldBean.setCity(cityName);
			// System.out.println(oldBean.getUrl());
			// System.out.println(oldBean.getLocation().getLat());
			fangCode.add(oldBean);
		}
		return fangCode;
	}

	public static void main1(String[] args) throws Exception {

		System.out.println(AntGetUrl.doGetGzip("http://office.fang.com", "gbk",
				false));

		FangListAntQueue test = new FangListAntQueue();
		test.initCityUrlMapping();

		// FangListAntQueue main = new FangListAntQueue();
		// main.getMongoDbFangCode("北京");
	}

	/**
	 * 从/zu/house/l21-w37/ 中截取最后的一个部分
	 * 
	 * @param str
	 */
	public String getCode(String str) {
		if (str.length() == 0) {
			return "";
		}
		// System.out.println(str);
		String temp = str.substring(str.substring(0, str.length() - 1)
				.lastIndexOf("/") + 1, str.lastIndexOf("/"));
		if (temp.contains("-")) {
			return temp;// temp.substring(0, temp.indexOf("-"));
		} else if (temp.contains("house")) {
			return "";
		} else {
			return temp;
		}
	}

	/**
	 * 解析热门写字楼列表也数据
	 * 
	 * @param url
	 * @param cityName
	 * @param intoDb
	 */
	public boolean Regex(String url, String cityName, IntoDb intoDb) {

		Pattern p_1 = Pattern
				.compile("<dt class=\"img rel floatl\">([\\s\\S]*?)</dl>");
		// 获取每一个页面对应的写字楼信息
		Matcher m_1 = p_1.matcher(url);
		boolean isContinue = false;
		while (m_1.find()) {
			isContinue = true;
			FangInput2QueueBean result = new FangInput2QueueBean();
			result.setCity(cityName);
			String buildUrl = m_1.group(0);
			Pattern p_2 = Pattern
					.compile("<img[^>]*?src=\"([^\"]*?)\"[^>]*?src2[^>]*?><");
			// 获取获取第一张logo
			Matcher m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setLogoUrl(m_2.group(1));
			}
			p_2 = Pattern.compile("<strong>([^<]*?)</strong>");
			// 获取写字楼名
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setOfficeBuildingName(m_2.group(1));
			}
			p_2 = Pattern.compile("href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a></p>");
			// 获取写字楼名
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				// 第一个为写字楼的url
				result.setUrl(m_2.group(1));
				// 第二个为写字楼名
				result.setOfficeBuildingName(m_2.group(2));
				result.setOfficeBuildingName(m_2.group(2));
			}
			if (FangListAntParse.uniCode.contains(result.getUrl())) {// 如果包含跳过
				continue;
			}
			p_2 = Pattern.compile("地址：([^<]*?)<");
			// 获取写字楼地址
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				// 设置地址
				result.setAddress(m_2.group(1)
						.substring(m_2.group(1).indexOf("]") + 1).trim());
				// 设置属于那个区
				String[] strTemp = m_2
						.group(1)
						.substring(m_2.group(1).indexOf("[") + 1,
								m_2.group(1).indexOf("]")).split("[\\s]");
				if (strTemp.length == 2) {
					result.setArea(strTemp[0]);
					result.setCbdCategory(strTemp[1]);
				} else {
					// 数据异常
					System.out.println("地域地址数据异常:" + m_2.group(1));
				}
			}
			p_2 = Pattern
					.compile("<span class=\"dtjt[^\"]*?\"><a[^>]*?href=\"([^\"]*?)\"");
			// 地图url
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setMapUrl(m_2.group(1));
			}
			p_2 = Pattern.compile("类型：([^<]*?)<");
			// 写字楼类型
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setOfficeBuildingCategory(m_2.group(1));
			}
			p_2 = Pattern.compile("物业费：([^<]*?)<");
			// 物业费
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				if (m_2.group(1).contains("元")) {
					result.setTenementFee(m_2.group(1).substring(0,
							m_2.group(1).indexOf("元")));
					result.setTenementFeeCategory(m_2.group(1).substring(
							m_2.group(1).indexOf("元")));
				} else {
					result.setTenementFee(m_2.group(1));
				}
			}
			p_2 = Pattern.compile("竣工时间：([^<]*?)<");
			// 写字楼类型
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setCompleteTime(DateFormat.transcateDate(m_2.group(1)));
			}
			p_2 = Pattern
					.compile("售价：<span[^>]*?>([^<]*?)</span><span[^>]*?>([^<]*?)</span");
			// 售价
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setSalePrice(m_2.group(1));
				result.setSalePriceCategory(m_2.group(2));
			}
			p_2 = Pattern
					.compile("租金：<span[^>]*?>([^<]*?)</span><span[^>]*?>([^<]*?)</span");
			// 租金
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setHirePrice(m_2.group(1));
				result.setHirePriceCategory(m_2.group(2));
			}
			p_2 = Pattern
					.compile("href=\"([^\"]*?)\"[^>]*?>出租房源</a><span[^>]*?>（([^）]*?)）");
			// 出租房源
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setHireHouseSouceUrl(m_2.group(1));
				result.setHireHouseSouce(Integer.parseInt(m_2.group(2)));
			}

			p_2 = Pattern
					.compile("href=\"([^\"]*?)\"[^>]*?>出售房源</a><span[^>]*?>（([^）]*?)）");
			// 出售房源
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setSaleHouseSourceUrl(m_2.group(1));
				result.setSaleHouseSource(Integer.parseInt(m_2.group(2)));
			}
			p_2 = Pattern
					.compile("href=\"([^\"]*?)\"[^>]*?>相册</a><span[^>]*?>（([^）]*?)）");
			// 相册
			m_2 = p_2.matcher(buildUrl);
			if (m_2.find()) {
				result.setPhotoAlbumUrl(m_2.group(1));
				result.setPhotoAlbum(Integer.parseInt(m_2.group(2)));
			}
		
			System.out.println("添加新楼盘:"+result.getUrl());
			FangListAntParse.uniCode.add(result.getUrl());
			// 加入队列
			intoDb.add(JsonUtil.getJsonStr(result));
		}
		return isContinue;
	}

	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public FangListBean get(QueueBean bean) {
		return (FangListBean) getQueueBean(bean, info, FangListBean.class, log,
				"搜房队列信息为空");
	}

	/**
	 * 添加内容
	 * 
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(FangListBean bean) {
		addQueueBean(bean, info);
	}

	public static void main(String[] args) {
		String url = "";
		try {
			 url =
			 AntGetUrl.doGetGzip("http://office.fang.com/zu/house/w37/","gbk",false);
//			url = AntGetUrl.doGetGzip("http://zu.sy.fang.com/cities.aspx",
//					"gbk", false);
			 System.out.println(url);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Pattern p_ = Pattern
				.compile("<a[\\s]*?href=\"([^\"]*?)\"[\\s]*?target=\"_blank\"[\\s]*?>([^<]*?)</a>");
		Matcher m_ = p_.matcher(url);
		while (m_.find()) {
			String cityUrl = m_.group(1).trim();
			String cityName = m_.group(2).trim();
			System.out.println("初始化城市列表:" + cityName + "\t" + cityUrl);
			cityUrlMap.put(cityName, cityUrl);
		}
	}
}
