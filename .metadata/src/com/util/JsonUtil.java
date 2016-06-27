package com.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import net.sf.ezmorph.object.DateMorpher;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.util.JSONUtils;
import net.sf.json.xml.XMLSerializer;

/**  
 * json
 * (C) 
 */  
public class JsonUtil {
	private static Logger log = Logger.getLogger(JsonUtil.class);
	/**
	 * 
	 * 
	 * @param jsonObjStr
	 *            e.g. {'name':'get','dateAttr':'2009-11-12'}
	 * @param clazz
	 *            Person.class
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static Object getDtoFromJsonObjStr(String jsonObjStr, Class clazz) {
		try{
			return JSONObject.toBean(JSONObject.fromObject(jsonObjStr), clazz);
		}catch(Exception e){
			log.error("json:"+jsonObjStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param jsonObjStr
	 *            e.g. {'data':[{'name':'get'},{'name':'set'}]}
	 * @param clazz
	 *            e.g. MyBean.class
	 * @param classMap
	 *            e.g. classMap.put("data", Person.class)
	 * @return Object
	 */
	@SuppressWarnings("rawtypes")
	public static Object getDtoFromJsonObjStr(String jsonObjStr, Class clazz,
			Map classMap) {
		try{
			return JSONObject.toBean(JSONObject.fromObject(jsonObjStr), clazz,
					classMap);
		}catch(Exception e){
			log.error("json:"+jsonObjStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
		
	}

	/**
	 * 
	 * 
	 * @param jsonArrStr
	 *            e.g. ['get',1,true,null]
	 * @return Object[]
	 */
	public static Object[] getArrFromJsonArrStr(String jsonArrStr) {
		try{
			return JSONArray.fromObject(jsonArrStr).toArray();
		}catch(Exception e){
			log.error("json:"+jsonArrStr+",exception:"+e.getMessage());
			return null;
		}
		
	}

	/**
	 * 
	 * 
	 * @param jsonArrStr
	 *            e.g. [{'name':'get'},{'name':'set'}]
	 * @param clazz
	 *            e.g. Person.class
	 * @return Object[]
	 */
	@SuppressWarnings("rawtypes")
	public static Object[] getDtoArrFromJsonArrStr(String jsonArrStr,
			Class clazz) {
		try{
			JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);
			Object[] objArr = new Object[jsonArr.size()];
			for (int i = 0; i < jsonArr.size(); i++) {
				objArr[i] = JSONObject.toBean(jsonArr.getJSONObject(i), clazz);
			}
			return objArr;
		}catch(Exception e){
			log.error("json:"+jsonArrStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
		
	}
	@SuppressWarnings("rawtypes")
	public static List<Object> getDtoArrFromJsonArrListStr(String jsonArrStr,
			Class clazz) {
		try{
			JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);
			JSONArray jsonArray=(JSONArray)jsonArr.getJSONArray(0);
			List<Object> objArr = new ArrayList<Object>();
			for (int i = 0; i < jsonArr.size(); i++) {
				objArr.add(JSONObject.toBean(jsonArray.getJSONObject(i), clazz));
			}
			return objArr;
		}catch(Exception e){
			log.error("json:"+jsonArrStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
		
	}
	/**
	 * 
	 * 
	 * @param jsonArrStr
	 *            e.g. [{'data':[{'name':'get'}]},{'data':[{'name':'set'}]}]
	 * @param clazz
	 *            e.g. MyBean.class
	 * @param classMap
	 *            e.g. classMap.put("data", Person.class)
	 * @return Object[]
	 */
	@SuppressWarnings("rawtypes")
	public static Object[] getDtoArrFromJsonArrStr(String jsonArrStr,
			Class clazz, Map classMap) {
		try{
			JSONArray array = JSONArray.fromObject(jsonArrStr);
			Object[] obj = new Object[array.size()];
			for (int i = 0; i < array.size(); i++) {
				JSONObject jsonObject = array.getJSONObject(i);
				obj[i] = JSONObject.toBean(jsonObject, clazz, classMap);
			}
			return obj;
		}catch(Exception e){
			log.error("json:"+jsonArrStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
		
	}

	/**
	 * 
	 * 
	 * @param jsonArrStr
	 *            e.g. ['get',1,true,null]
	 * @return List
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getListFromJsonArrStr(String jsonArrStr) {
		try{
			JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);
			List list = new ArrayList();
			for (int i = 0; i < jsonArr.size(); i++) {
				list.add(jsonArr.get(i));
			}
			return list;
		}catch(Exception e){
			log.error("json:"+jsonArrStr+",exception:"+e.getMessage());
			return null;
		}
		
	}

	/**
	 * 
	 * 
	 * @param jsonArrStr
	 *            e.g. [{'name':'get'},{'name':'set'}]
	 * @param clazz
	 * @return List
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getListFromJsonArrStr(String jsonArrStr, Class clazz) {
		
		try{
			JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);
			List list = new ArrayList();
			for (int i = 0; i < jsonArr.size(); i++) {
				list.add(JSONObject.toBean(jsonArr.getJSONObject(i), clazz));
			}
			return list;
		}catch(Exception e){
			log.error("json:"+jsonArrStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
	}
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getListFromJsonArrStrLinked(String jsonArrStr, Class clazz) {
		
		try{
			JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);
			List list = new LinkedList();
			for (int i = 0; i < jsonArr.size(); i++) {
				list.add(JSONObject.toBean(jsonArr.getJSONObject(i), clazz));
			}
			return list;
		}catch(Exception e){
			log.error("json:"+jsonArrStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
	}

	/**
	 * 
	 * 
	 * @param jsonArrStr
	 *            e.g. [{'data':[{'name':'get'}]},{'data':[{'name':'set'}]}]
	 * @param clazz
	 *            e.g. MyBean.class
	 * @param classMap
	 *            e.g. classMap.put("data", Person.class)
	 * @return List
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getListFromJsonArrStr(String jsonArrStr, Class clazz,
			Map classMap) {
		
		try{
			JSONArray jsonArr = JSONArray.fromObject(jsonArrStr);
			List list = new ArrayList();
			for (int i = 0; i < jsonArr.size(); i++) {
				list.add(JSONObject.toBean(jsonArr.getJSONObject(i), clazz,
						classMap));
			}
			return list;
		}catch(Exception e){
			log.error("json:"+jsonArrStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
	}

	/**
	 *
	 * 
	 * @param jsonObjStr
	 *            e.g. {'name':'get','int':1,'double',1.1,'null':null}
	 * @return Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getMapFromJsonObjStr(String jsonObjStr) {
		
		try{
			Map map = new HashMap();
			if(jsonObjStr.equals("0")){
				return map;
			}
			JSONObject jsonObject = JSONObject.fromObject(jsonObjStr);
			for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
				String key = (String) iter.next();
				map.put(key, jsonObject.get(key));
			}
			return map;
		}catch(Exception e){
			log.error("json:"+jsonObjStr+",exception:"+e.getMessage());
			return null;
		}
	}

	/**
	 *
	 * 
	 * @param jsonObjStr
	 *            e.g. {'data1':{'name':'get'},'data2':{'name':'set'}}
	 * @param clazz
	 *            e.g. Person.class
	 * @return Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getMapFromJsonObjStr(String jsonObjStr, Class clazz) {
		try{
			JSONObject jsonObject = JSONObject.fromObject(jsonObjStr);

			Map map = new HashMap();
			for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
				String key = (String) iter.next();
				map.put(key,
						JSONObject.toBean(jsonObject.getJSONObject(key), clazz));
			}
			return map;
		}catch(Exception e){
			log.error("json:"+jsonObjStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
		
	}

	/**
	 *
	 * 
	 * @param jsonObjStr
	 *            e.g. {'mybean':{'data':[{'name':'get'}]}}
	 * @param clazz
	 *            e.g. MyBean.class
	 * @param classMap
	 *            e.g. classMap.put("data", Person.class)
	 * @return Map
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map getMapFromJsonObjStr(String jsonObjStr, Class clazz,
			Map classMap) {
		try{
			JSONObject jsonObject = JSONObject.fromObject(jsonObjStr);

			Map map = new HashMap();
			for (Iterator iter = jsonObject.keys(); iter.hasNext();) {
				String key = (String) iter.next();
				map.put(key, JSONObject.toBean(jsonObject.getJSONObject(key),
						clazz, classMap));
			}
			return map;
		}catch(Exception e){
			log.error("json:"+jsonObjStr+",clazz:"+clazz+",exception:"+e.getMessage());
			return null;
		}
	}

	/**
	 *
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 *             String
	 */
	public static String getJsonStr(Object obj) {
		try{
			String jsonStr = null;
			// Json����
			JsonConfig jsonCfg = new JsonConfig();

			// ע�����ڴ�����
			jsonCfg.registerJsonValueProcessor(java.util.Date.class,
					new JsonDateValueProcessor());
			if (obj == null) {
				return "{}";
			}

			if (obj instanceof Collection || obj instanceof Object[]) {
				jsonStr = JSONArray.fromObject(obj, jsonCfg).toString();
			} else {
				jsonStr = JSONObject.fromObject(obj, jsonCfg).toString();
			}
			return jsonStr;
		}catch(Exception e){
			log.error("exception:"+e.getMessage());
			return "";
		}
		
	}

	/**
	 * 
	 * http://json-lib
	 * .sourceforge.net/apidocs/net/sf/json/xml/XMLSerializer.html ����ʵ����ο���
	 * http://json-lib.sourceforge.net/xref-test/net/sf/json/xml/
	 * TestXMLSerializer_writes.html
	 * http://json-lib.sourceforge.net/xref-test/net
	 * /sf/json/xml/TestXMLSerializer_writes.html
	 * 
	 * @param obj
	 * @return
	 * @throws Exception
	 *             String
	 */
	public static String getXMLFromObj(Object obj) {
		XMLSerializer xmlSerial = new XMLSerializer();

		// Json����
		JsonConfig jsonCfg = new JsonConfig();

		// ע�����ڴ�����
		jsonCfg.registerJsonValueProcessor(java.util.Date.class,
				new JsonDateValueProcessor());

		if ((String.class.isInstance(obj) && String.valueOf(obj)
				.startsWith("["))
				|| obj.getClass().isArray()
				|| Collection.class.isInstance(obj)) {
			JSONArray jsonArr = JSONArray.fromObject(obj, jsonCfg);
			return xmlSerial.write(jsonArr);
		} else {
			JSONObject jsonObj = JSONObject.fromObject(obj, jsonCfg);
			return xmlSerial.write(jsonObj);
		}
	}

	/**
	 * ��XMLתjson��
	 * 
	 * @param xml
	 * @return String
	 */
	public static String getJsonStrFromXML(String xml) {
		XMLSerializer xmlSerial = new XMLSerializer();
		return String.valueOf(xmlSerial.read(xml));
	}

	@SuppressWarnings("unused")
	private static void setDataFormat2JAVA() {
		// �趨����ת����ʽ
		JSONUtils.getMorpherRegistry().registerMorpher(
				new DateMorpher(new String[] { "yyyy-MM-dd",
						"yyyy-MM-dd HH:mm:ss" }));
	}

}
