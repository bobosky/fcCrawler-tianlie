package test.objbean;

/**
 * 解释信息的权重信息
 * @author Administrator
 *
 */
public class ExplainBean implements Comparable<ExplainBean>{

	
	private float value=0f;
	
	private String text="";
	
	private int type=0;
	
	@Override
	public int compareTo(ExplainBean o) {
		// TODO Auto-generated method stub
		ExplainBean in=(ExplainBean)o;
		return Float.compare(in.value,this.value);
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	
}
