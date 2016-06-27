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
 * 以比率过滤
 * @author Administrator
 *
 */
public class RecommanderHostItemRateFilter implements RecommanderFilterUtil {

	public WeightUtil weightUitl=null;
	public int recommanderFilterParam=1;
	public float rate=0;
	/**
	 * 
	 * @param weightUitl 物品统计表
	 * @param 推荐类型 ParamStatic中> < >= <=
	 * @param count 为 数量
	 * @param count
	 */
	public RecommanderHostItemRateFilter(CF cf,int recommanderFilterParam,float rate)
	{
		this.weightUitl=cf.getWeight();
		this.recommanderFilterParam=recommanderFilterParam;
		this.rate=rate;
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
		    	 if((int)item.getCount()/weightUitl.size()>this.rate)
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
		    	 if((int)item.getCount()/weightUitl.size()<this.rate)
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
		    	 if((int)item.getCount()/weightUitl.size()>=this.rate)
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
		    	 if((int)item.getCount()/weightUitl.size()<=this.rate)
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
				if((int)itemNode.getCount()/weightUitl.size()>this.rate)
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
				if((int)itemNode.getCount()/weightUitl.size()<this.rate)
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
		    	// System.out.println(itemNode.getValue()+"\t"+itemNode.getCount()+"\t"+weightUitl.size()+"\t"+this.rate);
				if((int)itemNode.getCount()/weightUitl.size()>=this.rate)
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
				if((int)itemNode.getCount()/weightUitl.size()<=this.rate)
				{
					iterator.remove();
					filterItem.add(itemNode);
				}
			}
		}
		
	}

}
