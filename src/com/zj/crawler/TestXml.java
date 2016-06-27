package com.zj.crawler;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.NTCredentials;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthPolicy;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import sun.security.krb5.Credentials;

 

public class TestXml {
 public static String getAccContent(String url){
  String ret = "";
  try{ 
         HttpClient client = new HttpClient();  

         client.getHostConfiguration().setProxy("61.156.3.166",80);
         NTCredentials defaultcreds = new NTCredentials("liu.cf", "Founder1234", "61.156.3.166", "hold");
        
         client.getState().setProxyCredentials(AuthScope.ANY, defaultcreds);
         HostConfiguration hcf =new HostConfiguration();
         hcf.setProxy("61.156.3.166",80);
        
         HttpMethod method = new GetMethod(url);
         //使用POST方法
         //HttpMethod method = new PostMethod("http://java.sun.com");
         client.executeMethod(hcf, method);

         System.out.println(method.getStatusLine());

         //打印返回的信息
         ret =  method.getResponseBodyAsString();
         //释放连接
         method.releaseConnection();
        
  }catch(Exception e){
   System.out.println("has Exception!");
  }
  
  return ret;
 }
 
 public static void main(String[] args){
  //String url = "http://www.webjeep.com/GenAcc?url=http://172.16.5.86:8080/file_upload/rmp/cxdb/20060201/product/cxdb-20060201.exe";
  
  
  
  try{
   String url = "http://www.baidu.com";
   //url = toUtf8String(url);
   //System.out.println(url);
   /*
   URL target = new URL(url);
   HttpURLConnection httpTarget = (HttpURLConnection)target.openConnection();
   //httpTarget.
         //httpTarget.setDoOutput(true);
         //OutputStream out = httpTarget.getOutputStream();
   
   System.out.println(httpTarget.getResponseMessage());
         */
         HttpClient client = new HttpClient();  
         //设置代理服务器地址和端口    
         client.getHostConfiguration().setProxy("61.156.3.166",80);
         //client.getState().setProxyCredentials(new AuthScope("172.18.253.69",80), new UsernamePasswordCredentials("hold\\liu.cf", "Founder1234"));
         //NTCredentials defaultcreds = new NTCredentials("liu.cf", "Founder1234", "172.18.40.3", "hold");
         //client.getState().setProxyCredentials(AuthScope.ANY, defaultcreds);      
         //HostConfiguration hcf =new HostConfiguration();
         //hcf.setProxy("172.18.40.3",80);
         //使用GET方法，如果服务器需要通过HTTPS连接，那只需要将下面URL中的http换成https
        
         HttpMethod method = new GetMethod(new String(url.getBytes(), "UTF-8"));
         //method.addRequestHeader("Content-Type","application/x-www-form-urlencoded;charset=gb2312");     
         //使用POST方法
         //HttpMethod method = new PostMethod("http://java.sun.com");
         client.executeMethod(client.getHostConfiguration(), method);
         //打印服务器返回的状态
         //System.out.println(method.getStatusLine());
         //System.out.println(method.getResponseHeader("Last-Modified"));
         //打印返回的信息    
         System.out.println(new String(method.getResponseBody(),"UTF-8"));

         //释放连接

         method.releaseConnection();
  

  }catch(Exception e){
   e.printStackTrace();
  }
  
  
  
 }
 
  public static String toUtf8String(String s) {  
         StringBuffer sb = new StringBuffer();  
         for (int i = 0; i < s.length(); i++) {  
                 char c = s.charAt(i);  
                 if (c >= 0 && c <= 255) {  
                         sb.append(c);  
                 } else {  
                         byte[] b;  
                         try {  
                                 b = String.valueOf(c).getBytes("utf-8");  
                         } catch (Exception ex) {  
                                 System.out.println(ex);  
                                 b = new byte[0];  
                         }  
                         for (int j = 0; j < b.length; j++) {  
                                 int k = b[j];  
                                 if (k < 0)  
                                         k += 256;  
                                 sb.append("%" + Integer.toHexString(k).toUpperCase());  
                         }  
                 }  
         }  
         return sb.toString();  
 }

}