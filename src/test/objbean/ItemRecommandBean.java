package test.objbean;

import java.io.Serializable;
import java.util.Collections;
import java.util.LinkedList;

/**
 * 给 brand 推荐 mall
 * @author Administrator
 *
 */
public class ItemRecommandBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * mallName
	 */
	private String brandName="";
	
	private long brandId=0L;
	
	/**
	 * 推荐的业态对应的物品
	 */
	private LinkedList<CategoryMallBean> categoryMall=new LinkedList<CategoryMallBean>();
	
	/**
	 * 品牌相似度信息
	 */
	private LinkedList<SonSimBean> similaryBrand=new LinkedList<SonSimBean>();

	public String getBrandName() {
		return brandName;
	}


	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public long getBrandId() {
		return brandId;
	}

	public void setBrandId(long brandId) {
		this.brandId = brandId;
	}




	public LinkedList<SonSimBean> getSimilaryBrand() {
		return similaryBrand;
	}




	public void setSimilaryBrand(LinkedList<SonSimBean> similaryBrand) {
		this.similaryBrand = similaryBrand;
	}




	public LinkedList<CategoryMallBean> getCategoryMall() {
		return categoryMall;
	}

	public void setCategoryMall(LinkedList<CategoryMallBean> categoryMall) {
		this.categoryMall = categoryMall;
	}
	public void addCategoryMall(CategoryMallBean categoryMall) {
		this.categoryMall.add(categoryMall);
	}
	/**
	 * 添加并替换重复的
	 * @param categoryMall
	 */
	public void addCategoryMallDistinct(CategoryMallBean categoryMall)
	{
		boolean flag=false;
		for(int i=0;i<this.categoryMall.size();i++)
		{
			if(categoryMall.getCategory()==this.categoryMall.get(i).getCategory())
			{
				flag=true;
				this.categoryMall.set(i,categoryMall);
			}
		}
		if(!flag)
		{
			addCategoryMall(categoryMall);
		}
	}

	public void addSimilaryBrand(SonSimBean similary) {
		boolean flag=false;
		for(SonSimBean son:this.similaryBrand)
		{
			if(son.getName().equals(similary.getName()))
			{
				flag=true;
				son.setValue(son.getValue()+similary.getValue());
			}
		}
		if(!flag)
		{
			this.similaryBrand.add(similary);
		}
	}
	
	public void addSimilaryBrandDistinct(SonSimBean similary)
	{
		boolean flag=false;
		for(SonSimBean son:this.similaryBrand)
		{
			if(son.getName().equals(similary.getName()))
			{
				flag=true;
			}
		}
		if(!flag)
		{
			this.similaryBrand.add(similary);
		}
	}
	/**
	 * 重新排序
	 */
	public void sortSim(int limit)
	{
		//重新排序
		Collections.sort(similaryBrand);
		//组合
		while(similaryBrand.size()>limit)
		{
			similaryBrand.pollLast();
		}
	}
	
	public void sortItem()
	{
		Collections.sort(this.categoryMall);
	}
	/**
	 * 排序解释信息
	 */
	public void sortExplain()
	{
		for(SonSimBean bean:similaryBrand)
		{
			bean.sortExplain();
		}
		for(CategoryMallBean bean:categoryMall)
		{
			for(SonBean beanson:bean.getRecommandMall())
			{
				beanson.sortExplain();
			}
		}
	}
}
