package test.otherBean;

import java.io.Serializable;

/**
 * 大众点评上 group组对应的id和名字
 * @author Administrator
 *
 */
public class GroupNameAndIdBean implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3714190504747906881L;
	public long _id=0L;
	public String value="";
	public long get_id() {
		return _id;
	}
	public void set_id(long _id) {
		this._id = _id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	
	
	
}
