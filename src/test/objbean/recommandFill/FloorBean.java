package test.objbean.recommandFill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class FloorBean implements Serializable{

	
	private ArrayList<FuncAreasBean> FuncAreas=null;

	public ArrayList<FuncAreasBean> getFuncAreas() {
		return FuncAreas;
	}

	public void setFuncAreas(ArrayList<FuncAreasBean> funcAreas) {
		FuncAreas = funcAreas;
	}
	
	
	
}
