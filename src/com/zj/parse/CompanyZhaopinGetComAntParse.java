package com.zj.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.util.JsonUtil;
import com.zj.bean.CompanyBean4;
import com.zj.crawler.AntGetUrl;
import com.zj.intoDb.IntoDb;

public class CompanyZhaopinGetComAntParse {

	/**
	 * 返回是否 正确 如果为 false 则表示 到页面结束了
	 * @param url
	 * @param inputBean
	 * @param intoDb
	 * @return
	 */
	public static void runUrl(String url,CompanyBean4 inputBean,IntoDb intoDb)
	{
		
		//解析页面获取 公司信息
		Pattern p_1 = Pattern.compile("公司行业：</dt>[^<]*?<dd>([^<]*?)</dd");
		Matcher m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			String str=m_1.group(1).replaceAll("(&nbsp;)","").trim();//.replaceAll("  ",",").replaceAll("[ ]*","");
			inputBean.setIndustryCategory(str);
		}
		p_1 = Pattern.compile("公司性质：</dt>[^<]*?<dd>([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			inputBean.setCompanyCategory(m_1.group(1).replaceAll("(&nbsp;)","").trim());
		}
		p_1 = Pattern.compile("公司规模：</dt>[^<]*?<dd>([^<]*?)</dd");
		m_1 = p_1.matcher(url);
		if(m_1.find())
		{
			inputBean.setCompanyMemberNum(m_1.group(1).replaceAll("(&nbsp;)","").trim());
		}
		//获取coid
		
			//System.out.println("result:"+result);
		intoDb.add(JsonUtil.getJsonStr(inputBean));
	}
	public static void main(String[] args) throws Exception {
		String url=AntGetUrl.doGet("http://www.nandu.com/", "utf-8");
		System.out.println(url);
	}
}
