package com.zj.parkExec;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;

import com.zj.bean.FangInput2QueueBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.crawler.AntGetUrlProxy;
import com.zj.crawler.IPProxy;
import com.zj.exec.MainProxy;
import com.zj.intoDb.IntoDb;
import com.zj.parse.FangAntParse;
import com.zj.queue.FangAntQueue;

public class FangExecThread implements Runnable {
	private static Logger log = Logger.getLogger(FangExecThread.class);
	private IntoDb IntoDb = null;
	private QueueBean queueBean = null;
	private FangAntQueue queue = null;
	private QueueBean queueFistBean = null;
	/**
	 * 有效执行次数
	 */
	private long count = 0;
	/**
	 * 结束的线程数
	 */
	private int threadCountEnd = 0;

	/**
	 * 初始化程序
	 */
	public FangExecThread(QueueBean queueBean, QueueBean queueFistBean,
			FangAntQueue queue, IntoDb IntoDb) {
		this.queueFistBean = queueFistBean;
		this.queue = queue;
		this.IntoDb = IntoDb;
		this.queueBean = queueBean;
	}

	/**
	 * 线程执行方法
	 */
	@SuppressWarnings("unchecked")
	public void run() {
		log.info("搜房执行线程启动：" + Thread.currentThread().getName());
		while (true) {
			if (queueBean.isEnd()) {// 如果该bean失效 一个周期完成则跳出
				log.info("fang 线程跳出:" + Thread.currentThread().getName());
				break;
			}
			try {
				Thread.sleep(queueBean.getUrlParseCycleTime());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (!queueBean.isStart()) {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			FangInput2QueueBean bean = null;
			boolean isBreak = false;
			int nullCount = 0;
			while (true) {
				bean = queue.get(queueBean);
				if (bean == null) {
					nullCount++;
					try {
						Thread.sleep(queueBean.getUrlParseCycleTime() + 1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (nullCount >= 3
							&& (queueFistBean == null || !queueFistBean
									.isExecIsRun())) {
						queueBean.setEnd(true);
						isBreak = true;
						break;
					}
					continue;
				} else {
					// 结束
					break;
				}
			}
			if (isBreak) {
				break;
			}
			// 执行 页面获取
			// //http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea=010000&funtype=0000&industrytype=00&issuedate=9&keywordtype=2&lang=c&stype=3&address=%E8%8B%B9%E6%9E%9C%E5%9B%AD&radius=0.03&fromType=20
			String urlCode = bean.getUrl();
			String url = "";
			if (urlCode.equals("")) {
				try {
					url = AntGetUrl.doGetGzip(bean.getFangSonSourceUrl(),
							"gbk", true);
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
					log.error("urlCode 被放入队列中:" + urlCode);
					queue.add(bean);
					continue;
				}
				if (url.length() > 0) {
					// 处理带有poi的页面并获取原始页面
					FangAntParse.runUrlPoi(url, bean, IntoDb);
					if (bean.getLocation() == null) {
						log.error("搜房poi数据解析错误:" + urlCode);
						continue;
					}
				} else {
					log.error("搜房页面执行url错误:" + urlCode);
				}
			} else {

			}
			urlCode = bean.getUrl();
			if (bean.getLocation() == null) {
				try {
					url = AntGetUrl.doGetGzip(urlCode, "gbk", true);
				} catch (Exception e) {
					log.error(e);
					e.printStackTrace();
					if (urlCode.equals("")) {
					} else {
						log.error("urlCode 被放入队列中:" + urlCode);
						queue.add(bean);
					}
					continue;
				}
			}
			// 执行页面解析 相关操作
			if (true) {// 处理原始页面
				FangAntParse.runUrl(url, bean, IntoDb, true);
				count++;
				if (count % queueBean.getPrintCount() == 0) {
					log.info("搜房页面剩余:" + queue.getSize(queue.info) + "\t爬取到:"
							+ bean.getUrl());
				}
			} else {
				log.error("搜房页面执行url错误:" + urlCode);
			}
		}
		log.info("搜房执行线程结束：" + Thread.currentThread().getName());
		threadCountEnd++;
		if (threadCountEnd >= queueBean.getThreadCount()) {
			queueBean.setExecIsRun(false);
		}
	}
	
	public static String doGet(String url, String encoding) {

		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams()
					.setConnectionTimeout(60000);
			client.getHttpConnectionManager().getParams()
					.setSoTimeout(600000);
			HttpMethod method = new GetMethod(new String(url.getBytes(),
					encoding));
			method.addRequestHeader("Content-Type",
					"application/x-www-form-urlencoded");
			method.addRequestHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
			method.removeRequestHeader("Proxy-Connection");
			method.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			method.addRequestHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3");
			//method.addRequestHeader("Accept-Encoding", "gzip, deflate");
			//method.addRequestHeader("Cookie", "city=www; __utma=147393320.525473397.1434073773.1436322492.1438306404.9; __utmz=147393320.1434073773.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); global_cookie=p58ilyu6y0igpgzxypi2daidk38iasyhx8h; unique_cookie=U_6jsx7de60l9h1crx62l446tq11licqyhw4j*10; __utmb=147393320.33.10.1438306404; __utmc=147393320; __utmt_t0=1; __utmt_t1=1; __utmt_t2=1");
			//method.addRequestHeader("Cache-Control", "max-age=0");
			method.addRequestHeader("Connection", "Keep-Alive");
			// 使用POST方法
			// HttpMethod method = new PostMethod("http://java.sun.com");
			client.executeMethod(client.getHostConfiguration(), method);
			InputStream is = method.getResponseBodyAsStream();
			Header header = method.getResponseHeader("Content-Encoding");
			InputStreamReader isr = null;
			GZIPInputStream gzin = null;
			boolean useGip = false;
			if (header == null) {
				isr = new InputStreamReader(is);
			} else {
				if (header.getValue().contains("gzip")) {
					useGip = true;
					gzin = new GZIPInputStream(is);
					isr = new InputStreamReader(gzin, encoding);
				}
			}
			java.io.BufferedReader br = new java.io.BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String tempbf;
			while ((tempbf = br.readLine()) != null) {
				sb.append(tempbf);
				sb.append("\r\n");
			}
			isr.close();
			if (useGip) {
				gzin.close();
			}
			// 释放连接
			method.releaseConnection();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static void main(String[] args) {
		String url=null;
		try {
			url = AntGetUrl.doGetGzip("http://shenganghaoyuan.fang.com/office/",
					"gbk",false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(url);
		FangAntParse.runUrl2(url);
		
	}
}
