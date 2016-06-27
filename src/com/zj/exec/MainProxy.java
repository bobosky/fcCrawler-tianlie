package com.zj.exec;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.sun.swing.internal.plaf.synth.resources.synth;
import com.util.FileUtil2;
import com.util.JsonUtil;
import com.zj.crawler.AntGetUrl;
import com.zj.crawler.IPProxy;

public class MainProxy {

	public static String str="http://nuodezhongxinbj.fang.com/";
	int page=0;
	
	public static LinkedList<IPProxy> proxyList=null;
	
	private static boolean isTrue=true;
	/**
	 * 获取代理列表
	 */
	public static void run(boolean isTrue)
	{
		MainProxy.isTrue=isTrue;
		if(MainProxy.isTrue)
		{
			//使用可以使用的 ip port 代理
			proxyList=getIpTrue(null);
		}else{
			//获取测试的ip port
			proxyList=getIp(null);
		}
	}
	static{
		//获取如果为false 则 使用全部的ip地址
		//为true则为使用有效的测试地址
		boolean getTrueProxy=true;
		run(getTrueProxy);
		//测试 并
		if(!getTrueProxy)
		{
			test();
			run(!getTrueProxy);
		}else{
		}
	}
	/**
	 * 获取的返回
	 * @param porxy
	 */
	public synchronized static void addProxy(IPProxy porxy)
	{
		proxyList.add(porxy);
	}
	/**
	 * 获取porxy
	 * 使用简单随机获取 应使用加权获取机制
	 * @return
	 */
	public synchronized static IPProxy get()
	{
		int count=0;
		while(true)
		{
			count++;
			if(proxyList.size()==0&&count<=5)
			{
				//等待时间
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				continue;
			}
			//如果代理池中没有了proxy 则等待
			if(proxyList.size()==0)
			{
				System.out.println("没有代理ip了");
				//return null;
				System.out.println("重新获取代理ip");
				if(MainProxy.isTrue)
				{
					//使用可以使用的 ip port 代理
					proxyList=getIpTrue(null);
				}else{
					//获取测试的ip port
					proxyList=getIp(null);
				}
			}
			break;
		}
		Random random=new Random();
		int i=Math.abs(random.nextInt()%proxyList.size());
		IPProxy porxy=proxyList.remove(i);
		return porxy;
	}
	/**
	 * 测试机制
	 */
	public static void test()
	{
		Iterator<IPProxy> lter=proxyList.iterator();
		String filePath2=System.getProperty("user.dir")+"/data/proxyTrue.txt";
		FileUtil2 file3=new FileUtil2(filePath2,"utf-8",false);
		LinkedList<String> oldList=file3.readAndClose();
		
		FileUtil2 file2=new FileUtil2(filePath2,"utf-8");
		file2.write(oldList);
		while(lter.hasNext())
		{
			IPProxy val=lter.next();
			//设置代理服务
			AntGetUrl.setProxy(val);
			//测试代理服务
			try{
			boolean flag=AntGetUrl.doGetGzipTest(str,"gbk");
			if(flag)
			{
				System.out.println("有效："+val.getIp());		
			}
			else{
				System.out.println("无效代理:"+val.getIp());
				lter.remove();
				continue;
			}
			}catch(Exception e)
			{
				e.printStackTrace();
				continue;
			}
			file2.write(JsonUtil.getJsonStr(val));
		}
		file2.close();
		
		
	
	}
	
	/**
	 * 获取proxy
	 * @param url
	 * @return
	 */
	public static LinkedList<IPProxy> getIp(String url)
	{
		LinkedList<IPProxy> list=new LinkedList<IPProxy>();
//		Pattern p_1 = Pattern.compile("");
//		Matcher m_1 = p_1.matcher(url);
		FileUtil2 file=new FileUtil2(System.getProperty("user.dir")+"/data/proxyTruebak.txt","gbk",false);
		LinkedList<String> list2=file.readAndClose();
		for(String ip:list2)
		{
			IPProxy proxy=(IPProxy)JsonUtil.getDtoFromJsonObjStr(ip,IPProxy.class);
			list.add(proxy);
		}
//		FileUtil2 file=new FileUtil2(System.getProperty("user.dir")+"/data/proxy2.txt","gbk",false);
//		LinkedList<String> list2=file.readAndClose();
//		for(String ip:list2)
//		{
//		
//			String[] strList=ip.split("@")[0].split(":");
//			//System.out.println(strList.length);
//			if(ip.split("@").length==1)
//			{
//				String[] ipL=ip.replaceAll("\"","").split(",");
//				for(String ipLL:ipL)
//				{
//					String[] ipLLL=ipLL.split(":");
//					IPProxy porxy=new IPProxy();
//					porxy.setIp(ipLLL[0]);
//					porxy.setPort(Integer.parseInt(ipLLL[1]));
//					list.add(porxy);
//				}
//				continue;
//			}
//			IPProxy porxy=new IPProxy();
//			porxy.setIp(strList[0]);
//			porxy.setPort(Integer.parseInt(strList[1]));
//			list.add(porxy);
//		}
		return list;
		
	}
	
	/**
	 * 获取proxy
	 * @param url
	 * @return
	 */
	public static LinkedList<IPProxy> getIpTrue(String url)
	{
		LinkedList<IPProxy> list=new LinkedList<IPProxy>();
//		Pattern p_1 = Pattern.compile("");
//		Matcher m_1 = p_1.matcher(url);
		FileUtil2 file=new FileUtil2(System.getProperty("user.dir")+"/data/proxyTrue.txt","gbk",false);
		LinkedList<String> list2=file.readAndClose();
		for(String ip:list2)
		{
			IPProxy porxy=(IPProxy)JsonUtil.getDtoFromJsonObjStr(ip, IPProxy.class);
			list.add(porxy);
		}
		return list;
	}
	
	public static void main(String[] args) {
		MainProxy proxy=new MainProxy();
		proxy.run(true);
	}
}
