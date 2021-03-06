package com.zj.parse;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.util.DateFormat;
import com.util.JsonUtil;
import com.zj.bean.FangBean;
import com.zj.bean.FangInputQueueBean;
import com.zj.bean.FangMonthCountBean;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;

/**
 * 地铁口对应的公交信息
 * @author Administrator
 *
 */
public class FangAntParse {
	private static Logger log = Logger.getLogger(FangAntParse.class);
	/**
	 * 
	 * @param url
	 * @param IntoDb 输出队列
	 * @param inputBean为输入的地铁线路信息
	 * @param km 附近几公里 
	 */
	public static void runUrl(String url,FangInputQueueBean inputBean,IntoDb intoDb)
	{
		FangBean result=new FangBean();
		//将房源的相关信息放入 result中
		result.setFangListc(inputBean);
		//获取首末车相关信息
		Pattern p_1 = Pattern.compile("<img id=\"sqlogo\"[^>]*?src=\"([^\"]*?)\"");
		Matcher m_1 = p_1.matcher(url);
		int end=0;
		if(m_1.find())
		{
			end=m_1.end();

			//设置logo
			result.setLogoUrl(m_1.group(1));
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("span[^>]*?>([^<]*?)</span> 次浏览");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			if(m_1.group(1).trim().length()<1)
			{
				
			}else{
			//设置浏览次数
			result.setScanCount(Integer.parseInt(m_1.group(1)));
			}
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("span[^>]*?>([^<]*?)</span> 位认证业主");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			if(m_1.group(1).trim().length()<1)
			{
				
			}else{
			//设置浏览次数
			result.setApproveOwner(Integer.parseInt(m_1.group(1)));
			}
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("<span class=\"biaoti\">([^<]*?)</span");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//设置写字楼名
			result.setOfficeBuildingName(m_1.group(1));
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("<span class=\"gray6\">([^<]*?)<");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//设置地区 以及cbd属性
			String[] strTemp=null;
			if(m_1.group(1).contains("["))
			{
				strTemp=m_1.group(1).substring(m_1.group(1).indexOf("[")+1,m_1.group(1).indexOf("]")).split("[\\s]");
			}if(strTemp==null)
			{}
			else if(strTemp.length==2)
			{
				result.setArea(strTemp[0]);
				result.setCbdCategory(strTemp[1]);
			}else{
				//数据异常
				System.out.println("地域地址数据异常:"+m_1.group(1));
			}
			url=url.substring(end<0?0:end);
		}

		end=0;
		p_1 = Pattern.compile("span title=\"[^>]*?>([^<]*?)</span></span");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//设置写字楼名
			result.setAddress(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("本月出售评估价:</strong>[^<]*?<strong[^>]*?>([^<]*?)</strong>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//月出售
			result.setCurrentMonthSalePrice(m_1.group(1).trim());
			if(m_1.group(2).equals("暂无资料")||m_1.group(2).equals("")||!m_1.group(2).contains("，"))
			{}else{
			result.setCurrentMonthSalePriceCategory(m_1.group(2).substring(0,m_1.group(2).indexOf("，")).trim());
			}
			url=url.substring(end<0?0:end);
		}
	
		end=0;
		p_1=Pattern.compile("[\\s\\S]*?>([^>]*?)套出售中");
		m_1 = p_1.matcher(url.substring(0, 200));
		if(m_1.find())
		{
			//end=m_1.end();
			result.setSaleCount(Integer.parseInt(m_1.group(1)));
			url=url.substring(end<0?0:end);
		}
		end=0;
		p_1 = Pattern.compile("本月出租评估价:[^<]]*?</strong>[^<]*<strong[^>]*?>([^<]*?)</strong>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//月出租
			result.setCurrentMonthHirePrice(m_1.group(1).trim());
			if(m_1.group(2).equals("暂无资料")||m_1.group(2).equals("")||!m_1.group(2).contains("，"))
			{}else{
			result.setCurrentMonthHirePriceCategory(m_1.group(2).substring(0,m_1.group(2).indexOf("，")).trim());
			}
			url=url.substring(end<0?0:end);
		}
		end=0;
		p_1=Pattern.compile("[\\s\\S]*?>([^>]*?)套出租中");
		m_1 = p_1.matcher(url.substring(0, 200));
		if(m_1.find())
		{
			//end=m_1.end();
			result.setHireCount(Integer.parseInt(m_1.group(1)));
			url=url.substring(end<0?0:end);
		}
		end=0;
		p_1 = Pattern.compile("物业类别：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//设置物业
			result.setTenement(m_1.group(1).trim());
		}
		url=url.substring(end);
		end=0;
		p_1 = Pattern.compile("物业类别：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//设置写字楼地址
			result.setTenement(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("总 层 数：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//设置写字楼地址
			result.setLayerCount(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("物 业 费：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//设置写字楼地址
			//result.setTenementFee(m_1.group(1).trim());
			if(m_1.group(1).contains("元"))
			{
				result.setTenementFee(m_1.group(1).substring(0,m_1.group(1).indexOf("元")));
				result.setTenementFeeCategory(m_1.group(1).substring(m_1.group(1).indexOf("元")));
			}else{
				result.setTenementFee(m_1.group(1));
			}
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("建筑面积：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//设置写字楼地址
			result.setBuildArea(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("得 房 率：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			//设置写字楼地址
			if(m_1.group(1).contains("暂无"))
			{
				result.setReceiveRate("null");
			}else{
			result.setReceiveRate(m_1.group(1).trim());
			}
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("竣工时间：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setCompleteTime(DateFormat.transcateDate(m_1.group(1)));
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("电梯数量：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setElevatorCount(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("停 车 位：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setParkingCount(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("物业公司：([^<]*?)</d");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setTenementCompany(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("href=\"([^\"]*?)\"[^>]*?><img[^>]*?alt=\"更多详情");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setDisUrl(m_1.group(1));
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("楼盘简介</dt>[\\s\\S]*?<div class=\"jianjie\">([^>]*?)<");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setBuildAbstruct(m_1.group(1));
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("高层环线位置：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setHightCycleStation(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("项目特色：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setProjectFeather(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("建筑类别：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setBuildCategory(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("是否可分割：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setIsSplit(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("是否涉外：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setIsInvolveOut(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("空　　调：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setAirCondition(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("装修状况：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setFitmentStatus(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("占地面积：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setFloorSpace(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("标准层面积：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setStanderSpace(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("开间面积：([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setOpenSpace(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
	
		end=0;
		p_1 = Pattern.compile("开 发 商：<span title=\"([^\"]*?)\"");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			result.setDeveloper(m_1.group(1).trim());
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("公交：<span title=\"([^\"]*?)\"");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			String[] strTemp=m_1.group(1).split("、");
			for(String st:strTemp)
			{
				result.addBusAndStation(st);
			}
			url=url.substring(end<0?0:end);
		}
		
		end=0;
		p_1 = Pattern.compile("地铁：<span title=\"([^\"]*?)\"");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			String[] strTemp=m_1.group(1).split("(；  )");
			for(String st:strTemp)
			{
				result.addSubway(st);
			}
			url=url.substring(end<0?0:end);
		}
		
		
		
		end=0;
		p_1 = Pattern.compile("<input type=\"button\"[^>]*?window.open([^\"]*?)\"");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			//获取写字楼code
			end=m_1.end();
			//System.out.println(m_1.group(1));
			if(m_1.group(1).contains("newcode="))
			{
				result.setFangCode(m_1.group(1).substring(m_1.group(1).indexOf("newcode=")+8,m_1.group(1).length()-2));
			}else{
				System.out.println("写字楼code获取错误");
			}
			url=url.substring(end<0?0:end);
		}
	
		end=0;
		p_1 = Pattern.compile("<div class=\"shipin[\\s\\S]*?<img [^>]*?src=\"([^\"]*?)\" alt=\"");
		m_1 = p_1.matcher(url);
		int imgFlag=0;
		while(m_1.find())
		{
			imgFlag++;
			//添加各种图
			end=m_1.end();
			if(imgFlag==1)
			{
				//外景图
				result.setOutdoorImgUrl(m_1.group(1));	
			}else if(imgFlag==2)
			{
				//交通图
				result.setTrafficImgUrl(m_1.group(1));
			}else if(imgFlag==3)
			{
				//实景图
				result.setFactImgUrl(m_1.group(1));
			}else if(imgFlag==4)
			{
				//平面图
				result.setPlantImgUrl(m_1.group(1));
				break;
			}
			
		}
		url=url.substring(end<0?0:end);

		//获取房价走势
		String urlPrice="http://pinggun.soufun.com/estimate/process/makechartdataOffice.aspx?dis=&newcode="+result.getFangCode();
		urlPrice+="&city=&district=&commerce=&isprojname=";
		while(true)
		{
			try {
				url = AntGetUrl.doGet(urlPrice,"gbk");
				//System.out.println("房价:"+url);
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		//获取对应信息
		p_1 = Pattern.compile("\"labels\":[\\s]*?\\[([^\\]]*?)\\]");
		m_1 = p_1.matcher(url);
		String[] strMonth=null;
		List<String> price=new ArrayList<String>();
		List<String> yearl=new ArrayList<String>();
		if(m_1.find())
		{
			//获取写字楼code
			strMonth=m_1.group(1).replaceAll("[\\s\"]", "").split("(,)");
		}
		p_1 = Pattern.compile("\"value\":[\\s]*?([0-9\\.]*?),[\\s]*?\"tip\":[\\s]*?\"([\\d]{4})");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			price.add(m_1.group(1));
			yearl.add(m_1.group(2));
		}
		if(strMonth!=null)
		{
			if(strMonth.length==price.size())
			{
				for(int j=0;j<price.size();j++)
				{
					FangMonthCountBean bean=new FangMonthCountBean();
					bean.setMoney(Double.parseDouble(price.get(j)));
					bean.setMonth(yearl.get(j)+"-"+strMonth[j]);
					//System.out.println("年月日:"+bean.getMonth());
					result.addPriceTrendValue(bean);
				}
			}
		}		
		
		//获取租金走势
		 urlPrice="http://pinggun.soufun.com/estimate/process/makerentchartdataOffice.aspx?dis=&newcode="+result.getFangCode();
			urlPrice+="&city=&district=&commerce=&isprojname=";
			while(true)
			{
				try {
					url = AntGetUrl.doGet(urlPrice,"gbk");
					//System.out.println("租金:"+url);
					break;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
			//获取对应信息
			p_1 = Pattern.compile("\"labels\":[\\s]*?\\[([^\\]]*?)\\]");
			m_1 = p_1.matcher(url);
			strMonth=null;
			price=new ArrayList<String>();
			yearl=new ArrayList<String>();
			if(m_1.find())
			{
				//获取写字楼code
				strMonth=m_1.group(1).replaceAll("[\\s\"]", "").split("(,)");
			}
			p_1 = Pattern.compile("\"value\":[\\s]*?([0-9\\.]*?),[\\s]*?\"tip\":[\\s]*?\"([\\d]{4})");
			m_1 = p_1.matcher(url);
			while(m_1.find())
			{
				price.add(m_1.group(1));
				yearl.add(m_1.group(2));
			}
			if(strMonth!=null)
			{
				if(strMonth.length==price.size())
				{
					for(int j=0;j<price.size();j++)
					{
						FangMonthCountBean bean=new FangMonthCountBean();
						bean.setMoney(Double.parseDouble(price.get(j)));
						bean.setMonth(yearl.get(j)+"-"+strMonth[j]);
						result.addHireTrendValue(bean);
					}
				}
			}
			//获取百度poi数据
//			String mapUrl="http://esf.fang.com/map/newhouse/ShequMap.aspx?newcode="+result.getFangCode();
//			while(true)
//			{
//				try {
//					url = AntGetUrl.doGetGzip(mapUrl,"gbk");
//					//System.out.println("租金:"+url);
//					break;
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//					try {
//						Thread.sleep(2000);
//					} catch (InterruptedException e1) {
//						// TODO Auto-generated catch block
//						e1.printStackTrace();
//					}
//				}
//			}
//			//获取对应信息
//			p_1 = Pattern.compile(",[\\s]*?px:\"([^\"]*?)\"[\\s]*?,[\\s]*?py:\"([^\"]*?)\"");
//			m_1 = p_1.matcher(url);
//			if(m_1.find())
//			{
//				LationLngLat location=new LationLngLat();
//				location.setLng(Double.parseDouble(m_1.group(1)));
//				location.setLat(Double.parseDouble(m_1.group(2)));
//				result.setLocation(location);
//			}
			// 加入入库队列中
			intoDb.add(JsonUtil.getJsonStr(result));
		
	}
	
	public static void main(String[] args) throws Exception {
//		System.out.println("00sdf".replaceAll("(^[0]*+)",""));
//		String str="('http://www.fang.com/ask/Ask_StepTwo.aspx?asktitle=%BD%A8%CD%E2SOHO%D0%B4%D7%D6%C2%A5&newcode=1010087100')";
//		System.out.println(str.substring(str.indexOf("newcode=")+8,str.indexOf(')')-1));
//		String url="";
//		String mapUrl="http://esf.fang.com/map/newhouse/ShequMap.aspx?newcode=1010132225";
//		while(true)
//		{
//			try {
//				url = AntGetUrl.doGetGzip(mapUrl,"gbk");
//				//System.out.println("租金:"+url);
//				break;
//			} catch (Exception e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//				try {
//					Thread.sleep(2000);
//				} catch (InterruptedException e1) {
//					// TODO Auto-generated catch block
//					e1.printStackTrace();
//				}
//			}
//		} 
//		System.out.println(url);
//		//获取对应信息
//		Pattern p_1 = Pattern.compile(",[\\s]*?px:\"([^\"]*?)\"[\\s]*?,[\\s]*?py:\"([^\"]*?)\"");
//		Matcher m_1 = p_1.matcher(url);
//		if(m_1.find())
//		{
//			System.out.println(m_1.group(1));
//			System.out.println(m_1.group(2));
//		}
//		
//	}
//		String url="http://guoyingdasha.fang.com/office/";
//		String urlCode=AntGetUrl.doGetGzip(url, "gbk");
//		FangAntParse.runUrl(urlCode,null,null);
		String str="元/平米·天，环比上月上涨";
		
		System.out.println(str.substring(0,str.indexOf("，")).trim());
		
		
		str="4923.htm\", \"width\": 2, \"dot-size\": 3, \"halo-size\": 1, \"loop\": false, \"colour\": \"#F24D00\", \"values\": [ { \"value\": 30012, \"tip\": \"2014\u5E7406\u6708\u003Cbr>#val#\u5143/\u5E73\u65B9\u7C73\" }, { \"value\": 30008, \"tip\": \"2014\u5E7407\u6708\u003Cbr>#val#\u5143/\u5E73\u65B9\u7C73\" }, { \"value\": 2930";
		Pattern p_1 = Pattern.compile("\"value\":[\\s]*?([0-9\\.]*?),[\\s]*?\"tip\":[\\s]*?\"([\\d]*?)年");
		Matcher m_1 = p_1.matcher(str);
		System.out.println(str);
		while(m_1.find())
		{
			System.out.println(m_1.group(1)+"\t"+m_1.group(2));
		}
	}

}
