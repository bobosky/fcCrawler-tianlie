package test.similary;

import java.util.HashMap;

import test.bean.UserNode;
import test.weight.WeightUtil;

public class MathchHard implements SimilaryUtil{

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
	 * 返回的为match的概率比
	 */
	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			HashMap<Long, long[]> map, WeightUtil weight, float[] weightPower) {
		// TODO Auto-generated method stub
		//获取对应的纬度数据
				long[] userNode1Map=map.get(userNode1.getUserId());
				long[] userNode2Map=map.get(userNode2.getUserId());
				float count=0f;
				for(int i=0;i<userNode1Map.length;i++)
				{
//					if(userNode1Map[i]==userNode2Map[i])
//					count++;
					count+=1.0/(Math.abs(userNode1Map[i]-userNode2Map[i])+1);
				}
//				if(count*1f/userNode1Map.length>(1-1E-10))
//				{
//					System.out.print(userNode1.getUserId()+":");
//					for(long l:userNode1Map)
//					{
//						System.out.print(l+"\t");
//					}
//					System.out.println();
//					System.out.print(userNode2.getUserId()+":");
//					for(long l:userNode2Map)
//					{
//						System.out.print(l+"\t");
//					}
//					System.out.println();
//				}
				//System.out.println("val:"+count*1f/userNode1Map.length);
		return count*1f/userNode1Map.length;
	}

}
