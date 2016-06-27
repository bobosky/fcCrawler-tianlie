package test.similary;

import java.util.ArrayList;
import java.util.HashMap;

import test.bean.ItemNode;
import test.bean.UserNode;
import test.weight.WeightUtil;

/**
 * nzi = sum(X .* Y) - (sum(X) * sum(Y)) / length(X);  
 * fenmu = sqrt((sum(X .^2) - sum(X)^2 / length(X)) * (sum(Y .^2) - sum(Y)^2 / length(X)));  
 * coeff = fenzi / fenmu;  
 * pearson 相关系数
 * @author Administrator
 *
 */
public class PearsonSimilary implements SimilaryUtil{

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
		float xy_sum=0f;
		float x_sum=0f;
		float x_2_sum=0f;
		float y_sum=0f;
		float y_2_sum=0f;
		int same_len=0;
		if(weight==null)
		{
			for(ItemNode item:item1)
			{
				x_sum+=item.getValue();
				x_2_sum+=Math.pow(item.getValue(),2f);
				//System.out.print(item.getItemId()+"\t");
				ItemNode it=userNode2.getItem(item.getItemId());
				if(it==null)
				{
					continue;
				}
				same_len++;
				xy_sum+=item.getValue()*it.getValue();
			}
		}else{
			for(ItemNode item:item1)
			{
				x_sum+=item.getValue();
				x_2_sum+=Math.pow(item.getValue(),2f);
				//System.out.print(item.getItemId()+"\t");
				ItemNode it=userNode2.getItem(item.getItemId());
				if(it==null)
				{
					continue;
				}
				same_len++;
				xy_sum+=item.getValue()*it.getValue();
			}
		}
		for(ItemNode item:item2)
		{
			y_sum+=item.getValue();
			y_2_sum+=Math.pow(item.getValue(),2f);
		}
		return compute(xy_sum,x_sum,y_sum,item1.size(),item2.size(),same_len,x_2_sum,y_2_sum);
		
	}
	
	public float compute(float xy_sum,float x_sum,float y_sum,int size1,int size2,int same_len,float x_2_sum,float y_2_sum)
	{
		float nzi=xy_sum-(x_sum+y_sum)/(size1+size2-same_len);
		//System.out.println(x_2_sum+"\t"+Math.pow(x_sum,2f)/item1.size());
		//System.out.println(y_2_sum+"\t"+Math.pow(y_sum,2f)/item2.size());
		float fenmu=(float) Math.sqrt((x_2_sum-Math.pow(x_sum,2f)/size1)*(y_2_sum-Math.pow(y_sum,2f)/size2));
		return nzi/fenmu;
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			HashMap<Long, long[]> map,WeightUtil weight,float[] weightPower) {
		// TODO Auto-generated method stub
		return 0;
	}

	
	
}
