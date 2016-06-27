package com.zj.exec;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * 模拟登陆程序
 * @author Administrator
 *
 */
public class Login {

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
						System.out.println("url异常:"+url);
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
//					 String redirectUrl =
//					 httpUrlConnection.getHeaderField("Location");
//					 System.out.println("redirectUrl"+redirectUrl);
//					
//					 String code = new
//					 Integer(httpUrlConnection.getResponseCode()).toString();
//					
//					 String message = httpUrlConnection.getResponseMessage();
//					
//					 System.out.println("getResponseCode code ="+ code);
//					
//					 System.out.println("getResponseMessage message ="+
//					 message);

					// 获取所有响应头字段
					 Map<String, List<String>> map =
					 connection.getHeaderFields();
					 String requestCookie="";
					// 遍历所有的响应头字段
					 if (null != map
					 && false == map.isEmpty())
					 {
					 for (Map.Entry<String, List<String>> entry :
					 map.entrySet())
					 {
					 String key = entry.getKey();
					 String value =
					 java.util.Arrays.toString(entry.getValue().toArray());
					 if (null != key
					 && "Set-Cookie".equals(key.trim()))
					 {
					 requestCookie = value;
					 requestCookie = requestCookie.replace("[", "");
					 requestCookie = requestCookie.replace("]", "");
					 }
					
					 System.out.println(key + " : " + value);
					 }
					 }
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
					System.out.println(e);
					System.out.println("url异常:"+url+"\t次数"+lp);
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
	
	
    public static void loginBaidu() {  
        
        URL url = null;  
      
        HttpURLConnection httpurlconnection = null;  
      
        try {  
      
//            url = new URL("http://www.baidu.com/");           
//      
//            httpurlconnection = (HttpURLConnection) url.openConnection();  
//      
//            httpurlconnection.setRequestProperty("User-Agent", "Internet Explorer");  
//      
//            httpurlconnection.setRequestProperty("Host", "www.baidu.com");  
//      
//            httpurlconnection.connect();  
//      
//            String cookie0 = httpurlconnection.getHeaderField("Set-Cookie");  
//      
//            httpurlconnection.disconnect();  
//      
//            url = new URL("http://passport.baidu.com/?login");  
//      
//            String strPost = "username=3210123210123&password=woainihack1&mem_pass=on";  
//      
//            httpurlconnection = (HttpURLConnection) url.openConnection();  
//      
//            HttpURLConnection.setFollowRedirects(true);  
//      
//            httpurlconnection.setInstanceFollowRedirects(true);  
//      
//            httpurlconnection.setDoOutput(true); // 需要向服务器写数据  
//      
//            httpurlconnection.setDoInput(true); //  
//      
//            httpurlconnection.setUseCaches(false); // 获得服务器最新的信息  
//      
//            httpurlconnection.setAllowUserInteraction(false);  
//      
//            httpurlconnection.setRequestMethod("POST");  
//      
//            httpurlconnection  
//      
//                    .addRequestProperty(  
//      
//                            "Accept",  
//      
//                            "image/gif, image/x-xbitmap, image/jpeg, image/pjpeg, application/x-shockwave-flash, application/msword, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/x-silverlight, */*");  
//      
//            httpurlconnection  
//      
//                    .setRequestProperty("Referer",  
//      
//                            "http://passport.baidu.com/?login&tpl=mn&u=http%3A//www.baidu.com/");  
//      
//            httpurlconnection.setRequestProperty("Accept-Language", "zh-cn");  
//      
//            httpurlconnection.setRequestProperty("Content-Type",  
//      
//                    "application/x-www-form-urlencoded");  
//      
//            httpurlconnection.setRequestProperty("Accept-Encoding",  
//      
//                    "gzip, deflate");  
//      
//            httpurlconnection  
//      
//                    .setRequestProperty(  
//      
//                            "User-Agent",  
//      
//                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; Foxy/1; .NET CLR 2.0.50727; MEGAUPLOAD 1.0)");  
//      
//            httpurlconnection.setRequestProperty("Host", "passport.baidu.com");  
//      
//            httpurlconnection.setRequestProperty("Content-Length", strPost  
//      
//                    .length()  
//      
//                    + "");  
//      
//            httpurlconnection.setRequestProperty("Connection", "Keep-Alive");  
//      
//            httpurlconnection.setRequestProperty("Cache-Control", "no-cache");  
//      
//            httpurlconnection.setRequestProperty("Cookie", cookie0);  
//      
//      
//      
//            httpurlconnection.getOutputStream().write(strPost.getBytes());  
//      
//            httpurlconnection.getOutputStream().flush();  
//      
//            httpurlconnection.getOutputStream().close();  
//      
//            httpurlconnection.connect();  
//      
//            int code = httpurlconnection.getResponseCode();  
//      
//            System.out.println("code   " + code);  
//      
//      
//      
//            String cookie1 = httpurlconnection.getHeaderField("Set-Cookie");  
//      
//            System.out.print(cookie0 + "; " + cookie1);  
//      System.out.println();
//            httpurlconnection.disconnect();  
      
              
      
           // url = new URL("http://www.baidu.com/");
        	String ss="";
        	ss="http://index.baidu.com/?tpl=trend&word=%D0%C2%C0%CB";
        	ss="http://index.baidu.com/Interface/IndexShow/show/?res=XxwmGHBGSDlCOz4bAT0sMjhtKj98fgt0a1EmEmcYSEExVk5VAglhSmRHCUNePgMWCFwPHBMlWT4MNwNFfScTL0EZFnc0DAIcQDYcCRBzMTBgF2ptcA89JgAlOSgFAl0bIy9AQxcWKjQBPDE%2BISUbezQWXwpxAQECNkwtcgswCwZzfENiIgoKKhV0KEYtJRIxY3ILJmwLXjJDB1UkBDNEXiAkNgFuOUJLQU9%2FfWZxBCEQQygTUVU2UFRDAAtlKCMGNQUeKA5xPSdWKR0wLHkhFw%3D%3D&res2=tAEXSTR51EYI5j7rNNnPi6F98ohS1n2I05d1BcQsqDlHulUdIqmZlHwRTSXETB275.8tA&classType=2&res3[]=p76B8&res3[]=Zc2B9&className=profWagv";
        	ss="http://index.baidu.com/Interface/Wordgraph/getWordgraph/?res=R35%2BNntyCw4yEBc1P3YnB1xoFVxRV0UcPlANMmV%2BNiE4BhodcGJeJQADIzpLIyxCdXNxX0MAAhYxdlcxIh0LGDZDViRUM3ZDXlktLxhXeQcDRyInWF1aSlMvEkwzKhJsRRFWfy4gFEAqfnwxUycSQgVyLktkBAF3MWFdQWgYXGEQCCFEB14XBzoFKAMhCncHUQBTUg00UBI1ElthKA43YCtjQjYoPCkPeSdEcRttHi8UPy0MCgx6OnJnTwUqNAU7HlVQZlIaLwM6BhIKSgYXBBQ%3D&res2=71EXSTR2.343v6EXSTRofJRfHmPOo7KLBxowsHBf8MmcLV3ataF2pff3sveBdY0omLXv6201.340";
            url = new URL(ss);     
      
            httpurlconnection = (HttpURLConnection) url.openConnection();  
      
            httpurlconnection.setRequestProperty("User-Agent", "Internet Explorer");  
      
            httpurlconnection.setRequestProperty("Host", "www.baidu.com");  
      
           // httpurlconnection.setRequestProperty("Cookie", cookie0 + "; " + cookie1);  
      String temp="BD_UPN=11253143; H_PS_645EC=3aa3frMRDustEevEVEQld34izlWcQzRd5EmhnTHoreFpYfOuypQDY2BWRYM; BD_UPN=13314352; BAIDUPSID=0CD4127149397ACD6FD85BE0B984921B; MCITY=-%3A; BAIDUID=29E0FB9C692A1277093EABD0C661DB95:FG=1; __zpspc=188.5.1419063399.1419063399.1%234%7C%7C%7C%7C%7C%23; Hm_lvt_f5127c6793d40d199f68042b8a63e725=1419646267,1419646941,1419647151; BD_HOME=1; BD_CK_SAM=1; H_PS_PSSID=8342_10643_10161_1425_10421_10488_10501_10497_10752_10646_10458_10796_10219_9374_10356_10667_10095_10657_10699_10460_10415_9950_10620_10701_10627; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; H_PS_645EC=cde990IpQ1w2a2pOBvZeQCvSZveZEJixlW0Jsn7sKQtvnxeNmM3cwNE39l5eIzhGm91E; BDUSS=5mdy1VZ1dRNjdKVGlnTUFlWXNmMzAzRkh1VVFDanZWZHpSYmM5M2FWZmc2OFZVQVFBQUFBJCQAAAAAAAAAAAEAAADjM0klMzIxMDEyMzIxMDEyMwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOBenlTgXp5UaH";
      temp="BAIDUPSID=0CD4127149397ACD6FD85BE0B984921B; MCITY=-%3A; Hm_lvt_d101ea4d2a5c67dab98251f0b5de24dc=1419588151,1419644732,1419647239,1419662998; bdshare_firstime=1417168726524; BAIDUID=29E0FB9C692A1277093EABD0C661DB95:FG=1; __zpspc=188.5.1419063399.1419063399.1%234%7C%7C%7C%7C%7C%23; CHKFORREG=26b786c38fe3ef46d039790baf493b02; Hm_lvt_f5127c6793d40d199f68042b8a63e725=1419647239; H_PS_PSSID=8342_10643_10161_1425_10421_10488_10501_10497_10752_10646_10458_10796_10219_9374_10356_10667_10095_10657_10699_10460_10415_9950_10620_10701_10627; Hm_lpvt_d101ea4d2a5c67dab98251f0b5de24dc=1419666071; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; BDUSS=5mdy1VZ1dRNjdKVGlnTUFlWXNmMzAzRkh1VVFDanZWZHpSYmM5M2FWZmc2OFZVQVFBQUFBJCQAAAAAAAAAAAEAAADjM0klMzIxMDEyMzIxMDEyMwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOBenlTgXp5UaH";
      httpurlconnection.setRequestProperty("Cookie",temp);
            httpurlconnection.connect();  
      
            InputStream urlStream = httpurlconnection.getInputStream();  
      
            BufferedInputStream buff = new BufferedInputStream(urlStream);  
      
                    Reader r = new InputStreamReader(buff, "gbk");  
      
                    BufferedReader br = new BufferedReader(r);  
      
                    StringBuffer strHtml = new StringBuffer("");  
      
                    String  strLine = null;  
      
                    while ((strLine = br.readLine()) != null) {  
      
                        strHtml.append(strLine + "/r/n");  
      
                    }  
      
                    System.out.print(strHtml.toString());  
      
        } catch (Exception e) {  
      
            e.printStackTrace();  
      
        } finally {  
      
            if (httpurlconnection != null)  
      
                httpurlconnection.disconnect();  
      
        }  
      
    }  
    
//    BD_UPN=11253143; H_PS_645EC=3aa3frMRDustEevEVEQld34izlWcQzRd5EmhnTHoreFpYfOuypQDY2BWRYM;
//    BD_UPN=13314352; BAIDUPSID=0CD4127149397ACD6FD85BE0B984921B; MCITY=-%3A;
//    BAIDUID=29E0FB9C692A1277093EABD0C661DB95:FG=1; __zpspc=
//    		188.5.1419063399.1419063399.1%234%7C%7C%7C%7C%7C%23;
//    Hm_lvt_f5127c6793d40d199f68042b8a63e725=1419646267,1419646941,1419647151;
//    BD_HOME=1; 
//    BD_CK_SAM=1; H_PS_PSSID=8342_10643_10161_1425_10421_10488_10501_10497_10752_10646_10458_10796_1021
//    		9_9374_10356_10667_10095_10657_10699_10460_10415_9950_10620_10701_10627; 
//    BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; H_PS_645EC=cde990IpQ1w2a2pOBvZeQCvSZveZEJixlW0Jsn7sKQt
//    		vnxeNmM3cwNE39l5eIzhGm91E; BDUSS=5mdy1VZ1dRNjdKVGlnTUFlWXNmMzAzRkh1VVFDanZWZHpSYmM5M
//    		2FWZmc2OFZVQVFBQUFBJCQAAAAAAAAAAAEAAADjM0klMzIxMDEyMzIxMDEyMwAAAAAAAAAAAAAAAAAAAAAAAAA
//    		AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOBenlTgXp5UaH
    	public static String strn2="";	
    //		BD_UPN=11253143; H_PS_645EC=3aa3frMRDustEevEVEQld34izlWcQzRd5EmhnTHoreFpYfOuypQDY2BWRYM; BD_UPN=13314352; BAIDUPSID=0CD4127149397ACD6FD85BE0B984921B; MCITY=-%3A; BAIDUID=29E0FB9C692A1277093EABD0C661DB95:FG=1; __zpspc=188.5.1419063399.1419063399.1%234%7C%7C%7C%7C%7C%23; Hm_lvt_f5127c6793d40d199f68042b8a63e725=1419646267,1419646941,1419647151; BD_HOME=1; BD_CK_SAM=1; H_PS_PSSID=8342_10643_10161_1425_10421_10488_10501_10497_10752_10646_10458_10796_10219_9374_10356_10667_10095_10657_10699_10460_10415_9950_10620_10701_10627; BDRCVFR[feWj1Vr5u3D]=I67x6TjHwwYf0; H_PS_645EC=cde990IpQ1w2a2pOBvZeQCvSZveZEJixlW0Jsn7sKQtvnxeNmM3cwNE39l5eIzhGm91E; BDUSS=5mdy1VZ1dRNjdKVGlnTUFlWXNmMzAzRkh1VVFDanZWZHpSYmM5M2FWZmc2OFZVQVFBQUFBJCQAAAAAAAAAAAEAAADjM0klMzIxMDEyMzIxMDEyMwAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAOBenlTgXp5UaH
  public static String  strn= "45,10,6,85,102,63,82,149,77,103,194,187,187,208,67,34,142,135,228,156,50,28,21,191,145,73,87,151,113,72,54,70,29,36,16,8,8,4,3,15,88,77,44,25,65,168,143,76,116,59,66,152,101,135,142,73,36,29,87,101,588,623,183,37,36,148,124,257,207,129,72,24,105,106,83,86,97,39,34,55,80,96,162,126,69,40,118,131,195,227,101,113,29,23,34,223,418,297,159,83,277,158,155,185,170,43,25,117,132,195,459,241,102,79,124,83,222,260,181,17,35,18,90,266,158,85,17,73,265,183,145,241,433,102,126,235,212,212,109,101,14,37,147,174,191,290,199,108,89,96,105,113,152,156,75,60,27,105,184,287,311,67,148,488,628,267,232,114,57,59,90,114,266,241,120,53,107,187,378,962,261,235,16,37,91,155,91,139,193,20,30,184,75,133,191,166,63,29,83,144,191,214,200,42,15,103,116,142,73,127,49,26,168,124,118,139,114,28,15,99,128,120,83,79,33,20,99,127,121,357,466,57,40,269,286,239,141,109,56,48,310,266,556,212,298,126,99,164,160,322,232,135,72,58,40,185,387,112,88,56,24,167,187,486,508,185,115,392,160,143,86,187,179,49,20,37,21,8,20,18,175,93,103,81,95,78,142,39,33,196,231,166,117,118,47,31,126,152,111,109,103,37,19,135,122,168,154,94,51,48,120,115,422,284,167,61,18,133,112,110,119,149,16,25,120,105,77,117,91,12,53,204,163,188,237,192,56,17,143,183,108,66,57,30,31,134,99,257,158,214,146,84,223,199,145,138,170,60,24,288,214,186,114,75,79,29,27,21,193,242,279,57,29,86,148,185,142,173,49,34,98,101,100,112,0,0,3,24,5,12,11,3,24,54,171,157,107,100,102,34,11,106,138,99,130,100,20,15,97,104,66,65,68,29,16,97,87,53,87,97,46,42,115,325,455,125,208,78,31,106,88,180,71,142,34,15,0,124,179,176,97,46,30,44,81,103,129,123,63,27,132,68,144,138,136,286,131,62,38,32,125,76,31,49,93,249,203,109,144,43,26,140,134,127,111,78,24,30,183,128,97,82,85,120,36,27,5,75,87,64,27,20,42,53,239,154,115,18,23,105,201,425,468,115,26,20,103,114,139,87,74,32,28,204,120,132,121,106,25,28,87,174,389,171,145,15,12,111,136,199,139,151,53,13,123,440,301,233,30,16,15,93,66,89,92,151,22,16,99,85,65,53,58,16,14,73,61,107,184,188,50,34,204,112,76,172,129,26,46,135,118,106,88,83,62,58,164,139,75,96,71,24,30,104,169,97,142,166,20,29,149,118,129,416,381,86,44,134,132,77,82,71,16,5,85,72,137,26,137,112,18,139,84,64,89,81,12,6,62,87,69,62,87,12,21,59,112,83,88,73,23,18,82,56,72,153,52,45,17,16,11,9,9,15,11,11,41,48,46,39,78,8,12,58,70,93,85,53,18,6,46,64,27,68,63,28,16,30,87,64,67,46,8,12,57,105,74,0,69,32,26,84,53,34,71,214,84,73,492,473,310,311,180,35,162,348,90,85,95,61,12,8,79,0,342,239,328,129,82,227,211,322,161,348,189,104,282,585,538,759,389,135,77,271,208,197,402,748,339,160,186,105,70,118,339,153,147,299,147,145,207,375,89,101,239,352,201,253,262,141,37,277,488,184,256,279,91,106,203,115,362,168,785,781,613,826,2906,806,784,673,635,486,532,571,701,576,517,791,674,911,816,4066,1424,702,721,619,687,858,768,79567,875,702,699,735,645,793,1020,819,662,526,838,831,818,823,860,649,455,2309,863,708,794,2393,587,508,747,779,2530,2734,754,746,430,235,361,177,65,96,41,174,277,239,232,337,391,118,84,181,267,159,269,177,169,147,356,223,238,419,294,277,205,579,1202,316,1018,391,207,207,247,220,261,141,125,67,103,359,170,157,135,353,110,48,122,211,177,269,109,68,37,98,173,170,208,147,60,69,129,143,220,219,355,295,102,54,40,137,184,104,31,62,116,251,111,97,141,67,62,226,80,77,111,92,42,124,120,85,185,322,279,286,56,270,184,297,586,333,135,56,219,173,114,232,306,126,137,149,222,231,327,376,98,92,229,95,280,362,584,137,50,233,219,254,158,168,97,58,122,460,440,201,186,45,94,333,173,173,131,256,126,83,451,156,155,186,225,174,55,136,142,210,365,413,117,51,165,223,188,136,153,35,57,187,168,276,58,19,16,151,262,193,87,193,93,43,98,99,56,65,10,34,15,13,69,94,141,0,145,90,19,151,279,159,207,299,74,104,171,90,128,147,139,86,81,87,115,165,141,0,0,44,114,169,118,151,208,65,0,107,192,426,0,128,65,20,109,90,106,140,97,61,55,147,196,135,131,119,84,66,184,124,170,132,104,56,56,140,122,77,85,129,46,15,162,175,196,97,121,51,20,94,61,112,123,112,62,35,78,105,42,74,112,20,19,126,163,329,216,161,8,18,102,0,166,188,97,60,17,98,140,101,209,171,47,117,117,123,54,60,16,20,12,13,6,2,14,73,79,8,61,50,80,59,104,116,0,139,208,0,119,95,35,20,194,0,678,379,259,96,40,130,111,83,133,91,50,46,73,137,0,161,60,346,175,387,193,129,116,169,142,39,0,118,101,124,241,106,80,144,259,0,103,115,0,20,77,304,179,229,255,0,36,192,172,148,400,1126,115,0,282,165,198,476,1370,186,165,423,201,165,18,13,16,87,198,111,70,100,96,19,20,120,111,84,113,83,44,48,70,82,85,266,156,46,15,105,0,85,66,64,11,14,9,78,104,105,159,24,17,127,109,134,116,155,85,37,144,94,94,123,78,51,19,80,98,124,0,153,75,11,116,119,98,124,92,38,37,79,127,118,168,130,41,48,119,91,126,149,114,42,19,70,108,125,209,141,58,40,69,100,108,123,126,94,99,113,101,103,127,0,0,64,145,167,93,0,0,85,110,187,110,157,128,0,0,133,199,0,237,187,131,56,54,83,0,104,77,96,55,61,76,122,184,90,133,73,60,133,106,152,73,207,8,18,167,208,103,144,107,93,158,248,212,68,50,71,52,56,42,49,0,118,104,275,48,0,152,204,153,95,83,55,113,0,639,698,263,139,104,155,251,927,146,184,87,20,125,366,278,275,162,68,0,118,173,117,84,1796,972,837,1602,920,1108,123,648,361,20,1513,994,573,1676,540,225,2180,882,976,1216,1571,1653,808,811,127,825,550,728,321,1054,17,357,438,534,1209,1083,305,668,2230,459,744,469,980";
	public static void main(String[] args) throws Exception {
//		String str=Login.doGet("http://index.baidu.com/?tpl=trend&word=asdf","gbk");
//		System.out.println(str);
	//	Login.loginBaidu();
		//System.out.println("\u65b0\u6d6a\u8d22\u7ecf");
		
		String str=Login.strn;
		String[] str2=str.split(",");
		for(int i=0;i<str2.length;i++)
		{
			System.out.println(str2[i]);
		}
	}
}
