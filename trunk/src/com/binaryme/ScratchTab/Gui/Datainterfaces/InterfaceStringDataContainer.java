package com.binaryme.ScratchTab.Gui.Datainterfaces;

import com.binaryme.ScratchTab.Exec.ExecutionHandler;

/** This interface should be implemented by everything, what can contain data which can be interpreted as a string. */
public interface InterfaceStringDataContainer {

	/** return the container data as String. */
	public String parseString(ExecutionHandler handler);
}
