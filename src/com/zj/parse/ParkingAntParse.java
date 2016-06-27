package com.zj.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;

import com.util.JsonUtil;
import com.zj.bean.ParkingBean;
import com.zj.intoDb.IntoDb;

/**
 * 正佳爬虫
 * 取http://www.bjjtw.gov.cn/jtw_service/page/service/parking.jsp
 * 对应的所有停车信息 并存储到文件中
 * json值
 * @author Administrator
 *
 */
public class ParkingAntParse {

	//<li class="table_list">[\s\S]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?
	private static Logger log = Logger.getLogger(ParkingAntParse.class);
	/**
	 * 从url中获取对应的信息
	 * @param url
	 */
	public static void runUrl(String url,IntoDb parkingIntoDb)
	{
		Pattern p_1 = Pattern.compile("<li class=\"table_list\">[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?<span[^>]*?>([^<]*?)</span[^<]*?");
		Matcher m_1 = p_1.matcher(url);

		while(m_1.find())
		{
			ParkingBean bean=new ParkingBean();
			bean.setAreaCode(m_1.group(1).trim());
			bean.setBakCode(m_1.group(2).trim());
			bean.setParkingName(m_1.group(3).trim());
			bean.setParkingCategory(m_1.group(4).trim());
			bean.setParkingManageComp(m_1.group(5).trim());
			if(m_1.group(6).trim().length()==0)
			{
				bean.setCarNum(0);
			}else{
				//System.out.println(m_1.group(6).trim());
			bean.setCarNum(Integer.parseInt(m_1.group(6).trim()));
			}
			if(m_1.group(7).trim().length()==0)
			{
				bean.setMechanicalCarNum(0);
			}else{
			bean.setMechanicalCarNum(Integer.parseInt(m_1.group(7).trim()));
			}
			if(m_1.group(8).trim().length()==0)
			{
				bean.setMachineryCarNum(0);
			}else{
				bean.setMachineryCarNum(Integer.parseInt(m_1.group(8).trim()));
			}
			bean.setAreaCatygory(m_1.group(9).trim());
			//转化为json
			String result=JsonUtil.getJsonStr(bean);
			//System.out.println("最终结果:"+result);
			//对应信息写入队列中
			//最终结果写入文件中
			parkingIntoDb.add(result);
		}
	}
}
