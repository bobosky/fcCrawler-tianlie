package test.otherBean;

import java.io.Serializable;
/**
 * 热门品牌
 * @author Administrator
 *
 */
public class HotBrandBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6897681429871413862L;
	public int shopType=0;
	public String shopGroupName="";
	public float averageHits=0f;
	public long shopGroupId=0L;
	public int getShopType() {
		return shopType;
	}
	public void setShopType(int shopType) {
		this.shopType = shopType;
	}
	public String getShopGroupName() {
		return shopGroupName;
	}
	public void setShopGroupName(String shopGroupName) {
		this.shopGroupName = shopGroupName;
	}
	public float getAverageHits() {
		return averageHits;
	}
	public void setAverageHits(float averageHits) {
		this.averageHits = averageHits;
	}
	public long getShopGroupId() {
		return shopGroupId;
	}
	public void setShopGroupId(long shopGroupId) {
		this.shopGroupId = shopGroupId;
	}
	
}
