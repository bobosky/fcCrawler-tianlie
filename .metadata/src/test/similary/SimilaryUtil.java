package test.similary;

import java.util.HashMap;

import test.bean.UserNode;
import test.weight.WeightUtil;

public interface SimilaryUtil {

/**
 * 获取函数	
 * @return
 */
	public SimilaryUtil getSimilaryFunc();
	/**
	 * 获取相似性
	 * @param userNode1
	 * @param userNode2
	 * @param 对应的权重类型
	 * @return
	 */
	public float getSimilary(UserNode userNode1,UserNode userNode2,WeightUtil weigth);
	/**
	 * 用于计算基于内容的相似度
	 * @param userNode1
	 * @param userNOde2
	 * @param map
	 * @return
	 */
	public float getSimilary(UserNode userNode1,UserNode userNode2,HashMap<Long,long[]> map,WeightUtil weight,float[] weightPower);
}
