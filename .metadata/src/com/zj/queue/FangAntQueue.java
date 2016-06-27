package com.zj.queue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.db.Redis;
import com.util.DateFormat;
import com.util.JsonUtil;
import com.zj.bean.FangInputQueueBean;
import com.zj.bean.QueueBean;
import com.zj.crawler.AntGetUrl;

/**
 * 停车信息 队列容器
 * @author Administrator
 *
 */
@SuppressWarnings("rawtypes")
public class FangAntQueue extends QueueFather{
	private  Logger log = Logger.getLogger(FangAntQueue.class);
	/**
	 * 队列容器
	 * 其中存储的为
	 */
	public  LinkedBlockingQueue<FangInputQueueBean> info=null;
	/**
	 * 初始化队列信息
	 */
	@SuppressWarnings("unchecked")
	public  void init(QueueBean queueBean,String listName2,boolean flag)
	{

		if(queueBean.getQueueInputName()!=null)
		{
			redis=new Redis(queueBean.getInputQueueUrl());
			listName=listName2;
			isRedis=true;
		}else
		info=new LinkedBlockingQueue<FangInputQueueBean>(queueBean.getInputQueueNum());
		//>([^<]*?)</option>([^<]*?)</select> 页 </span>
		if(!flag)
			return;
		String url="";
		while(true)
		{
			try {
				url = AntGetUrl.doGetGzip("http://office.fang.com/loupan/house/","gbk");
				break;
			} catch (Exception e) {
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		//System.out.println("url:"+url);
		//i3
		Pattern p_1 = Pattern.compile(">共([\\d]*?)页</span>");
		//获取最大页面数
		Matcher m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			int pageSum=0;
			if(m_1.group(1).length()>0)
			{
				//pageSum=Integer.parseInt(m_1.group(1).substring(m_1.group(1).indexOf("/")+1,m_1.group(1).indexOf("页")).trim());
				pageSum=Integer.parseInt(m_1.group(1));
			}
			//获取当前也面得所有数据
			Regex(url);
			for(int i=2;i<=pageSum;i++)
			{
				System.out.println("读取列表页:"+"http://office.fang.com/loupan/house/i3"+i+"/"+"\t总数:"+pageSum);
				try{
				url = AntGetUrl.doGetGzip("http://office.fang.com/loupan/house/i3"+i+"/","gbk");
				Regex(url);
				}catch(Exception e)
				{
					
				}
				//获取当前也面得所有数据
			
			}
		}
		log.info("搜房网信息初始化完成");

	}
	
	public void Regex(String url)
	{
		
		Pattern p_1 = Pattern.compile("<dt class=\"img rel floatl\">([\\s\\S]*?)</dl>");
		//获取每一个页面对应的写字楼信息
		Matcher m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			FangInputQueueBean result=new FangInputQueueBean();
			result.setCity("北京");
			String buildUrl=m_1.group(0);
			Pattern p_2 = Pattern.compile("<img[^>]*?src=\"([^\"]*?)\"[^>]*?src2[^>]*?><");
			//获取获取第一张logo
			Matcher m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setLogoUrl(m_2.group(1));
			}
			p_2 = Pattern.compile("<strong>([^<]*?)</strong>");
			//获取写字楼名
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setOfficeBuildingName(m_2.group(1));
			}
			p_2 = Pattern.compile("href=\"([^\"]*?)\"[^>]*?>([^<]*?)</a></p>");
			//获取写字楼名
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				//第一个为写字楼的url
				result.setUrl(m_2.group(1));
				//第二个为写字楼名
				result.setOfficeBuildingName(m_2.group(2));
				result.setOfficeBuildingName(m_2.group(2));
			}
			p_2 = Pattern.compile("地址：([^<]*?)<");
			//获取写字楼地址
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				//设置地址
				result.setAddress(m_2.group(1).substring(m_2.group(1).indexOf("]")+1).trim());
				//设置属于那个区
				String[] strTemp=m_2.group(1).substring(m_2.group(1).indexOf("[")+1,m_2.group(1).indexOf("]")).split("[\\s]");
				if(strTemp.length==2)
				{
					result.setArea(strTemp[0]);
					result.setCbdCategory(strTemp[1]);
				}else{
					//数据异常
					System.out.println("地域地址数据异常:"+m_2.group(1));
				}
			}
			p_2 = Pattern.compile("<span class=\"dtjt[^\"]*?\"><a[^>]*?href=\"([^\"]*?)\"");
			//地图url
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setMapUrl(m_2.group(1));
			}
			p_2 = Pattern.compile("类型：([^<]*?)<");
			//写字楼类型
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setOfficeBuildingCategory(m_2.group(1));
			}
			p_2 = Pattern.compile("物业费：([^<]*?)<");
			//物业费
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				if(m_2.group(1).contains("元"))
				{
					result.setTenementFee(m_2.group(1).substring(0,m_2.group(1).indexOf("元")));
					result.setTenementFeeCategory(m_2.group(1).substring(m_2.group(1).indexOf("元")));
				}else{
					result.setTenementFee(m_2.group(1));
				}
			}
			p_2 = Pattern.compile("竣工时间：([^<]*?)<");
			//写字楼类型
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setCompleteTime(DateFormat.transcateDate(m_2.group(1)));
			}
			p_2 = Pattern.compile("售价：<span[^>]*?>([^<]*?)</span><span[^>]*?>([^<]*?)</span");
			//售价
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setSalePrice(m_2.group(1));
				result.setSalePriceCategory(m_2.group(2));
			}
			p_2 = Pattern.compile("租金：<span[^>]*?>([^<]*?)</span><span[^>]*?>([^<]*?)</span");
			//租金
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setHirePrice(m_2.group(1));
				result.setHirePriceCategory(m_2.group(2));
			}
			p_2 = Pattern.compile("href=\"([^\"]*?)\"[^>]*?>出租房源</a><span[^>]*?>（([^）]*?)）");
			//出租房源
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setHireHouseSouceUrl(m_2.group(1));
				result.setHireHouseSouce(Integer.parseInt(m_2.group(2)));
			}
			
			p_2 = Pattern.compile("href=\"([^\"]*?)\"[^>]*?>出售房源</a><span[^>]*?>（([^）]*?)）");
			//出售房源
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setSaleHouseSourceUrl(m_2.group(1));
				result.setSaleHouseSource(Integer.parseInt(m_2.group(2)));
			}
			p_2 = Pattern.compile("href=\"([^\"]*?)\"[^>]*?>相册</a><span[^>]*?>（([^）]*?)）");
			//相册
			m_2 = p_2.matcher(buildUrl);
			if(m_2.find())
			{
				result.setPhotoAlbumUrl(m_2.group(1));
				result.setPhotoAlbum(Integer.parseInt(m_2.group(2)));
			}
			//加入队列
			 if(isRedis)
        	 {
        		 try {
					redis.rpush(listName, JsonUtil.getJsonStr(result));
				} catch (Exception e) {
					e.printStackTrace();
				}
        	 }else{
        		 add(result);
        	 }
		}
	}

	/**
	 * 
	 * @return 如果为 null 则一直挂在此处
	 */
	@SuppressWarnings("unchecked")
	public  FangInputQueueBean get(QueueBean bean)
	{
		return (FangInputQueueBean)getQueueBean(bean,info,FangInputQueueBean.class,log,"搜房队列信息为空");
	}
	/**
	 * 添加内容
	 * @param bean
	 */
	@SuppressWarnings("unchecked")
	public void add(FangInputQueueBean bean)
	{
		addQueueBean(bean,info);
	}
} 
