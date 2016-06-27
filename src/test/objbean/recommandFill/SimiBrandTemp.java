package test.objbean.recommandFill;

public class SimiBrandTemp implements Comparable<SimiBrandTemp>{

	/**
	 * 相似度值
	 */
	private float simi=0f;
	/**
	 * 品牌位置
	 */
	private int index=-1;
	@Override
	public int compareTo(SimiBrandTemp o) {
		// TODO Auto-generated method stub
		return Float.compare(o.simi,simi);
	}
	public SimiBrandTemp(float simi,int index)
	{
		this.simi=simi;
		this.index=index;
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
