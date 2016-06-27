package test.objbean.recommandFill;

public class SimiBrandTemp2 implements Comparable<SimiBrandTemp2>{

	/**
	 * 相似度值
	 */
	private float simi=0f;
	/**
	 * 品牌位置
	 */
	private int index=-1;
	@Override
	public int compareTo(SimiBrandTemp2 o) {
		// TODO Auto-generated method stub
		return Integer.compare(index,o.index);
	}
	public SimiBrandTemp2(SimiBrandTemp temp)
	{
		this.simi=temp.getSimi();
		this.index=temp.getIndex();
	}
	public float getSimi() {
		return simi;
	}
	public void setSimi(float simi) {
		this.simi = simi;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	
	
	
}
