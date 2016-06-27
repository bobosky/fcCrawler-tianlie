package test.filter;

import java.util.HashMap;
import java.util.LinkedList;

import test.bean.UserNode;
import test.weight.WeightUtil;

public interface ItemGetRurl {
	
	/**
	 * 
	 * @param user1
	 * @param user2
	 * @param sim 相似度
	 */
	public void getRecommendItems(UserNode user1,UserNode user2,float sim,HashMap<Long,long[]> map,WeightUtil weight);
}
