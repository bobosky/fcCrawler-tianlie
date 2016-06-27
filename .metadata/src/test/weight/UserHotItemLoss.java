package test.weight;

import java.util.Map.Entry;

import test.bean.ItemNode;
import test.bean.UserInfo;
import test.bean.UserNode;

/**
 * 计算用户相似度的时候
 * 做热门物品损失
 * sum(x*y*1/(log(1+n)))/n1*n2
 * 
 * *1/(log(1+n)
 * @author Administrator
 *
 */
public class UserHotItemLoss implements WeightUtil{
	public UserNode userNode=null;
	public UserHotItemLoss()
	{
		
	}
	public void init(UserInfo userInfo)
	{
		userNode=new UserNode();
		for(Entry<Long, UserNode> user:userInfo.entrySet())
		{
			for(Entry<Long,ItemNode> item:user.getValue().entrySet())
			{
				//统计所有用户的物品信息
				ItemNode item2=new ItemNode(item.getKey(),1,1);
				userNode.addItemAccumulation(item2);
			}
		}
		//将元数据 数量设置上
		for(Entry<Long, UserNode> user:userInfo.entrySet())
		{
			for(Entry<Long,ItemNode> item:user.getValue().entrySet())
			{
				//System.out.println(item.getKey()+"\t"+userNode.getItem(item.getKey()).getCount());
				item.getValue().setCount(userNode.getItem(item.getKey()).getCount());
			}
		}
	}
	@Override
	public float getCount(ItemNode item) {
		// TODO Auto-generated method stub
		//通过userNode1的物品获取userNode2物品
		ItemNode item2= userNode.getItem(item.getItemId());
		return item2.getCount();
	}
	@Override
	public int size() {
		// TODO Auto-generated method stub
		return userNode.size();
	}
	
	
	
}
