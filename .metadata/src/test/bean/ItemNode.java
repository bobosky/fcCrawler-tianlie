package test.bean;


public class ItemNode implements Comparable<ItemNode>{

	/**
	 * 物品id
	 */
	private long itemId=0L;
	/**
	 * 喜好度 及最终得分
	 */
	private float value=0.0f;
	/**
	 * 对应的数量
	 */
	private long count=1L;
	
	
	
	public ItemNode(long itemId,float value,long count)
	{
		this.itemId=itemId;
		this.value=value;
		this.count=count;
	}
	
	public int compareTo(ItemNode other)
	{
		return -Float.compare(value, other.value);
	}
	
	public boolean equals(Object in)
	{
		ItemNode st=(ItemNode)in;
		if(this.itemId==st.itemId)
		{
			return true;
		}else{
			return false;
		}
	}
	public int hashCode()
	{
		return (int)itemId & 0x7FFFFFFF;
	}

	public long getItemId() {
		return itemId;
	}

	public void setItemId(long itemId) {
		this.itemId = itemId;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
	
}
