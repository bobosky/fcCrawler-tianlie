package com.statistic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import com.db.MongoDb;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.util.FileUtil2;
import com.util.JsonUtil;
import com.zj.bean.FangBean;

/**
 * mongo 数据统计
 * @author Administrator
 *
 */
public class MongoStastic {

	private String ip="192.168.1.4:27017";
	private String database="demo";
	
	private String name="";
	
	MongoDb mongo=null;
	
	FileUtil2 file=null;
	
	private String filePath=System.getProperty("user.dir")+"/data/fangPersonNum.txt";
	public MongoStastic(String str)
	{
		name=str;
	}
	/**
	 * 每平米人数
	 * @param perpleNum
	 */
	public void run(float perpleNum)
	{
		file=new FileUtil2(filePath,"utf-8");
		mongo=new MongoDb(ip,database);
		if(name.equals("fang"))
		{
			fangStastic(perpleNum);
		}
	}
	
	
	public void fangStastic(float perpleNum)
	{
		DBCursor cursor=mongo.find("fang");
		ArrayList<FangBean> fangList=new ArrayList<FangBean>();
		while(cursor.hasNext())
		{
			DBObject obj=cursor.next();
			obj.removeField("_id");
			String objStr=obj.toString();
			//System.out.println(objStr);
			FangBean fangBean=(FangBean)JsonUtil.getDtoFromJsonObjStr(objStr, FangBean.class);
			//System.out.println(fangBean.getFangCode()+"\t"+fangBean.getBuildArea().replace("平方米", "")+"\t"+fangBean.getOpenSpace().replace("平方米", "")+"\t"+
			//fangBean.getSaleCount()+"\t"+fangBean.getHireCount());
			if(fangBean.getFangCode().equals(""))
			{
				continue;
			}
			fangBean.setBuildArea(fangBean.getBuildArea().replace("平方米", ""));
			fangBean.setOpenSpace(fangBean.getOpenSpace().replace("平方米", ""));
			fangList.add(fangBean);
		}
		System.out.println("数据读取完成");
		System.out.println("开始清洗");
		//统一做数据处理
		buildAreaFill(fangList,10);
		int pCount=0;
		int pArea=0;
		LinkedList<String> input=new LinkedList<String>();
		for(int i=0;i<fangList.size();i++)
		{
			FangBean bean=fangList.get(i);
			int perNum=(int)(Integer.parseInt(bean.getBuildArea())*perpleNum);
			pCount+=perNum;
			pArea+=Integer.parseInt(bean.getBuildArea());
			//System.out.println(bean.getFangCode()+"\t"+bean.getBuildArea()+"\t人数:"+perNum);

			FangOutputBean beanOut=new FangOutputBean();
			beanOut.setFangCode(bean.getFangCode());
			beanOut.setBuildName(bean.getOfficeBuildingName());
			beanOut.setPersonNum((long)perNum);
			input.add(JsonUtil.getJsonStr(beanOut));
		}
		file.write(input);
		System.out.println("总计:"+pCount+"\t人");
		System.out.println("总面积:"+pArea+"\t㎡");
	}
	
	/**
	 * 面积的填充树处理
	 * 通过获取和房相似的前10个作为写字楼的平均值作为
	 * 平均占地面积
	 */
	public void buildAreaFill(ArrayList<FangBean> fangList,int limit)
	{
		HashMap<String,Float> map=new HashMap<String,Float>();

		for(int i=0;i<fangList.size();i++)
		{
			FangBean bean=fangList.get(i);
			if(isFill(bean))
			{
				ArrayList<FangBuildAreaBean> list=similaryFang(bean,fangList,map);
				float fval=0f;
				int count=0;
				for(FangBuildAreaBean b:list)
				{
					count++;
					fval+=b.getBuildArea();
					if(count>=limit)
					{
						break;
					}
				}
				bean.setBuildArea(Integer.toString((int)fval/limit));
				//System.out.println(bean.getFangCode()+"\t"+bean.getBuildArea()+"\t"+Integer.toString((int)fval/limit));
				//input.add(JsonUtil.getJsonStr(obj));

			}
		}
	}
	
	public boolean isFill(FangBean bean)
	{
//		System.out.println();
		return bean.getBuildArea().contains("资料")||bean.getBuildArea().equals("");
	}
	
	/**
	 * 获取写字楼相似的列表中
	 * @param fang
	 * @param fangList
	 * @param map
	 */
	public ArrayList<FangBuildAreaBean> similaryFang(FangBean fang,ArrayList<FangBean> fangList,HashMap<String,Float> map)
	{
		//获取前10个
		ArrayList<FangBuildAreaBean> fangSort=new ArrayList<FangBuildAreaBean>();
		for(int i=0;i<fangList.size();i++)
		{
			FangBean bean=fangList.get(i);
			if(isFill(bean))
			{
				continue;
			}
			if(fang.getBuildArea().equals(bean))
			{
				continue;
			}
			float val=similaryFunction(fang,bean,map);
		//	System.out.println(bean.getBuildArea());
			FangBuildAreaBean b=new FangBuildAreaBean(Float.parseFloat(bean.getBuildArea()),val);
			fangSort.add(b);
		}
		Collections.sort(fangSort);
		return fangSort;
	}

	/**
	 * 相似度程序
	 * @param fang1
	 * @param fang2
	 * @param map
	 * @return
	 */
	public Float similaryFunction(FangBean fang1,FangBean fang2,HashMap<String,Float> map)
	{
		String str=getU_U(fang1,fang2);
		Float fvalue=map.get(str);
		
		Float result=0F;
		if(fvalue==null)
		{
			int size=3;
			float[] fangVal=new float[size];
			float[] fangVal2=new float[size];
			fangVal[0]=fang1.getSaleCount();
			fangVal[1]=fang1.getHireCount();
			fangVal[2]=(float) Math.log(fang1.getScanCount());
			fangVal2[0]=fang2.getSaleCount();
			fangVal2[1]=fang2.getHireCount();
			fangVal2[2]=(float) Math.log(fang2.getScanCount());
			float val1=0f;
			float val2=0f;
			float valup=0f;
			for(int i=0;i<size;i++)
			{
				val1+=Math.pow(fangVal[i],2f);
				val2+=Math.pow(fangVal2[i],2f);
				valup+=fangVal[i]*fangVal2[i];
			}
			if(valup<1E-4)
			{
//				result= 0f;
			}
			result= (float)(valup/(val1+val2));
		}else{
//			return fvalue;
		}
		map.put(str,result);
		return result;
	}
	
	/**
	 * 获取唯一key
	 * @param fang1
	 * @param fang2
	 * @return
	 */
	public String getU_U(FangBean fang1,FangBean fang2)
	{
		if(fang1.getFangCode().compareTo(fang2.getFangCode())>0)
		{
			return fang2.getFangCode()+""+fang1.getFangCode();
		}else if(fang1.getFangCode().compareTo(fang2.getFangCode())<0){
			return fang1.getFangCode()+""+fang2.getFangCode();
		}else {
			return null;
		}
	}
	
	public static void main(String[] args) {
		//总人数/平米数
		float weight=(float)8119920.0/138169981;
		MongoStastic stastic=new MongoStastic("fang");
		stastic.run(weight);
		System.out.println("每平米:"+weight+" 人");
		
	}
}
