package com.binaryme.ScratchTab.Gui.Datainterfaces;

/** This interface should be implemented by everything, what can contain data which can be interpreted as a boolean. */
public interface InterfaceBooleanDataContainer extends InterfaceStringDataContainer {

	/** Returns the number, embedded into the current container as double.
	 *  Internally, all numbers should be converted to double, so that data fields can be added to all data fields. */
	public Boolean getBoolean();
}
