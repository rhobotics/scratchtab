package com.binaryme.ScratchTab.Gui;

import com.binaryme.ScratchTab.Gui.Blocks.Block;
import com.binaryme.ScratchTab.Gui.Shapes.Shape;

/**
 * Interface, responsible for providing the Methods to add and remove Blocks. 
 * This Interface will be implemented by GUI Elements, which are possible to hold Blocks.
 * e.g. Blocks can hold other Blocks, workspace can hold Blocks.
 * 
 * @author Alexander Friesen
 *
 */

//TODO it this interface necessary?
public interface InterfaceBlockContainer {

	/** 
	 * Method, responsible for handling the dropped block on coordinates x,y
	 * Workspace will simply display add the block on that coordinates.
	 * Slot will add the Block always at coordinates 0,0 so Slot doesn't use x,y 
	 */
	void add(Block<? extends Shape> b, int x, int y);
	
	/**
	 * Method, responsible for removing the Block from it's old Parent. 
	 */
	void remove(Block<? extends Shape> b);
	
}
