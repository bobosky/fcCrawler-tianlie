package test.objbean;

import java.io.Serializable;
import java.util.LinkedList;

import antlr.collections.List;

public class CategoryItemBean  implements Serializable,Comparable<CategoryItemBean>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * 业态
	 */
	private String  categoryName="";
	/**
	 * 业态id
	 */
	private int category=0;
	
	/**
	 * 子类
	 */
	private LinkedList<SonBean> recommandShop=new LinkedList<SonBean>();
	
//	/**
//	 * mall相似度信息
//	 */
//	private LinkedList<SonSimBean> similaryMall=new LinkedList<SonSimBean>();

	public int getSizeShop()
	{
		return this.recommandShop.size();
	}
	


	public String getCategoryName() {
		return categoryName;
	}



	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}



	public int getCategory() {
		return category;
	}



	public void setCategory(int category) {
		this.category = category;
	}



	public LinkedList<SonBean> getRecommandShop() {
		return recommandShop;
	}

	public void setRecommandShop(LinkedList<SonBean> recommandShop) {
		this.recommandShop = recommandShop;
	}

	public void addRecommandShop(SonBean recommandShop) {
		this.recommandShop.add(recommandShop);
	}
	
	public void addRecommandShopLimit(SonBean recommandShop,int count) {
		if(this.recommandShop.size()<=count)
		this.recommandShop.add(recommandShop);
	}
//	public LinkedList<SonSimBean> getSimilaryMall() {
//		return similaryMall;
//	}
//
//	public void setSimilaryMall(LinkedList<SonSimBean> similaryMall) {
//		this.similaryMall = similaryMall;
//	}
//	public void addSimilaryMall(SonSimBean similaryMall) {
//		this.similaryMall.add(similaryMall);
//	}

	@Override
	public int compareTo(CategoryItemBean o) {
		// TODO Auto-generated method stub
		return Integer.compare(category,o.category);
	}
	
	
	
}
