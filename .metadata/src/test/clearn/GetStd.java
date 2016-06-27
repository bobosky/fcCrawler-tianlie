package test.clearn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

public class GetStd {

	
	/**
	 * 
	 * @param val
	 * @param valFloat 均值
	 * @return
	 */
	public static float[] get(ArrayList<ArrayList<Float>> val,float[] valFloat)
	{
		float[] result=new float[valFloat.length];
		for(int i=0;i<val.size();i++)
		{
			for(Float f:val.get(i))
			{
				result[i]+=Math.pow(f-valFloat[i],2f);
			}
			result[i]=(float) Math.sqrt(result[i]/(val.get(i).size()-1));
		}
		return result;
	}
	/**
	 * 正规化方法
	 * @param map
	 * @param mean
	 * @param std
	 */
	public static void change(HashMap<Long,float[]> map,float[] mean,float[] std)
	{
		for(Entry<Long,float[]> entry:map.entrySet())
		{
			float[] fl=entry.getValue();
			for(int i=0;i<fl.length;i++)
			{
				float fz=fl[i];
				fl[i]=(fl[i]-mean[i])/std[i];
				if(Math.abs(fl[i])>1)
				{
					fl[i]=0;
					//System.out.println(fl[i]+"\t"+fz+"\t"+mean[i]+"\t"+std[i]);
				}
			}
		}
	}
	public static void  print(float[] mean,float[] std,String name)
	{
		System.out.println(name);
		for(int i=0;i<mean.length;i++)
		{
			System.out.println(i+"\tmean"+mean[i]+"\tstd:"+std[i]);
		}
	}
}
