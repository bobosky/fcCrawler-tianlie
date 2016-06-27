package test.similary;

import java.util.HashMap;

import test.bean.UserNode;
import test.weight.WeightUtil;

public class MathchHard2 implements SimilaryUtil{

	@Override
	public SimilaryUtil getSimilaryFunc() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			WeightUtil weigth) {
		// TODO Auto-generated method stub
		return 0;
	}
	/**
	 * 返回的为math的概率比
	 */
	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			HashMap<Long, long[]> map, WeightUtil weight, float[] weightPower) {
		// TODO Auto-generated method stub
		//获取对应的纬度数据
				long[] userNode1Map=map.get(userNode1.getUserId());
				long[] userNode2Map=map.get(userNode2.getUserId());
				int count=0;
				for(int i=0;i<userNode1Map.length;i++)
				{
					if(userNode1Map[i]==userNode2Map[i])
					count++;
				}
				//System.out.println("count:"+count);
				int size=userNode1Map.length;
		return count/(size-count)*count/(size);
	}

}
