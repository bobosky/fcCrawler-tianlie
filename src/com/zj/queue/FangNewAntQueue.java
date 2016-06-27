package com.zj.queue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

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
import com.zj.parse.FangListAntParse;

/**
 * 停车信息 队列容器
 * 
 * @author Administrator
 * 
 */
@SuppressWarnings("rawtypes")
public class FangNewAntQueue extends QueueFather {
	private Logger log = Logger.getLogger(FangNewAntQueue.class);
	/**
	 * 队列容器 其中存储的为
	 */
	public LinkedBlockingQueue<FangInput2QueueBean> info = null;
	/**
	 * key为北京 value为url地址
	 */
	public static HashMap<String, String> cityUrlMap = new HashMap<String, String>();

	static {
		cityUrlMap.put("北京", "http://newhouse.fang.com/house/s/list/a75");
		cityUrlMap.put("南京",
				"http://newhouse.nanjing.fang.com/house/s/list/a77");
	}
	/**
	 * 唯一的新楼盘的url
	 */
	public HashSet<String> uniUrl = new HashSet<String>();

	/**
	 * 初始化队列信息
	 */
	@SuppressWarnings("unchecked")
	public void init(QueueBean queueBean, String listName2, boolean flag,
			String listNameOutput) {

		if (queueBean.getQueueInputName() != null) {
			redis = new Redis(queueBean.getInputQueueUrl());
			listName = listName2;
			isRedis = true;
		} else
			info = new LinkedBlockingQueue<FangInput2QueueBean>(
					queueBean.getInputQueueNum());
		// >([^<]*?)</option>([^<]*?)</select> 页 </span>
		if (!flag)
			return;
		String url = "";
		// 从本页获取最新的 搜房信息

		// 获取每一页的楼盘并且判断下一个页面是否无效
		int page = 0;
		String urlCode = "";
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
			page = 0;
			while (true) {
				page++;
				if (page == 1) {
					while (true) {
						try {
							url = AntGetUrl.doGetGzip(cityUrl, "gbk", false);
							break;
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				} else {
					while (true) {
						try {
							log.info(urlCode);
							url = AntGetUrl.doGetGzip(urlCode, "gbk", false);
							break;
						} catch (Exception e1) {
							e1.printStackTrace();
						}
					}
				}
				urlCode = regex(url, page, cityUrl, cityName);
				log.info("新房源检测到第:" + page + "页");
				if (urlCode == null) {
					log.info("新房源页面解析完成,总页数为:" + page);
					break;
				}
				if (cityName.equals("北京")) {
					break;
				}
			}
		}
		log.info("搜房网信息初始化完成");
	}

	/**
	 * 解析当前页的数据
	 * 
	 * @param url
	 * @return
	 */
	public String regex(String url, int page, String cityUrl, String cityName) {
		// 获取最大页面数
		Pattern p_1 = Pattern
				.compile("href=\"([^\"]*?)\"[^>]*?>[\\s]*?<img[^>]*?alt=\"([^\"]*?)\"");
		Matcher m_1 = p_1.matcher(url);
		int end = 0;
		while (m_1.find()) {
			end = m_1.end();
			FangInput2QueueBean bean = new FangInput2QueueBean();
			bean.setUrl(m_1.group(1));
			if (bean.getUrl().equals("") || uniUrl.contains(bean.getUrl())
					|| !bean.getUrl().contains("http")) {
				continue;
			}
			uniUrl.add(bean.getUrl());
			bean.setCity(cityName);
			add(bean);
		}
		// url = url.substring(end);
		// 获取是否达到最大页面
		p_1 = Pattern.compile("-b9([\\d]*?)/\">尾页</a");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			// 每次检测放置页面新增
			int pageCount = Integer.parseInt(m_1.group(1));
			if (page < pageCount) {
				return cityUrl + "-b9" + (page + 1) + "/";
			} else {
				return null;
			}
		}
		return null;
	}

	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public FangInput2QueueBean get(QueueBean bean) {
		return (FangInput2QueueBean) getQueueBean(bean, info,
				FangInput2QueueBean.class, log, "搜房队列信息为空");
	}

	/**
	 * 添加内容
	 * 
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(FangInput2QueueBean bean) {
		addQueueBean(bean, info);
	}
}
