package test.weight;

import test.bean.ItemNode;
import test.bean.UserInfo;
import test.bean.UserNode;

/**
 * 权重接口
 * @author Administrator
 *
 */
public interface WeightUtil {

	public UserNode userNode=null;
	/**
	 * 获取全部用户统计后该物品的数量
	 * @param item
	 * @return
	 */
	public float getCount(ItemNode item);
	/**
	 * 权重初始化
	 * @param userInfo
	 */
	public void init(UserInfo userInfo);
	/**
	 * 物品总数
	 * @return
	 */
	public int size();
}
