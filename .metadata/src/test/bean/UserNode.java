package test.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.lucene.analysis.util.CharArrayMap.EntrySet;

import test.filter.RecommanderFilterUtil;

public class UserNode implements Comparable<UserNode>{

	/**
	 * 用户id
	 */
	private long userId=0L;
	
	/**
	 * 物品id
	 */
	private HashMap<Long,ItemNode> items=new HashMap<Long,ItemNode>();
	
/**
 * 推荐的物品 有排序的
 */
	private LinkedList<ItemNode> sortItem=new LinkedList<ItemNode>();
	/**
	 * 被过滤的部分
	 */
	private LinkedList<ItemNode> filterItem=new LinkedList<ItemNode>();
	/**
	 * 初始化
	 */
	public UserNode()
	{
		
	}
	
	/**
	 * 对最终的sortItem排序
	 * 是否为降序
	 * 过滤器方法
	 */
	public void sortItem(boolean isDown,LinkedList<RecommanderFilterUtil> recommanderFilterUtil)
	{
		Collections.sort(sortItem);
		if(isDown)
		{	
		}else{
			LinkedList<ItemNode> sortItem2=new LinkedList<ItemNode>();
			for(ItemNode sortTemp:sortItem)
			{
				sortItem2.addFirst(sortTemp);
			}
			sortItem=sortItem2;
		}
		//添加过滤器方法 
		if(recommanderFilterUtil==null||recommanderFilterUtil.size()==0)
		{}else{
			for(RecommanderFilterUtil recommanderFilter:recommanderFilterUtil)
			{
				recommanderFilter.filter(sortItem, filterItem);
			}
			}
	}
	/**
	 * 添加到排序中
	 * @param itemNode
	 */
	public void addSortItem(ItemNode itemNode)
	{
		int i=0;
		boolean flag=true;
		for(ItemNode itemno:sortItem)
		{
			if(itemno.equals(itemNode))
			{
				itemno.setValue(itemno.getValue()+itemNode.getValue());
				flag=false;
			}
			i++;
		}
		if(i==0 || flag)
		{
			sortItem.add(itemNode);
		}
	}
	
	public LinkedList<ItemNode> getSortItem()
	{
		return sortItem;
	}
	
	
	
	public LinkedList<ItemNode> getFilterItem() {
		return filterItem;
	}

	public void setFilterItem(LinkedList<ItemNode> filterItem) {
		this.filterItem = filterItem;
	}
	public void addFilterItem(ItemNode filterItem)
	{
		this.filterItem.add(filterItem);
	}

	public long getUserId() {
		return userId;
	}



	public void setUserId(long userId) {
		this.userId = userId;
	}


	public void setItems(HashMap<Long, ItemNode> items) {
		this.items = items;
	}



	/**
	 * 设置用户
	 * @param userId
	 */
	public void setUser(long userId)
	{
		this.userId=userId;
	}
	/**
	 * 设置物品
	 */
	public void addItem(ItemNode item)
	{
		this.items.put(item.getItemId(),item);
	}
	/**
	 * 添加物品 如果物品不存在则直接添加
	 * 否则 item值累加
	 * @param item
	 */
	public void addItemAccumulation(ItemNode item)
	{
		ItemNode itemnode=this.items.get(item.getItemId());
		if(itemnode==null)
		{
			addItem(item);
		}else{
			itemnode.setCount(itemnode.getCount()+item.getCount());
		}
	}
	/**
	 * 移出一个物品
	 * @param item
	 */
	public void remove(long itemId)
	{
		items.remove(itemId);	
	}
	/**
	 * 物品数量
	 * @return
	 */
	public int size()
	{
		return this.items.size();
	}
	/**
	 * 获取全部物品
	 * @return
	 */
	public ArrayList<ItemNode> getItems()
	{
		ArrayList<ItemNode> result=new ArrayList<ItemNode>();
		for(Entry<Long, ItemNode> item:items.entrySet())
		{
			result.add(item.getValue());
		}
		return result;
	}
	/**
	 * 获取全部物品
	 * @return
	 */
	public LinkedList<ItemNode> getItemsLinked()
	{
		LinkedList<ItemNode> result=new LinkedList<ItemNode>();
		for(Entry<Long, ItemNode> item:items.entrySet())
		{
			result.add(item.getValue());
		}
		return result;
	}
	
	public Set<Entry<Long,ItemNode>> entrySet()
	{
		return items.entrySet();
	}
	
	public void removeItem(ItemNode itemNode)
	{
		items.remove(itemNode.getItemId());
	}
	
	/**
	 * 通过商品编号获取商品信息
	 * @param itemId
	 * @return
	 */
	public ItemNode getItem(long itemId)
	{
		return items.get(itemId);
	}
	
	public int compareTo(UserNode other)
	{
		return Long.compare(userId, other.userId);
	}
	
	public boolean equals(Object in)
	{
		UserNode st=(UserNode)in;
		return this.userId==st.userId;
	}
	public int hashCode()
	{
		return (int)userId & 0x7FFFFFFF;
	}
	
}
