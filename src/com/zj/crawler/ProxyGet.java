package com.zj.crawler;

import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.util.FileUtil2;

/**
 * 代理的爬虫程序
 * @author Administrator
 *
 */
public class ProxyGet {

	LinkedList<IPProxy> list=new LinkedList<IPProxy>();
	String filePath=System.getProperty("user.dir")+"/data/proxy2.txt";
	int page=0;
	int pageNum=500;
	/**
	 * url获取
	 * @param url
	 */
	public void get(String url)
	{
		FileUtil2 file=new FileUtil2(filePath,"utf-8");
		url="http://www.nianshao.me/?page=";
		while(true)
		{
			page++;
			if(page>pageNum)
			{
				break;
			}
			String urlCode="";
			try {
				System.out.println(url+page);
				urlCode=AntGetUrl.doGet(url+page,"gbk");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(urlCode.equals(""))
			{
				continue;
			}
			//System.out.println(urlCode);
			//使用解析
			LinkedList<IPProxy> proxyList=getUrl(urlCode);
			list.addAll(proxyList);
			LinkedList<String> input=new LinkedList<String>();
			for(IPProxy proxy:proxyList)
			{
				input.add(proxy.getIp()+":"+proxy.getPort()+"@sdf");
			}
			file.write(input);
		}
		file.close();
	}
	/**
	 * http://ip.zdaye.com/
	 * 页面获取
	 */
	public LinkedList<IPProxy> getUrl(String urlCode)
	{
		LinkedList<IPProxy> listProxy=new LinkedList<IPProxy>();
		Pattern p_1 = Pattern.compile("<tr>[\\s]*?<td[^>]*?>([^<]*?)</td>[\\s]*?<td[^>]*?>([^<]*?)</td>[\\s]*?<td[^>]*?>[^<]*?</td>[\\s]*?<td[^>]*?>[^<]*?</td>[\\s]*?<td[^>]*?>([^<]*?)</td>");
		Matcher m_1=p_1.matcher(urlCode);
		while(m_1.find())
		{
		//	System.out.println(m_1.group(1)+"\t"+m_1.group(2)+"\t"+m_1.group(3));
			IPProxy proxy=new IPProxy();
			proxy.setIp(m_1.group(1));
			proxy.setPort(Integer.parseInt(m_1.group(2)));
			proxy.setCategory(m_1.group(3));
			if(proxy.getCategory().toUpperCase().equals("HTTP"))
			{
				listProxy.add(proxy);
			}
		}
		return listProxy;
	}
	
	public static void main(String[] args) {
		ProxyGet get=new ProxyGet();
		get.get(null);
	}
}
