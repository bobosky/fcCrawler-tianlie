package com.zj.crawler;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.zip.GZIPInputStream;

import org.apache.log4j.Logger;

import com.zj.exec.MainProxy;
import com.zj.queue.BusAndStationQueue;

public class AntGetUrl {

	private static Logger log = Logger.getLogger(AntGetUrl.class);

	private static int limitTime = 5000;
	private static Lock lockAntUrl = new ReentrantLock();

	private static Lock lockProxy = new ReentrantLock();
	private static ConcurrentLinkedQueue<Integer> queueAntUrl = new ConcurrentLinkedQueue<Integer>();

	private static HashSet<String> lockAntUrlSet = new HashSet<String>();

	/**
	 * 得到指定URL的文本响应数据
	 * */
	public static String get(String url, String encoding, boolean isProxy) {

		try {
			long st = System.currentTimeMillis();
			URL _url = new URL(url);
			HttpURLConnection conn = null;
			if (isProxy) {
				IPProxy proxy = MainProxy.get();
				System.out.println(proxy.getIp());
				SocketAddress addr = new InetSocketAddress(proxy.getIp(),
						proxy.getPort());
				Proxy typeProxy = new Proxy(Proxy.Type.SOCKS, addr);
				conn = (HttpURLConnection) _url.openConnection(typeProxy);
			} else {
				conn = (HttpURLConnection) _url.openConnection();
			}
			conn.setConnectTimeout(limitTime);
			conn.connect();
			InputStream in = conn.getInputStream();
			BufferedReader bin = new BufferedReader(new InputStreamReader(in,
					encoding));
			String s = null;
			StringBuffer all = new StringBuffer();

			while ((s = bin.readLine()) != null) {
				all.append(s);
				all.append("\n");
			}
			bin.close();
			st = System.currentTimeMillis() - st;
			return all.toString();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

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
	public static void main(String[] args) {
		System.out.println(getUrl(10));
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

	/**
	 * 设置 代理
	 * 
	 * @param isProxy
	 * @return
	 */
	public static IPProxy setProxy(boolean isProxy) {
		if (isProxy) {
			// 如果有代理
			synchronized (lockAntUrl) {
				IPProxy proxy = MainProxy.get();
				lockProxy.lock();
				setProxy(proxy);
				return proxy;
			}
		} else {
			synchronized (lockProxy) {
				if (queueAntUrl.size() == 0) {
					lockAntUrlSet.add(Thread.currentThread().getName());
					lockAntUrl.lock();
				}
				queueAntUrl.add(1);
				// System.out.println("+size:"+queueAntUrl.size()+"\t"+Thread.currentThread().getName());
				setProxy(null);
				return null;
			}
		}
	}

	/**
	 * 解锁
	 * 
	 * @param flag
	 *            是否正常
	 * @param proxy
	 */
	public static void releaseProxy(IPProxy proxy, boolean flag) {
		if (proxy != null) {
			setProxy(null);
			log.info(Thread.currentThread().getName() + "\t释放代理ip:"
					+ proxy.getIp() + "\t" + proxy.getPort());
			lockProxy.unlock();
			if (flag) {
				MainProxy.addProxy(proxy);
			}
		} else {
			// 判断数量
			queueAntUrl.poll();
			// System.out.println("-size:"+queueAntUrl.size()+"\t"+Thread.currentThread().getName());
			while (true) {

				if (lockAntUrlSet.contains(Thread.currentThread().getName())
						&& queueAntUrl.size() == 0) {
					lockAntUrlSet.remove(Thread.currentThread().getName());
					lockAntUrl.unlock();
					// System.out.println("*****:"+queueAntUrl.size()+"\t"+Thread.currentThread().getName()+"\t解锁");
					break;
				}
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			// System.out.println("-----:"+queueAntUrl.size()+"\t"+Thread.currentThread().getName());
		}
	}

	/**
	 * 设置代理
	 * 
	 * @param proxy
	 */
	public static void setProxy(IPProxy proxy) {
		if (proxy == null) {
			// log.info("使用:"+proxy.getIp()+"\t"+proxy.getPort());
			System.setProperty("http.maxRedirects", "");
			System.setProperty("proxySet", "false");
			System.setProperty("http.proxyHost", "");
			System.setProperty("http.proxyPort", "");
			return;
		}
		System.setProperty("http.maxRedirects", "50");
		System.setProperty("proxySet", "true");
		System.setProperty("http.proxyHost", proxy.getIp());
		System.setProperty("http.proxyPort", Integer.toString(proxy.getPort()));
		log.info(Thread.currentThread().getName() + "\t使用代理ip:" + proxy.getIp()
				+ "\t" + proxy.getPort());
	}

	public static String doPost(String reqUrl, Map parameters,
			String recvEncoding) {
		HttpURLConnection conn = null;
		String responseContent = null;
		int lp = 0;
		while (true) {
			try {
				try {
					if (reqUrl.equals("")) {
						return "";
					} else if (reqUrl.startsWith("http")) {
					} else {
						reqUrl = "http://" + reqUrl;
					}
					if (reqUrl.contains("http://www2.")) {
						log.error("url异常:" + reqUrl);
						return "";
					}
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
					url_con.setConnectTimeout(limitTime);// （单位：毫秒）jdk
					// 1.5换成这个,连接超时
					url_con.setReadTimeout(limitTime);// （单位：毫秒）jdk
														// 1.5换成这个,读操作超时
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
				}catch(SocketTimeoutException e)
				{
					
				}
				catch(IllegalArgumentException e)
				{
					
				} 
				catch (IOException e) {
					e.printStackTrace();
				} finally {
					if (conn != null) {
						conn.disconnect();
					}
				}
				break;
			}catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					lp++;
					log.error(e);
					log.error("url异常:" + reqUrl + "\t次数" + lp);
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
	public static boolean doGetTest(String url, String unicode)
			throws Exception {
		// 在此检测url是否为空，是否是合适的url的格式
		StringBuffer result = new StringBuffer();
		BufferedReader in = null;
		int lp = 0;
		try {
			if (url.equals("")) {
				return true;
			} else if (url.startsWith("http")) {
			} else {
				url = "http://" + url;
			}
			if (url.contains("http://www2.")) {
				log.error("url异常:" + url);
				return false;
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
			httpUrlConnection.setConnectTimeout(limitTime);
			httpUrlConnection.setReadTimeout(limitTime);
			// 设置本次连接禁止重定向
			// httpUrlConnection.setInstanceFollowRedirects(false);

			httpUrlConnection.setRequestProperty("accept", "*/*");
			httpUrlConnection.setRequestProperty("connection", "Keep-Alwive");
			httpUrlConnection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			httpUrlConnection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded");
			// "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1; Trident/4.0; .NET CLR 2.0.50727)");
			// Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1);
			// Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.1;
			// Trident/4.0; .NET CLR 2.0.50727)
			// 建立实际的连接

			httpUrlConnection.connect();

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
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				lp++;
				log.error(e);
				log.error("url异常:" + url + "\t次数" + lp);
				if (lp >= 5) {
					throw e;
					// break;
				}
				Thread.sleep(2000);
			} catch (InterruptedException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			return false;
		}

		return true;
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
	public static boolean doGetGzipTest(String urlLT, String code) {
		// int lp = 0;
		StringBuilder sb = new StringBuilder();
		String urlL = "";
		if (urlLT.equals("")) {
			return false;
		} else if (urlLT.contains("http://")) {
			urlL = urlLT;
		} else {
			urlL = "http://" + urlLT;
		}
		try {
			// System.out.println(lp+"\t"+urlL);
			URL url = new URL(urlL);
			URLConnection connection2 = url.openConnection();
			// HttpURLConnection httpUrlConnection = (HttpURLConnection)
			// connection2;
			HttpURLConnection connection = (HttpURLConnection) connection2;
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(limitTime);
			connection.setReadTimeout(limitTime);

			connection.setRequestProperty("accept", "*/*");
			connection.setRequestProperty("connection", "Keep-Alwive");
			connection.setRequestProperty("user-agent",
					"Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
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
			System.out.println("长度:" + sb.length());
			return true;
		} catch (Exception e) {
			log.error("异常url:" + urlLT);
			return false;
		}
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
				if (url.equals("")) {
					return "";
				} else if (url.startsWith("http")) {
				} else {
					url = "http://" + url;
				}
				if (url.contains("http://www2.")) {
					log.error("url异常:" + url);
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
				httpUrlConnection.setConnectTimeout(limitTime);
				httpUrlConnection.setReadTimeout(limitTime);
				// 设置本次连接禁止重定向
				// httpUrlConnection.setInstanceFollowRedirects(false);

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
			}
			catch(IllegalArgumentException e)
			{
				return "";
			}
			catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				try {
					lp++;
					log.error(e);
					log.error("url异常:" + url + "\t次数" + lp);
					if (lp >= 5) {
						throw e;
						// break;
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
	 * 页面获取程序
	 * 
	 * @param url
	 *            页面地址
	 * @param unicode
	 *            页面编码
	 * @return 返回String
	 * @throws Exception
	 */
	public static String doGet(String url, String unicode, boolean isProxy)
			throws Exception {
		// 在此检测url是否为空，是否是合适的url的格式
		StringBuffer result = new StringBuffer();
		BufferedReader in = null;
		int lp = 0;
		IPProxy proxyTemp = null;
		while (true) {
			try {
				if (url.equals("")) {
					return "";
				} else if (url.startsWith("http")) {
				} else {
					url = "http://" + url;
				}
				if (url.contains("http://www2.")) {
					log.error("url异常:" + url);
					return "";
				}
				URL realUrl = new URL(url);
				// System.out.println("url:"+url);
				// 打开和URL之间的连接
				URLConnection connection = null;

				proxyTemp = setProxy(isProxy);
				// if(isProxy)
				// {
				// System.out.println("使用ip:"+proxyTemp.getIp()+"\t"+proxyTemp.getPort());
				// }
				// setProxy(false);
				connection = realUrl.openConnection();
				HttpURLConnection httpUrlConnection = (HttpURLConnection) connection;
				// 设置通用的请求属性
				httpUrlConnection.setDoInput(true);
				httpUrlConnection.setDoOutput(true);
				httpUrlConnection.setUseCaches(false);
				httpUrlConnection.setConnectTimeout(limitTime);
				httpUrlConnection.setReadTimeout(limitTime);
				// 设置本次连接禁止重定向
				// httpUrlConnection.setInstanceFollowRedirects(false);

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
				releaseProxy(proxyTemp, true);
				break;
			}
			catch(IllegalArgumentException e)
			{
				releaseProxy(proxyTemp, false);
				e.printStackTrace();
				return "";
			}
			catch (SocketTimeoutException e) {
				// TODO Auto-generated catch block
				releaseProxy(proxyTemp, false);
				e.printStackTrace();
				try {
					lp++;
					log.error(e);
					log.error("url异常:" + url + "\t次数" + lp);
					if (lp >= 5) {
						throw e;
						// break;
					}
					Thread.sleep(2000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}catch(Exception e)
			{
				releaseProxy(proxyTemp, false);
				return "";
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
	public static String doGetGzip(String urlLT, String code, boolean isProxy)
			throws Exception {
		// int lp = 0;
		StringBuilder sb = new StringBuilder();
		String urlL = "";
		IPProxy proxy = null;
		if (urlLT.equals("")) {
			return "";
		} else if (urlLT.contains("http://")) {
			urlL = urlLT;
		} else {
			urlL = "http://" + urlLT;
		}
		try {
			// System.out.println(lp+"\t"+urlL);
			URL url = new URL(urlL);
			proxy = setProxy(isProxy);
			URLConnection connection2 = url.openConnection();
			// HttpURLConnection httpUrlConnection = (HttpURLConnection)
			// connection2;
			HttpURLConnection connection = (HttpURLConnection) connection2;
			connection.setDoInput(true);
			connection.setDoOutput(true);
			connection.setUseCaches(false);
			connection.setConnectTimeout(limitTime);
			connection.setReadTimeout(limitTime);

			connection.setRequestProperty("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
			connection.setRequestProperty("connection", "Keep-Alwive");
			connection.setRequestProperty("Content-Type",
			"application/x-www-form-urlencoded");
			connection.setRequestProperty("user-agent",
					"Mozilla/5.0 (Windows NT 6.1; WOW64; rv:39.0) Gecko/20100101 Firefox/39.0");
			//connection.setRequestProperty("cookie","city=www; __utma=147393320.525473397.1434073773.1436322492.1438306404.9; __utmz=147393320.1434073773.1.1.utmcsr=(direct)|utmccn=(direct)|utmcmd=(none); global_cookie=p58ilyu6y0igpgzxypi2daidk38iasyhx8h; unique_cookie=U_6jsx7de60l9h1crx62l446tq11licqyhw4j*5; __utmb=147393320.15.10.1438306404; __utmc=147393320; __utmt_t0=1; __utmt_t1=1; __utmt_t2=1");
			connection.setRequestProperty("Accept-Encoding", "gzip, deflate");
			InputStream is = connection.getInputStream();// url.openStream();
			InputStream in = null;
			try {
				in = new GZIPInputStream(is);
			} catch (Exception e) {
				in = is;
			}
			InputStreamReader isr = new InputStreamReader(in, code);
			char[] buffer = new char[1024];
			int pos = 0;
			sb = new StringBuilder();
			releaseProxy(proxy, true);
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
		}catch(IllegalArgumentException e)
		{
			releaseProxy(proxy, false);
			e.printStackTrace();
		}
		catch (SocketTimeoutException e) {
			releaseProxy(proxy, false);
			log.error("异常url:" + urlLT);
			throw e;
		}
		catch (Exception e) {
			releaseProxy(proxy, false);
			log.error("异常url:" + urlLT);
		} finally {

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

	public static void main2(String[] args) {

		// String str= ParkingAntGetUrl.getUrl(1);
		// String str2= AntGetUrl.getUrl(20);
		// String str=AntGetUrl.getUrl2(2);
		// http://www.baidu87875.com/
		// String str = "";// company.zhaopin.com/CC410285728.htm
		// try {
		// str = AntGetUrl.doGet("company.zhaopin.com/CC410285728.htm", "gbk");
		// } catch (Exception e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }
		// System.out.println(str);
		String str = "http://guiguliangcheng.fang.com";
		str = "http://www.ip138.com/ip2city.asp";
		// str="http://pinggun.soufun.com/estimate/process/makerentchartdataOffice.aspx?dis=&newcode=1010084662&city=&district=&commerce=&isprojname=";
		str = "http://sydc.fang.com/house/1010704649.htm";
		// str="http://office.fang.com/zu/3_230755030.html";
		str = "http://sadf.qiyeshu.fang.com/";
		IPProxy proxy = new IPProxy();
		proxy.setIp("27.12.105.148");
		proxy.setPort(8088);
		// AntGetUrl.setProxy(proxy);
		try {
			String urls = AntGetUrl.doGet(str, "gbk", false);
			 System.out.println(urls);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// String urls=AntGetUrl.doGetGzip(str, "gbk");
		// System.out.println(urls);
	}
}
