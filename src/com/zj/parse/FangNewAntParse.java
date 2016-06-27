package com.zj.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.util.JsonUtil;
import com.zj.bean.FangInput2QueueBean;
import com.zj.bean.FangNewBean;
import com.zj.bean.FangNewDescBean;
import com.zj.bean.LationLngLat;
import com.zj.bean.NewsBean;
import com.zj.intoDb.IntoDb;

/**
 * 地铁口对应的公交信息
 * @author Administrator
 *
 */
public class FangNewAntParse {
	private static Logger log = Logger.getLogger(FangNewAntParse.class);

	/**
	 * 从字符串中接写出fangcode
	 * @param str
	 * @return
	 */
	public static Long clearCode(String str)
	{
		try{
			
			return Long.parseLong(str.substring(str.indexOf("house-xm")+8,str.length()-1));
		}catch(Exception e)
		{
			return null;
		}
	}
	
	/**
	 * 
	 * @param url 首页信息+详情页
	 * @param IntoDb 输出队列
	 * @param inputBean为输入的地铁线路信息
	 * @param km 附近几公里 
	 */
	public static FangNewBean runUrl(String url,FangInput2QueueBean bean2,IntoDb intoDb)
	{
		FangNewBean bean=new FangNewBean();
		bean.setUrl(bean2.getUrl());
		//获取fangcode
		Pattern p_1=Pattern.compile("<h1>[\\s]*?<a[\\s]*?class[^>]*?>([^<]*?)</a>[\\s]*?</h1>");
		Matcher m_1=p_1.matcher(url);	
		int end=0;
		if(m_1.find())
		{
			bean.setBuildingName(m_1.group(1));
		}else{
			return null;
		}
		p_1=Pattern.compile("别名：([^<]*?)</span");
		m_1=p_1.matcher(url);		
		if(m_1.find())
		{
			end=m_1.end();
			bean.setBuildingNameAlias(m_1.group(1));
		}
		url=url.substring(end<0?0:end);
		end=0;
		
		 p_1 = Pattern.compile("楼盘详情</a>[\\s]*?<a[\\s]*?href=\"([^\"]*?)\"");
		 m_1 = p_1.matcher(url);

		if(m_1.find())
		{
			end=m_1.end();
			//从url中解析出fangcode
			String fangUrl=m_1.group(1);
			//获取code
			//设置搜房code
			//System.out.println(fangUrl);
			//System.out.println(fangUrl.substring(0,fangUrl.length()-1));
			bean.setFangCode(Long.parseLong(fangUrl.substring(fangUrl.substring(0,fangUrl.length()-1).lastIndexOf("/")+1,fangUrl.length()-4)));
		}
		if(bean.getFangCode()==0)
		{
			return null;
		}
		url=url.substring(end<0?0:end);
		end=0;
	
		p_1=Pattern.compile("<img[^>]*?src=\"([^\"]*?)\"[^>]*?效果图\"[\\s]*?/></a");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			bean.setEffectImg(m_1.group(1));
		}
		url=url.substring(end<0?0:end);
		end=0;
		p_1=Pattern.compile("<img[^>]*?src=\"([^\"]*?)\"[^>]*?交通图\"[\\s]*?/></a");
		m_1=p_1.matcher(url);		
		if(m_1.find())
		{
			end=m_1.end();
			bean.setTrifficImg(m_1.group(1));
		}
		url=url.substring(end<0?0:end);
		end=0;
		p_1=Pattern.compile("<img[^>]*?src=\"([^\"]*?)\"[^>]*?实景图\"[\\s]*?/></a");
		m_1=p_1.matcher(url);		
		if(m_1.find())
		{
			bean.setRealImg(m_1.group(1));
		}
		p_1=Pattern.compile("<img[^>]*?src=\"([^\"]*?)\"[^>]*?外景图\"[\\s]*?/></a");
		m_1=p_1.matcher(url);		
		if(m_1.find())
		{
			bean.setOuterImg(m_1.group(1));
		}
		p_1=Pattern.compile("<img[^>]*?src=\"([^\"]*?)\"[^>]*?周边配套图\"[\\s]*?/></a");
		m_1=p_1.matcher(url);		
		if(m_1.find())
		{
			bean.setAroundImg(m_1.group(1));
		}
		p_1=Pattern.compile("<img[^>]*?src=\"([^\"]*?)\"[^>]*?户型图\"[\\s]*?/></a");
		m_1=p_1.matcher(url);		
		if(m_1.find())
		{
			bean.setHouseTypeImg(m_1.group(1));
		}
		
		
		
		p_1=Pattern.compile(">([^<]*?)</li>[\\s]*?<li>最低价[^<]*?</li>[\\s]*?<li>[\\s]*?<span[^<]*?>([^<]*?)<");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			bean.setCurrentDate(m_1.group(1));
			bean.setCurrentLowPrice(m_1.group(2)+"元/平方米");
		}else{
			p_1=Pattern.compile(">([^<]*?)</li>[\\s]*?<li>均价[^<]*?</li>[\\s]*?<li>[\\s]*?<span[^<]*?>([^<]*?)<");
			m_1=p_1.matcher(url);
			if(m_1.find())
			{
				end=m_1.end();
				bean.setCurrentDate(m_1.group(1));
				bean.setCurrentLowPrice(m_1.group(2)+"元/平方米");
			}
		}
		url=url.substring(end<0?0:end);
		end=0;
		p_1=Pattern.compile("<li[^>]*?><span class=\"zt_ct\"[\\s]*?>([^<]*?)</span>([^<]*?)<");
		m_1=p_1.matcher(url);		
		while(m_1.find())
		{
			end=m_1.end();
			String key=m_1.group(1).trim();
			String value=m_1.group(2).trim();
			if(key.equals("租售状态"))
			{
				bean.setHireSaleSitation(value);
			}else if(key.equals("写字楼类型"))
			{
				bean.setBuildingCategory(value);
			}else if(key.equals("开间面积"))
			{
				bean.setOpenSpace(value);
			}else if(key.equals("电梯配置"))
			{
				bean.setElevatorPlat(value);
			}else if(key.equals("物 业 费"))
			{
				bean.setPropertyFee(value);
			}else if(key.equals("开盘时间"))
			{
				bean.setOpeningDate(value);
			}else if(key.equals("建筑面积"))
			{
				bean.setBuildSpace(value);
			}else if(key.equals("停 车 位"))
			{
				bean.setParkingNum(value);
			}else if(key.equals("物业公司"))
			{
				bean.setPropertyCompany(value);
			}else if(key.equals("物业地址"))
			{
				bean.setPropertyAddress(value);
			}
			else if(key.equals("所属商圈"))
			{
				bean.setTradingArea(value);
			}else if(key.equals("产权年限"))
			{
				bean.setEquityAgeLimit(value);
			}else if(key.equals("总楼层数"))
			{
				bean.setLayerNum(value);
			}else if(key.equals("标准层高"))
			{
				bean.setStanderLayerHight(value);
			}else if(key.equals("周边配套"))
			{
				bean.setCouplingPeriphery(value);
			}else if(key.equals("目标业态"))
			{
				bean.setTargetBusiness(value);
			}else if(key.equals("楼栋状况"))
			{
				bean.setBuildingCondition(value);
			}else if(key.equals("项目特色"))
			{
				bean.setProgramFeature(value);
			}else if(key.equals("标准层面积"))
			{
				bean.setStanderLayerSpace(value);
			}
			else if(key.equals("装修状况"))
			{
				bean.setFitmentSituation(value);
			}
				
		}
		p_1=Pattern.compile(">([^<‖]*?)</span>[\\s]*?<a[^>]*?>([^<]*?)</a>");
		m_1=p_1.matcher(url);		
		while(m_1.find())
		{
			String key=m_1.group(1).trim();
			String value=m_1.group(2).trim();
			if(key.equals("开 发 商"))
			{
				bean.setDevelopers(value);
			}	
			else if(key.equals("周边配套"))
			{
				bean.setCouplingPeriphery(value);
			}else if(key.equals("交通状况"))
			{
				bean.setTrifficSituation(value);
			}
		}
		
		//获取电话
		p_1=Pattern.compile("<strong>([^<]*?)</strong></dd>[\\s\\S]*?<strong>([^<]*?)</strong>[\\s]*?找[\\s]*?售楼处");
		m_1=p_1.matcher(url);		
		if(m_1.find())
		{
			end=m_1.end();
			bean.setSalePhoneInfo(m_1.group(1)+" "+m_1.group(2));
		}
		url=url.substring(end<0?0:end);
		end=0;
		
		end=0;
		p_1=Pattern.compile("最新动态</a>[\\s\\S]*?<a title=\"([^\"]*?)\"[\\s]*?href=\"([^\"]*?)\"");
		m_1=p_1.matcher(url);		
		if(m_1.find())
		{
			end=m_1.end();
			bean.setDesc(m_1.group(1));
			bean.setDescUrl(m_1.group(2));
		}
		url=url.substring(end<0?0:end);
		end=0;
		p_1=Pattern.compile("<li><a href=\"([^\"]*?)\"[^>]*?title=\"([^\"]*?)\"[^>]*?>[^<]*?</a>[\\s]*?<span>([^<]*?)</span>[\\s]*?</li>");
		m_1=p_1.matcher(url);		
		if(m_1.find())
		{
			end=m_1.end();
			NewsBean news=new NewsBean();
			news.setUrl(m_1.group(1));
			news.setBriefIntroduction(m_1.group(2));
			news.setPublishDate(m_1.group(3));
			bean.addNews(news);
		}
		url=url.substring(end<0?0:end);
		end=0;
		//获取poi数据
		LationLngLat location=FangAntParse.getLation(bean.getFangCode(),bean2.getCity());
		bean.setLocation(location);
		//将左中数据入库
		//System.out.println(JsonUtil.getJsonStr(bean));
		
		//从详情页中获取对应的数据
		return bean;
		//intoDb.add(JsonUtil.getJsonStr(bean));
	}

	
	
	/**
	 * 用来解析详情页
	 * @param url
	 * @param inputBean
	 * @param intoDb
	 */
	public static void runDescUrl(String url,FangNewBean inputBean,IntoDb intoDb)
	{
		FangNewDescBean desc=new FangNewDescBean();
		inputBean.setBuildingDescPage(desc);

		int end=0;
		Pattern p_1=Pattern.compile("<td[^>]*?><strong>([^<]*?)</strong>[^<]*?<span[^>]*?><a[^>]*?>([^<]*?)</a");
		Matcher m_1=p_1.matcher(url);
		while(m_1.find())
		{
			String name=m_1.group(1).replaceAll("(&nbsp;)","").trim();
			String value=m_1.group(2).replaceAll("(&nbsp;)","").trim();
		//	System.out.println(name+"\t"+value);
			if(name.equals("环线位置"))
			{
				desc.setCycleLineStation(value);
			}else if(name.equals("所属商圈"))
			{
				desc.setBusinessArea(value);
			}
		}
		
		
		 p_1=Pattern.compile("<td[^>]*?><strong>([^<]*?)</strong>([^<]*?)<");
		 m_1=p_1.matcher(url);

		while(m_1.find())
		{
			end=m_1.end();
			String name=m_1.group(1).replaceAll("(&nbsp;)","").trim();
			String value=m_1.group(2).replaceAll("(&nbsp;)","").trim();
			if(name.equals("写字楼类型"))
			{
				desc.setBuildCategory(value);
			}else if(name.equals("写字楼级别"))
			{
				desc.setBuildLevel(value);
			}else if(name.equals("租售状态"))
			{
				desc.setHireSaleSituation(value);
			}else if(name.equals("所属分期"))
			{
				desc.setStaging(value);
			}else if(name.equals("装修状况"))
			{
				desc.setFitmentSituation(value);
			}else if(name.equals("项目特色"))
			{
				desc.setProgramFeather(value);
			}else if(name.equals("容积率"))
			{
				desc.setVolumeRate(value);
			}else if(name.equals("绿化率"))
			{
				desc.setGreeningRate(value);
			}else if(name.equals("使用率"))
			{
				desc.setUseRate(value);
			}else if(name.equals("产权年限"))
			{
				desc.setEquityAgeLimit(value);
			}else if(name.equals("占地面积"))
			{
				desc.setFloorSpace(value);
			}else if(name.equals("建筑面积"))
			{
				desc.setBuildingSpace(value);
			}else if(name.equals("标准层面积"))
			{
				desc.setStanderLayerSpace(value);
			}else if(name.equals("开间面积"))
			{
				desc.setOpenSpace(value);
			}else if(name.equals("商业面积"))
			{
				desc.setBusinessSpace(value);
			}else if(name.equals("办公面积"))
			{
				desc.setOfficeSpace(value);
			}else if(name.equals("楼栋状况"))
			{
				desc.setBuildingCondition(value);
			}else if(name.equals("当期楼栋数"))
			{
				desc.setCurrentBuildingCount(value);
			}else if(name.equals("开盘时间"))
			{
				desc.setOpeningQuotationDate(value);
			}else if(name.equals("入住时间"))
			{
				desc.setCheckIndate(value);
			}else if(name.equals("物业管理费"))
			{
				desc.setPropertyFee(value);
			}else if(name.equals("售楼地址"))
			{
				desc.setSaleAddress(value);
			}else if(name.equals("物业地址"))
			{
				desc.setPropertyAddress(value);
			}
		}
		url=url.substring(end);
		end=0;
		p_1=Pattern.compile("项目房价[^<]*?</strong>[\\s]*?<span[^>]*?>([^<]*?)<strong[^>]*?>([^<]*?)</strong>([^<]*?)<");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			desc.setProjectHousePrice(m_1.group(1)+m_1.group(2)+m_1.group(3));
		}
		url=url.substring(end);
		end=0;
		p_1=Pattern.compile("项目配套</h2>[\\s]*?<div[^>]*?>[\\s]*([\\s\\S]*?)[\\s]*</div>");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			desc.setProjectMating(m_1.group(1));
		}
		url=url.substring(end);
		end=0;
		p_1=Pattern.compile("交通状况</h2>[\\s]*?<div[^>]*?>[\\s]*([\\s\\S]*?)[\\s]*</div>");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			desc.setTrafficSituation(m_1.group(1));
		}
		url=url.substring(end);
		end=0;
		p_1=Pattern.compile("内部设施</h2>[\\s]*?<div[^>]*?>[\\s]*([\\s\\S]*?)[\\s]*</div>");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			desc.setInnerInstitution(m_1.group(1));
		}
		url=url.substring(end);
		end=0;
		p_1=Pattern.compile("楼层状况</h2>[\\s]*?<div[^>]*?>[\\s]*([\\s\\S]*?)[\\s]*</div>");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			desc.setFloorSituation(m_1.group(1));
		}
		url=url.substring(end);
		end=0;
		p_1=Pattern.compile("车位信息</h2>[\\s]*?<div[^>]*?>[\\s]*([\\s\\S]*?)[\\s]*</div>");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			desc.setParkingInfo(m_1.group(1));
		}
		url=url.substring(end);
		end=0;
		p_1=Pattern.compile("项目简介</h2>[\\s]*?<div[^>]*?>[\\s]*([\\s\\S]*?)[\\s]*</div>");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			end=m_1.end();
			desc.setProjectDis(m_1.group(1));
		}
		url=url.substring(end);
		end=0;
		p_1=Pattern.compile("<strong>([^>]*?)</strong>([^>]*?)<br");
		m_1=p_1.matcher(url);
		while(m_1.find())
		{
			end=m_1.end();
			String name=m_1.group(1).replaceAll("[：:]","").trim();
			String value=m_1.group(2).trim();
			if(name.equals("销售阶段"))
			{
				desc.setSaleSection(value);
			}else if(name.equals("开工时间"))
			{
				desc.setStartWorkingDate(value);
			}else if(name.equals("竣工时间"))
			{
				desc.setCompletedDate(value);
			}else if(name.equals("预售许可证"))
			{
				desc.setForestsaleLicense(value);
			}else if(name.equals("物业管理费描述"))
			{
				desc.setPropertyFreeDis(value);
			}else if(name.equals("产权描述"))
			{
				desc.setEquityDis(value);
			}else if(name.equals("租金是否包含物业费"))
			{
				desc.setHireContainsfee(value);
			}else if(name.equals("待租空间信息"))
			{
				desc.setHireSpaceInfo(value);
			}else if(name.equals("出售类型"))
			{
				desc.setSaleCategory(value);
			}else if(name.equals("出租类型"))
			{
				desc.setHireCategory(value);
			}else if(name.equals("是否可分割"))
			{
				desc.setSplitIsTrue(value);
			}else if(name.equals("付款方式"))
			{
				desc.setPaymentMethod(value);
			}else if(name.equals("工程进度"))
			{
				desc.setProjectSchedule(value);
			}else if(name.equals("面积说明"))
			{
				desc.setSpaceExplain(value);
			}else if(name.equals("按揭银行"))
			{
				desc.setControlBrank(value);
			}
		}
		url=url.substring(end);
		end=0;

		p_1=Pattern.compile("开发商[^<]*?</strong><a[^>]*?>([^<]*?)<");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			desc.setDevelopers(m_1.group(1));
		}
		p_1=Pattern.compile("投资商[^<]*?</strong><a[^>]*?>([^<]*?)<");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			desc.setInvestBusiness(m_1.group(1));
		}
		p_1=Pattern.compile("物业管理公司[^<]*?</strong><a[^>]*?>([^<]*?)<");
		m_1=p_1.matcher(url);
		if(m_1.find())
		{
			desc.setPropertyMenageCompany(m_1.group(1));
		}
		intoDb.add(JsonUtil.getJsonStr(inputBean));
	}
}
