package com.zj.parkExec;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.zj.bean.FangListBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;
import com.zj.parse.FangListAntParse;
import com.zj.queue.FangListAntQueue;

public class FangListExecThread implements Runnable{
private static Logger log = Logger.getLogger(FangListExecThread.class);
	private IntoDb IntoDb=null;
	private QueueBean queueBean=null;
	private FangListAntQueue queue=null;
	private QueueBean queueFirstBean=null;
	/**
	 * 有效执行次数
	 */
	private long count=0;
	/**
	 * 结束的线程数
	 */
	private int threadCountEnd=0;
	/**
	 * 初始化程序
	 */
	public FangListExecThread(QueueBean queueBean,QueueBean queueFirstBean,FangListAntQueue queue,IntoDb IntoDb)
	{
		this.queueFirstBean=queueFirstBean;
		this.queue=queue;
		this.IntoDb=IntoDb;
		this.queueBean=queueBean;
	}
	/**
	 * 线程执行方法
	 */
	@SuppressWarnings("unchecked")
	public void run()
	{
		log.info("搜房执行线程启动："+Thread.currentThread().getName());
		while(true)
		{
			if(queueBean.isEnd())
			{//如果该bean失效 一个周期完成则跳出
				log.info("fang 线程跳出:"+Thread.currentThread().getName());
				break;
			}
			try {
				Thread.sleep(queueBean.getUrlParseCycleTime());
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(!queueBean.isStart())
			{
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			FangListBean bean=null;
			boolean isBreak=false;
			int nullCount=0;
			while(true)
			{
				bean=queue.get(queueBean);
				if(bean==null)
				{
					nullCount++;
					if(nullCount>=3 &&(queueFirstBean==null || !queueFirstBean.isExecIsRun()))
					{
						queueBean.setEnd(true);
						isBreak=true;
						break;
					}
					try {
						Thread.sleep(queueBean.getUrlParseCycleTime()+1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					continue;
				}else{
					//结束
					break;
				}
			}
			if(isBreak)
			{
				break;
			}
			if(bean==null)
			{
				continue;
			}
			//执行 页面获取
			////http://search.51job.com/jobsearch/search_result.php?fromJs=1&jobarea=010000&funtype=0000&industrytype=00&issuedate=9&keywordtype=2&lang=c&stype=3&address=%E8%8B%B9%E6%9E%9C%E5%9B%AD&radius=0.03&fromType=20
			int int1=0;
			int int2=0;
			while(true)
			{
				//配置并查看源码中是否最大页数为100
				if(bean.getArea().size()==0)
				{
					break;
				}
				//System.out.println(bean.getArea().get(0)+"/");
				String urlCode=add(bean.getArea().get(0)+"/",bean.getHire().get(int1));
				//System.out.println(urlCode);
				urlCode=add(urlCode,bean.getSpace().get(int2));
				//System.out.println(urlCode);
				urlCode=add(urlCode,bean.getBuildingCode());
				String url="";
				//System.out.println(urlCode);
				try{
					url=AntGetUrl.doGetGzip(urlCode,"gbk",false);
				}catch(Exception e)
				{
					log.error(e);
					e.printStackTrace();
					log.error("urlCode 被放入队列中:"+urlCode);
					queue.add(bean);
					continue;
				}
				//执行页面解析 相关操作
				//System.out.println(url);
				//判断页面是否超过100;
				Pattern p_2 = Pattern.compile("<span class=\"fy_text\">1/([^<]*?)</span>");
				Matcher m_2 = p_2.matcher(url);
				int countNum=0;
				if(m_2.find())
				{
					log.info("页面数量:"+m_2.group(1));
					//判断是否为100
					if(m_2.group(1).trim().equals("100"))
					{
						if(int1!=0)
						{
							int2++;
							log.info("搜房页面细分后依然出现100"+urlCode);
						}else{
							int1++;
							continue;
						}
					}
					countNum=Integer.parseInt(m_2.group(1));
				}else{
					log.info("搜房页面不存在房源信息:"+urlCode);
					break;
				}
				//遍历全部页面
				int index=0;
				while(index<countNum)
				{
					index++;
					if(index>1)
					{
						try{
							//System.out.println(urlCode.substring(0,urlCode.length()-1)+"-i3"+index+"/");
							url=AntGetUrl.doGetGzip(urlCode.substring(0,urlCode.length()-1)+"-i3"+index+"/","gbk",false);
						}catch(Exception e)
						{
							e.printStackTrace();
							log.error(e);
							continue;
						}
					}
					if(url.length()>0)
					{
						//System.out.println("urlCode:"+urlCode);
						FangListAntParse.runUrl(url,bean,IntoDb);
						count++;
						//log.info("搜房页面剩余:"+queue.getSize(queue.info)+"\t爬取到:"+urlCode.substring(0,urlCode.length()-1)+"-i3"+index+"/"+"\t"+countNum);
						if(count%queueBean.getPrintCount()==0)
						{
							log.info("搜房页面剩余:"+queue.getSize(queue.info)+"\t爬取到:"+urlCode.substring(0,urlCode.length()-1)+"-i3"+index+"/"+"\t"+countNum);
						}
					}else{
						log.error("搜房页面执行url错误:"+urlCode.substring(0,urlCode.length()-1)+"-i3"+index+"/");
					}
				}
				System.out.println(count+"\t"+int1+"\t"+int2);
				//判断是否跳出
				//如果一个周期执行完成则结束
				if(int1==0)
				{
					break;
				}else if(int2==0)
				{
					break;
				}else if(int2==bean.getSpace().size())
				{
					break;
				}
			}
		}
		log.info("搜房执行线程结束："+Thread.currentThread().getName());
		threadCountEnd++;
		if(threadCountEnd>=queueBean.getThreadCount())
		{
			queueBean.setExecIsRun(false);
		}
	}
	
	/**
	 * 对//w37/添加为//d13-w37/格式
	 * @param str
	 * @return
	 */
	public String add(String strSoucre,String str)
	{
		if(str.equals(""))
		{
			return strSoucre;
		}
		int index=strSoucre.substring(0,strSoucre.length()).lastIndexOf("/");
		int index2=strSoucre.substring(0,strSoucre.length()-1).lastIndexOf("/");
		if(index-index2==1)
		{
			return strSoucre.substring(0,index2+1)+str+"/";
		}else{
			return strSoucre.substring(0,index2+1)+str+"-"+strSoucre.substring(index2+1);
		}
	}
	
	

}
