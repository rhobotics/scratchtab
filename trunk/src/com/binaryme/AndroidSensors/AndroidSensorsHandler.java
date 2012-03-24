package com.binaryme.AndroidSensors;


/** 
 *  Class manages android's sensors. */
public class AndroidSensorsHandler{
	
	//INTERACTION
	public static MySensor[] getAllAvailableSensors(){
		String[] all = MySensor.getAllPossibleSensors();
		MySensor[] sensors = new MySensor[all.length];
		for(int i=0; i<all.length; i++){
			sensors[i] = new MySensor(all[i]);
		}
		return sensors;
	}
		
	
}
