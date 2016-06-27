package test.similary;

import java.util.ArrayList;
import java.util.HashMap;

import test.bean.ItemNode;
import test.bean.UserNode;
import test.weight.WeightUtil;

/**
 * sum(|xi-yi|)
 * pearson 曼哈顿距离
 * @author Administrator
 *
 */
public class CityBlockSimilary implements SimilaryUtil{

	@Override
	public SimilaryUtil getSimilaryFunc() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,WeightUtil weight) {
		// TODO Auto-generated method stub
		ArrayList<ItemNode> item1=userNode1.getItems();
		ArrayList<ItemNode> item2=userNode2.getItems();
		if(item1.size()==0 || item2.size()==0)
		{
			return 0f;
		}
		float result=0f;
		if(weight==null)
		{
			for(ItemNode item:item1)
			{
				//System.out.print(item.getItemId()+"\t");
				ItemNode it=userNode2.getItem(item.getItemId());
				if(it==null)
				{
					result+=item.getValue();
					continue;
				}
				result+=Math.abs(item.getValue()-it.getValue());
			}
		}else{
			for(ItemNode item:item1)
			{
				//System.out.print(item.getItemId()+"\t");
				ItemNode it=userNode2.getItem(item.getItemId());
				if(it==null)
				{
					result+=item.getValue();
					continue;
				}
				float it2=weight.getCount(item);
				result+=Math.abs(item.getValue()-it.getValue())*Math.log(1+it2);
			}
		}
		for(ItemNode item:item2)
		{
			ItemNode it=userNode1.getItem(item.getItemId());
			if(it==null)
			{
				result+=item.getValue();
				continue;
			}
		}
		return result;
		
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			HashMap<Long, long[]> map,WeightUtil weight,float[] weightPower) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
}
