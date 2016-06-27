package test.similary;

import java.util.ArrayList;
import java.util.HashMap;

import test.bean.ItemNode;
import test.bean.UserNode;
import test.weight.WeightUtil;

public class CosinSimilary implements SimilaryUtil{

	public CosinSimilary()
	{
		
	}
	@Override
	public SimilaryUtil getSimilaryFunc() {
		// TODO Auto-generated method stub
		return this;
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,WeightUtil weight) {
		// TODO Auto-generated method stub
		//获取物品
		ArrayList<ItemNode> item1=userNode1.getItems();
		ArrayList<ItemNode> item2=userNode2.getItems();
		float down=0f;
		float down2=0f;
		for(ItemNode item:item1)
		{
			down+=Math.pow(item.getValue(),2.0);
		}
		if(down<1E-10)
		{
			return 0f;
		}
		for(ItemNode item:item2)
		{
			down2+=Math.pow(item.getValue(),2.0);
		}
		down*=down2;
		if(down<1E-10)
		{
			return 0f;
		}
		float up=0f;
		//System.out.println(userNode1.getUserId());
		
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
				up+=item.getValue()*it.getValue();
			}
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
				up+=item.getValue()*it.getValue()/Math.log(1+it2);
			}
		}
		down=(float) Math.sqrt(down);
		return up/down;
	}
	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			HashMap<Long, long[]> map,WeightUtil weight,float[] weightPower) {
		// TODO Auto-generated method stub
		//获取对应的纬度数据
		long[] userNode1Map=map.get(userNode1.getUserId());
		long[] userNode2Map=map.get(userNode2.getUserId());
		float up=0f;
		float down1=0f;
		float down2=0f;
		if(weight==null)
		{
			if(weightPower==null)
			{
			for(int i=0;i<userNode1Map.length;i++)
			{
				up+=userNode1Map[i]*userNode2Map[i];
				down1+=Math.pow(userNode1Map[i],2D);
				down2+=Math.pow(userNode2Map[i],2D);
			}
			return (float)(up/Math.sqrt(down1*down2));
			}else{
				for(int i=0;i<userNode1Map.length;i++)
				{
					up+=userNode1Map[i]*userNode2Map[i]*weightPower[i];
					down1+=Math.pow(userNode1Map[i],2D)*weightPower[i];
					down2+=Math.pow(userNode2Map[i],2D)*weightPower[i];
				}
				return (float)(up/Math.sqrt(down1*down2));
			}
		}else{
			if(weightPower==null)
			{
			for(int i=0;i<userNode1Map.length;i++)
			{
				up+=userNode1Map[i]*userNode2Map[i];
				down1+=Math.pow(userNode1Map[i],2D);
				down2+=Math.pow(userNode2Map[i],2D);
			}
		//	float it2=weight.getCount(item);
			return (float) (up/Math.sqrt(down1*down2));///Math.log(1+it2);
			}else{
				for(int i=0;i<userNode1Map.length;i++)
				{
					up+=userNode1Map[i]*userNode2Map[i]*weightPower[i];
					down1+=Math.pow(userNode1Map[i],2D)*weightPower[i];
					down2+=Math.pow(userNode2Map[i],2D)*weightPower[i];
				}
			//	float it2=weight.getCount(item);
				return (float) (up/Math.sqrt(down1*down2));///Math.log(1+it2);
			}
		}
		
	}

}