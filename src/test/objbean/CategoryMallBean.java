package test.objbean;

import java.io.Serializable;
import java.util.LinkedList;

public class CategoryMallBean implements Serializable,Comparable<CategoryMallBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 业态id
	 */
	private long  category=0L;
	/**
	 * 分类名
	 */
	private String categoryName="";
	
	/**
	 * 子类
	 */
	private LinkedList<SonBean> recommandMall=new LinkedList<SonBean>();
//	/**
//	 * 品牌相似度信息
//	 */
//	private LinkedList<SonSimBean> similaryBrand=new LinkedList<SonSimBean>();
	
	public LinkedList<SonBean> getRecommandMall() {
		return recommandMall;
	}
	public long getCategory() {
		return category;
	}
	public void setCategory(long category) {
		this.category = category;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public void setRecommandMall(LinkedList<SonBean> recommandMall) {
		this.recommandMall = recommandMall;
	}
	
	public void addRecommandMall(SonBean recommandMall) {
		this.recommandMall.add(recommandMall);
	}
	
	public void addRecommandMallDistinct(SonBean recommandMall) {
		
		boolean flag=false;
		for(SonBean son:this.recommandMall)
		{
			if(son.getName().equals(recommandMall.getName()))
			{
				flag=true;
			}
		}
		if(!flag)
		{
			this.recommandMall.add(recommandMall);
		}
	}
//	public LinkedList<SonSimBean> getSimilaryBrand() {
//		return similaryBrand;
//	}
//	public void setSimilaryBrand(LinkedList<SonSimBean> similaryBrand) {
//		this.similaryBrand = similaryBrand;
//	}
//	public void addSimilaryBrand(SonSimBean similaryBrand) {
//		this.similaryBrand.add(similaryBrand);
//	}
	@Override
	public int compareTo(CategoryMallBean o) {
		// TODO Auto-generated method stub
		return Long.compare(category,o.category);
	}
	
}
