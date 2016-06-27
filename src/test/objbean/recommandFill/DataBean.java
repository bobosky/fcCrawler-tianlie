package test.objbean.recommandFill;

import java.io.Serializable;
import java.util.ArrayList;

public class DataBean implements Serializable{

	
	private ArrayList<FloorBean> Floors=null;
	
	private MallSonBean building=null;

	public ArrayList<FloorBean> getFloors() {
		return Floors;
	}

	public void setFloors(ArrayList<FloorBean> floors) {
		Floors = floors;
	}

	public MallSonBean getBuilding() {
		return building;
	}

	public void setBuilding(MallSonBean building) {
		this.building = building;
	}
	
	
}
