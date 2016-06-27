package test.filter;

import java.util.LinkedList;

import test.bean.ItemNode;
import test.bean.UserNode;
/**
 * 用于过滤最终推荐结果
 * @author Administrator
 *
 */
public interface RecommanderFilterUtil {

	/**
	 * 用于数据开始时 的数据清洗
	 * @param userNode
	 */
	public void filter(UserNode userNode);
	/**
	 * 用于过滤 推荐结果
	 * @param userItem
	 * @param filterItem
	 */
	public void filter(LinkedList<ItemNode> userItem,LinkedList<ItemNode> filterItem);
}
