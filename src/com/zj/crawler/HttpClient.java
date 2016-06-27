package com.zj.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {
	CookieManager ca = new CookieManager();
	String sessionID = "";

	public String get(String url, String charset) {
		try {
			String key = "";
			String cookieVal = "";

			URL httpURL = new URL(url);
			HttpURLConnection http = (HttpURLConnection) httpURL
					.openConnection();
			if (!sessionID.equals("")) {
				http.setRequestProperty("Cookie", sessionID);
			}
			System.out.println("session:"+sessionID);
			for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++) {
				if (key.equalsIgnoreCase("set-cookie")) {
					cookieVal = http.getHeaderField(i);
					cookieVal = cookieVal.substring(
							0,
							cookieVal.indexOf(";") > -1 ? cookieVal
									.indexOf(";") : cookieVal.length() - 1);
					sessionID = sessionID + cookieVal + ";";
				}
			}
			BufferedReader br = new BufferedReader(new InputStreamReader(
					http.getInputStream(), charset));
			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
				sb.append("\n");
			}
			br.close();
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public String post(String url, String data, String charset) {
		try {
			URL httpURL = new URL(url);
			String key = null;
			String cookieVal = null;
			HttpURLConnection http = (HttpURLConnection) httpURL
					.openConnection();
			http.setDoOutput(true);
			http.setDoInput(true);
			if (!sessionID.equals("")) {
				http.setRequestProperty("Cookie", sessionID);
			}
			BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(
					http.getOutputStream(), charset));
			bw.write(data);
			bw.close();
			BufferedReader br = new BufferedReader(new InputStreamReader(
					http.getInputStream(), charset));
			StringBuilder sb = new StringBuilder();
			String temp = null;
			while ((temp = br.readLine()) != null) {
				sb.append(temp);
				sb.append("\n");
			}
			br.close();
			for (int i = 1; (key = http.getHeaderFieldKey(i)) != null; i++) {
				if (key.equalsIgnoreCase("set-cookie")) {
					cookieVal = http.getHeaderField(i);
					cookieVal = cookieVal.substring(0, cookieVal.indexOf(";"));
					sessionID = sessionID + cookieVal + ";";
				}
			}
			return sb.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static void main(String[] args) {
		HttpClient client=new HttpClient();
	String str=client.get("http://www.nianshao.me/", "gbk");
	System.out.println(str);
	}
}