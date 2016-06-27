package com.zj.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.util.JsonUtil;
import com.zj.bean.JobAndCompanyBean;
import com.zj.bean.JobsBean;
import com.zj.bean.JobsInputQueueBean;
import com.zj.bean.JobsSonBean;
import com.zj.intoDb.IntoDb;

public class JobsAntParse {
	private static Logger log = Logger.getLogger(JobsAntParse.class);
	/**
	 * 
	 * @param url
	 * @param IntoDb 输出队列
	 * @param inputBean为输入的地铁线路信息
	 * @param km 附近几公里 
	 */
	public static void runUrl(String url,JobsInputQueueBean inputBean,int km,IntoDb intoDb)
	{
		JobsBean bean=new JobsBean();
		//获取工作年限
		Pattern p_1 = Pattern.compile("fromType=6\">([^<]*?)</a>");
		Matcher m_1 = p_1.matcher(url);
		int end=0;
		while(m_1.find())
		{
			end=m_1.end();
			JobsSonBean sonBean=get(m_1.group(1));
			if(sonBean==null)
			{
				continue;
			}else{
				bean.addWorkExperience(sonBean);
			}
			
		}
		//获取发布日期
		url=url.substring(end<0?0:end);
		end=0;
		p_1 = Pattern.compile("fromType=5\">([^<]*?)</a>");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			end=m_1.end();
			JobsSonBean sonBean=get(m_1.group(1));
			if(sonBean==null)
			{
				continue;
			}else{
				bean.addReleaseDate(sonBean);
			}
		}
		//学历要求
		url=url.substring(end<0?0:end);
		end=0;
		p_1 = Pattern.compile("fromType=7\">([^<]*?)</a>");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			end=m_1.end();
			JobsSonBean sonBean=get(m_1.group(1));
			if(sonBean==null)
			{
				continue;
			}else{
				bean.addEducationBackground(sonBean);
			}
		}
		
		//公司类型
		url=url.substring(end<0?0:end);
		end=0;
		p_1 = Pattern.compile("fromType=8\">([^<]*?)</a>");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			end=m_1.end();
			JobsSonBean sonBean=get(m_1.group(1));
			if(sonBean==null)
			{
				continue;
			}else{
				bean.addCompanyProperty(sonBean);
			}
		}
		//工资范围
				url=url.substring(end<0?0:end);
				end=0;
				p_1 = Pattern.compile("fromType=21\">([^<]*?)</a>");
				m_1 = p_1.matcher(url);
				while(m_1.find())
				{
					end=m_1.end();
					JobsSonBean sonBean=get(m_1.group(1));
					if(sonBean==null)
					{
						continue;
					}else{
						bean.addMonthlyPay(sonBean);
					}
				}
		//全职
		url=url.substring(end<0?0:end);
		end=0;
		p_1 = Pattern.compile("fromType=22\">([^<]*?)</a>");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			end=m_1.end();
			JobsSonBean sonBean=get(m_1.group(1));
			if(sonBean==null)
			{
				continue;
			}else{
				bean.addJobCategory(sonBean);
			}
		
		}
		url=url.substring(end<0?0:end);
		//公司规模
		
		end=0;
		p_1 = Pattern.compile("fromType=\">([^<]*?)</a>");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			end=m_1.end();
			JobsSonBean sonBean=get(m_1.group(1));
			if(sonBean==null)
			{
				continue;
			}else{
				bean.addCompanyScale(sonBean);
			}
		}
		url=url.substring(end<0?0:end);
		//解析公司对应工作职位
		p_1=Pattern.compile("class=\"jobname\"[^>]*?>([^<]*?)</a>[\\s\\S]*?class=\"coname\"[^>]*?>([^<]*?)</a>");
		m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			//end=m_1.end();
			JobAndCompanyBean company=new JobAndCompanyBean();
			company.setCompanyName(m_1.group(2));
			company.setJobName(m_1.group(1));
			bean.addJobDesc(company);
		}
		if(bean.isOk())
		{
			bean.setSubwayLine(inputBean.getSubWayLine());
			bean.setSubWayLineCode(inputBean.getSubWayLineCode());
			bean.setSubwayStation(inputBean.getSubWayStation());
			bean.setNearbyKm(km);
			//如果有效则转换json
			bean.setUrl(inputBean.getUrl());
			bean.setWorkCategory(inputBean.getWorkCategory());
			String result=JsonUtil.getJsonStr(bean);
			
			//System.out.println("result:"+result);
			intoDb.add(result);
		}
	}
	/**
	 * 获取具体的类型对应的招聘数
	 * @return null则表示为无效
	 */
	public static JobsSonBean get(String str)
	{
		str=str.trim();
		if(str.contains("("))
		{
			//System.out.println(str);
			JobsSonBean sonBean=new JobsSonBean();
			sonBean.setName(str.substring(0,str.indexOf("(")));
			String coun=str.substring(str.indexOf("(")+1,str.indexOf(")"));
			if(coun.length()>0)
			{
				try{
					sonBean.setCount(Integer.parseInt(coun));
				}catch(Exception e)
				{
					sonBean.setCount(0);
				}
			}else{
				return null;
			}
			return sonBean;
		}else
		{
			return null;
		}
	}
	
}
