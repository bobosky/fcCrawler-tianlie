package com.zj.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.util.JsonUtil;
import com.zj.bean.CompanyBean3;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;

public class Company51JobGetComAntParse {

	/**
	 * 返回是否 正确 如果为 false 则表示 到页面结束了
	 * @param url
	 * @param inputBean
	 * @param intoDb
	 * @return
	 */
	public static void runUrl(String url,CompanyBean3 inputBean,IntoDb intoDb)
	{
		//System.out.println("url:"+url);
//		LinkedList<CompanyBean> company=new LinkedList<CompanyBean>();
//		//临时存储公司名
//		HashSet<String> companyName=new HashSet<String>();
		
		//解析页面获取 公司信息
		Pattern p_1 = Pattern.compile("公司行业：</strong>([^<]*?)<");
		Matcher m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			String str=m_1.group(1).replaceAll("(&nbsp;&nbsp;)",",");
			if(str.startsWith(","))
			{
				str=str.substring(1);
			}
			inputBean.setIndustryCategory(str);
		}
		p_1 = Pattern.compile("公司性质：</strong>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			inputBean.setCompanyCategory(m_1.group(1).replaceAll("(&nbsp;&nbsp;)",","));
		}
		p_1 = Pattern.compile("公司规模：</strong>([^<]*?)<");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			inputBean.setCompanyMemberNum(m_1.group(1).replaceAll("(&nbsp;&nbsp;)",","));
		}
		//获取coid
		
		p_1 = Pattern.compile("search/codetail.php\\?coid=([\\d]*?)&");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			inputBean.setCompanyCode(m_1.group(1));
		}
		//String code2="http://fans.51job.com/payservice/fans/ajax/fans_exteral_ajax.php?jsoncallback=jsonp1417080952633&_=1417080954099&type=0&coid="+inputBean.getCompanyCode()+"&step=0";
		String code2="http://fans.51job.com/payservice/fans/ajax/fans_exteral_ajax.php?jsoncallback?&type=0&coid="+inputBean.getCompanyCode()+"&step=0";
		//System.out.println(code2);
		while(true)
		{
		try {
			url = AntGetUrl.doGet(code2,"gbk");
		//	System.out.println("url:"+url);
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
		
		p_1 = Pattern.compile("粉丝团\\(([\\d]*?)\\)");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			if(m_1.group(1).length()>0)
			inputBean.setFansCount(Integer.parseInt(m_1.group(1)));
		}
			//System.out.println("result:"+result);
		intoDb.add(JsonUtil.getJsonStr(inputBean));
	}
}
