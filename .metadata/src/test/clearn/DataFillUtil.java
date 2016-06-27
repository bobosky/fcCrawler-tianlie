package test.clearn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map.Entry;

import com.etl.FileUtil2;

/**
 * 缺失值填补方法
 * @author Administrator
 *
 */
public class DataFillUtil {
	
		/**
		 * 对应的映射列
		 */
		public  HashMap<Long, long[]> relMap=new HashMap<Long,long[]>();
		/**
		 * 需要操作的id
		 */
		public  LinkedList<Long> notFile=new LinkedList<Long>();
		/**
		 * content对应的值float值
		 */
		public HashMap<Long,float[]> map =new HashMap<Long,float[]>();
		
		/**
		 * 全部离散化的值
		 */
		public HashMap<Long,int[]> mapS =new HashMap<Long,int[]>();
		/**
		 * id对应mall信息
		 */
		public HashMap<Long,String> mall=new HashMap<Long,String>();
		
		/**
		 * id对应brand信息
		 */
		public HashMap<Long,String> brand=new HashMap<Long,String>();
		
		/**
		 * 业态信息
		 */
		public HashMap<Long,String> category=new HashMap<Long,String>();
		
		/**
		 * mall的统计结果
		 */
		public Statistics mallStatistic=new Statistics();
		
		/**
		 * brand的统计结果
		 */
		public Statistics brandStatistic=new Statistics();
		
		/**
		 * 用于从原始数据根据条件添加 新的feather的公共方法
		 */
		public ChangeInter change=null;
		
		public ChangeInter getChange() {
			return change;
		}

		public void setChange(ChangeInter change) {
			this.change = change;
		}

		/**
		 * 统一的执行程序
		 * 需要获取feather对应在文件种的index 从0开始
		 */
	    public void run(HashSet<Integer> feather,HashMap<Integer,Integer> cluster)
	    {
	    	
	    }
	    
	    public long[] getRel(Long l)
	    {
	    	return relMap.get(l);
	    }
	    public Long poll()
	    {
	    	return notFile.poll();
	    }
	    public void add(Long e)
	    {
	    	if(notFile.peekLast()!=e)
	    		notFile.add(e);
	    }
	    
	    //写文件
	    public void print(String filePath)
	    {
	    	FileUtil2 fileUtil=new FileUtil2(filePath,"utf-8");
	    	LinkedList<String> write=new LinkedList<String>();
	    	for(Entry<Long,float[]> m:map.entrySet())
	    	{
	    		String str=Long.toString(m.getKey());
	    		int i=0;
	    		for(float f:m.getValue())
	    		{
	    			str+="\t"+f;
	    		}
	    		write.add(str);
	    	}
	    	fileUtil.wirte(write, write.size());
	    	System.out.println("文件写入完成");
	    }
	    /**
	     * 打印离散化的变量
	     * @param filePath
	     */
	    public void printS(String filePath)
	    {
	    	FileUtil2 fileUtil=new FileUtil2(filePath,"utf-8");
	    	LinkedList<String> write=new LinkedList<String>();
	    	for(Entry<Long,int[]> m:mapS.entrySet())
	    	{
	    		String str=Long.toString(m.getKey());
	    	//	float[] val=map.get(m.getKey());
	    		for(int f:m.getValue())
	    		{
	    			str+="\t"+f;
	    		}
//	    		for(float f:val)
//	    		{
//	    			str+="\t"+f;
//	    		}
	    		write.add(str);
	    	}
	    	fileUtil.wirte(write, write.size());
	    	System.out.println("文件写入完成");
	    }
	    
	    
	    /**
		 * 获取交叉方法
		 * 
		 * @param index
		 *            使用的index1
		 * @param index2
		 *            使用的index2
		 * @param mapIndex
		 *            使用的分类
		 * @param rel
		 *            最终生成的分类号
		 * 
		 */
		public void runCoss(int index, int index2, ArrayList<int[]> mapIndex,
				ArrayList<int[]> mapIndex2, ArrayList<Integer> rel, Integer otherRel) {
			for (Entry<Long, int[]> m : mapS.entrySet()) {
				int[] val = m.getValue();
				int[] valEnd = new int[val.length + 1];
				for(int i=0;i<val.length;i++)
				{
					valEnd[i]=val[i];
				}
				boolean flagN = false;
				for (int i = 0; i < mapIndex.size(); i++) {
					boolean flag = false;
					for (int iL1 : mapIndex.get(i)) {
						// 如果满足要求
						if (val[index] == iL1) {
							flag = true;
							break;
						}
					}
					if (flag) {
						flag = false;
						for (int iL1 : mapIndex2.get(i)) {
							// 如果满足要求
							if (val[index2] == iL1) {
								flag = true;
								break;
							}
						}
					} else {
						continue;
					}
					if (flag) {
						valEnd[val.length] = rel.get(i);
						flagN = true;
						break;
					}
				}
				if (!flagN) {
					valEnd[val.length] = otherRel;
				}
				m.setValue(valEnd);
			}
		}
		/**
		 * 获取原始数据非离散数据
		 * 新添加字段
		 * @param index 需要获取的字段
		 * @param 
		 */
		public void addOne()
		{
			for(Entry<Long,float[]> val:map.entrySet())
			{
				float[] fval=val.getValue();
				int[] fint=mapS.get(val.getKey());
//				int[] fNew=new int[fint.length+1];
//				for(int i=0;i<fint.length;i++)
//				{
//					fNew[i]=fint[i];
//				}
//				int change2=this.change.change(fval);
//				fNew[fint.length]=change2;
				int[] fNew=this.change.change(fval,fint);
				mapS.put(val.getKey(), fNew);
			}
		}
		
		public void updateOne()
		{
			for(Entry<Long,float[]> val:map.entrySet())
			{
				float[] fval=val.getValue();
				int[] fint=mapS.get(val.getKey());
				//int[] fNew=new int[fint.length+1];
//				for(int i=0;i<fint.length;i++)
//				{
//					fNew[i]=fint[i];
//				}
				int[] fNew=this.change.change(fval,fint);
				mapS.put(val.getKey(), fNew);
			}
		}

		

		public Long parseLong(String str) {
			return Long.parseLong(str.equals("") ? "0" : str);
		}
	
}
