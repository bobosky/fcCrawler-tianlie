package test.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import test.bean.ItemNode;
import test.bean.UserNode;
import test.weight.WeightUtil;

/**
 * 获取
 * @author Administrator
 *
 */
public class ItemGetDownCel implements ItemGetRurl{

	@Override
	public void getRecommendItems(UserNode user1,UserNode user2, float sim,HashMap<Long, long[]> map,WeightUtil weight) {
		// TODO Auto-generated method stub
		for(ItemNode item2:user2.getItems())
		{
			//如果不在 用户 1中
			ItemNode temp=user1.getItem(item2.getItemId());
			if(temp==null)
			{
				//如果为正常协同过滤则为null,如果为content则添加
				if(weight==null)
				{
					user1.addSortItem(new ItemNode(item2.getItemId(),item2.getValue()*sim,item2.getCount()));
				}else{
				//获取总量
				float it2=weight.getCount(item2);
				user1.addSortItem(new ItemNode(item2.getItemId(),item2.getValue()*sim*2/it2,item2.getCount()));
				}
			}else{
				
			}
		}
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
					user2.addSortItem(new ItemNode(item2.getItemId(),item2.getValue()*sim,item2.getCount()));
				}else{
				//获取总量
				float it2=weight.getCount(item2);
				user2.addSortItem(new ItemNode(item2.getItemId(),item2.getValue()*sim*2/it2,item2.getCount()));
				}
			}else{
				
			}
		}
		
	}
	
	
}
