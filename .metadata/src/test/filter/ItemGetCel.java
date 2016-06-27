package test.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import test.bean.ItemNode;
import test.bean.UserNode;
import test.weight.WeightUtil;

/**
 * 获取补集
 * @author Administrator
 *
 */
public class ItemGetCel implements ItemGetRurl{

	@Override
	public void getRecommendItems(UserNode user1,UserNode user2, float sim,HashMap<Long, long[]> map,WeightUtil weight) {
		// TODO Auto-generated method stub
		if(sim<1E-10)
		{
			return;
		}
		long[] user1Item=null;
		if(map!=null)
			user1Item=map.get(user1.getUserId());
		for(ItemNode item2:user2.getItems())
		{
			//如果不在 用户 1中
			ItemNode temp=user1.getItem(item2.getItemId());
			if(temp==null)
			{
				//如果为正常协同过滤则为null,如果为content则添加
				if(weight==null)
				{
					//***************需要添加match方法实现硬匹配方法
					if(map!=null)
					{
					long[] itemItem=map.get(item2.getItemId());
					float sim2=getMathRate(user1Item,itemItem);
					user1.addSortItem(new ItemNode(item2.getItemId(),item2.getValue()*(sim+sim2)/2,item2.getCount()));
					}else{
						user1.addSortItem(new ItemNode(item2.getItemId(),item2.getValue()*sim,item2.getCount()));
					}
				}else{
				//获取总量
					if(map!=null)
					{
					long[] itemItem=map.get(item2.getItemId());
					float sim2=getMathRate(user1Item,itemItem);
				float it2=weight.getCount(item2);
				//System.out.println(item2.getItemId()+"\t"+it2);
			//	System.out.println(item2.getItemId()+"\t"+item2.getValue()*(sim)*(sim2)/Math.log(1+it2));
				user1.addSortItem(new ItemNode(item2.getItemId(),(float) (item2.getValue()*(sim)*(sim2)/Math.log(1+it2)),item2.getCount()));
					}else{
						float it2=weight.getCount(item2);
						user1.addSortItem(new ItemNode(item2.getItemId(), (float) (item2.getValue()*(sim)/Math.log(1+it2)),item2.getCount()));
					}
					}
			}else{
				
			}
		}
		long[] user2Item=null;
		if(map!=null)
			user2Item=map.get(user2.getUserId());
		//System.out.println(i+"\t"+j+"\t"+sim);
		for(ItemNode item2:user1.getItems())
		{
			//如果不在 用户 1中
			ItemNode temp=user2.getItem(item2.getItemId());
			if(temp==null)
			{
				//添加损失权重
				if(weight==null)
				{
					if(map!=null)
					{
					long[] itemItem=map.get(item2.getItemId());
					float sim2=getMathRate(user2Item,itemItem);
					//***************需要添加match方法实现硬匹配方法
					user2.addSortItem(new ItemNode(item2.getItemId(),item2.getValue()*(sim+sim2)/2,item2.getCount()));
					}else{
						user2.addSortItem(new ItemNode(item2.getItemId(),item2.getValue()*(sim),item2.getCount()));	
					}
				}else{
				//获取总量
					if(map!=null)
					{
				float it2=weight.getCount(item2);
				long[] itemItem=map.get(item2.getItemId());
				float sim2=getMathRate(user2Item,itemItem);
				user2.addSortItem(new ItemNode(item2.getItemId(),(float) (item2.getValue()*(sim)*(sim2)/Math.log(1+it2)),item2.getCount()));
					}else{
						float it2=weight.getCount(item2);
						user2.addSortItem(new ItemNode(item2.getItemId(),(float) (item2.getValue()*(sim)/Math.log(1+it2)),item2.getCount()));
					}
					}
			}else{
				
			}
		}
		
	}
	
	/**
	 * 计算匹配度
	 * @param userNode1Map
	 * @param userNode2Map
	 * @return
	 */
	public float getMathRate(long[] userNode1Map,long[] userNode2Map) {
		// TODO Auto-generated method stub
		//获取对应的纬度数据
				int count=0;
				for(int i=0;i<userNode1Map.length;i++)
				{
					if(userNode1Map[i]==userNode2Map[i])
					count++;
				}
				//System.out.println("count:"+count);
		return count*1f/userNode1Map.length;
	}
	
	
}
