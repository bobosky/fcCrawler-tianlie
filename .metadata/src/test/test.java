package test;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;

import com.util.JsonUtil;
import com.zj.bean.JobsInputQueueBean;
import com.zj.bean.LationLngLat;

public class test {
public static void main(String[] args) throws UnsupportedEncodingException {
//	String str="%D6%D0%B9%D8%B4%E5";
//	String str2="%E4%B8%AD%E5%85%B3%E6%9D%91";
//	System.out.println(URLDecoder.decode(str,"gbk"));
//	System.out.println(URLDecoder.decode(str2,"utf-8"));
//	
//	System.out.println(URLEncoder.encode(str));
	String str="{\"currentPage\":2,\"location\":{\"lat\":339.7361632323,\"lng\":\"116.189358\"},\"nearKm\":1,\"subWayLine\":\"\",\"subWayLineCode\":\"010000\",\"subWayStation\":\"良乡大学城北\",\"subWayStationCode\":\"\",\"url\":\"\",\"workCategory\":-1}";
	HashMap<String,Object> map=new HashMap<String,Object>();
	//map.put("location", LationLngLat.class);
	JobsInputQueueBean result=(JobsInputQueueBean)JsonUtil.getDtoFromJsonObjStr(str, JobsInputQueueBean.class);
	System.out.println(result.getLocation().getLng());
	System.out.println(result.getLocation().getLat());
	System.out.println(result.getCurrentPage());
//	String s = "{'str':123.519412}";
//	System.out.println(JsonUtil.getDtoFromJsonObjStr(s,Double.class));
//	
//	System.out.println(JsonUtil.getJsonStr(result));
	System.out.println(Double.parseDouble("114.736133342322323353"));
	
	
	System.out.println("&nstp;".replaceAll("(&nstp;)","1"));
	
	System.out.println(new Date());
}
}
