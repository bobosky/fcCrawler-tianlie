package test.similary;

import java.util.ArrayList;
import java.util.HashMap;

import test.bean.ItemNode;
import test.bean.UserNode;
import test.weight.WeightUtil;

/**
 * jarcard 相关系数
 * @author Administrator
 *
 */
public class JarcardSimilary implements SimilaryUtil{

	@Override
	public SimilaryUtil getSimilaryFunc() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,WeightUtil weight) {
		// TODO Auto-generated method stub
		//获取物品
		//添加用户权重信息
		ArrayList<ItemNode> item1=userNode1.getItems();
		//ArrayList<ItemNode> item2=userNode2.getItems();
		int item2Size=userNode2.size();
		if(item1.size()==0 || item2Size==0)
		{
			return 0f;
		}
		float value=0;
		if(weight==null)
		{
			for(ItemNode item:item1)
			{
				//System.out.print(item.getItemId()+"\t");
				ItemNode it=userNode2.getItem(item.getItemId());
				if(it==null)
				{
					continue;
				}
				value++;
			}
			return value/(item1.size()*item2Size);
		}else{

			for(ItemNode item:item1)
			{
				//System.out.print(item.getItemId()+"\t");
				ItemNode it=userNode2.getItem(item.getItemId());
				if(it==null)
				{
					continue;
				}
				float it2=weight.getCount(item);
				value+=item.getValue()*it.getValue()/Math.log(1+it2);
			}
			return  (value/(float)(Math.sqrt(item1.size()*item2Size)));
		}
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			HashMap<Long, long[]> map,WeightUtil weight,float[] weightPower) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
	
}
