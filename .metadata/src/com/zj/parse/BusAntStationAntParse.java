package com.zj.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.util.JsonUtil;
import com.zj.bean.BusAndStationBean;
import com.zj.bean.BusAndStationInputQueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;

/**
 * 地铁口对应的公交信息
 * 
 * @author Administrator
 *
 */
public class BusAntStationAntParse {
	
	private static Logger log = Logger.getLogger(BusAntStationAntParse.class);
	
	/**
	 * 
	 * @param url
	 * @param IntoDb
	 *            输出队列
	 * @param inputBean为输入的地铁线路信息
	 * @param km
	 *            附近几公里
	 */
	public static void runUrl(String url,
			BusAndStationInputQueueBean inputBean, IntoDb intoDb,BusAndStationBean result) {
		result.setBusName(inputBean.getBusName());
		// 获取首末车相关信息
		Pattern p_1 = Pattern.compile("发车间隔：([^<]*?)<");
		Matcher m_1 = p_1.matcher(url);
		if (m_1.find()) {
			result.setIntervalTime(getTimeInteger(m_1.group(1)));
		} else {
			//System.out.println("是否包含时间间隔信息:" + url.contains("发车间隔："));
		}
		p_1 = Pattern.compile("起点站首末车时间:([^<]*?)<");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			result.setUpStationTime(m_1.group(1));
		}
		p_1 = Pattern.compile("终点站首末车时间:([^<]*?)<");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			result.setDownStationTime(m_1.group(1));
		}
		p_1 = Pattern.compile("票价信息：</span>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			result.setPayDisc(m_1.group(1).trim());
		}
		p_1 = Pattern.compile("汽车公司：</span>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			result.setBusCompany(m_1.group(1).trim());
		}
		// 获取公交站信息
		p_1 = Pattern.compile("value=\"([^\"]*?)\" id=\"stationNames\"");
		m_1 = p_1.matcher(url);
		if (m_1.find()) {
			String[] strTemp = m_1.group(1).split(",");
			for (String str : strTemp) {
				result.addStationName(str);
			}
		}

		if (result.isOk()) {
			// 加入入库队列中
			intoDb.add(JsonUtil.getJsonStr(result));
		}

	}

	public static void runUrlRever(String url,
			BusAndStationInputQueueBean inputBean,BusAndStationBean result) {
		// 获取公交站信息
		Pattern p_1 = Pattern.compile("value=\"([^\"]*?)\" id=\"stationNames\"");
		Matcher m_1 = p_1.matcher(url);
		if (m_1.find()) {
			String[] strTemp = m_1.group(1).split(",");
			for (String str : strTemp) {
				result.addStationNameRever(str);
				;
			}
		}
	}
	/**
	 * 获取以分钟为时间的值
	 * 
	 * @param str
	 * @return
	 */
	public static String getTimeInteger(String str) {
		if (str.endsWith("分钟")) {
			return str.substring(0, str.length() - 2);
		} else if (str.endsWith("小时")) {
			String[] temp = str.split("-");
			String te = "";
			int i = 0;
			for (String st : temp) {
				i++;
				if (i == 1) {
					te += Integer.parseInt(st) * 60;
				} else {
					te += "-" + (Integer.parseInt(st) * 60);
				}
			}
			return te;
		}
		return "";
	}
	public static void main(String[] args) throws Exception {
		String url=AntGetUrl.doGet("http://bus.mapbar.com/beijing/xianlu/978lu/", "utf-8");
		BusAntStationAntParse.runUrl(url,null,null,null);
	}
}
