package com.zj.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import com.zj.queue.BusAndStationQueue;

public class AntGetUrl {

	private static Logger log = Logger.getLogger(AntGetUrl.class);

	/**
	 * 获取page信息
	 * 
	 * @param pageNum
	 *            page编号
	 * @return
	 */
	public static String getUrl(int pageNum) {
		Map m = new HashMap();
		String url = "http://www.bjjtw.gov.cn/jtw_service/page/service/parking.jsp";
		String code = "UTF-8";

		// area=东城区&ptype=&pname=&pagenum=1&pagesize=12
		m.put("area", "");
		m.put("ptype", "");
		m.put("pname", "停车场");
		m.put("pagenum", 1);
		m.put("pagesize", 12);
		m.put("page", pageNum);
		String rus = doPost(url, m, code);

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
		String rus = doPost(url, m, code);
		// System.out.println("result:"+rus);
		return rus;
	}

	public static String doPost(String reqUrl, Map parameters,
			String recvEncoding) {
		HttpURLConnection conn = null;
		String responseContent = null;
		int lp = 0;
		while (true) {
			try {
				try {
					StringBuffer params = new StringBuffer();
					for (Iterator iter = parameters.entrySet().iterator(); iter
							.hasNext();) {
						Entry element = (Entry) iter.next();
						params.append(element.getKey().toString());
						params.append("=");
						params.append(URLEncoder.encode(element.getValue()
								.toString(), recvEncoding));
						params.append("&");
					}

					if (params.length() > 0) {
						params = params.deleteCharAt(params.length() - 1);
					}
					// System.out.println("params:"+params);
					URL url = new URL(reqUrl);
					HttpURLConnection url_con = (HttpURLConnection) url
							.openConnection();
					url_con.setRequestMethod("POST");
					// System.setProperty("sun.net.client.defaultConnectTimeout",
					// String
					// .valueOf(HttpRequestProxy.connectTimeOut));//
					// （单位：毫秒）jdk1.4换成这个,连接超时
					// System.setProperty("sun.net.client.defaultReadTimeout",
					// String
					// .valueOf(HttpRequestProxy.readTimeOut)); //
					// （单位：毫秒）jdk1.4换成这个,读操作超时
					url_con.setConnectTimeout(30000);// （单位：毫秒）jdk
					// 1.5换成这个,连接超时
					url_con.setReadTimeout(30000);// （单位：毫秒）jdk 1.5换成这个,读操作超时
					url_con.setDoOutput(true);
					byte[] b = params.toString().getBytes();
					url_con.getOutputStream().write(b, 0, b.length);
					url_con.getOutputStream().flush();
					url_con.getOutputStream().close();
					// System.out.println(url_con.toString());
					java.io.InputStream in = url_con.getInputStream();
					BufferedReader rd = new BufferedReader(
							new InputStreamReader(in, recvEncoding));
					String tempLine = rd.readLine();
					StringBuffer tempStr = new StringBuffer();
					String crlf = System.getProperty("line.separator");
					while (tempLine != null) {
						tempStr.append(tempLine);
						tempStr.append(crlf);
						tempLine = rd.readLine();
					}
					responseContent = tempStr.toString();
					rd.close();
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					lp++;
					log.error(e);
					log.error("url异常:"+reqUrl+"\t次数"+lp);
					if (lp >= 5) {
						break;
					}
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		return responseContent;
	}

	/**
	 * 页面获取程序
	 * 
	 * @param url
	 *            页面地址
	 * @param unicode
	 *            页面编码
	 * @return 返回String
	 * @throws Exception 
	 */
	public static String doGet(String url, String unicode) throws Exception {
		// 在此检测url是否为空，是否是合适的url的格式
		StringBuffer result = new StringBuffer();
		BufferedReader in = null;
		int lp = 0;
		while (true) {
			try {
					if (url.startsWith("http")) {
					} else {
						url = "http://" + url;
					}
					if (url.contains("http://www2.")) {
						log.error("url异常:"+url);
						return "";
					}
					URL realUrl = new URL(url);
					// System.out.println("url:"+url);
					// 打开和URL之间的连接
					URLConnection connection = realUrl.openConnection();
					HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
					// 设置通用的请求属性
					httpUrlConnection.setDoInput(true);
					httpUrlConnection.setDoOutput(true);
					httpUrlConnection.setUseCaches(false);
					httpUrlConnection.setConnectTimeout(30000);
					httpUrlConnection.setReadTimeout(30000);
					// 设置本次连接禁止重定向
					//httpUrlConnection.setInstanceFollowRedirects(false);

					httpUrlConnection.setRequestProperty("accept", "*/*");
					httpUrlConnection.setRequestProperty("connection",
							"Keep-Alwive");
					httpUrlConnection
							.setRequestProperty("user-agent",
									"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
					httpUrlConnection.setRequestProperty("Content-Type",
							"application/x-www-form-urlencoded");
					// "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");
					// Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1);
					// Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1;
					// Trident/4.0; .NET CLR 2.0.50727)
					// 建立实际的连接

					httpUrlConnection.connect();
					// String redirectUrl =
					// connection.getHeaderField(HttpProtocolConstants.HEADER_LOCATION);
					// System.out.println("redirectUrl"+redirectUrl);
					// String redirectUrl =
					// httpUrlConnection.getHeaderField("Location");
					// System.out.println("redirectUrl"+redirectUrl);
					//
					// String code = new
					// Integer(httpUrlConnection.getResponseCode()).toString();
					//
					// String message = httpUrlConnection.getResponseMessage();
					//
					// System.out.println("getResponseCode code ="+ code);
					//
					// System.out.println("getResponseMessage message ="+
					// message);

					// 获取所有响应头字段
					// Map<String, List<String>> map =
					// connection.getHeaderFields();
					// String requestCookie="";
					// 遍历所有的响应头字段
					// if (null != map
					// && false == map.isEmpty())
					// {
					// for (Map.Entry<String, List<String>> entry :
					// map.entrySet())
					// {
					// String key = entry.getKey();
					// String value =
					// java.util.Arrays.toString(entry.getValue().toArray());
					// if (null != key
					// && "Set-Cookie".equals(key.trim()))
					// {
					// requestCookie = value;
					// requestCookie = requestCookie.replace("[", "");
					// requestCookie = requestCookie.replace("]", "");
					// }
					//
					// System.out.println(key + " : " + value);
					// }
					// }
					// 定义 BufferedReader输入流来读取URL的响应
					in = new BufferedReader(new InputStreamReader(
							connection.getInputStream(), unicode));
					String line;
					while ((line = in.readLine()) != null) {
						result.append(line);
					}
					
				// 使用finally块来关闭输入流
					try {
						if (in != null) {
							in.close();
						}
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				break;
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					lp++;
					log.error(e);
					log.error("url异常:"+url+"\t次数"+lp);
					if (lp >= 5) {
						throw e;
//						break;
					}
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		return result.toString();
	}

	/**
	 * 被gzip压缩过的网页获取方式
	 * 
	 * @param urlL
	 * @param code
	 * @return
	 * @throws Exception 
	 * @throws IOException
	 */
	public static String doGetGzip(String urlLT, String code) throws Exception {
		//int lp = 0;
		StringBuilder sb = new StringBuilder();
		String urlL="";
		if(urlLT.contains("http://"))
		{
			urlL=urlLT;
		}else{
			urlL="http://"+urlLT;
		}
		try {
			//System.out.println(lp+"\t"+urlL);
			URL url = new URL(urlL);
			URLConnection connection2 = url.openConnection();
			HttpURLConnection connection = (HttpURLConnection) connection2;
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);

			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alwive");
			connection
					.setRequestProperty("user-agent",
							"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			InputStream is = connection.getInputStream();// url.openStream();
			GZIPInputStream in = new GZIPInputStream(is);

			InputStreamReader isr = new InputStreamReader(in, code);
			char[] buffer = new char[1024];
			int pos = 0;
			sb = new StringBuilder();

			try {
				while ((pos = isr.read(buffer)) != -1) {
					sb.append(new String(buffer, 0, pos));
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			in.close();
			isr.close();
			is.close();
		} catch (Exception e) {
			log.error("异常url:"+urlLT);
			throw e;
		}finally{
			
		}
		return sb.toString();
	}

	/**
	 * Do POST request
	 * 
	 * @param url
	 * @param parameterMap
	 * @return
	 * @throws Exception
	 */
	public String doPostMap(String url, Map parameterMap, String code)
			throws Exception {

		/* Translate parameter map to parameter date string */
		StringBuffer parameterBuffer = new StringBuffer();
		if (parameterMap != null) {
			Iterator iterator = parameterMap.keySet().iterator();
			String key = null;
			String value = null;
			while (iterator.hasNext()) {
				key = (String) iterator.next();
				if (parameterMap.get(key) != null) {
					value = (String) parameterMap.get(key);
				} else {
					value = "";
				}

				parameterBuffer.append(key).append("=").append(value);
				if (iterator.hasNext()) {
					parameterBuffer.append("&");
				}
			}
		}

		System.out.println("POST parameter : " + parameterBuffer.toString());

		URL localURL = new URL(url);

		URLConnection connection = localURL.openConnection();
		HttpURLConnection httpURLConnection = (HttpURLConnection) connection;

		httpURLConnection.setDoOutput(true);
		httpURLConnection.setRequestMethod("POST");
		httpURLConnection.setRequestProperty("Accept-Charset", code);
		httpURLConnection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		httpURLConnection.setRequestProperty("Content-Length",
				String.valueOf(parameterBuffer.length()));

		OutputStream outputStream = null;
		OutputStreamWriter outputStreamWriter = null;
		InputStream inputStream = null;
		InputStreamReader inputStreamReader = null;
		BufferedReader reader = null;
		StringBuffer resultBuffer = new StringBuffer();
		String tempLine = null;

		try {
			outputStream = httpURLConnection.getOutputStream();
			outputStreamWriter = new OutputStreamWriter(outputStream);

			outputStreamWriter.write(parameterBuffer.toString());
			outputStreamWriter.flush();

			if (httpURLConnection.getResponseCode() >= 300) {
				throw new Exception(
						"HTTP Request is not success, Response code is "
								+ httpURLConnection.getResponseCode());
			}

			inputStream = httpURLConnection.getInputStream();
			inputStreamReader = new InputStreamReader(inputStream);
			reader = new BufferedReader(inputStreamReader);

			while ((tempLine = reader.readLine()) != null) {
				resultBuffer.append(tempLine);
			}

		} finally {

			if (outputStreamWriter != null) {
				outputStreamWriter.close();
			}

			if (outputStream != null) {
				outputStream.close();
			}

			if (reader != null) {
				reader.close();
			}

			if (inputStreamReader != null) {
				inputStreamReader.close();
			}

			if (inputStream != null) {
				inputStream.close();
			}

		}

		return resultBuffer.toString();
	}

	public static void main(String[] args) {

		// String str= ParkingAntGetUrl.getUrl(1);
		// String str2= AntGetUrl.getUrl(20);
		// String str=AntGetUrl.getUrl2(2);
		// http://www.baidu87875.com/
//		String str = "";// company.zhaopin.com/CC410285728.htm
//		try {
//			str = AntGetUrl.doGet("company.zhaopin.com/CC410285728.htm", "gbk");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		System.out.println(str);
		String str="http://guiguliangcheng.fang.com";
		//String urls=AntGetUrl.doGetGzip(str, "gbk");
//		String urls=AntGetUrl.doGetGzip(str, "gbk");
//		System.out.println(urls);
	}
}
