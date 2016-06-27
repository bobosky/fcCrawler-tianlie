package com.zj.parse;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.util.JsonUtil;
import com.zj.bean.CompanyBean4;
import com.zj.intoDb.IntoDb;

public class CompanyZhaopinAntParse {

	/**
	 * 返回是否 正确 如果为 false 则表示 到页面结束了
	 * @param url
	 * @param inputBean
	 * @param intoDb
	 * @return
	 */
	public static boolean runUrl(String url,CompanyBean4 inputBean,IntoDb intoDb)
	{
		LinkedList<CompanyBean4> company=new LinkedList<CompanyBean4>();
		//临时存储公司名
		HashSet<String> companyName=new HashSet<String>();
		
		//解析页面获取 公司信息
		Pattern p_1 = Pattern.compile("<td class=\"gsmc\"><a href=\"([^\"]*?)\"[^>]*?>([^<]*?)<b>([^<]*?)</b>([^<]*?)</a");
		Matcher m_1 = p_1.matcher(url);
		while(m_1.find())
		{
			if(companyName.contains(m_1.group(3)+m_1.group(4)))
			{}else{
				companyName.add(m_1.group(3)+m_1.group(4));
				CompanyBean4 com=new CompanyBean4();
				com.setKeyword(inputBean.getKeyword());
				com.setCompanyUrl(m_1.group(1));
				com.setCompanyName(m_1.group(2)+m_1.group(3)+m_1.group(4));
				company.add(com);
			}
		}
		boolean flag=false;
		while(company.size()>0)
		{
			CompanyBean4 bean=company.poll();
			//如果有效则转换json
			String result=JsonUtil.getJsonStr(bean);
			//System.out.println("result:"+result);
			intoDb.add(result);
			flag=true;
		}
		return flag;
	}
}
