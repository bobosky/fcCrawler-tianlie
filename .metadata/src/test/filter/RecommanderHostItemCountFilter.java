package test.filter;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import test.CF;
import test.CFParamStatic;
import test.bean.ItemNode;
import test.bean.UserInfo;
import test.bean.UserNode;
import test.weight.WeightUtil;

/**
 * 将热门商品过滤
 * 以数量过滤
 * @author Administrator
 *
 */
public class RecommanderHostItemCountFilter implements RecommanderFilterUtil {

	public WeightUtil weightUitl=null;
	public int recommanderFilterParam=1;
	public int count=0;
	/**
	 * 
	 * @param weightUitl 物品统计表
	 * @param 推荐类型 ParamStatic中> < >= <=
	 * @param count 为 数量
	 * @param count
	 */
	public RecommanderHostItemCountFilter(CF cf,int recommanderFilterParam,int count)
	{
		this.weightUitl=cf.getWeight();
		this.recommanderFilterParam=recommanderFilterParam;
		this.count=count;
	}


	@Override
	public void filter(UserNode userNode) {
		// TODO Auto-generated method stub
		//如果>
		if(recommanderFilterParam==CFParamStatic.recommanderHotGtFilter)
		{
			Iterator<Entry<Long,ItemNode>> iterator = userNode.entrySet().iterator();  
		     while(iterator.hasNext()) {  
		    	 Entry<Long,ItemNode> itemNode = iterator.next();
		    	 ItemNode item=itemNode.getValue();
				if((int)item.getCount()>this.count)
				{
					iterator.remove();
					userNode.addFilterItem(item);
				}
			}
		}else if(recommanderFilterParam==CFParamStatic.recommanderHotltFilter)
		{//如果<
			Iterator<Entry<Long,ItemNode>> iterator = userNode.entrySet().iterator();  
		     while(iterator.hasNext()) {  
		    	 Entry<Long,ItemNode> itemNode = iterator.next();
		    	 ItemNode item=itemNode.getValue();
				if((int)item.getCount()<this.count)
				{
					iterator.remove();
					userNode.addFilterItem(item);
				}
			}
		}
		else if(recommanderFilterParam==CFParamStatic.recommanderHotGteFilter)
		{//如果>=
			Iterator<Entry<Long,ItemNode>> iterator = userNode.entrySet().iterator();  
		     while(iterator.hasNext()) {  
		    	 Entry<Long,ItemNode> itemNode = iterator.next();
		    	 ItemNode item=itemNode.getValue();
				if((int)item.getCount()>=this.count)
				{
					iterator.remove();
					userNode.addFilterItem(item);
				}
			}
		}
		else if(recommanderFilterParam==CFParamStatic.recommanderHotlteFilter)
		{//如果<=
			Iterator<Entry<Long,ItemNode>> iterator = userNode.entrySet().iterator();  
		     while(iterator.hasNext()) {  
		    	 Entry<Long,ItemNode> itemNode = iterator.next();
		    	 ItemNode item=itemNode.getValue();
				if((int)item.getCount()<=this.count)
				{
					iterator.remove();
					userNode.addFilterItem(item);
				}
			}
		}
	}
	
	@Override
	public void filter(LinkedList<ItemNode> userItem,LinkedList<ItemNode> filterItem) {
		//如果>
		if(recommanderFilterParam==CFParamStatic.recommanderHotGtFilter)
		{
			Iterator<ItemNode> iterator = userItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode itemNode = iterator.next();
				if((int)itemNode.getCount()>this.count)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}else if(recommanderFilterParam==CFParamStatic.recommanderHotltFilter)
		{//如果<
			Iterator<ItemNode> iterator = userItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode itemNode = iterator.next();
				if((int)itemNode.getCount()<this.count)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}
		else if(recommanderFilterParam==CFParamStatic.recommanderHotGteFilter)
		{//如果>=
			Iterator<ItemNode> iterator = userItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode itemNode = iterator.next();
				if((int)itemNode.getCount()>=this.count)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}
		else if(recommanderFilterParam==CFParamStatic.recommanderHotlteFilter)
		{//如果<=
			Iterator<ItemNode> iterator = userItem.iterator();  
		     while(iterator.hasNext()) {  
		    	 ItemNode itemNode = iterator.next();
				if((int)itemNode.getCount()<=this.count)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}
		
	}


}
