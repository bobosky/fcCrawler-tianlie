package com.zj.crawler;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.util.LinkedList;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;

import net.sf.json.JSONObject;

import com.util.FileUtil2;
import com.zj.exec.MainProxy;

public class test {
	public static String get(String url, String encoding,String proxyIp,int proxyPort) {
		try {
			long st = System.currentTimeMillis();
			URL _url = new URL(url);
			   HttpClient client = new HttpClient();  

		         //设置代理服务器地址和端口    

		         client.getHostConfiguration().setProxy(proxyIp,proxyPort);
		         client.setConnectionTimeout(1000);
		         HttpMethod method = new GetMethod(new String(url.getBytes(), "UTF-8"));
		         //method.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gb2312");
		         
		         // client.setTimeout(1000);
		         //使用POST方法

		         //HttpMethod method = new PostMethod("http://java.sun.com");
		         client.executeMethod(client.getHostConfiguration(), method);

		         //打印服务器返回的状态

		         //System.out.println(method.getStatusLine());
		         //System.out.println(method.getResponseHeader("Last-Modified"));

		         //打印返回的信息
		        
		        
		        
		         
		         String str= new String(method.getResponseBody(),"UTF-8");
		         //释放连接

		         method.releaseConnection();
		       return str;
//			HttpURLConnection conn = null;
//				SocketAddress addr = new InetSocketAddress(proxyIp,
//						proxyPort);
//				Proxy typeProxy = new Proxy(Proxy.Type.SOCKS, addr);
//				
//				System.setProperty("http.maxRedirects", "50");
//				System.setProperty("proxySet", "true");
//				System.setProperty("http.proxyHost", proxyIp);
//				System.setProperty("http.proxyPort", Integer.toString(proxyPort));
//				conn = (HttpURLConnection) _url.openConnection();
//				conn = (HttpURLConnection) _url.openConnection(typeProxy);
//				
//				
//			conn.setConnectTimeout(3000);
//			conn.connect();
//			InputStream in = conn.getInputStream();
//			BufferedReader bin = new BufferedReader(new InputStreamReader(in,
//					encoding));
//			String s = null;
//			StringBuffer all = new StringBuffer();
//
//			while ((s = bin.readLine()) != null) {
//				all.append(s);
//				all.append("\n");
//			}
//			bin.close();
//			st = System.currentTimeMillis() - st;
//			return all.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {
		FileUtil2 file=new FileUtil2("./proxyTrue.txt","utf-8",false);
		LinkedList<String> list=file.readAndClose();
		for(String str:list)
		{
			JSONObject obj=JSONObject.fromObject(str);
			System.out.println(obj.toString());
			try{
				System.out.println(test.get("http://www.baidu.com","utf-8",obj.getString("ip"),obj.getInt("port")).substring(0,100));
			}catch(Exception e)
			{
//				e.printStackTrace();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
