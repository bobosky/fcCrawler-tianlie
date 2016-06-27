package test;

import java.util.ArrayList;

public class SortList {
	
//	public static List<T extends Comparable<? super T>> sort(List<T> list)
//	{
//		List<T> result=new LinkedList<T>();
//		for(T t:list)
//		{
//		}
//	}
	
	//直接跳用冒泡排序获取前n个最大值
	/**
	 * 
	 * @param list
	 * @param limit
	 * @param flag true 降序 false升序
	 */
	public static <T extends Comparable<? super T>> void bubbleSort(ArrayList<T> list,int limit,boolean flag)
	{
		Object[] fl=list.toArray();
		for(int j=0;j<limit;j++)
		{
			if(flag)
			{
				for(int i=fl.length-1;i>0;i--)
				{
					if(((Comparable)fl[i]).compareTo(fl[i-1])>0)
					{
						fl[i-1]=fl[i];
					}
				}
				list.set(j,(T)fl[j]);
			}else{
				for(int i=fl.length-1;i>0;i--)
				{
					if(((Comparable)fl[i]).compareTo(fl[i-1])<0)
					{
						fl[i-1]=fl[i];
					}
				}
				list.set(j,(T)fl[j]);
			}
		}
	}
}
