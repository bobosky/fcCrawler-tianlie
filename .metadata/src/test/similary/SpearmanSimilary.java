package test.similary;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import test.bean.ItemNode;
import test.bean.UserNode;
import test.weight.WeightUtil;

/**
 * 斯皮尔曼相关系数
 * @author Administrator
 *
 */
public class SpearmanSimilary implements SimilaryUtil{

	//public Hash
	@Override
	public SimilaryUtil getSimilaryFunc() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,WeightUtil weight) {
		// TODO Auto-generated method stub
		//计算秩
		LinkedList<ItemNode> item1=userNode1.getItemsLinked();
		LinkedList<ItemNode> item2=userNode2.getItemsLinked();
		for(ItemNode item:item1)
		{
			ItemNode it=userNode2.getItem(item.getItemId());
			if(it==null)
			{
				ItemNode it2=new ItemNode(item.getItemId(),0,1);
				item2.add(it2);
			}
		}
		for(ItemNode item:item2)
		{
			ItemNode it=userNode1.getItem(item.getItemId());
			if(it==null)
			{
				ItemNode it2=new ItemNode(item.getItemId(),0,1);
				item1.add(it2);
			}
		}
		if(item1.size()==0 || item2.size()==0)
		{
			return 0f;
		}
		Collections.sort(item1);
		Collections.sort(item2);
		int i=0;
		int[] xRank=new int[item1.size()];
		getRank(item1,xRank);
		int[] yRank=new int[item2.size()];
		getRank(item2,yRank);
		//利用差分等级(或排行)序列计算斯皮尔曼等级相关系数
		float fenzi=0f;
		for(int j=0;j<xRank.length;j++)
		{
			fenzi+=Math.pow(xRank[j]-yRank[j],2f);
		}
		fenzi*=6;
		float fenmu=xRank.length*(xRank.length*xRank.length-1);
		return 1-fenzi/fenmu;
	}
	/**
	 * 获取秩
	 * @param items
	 * @param rank
	 */
	public void getRank(LinkedList<ItemNode> items,int[] rank)
	{
		int i=0;
		for(ItemNode item:items)
		{
			int count1=1;//记录大于特定元素的元素个数
			int count2=-1;//记录与特定元素相同的元素个数  
			for(ItemNode item2:items)
			{
				if(item.getValue()<item2.getValue())
				{
					count1++;
				}else if(item.getValue()==item2.getValue())
				{
					count2++;
				}
			}
			rank[i]=count1+getMean(count2);
			i++;	
		}
	}

	public int getMean(int count2)
	{
		int val=0;
		for(int i=0;i<=count2;i++)
		{
			val+=i;
		}
		return val/(count2+1);
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			HashMap<Long, long[]> map,WeightUtil weight,float[] weightPower) {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
}
