package com.binaryme.ScratchTab.Gui.Datainterfaces;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;


/** This interface should be implemented by everything, what can contain data which can be interpreted as a number. Internally all numbers are converted to double. Only the user input constraints can value from integer to double. */
public interface InterfaceNumDataContainer extends InterfaceStringDataContainer {

	/** Returns the number, embedded into the current container as double.
	 *  Internally, all numbers should be converted to double, so that data fields can be added to all data fields. 
	 *  
	 *  IMPORTANT*/
	public double getNum(ExecutionHandler handler);
}
