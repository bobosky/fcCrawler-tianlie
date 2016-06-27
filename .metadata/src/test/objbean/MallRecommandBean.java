package test.objbean;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

/**
 * 给 mall 推荐 brand
 * @author Administrator
 *
 */
public class MallRecommandBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * mallName
	 */
	private String mallName="";
	
	private long mallId=0L;
	/**
	 * 推荐的业态对应的物品
	 */
	private LinkedList<CategoryItemBean> categoryItem=new LinkedList<CategoryItemBean>();
	

	/**
	 * 品牌相似度信息
	 */
	private LinkedList<SonSimBean> similaryMall=new LinkedList<SonSimBean>();
	
	


	public long getMallId() {
		return mallId;
	}

	public void setMallId(long mallId) {
		this.mallId = mallId;
	}

	public String getMallName() {
		return mallName;
	}

	public void setMallName(String mallName) {
		this.mallName = mallName;
	}

	
	public LinkedList<CategoryItemBean> getCategoryItem() {
		return categoryItem;
	}

	public void setCategoryItem(LinkedList<CategoryItemBean> categoryItem) {
		this.categoryItem = categoryItem;
	}
	
	public void setCategoryItem(int index,CategoryItemBean categoryItem)
	{
		this.categoryItem.set(index, categoryItem);
	}
	
	public void addCategoryItem(CategoryItemBean categoryItem) {
		this.categoryItem.add(categoryItem);
	}

	public LinkedList<SonSimBean> getSimilaryMall() {
		return similaryMall;
	}

	public void setSimilaryMall(LinkedList<SonSimBean> similaryMall) {
		this.similaryMall = similaryMall;
	}

	public void addSimilaryMall(SonSimBean similary) {
		boolean flag=false;
		for(SonSimBean son:this.similaryMall)
		{
			if(son.getName().equals("similary"))
			{
				flag=true;
				son.setValue(son.getValue()+similary.getValue());
			}
		}
		if(!flag)
		{
			this.similaryMall.add(similary);
		}
	}
	/**
	 * 重新排序
	 */
	public void sortSim()
	{
		//重新排序
		Collections.sort(similaryMall);
		//组合
	}
	
	public void sortItem()
	{
		Collections.sort(this.categoryItem);
	}
}
