package com.zj.parkExec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.zj.bean.FangInput2QueueBean;
import com.zj.bean.FangNewBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.FangAntParse;
import com.zj.parse.FangNewAntParse;
import com.zj.queue.FangNewAntQueue;
import com.zj.queue.QueueFather;

public class FangNewExecThread extends ExecThread implements Runnable {
	private static Logger log = Logger.getLogger(FangNewExecThread.class);
	private IntoDb intoDb = null;
	private QueueBean queueBean = null;
	private FangNewAntQueue queue = null;
	private QueueBean queueFirstBean = null;
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
	public FangNewExecThread(QueueBean queueBean, QueueBean queueFirstBean,
			FangNewAntQueue queue, IntoDb intoDb) {
		this.queue = queue;
		this.queueFirstBean = queueFirstBean;
		this.intoDb = intoDb;
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
			bean = (FangInput2QueueBean) isStop(queue, queueBean,
					queueFirstBean);
			if (bean == null) {
				break;
			}
			String url = "";
			try {
				url = AntGetUrl.doGetGzip(bean.getUrl(), "gbk", true);
			} catch (Exception e) {
				e.printStackTrace();
				log.error(e);
				log.info("被放回队列中:" + bean.getUrl());
				queue.add(bean);
				continue;
			}
			if (url.length() > 0) {
				// 查看是哪种数据集合
				System.out.println(bean.getUrl());
				Pattern p_1 = Pattern.compile("楼盘简介");
				Matcher m_1 = p_1.matcher(url);
				if (!m_1.find()) {
					// 那么执行老房源的数据
					FangInput2QueueBean inputBean = new FangInput2QueueBean();
					inputBean.setUrl(bean.getUrl());
					// FangNewAntParse.runUrl(url, inputBean, intoDb);
					FangNewBean fangNewBean = FangNewAntParse.runUrl(url,
							inputBean, intoDb);
					if (fangNewBean == null) {
					} else {
						// 执行详情页
						url = "";
						try {
//							System.out.println(fangNewBean.getUrl() + "house/"
//									+ fangNewBean.getFangCode() + "/housedetail.htm");
							url = AntGetUrl.doGetGzip(fangNewBean.getUrl() + "house/"
									+ fangNewBean.getFangCode() + "/housedetail.htm",
									"gbk", true);
						} catch (Exception e) {
							e.printStackTrace();
							log.error(e);
							log.info("被放回队列中:" + bean.getUrl());
							queue.add(bean);
							continue;
						}
					//	System.out.println(url);
						if (url.length() > 0) {
							// 获取详情页信息 并写入文件中
							FangNewAntParse
									.runDescUrl(url, fangNewBean, intoDb);
						}
					}
				} else {
					// 获取新房源信息
					FangInput2QueueBean inputBean = new FangInput2QueueBean();
					inputBean.setUrl(bean.getUrl());
					FangAntParse.runUrl(url, inputBean, intoDb,true);
				}
				count++;
				// log.info("搜房页面剩余:"+queue.getSize(queue.info)+"\t爬取到:"+urlCode.substring(0,urlCode.length()-1)+"-i3"+index+"/"+"\t"+countNum);
				if (count % queueBean.getPrintCount() == 0) {
					log.info("搜房新页面剩余:" + queue.getSize(queue.info) + "\t爬取到:"
							+ bean.getUrl());
				} else {
					log.error("搜房新页面执行url错误:" + bean.getUrl());
				}
			}
		}
		log.info("搜房执行线程结束：" + Thread.currentThread().getName());
		threadCountEnd++;
		if (threadCountEnd >= queueBean.getThreadCount()) {
			queueBean.setExecIsRun(false);
		}
	}

	/**
	 * 对//w37/添加为//d13-w37/格式
	 * 
	 * @param str
	 * @return
	 */
	public String add(String strSoucre, String str) {
		if (str.equals("")) {
			return strSoucre;
		}
		int index = strSoucre.substring(0, strSoucre.length() - 1).lastIndexOf(
				"/");
		return strSoucre.substring(0, index) + "/" + str + "-"
				+ strSoucre.substring(index + 1);
	}

}
