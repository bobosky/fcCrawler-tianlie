package com.zj.crawler;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.log4j.Logger;

import com.zj.exec.MainProxy;

public class AntGetUrlProxy {
	private static Logger log = Logger.getLogger(AntGetUrlProxy.class);
	private static int limitTime = 3000;

	/**
	 * 得到指定URL的文本响应数据
	 * */
	public static String doGet(String url, String encoding, boolean isProxy) {

		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams()
					.setConnectionTimeout(limitTime);
			client.getHttpConnectionManager().getParams()
					.setSoTimeout(limitTime);
			if (isProxy) {
				IPProxy proxy = MainProxy.get();
				// 设置代理服务器地址和端口
				client.getHostConfiguration().setProxy(proxy.getIp(),proxy.getPort());// .setProxy(proxy.getIp(),proxy.getPort());
				// 设置用户和密码，代理控件，代理域
				// NTCredentials defaultcreds = new NTCredentials("liu.cf",
				// "Founder1234", "172.18.40.3", "hold");
				// 设置
				// client.getState().setProxyCredentials(AuthScope.ANY,
				// defaultcreds);
				// 使用GET方法，如果服务器需要通过HTTPS连接，那只需要将下面URL中的http换成https
			}
			HttpMethod method = new GetMethod(new String(url.getBytes(),
					encoding));
			method.addRequestHeader("Content-Type",
					"application/x-www-form-urlencoded");
			method.addRequestHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
			method.removeRequestHeader("Proxy-Connection");
			method.addRequestHeader("Connection", "Keep-Alive");
			// 使用POST方法
			// HttpMethod method = new PostMethod("http://java.sun.com");
			client.executeMethod(client.getHostConfiguration(), method);
			InputStream is = method.getResponseBodyAsStream();
			Header header = method.getResponseHeader("Content-Encoding");
			InputStreamReader isr = null;
			GZIPInputStream gzin = null;
			boolean useGip = false;
			if (header == null) {
				isr = new InputStreamReader(is);
			} else {
				if (header.getValue().contains("gzip")) {
					useGip = true;
					gzin = new GZIPInputStream(is);
					isr = new InputStreamReader(gzin, encoding);
				}
			}
			java.io.BufferedReader br = new java.io.BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String tempbf;
			while ((tempbf = br.readLine()) != null) {
				sb.append(tempbf);
				sb.append("\r\n");
			}
			isr.close();
			if (useGip) {
				gzin.close();
			}
			// 释放连接
			method.releaseConnection();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void main(String[] args) {

//		String url = "http://kaidedasha.fang.com/";
//		url = "http://www.baidu.com";
//		System.out.println(AntGetUrlProxy.doGet(url, "gbk", true));
		System.out.println(getUrl(1));
	}

	/**
	 * 获取page信息
	 * 
	 * @param pageNum
	 *            page编号
	 * @return
	 */
	public static String getUrl(int pageNum) {
		Map<String,String> m = new HashMap();
		String url = "http://www.bjjtw.gov.cn/jtw_service/page/service/parking.jsp";
		String code = "UTF-8";
		// area=东城区&ptype=&pname=&pagenum=1&pagesize=12
		m.put("area", "西城");
		m.put("ptype", "公建配建停车场（位）");
		m.put("pname", "停车场");
		m.put("pagenum", "1");
		m.put("pagesize", "12");
		m.put("page", Integer.toString(pageNum));
		String rus = doPost(url, m, code, false);

		// System.out.println(rus);
		return rus;
	}

	public static String getUrl2(int pageNum) {
		Map m = new HashMap();
		String url = "http://www.bjjtw.gov.cn/jtw_service/page/service/parking.jsp";
		String code = "UTF-8";
		m.put("area", "");
		m.put("ptype", "");
		m.put("pname", "停车场");
		m.put("pagenum", 1);
		m.put("pagesize", 12);
		m.put("page", pageNum);
		String rus = doPost(url, m, code, false);
		// System.out.println("result:"+rus);
		return rus;
	}

	public static String doPost(String reqUrl, Map<String, String> parameters,
			String recvEncoding, boolean isProxy) {
		try {
			HttpClient client = new HttpClient();
			client.getHttpConnectionManager().getParams()
					.setConnectionTimeout(limitTime);
			client.getHttpConnectionManager().getParams()
					.setSoTimeout(limitTime);
			if (isProxy) {
				IPProxy proxy = MainProxy.get();
				// 设置代理服务器地址和端口
				client.getHostConfiguration().setProxy(proxy.getIp(),proxy.getPort());// .setProxy(proxy.getIp(),proxy.getPort());
				// 设置用户和密码，代理控件，代理域
				// NTCredentials defaultcreds = new NTCredentials("liu.cf",
				// "Founder1234", "172.18.40.3", "hold");
				// 设置
				// client.getState().setProxyCredentials(AuthScope.ANY,
				// defaultcreds);
				// 使用GET方法，如果服务器需要通过HTTPS连接，那只需要将下面URL中的http换成https
			}
			PostMethod postMethod = new PostMethod(new String(
					reqUrl.getBytes(), recvEncoding));
			NameValuePair[] param = new NameValuePair[parameters.size()];
			int i = -1;
			for (Entry<String, String> pa : parameters.entrySet()) {
				i++;
				param[i] = new NameValuePair(URLEncoder.encode(pa.getKey(),recvEncoding),URLEncoder.encode(pa.getValue(),recvEncoding));
			}
			postMethod.setRequestBody(param);
			postMethod.addRequestHeader("Content-Type",
					"application/x-www-form-urlencoded");
			postMethod.addRequestHeader("User-Agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:38.0) Gecko/20100101 Firefox/38.0");
			postMethod.removeRequestHeader("Proxy-Connection");
			postMethod.addRequestHeader("Connection", "Keep-Alive");
			System.out.println(Arrays.toString(postMethod.getParameters()));
			// 使用POST方法
			// HttpMethod method = new PostMethod("http://java.sun.com");
			client.executeMethod(client.getHostConfiguration(), postMethod);
			InputStream is = postMethod.getResponseBodyAsStream();
			Header header = postMethod.getResponseHeader("Content-Encoding");
			InputStreamReader isr = null;
			GZIPInputStream gzin = null;
			boolean useGip = false;
			if (header == null) {
				isr = new InputStreamReader(is);
			} else {
				if (header.getValue().contains("gzip")) {
					useGip = true;
					gzin = new GZIPInputStream(is);
					isr = new InputStreamReader(gzin, recvEncoding);
				}
			}
			java.io.BufferedReader br = new java.io.BufferedReader(isr);
			StringBuffer sb = new StringBuffer();
			String tempbf;
			while ((tempbf = br.readLine()) != null) {
				sb.append(tempbf);
				sb.append("\r\n");
			}
			isr.close();
			if (useGip) {
				gzin.close();
			}
			// 释放连接
			postMethod.releaseConnection();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
