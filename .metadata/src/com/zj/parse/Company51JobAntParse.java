package com.zj.parse;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.mortbay.log.Log;

import com.util.JsonUtil;
import com.zj.bean.Company51JobBean;
import com.zj.bean.JobsInputQueueBean;
import com.zj.bean.LationLngLat;
import com.zj.exec.MainStatic;
import com.zj.intoDb.IntoDb;
import com.zj.queue.Company51JobAntQueue;

public class Company51JobAntParse {
	private static Logger log = Logger.getLogger(Company51JobAntParse.class);
	/**
	 * 返回是否 正确 如果为 false 则表示 到页面结束了
	 * @param url
	 * @param inputBean
	 * @param intoDb
	 * @return
	 */
	public static boolean runUrl(String url,JobsInputQueueBean inputBean,Company51JobAntQueue queue,IntoDb intoDb)
	{
		boolean flag=false;
		if(inputBean.getCurrentPage()==0)
		{
			Pattern p_2 = Pattern.compile("type=\"hidden\"[^>]*?name=\"lonlat\"[^>]*?value=\"([^,]*?),([^,]*?)\">");
			Matcher m_2 = p_2.matcher(url);
			if(m_2.find())
			{
				LationLngLat location=new LationLngLat();
				location.setLng(Double.parseDouble(m_2.group(1)));
				location.setLat(Double.parseDouble(m_2.group(2)));
				inputBean.setLocation(location);
				flag=true;
			}else{
				flag=false;
			}
			return flag;
		}
		HashSet<Company51JobBean> company=new HashSet<Company51JobBean>();
		
		//获取职位对应的 公司 poi数据
		Pattern p_1 = Pattern.compile("M_([\\d]*?):[^}]*?lonlat:'([\\d\\.]*?),([\\d\\.]*?)'");
		Matcher m_1 = p_1.matcher(url);
		
		HashMap<String,LationLngLat> companyAndPoi=new HashMap<String,LationLngLat>();
		while(m_1.find())
		{
			flag=true;
			companyAndPoi.put(m_1.group(1),new LationLngLat(Double.parseDouble(m_1.group(2)),
					Double.parseDouble(m_1.group(3))));
		}
		
		//解析页面获取 公司信息
		 p_1 = Pattern.compile("<a href=\"([^\"]*?)\" class=\"coname\"[^>]*? name=\"coname_([^\"]*?)\">([^<]*?)</a");
		 m_1 = p_1.matcher(url);
		
		while(m_1.find())
		{
			flag=true;
			LationLngLat location=companyAndPoi.get(m_1.group(2));
			if(location==null)
			{
				Log.info("公司名不存在地图中"+m_1.group(3));
			}else{
				Company51JobBean com=new Company51JobBean();
				com.setLocation(location);
				com.setCompanyCode(Long.parseLong(m_1.group(2)));
				com.setCompanyUrl(m_1.group(1));
				com.setCompanyName(m_1.group(3));
				company.add(com);
			}
		}
		Iterator<Company51JobBean> iterator=company.iterator();
		while(iterator.hasNext())
		{
			flag=true;
			Company51JobBean bean=iterator.next();
			//判断是否存在
			if(queue.redis==null)
			{
			if(MainStatic.companyCode(bean.getCompanyCode()))
			{
				//如果包含则不管
			}else{
				MainStatic.addCompanyCode(bean.getCompanyCode());
				//如果有效则转换json
				String result=JsonUtil.getJsonStr(bean);
				//System.out.println("result:"+result);
				intoDb.add(result);
			}
			}else{
				//使用内存中的值
				try {
					String val=queue.redis.hget(MainStatic.companyCodeMap,Long.toString(bean.getCompanyCode()));
					if(val!=null)
					{//存在
						
					}else{

						if(bean.getLocation()==null)
						{
							log.info("location 未捕获到:"+bean.getCompanyCode()+"\tsubway:"+inputBean.getSubWayLine()+"\turl:"+inputBean.getUrl()+"\tpage:"+inputBean.getCurrentPage());
						}else{
							//不存在
							String result=JsonUtil.getJsonStr(bean);
							//本列中只存储url
							queue.redis.hset(MainStatic.companyCodeMap,Long.toString(bean.getCompanyCode()),bean.getCompanyUrl());
							log.info("新增公司:"+bean.getCompanyName()+"\t"+bean.getCompanyCode());
							intoDb.add(result);
						}
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		return flag;
	}
}
