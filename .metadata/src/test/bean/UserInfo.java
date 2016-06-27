package test.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.Set;

import test.filter.RecommanderFilterUtil;

public class UserInfo {

	/**
	 * 存储用户数据
	 * UserInfo.getKey
	 */
	private HashMap<Long,UserNode> userInfo=new HashMap<Long,UserNode>();
	/**
	 * 分组信息
	 */
	private long catgory=0L;
	
	/**
	 * 用户相似度
	 */
	private HashMap<Long,Float> userSim=new HashMap<Long,Float>();
	private StringBuffer strBuffer=new StringBuffer();
	public UserInfo()
	{
		
	}
	/**
	 * 两个用户之间的相似度 存储
	 * @param user1
	 * @param user2
	 * @param sim
	 */
	public void addUserSim(long user1,long user2,Float sim)
	{
		userSim.put(getU_U(user1,user2),sim);
	}
	public static long getU_U(long user1,long user2)
	{
		if(user1<user2)
		{
			return user1*100000000+user2;
		}else{
			return user2*100000000+user1;
		}
	}
	public static long getU_U1(long value)
	{
		return (value/100000000);
	}
	public static long getU_U2(long value)
	{
		return value%100000000;
	}
	
	/**
	 * 获取两个用户的相似度
	 * @param user1
	 * @param user2
	 * @return
	 */
	public float getUserSim(long user1,long user2)
	{
		strBuffer=new StringBuffer();
		return userSim.get(getU_U(user1,user2));
	}
	
	/**
	 * 数据清洗
	 * @param recommanderFilterUtil
	 */
	public void dataClearnFilter(LinkedList<RecommanderFilterUtil> recommanderFilterUtil)
	{
		//添加过滤器方法 
		if(recommanderFilterUtil==null)
		{
			return;
		}
		for(Entry<Long,UserNode> users:entrySet())
		{
			UserNode userNode=users.getValue();
			for(RecommanderFilterUtil recommanderFilter:recommanderFilterUtil)
			{
				recommanderFilter.filter(userNode);
			}
		}
	}
	
	
	public HashMap<Long, UserNode> getUserInfo() {
		return userInfo;
	}



	public void setUserInfo(HashMap<Long, UserNode> userInfo) {
		this.userInfo = userInfo;
	}



	public long getCatgory() {
		return catgory;
	}



	public void setCatgory(long catgory) {
		this.catgory = catgory;
	}



	public int size()
	{
		return userInfo.size();
	}
	/**
	 * 改组中是否存在该用户
	 * @param userNode
	 * @return
	 */
	public boolean isThisGroup(long category)
	{
		return this.catgory==category;
	}
	/**
	 * 添加用户
	 * @param user
	 */
	public void addUser(UserNode user)
	{
		if(userInfo.containsKey(user.getUserId()))
		{
			UserNode userNode= userInfo.get(user.getUserId());
			userNode.addItem(user.getItems().get(0));
		}else{
			userInfo.put(user.getUserId(), user);
		}
	}
	
	
	/**
	 * 获取全部用户
	 * @return
	 */
	public ArrayList<UserNode> getUsers()
	{
		ArrayList<UserNode> result=new ArrayList<UserNode>();
		for(Entry<Long, UserNode> item:userInfo.entrySet())
		{
			result.add(item.getValue());
		}
		return result;
	}
	
	public Set<Entry<Long,UserNode>> entrySet()
	{
		return userInfo.entrySet();
	}
	
	public Set<Entry<Long,Float>> entrySetSimilary()
	{
		return this.userSim.entrySet();
	}
}
