package util;

import java.util.HashMap;
import java.util.Map;

public class InterferenceModel {
	private Map<Integer,Double> model;
	
	public InterferenceModel(){
		this.model = new HashMap<Integer,Double>();
		model.put(0, 1.0);
		model.put(1, 0.7272);
		model.put(2, 0.2714);	
		model.put(3, 0.0375);
		model.put(4, 0.0054);
		model.put(5, 0.0008);
		model.put(6, 0.0002);
		model.put(7, 0.0);
		model.put(8, 0.0);
		model.put(9, 0.0);
		model.put(10, 0.0);		
	}
	
	public Double get(Integer key){
		return model.get(key);
	}	
}
